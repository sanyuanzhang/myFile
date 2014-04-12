package com.newer.sina.weibo;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.newer.sina.weibo.dao.AccessTokenKeeper;
import com.newer.sina.weibo.dao.Constants;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class AuthActivity extends Activity {

	public static final String TAG = null;
	private TextView textView;
	// 微博授权类，由库提供
	private WeiboAuth weiboAuth;
	// 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
	private Oauth2AccessToken myAccessToken;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);

		textView = (TextView) findViewById(R.id.textView_auth_token);

		// 创建微博实例
		weiboAuth = new WeiboAuth(this, Constants.APP_KEY,
				Constants.REDIRECT_URL, Constants.SCOPE);

		// web授权
		weiboAuth.anthorize(new AuthListener());
	}

	/**
	 * 微博认证授权回调类,当授权结束后，该回调就会被执行
	 * 
	 * @author Administrator
	 * 
	 */
	class AuthListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle bundle) {
			myAccessToken = Oauth2AccessToken.parseAccessToken(bundle);
			if (myAccessToken.isSessionValid()) {
				// 显示token

				String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
						.format(new Date(myAccessToken
								.getExpiresTime()));
				
				String message = String.format("Token：%1$s \n有效期：%2$s",
						myAccessToken.getToken(),date);
				
				textView.setText(message);

				//保存 Token 到 SharedPreferences
				AccessTokenKeeper.saveAccessToken(getApplicationContext(), myAccessToken);
				
				Log.d(TAG, "授权返回token信息：" + message);
				//授权成功，跳转到主界面
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				finish();

			} else {

			}
		}

		@Override
		public void onCancel() {
			Log.d(TAG, "onCancel");
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			Log.d(TAG, "onWeiboException");
		}

	}

}
