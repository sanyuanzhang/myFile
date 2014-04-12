package com.newer.sina.weibo;

import java.util.Locale;

import com.newer.sina.weibo.dao.AccessTokenKeeper;
import com.newer.sina.weibo.dao.UserDao;
import com.newer.sina.weibo.fragment.AboutFragment;
import com.newer.sina.weibo.fragment.CommonWeiboFragment;
import com.newer.sina.weibo.fragment.HomeFragment;
import com.newer.sina.weibo.fragment.MyWeiboFragment;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

public class MainActivity extends FragmentActivity {

	public static final String TAG = "MainActivity";

	private SectionsPagerAdapter mSectionsPagerAdapter;

	private ViewPager mViewPager;

	private ActionBar actionBar;
	private MenuItem actionNew;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initActionBar();

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	private void initActionBar() {
		actionBar = getActionBar();
		/*
		 * actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		 * 
		 * // 下拉列表数据适配器 SpinnerAdapter adapter =
		 * ArrayAdapter.createFromResource(this, R.array.action_list,
		 * android.R.layout.simple_spinner_item);
		 * 
		 * actionBar.setListNavigationCallbacks(adapter, new
		 * OnNavigationListener() {
		 * 
		 * @Override public boolean onNavigationItemSelected(int arg0, long
		 * arg1) {
		 * 
		 * return false; } });
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		actionNew = menu.findItem(R.id.action_new);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		AlertDialog.Builder builder = new Builder(this);
		switch (item.getItemId()) {
		case R.id.action_exit:

			builder.setMessage("退出wei博江湖？");
			builder.setNegativeButton("取消", null);
			builder.setPositiveButton("确定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {

					finish();
				}
			});
			builder.show();
			break;

		case R.id.action_new:
			Intent intent = new Intent(this, ReleaseWeiboActivity.class);
			startActivity(intent);
			break;

		case R.id.action_logout:
			builder.setMessage("注销wei博江湖？");
			builder.setNegativeButton("取消", null);
			builder.setPositiveButton("确定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 退出当前账号，清空sharedpreference
					AccessTokenKeeper.clear(getApplicationContext());
					// 清空数据库
					UserDao dao = new UserDao(getApplicationContext());
					dao.deleteUserJson();
					// 跳转到登录界面
					Intent intent = new Intent(getApplicationContext(),
							LoginActivity.class);
					startActivity(intent);
				}
			});
			builder.show();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;

			switch (position) {
			case 0:
				fragment = new HomeFragment();
				Log.d(TAG, "点击 HomeFragment");
				break;

			case 1:
				fragment = new MyWeiboFragment();
				Log.d(TAG, "点击MyWeiboFragment");
				break;

			case 2:
				fragment = new CommonWeiboFragment();
				Log.d(TAG, "点击CommonWeiboFragment");
				break;

			case 3:
				fragment = new AboutFragment();
				Log.d(TAG, "点击AboutFragment");
				break;
			}

			return fragment;
		}

		@Override
		public int getCount() {
			// Show 4 total pages.
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale locale = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(locale);
			case 1:
				return getString(R.string.title_section2).toUpperCase(locale);
			case 2:
				return getString(R.string.title_section3).toUpperCase(locale);
			case 3:
				return getString(R.string.title_section4).toUpperCase(locale);
			}
			return null;
		}

	}

}
