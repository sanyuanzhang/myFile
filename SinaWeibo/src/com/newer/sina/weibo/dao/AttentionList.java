package com.newer.sina.weibo.dao;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

/**
 * 关注列表结构体
 * @author Administrator
 *
 */
public class AttentionList {

	public List<Attention> attentionList;
	public String previous_cursor;
    public String next_cursor;
    public int total_number;
    
    public static AttentionList parse(String jsonString) {
    	if (TextUtils.isEmpty(jsonString)) {
			return null;
		}
    	
    	AttentionList attentions = new AttentionList();
    	try {
			JSONObject jsonObject = new JSONObject(jsonString);
//			attentions.previous_cursor = jsonObject.optString("previous_cursor", "0");
//			attentions.next_cursor = jsonObject.optString("next_cursor", "0");
//			attentions.total_number = jsonObject.optInt("total_number", 0);
			
			JSONArray jsonArray = jsonObject.optJSONArray("users");
			if (jsonArray != null && jsonArray.length() > 0) {
				int length = jsonArray.length();
				attentions.attentionList = new ArrayList<Attention>();
				for (int i = 0; i < length; i++) {
					attentions.attentionList.add(Attention.parse(jsonArray.optJSONObject(i)));
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
		return attentions;
    }
}
