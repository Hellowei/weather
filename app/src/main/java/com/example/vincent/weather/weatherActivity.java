package com.example.vincent.weather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private String weatherKey = "274eee62f6756e36ea8beafd986aa7b3";
    private String areaKey = "a1f1216eb5152943deef4d02c9084a94";
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<String>();
    private TextView weatherResult;

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
//        queryWeatherCode("190404");
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                getCityWeather("2235",weatherKey);
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
    /**
     * 查询省。
     */
    private void queryProvince(int areaID,String key) {

        String address = "http://AreaData.api.juhe.cn/AreaHandler.ashx?action=getArea&areaID="+areaID +"&key=" + key;
        Log.d(TAG, address );
        queryFromCityServer(address, "province");
        Log.d(TAG, "dggguu6" );
    }
    private void queryCity(String cityCode) {
        String address = "http://www.weather.com.cn/data/list3/city"+cityCode+".xml";
        queryFromCityServer(address, "city");
    }
    private void getCityWeather(String cityCode,String key) {
        String address = "http://v.juhe.cn/weather/index?format=2&cityname="+cityCode+"&key="+key;
        queryFromCityServer(address, "city");
    }
    /**
     * 根据传入的地址和类型去向服务器查询天气代号或者天气信息。
     */
    private void queryFromCityServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.d(TAG, "dggg6" +response);
                if ("province".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        Log.d(TAG,response);
                    }
                } else  if ("city".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        Log.d(TAG,response);
                    }
                }
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }
}
/*

 */