package us.v0gel.riddles;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper
extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = "riddles_db";
	private static final int DATABASE_VERSION = 1;
	public static final String RIDDLE_TABLE = "riddles";
	public static final String RIDDLE_ID_COLUMN = "id";
	public static final String RIDDLE_QUERY_COLUMN = "query";
	public static final String RIDDLE_RESPONSE_COLUMN = "response";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + RIDDLE_TABLE + " (" + RIDDLE_ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " + RIDDLE_QUERY_COLUMN + " TEXT, " + RIDDLE_RESPONSE_COLUMN + " TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + RIDDLE_TABLE);
		onCreate(db);
	}
}