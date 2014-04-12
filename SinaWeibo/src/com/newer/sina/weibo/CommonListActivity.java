package com.newer.sina.weibo;

import java.io.IOException;

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
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.squareup.picasso.Picasso;

import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CommonListActivity extends ActionBarActivity {

	public static final String TAG = null;
	private ActionBar actionBar;
	private ImageView imageViewUser;
	private TextView textViewName;
	private TextView textViewDate;
	private TextView textViewSource;
	private TextView textViewContent;
	private ImageView imageViewContent;
	private TextView textCommonCount;
	private ListView listView;
	private String id;
	private Oauth2AccessToken accessToken;
	private ConnectivityManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_list);

		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("微博正文");

		manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		accessToken = AccessTokenKeeper.getAccessToken(this);
		String token = accessToken.getToken();

		initView();

		String[] data = getIntent().getStringArrayExtra("data");
		// 加载图片
		Picasso.with(this).load(data[0]).into(imageViewUser);
		if (!data[4].equals("")) {
			Picasso.with(this).load(data[4]).into(imageViewContent);
		}

		textViewName.setText(data[1]);
		textViewDate.setText("发布：" + data[2]);
		textViewSource.setText("来源：" + data[6]);
		textViewContent.setText(data[3]);
		textCommonCount.setText("评论： " + data[5]);

		id = data[7];
		
		new LoadTask().execute(id,token);
	}

	private void initView() {
		imageViewUser = (ImageView) findViewById(R.id.imageView_common_list);
		textViewName = (TextView) findViewById(R.id.textView_common_weiboname);
		textViewDate = (TextView) findViewById(R.id.textView_common_weibodate);
		textViewSource = (TextView) findViewById(R.id.textView_common_weibosource);
		textViewContent = (TextView) findViewById(R.id.textView_common_weibotext);
		imageViewContent = (ImageView) findViewById(R.id.imageView_common_weiboimage);
		textCommonCount = (TextView) findViewById(R.id.textView_comments_count);
		listView = (ListView) findViewById(R.id.listView_common);
	}

	class LoadTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String url = "https://api.weibo.com/2/comments/show.json";
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 1000 * 15);
			HttpConnectionParams.setSoTimeout(httpParams, 1000 * 15);
			HttpClient client = new DefaultHttpClient(httpParams);
			HttpGet get = new HttpGet(url + "?" + "id=" + params[0] + "&"
					+ "access_token=" + params[1]);
			try {
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				int code = response.getStatusLine().getStatusCode();
				if (code == 200) {
					String json = EntityUtils.toString(entity, "utf-8");
					Log.d(TAG, "评论列表json=" + json);
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
				//showToast("获得评论列表");
			} else {
				showToast("加载出错了");
			}
		}
	}

	public void showToast(String string) {
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
	}
}
