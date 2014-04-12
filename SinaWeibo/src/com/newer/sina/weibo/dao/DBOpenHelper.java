package com.newer.sina.weibo.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "myweibo.db";
	public static final int VERSION = 1;
	public static final String USER_TABLE_NAME = "user";
	public static final String ATTENTION_TABLE_NAME = "attention";

	// 创建用户表，保存用户信息的json数据，使用需取出json数据再解析
	private static final String CREATE_USER_TABLE = "create table "
			+ USER_TABLE_NAME
			+ "(_id integer primary key autoincrement,json text,attention_json text,fans_json text,weibo_json text,favorites_json text,common_json text)";

	private static final String DROP_USER_TABLE = "drop table if exists"
			+ USER_TABLE_NAME;

	public DBOpenHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_USER_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL(DROP_USER_TABLE);

		onCreate(db);
	}

}
