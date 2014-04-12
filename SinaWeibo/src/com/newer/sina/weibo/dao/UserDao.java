package com.newer.sina.weibo.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserDao {

	private static final String KEY_USER_JSON = "json";
	private static final String TAG = "UserDao";
	private static final String KEY_ATTENTION_JSON = "attention_json";
	private static final String KEY_FANS_JSON = "fans_json";
	private static final String KEY_COLLECTION_JSON = "favorites_json";
	private static final String KEY_MYWEIBO_JSON = "weibo_json";
	private static final String KEY_COMMONWEIBO_JSON = "common_json";
	private SQLiteDatabase db;

	public UserDao(Context context) {
		db = new DBOpenHelper(context).getWritableDatabase();
	}

	/**
	 * 保存用户信息
	 * 
	 * @param json
	 *            用户信息的json数据
	 * @return 插入行的id
	 */
	public long saveUser(String json) {
		ContentValues values = new ContentValues();
		values.put(KEY_USER_JSON, json);
		long id = db.insert(DBOpenHelper.USER_TABLE_NAME, null, values);
		Log.d(TAG, "数据库保存用户信息：id=" + id);
		return id;
	}

	/**
	 * 从数据库中获得用户个人信息json
	 * 
	 * @return cursor
	 */
	public Cursor readUserJson() {
		Cursor cursor = db.query(DBOpenHelper.USER_TABLE_NAME, new String[] {
				"_id", "json" }, null, null, null, null, null);
		return cursor;
	}

	/**
	 * 更新用户个人信息
	 * 
	 * @param json
	 * @return row
	 */
	public int updateUserJson(String json) {
		ContentValues values = new ContentValues();
		values.put(KEY_USER_JSON, json);
		
		int row = db.update(DBOpenHelper.USER_TABLE_NAME, values, null,
				null);
		Log.d(TAG, "更新数据库用户信息：row=" + row);
		return row;
	}

	/**
	 * 删除用户数据
	 * 
	 * @return
	 */
	public int deleteUserJson() {
		int row = db.delete(DBOpenHelper.USER_TABLE_NAME, null, null);
		return row;
	}

	/**
	 * 获得关注列表
	 * 
	 * @return
	 */
	public Cursor readAttention() {
		Cursor cursor = db.query(DBOpenHelper.USER_TABLE_NAME,
				new String[] { "_id", "attention_json" }, null, null, null,
				null, null);
		return cursor;
	}

	/**
	 * 更新关注列表
	 * 
	 * @param json
	 * @return
	 */
	public int updateAttention(String json) {
		ContentValues values = new ContentValues();
		values.put(KEY_ATTENTION_JSON, json);
		int row = db.update(DBOpenHelper.USER_TABLE_NAME, values,
				null, null);
		return row;
	}

	/**
	 * 获取粉丝列表
	 * @return
	 */
	public Cursor readFans() {
		Cursor cursor = db.query(DBOpenHelper.USER_TABLE_NAME, new String[] { "_id",
				"fans_json" }, null, null, null, null, null);
		return cursor;
	}
	
	/**
	 * 更新粉丝列表
	 * @param json
	 * @return
	 */
	public int updateFans(String json) {
		ContentValues values = new ContentValues();
		values.put(KEY_FANS_JSON, json);
		int row = db.update(DBOpenHelper.USER_TABLE_NAME, values, null, null);
		return row;
	}
	
	/**
	 * 获得收藏列表
	 * @return
	 */
	public Cursor readCollections() {
		Cursor cursor = db.query(DBOpenHelper.USER_TABLE_NAME, new String[] { "_id",
				"favorites_json" }, null, null, null, null, null);
		return cursor;
	}
	
	/**
	 * 更新收藏列表
	 * @param json
	 * @return
	 */
	public int updateCollections(String json) {
		ContentValues values = new ContentValues();
		values.put(KEY_COLLECTION_JSON, json);
		int row = db.update(DBOpenHelper.USER_TABLE_NAME, values, null, null);
		return row;
	}
	
	/**
	 * 获得我的微博
	 * @return cursor
	 */
	public Cursor readMyweibo() {
		Cursor cursor = db.query(DBOpenHelper.USER_TABLE_NAME, new String[] { "_id",
				"weibo_json" }, null, null, null, null, null);
		return cursor;
	} 
	
	/**
	 * 更新我的微博
	 * @param json
	 * @return
	 */
	public int updateMyweibo(String json) {
		ContentValues values = new ContentValues();
		values.put(KEY_MYWEIBO_JSON, json);
		int row = db.update(DBOpenHelper.USER_TABLE_NAME, values, null, null);
		return row;
	}
	
	/**
	 * 获得公共微博
	 * @return
	 */
	public Cursor readCommonweibo() {
		Cursor cursor = db.query(DBOpenHelper.USER_TABLE_NAME, new String[] { "_id",
				"common_json" }, null, null, null, null, null);
		return cursor;
	} 
	
	/**
	 * 更新公共微博
	 * @param json
	 * @return
	 */
	public int updateCommonweibo(String json) {
		ContentValues values = new ContentValues();
		values.put(KEY_COMMONWEIBO_JSON, json);
		//在表中插入新的一列  common_json
		//db.execSQL("alter table " + DBOpenHelper.USER_TABLE_NAME + " add column common_json text");
		//Log.d(TAG, "插入新的一列");
		int row = db.update(DBOpenHelper.USER_TABLE_NAME, values, null, null);
		return row;
	}
}
