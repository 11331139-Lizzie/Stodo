package zekai.com;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class PlanInfoActivity extends Activity{
	private Button sure;
	private ListView listview;
	private TextView date;
	private ArrayList<String> tasks_names;
	private ArrayList<Integer> need_time;
	private int year;
	private int month;
	private int day;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.float_plan_activity);
		//屏蔽窗口之外的点击
		this.setFinishOnTouchOutside(false);
		
		Intent intent = getIntent();
		year = intent.getIntExtra("year", 0);
		month = intent.getIntExtra("month", 0);
		day = intent.getIntExtra("day", 0);
		tasks_names = intent.getStringArrayListExtra("tasks_names");
		need_time = intent.getIntegerArrayListExtra("need_time");
			
		findViews();
		setList();
	}
	
	public void findViews()
	{
		sure = (Button)findViewById(R.id.plan_sure);
		listview = (ListView)findViewById(R.id.float_plan_list);
		date = (TextView)findViewById(R.id.date_detail);
		
		date.setText(year + "." + month + "." + day);
		sure.setOnClickListener(new SureListener());
	}
	
	class SureListener implements OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
		
	}

	public void setList()
	{
		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		
		for(int i = 0;i < tasks_names.size();i++)
		{
			HashMap<String,String> map = new HashMap<String, String>();

			map.put("small","计划" + (i + 1));
			map.put("task_name",tasks_names.get(i));
			map.put("need_time", "用时" + need_time.get(i) + "h");
			list.add(map);
		}
		
		SimpleAdapter listAdapter = new SimpleAdapter(this,list,R.layout.float_plan_list,new String[]{"small","task_name","need_time"},new int[]{R.id.small_plan,R.id.small_plan_name,R.id.need_time}) ;
		listview.setAdapter(listAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				intent.putParcelableArrayListExtra("task_list", task_list);
//				intent.putExtra("current", arg2);
//				intent.setClass(getActivity(), TaskInfoActivity.class);
//				startActivityForResult(intent, 0);				
			}
		});
	}
}
