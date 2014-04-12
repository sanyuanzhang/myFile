package com.newer.sina.weibo.dao;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AccessTokenKeeper {

	private static final String PREFERENCES_NAME = "com_sina_weibo";
	private static final String KEY_UID = "uid";
	private static final String KEY_ACCESS_TOKEN = "access_token";
	private static final String KEY_EXPIRES_IN = "expires_in";

	/**
	 * 保存 Token 对象到 SharedPreferences
	 * 
	 * @param context
	 * @param token
	 */
	public static void saveAccessToken(Context context, Oauth2AccessToken token) {
		if (context == null && token == null) {
			return;
		}

		SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(KEY_UID, token.getUid());
		editor.putString(KEY_ACCESS_TOKEN, token.getToken());
		editor.putLong(KEY_EXPIRES_IN, token.getExpiresTime());
		editor.commit();
	}

	/**
	 * 从 SharedPreferences 获取 Token 信息
	 * @param context
	 * @return
	 */
	public static Oauth2AccessToken getAccessToken(Context context) {
		if (context == null) {
			return null;
		}

		Oauth2AccessToken token = new Oauth2AccessToken();
		SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCES_NAME, Context.MODE_PRIVATE);
		token.setUid(preferences.getString(KEY_UID, ""));
		token.setToken(preferences.getString(KEY_ACCESS_TOKEN, ""));
		token.setExpiresTime(preferences.getLong(KEY_EXPIRES_IN, 0));

		return token;
	}
	
	/**
	 * 清空 SharedPreferences 中 Token信息。
	 * @param context
	 */
	public static void clear(Context context) {
		if (context == null) {
			return;
		}
		SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
		
	}

}
