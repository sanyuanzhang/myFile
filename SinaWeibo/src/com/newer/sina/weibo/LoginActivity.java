package com.newer.sina.weibo;

import com.newer.sina.weibo.dao.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_login);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		Oauth2AccessToken token = AccessTokenKeeper.getAccessToken(this);
		if (!token.getToken().equals("")) {
			//判断是否已授权
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		}
	}
	
	public void doAuth(View v) {
		Intent intent = new Intent(this, AuthActivity.class);
		startActivity(intent);
	}
	
}
