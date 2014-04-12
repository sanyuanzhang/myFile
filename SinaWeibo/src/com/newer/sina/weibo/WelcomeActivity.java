package com.newer.sina.weibo;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class WelcomeActivity extends Activity {

	private static final String TAG = null;
	private RelativeLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		layout = (RelativeLayout) findViewById(R.id.relativeLayout);

		// 颜色渐变
		AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
		animation.setDuration(4000);
		layout.startAnimation(animation);
	}

	@Override
	protected void onStart() {
		super.onStart();

		new Thread() {

			public void run() {
				try {
					Thread.sleep(3000);
					Intent intent = new Intent(getApplicationContext(),
							LoginActivity.class);
					startActivity(intent);
					finish();

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();

	}
}
