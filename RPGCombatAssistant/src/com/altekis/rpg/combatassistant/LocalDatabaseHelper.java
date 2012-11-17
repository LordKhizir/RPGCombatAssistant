package com.altekis.rpg.combatassistant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Database helper, as singleton, to avoid leaving database opened & leaking
 * @see http://www.androiddesignpatterns.com/2012/05/correctly-managing-your-sqlite-database.html
 *
 */
public class LocalDatabaseHelper extends SQLiteOpenHelper {
	// CONFIG
	private static final String DATABASE_NAME = "rpg-combat-assistant-v5.db";
	private static final int DATABASE_VERSION = 1;

	// Singleton instance
	private static LocalDatabaseHelper mInstance = null;
	private static SQLiteDatabase db;

	// DDL for database creation
	private static String[] DATABASE_CREATE = {
		"CREATE TABLE characters (\n" +
				"_id INTEGER PRIMARY KEY,\n" +
				"name TEXT NOT NULL,\n" +
				"playerName TEXT);",
				"CREATE TABLE attacks (\n" +
						"_id INTEGER PRIMARY KEY autoincrement,\n" +
						"characterId INTEGER NOT NULL,\n" +
						"attackType TEXT NOT NULL,\n" +
						"name TEXT NOT NULL);",
						//		"CREATE TABLE answers (\n" +
						//				"_id INTEGER PRIMARY KEY,\n" +
						//				"question INTEGER NOT NULL,\n" +
						//				"ordinal INTEGER NOT NULL,\n" +
						//				"text TEXT NOT NULL);",
						//		"CREATE TABLE users (\n" +
						//				"_id INTEGER PRIMARY KEY autoincrement,\n" +
						//				"fbId INTEGER,\n" +
						//				"email TEXT NOT NULL,\n" +
						//				"password TEXT NOT NULL,\n" +
						//				"nickname TEXT NOT NULL);",
	};

	/**
	 * Constructor should be private to prevent direct instantiation.
	 * make call to static factory method "getInstance()" instead.
	 */
	private LocalDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		db = getWritableDatabase();
	}

	public static void initialize(Context ctx) {
		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		// See this article for more information: http://bit.ly/6LRzfx
		if (mInstance == null) {
			mInstance = new LocalDatabaseHelper(ctx.getApplicationContext());
		}
	}

	public SQLiteDatabase getDB() {
		return db;
	}

	public static LocalDatabaseHelper getInstance() {
		if (mInstance == null) {
			Log.e("RPGCombatAssistant", "LocalDatabaseHelper getInstance() called before initialize()");
		}
		return mInstance;
	}
	
	@Override
	protected void finalize() throws Throwable {
		try {
			db.close();
			close();
		} finally {
			super.finalize();
		}
	}

	/**
	 * Called if required DB is missing
	 * Will execute the DDL required for the creation of the empty DB model
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		for (String query:DATABASE_CREATE) {
			db.execSQL(query);
		}
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#close()
	 */
	@Override
	public synchronized void close() {
		// TODO Auto-generated method stub
		db.close();
		super.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		// FIXME We will lost all data!
		Log.w(LocalDatabaseHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS characters");
		db.execSQL("DROP TABLE IF EXISTS attacks");
		//		db.execSQL("DROP TABLE IF EXISTS answers");
		//		db.execSQL("DROP TABLE IF EXISTS users");
		onCreate(db);
	}
}
