package com.tz.concordchurch.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WordLogDao extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "church_database";
	private static final String TABLE_NAME = "word_log";

	private static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
			+ " (id INTEGER PRIMARY KEY," + " link TEXT UNIQUE NULL, "
			+ " img TEXT NULL, " + " title TEXT NULL, "
			+ " content TEXT NULL, " + " speaker TEXT NULL, "
			+ " date TEXT NULL, " + " video TEXT NULL, " + " desc TEXT NULL) ";
	private static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;

	public static final int DATABASE_VERSION = 1;

	public WordLogDao(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_TABLE);
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

	private void update(ContentValues contentValues) {
		SQLiteDatabase db = getWritableDatabase();
		String selection = " id = ?";
		String[] selectionArgs = { String.valueOf(contentValues
				.getAsString("id")) };
		db.update(TABLE_NAME, contentValues, selection, selectionArgs);
		// db.close();
	}

	private void insert(ContentValues contentValues) {
		SQLiteDatabase db = getWritableDatabase();
		db.insert(TABLE_NAME, null, contentValues);
		// db.close();
	}

	public void write(ContentValues contentValues) {
		if (contentValues.containsKey("id")) {
			if (checkIfExist(contentValues)) {
				update(contentValues);
			} else {
				insert(contentValues);
			}
		} else {
			throw new RuntimeException("Must have column Id");
		}
	}

	private boolean checkIfExist(ContentValues contentValues) {
		SQLiteDatabase sqldb = getReadableDatabase();
		Cursor cursor = sqldb.query(TABLE_NAME, new String[] { "id", "link" },
				"id = ?", new String[] { contentValues.getAsString("id") },
				null, null, null);
		boolean alreadyExist;
		if (cursor.getCount() > 0) {
			alreadyExist = true;
		} else {
			alreadyExist = false;
		}
		cursor.close();
		// sqldb.close();
		return alreadyExist;
	}

}