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

public class FansActivity extends ActionBarActivity implements
		OnRefreshListener {

	private static final String TAG = "FansActivity";
	private ActionBar actionBar;
	private SwipeRefreshLayout refreshLayout;
	private ListView listView;
	// 共用Attention的适配器
	private List<Attention> dataSet;
	private AttentionListAdapter adapter;

	private ConnectivityManager manager;
	private String jsonString;
	private Oauth2AccessToken accessToken;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fans);

		String fansCount = getIntent().getStringExtra("fansCount");
		
		actionBar = getSupportActionBar();
		actionBar.setTitle("粉丝: " + fansCount);
		actionBar.setDisplayHomeAsUpEnabled(true);

		manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

		listView = (ListView) findViewById(R.id.listview_fans);
		dataSet = new ArrayList<Attention>();

		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_fans);
		refreshLayout.setColorScheme(android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light);
		refreshLayout.setOnRefreshListener(this);

		getFans();

		adapter = new AttentionListAdapter(this, dataSet);
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
	
	class LoadTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			String url = "https://api.weibo.com/2/friendships/followers.json";
			accessToken = AccessTokenKeeper.getAccessToken(getApplicationContext());
			String token = accessToken.getToken();
			String uid = accessToken.getUid();
			
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 1000 * 15);
			HttpConnectionParams.setSoTimeout(httpParams, 1000 * 15);
			HttpClient client = new DefaultHttpClient(httpParams);
			
			HttpGet get = new HttpGet(url + "?" + "uid=" + uid + "&" + "access_token=" + token);
			try {
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				int code = response.getStatusLine().getStatusCode();
				if (code == 200) {
					String json = EntityUtils.toString(entity, "utf-8");
					Log.d(TAG, "获取用户的粉丝列表json=" + json);
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
			
			if (result !=null ) {

				Log.d(TAG, "result=" + result);
				if (result.startsWith("{")) {

					Toast.makeText(getApplicationContext(), "加载粉丝列表完成",
							Toast.LENGTH_SHORT).show();
					// 停止刷新
					refreshLayout.setRefreshing(false);
					// 保存粉丝列表到数据库
					UserDao dao = new UserDao(getApplicationContext());
					int row = dao.updateFans(result);
					Toast.makeText(getApplicationContext(),
							"保存粉丝列表到数据库row=" + row, Toast.LENGTH_LONG).show();
					// 更新列表
					
				}

			} else {
				Toast.makeText(getApplicationContext(), "加载粉丝列表失败",
						Toast.LENGTH_SHORT).show();
				refreshLayout.setRefreshing(false);
			}
		}
		
	}

	/**
	 * 判断数据库中是否保存了粉丝列表
	 */
	private void getFans() {
		UserDao dao = new UserDao(this);
		Cursor cursor = dao.readFans();
		if (cursor.moveToNext()) {
			jsonString = cursor.getString(1);
		}

		if (jsonString != null) {
			//showToast("从数据库中加载粉丝列表json");
			Log.d(TAG, "从数据库中加载粉丝列表json=" + jsonString);

			// 解析jsonString
			AttentionList attentions = AttentionList.parse(jsonString);
			if (attentions != null) {
				dataSet = attentions.attentionList;
				Log.d(TAG, "加载dataSet=" + dataSet);
				//showToast("加载dataSet 更新listView");
			}
		} else {
			showToast("下拉刷新粉丝列表");
		}
		cursor.close();
	}

	/**
	 * 提示信息
	 * 
	 * @param string
	 */
	private void showToast(String string) {
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
	}
}
