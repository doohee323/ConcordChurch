package com.tz.concordchurch.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ResourceDao extends SQLiteOpenHelper {
	/** Keywords */
	public static final String NULL_STRING = "NULL";
	private static final String SQL_CREATE_TABLE_CONTACTS = "CREATE TABLE ";
	private static final String SQL_DELETE_TABLE_CONTACTS = "DROP TABLE IF EXISTS aaa";

	/** DB Name */
	private static final String DATABASE_NAME = "screenpop_database";
	public static final int DATABASE_VERSION = 1;

	public ResourceDao(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE_CONTACTS);

	}

	/* On upgrade, delete all tables and recreate them. */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_TABLE_CONTACTS);
		onCreate(db);

	}

	/* On downgrade, delete all tables and recreate them. */
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

	/* Update existing entry with columns in the input contentValues. */
	private void updateContactEntry(ContentValues contentValues) {
		SQLiteDatabase db = getWritableDatabase();
		String selection = " phone_id LIKE ?";
		String[] selectionArgs = { String.valueOf(contentValues
				.getAsString("phone_id")) };
		db.update("aaa", contentValues, selection, selectionArgs);
		// db.close();
	}

	/* Insert the input contentValues into database. */
	private void insertContactEntry(ContentValues contentValues) {
		SQLiteDatabase db = getWritableDatabase();
		db.insert("aaa", null, contentValues);
		// db.close();
	}

	public void insertOrUpdateContactEntry(ContentValues contentValues) {
		if (contentValues.containsKey("phone_id")) {
			if (checkIfExistInTableContactsAndAddDisplayName(contentValues)) {
				updateContactEntry(contentValues);
			} else {
				insertContactEntry(contentValues);
			}
		} else {
			throw new RuntimeException("Must have column phone Id");
		}
	}

	private boolean checkIfExistInTableContactsAndAddDisplayName(
			ContentValues contentValues) {
		SQLiteDatabase sqldb = getReadableDatabase();
		Cursor cursor = sqldb.query("aaa", new String[] { "a", "b", "c" },
				"phone_id = ?",
				new String[] { contentValues.getAsString("phone_id") }, null,
				null, null);
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