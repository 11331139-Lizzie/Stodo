package zekai.com;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

public class TaskInfoActivity extends Activity{
	private EditText task_name;
	private EditText deadline;
	private EditText cost_time;
	private EditText priority;
	private Button sure;
	private Button cancel;
	private Button reset;
	private Button delete;
	
	private ArrayList<Task> task_list;
	private int current;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.float_task_activity);
		//屏蔽窗口之外的点击
		this.setFinishOnTouchOutside(false);
		
		//
		Intent intent = getIntent();
		task_list = intent.getParcelableArrayListExtra("task_list");
		current = intent.getIntExtra("current", -1);

		findView();
		addListener();
		
		if(current != -1)
		{
			//这是来查看任务的
			Task current_task = task_list.get(current);
			task_name.setText(current_task.getName());
			String time = current_task.getYear() + "." + current_task.getMonth() + "." + current_task.getDay();
			deadline.setText(time);
			cost_time.setText(current_task.getCostTime() + "");
			priority.setText(current_task.getPriority() + "");
		}
	}
	
	public void findView()
	{
		task_name = (EditText)findViewById(R.id.task_name_detail);
		deadline = (EditText)findViewById(R.id.deadline_detail);
		cost_time = (EditText)findViewById(R.id.cost_time_detail);
		priority = (EditText)findViewById(R.id.priority_detail);
		sure = (Button)findViewById(R.id.sure_button);
		cancel = (Button)findViewById(R.id.cancel_button);
		reset = (Button)findViewById(R.id.reset_button);
		delete = (Button)findViewById(R.id.delete_button);
		
		if(current == -1)
			delete.setVisibility(4);
		
		deadline.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		cost_time.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		priority.setInputType(EditorInfo.TYPE_CLASS_PHONE);
	}
	
	public void addListener()
	{
		sure.setOnClickListener(new SureListener());
		cancel.setOnClickListener(new CancelListener());
		reset.setOnClickListener(new ResetListener());
		delete.setOnClickListener(new DeleteListener());		
	}
	
	public boolean check(String name,String date,String time,String tmppri)
	{
		boolean ok = true;
		if(name == null || name.equals(""))
		{
			task_name.setText("名字不能为空");
			
			//task_name.setHintTextColor(0xDC143C);
			ok = false;
		}
		if(date == null || date.equals(""))
		{
			deadline.setHint("截止日期不能为空");
			deadline.setHintTextColor(0xDC143C );
			ok = false;
		}
		if(time == null || time.equals(""))
		{
			cost_time.setHint("所需时间不能为空");
			cost_time.setHintTextColor(0xDC143C);
			ok = false;
		}
		if(tmppri == null || tmppri.equals(""))
		{
			priority.setHint("优先级不能为空");
			priority.setHintTextColor(0xDC143C);
			ok = false;
		}
		
		if(ok)
		{
			return true;
		}
		return false;
	}
	
	class SureListener implements OnClickListener {

		public void onClick(View v) {
			String name = task_name.getText().toString();
			String date = deadline.getText().toString();
			String tmptime = cost_time.getText().toString();
			String tmppri = priority.getText().toString();

			if(check(name,date,tmptime,tmppri))
			{
				int time = Integer.parseInt(tmptime);
				int pri = Integer.parseInt(tmppri);
				int year = Integer.parseInt(date.substring(0, 4));
				int i = 5;
				while(date.charAt(i) != '.')
					i++;
				int month = Integer.parseInt(date.substring(5, i));
				int day = Integer.parseInt(date.substring(i + 1,date.length()));
				if(current == -1)
				{
					Task new_task = new Task(name,year,month,day,time,0,0,pri,0);
					task_list.add(new_task);
				}
				else
				{
					Task current_task = task_list.get(current);
					current_task.setName(name);
					current_task.setYear(year);
					current_task.setMonth(month);
					current_task.setDay(day);
					current_task.setCostTime(time);
					current_task.setPriority(pri);
					current_task.setState(0);
				}
				
				Intent intent = new Intent();
				intent.putParcelableArrayListExtra("task_list", task_list);
				setResult(2, intent);
				finish();
			}
		}	
		
	}
	
	class CancelListener implements OnClickListener {

		public void onClick(View v) {
			Intent intent = new Intent();
			intent.putParcelableArrayListExtra("task_list", task_list);
			setResult(2, intent);
			finish();
		}
		
	}
	
	class ResetListener implements OnClickListener{

		public void onClick(View v) {
			task_name.setText("");
			deadline.setText("");
			cost_time.setText("");
			priority.setText("");
		}		
	}
	
	class DeleteListener implements OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			String name = task_name.getText().toString();
			for(int i = 0;i < task_list.size();i++)
			{
				Task task = task_list.get(i);
				if(task.getName().equals(name))
				{
					task_list.remove(i);
					break;
				}
			}
			
			Intent intent = new Intent();
			intent.putParcelableArrayListExtra("task_list", task_list);
			setResult(2, intent);
			finish();
		}
		
	}
}
