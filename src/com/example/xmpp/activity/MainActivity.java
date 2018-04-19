/**
 * 
 */
package com.example.xmpp.activity;

import java.util.ArrayList;

import java.util.List;

import org.jivesoftware.smack.packet.Session;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.example.xmpp.R;
import com.example.xmpp.fragment.ContactFragment;
import com.example.xmpp.fragment.SessionFragment;
import com.example.xmpp.uitls.ToolBarUtil;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Administrator
 *
 */
public class MainActivity extends ActionBarActivity{
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
//	@InjectView(R.id.main_bottom)
//	LinearLayout mMainBottom;
//	@InjectView(R.id.main_tv_title)
//	TextView mMainTvTitle;
//	@InjectView(R.id.main_viewpager)
//	ViewPager mMainViewpager;
	
	
	private List<Fragment> mFragments = new ArrayList<Fragment>();
	private ViewPager mMainViewpager;
	private LinearLayout mMainBottom;


	private ToolBarUtil mToolBarUtil;
	private String[] mToolBarTileArr;
	private TextView mMainTvTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.inject(this);
		//视频上写Butterknife的inject方式是可以的，
		//但是不知道什么原因导致无法查找到相应的viewpager,只能使用这种方式来写
		mMainViewpager = (ViewPager) findViewById(R.id.main_viewpager);
		mMainBottom = (LinearLayout) findViewById(R.id.main_bottom);
		mMainTvTitle = (TextView) findViewById(R.id.main_tv_title);
		initData();
		initListener();
	}

	/**
	 * 
	 */
	private void initListener() {
		mMainViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				//修改颜色
				mToolBarUtil.changeColor(arg0);
				//修改title
				mMainTvTitle.setText(mToolBarTileArr[arg0]);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		
		mToolBarUtil.setOnToolBarClickListener(new ToolBarUtil.OnToolBarClickListener() {
			
			@Override
			public void onToolBarClick(int position) {
				mMainViewpager.setCurrentItem(position);
				
			}
		});
		
	}

	/**
	 * 
	 */
	private void initData() {
		//ViewPager --> 三种Adapter
		//ViewPager --> view --> pagerAdapter
		//ViewPager --> fragment --> fragmentPagerAdapter --> fragment数量比较少
		//ViewPager --> fragment --> fragmentStatePagerAdapter
		//添加fragment到集合中
		mFragments.add(new SessionFragment());
		mFragments.add(new ContactFragment());


		mMainViewpager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		mToolBarUtil = new ToolBarUtil();
		mToolBarTileArr = new String[] {"会话","联系人"};
		//图表内容
		int[] iconArr = {R.drawable.selector_message,R.drawable.selector_selfinfo};
		 
		mToolBarUtil.createToolBar(mMainBottom,mToolBarTileArr,iconArr);

		//设置默认选中会话
		mToolBarUtil.changeColor(0);

		
	}
	
	class MyPagerAdapter extends FragmentPagerAdapter
	{

		/**
		 * @param fm
		 */
		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		/* (non-Javadoc)
		 * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
		 */
		@Override
		public Fragment getItem(int arg0) {
			return mFragments.get(arg0);
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#getCount()
		 */
		@Override
		public int getCount() {
			return 2;
		}

		
	}



}
