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
import com.newer.sina.weibo.R;
import com.newer.sina.weibo.dao.AccessTokenKeeper;
import com.newer.sina.weibo.dao.DoOperate;
import com.newer.sina.weibo.dao.UserDao;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MyWeiboFragment extends Fragment implements OnRefreshListener, OnItemLongClickListener, OnItemClickListener {

	protected static final String TAG = "MyWeiboFragment";
	
	private SwipeRefreshLayout refreshLayout;
	private ListView listView;
	private ConnectivityManager manager;
	private Oauth2AccessToken accessToken;
	private List<Status> dataSet;
	private String jsonString;
	//共用collectionAdapter
	private CollectionAdatper adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_myweibo, container,false);
		listView = (ListView) view.findViewById(R.id.listview_myweibo);
		manager = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
		refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_myweibo);
		refreshLayout.setColorScheme(android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light);
		refreshLayout.setOnRefreshListener(this);
		
		dataSet = new ArrayList<Status>();
		accessToken = AccessTokenKeeper.getAccessToken(getActivity());
		
		getMyweibo();
		
		adapter = new CollectionAdatper(getActivity(), dataSet);
		listView.setAdapter(adapter);
		listView.setOnItemLongClickListener(this);
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
	 * 从数据库中获得我的微博
	 */
	private void getMyweibo() {
		UserDao userDao = new UserDao(getActivity());
		Cursor cursor = userDao.readMyweibo();
		if (cursor.moveToNext()) {
			jsonString = cursor.getString(1);
		}
		
		if (jsonString != null) {
			//showToast("从数据库中加载我的微博");
			Log.d(TAG, "从数据库中加载我的微博jsonString=" + jsonString);
			
			//解析jsonString
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
		//关闭游标
		cursor.close();
	}
	
	class LoadTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String url = "https://api.weibo.com/2/statuses/user_timeline.json";
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
					Log.d(TAG, "获取我的微博json=" + json);
					
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
					//showToast("加载我的微博完成");
					//停止刷新
					refreshLayout.setRefreshing(false);
					//保存我的微博到数据库
					UserDao dao = new UserDao(getActivity());
					int row = dao.updateMyweibo(result);
					//showToast("保存我的微博row=" + row);
				}
			} else {
				showToast("加载我的微博失败");
				refreshLayout.setRefreshing(false);
			}
		}
		
	}

	private void showToast(String string) {
		Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position,
			long id) {
		//showToast("长按 item" + position);
		
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setMessage("删除微博？");
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//执行删除微博操作
				DoOperate doOperate = new DoOperate(getActivity());
				Status status = dataSet.get(position);
				doOperate.removeWeibo(status.id);
			}
		});
		builder.show();
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		//showToast("点击 item" + position);
		//跳转
	}
}
