package com.newer.sina.weibo;

import java.util.ArrayList;
import java.util.List;

import com.newer.sina.weibo.dao.Attention;
import com.newer.sina.weibo.dao.AttentionList;
import com.newer.sina.weibo.dao.UserDao;
import com.squareup.picasso.Picasso;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class AttentionUserActivity extends ActionBarActivity {

	private static final String TAG = "AttentionUserActivity";
	private ActionBar actionBar;
	private String jsonString;
	private List<Attention> dataSet;
	private ImageView imageView;
	private TextView textViewLocation;
	//状态  0：不在线  1：在线
	private TextView textViewOnline;
	private TextView textViewName;
	private TextView textViewDescription;
	private TextView textViewfriends;
	private TextView textViewFollowers;
	private TextView textViewStatuses;
	private TextView textViewFavourites;
	//是否关注我
	private TextView textViewFollowMe;
	//认证理由
	private TextView textViewVerified;
	private TextView textViewCreated;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attention_user);
		
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("");
		
		initView();
		dataSet = new ArrayList<Attention>();
		
		getAttention();
		
		int position = getIntent().getIntExtra("position", 0);
		Attention attention = dataSet.get(position);
		if (attention != null) {
			Picasso.with(this).load(attention.avatar_large).into(imageView);
			textViewLocation.setText(attention.location);
			if (attention.online_status == 0) {
				textViewOnline.setText("状态：不在线");
			} else {
				textViewOnline.setText("状态：在线");
			}
			textViewName.setText(attention.screen_name);
			textViewDescription.setText(attention.description);
			textViewfriends.setText("关注： " + attention.friends_count);
			textViewFollowers.setText("粉丝： " + attention.followers_count);
			textViewStatuses.setText("微博： " + attention.statuses_count);
			textViewFavourites.setText("收藏： " + attention.favourites_count);
			textViewFollowMe.setText("关注我： " + attention.follow_me);
			textViewVerified.setText("认证理由： " + attention.verified_reason);
			textViewCreated.setText("创建时间： " + attention.created_at);
		}
	}
	
	private void initView() {
		imageView = (ImageView) findViewById(R.id.imageView_attentionUser);
		textViewLocation = (TextView) findViewById(R.id.textView_Userlocation);
		textViewOnline = (TextView) findViewById(R.id.textView_online_status);
		textViewName = (TextView) findViewById(R.id.textView_attentionName);
		textViewDescription = (TextView) findViewById(R.id.textView_description);
		textViewfriends = (TextView) findViewById(R.id.textView_friends_count);
		textViewFollowers = (TextView) findViewById(R.id.textView_followers_count);
		textViewStatuses = (TextView) findViewById(R.id.textView_statuses_count);
		textViewFavourites = (TextView) findViewById(R.id.textView_favourites_count);
		textViewFollowMe = (TextView) findViewById(R.id.textView_follow_me);
		textViewVerified = (TextView) findViewById(R.id.textView_verified_reason);
		textViewCreated = (TextView) findViewById(R.id.textView_created_at);
	}

	private void getAttention() {
		UserDao userDao = new UserDao(this);
		Cursor cursor = userDao.readAttention();
		if (cursor.moveToNext()) {
			jsonString = cursor.getString(1);
			Log.d(TAG, "从数据库中获得关注jsonString=" + jsonString);
		}

		if (jsonString != null) {
			Log.d(TAG, "从数据库中加载关注列表json=" + jsonString);
			// 解析jsonString
			AttentionList attentions = AttentionList.parse(jsonString);
			if (attentions != null) {
				dataSet = attentions.attentionList;
				Log.d(TAG, "加载dataSet=" + dataSet);
			}

		} else {
			
		}
		cursor.close();
	}
}
