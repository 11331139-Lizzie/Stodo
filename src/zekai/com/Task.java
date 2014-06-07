package zekai.com;

import android.os.Parcel;
import android.os.Parcelable;

public class Task implements Parcelable{
	private String name;
	private int year;
	private int month;
	private int day;
	private int cost_time;
	private int plan_time;
	private int done_time;
	private int priority;  //1-5
	private int state; //1代表已完成，0代表未完成

	
	public Task(String n,int y,int m,int d,int ct,int pt,int dt,int p,int s)
	{
		name = n;
		year = y;
		month = m;
		day = d;
		cost_time = ct;
		plan_time = pt;
		done_time = dt;
		priority = p;
		state = s;
	}
	
	public Task(Task task)
	{
		this.name = task.name;
		this.year = task.year;
		this.month = task.month;
		this.day = task.day;
		this.cost_time = task.cost_time;
		this.plan_time = task.plan_time;
		this.done_time = task.done_time;
		this.priority = task.priority;
		this.state = task.state;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getYear()
	{
		return year;
	}
	
	public int getMonth()
	{
		return month;
	}
	
	public int getDay()
	{
		return day;
	}
	
	public int getCostTime()
	{
		return cost_time;
	}
	
	public int getPlanTime()
	{
		return plan_time;
	}
	
	public int getDoneTime()
	{
		return done_time;
	}
	
	public int getPriority()
	{
		return priority;
	}
	
	public int getState()
	{
		return state;
	}
	
	public void setName(String n)
	{
		name = n;
	}
	
	public void setYear(int y)
	{
		year = y;
	}
	
	public void setMonth(int m)
	{
		month = m;
	}
	
	public void setDay(int d)
	{
		day = d;
	}
	
	public void setPriority(int p)
	{
		priority = p;
	}
	
	public void setCostTime(int t)
	{
		cost_time = t;
	}
	
	public void setPlanTime(int t)
	{
		plan_time = t;
	}
	
	public void setDoneTime(int t)
	{
		done_time = t;
	}
	
	public void setState(int s)
	{
		state = s;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public static final Parcelable.Creator<Task> CREATOR = new Creator<Task>() {

		public Task createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			Task task = new Task(source.readString(),source.readInt(),source.readInt(),source.readInt(),source.readInt(),source.readInt(),source.readInt(),source.readInt(),source.readInt());
			return task;
		}

		public Task[] newArray(int size) {
			// TODO Auto-generated method stub
			return null;
		}
	};

	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(name);
		dest.writeInt(year);
		dest.writeInt(month);
		dest.writeInt(day);
		dest.writeInt(cost_time);
		dest.writeInt(plan_time);
		dest.writeInt(done_time);
		dest.writeInt(priority);
		dest.writeInt(state);
	}

}
