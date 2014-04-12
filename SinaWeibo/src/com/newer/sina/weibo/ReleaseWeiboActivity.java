package com.newer.sina.weibo;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import com.newer.sina.weibo.dao.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ReleaseWeiboActivity extends ActionBarActivity {

	public static final String TAG = null;
	private ActionBar actionBar;
	private EditText editText;
	private ProgressDialog dialog;
	private ConnectivityManager manager;
	private Oauth2AccessToken accessToken;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_release_weibo);

		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("写微博");

		manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		accessToken = AccessTokenKeeper.getAccessToken(this);

		dialog = new ProgressDialog(this);
		dialog.setMessage("正在发布...");

		editText = (EditText) findViewById(R.id.editText_release_weibo);
	}

	public void doRelease(View v) {
		String status = editText.getText().toString();
		if (!status.equals("")) {
			NetworkInfo info = manager.getActiveNetworkInfo();
			if (info != null && info.isConnected()) {
				//showToast("发布微博");
				String token = accessToken.getToken();
				new ReleaseTask().execute(token, status);
			} else {
				showToast("网络异常");
			}
		} else {
			showToast("请输入内容");
		}
	}

	class ReleaseTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			String url = "https://api.weibo.com/2/statuses/update.json";

			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 1000 * 15);
			HttpConnectionParams.setSoTimeout(httpParams, 1000 * 15);
			HttpClient client = new DefaultHttpClient(httpParams);
			// post.addHeader("Content-Type", "application/json");

			HttpPost httpPost = new HttpPost(url);
			try {
				// Post参数
				List<NameValuePair> mparams = new ArrayList<NameValuePair>();
				mparams.add(new BasicNameValuePair("access_token", params[0]));
				mparams.add(new BasicNameValuePair("status", params[1]));
				// 设置字符集
				HttpEntity mentity = new UrlEncodedFormEntity(mparams, "utf-8");
				// 设置参数实体
				httpPost.setEntity(mentity);
				Log.d(TAG, "发布微博的内容=" + params[1]);

				HttpResponse response = client.execute(httpPost);
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

			dialog.dismiss();
			Log.d(TAG, "发布微博结果result=" + result);
			if (result != null) {
				if (result.startsWith("{")) {
					showToast("发布成功");
					editText.setText("");
				} else {
					showToast("发布失败");
				}
			} else {
				showToast("发布失败");
			}
		}

	}

	private void showToast(String string) {
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
	}
}
