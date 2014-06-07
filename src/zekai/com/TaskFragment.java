package zekai.com;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.util.EncodingUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;


public class TaskFragment extends Fragment{
	private View rootView;
	private Button addTask;
	private Button setting;
	private ListView listview;
	private ArrayList<Task> task_list;
	private int limit_time;
	
	private String filename = "task_list.txt";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.fragment_selection_task, container, false);
		
		findViews();
	
		Bundle bundle = getArguments();
		task_list = bundle.getParcelableArrayList("task_list");
		
		limit_time = 0;
			
		
		
		//注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction("update_done_time");
		getActivity().registerReceiver(receiver, filter);
		
		readTasks();
		setList();
		
		if(limit_time == 0)
		{
			Intent intent = new Intent();
			intent.putExtra("limit_time", limit_time);
			intent.setClass(getActivity(), SettingActivity.class);
			startActivityForResult(intent, 1);
		}
		return rootView;
	}
	
	
	public void findViews()
	{
		addTask = (Button)rootView.findViewById(R.id.addTask);
		setting = (Button)rootView.findViewById(R.id.setting);
		listview = (ListView)rootView.findViewById(R.id.listview);
		
		addTask.setText("+");
		addTask.setOnClickListener(new AddTasksListener());
		setting.setOnClickListener(new SettingListener());
	}
	
	public void readTasks()
	{
		String res = "";
		try {
			FileInputStream is = getActivity().openFileInput(filename);
			int len = is.available();
			byte []buffer = new byte[len];
			is.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			
			String each = "";
			int i;
			for(i = 0;i < res.length();i++)
			{
				if(res.charAt(i) != '\n')
					each += res.charAt(i);
				else
					break;
			}
			//第一行是学习时间
			limit_time = Integer.parseInt(each);
			
			i++;
			each = "";
			for(;i < res.length();i++)
			{
				if(res.charAt(i) != '\n')
					each += res.charAt(i);
				else
				{
					System.out.println(each);
					String []strings = new String[7];
					strings = each.split(" ");
					
					String name = strings[0];
					
					int k;
					String date = strings[1];
					int year = Integer.parseInt(date.substring(0, 4));
					for(k = 5;date.charAt(k) != '.';k++);
					int month = Integer.parseInt(date.substring(5,k));
					int day = Integer.parseInt(date.substring(k+1, date.length()));

					int cost_time = Integer.parseInt(strings[2]);
					int plan_time = Integer.parseInt(strings[3]);
					int done_time = Integer.parseInt(strings[4]);
					int priority = Integer.parseInt(strings[5]);
					int state = Integer.parseInt(strings[6]);
					
					Task task = new Task(name,year,month,day,cost_time,plan_time,done_time,priority,state);
					task_list.add(task);
					each = "";
				}
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//发送广播
		Intent intent = new Intent();
		intent.setAction("update_messages");
		intent.putParcelableArrayListExtra("new_task_list", task_list);
		intent.putExtra("limit_time", limit_time);
		getActivity().sendBroadcast(intent);
	}
	
	class AddTasksListener implements android.view.View.OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.putParcelableArrayListExtra("task_list", task_list);
			intent.putExtra("current", -1);
			intent.setClass(getActivity(), TaskInfoActivity.class);
			startActivityForResult(intent, 0);
		}		
	}
	
	class SettingListener implements android.view.View.OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.putExtra("limit_time", limit_time);
			intent.setClass(getActivity(), SettingActivity.class);
			startActivityForResult(intent, 1);
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode == 2)
		{
			task_list = data.getParcelableArrayListExtra("task_list");
			setList();			
			//发送广播
			Intent intent = new Intent();
			intent.setAction("update_task_list");
			intent.putParcelableArrayListExtra("new_task_list", task_list);
			intent.putExtra("limit_time", limit_time);
			getActivity().sendBroadcast(intent);
		}
		else if(resultCode == 1)
		{
			limit_time = data.getIntExtra("limit_time", 0);
			//发送广播
			Intent intent = new Intent();
			intent.setAction("update_limit_time");
			intent.putExtra("new_limit_time", limit_time);
			getActivity().sendBroadcast(intent);
		}		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	//复写接受广播的方法
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals("update_done_time"))
			{
				int num = intent.getIntExtra("num", 0);
				if(num != 0)
				{
					for(int i = 0;i < num;i++)
					{
						Bundle bundle = intent.getBundleExtra(i + "");
						ArrayList<String> names = bundle.getStringArrayList("names");
						ArrayList<Integer> times = bundle.getIntegerArrayList("times");
						for(int j = 0;j < names.size();j++)
						{
							String name = names.get(j);
						    for(int k = 0;k < task_list.size();k++)
						    {
						    	Task task = task_list.get(k);
						    	if(task.getName().equals(name))
						    	{
						    		task.setDoneTime(task.getDoneTime() + times.get(j));
						    		break;
						    	}
						    }
						}
					}
				}			
			}
		}
	};
	
	public void setList()
	{
		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		
		
		for(int i = 0;i < task_list.size();i++)
		{
			HashMap<String,String> map = new HashMap<String, String>();
			map.put("index", i + 1 + "、");
			map.put("task_name",task_list.get(i).getName());
			map.put("done_time", "已完成:" + task_list.get(i).getDoneTime() + "h");
			list.add(map);
		}
		
		SimpleAdapter listAdapter = new SimpleAdapter(getActivity(),list,R.layout.list_view,new String[]{"index","task_name","done_time"},new int[]{R.id.index,R.id.list_name,R.id.done_time}) ;
		listview.setAdapter(listAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putParcelableArrayListExtra("task_list", task_list);
				intent.putExtra("current", arg2);
				intent.setClass(getActivity(), TaskInfoActivity.class);
				startActivityForResult(intent, 0);			
			}
		});		
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		getActivity().unregisterReceiver(receiver);
		try {
			FileOutputStream os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
			//第一行写每天学习时间
			String tmp = limit_time + "\n";
			os.write(tmp.getBytes());
			//每行一个task信息
			for(int i = 0;i < task_list.size();i++)
			{
				Task task = task_list.get(i);
				tmp = task.getName() + " ";
				tmp += (task.getYear() + "." + task.getMonth() + "." + task.getDay() + " ");
				tmp += (task.getCostTime() + " " + task.getPlanTime() + " " + task.getDoneTime() + " ");
				tmp += (task.getPriority() + " ");
				tmp += (task.getState() + " ");
				tmp += "\n";
				os.write(tmp.getBytes());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onDestroy();
	}
	
	
	
}
