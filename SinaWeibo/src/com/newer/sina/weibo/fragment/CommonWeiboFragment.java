package com.newer.sina.weibo.fragment;

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

import com.newer.sina.weibo.CollectionAdatper;
import com.newer.sina.weibo.CommonListActivity;
import com.newer.sina.weibo.R;
import com.newer.sina.weibo.dao.AccessTokenKeeper;
import com.newer.sina.weibo.dao.UserDao;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class CommonWeiboFragment extends Fragment implements OnRefreshListener,
		OnItemClickListener {

	public static final String TAG = null;
	private SwipeRefreshLayout refreshLayout;
	private ListView listView;
	private List<Status> dataSet;
	private CollectionAdatper adapter;
	private ConnectivityManager manager;
	private Oauth2AccessToken accessToken;
	private String jsonString;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_commonweibo, container,
				false);
		listView = (ListView) view.findViewById(R.id.listview_commonweibo);
		refreshLayout = (SwipeRefreshLayout) view
				.findViewById(R.id.swipe_refresh_commonweibo);
		refreshLayout.setColorScheme(android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light);
		refreshLayout.setOnRefreshListener(this);

		dataSet = new ArrayList<Status>();

		manager = (ConnectivityManager) getActivity().getSystemService(
				getActivity().CONNECTIVITY_SERVICE);

		accessToken = AccessTokenKeeper.getAccessToken(getActivity());

		getCommonWeibo();

		adapter = new CollectionAdatper(getActivity(), dataSet);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onRefresh() {
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
	 * 从数据库中获得最新的公共微博
	 */
	private void getCommonWeibo() {
		UserDao dao = new UserDao(getActivity());
		Cursor cursor = dao.readCommonweibo();
		if (cursor.moveToNext()) {
			jsonString = cursor.getString(1);
		}

		if (jsonString != null) {
			//showToast("从数据库中加载公共微博");
			Log.d(TAG, "从数据库中加载公共微博jsonString=" + jsonString);

			// 解析jsonString
			StatusList list = StatusList.parse(jsonString);
			Log.d(TAG, "解析获得statusList=" + list.statusList);
			if (list.statusList != null) {
				dataSet = list.statusList;
				Log.d(TAG, "加载dataSet=" + dataSet);
				//showToast("加载dataSet 更新listView");
			} else {
				//showToast("加载dataSet失败");
			}

		} else {
			showToast("下拉刷新粉丝列表");
		}
		// 关闭游标
		cursor.close();
	}

	class LoadTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String url = "https://api.weibo.com/2/statuses/public_timeline.json";
			accessToken = AccessTokenKeeper.getAccessToken(getActivity());
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
					Log.d(TAG, "获取最新的公共微博json=" + json);

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
				Log.d(TAG, "结果result=" + result);
				if (result.startsWith("{")) {
					//showToast("加载公共微博完成");
					// 停止刷新
					refreshLayout.setRefreshing(false);
					// 保存我的微博到数据库
					UserDao dao = new UserDao(getActivity());
					int row = dao.updateCommonweibo(result);
					//showToast("保存公共微博row=" + row);
				}
			} else {
				showToast("加载公共微博失败");
				refreshLayout.setRefreshing(false);
			}
		}

	}

	private void showToast(String string) {
		Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
		Status status = dataSet.get(position);
		String[] data = {
				status.user.avatar_large,
				status.user.screen_name,
				status.created_at,
				status.text,
				status.bmiddle_pic,
				String.valueOf(status.comments_count),
				status.source.substring(status.source.indexOf(">") + 1,
						status.source.length() - 4),
				status.idstr};
		Intent intent = new Intent(getActivity(), CommonListActivity.class);
		intent.putExtra("data", data);
		startActivity(intent);
	}
}
