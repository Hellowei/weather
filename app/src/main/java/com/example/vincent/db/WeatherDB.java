package com.example.vincent.db;

import java.util.ArrayList;
import java.util.List;

import com.example.vincent.model.City;
import com.example.vincent.model.County;
import com.example.vincent.model.Province;
import com.example.vincent.model.Weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WeatherDB {
	String TAG  = "weatherActivity";
	/**
	 * 数据库名
	 */
	public static final String DB_NAME = "dbname_weather";

	/**
	 * 数据库版本
	 */
	public static final int VERSION = 1;

	private static WeatherDB WeatherDB;

	private SQLiteDatabase db;

	/**
	 * 将构造方法私有化
	 */
	private WeatherDB(Context context) {
		WeatherOpenHelper dbHelper = new WeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	/**
	 * 获取CoolWeatherDB的实例。
	 */
	public synchronized static WeatherDB getInstance(Context context) {
		if (WeatherDB == null) {
			WeatherDB = new WeatherDB(context);
		}
		return WeatherDB;
	}



	public void saveWeather(Weather weather,boolean hasDayData) {
		if (weather != null) {
			int countryCode = weather.getCountryCode();
			int weatherDate = weather.getWeatherDate();
			ContentValues values = new ContentValues();
			values.put("country_code",countryCode );
			values.put("country_name", weather.getCountryName());
		 	values.put("weather_date", weatherDate);
			Log.d(TAG, "updatetime " + weather.getUpdateTime());
		 	values.put("update_time", weather.getUpdateTime());
			Log.d(TAG, "updatetime ");
			if(hasDayData){
				values.put("day_weather", weather.getFa());
				values.put("day_temp", weather.getFc());
				values.put("day_direction", weather.getFe());
				values.put("day_wind_power", weather.getFg());
			}
			values.put("night_weather", weather.getFb());
			values.put("night_temp", weather.getFd());
			values.put("night_direction", weather.getFf());
			values.put("night_wind_power", weather.getFh());
			values.put("sun_time", weather.getFi());
			Log.d(TAG, "ddsff:" );
			Cursor cursor1 = db.query("Weather", null, null,
					null, null, null, null);

			String s[] =  cursor1.getColumnNames();
			for (int i = 0;i<s.length;i++)
				Log.d(TAG,i+ "namei:"+s[i]);
			Log.d(TAG, "dddff:");
			if (cursor1.moveToFirst()) {
				do {
					cursor1.getColumnNames();
					String A = cursor1.getString(cursor1.getColumnIndex("country_name"));
					int b = cursor1.getInt(cursor1.getColumnIndex("country_code"));
					int c = cursor1.getInt(cursor1.getColumnIndex("weather_date"));
					Log.d(TAG, cursor1.getCount()+"ddff:" + A + b);
				} while (cursor1.moveToNext());
			}
			cursor1.close();
			String SQL = "select * from Weather where country_code=?";
			Log.d(TAG, "LLLL  xquery" + SQL + weatherDate);
			Cursor cursor = db.rawQuery(SQL, new String[]{ String.valueOf(countryCode)});

			Log.d(TAG, "LLLL wil query oredd,cursor.getcount=" + cursor.getCount());
			if(false && cursor.getCount() == 1){
				cursor.close();
				Log.d(TAG,"UPDATE DB");
				db.update("Weather", values, "country_code = ? and weather_date = ?",new String[]{ String.valueOf(countryCode), String.valueOf(weatherDate)});
			}else {
				cursor.close();
				Log.d(TAG, "INSERT DB");
				db.insert("Weather", null, values);
			}
			Log.d(TAG, "LLLLre");
		}
	}


	public List<Weather> loadWeather(int countryCode) {
		List<Weather> list = new ArrayList<Weather>();
		//Cursor cursor = db.query("Weather", null, null,
		//		null, null, null, null);
		Cursor cursor = db.query("Weather", null, "country_code = ?",
				new String[] { String.valueOf(countryCode) }, null, null, null);
		Log.d(TAG, cursor.getCount()+"loadWeather++");

		if (cursor.moveToFirst()) {
			do {
				Weather weather = new Weather();
				Log.d(TAG, "a");
				weather.setId(cursor.getInt(cursor.getColumnIndex("id")));
				Log.d(TAG, "a"+cursor.getString(cursor.getColumnIndex("country_name")));
				weather.setCountryCode(countryCode);
				Log.d(TAG, "a3");
				weather.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
				Log.d(TAG, "a");
				weather.setWeatherDate(cursor.getInt(cursor.getColumnIndex("weather_date")));
				Log.d(TAG, "a4");


				weather.setFa(cursor.getString(cursor.getColumnIndex("day_weather")));
				Log.d(TAG, "a5");
			 	weather.setFc(cursor.getInt(cursor.getColumnIndex("day_temp")));
				Log.d(TAG, "a6:" + cursor.getColumnIndex("night_temp"));
				weather.setFd(cursor.getInt(2));
				Log.d(TAG, "a");
			 	weather.setFe(cursor.getInt(cursor.getColumnIndex("day_direction")));
				weather.setFf(cursor.getInt(cursor.getColumnIndex("night_direction")));
				Log.d(TAG, "a7:"+cursor.getColumnIndex("night_weather"));
			 	weather.setFg(cursor.getInt(cursor.getColumnIndex("day_wind_power")));
				weather.setFb(cursor.getString(4));
				Log.d(TAG, "a");
				weather.setFh(cursor.getInt(cursor.getColumnIndex("night_wind_power")));
				Log.d(TAG, "a");
				weather.setFi(cursor.getString(cursor.getColumnIndex("sun_time")));
				weather.setUpdateTime(cursor.getInt(cursor.getColumnIndex("update_time")));
				Log.d(TAG, "a");

				list.add(weather);
				Log.d(TAG, "a9");
			} while (cursor.moveToNext());
		}
		cursor.close();
		Log.d(TAG, "loadWeather--");
		return list;
	}
	public void saveCity(City city) {
		if (city != null) {

			ContentValues values = new ContentValues();
			values.put("city_code",city.getCityCode() );
			values.put("city_name", city.getCityName());

			Cursor cursor1 = db.query("TABLE_CITY", null, null,
					null, null, null, null);
			Log.d(TAG, "dddff:");
			if (cursor1.moveToFirst()) {
				do {
					String A = cursor1.getString(cursor1.getColumnIndex("city_name"));
					int b = cursor1.getInt(cursor1.getColumnIndex("city_code"));
					Log.d(TAG, cursor1.getCount()+"ddff:" + A + b);
				} while (cursor1.moveToNext());
			}
			cursor1.close();
			String SQL = "select * from TABLE_CITY where city_code=?";
			Cursor cursor = db.rawQuery(SQL, new String[]{ String.valueOf(city.getCityCode())});
			/*String SQL = "select * from TABLE_CITY where country_code=? and city_date=?";
			Log.d(TAG,"LLLL wil xquery"+SQL);
			Cursor cursor = db.rawQuery(SQL, new String[]{countryCode+"",cityDate+""});*/
			Log.d(TAG, "LLLL wil query oredd,cursor.getcount=" + cursor.getCount());
			cursor.close();
			Log.d(TAG, "INSERT DB");
			db.insert("TABLE_CITY", null, values);

			Log.d(TAG, "LLLLre");
		}
	}


	public List<City> loadCity(String city_name) {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("TABLE_CITY", null, "city_name = ?",
				new String[] { city_name }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityCode(cursor.getColumnIndex("city_code"));
				city.setCityName(city_name);
				list.add(city);
			} while (cursor.moveToNext());
		}
		return list;
	}
}