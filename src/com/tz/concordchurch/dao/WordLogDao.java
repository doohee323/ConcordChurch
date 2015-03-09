package com.tz.concordchurch.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorJoiner.Result;
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
		new File("/data/data/com.tz.concordchurch/databases/church_database")
				.exists();
		new File("/data/data/com.tz.concordchurch/databases/church_database")
				.delete();
		// db.execSQL(MySQLiteHelper.SQL_DELETE_TABLE);
	}

	public void close() {
		dbHelper.close();
	}

	public void update(ContentValues contentValues) {
		try {
//			db = dbHelper.getWritableDatabase();
			String selection = " id = ?";
			String[] selectionArgs = { String.valueOf(contentValues
					.getAsString("id")) };
			db.update(MySQLiteHelper.TABLE_WORD, contentValues, selection,
					selectionArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			db.close();
		}
	}

	public void insert(ContentValues contentValues) {
		try {
//			db = dbHelper.getWritableDatabase();
			db.insert(MySQLiteHelper.TABLE_WORD, null, contentValues);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			db.close();
		}
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
		boolean alreadyExist = false;
		try {
//			db = dbHelper.getWritableDatabase();
			Cursor cursor = db.query(MySQLiteHelper.TABLE_WORD, new String[] {
					"id", "link" }, "id = ?",
					new String[] { contentValues.getAsString("id") }, null,
					null, null);
			if (cursor.getCount() > 0) {
				alreadyExist = true;
			} else {
				alreadyExist = false;
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			db.close();
		}
		return alreadyExist;
	}

	public List<JSONObject> getLogsByReadAt(String params) {
		List<JSONObject> items = new ArrayList<JSONObject>();
//		db = dbHelper.getWritableDatabase();
		Cursor cursor = null;
		try {
			JSONObject input = new JSONObject(params);
			cursor = db
					.rawQuery(
							"SELECT _id, link, content, title, desc, speaker, bible, img, date, video, read_at FROM "
									+ MySQLiteHelper.TABLE_WORD
									+ " WHERE read_at = ?",
							new String[] { input.getString("read_at") });
			// Check if the row exists, return it if it does
			// if(mycursor.moveToFirst())
			// return
			// mycursor.getString(mycursor.getColumnIndex(COLUMN_CATEGORIES));

			// No rows match id
			while (cursor.moveToNext()) {
				JSONObject result = new JSONObject();
				result.put("_id", cursor.getString(0));
				result.put("link", cursor.getString(1));
				result.put("content", cursor.getString(2));
				result.put("title", cursor.getString(3));
				result.put("desc", cursor.getString(4));
				result.put("speaker", cursor.getString(5));
				result.put("bible", cursor.getString(6));
				result.put("img", cursor.getString(7));
				result.put("date", cursor.getString(8));
				result.put("video", cursor.getString(9));
				result.put("read_at", cursor.getString(10));
				items.add(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
//			db.close();
		}
		return items;
	}

}