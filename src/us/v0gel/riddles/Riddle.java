package us.v0gel.riddles;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class Riddle {
	private Long id;
	private String query;
	private String response;

	Riddle(String query, String response) {
		this.query = query;
		this.response = response;
	}
	
	Riddle(long id, String query, String response) {
		this.id = id;
		this.query = query;
		this.response = response;
	}
	
	public void setId(long id) {
		if(this.id == null && id > 0)
			this.id = id;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	public String getQuery() {
		return query;
	}
	
	public void setResponse(String response) {
		this.response = response;
	}
	
	public String getResponse() {
		return response;
	}
	
	public String toString() {
		return query;
	}
	
	public void saveTo(SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.RIDDLE_QUERY_COLUMN, query);
		values.put(DatabaseHelper.RIDDLE_RESPONSE_COLUMN, response);
		if(id == null) {
			setId(db.insert(DatabaseHelper.RIDDLE_TABLE, null, values));
		} else {
			db.update(DatabaseHelper.RIDDLE_TABLE, values, DatabaseHelper.RIDDLE_ID_COLUMN + " = ?", new String[]{String.valueOf(id)});
		}
	}
	
	public void deleteFrom(SQLiteDatabase db) {
		db.delete(DatabaseHelper.RIDDLE_TABLE, DatabaseHelper.RIDDLE_ID_COLUMN + " = ?", new String[]{String.valueOf(id)});
	}
}