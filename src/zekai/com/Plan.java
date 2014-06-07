package zekai.com;

import java.util.ArrayList;


public class Plan{
	private int year;
	private int month;
	private int day;
	private int limit;
	private ArrayList<String> tasks_names;
	private ArrayList<Integer> need_time;
	private int state; // 1代表完成，0代表未完成
	
	
	public Plan()
	{
		this.year = 0;
		this.month = 0;
		this.day = 0;
		this.limit = 0;
		this.tasks_names = new ArrayList<String>();
		this.need_time = new ArrayList<Integer>();
		this.state = 0;
	}
	
	public Plan(int y,int m,int d,int l,ArrayList<String> tn,ArrayList<Integer> nt,int s) {
		year = y;
		month = m;
		day = d;
		state = s;
		limit = l;
		tasks_names = tn;
		need_time = nt;
	}
	
	public Plan(Plan plan)
	{
		this.year = plan.year;
		this.month = plan.month;
		this.day = plan.day;
		this.limit = plan.limit;
		this.tasks_names = new ArrayList<String>();
		this.need_time = new ArrayList<Integer>();
		for(int i = 0;i < plan.tasks_names.size();i++)
		{
			String name = tasks_names.get(i);
			int time = need_time.get(i);
			this.tasks_names.add(name);
			this.need_time.add(time);
		}
		this.state = plan.state;
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
	
	public int getLimit()
	{
		return limit;
	}
	
	public int getState()
	{
		return state;
	}
	
	public ArrayList<String> getNameList()
	{
		return tasks_names;
	}
	
	public ArrayList<Integer> getTimeList()
	{
		return need_time;
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
	
	public void setTaskNams(ArrayList<String> tn)
	{
		tasks_names = tn;
	}
	
	public void setNeedTime(ArrayList<Integer> nt)
	{
		need_time = nt;
	}
	
	public void setLimit(int l)
	{
		limit = l;
	}
	
	public void setState(int s)
	{
		state = s;
	}
	
	
//	public int describeContents() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	public void writeToParcel(Parcel dest, int flags) {
//		// TODO Auto-generated method stub
//		dest.writeInt(year);
//		dest.writeInt(month);
//		dest.writeInt(day);
//		dest.writeInt(limit);
//		dest.writeStringList(tasks_names);
//		dest.writeList(need_time);
//		dest.writeInt(state);
//		
//	}
//	
//	public static final Parcelable.Creator<Plan> CREATOR = new Creator<Plan>() {
//
//		public Plan createFromParcel(Parcel source) {
//			// TODO Auto-generated method stub
//			Plan plan = new Plan(source.readInt(),source.readInt(),source.readInt(),source.readInt(),source.readStringList(new lis),source.readlsource.readInt());
//			return plan;
//		}
//
//		public Plan[] newArray(int size) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//	};
}
