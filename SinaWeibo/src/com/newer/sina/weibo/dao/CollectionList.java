package com.newer.sina.weibo.dao;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.sina.weibo.sdk.openapi.models.Status;

/**
 * 收藏列表结构体，解析多重嵌套json
 * @author Administrator
 *
 */
public class CollectionList {

	private static final String TAG = null;
	public ArrayList<Status> collectionList;
	public Status statuses;
    
	public static CollectionList parse(String jsonString) {
		if (TextUtils.isEmpty(jsonString)) {
			return null;
		}
		
		CollectionList collections = new CollectionList();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONArray jsonArray = jsonObject.getJSONArray("favorites");
			Log.d(TAG, "获得favorites jsonArray=" + jsonArray);
			if (jsonArray != null && jsonArray.length() > 0) {
				int length = jsonArray.length();
				collections.collectionList = new ArrayList<Status>();
				for (int i = 0; i < length; i++) {
					JSONObject object = jsonArray.getJSONObject(i);
					Log.d(TAG, "获得 object=" + object + "\n");
					JSONObject jsonob = object.getJSONObject("status");
					Log.d(TAG, "获得status jsonob=" + jsonob + "\n");
					
					collections.collectionList.add(Status.parse(jsonob));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return collections;
	}
}
