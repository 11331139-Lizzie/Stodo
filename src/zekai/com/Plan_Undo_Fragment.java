package zekai.com;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Plan_Undo_Fragment extends Fragment {
	private View rootView;
	private ArrayList<Task> task_list;
	private ArrayList<Plan> plan_list;
	private int limit_time;
	
	private String filename = "plan_undo.txt";
	
	private ListView listview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_selection_undo, container, false);
		
		findViews();
		
		task_list = getArguments().getParcelableArrayList("task_list");
		plan_list = new ArrayList<Plan>();
		limit_time = 0;
		
		//注册收听广播
		IntentFilter filter1 = new IntentFilter();
		filter1.addAction("update_task_list");
		getActivity().registerReceiver(receiver, filter1);
		
		IntentFilter filter2 = new IntentFilter();
		filter2.addAction("update_limit_time");
		getActivity().registerReceiver(receiver, filter2);
		
		IntentFilter filter3 = new IntentFilter();
		filter3.addAction("update_messages");
		getActivity().registerReceiver(receiver, filter3);
		
		//读入计划数据
		readPlans();
		
		//将过期计划移除
		movePlans();
		
		//显示出来
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
		listview = (ListView)rootView.findViewById(R.id.plan_list_view);
	}
	
	//读取undo_plan的内容
	public void readPlans()
	{
		String res = "";
		try {
			FileInputStream is = getActivity().openFileInput(filename);
			int len = is.available();
			byte []buffer = new byte[len];
		
			is.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			String each = "";
			for(int i = 0;i < res.length();i++)
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
					plan_list.add(plan);
					
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
	
	public void movePlans()
	{
		if(plan_list.size() != 0)
		{
			ArrayList<Plan> old_plans = new ArrayList<Plan>();
			Date date = new Date(System.currentTimeMillis());
			int cur_year = date.getYear()+1900;
			int cur_month = date.getMonth()+1;
			int cur_day = date.getDate();
			for(int i = 0;i < plan_list.size();i++)
			{
				Plan plan = plan_list.get(i);
				//计划过期了
				if(compare_date(plan.getYear(), plan.getMonth(), plan.getDay(), cur_year, cur_month, cur_day) == 1)
				{
					plan.setState(1);
					old_plans.add(plan);
					plan_list.remove(i);
				}
			}
			//给已完成计划界面发送广播
			Intent intent = new Intent();
			intent.setAction("update_old_plans");
			intent.putExtra("num", old_plans.size());
			//我想让Plan类继承Parcelable,但由于成员变量的读和写较为难搞定，所以放弃
			//只能用这个流氓方法了，到那边再组合起来
			for(int i = 0;i < old_plans.size();i++)
			{
				Bundle bundle = new Bundle();
				Plan plan = old_plans.get(i);
				bundle.putInt("year", plan.getYear());
				bundle.putInt("month", plan.getMonth());
				bundle.putInt("day", plan.getDay());
				bundle.putStringArrayList("tasks_names", plan.getNameList());
				bundle.putIntegerArrayList("need_time", plan.getTimeList());
				bundle.putInt("limit_time", plan.getLimit());
				bundle.putInt("state", plan.getState());
				intent.putExtra(i + "", bundle);
			}			
			getActivity().sendBroadcast(intent);
			
			//给任务界面发送广播，更新已完成时间
			Intent intent2 = new Intent();
			intent2.putExtra("num", old_plans.size());
			for(int i = 0;i < old_plans.size();i++)
			{
				Plan plan = old_plans.get(i);
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("names", plan.getNameList());
				bundle.putIntegerArrayList("times", plan.getTimeList());
				intent2.putExtra(i + "", bundle);
			}		
			intent2.setAction("update_done_time");
			getActivity().sendBroadcast(intent2);
		}
	}
	
	//复写接受广播的方法
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals("update_task_list"))
			{
				task_list = intent.getParcelableArrayListExtra("new_task_list");
				limit_time = intent.getIntExtra("limit_time", 0);
				orderTasks();
				setPlan();
				setList();
			}
			else if(intent.getAction().equals("update_limit_time"))
			{
				limit_time = intent.getIntExtra("new_limit_time", 0);
				setPlan();
				setList();
			}
			else if(intent.getAction().equals("update_messages"))
			{
				//获取保存的数据
				task_list = intent.getParcelableArrayListExtra("new_task_list");
				limit_time = intent.getIntExtra("limit_time", 0);
				orderTasks();
			}
		}
	};
			
	
	public void orderTasks()
	{
		for(int i = 0;i < task_list.size();i++)
		{
			for(int j = task_list.size()-1;j > i;j--)
			{
				Task before = task_list.get(j-1);
				Task after = task_list.get(j);
				int value = compare_date(before.getYear(),before.getMonth(),before.getDay(),
						after.getYear(),after.getMonth(),after.getDay());
				//第一个的deadline早于第二个，不变
				if(1 == value)
				{
					continue;
				}
				//两者deadline相同，比较优先级
				else if(2 == value)
				{
					//第一个的优先级大于第二个，不变
					if(before.getPriority() > after.getPriority())
						continue;
					//两者优先级相同，比较花费的时间
					else if(before.getPriority() == after.getPriority())
					{
						//第一个花费的时间少于第二个，不变
						if(before.getCostTime() < after.getCostTime())
							continue;
						//两者时间相同，至此，两者一模一样，不变
						else if(before.getCostTime() == after.getCostTime())
							continue;
						//第一个花费的时间多于第二个，交换
						else 
							swap(j,j-1);
					}
					//第一个的优先级小于第二个，交换
					else
						swap(j,j-1);
				}
				//第一个的deadline晚于第二个，交换
				else
					swap(j,j-1);
			}
		}
	}
	
	public void swap(int a,int b)
	{
		Task tmp = task_list.get(a);
		task_list.set(a, task_list.get(b));
		task_list.set(b,tmp);
	}
	
	//若第一个Timer比第二个早，1，同样返回2,晚返回0
	public int compare_date(int y1,int m1,int d1,int y2,int m2,int d2)
	{
		if(y1 < y2)
			return 1;
		else if(y1 == y2)
		{
			if(m1 < m2)
				return 1;
			else if(m1 == m2)
			{
				if(d1 < d2)
					return 1;
				else if(d1 == d2)
					return 2;
			}
		}
		
		return  0;
		
	}
	
	
	
	public void setPlan()
	{
		plan_list.clear();
		for(int i = 0;i < task_list.size();i++)
			task_list.get(i).setPlanTime(task_list.get(i).getDoneTime());
		if(limit_time != 0)
		{
			Date date = new Date(System.currentTimeMillis());
			int cur_year = date.getYear()+1900;
			int cur_month = date.getMonth()+1;
			int cur_day = date.getDate();
			int tmp_limit = limit_time;
			
			ArrayList<String> tasks_names = new ArrayList<String>();
			ArrayList<Integer> need_time = new ArrayList<Integer>();
			Plan plan = new Plan(cur_year,cur_month,cur_day,limit_time,tasks_names,need_time,0);
			for(int i = 0;i < task_list.size();i++)
			{
				Task task = task_list.get(i);
				while(task.getPlanTime() < task.getCostTime())
				{
					if(task.getCostTime() - task.getPlanTime() < tmp_limit)
					{
						tasks_names.add(task.getName());
						need_time.add(task.getCostTime() - task.getPlanTime());
						tmp_limit -= (task.getCostTime() - task.getPlanTime());
						task.setPlanTime(task.getCostTime());
					}
					else
					{
						tasks_names.add(task.getName());
						need_time.add(tmp_limit);
						task.setPlanTime(task.getPlanTime() + tmp_limit);	
						tmp_limit = 0;
					}
					if(tmp_limit == 0 || (tmp_limit != 0 && tasks_names.size() != 0 && i == task_list.size() - 1))
					{
						plan_list.add(plan);			
						int next[] = new int[3];
						next = addOneDate(cur_year, cur_month, cur_day);
						tasks_names = new ArrayList<String>();
						need_time = new ArrayList<Integer>();
						plan = new Plan(next[0], next[1], next[2], limit_time, tasks_names, need_time, 0);
						cur_year = next[0];
						cur_month = next[1];
						cur_day = next[2];
						tmp_limit = limit_time;
					}
				}
			}
		}	
	}
	
	public int[] addOneDate(int cur_year,int cur_mon,int cur_day)
	{
		int next[] = new int[3];
		int day1[] = {31,28,31,30,31,30,31,31,30,31,30,31};
		int day2[] = {31,29,31,30,31,30,31,31,30,31,30,31};
		if((cur_year % 4 == 0 && cur_year % 100 != 0) || (cur_year % 400 == 0))
		{
			if(cur_day == day2[cur_mon-1])
			{
				if(cur_mon == 12)
				{
					next[0] = cur_year + 1;
					next[1] = 1;
					next[2] = 1;
				}
				else
				{
					next[0] = cur_year;
					next[1] = cur_mon + 1;
					next[2] = 1;
				}
			}
			else
			{
				next[0] = cur_year;
				next[1] = cur_mon;
				next[2] = cur_day + 1;
			}
		}
		else
		{
			if(cur_day == day1[cur_mon-1])
			{
				if(cur_mon == 12)
				{
					next[0] = cur_year + 1;
					next[1] = 1;
					next[2] = 1;
				}
				else
				{
					next[0] = cur_year;
					next[1] = cur_mon + 1;
					next[2] = 1;
				}
			}
			else
			{
				next[0] = cur_year;
				next[1] = cur_mon;
				next[2] = cur_day + 1;
			}
		}
		return next;
	}
	
	
	public void setList()
	{
		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
				
		for(int i = 0;i < plan_list.size();i++)
		{
			HashMap<String,String> map = new HashMap<String, String>();
			Plan plan = plan_list.get(i);
			ArrayList<String> tasks_names = plan.getNameList();
			map.put("date",plan.getYear() + "."+plan.getMonth()+"."+plan.getDay());
			map.put("task_num",tasks_names.size() + "");
//			map.put("date", task_list.size()+"");
//			map.put("task_num", limit_time + "");
			list.add(map);
		}
		
		SimpleAdapter listAdapter = new SimpleAdapter(getActivity(),list,R.layout.plan_list_view,new String[]{"date","task_num"},new int[]{R.id.date,R.id.task_num}) ;
		listview.setAdapter(listAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Plan plan = plan_list.get(arg2);
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
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		getActivity().unregisterReceiver(receiver);
		
		try {
			FileOutputStream os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
			for(int i = 0;i < plan_list.size();i++)
			{
				Plan plan = plan_list.get(i);
				ArrayList<String> names = plan.getNameList();
				ArrayList<Integer> need_time = plan.getTimeList();
				String tmp = plan.getYear() + "." + plan.getMonth() + "." + plan.getDay() + " ";
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
