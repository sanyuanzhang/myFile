package com.newer.sina.weibo;

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
import com.newer.sina.weibo.dao.Attention;
import com.newer.sina.weibo.dao.AttentionList;
import com.newer.sina.weibo.dao.UserDao;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import android.content.Intent;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MyAttentionActivity extends ActionBarActivity implements
		OnRefreshListener, OnItemClickListener {

	public static final String TAG = "MyAttentionActivity";
	private ActionBar actionBar;
	private SwipeRefreshLayout refreshLayout;
	private ListView listView;
	private List<Attention> dataSet;
	private AttentionListAdapter adapter;
	private Oauth2AccessToken accessToken;
	private ConnectivityManager manager;
	private String jsonString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myattention);

		manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		listView = (ListView) findViewById(R.id.listview_attention);
		dataSet = new ArrayList<Attention>();

		String attentionCount = getIntent().getStringExtra("attentionCount");
		
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("我关注的: " + attentionCount);

		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_attention);
		refreshLayout.setOnRefreshListener(this);

		// 顶部刷新的样式
		refreshLayout.setColorScheme(android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light);

		// 判断数据库是否已保存关注的列表
		getAttention();

		adapter = new AttentionListAdapter(this, dataSet);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	/**
	 * 从数据库中取出数据
	 */
	private void getAttention() {
		UserDao userDao = new UserDao(this);
		Cursor cursor = userDao.readAttention();
		if (cursor.moveToNext()) {
			jsonString = cursor.getString(1);
			Log.d(TAG, "从数据库中获得关注jsonString=" + jsonString);
		}

		if (jsonString != null) {
			//Toast.makeText(this, "从数据库中加载关注人列表json", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "从数据库中加载关注列表json=" + jsonString);
			// 解析jsonString
			AttentionList attentions = AttentionList.parse(jsonString);
			if (attentions != null) {
				dataSet = attentions.attentionList;
				Log.d(TAG, "加载dataSet=" + dataSet);
//				Toast.makeText(this, "加载dataSet,更新listView", Toast.LENGTH_SHORT)
//						.show();
			}

		} else {
			Toast.makeText(this, "请下拉刷新关注列表", Toast.LENGTH_SHORT).show();
		}

		cursor.close();
	}

	@Override
	public void onRefresh() {
		// 通知SwipeRefreshLayout刷新状态改变，开始刷新
		refreshLayout.setRefreshing(true);

		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			//Toast.makeText(this, "开始加载数据", Toast.LENGTH_SHORT).show();
			new LoadAttentionsTask().execute();
		} else {
			Toast.makeText(this, "网络连接错误", Toast.LENGTH_SHORT).show();
			refreshLayout.setRefreshing(false);
		}

	}

	class LoadAttentionsTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String url = "https://api.weibo.com/2/friendships/friends.json";
			accessToken = AccessTokenKeeper
					.getAccessToken(getApplicationContext());
			String token = accessToken.getToken();
			String uid = accessToken.getUid();
			
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 1000 * 15);
			HttpConnectionParams.setSoTimeout(httpParams, 1000 * 15);
			HttpClient client = new DefaultHttpClient(httpParams);
			
			HttpGet get = new HttpGet(url + "?" + "uid=" + uid + "&"
					+ "access_token=" + token);
			try {
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				int code = response.getStatusLine().getStatusCode();
				if (code == 200) {
					String json = EntityUtils.toString(entity, "utf-8");
					Log.d(TAG, "获取用户的关注列表json=" + json);
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

//					Toast.makeText(getApplicationContext(), "加载关注列表完成",
//							Toast.LENGTH_SHORT).show();
					// 停止刷新
					refreshLayout.setRefreshing(false);
					// 保存关注列表到数据库
					UserDao dao = new UserDao(getApplicationContext());
					int row = dao.updateAttention(result);
//					Toast.makeText(getApplicationContext(),
//							"保存关注列表到数据库row=" + row, Toast.LENGTH_LONG).show();
					// 更新列表
//					getAttention();
//					adapter.notifyDataSetChanged();
				}

			} else {
				Toast.makeText(getApplicationContext(), "加载关注列表失败",
						Toast.LENGTH_SHORT).show();
				refreshLayout.setRefreshing(false);
			}
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		Intent intent = new Intent(this, AttentionUserActivity.class);
		intent.putExtra("position", position);
		startActivity(intent);
	}
}
