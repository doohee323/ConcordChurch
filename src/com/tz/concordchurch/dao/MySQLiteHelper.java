package com.tz.concordchurch.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {
	SQLiteDatabase db;
	
	private static final String DATABASE_NAME = "church_database.db";
	public static final String TABLE_WORD = "word_log";

	private static final String SQL_CREATE_TABLE_WORD = "CREATE TABLE " + TABLE_WORD
			+ " (" 
			+ " _id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ " link TEXT NOT NULL, " 
			+ " content TEXT NULL, " 
			+ " title TEXT NOT NULL, "
			+ " desc TEXT NULL, "
			+ " speaker TEXT NULL, "
			+ " bible TEXT NULL, "
			+ " img TEXT NULL, " 
			+ " date TEXT NULL, "
			+ " video TEXT NULL, " 
			+ " read_at TEXT NULL "
			+ ") ";
	public static final String SQL_DELETE_TABLE_WORD = "DROP TABLE IF EXISTS "
			+ TABLE_WORD;

	public static final int DATABASE_VERSION = 1;

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE_WORD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_TABLE_WORD);
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

}