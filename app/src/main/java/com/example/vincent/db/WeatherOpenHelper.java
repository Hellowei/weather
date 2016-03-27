package com.example.vincent.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WeatherOpenHelper extends SQLiteOpenHelper {
	/**
	 *  Weather表建表语句
	 */

	public static final String CREATE_WEATHER = "create table Weather ("
			+ "id integer primary key autoincrement, "
			+ "country_code text, "
			+ "country_name text, "
			+ "weather_date integer,"
			+ "update_time integer,"
			+ "day_weather text,"
			+ "night_weather text,"
			+ "day_temp integer,"
			+ "night_temp integer,"
			+ "day_direction integer,"
			+ "night_direction integer,"
			+ "day_wind_power integer,"
			+ "night_wind_power integer,"
			+ "sun_time text)";
	public static final String CREATE_CITY = "create table TABLE_CITY ("
			+ "id integer primary key autoincrement, "
			+ "city_name text, "
			+ "city_code integer)";

	public WeatherOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	String TAG  = "weatherActivity";
	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL(CREATE_CITY);  // 创建County表
		db.execSQL(CREATE_WEATHER);
		Log.d(TAG, "CREATE_WEATHER DB");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}