package zekai.com;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class StodoActivity extends FragmentActivity implements TabListener {

	private ViewPager mViewPager;
	public static final int MAX_TAB_SIZE = 3;
	public static final String ARGUMENTS_NAME1 = "args1";
	public static final String ARGUMENTS_NAME2 = "args2";
	public static final String ARGUMENTS_NAME3 = "args3";
	private static final String name1 = "未完成任务";
	private static final String name2 = "已完成计划";
	private static final String name3 = "计划列表";
	private TabFragmentPagerAdapter mAdapter;
	
	private static ArrayList<Task> task_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		task_list = new ArrayList<Task>();
		findViewById();
		initView();
	}

	private void findViewById() {
		mViewPager = (ViewPager) this.findViewById(R.id.pager);
	}

	private void initView() {
		
		final ActionBar mActionBar = getActionBar();
	
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayShowHomeEnabled(false);
		
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		

		mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
		
		mAdapter.notifyDataSetChanged();
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOffscreenPageLimit(MAX_TAB_SIZE);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			public void onPageSelected(int arg0) {
				
				mActionBar.setSelectedNavigationItem(arg0);
			}
			
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		//初始化 ActionBar
		for(int i=0;i<MAX_TAB_SIZE;i++){
			Tab tab = mActionBar.newTab();
			tab.setText(mAdapter.getPageTitle(i)).setTabListener(this);
			mActionBar.addTab(tab);
			
		}
	}

	public static class TabFragmentPagerAdapter extends FragmentPagerAdapter{

		public TabFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			Fragment ft = null;
			switch (arg0) {
			case 0:
				ft = new TaskFragment();				
				Bundle args1 = new Bundle();
				
				args1.putParcelableArrayList("task_list", task_list);
				args1.putString(ARGUMENTS_NAME1, name1);
				ft.setArguments(args1);
				break;
				
			case 1:
				ft = new Plan_Done_Fragment();
				Bundle args2 = new Bundle();
				args2.putParcelableArrayList("task_list", task_list);
				args2.putString(ARGUMENTS_NAME2, name2);
				ft.setArguments(args2);
				break;

			case 2:
				ft = new Plan_Undo_Fragment();				
				Bundle args3 = new Bundle();
				args3.putParcelableArrayList("task_list", task_list);
				args3.putString(ARGUMENTS_NAME3, name3);
				ft.setArguments(args3);
				
				break;
			}
			return ft;
		}

		@Override
		public int getCount() {
			
			return MAX_TAB_SIZE;
		}
		 @Override
        public CharSequence getPageTitle(int position) {
			 if(position == 0)
				 return name1;
			 else if (position == 1)
				 return name2;
			 else
				 return name3;
        }
	}


	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		
	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		
	}
}
