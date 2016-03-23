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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vincent.util.HttpCallbackListener;
import com.example.vincent.util.HttpUtil;
import com.example.vincent.util.Utility;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class weatherActivity extends AppCompatActivity {
    String TAG = "weatherActivity";
    private ListView listView;
    private String weatherMsg;
    private String weatherKey = "274eee62f6756e36ea8beafd986aa7b3";
    private String areaKey = "a1f1216eb5152943deef4d02c9084a94";
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<String>();
    private TextView weatherResult;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    Log.d(TAG, "revdde msg" + msg.what);
                    weatherResult.setText("dddd");
                    Log.d(TAG, "r:" + getWeather(0)+ getWeather(1)+ getWeather(5)+ getWeather(10));
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
                getCityWeather("2235", weatherKey);
            }
        });


    }

    /**
     * 查询县级代号所对应的天气代号。
     */
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address, "countyCode");
        Log.d(TAG, "dddd86" + countyCode);
    }
    private String getWeather(int weatherCode) {
        int key = R.string.weather_00 + weatherCode ;
        if(weatherCode == 53)
            key = R.string.weather_53 ;
        else if(weatherCode == 99)
            key = R.string.weather_99 ;
        return  getString(key);

    }

    /**
     * 查询天气代号所对应的天气。
     */
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(address, "weatherCode");
    }

    /**
     * 根据传入的地址和类型去向服务器查询天气代号或者天气信息。
     */
    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.d(TAG, "d66 " + response);
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        // 从服务器返回的数据中解析出天气代号
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                            Log.d(TAG, "dddd" + response);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    // 处理服务器返回的天气信息
                    Log.d(TAG, "ddf" + response);
                    Utility.handleWeatherResponse(weatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
            }
        });
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
    private String getSMT_API_URL(String areaid,String date) {
        String appid      = "ce74b07002e7adfe";
        String appid_6bit = "ce74b0";
        String URL="";
        return URL;
    }
    private void getCityWeather(String cityCode,String key) {
        String data = "http://open.weather.com.cn/data/?areaid=101280601&type=forecast_f&date=201603231453&appid=ce74b07002e7adfe";
        String data1 = "http://open.weather.com.cn/data/?areaid=101280601&type=forecast_f&date=201603231453&appid=ce74b0";
        //密钥
        String key1 = "f82321_SmartWeatherAPI_33d48bf";
        String str =  toURLString.standardURLEncoder(data, key1);
        Log.d(TAG, "dggg:" +data1+"&key="+str);
        queryFromCityServer(data1+"&key="+str, "city");
    }
    /**
     * 根据传入的地址和类型去向服务器查询天气代号或者天气信息。
     */
    private void queryFromCityServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {

                if ("province".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        Log.d(TAG,response);
                    }
                } else  if ("city".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {

                        Log.d(TAG, "KKKKKKK" + response + "HHHHHH");
                       //
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message ms = new Message();
                                ms.what = 1;
                                Log.d(TAG,"send mes vill"+ms.what);
                                handler.sendMessage(ms);
                                Log.d(TAG, "send over" + ms.what);
                            }
                        }).start();
                    }
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