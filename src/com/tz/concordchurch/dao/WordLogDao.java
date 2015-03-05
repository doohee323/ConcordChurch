package com.tz.concordchurch.dao;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class WordLogDao {
	private static SQLiteDatabase db;
	private MySQLiteHelper dbHelper;

	public WordLogDao(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	public void drop() throws SQLException {
		new File("/data/data/com.tz.concordchurch/databases/church_database").exists();
		new File("/data/data/com.tz.concordchurch/databases/church_database").delete();
		//db.execSQL(MySQLiteHelper.SQL_DELETE_TABLE);
	}

	public void close() {
		dbHelper.close();
	}

	public void update(ContentValues contentValues) {
		String selection = " id = ?";
		String[] selectionArgs = { String.valueOf(contentValues
				.getAsString("id")) };
		db.update(MySQLiteHelper.TABLE_NAME, contentValues, selection,
				selectionArgs);
//		db.close();
	}

	public void insert(ContentValues contentValues) {
		if (db == null) {
			open();
		}
		db.insert(MySQLiteHelper.TABLE_NAME, null, contentValues);
//		db.close();
	}

	public void write(ContentValues contentValues) {
		if (contentValues.containsKey("id")) {
			if (checkIfExist(contentValues)) {
				update(contentValues);
			} else {
				insert(contentValues);
			}
		} else {
			insert(contentValues);
		}
	}

	public boolean checkIfExist(ContentValues contentValues) {
		if (db == null) {
			open();
		}
		Cursor cursor = db.query(MySQLiteHelper.TABLE_NAME, new String[] {
				"id", "link" }, "id = ?",
				new String[] { contentValues.getAsString("id") }, null, null,
				null);
		boolean alreadyExist;
		if (cursor.getCount() > 0) {
			alreadyExist = true;
		} else {
			alreadyExist = false;
		}
		cursor.close();
//		db.close();
		return alreadyExist;
	}

}