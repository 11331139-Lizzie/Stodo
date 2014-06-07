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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class Plan_Done_Fragment extends Fragment {
	private View rootView;
	private ArrayList<Plan> old_plans;
	
	private ListView listview;
	
	private String filename = "plan_done.txt";
	private int limit_time;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.fragment_selection_done, container, false);
		
		old_plans = new ArrayList<Plan>();

		readPlans();
//		×¢²áÊÕÌý¹ã²¥
		IntentFilter filter1 = new IntentFilter();
		filter1.addAction("update_old_plans");
		getActivity().registerReceiver(receiver, filter1);
		
		
		findViews();
		setList();
		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
	public void findViews()
	{
		listview = (ListView) rootView.findViewById(R.id.done_plan_list);
	}
	
	public void readPlans()
	{
		String res = "";
		try {
			FileInputStream is = getActivity().openFileInput(filename);
			int len = is.available();
			byte []buffer = new byte[len];
		
			is.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			
			int i;
			String limit = "";
			for(i = 0;res.charAt(i) != '\n';i++)
				limit += res.charAt(i);
			limit_time = Integer.parseInt(limit);
			i++;
			
			String each = "";
			for(;i < res.length();i++)
			{
				if(res.charAt(i) != '\n')
					each += res.charAt(i);
				else
				{
					Plan plan = new Plan();
					ArrayList<String> task_names = new ArrayList<String>();
					ArrayList<Integer> need_time = new ArrayList<Integer>();
					String date = "";
					int k,l;
					//set the time
					for(k = 0;each.charAt(k) != ' ';k++)
						date += each.charAt(k);
					plan.setYear(Integer.parseInt(date.substring(0,4)));
					for(l = 5;date.charAt(l) != '.';l++);
					plan.setMonth(Integer.parseInt(date.substring(5,l)));
					plan.setDay(Integer.parseInt(date.substring(l+1,date.length())));
					
					//set the small_tasks' names and need_time
					int count = 1;
					k++;
					int j = k;
					for(;k < each.length();k++)
					{
						if(each.charAt(k) == ' ')
						{
							if(count % 2 != 0)
							{
								//it is a name
								String name = each.substring(j, k);
								task_names.add(name);
								j = k+1;
								count++;
							}
							else
							{
								//it is need_time
								String time = "";
								time = each.substring(j,k);
								need_time.add(Integer.parseInt(time));
								j = k+1;
								count++;
							}
						}
						else
							continue;
					}
					plan.setTaskNams(task_names);
					plan.setNeedTime(need_time);
					plan.setState(0);
					plan.setLimit(limit_time);
					old_plans.add(plan);
					
					each = "";
				}
				
			}
			is.close();		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setList()
	{
		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		
		for(int i = 0;i < old_plans.size();i++)
		{
			HashMap<String,String> map = new HashMap<String, String>();
			Plan plan = old_plans.get(i);
			ArrayList<String> tasks_names = plan.getNameList();
			map.put("date",plan.getYear() + "."+plan.getMonth()+"."+plan.getDay());
			map.put("task_num",tasks_names.size() + "");
//			map.put("date", old_plans.size()+"");
//			map.put("task_num", old_plans.size + "");
			list.add(map);
		}
		
		SimpleAdapter listAdapter = new SimpleAdapter(getActivity(),list,R.layout.done_plan_list,new String[]{"date","task_num"},new int[]{R.id.old_date,R.id.old_task_num}) ;
		listview.setAdapter(listAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Plan plan = old_plans.get(arg2);
				intent.putStringArrayListExtra("tasks_names", plan.getNameList());
				intent.putIntegerArrayListExtra("need_time", plan.getTimeList());
				intent.putExtra("year", plan.getYear());
				intent.putExtra("month", plan.getMonth());
				intent.putExtra("day", plan.getDay());
				intent.setClass(getActivity(), PlanInfoActivity.class);
				startActivity(intent);				
			}
		});		
	}
	
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals("update_old_plans"))
			{
				int num = intent.getIntExtra("num", 0);
				if(num != 0)
				{
					for(int i = 0;i < num;i++)
					{
						Bundle bundle = intent.getBundleExtra(i + "");
						int year = bundle.getInt("year");
						int month = bundle.getInt("month");
						int day = bundle.getInt("day");
						int limit_time = bundle.getInt("limit_time");
						int state = bundle.getInt("state");
						ArrayList<String> tasks_names = bundle.getStringArrayList("tasks_names");
						ArrayList<Integer> need_time = bundle.getIntegerArrayList("need_time");
						Plan plan = new Plan(year, month, day, limit_time, tasks_names, need_time, state);
						old_plans.add(plan);
					}
				}
				setList();
				if(old_plans.size() != 0)
					limit_time = old_plans.get(0).getLimit();
			}
		}
	};
	
	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		getActivity().unregisterReceiver(receiver);
		try {
			FileOutputStream os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
			String tmp = limit_time + "\n";
			os.write(tmp.getBytes());
			for(int i = 0;i < old_plans.size();i++)
			{
				Plan plan = old_plans.get(i);
				ArrayList<String> names = plan.getNameList();
				ArrayList<Integer> need_time = plan.getTimeList();
				tmp = plan.getYear() + "." + plan.getMonth() + "." + plan.getDay() + " ";
				for(int j = 0;j < names.size();j++)
				{
					tmp += (names.get(j) + " " + need_time.get(j) + " ");
				}			
				tmp += "\n";
				os.write(tmp.getBytes());
			}
			os.close();
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
