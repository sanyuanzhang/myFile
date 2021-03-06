package com.newer.sina.weibo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import com.newer.sina.weibo.dao.AccessTokenKeeper;
import com.newer.sina.weibo.dao.CollectionList;
import com.newer.sina.weibo.dao.UserDao;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.openapi.models.Status;

import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

public class CollectionActivity extends ActionBarActivity implements
		OnRefreshListener {

	public static final String TAG = "CollectionActivity";
	private ActionBar actionBar;
	private SwipeRefreshLayout refreshLayout;
	private ListView listView;
	private ConnectivityManager manager;
	private Oauth2AccessToken accessToken;
	private String jsonString;
	private List<Status> dataSet;
	private CollectionAdatper adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collection);

		String collectionCount = getIntent().getStringExtra("collectionCount");

		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("收藏: " + collectionCount);

		manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		listView = (ListView) findViewById(R.id.listview_collection);
		dataSet = new ArrayList<Status>();

		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_collection);
		refreshLayout.setColorScheme(android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light);
		refreshLayout.setOnRefreshListener(this);

		getCollections();

		adapter = new CollectionAdatper(this, dataSet);
		listView.setAdapter(adapter);

	}

	@Override
	public void onRefresh() {
		// 通知SwipeRefreshLayout刷新状态改变，开始刷新
		refreshLayout.setRefreshing(true);

		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			//showToast("开始加载数据");
			new LoadTask().execute();
		} else {
			showToast("网络连接错误");
			refreshLayout.setRefreshing(false);
		}
	}

	/**
	 * 从数据库中加载收藏
	 */
	public void getCollections() {
		UserDao userDao = new UserDao(this);
		Cursor cursor = userDao.readCollections();
		if (cursor.moveToNext()) {
			jsonString = cursor.getString(1);
		}

		if (jsonString != null) {
			//showToast("从数据库中加载收藏列表json");
			Log.d(TAG, "从数据库中加载收藏列表json=" + jsonString);

			// 解析json
			CollectionList collections = CollectionList.parse(jsonString);
			Log.d(TAG, "解析获得statusList=" + collections.collectionList);
			if (collections.collectionList != null) {
				dataSet = collections.collectionList;
				Log.d(TAG, "加载dataSet=" + dataSet);
				//showToast("加载dataSet 更新listView");
			} else {
				//showToast("加载dataSet失败");
			}

		} else {
			showToast("下拉刷新粉丝列表");
		}
		cursor.close();
	}

	/**
	 * 加载收藏
	 * 
	 * @author Administrator
	 * 
	 */
	class LoadTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			String url = "https://api.weibo.com/2/favorites.json";
			accessToken = AccessTokenKeeper
					.getAccessToken(getApplicationContext());
			String token = accessToken.getToken();
			
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 1000 * 15);
			HttpConnectionParams.setSoTimeout(httpParams, 1000 * 15);
			HttpClient client = new DefaultHttpClient(httpParams);
			
			HttpGet get = new HttpGet(url + "?" + "access_token=" + token);
			try {
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				int code = response.getStatusLine().getStatusCode();
				if (code == 200) {
					String json = EntityUtils.toString(entity, "utf-8");
					Log.d(TAG, "获取用户的收藏列表json=" + json);
					return json;
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (result != null) {

				Log.d(TAG, "result=" + result);
				if (result.startsWith("{")) {

					//showToast("加载收藏列表完成");
					// 停止刷新
					refreshLayout.setRefreshing(false);
					// 保存收藏列表到数据库
					UserDao dao = new UserDao(getApplicationContext());
					int row = dao.updateCollections(result);
					//showToast("保存收藏列表到数据库row=" + row);
					// 更新列表

				}

			} else {
				showToast("加载收藏列表失败");
				refreshLayout.setRefreshing(false);
			}
		}

	}

	private void showToast(String string) {
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
	}
}
