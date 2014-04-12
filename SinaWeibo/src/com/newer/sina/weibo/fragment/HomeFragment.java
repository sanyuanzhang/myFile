package com.newer.sina.weibo.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.newer.sina.weibo.CollectionActivity;
import com.newer.sina.weibo.FansActivity;
import com.newer.sina.weibo.MyAttentionActivity;
import com.newer.sina.weibo.R;
import com.newer.sina.weibo.UserInfoActivity;
import com.newer.sina.weibo.dao.AccessTokenKeeper;
import com.newer.sina.weibo.dao.UserDao;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.LogUtil;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class HomeFragment extends Fragment implements OnItemClickListener,
		OnRefreshListener {

	public static final String TAG = "HomeFragment";

	private static final String IMAGE = "image";

	private static final String ITEM = "item";

	private TextView textViewUserName;
	private TextView textViewLocation;
	private ImageView imageViewUser;
	private ListView listViewHome;
	private Button button;
	private List<Map<String, String>> dataSet;
	private SimpleAdapter adapter;
	private SwipeRefreshLayout refreshLayout;

	private ConnectivityManager manager;

	private Oauth2AccessToken accessToken;
	// 用户信息接口
	private UsersAPI usersAPI;
	// 用户信息json数据
	private String json;

	// 获取用户信息url
	private String url = "https://api.weibo.com/2/users/show.json";

	private int[] images = { R.drawable.ic_attention, R.drawable.ic_fans,
			R.drawable.ic_star };
	private String[] items = { "我关注的: ", "我的粉丝: ", "收藏: " };

	private String[] from = { IMAGE, ITEM };

	private int[] to = { R.id.imageView_home_list, R.id.textView_home_list };
	
	private String attentionCount;
	private String fansCount;
	private String collectionCount;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_home, container, false);
		textViewUserName = (TextView) view.findViewById(R.id.textView_userName);
		textViewLocation = (TextView) view.findViewById(R.id.textView_location);
		imageViewUser = (ImageView) view.findViewById(R.id.imageView_user);
		listViewHome = (ListView) view.findViewById(R.id.listView_home);
		refreshLayout = (SwipeRefreshLayout) view
				.findViewById(R.id.swipe_refresh_home);
		refreshLayout.setColorScheme(android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light);
		refreshLayout.setOnRefreshListener(this);
		
		button = (Button) view.findViewById(R.id.button_user);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), UserInfoActivity.class);
				startActivity(intent);
			}
		});
		
		accessToken = AccessTokenKeeper.getAccessToken(getActivity());
		manager = (ConnectivityManager) getActivity().getSystemService(
				getActivity().CONNECTIVITY_SERVICE);

		dataSet = new ArrayList<Map<String, String>>();
		Map<String, String> data;
		for (int i = 0; i < items.length; i++) {
			data = new HashMap<String, String>();
			data.put(IMAGE, String.valueOf(images[i]));
			data.put(ITEM, items[i]);
			dataSet.add(data);
		}

		// 从数据库中读取用户信息
		getUser();

		adapter = new SimpleAdapter(getActivity(), dataSet,
				R.layout.home_list_item, from, to);

		listViewHome.setAdapter(adapter);
		listViewHome.setOnItemClickListener(this);

		usersAPI = new UsersAPI(accessToken);

		return view;
	}

	@Override
	public void onRefresh() {
		refreshLayout.setRefreshing(true);

		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			//showToast("开始加载数据");
			long uid = Long.parseLong(accessToken.getUid());
			usersAPI.show(uid, requestListener);
		} else {
			showToast("网络连接错误");
			refreshLayout.setRefreshing(false);
		}
	}
	
	/**
	 * 从数据库中加载用户
	 */
	private void getUser() {
		UserDao dao = new UserDao(getActivity());
		Cursor cursor = dao.readUserJson();
		if (cursor.moveToNext()) {
			json = cursor.getString(1);
		}
		// 判断json数据是否为空，如果为空从网上加载用户信息
		if (json != null) {
			User user = User.parse(json);
			textViewUserName.setText(user.screen_name);
			textViewLocation.setText(user.location);
			//加载用户图片
			Picasso.with(getActivity()).load(user.avatar_large).into(imageViewUser);
			// 更新dataSet
			dataSet.clear();
			Map<String, String> data1 = new HashMap<String, String>();
			data1.put(IMAGE, String.valueOf(images[0]));
			// 关注数
			attentionCount = String.valueOf(user.friends_count);
			data1.put(ITEM, items[0] + user.friends_count);
			
			Map<String, String> data2 = new HashMap<String, String>();
			data2.put(IMAGE, String.valueOf(images[1]));
			// 粉丝数
			fansCount = String.valueOf(user.followers_count);
			data2.put(ITEM, items[1] + user.followers_count);
			
			Map<String, String> data3 = new HashMap<String, String>();
			data3.put(IMAGE, String.valueOf(images[2]));
			collectionCount = String.valueOf(user.favourites_count);
			// 收藏数
			data3.put(ITEM, items[2] + user.favourites_count);
			
			dataSet.add(data1);
			dataSet.add(data2);
			dataSet.add(data3);

			//showToast("从数据库中获取用户信息");
		} else {
			showToast("下拉刷新列表");
		}
		cursor.close();
	}

	private RequestListener requestListener = new RequestListener() {

		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				LogUtil.i(TAG, response);
				// 获得json
				json = response;

				// 调用 User#parse 将JSON串解析成User对象
				User user = User.parse(response);
				Log.d(TAG, "获得用户json数据=" + response);

				if (user != null) {
					// 更新用户昵称、location、image
					textViewUserName.setText(user.screen_name);
					textViewLocation.setText(user.location);

					// 保存用户信息到数据库
					UserDao dao = new UserDao(getActivity());
					Cursor cursor = dao.readUserJson();
					long id;
					if (cursor.moveToNext()) {
						//表数据存在，执行更新操作
						id = dao.updateUserJson(response);
					} else {
						//表为空，执行插入操作，用户第一次登陆的时候执行此操作
						id = dao.saveUser(response);
					}
					if (id == -1) {
						//showToast("保存用户信息失败 id=-1");
					} else {
						//showToast("保存用户id=" + id);
					}
					// 停止刷新
					refreshLayout.setRefreshing(false);

					//showToast("location=" + user.location);
					//showToast("获取user信息，用户昵称=" + user.screen_name);
					Log.d(TAG, "获得用户信息,用户昵称=" + user.screen_name);
				} else {
					Toast.makeText(getActivity(), "用户信息为空：" + response,
							Toast.LENGTH_LONG).show();
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException exception) {
			LogUtil.e(TAG, exception.getMessage());
			ErrorInfo info = ErrorInfo.parse(exception.getMessage());
			Toast.makeText(getActivity(), "获取用户信息出错了：" + info.toString(),
					Toast.LENGTH_SHORT).show();
		}

	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long id) {

		switch (position) {
		case 0:
			Intent intent = new Intent(getActivity(), MyAttentionActivity.class);
			intent.putExtra("attentionCount", attentionCount);
			startActivity(intent);
			break;

		case 1:
			Intent intent2 = new Intent(getActivity(), FansActivity.class);
			intent2.putExtra("fansCount", fansCount);
			startActivity(intent2);
			break;

		case 2:
			Intent intent3 = new Intent(getActivity(), CollectionActivity.class);
			intent3.putExtra("collectionCount", collectionCount);
			startActivity(intent3);
			break;
		}

	}

	private void showToast(String string) {
		Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
	}
}
