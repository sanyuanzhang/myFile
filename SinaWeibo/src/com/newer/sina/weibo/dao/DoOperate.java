package com.newer.sina.weibo.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class DoOperate {
	public static final String TAG = null;
	private Context context;
	private ConnectivityManager manager;
	private Oauth2AccessToken accessToken;

	public DoOperate(Context context) {
		this.context = context;
		manager = (ConnectivityManager) context
				.getSystemService(context.CONNECTIVITY_SERVICE);
		accessToken = AccessTokenKeeper.getAccessToken(context);
	}

	/**
	 * 关注某人
	 */
	public void doAttention() {
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {

		} else {

		}
	}

	/**
	 * 取消关注
	 */
	public void doRemove() {
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {

		} else {

		}
	}

	/**
	 * 删除微博
	 * 
	 * @param id
	 */
	public void removeWeibo(String id) {
		String url = "https://api.weibo.com/2/statuses/destroy.json";
		String token = accessToken.getToken();
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			showToast("执行删除微博");
			new doTask().execute(url, token, id);
		} else {

		}
	}

	class doTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 1000 * 15);
			HttpConnectionParams.setSoTimeout(httpParams, 1000 * 15);
			HttpClient httpClient = new DefaultHttpClient(httpParams);

			Log.d(TAG, "id=" + params[2] + ",access_token=" + params[1]);

			HttpPost httpPost = new HttpPost(params[0]);
			// Post参数
			List<NameValuePair> mparams = new ArrayList<NameValuePair>();
			mparams.add(new BasicNameValuePair("id", params[2]));
			mparams.add(new BasicNameValuePair("access_token", params[1]));

			try {
				// 设置字符集
				HttpEntity mentity = new UrlEncodedFormEntity(mparams, "utf-8");
				// 设置参数实体
				httpPost.setEntity(mentity);

				HttpResponse response = httpClient.execute(httpPost);
				HttpEntity entity = response.getEntity();
				int code = response.getStatusLine().getStatusCode();
				if (code == 200) {
					String json = EntityUtils.toString(entity, "utf-8");
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
				Log.d(TAG, "doTask result=" + result);
				showToast("成功删除");
			} else {

			}
		}

	}

	private void showToast(String string) {
		Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
	}
}
