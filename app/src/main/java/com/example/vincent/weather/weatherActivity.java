package com.example.vincent.weather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.example.vincent.util.HttpCallbackListener;
import com.example.vincent.util.HttpUtil;
import com.example.vincent.util.Utility;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class weatherActivity extends AppCompatActivity {
    String TAG = "weatherActivity";
    private ListView listView;
    private String key = "f82321_SmartWeatherAPI_33d48bf";
    private String appid  = "ce74b07002e7adfe";
    private String weatherMsg;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<String>();
    private TextView weatherResult;
    private String updateWeatherInfo;
    public static final int UPDATE_WEATHER = 1;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_WEATHER:
                    Log.d(TAG,"GETMSG:"+updateWeatherInfo);
                    weatherResult.setText(updateWeatherInfo);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        listView = (ListView) findViewById(R.id.list_view);
        weatherResult = (TextView) findViewById(R.id.weather_result);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, dataList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        dataList.add("--");
        dataList.add("++");
        listView.setSelection(1);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                getCityWeather("2235",key,appid);
            }
        });
    }

    private String getWeatherByCode(int weatherCode) {
        int key = R.string.weather_00 + weatherCode ;
        if(weatherCode == 53)
            key = R.string.weather_53 ;
        else if(weatherCode == 99)
            key = R.string.weather_99 ;
        return  getString(key);

    }
    /**
     * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
     */
    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String result = prefs.getString("city_name", "") + prefs.getString("temp1", "") + prefs.getString("temp2", "")
                 + prefs.getString("weather_desp", "") +  prefs.getString("publish_time", "");
        weatherResult.setText(result);
     //   Intent intent = new Intent(this, com.coolweather.app.service.AutoUpdateService.class);
     //   startService(intent);
    }
    private String getBeiJingTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String BeiJingTime = formatter.format(curDate);
        return  BeiJingTime;
    }
    private void getCityWeather(String areaid,String key, String appid) {//
        areaid =  "101280601";
        String format1 = "http://open.weather.com.cn/data/?areaid=%s&type=forecast_f&date=%s&appid=%s";
        String format2 = "http://open.weather.com.cn/data/?areaid=%S&type=forecast_f&date=%s&appid=%s&key=%s";
        String date = getBeiJingTime();
        String data = String.format(format1, areaid, date, appid);
        Log.d(TAG,"data "+data);
        String str =  toURLString.standardURLEncoder(data, key);
        String url = String.format(format2, areaid, date, appid.substring(0,6),str);
        Log.d(TAG,"url "+url);
        queryFromCityServer(url);
    }
    private String getWeatherByJson(String weatherJson) {
        int  updateTime =  0;
        try {
            JSONObject jsonObject = new JSONObject(weatherJson);
            JSONObject weatherInfo = jsonObject.getJSONObject("f");
            updateTime = weatherInfo.getInt("f0")%10000;
            JSONArray jsonArray = new JSONArray(weatherInfo.getString("f1"));
       //     for(int i = 0; i < jsonArray.length();i++){
            int i = 0;
            JSONObject jsonObjectWeather =jsonArray.getJSONObject(i);
            String nightWeather = getWeatherByCode(jsonObjectWeather.getInt("fb"));
            int nightTemp = jsonObjectWeather.getInt("fd");
            if(updateTime < 1800) {
                String dayWeather = getWeatherByCode(jsonObjectWeather.getInt("fa"));
                int dayTemp = jsonObjectWeather.getInt("fc");
                if(nightWeather.equals(nightWeather))
                    updateWeatherInfo = dayWeather  + "\n" + dayTemp  + "~"+ nightTemp +"℃";
                else
                    updateWeatherInfo = "day:"+ dayWeather + "day:"+ nightWeather  + "\n" + dayTemp  + "~" + nightTemp +"℃";
            }else
                updateWeatherInfo = nightWeather + "\n" + nightTemp +"℃";

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return updateWeatherInfo;
    }
    // 根据传入的地址和类型去向服务器查询天气代号或者天气信息。
    private void queryFromCityServer(final String address) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                if (!TextUtils.isEmpty(response)) {
                    getWeatherByJson(response);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message ms = new Message();
                            ms.what = UPDATE_WEATHER;
                            handler.sendMessage(ms);
                        }
                    }).start();
                }

            }
            @Override
            public void onError(Exception e) {
                Log.d(TAG,"KKKKKKerrorHHHHHH");
            }
        });
    }
}
/*

 */