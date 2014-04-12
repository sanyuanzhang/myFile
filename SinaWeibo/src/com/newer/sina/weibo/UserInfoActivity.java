package com.newer.sina.weibo;

import java.util.HashMap;
import java.util.Map;

import com.newer.sina.weibo.dao.UserDao;
import com.sina.weibo.sdk.openapi.models.User;
import com.squareup.picasso.Picasso;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoActivity extends ActionBarActivity {

	private ActionBar actionBar;
	//用户图片
	private ImageView imageView;
	//昵称
	private TextView textViewName;
	//所在地
	private TextView textViewLocation;
	//性别
	private TextView textViewGender;
	//关注数
	private TextView textViewAttention;
	//粉丝数
	private TextView textViewFans;
	//收藏数
	private TextView textViewCollection;
	//个人简介
	private TextView textViewDescription;
	//博客地址
	private TextView textViewUrl;
	//创建时间
	private TextView textViewCreate;
	//是否是认证用户
	private TextView textViewVerified;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userinfo);
		
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("个人资料");
		
		initView();
		
		getUserInfo();
	}

	private void initView() {
		imageView = (ImageView) findViewById(R.id.imageView_userInfo);
		textViewName = (TextView) findViewById(R.id.textView_userInfo_name);
		textViewLocation = (TextView) findViewById(R.id.textView_userInfo_location);
		textViewGender = (TextView) findViewById(R.id.textView_userInfo_gender);
		textViewAttention = (TextView) findViewById(R.id.textView_userInfo_attention);
		textViewFans = (TextView) findViewById(R.id.textView_userInfo_fans);
		textViewCollection = (TextView) findViewById(R.id.textView_userInfo_collection);
		textViewDescription = (TextView) findViewById(R.id.textView_userInfo_description);
		textViewUrl = (TextView) findViewById(R.id.textView_userInfo_url);
		textViewCreate = (TextView) findViewById(R.id.textView_userInfo_create);
		textViewVerified = (TextView) findViewById(R.id.textView_userInfo_verified);
	}
	
	/**
	 * 从数据库中获得用户信息
	 */
	private void getUserInfo() {
		UserDao dao = new UserDao(this);
		Cursor cursor = dao.readUserJson();
		String json = null;
		if (cursor.moveToNext()) {
			json = cursor.getString(1);
		}
		// 判断json数据是否为空，如果为空从网上加载用户信息
		if (json != null) {
			//showToast("从数据库中获取用户信息");
			
			User user = User.parse(json);
			//加载图片
			Picasso.with(this).load(user.avatar_large).into(imageView);
			textViewName.setText(user.screen_name);
			textViewLocation.setText(user.location);
			textViewGender.setText("gender:" + user.gender);
			textViewAttention.setText("关注：" + user.friends_count);
			textViewFans.setText("粉丝：" + user.followers_count);
			textViewCollection.setText("收藏：" + user.favourites_count);
			textViewDescription.setText("个人描述：" + user.description);
			textViewUrl.setText("博客地址：" + user.url);
			textViewCreate.setText("创建时间：" + user.created_at);
			textViewVerified.setText("是否为认证用户：" + user.verified);

		} else {
			
		}
		cursor.close();
	}

	private void showToast(String string) {
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
	}
}
