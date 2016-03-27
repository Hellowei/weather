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

import com.example.vincent.db.WeatherDB;
import com.example.vincent.model.City;
import com.example.vincent.model.Weather;
import com.example.vincent.util.HttpCallbackListener;
import com.example.vincent.util.HttpUtil;
import com.example.vincent.util.Utility;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class weatherActivity extends AppCompatActivity {
    String TAG = "weatherActivity";
    private ListView listView;
    private WeatherDB weatherDB;
    private static  int c = 1;
    private String key = "f82321_SmartWeatherAPI_33d48bf";
    private String appid  = "ce74b07002e7adfe";
    private String weatherMsg;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<String>();
    private TextView weatherResult;
    private String updateWeatherInfo;
    public static final int UPDATE_WEATHER = 1;
    public static final int INVALID_TEMP = 100;
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
        weatherDB = WeatherDB.getInstance(this);
        listView.setSelection(1);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Weather w = new Weather();
                w.setCountryCode(c);
                w.setCountryName("shenzhen");
                c= c+1;
                //  weatherDB.saveWeather(w,false);
                getNewestWeather(101280601);
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
    private String getWindDirectionByCode(int windDirectionCode) {
        int key = R.string.windDirection_00 + windDirectionCode ;
        if(key < R.string.windDirection_00 || key >  R.string.windDirection_09)
            key = R.string.weather_99 ;
        return  getString(key);
    }
    private String getWindPoerByCode(int windPower) {
        int key = R.string.windPower_00 + windPower ;
        if(key < R.string.windPower_00 || key >  R.string.windPower_09)
            key = R.string.weather_99 ;
        return  getString(key);
    }
    private void getNewestWeather(int countryCode)
    {
        int curDate = getCurDate();
        int curHHMM = Integer.parseInt(getBeiJingTime().substring(8));
        int publishTime[] = {800,1100,1800};
        List<Weather> list = weatherDB.loadWeather(countryCode);
        boolean isNewest = false;
        Log.d(TAG, "aabbsize=" + list.size());
      /*  for(int i = 0; i < list.size();i++) {
            Weather weather = list.get(i);
            if (weather.getWeatherDate() == curDate) {
                int lastUpdateTimeHHMM = weather.getUpdateTime() % 10000;
                if (lastUpdateTimeHHMM == publishTime[0] && publishTime[1] <= curHHMM) {
                    isNewest = true;
                }
                if (lastUpdateTimeHHMM == publishTime[1] && publishTime[2] <= curHHMM) {
                    isNewest = true;
                }
                if (lastUpdateTimeHHMM == publishTime[3] && publishTime[0] <= curHHMM && publishTime[3] > curHHMM) {
                    isNewest = true;
                }
                if (isNewest) {
                    getWeatherByWeather(weather);
                    return;
                }
            }
        }*/
        if (!isNewest) {
            getCityWeather( countryCode, key,  appid);
        }
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
    private int getCurDate() {
        String BeiJingTime = getBeiJingTime();
        int YYYYMMDD = Integer.parseInt(BeiJingTime.substring(0, 8));
        return  YYYYMMDD;
    }
    private String getBeiJingTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String BeiJingTime = formatter.format(curDate);
        return  BeiJingTime;
    }
    private void getCityWeather(int areaid,String key, String appid) {//
        String format1 = "http://open.weather.com.cn/data/?areaid=%d&type=forecast_f&date=%s&appid=%s";
        String format2 = "http://open.weather.com.cn/data/?areaid=%d&type=forecast_f&date=%s&appid=%s&key=%s";
        String date = getBeiJingTime();
        String data = String.format(format1, areaid, date, appid);
        String str =  toURLString.standardURLEncoder(data, key);
        String url = String.format(format2, areaid, date, appid.substring(0,6),str);
        Log.d(TAG,"url "+url);
        queryFromCityServer(url);
    }
    private String getWeatherByWeather(Weather weather) {
        String countryName = weather.getCountryName();
        String dayInfo = "";
        if(weather.getFc() != INVALID_TEMP) {
            String dayWeather = getWeatherByCode(Integer.parseInt(weather.getFa()));
            int dayTemp = weather.getFc();
            String dayWindDirection = getWindDirectionByCode(weather.getFe());
            String dayWindPower = getWindPoerByCode(weather.getFg());
            String sunUp = getString(R.string.sun_up_time) + weather.getFi().substring(0,4);
            dayInfo = getString(R.string.day) + dayWeather + dayTemp + getString(R.string.tempUnit) + dayWindPower;
        }
        String nightWeather = getWeatherByCode(Integer.parseInt(weather.getFb()));
        int nightTemp = weather.getFd();
        String nightWindDirection = getWindDirectionByCode(weather.getFf());
        String nightWindPower = getWindPoerByCode(weather.getFh());
        String sunUp = getString(R.string.sun_down_time) + weather.getFi().substring(5,9);
        String nightInfo = getString(R.string.night) + nightWeather + nightTemp + getString(R.string.tempUnit) + nightWindPower;
        updateWeatherInfo =dayInfo +"\n"+ nightInfo;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message ms = new Message();
                ms.what = UPDATE_WEATHER;
                handler.sendMessage(ms);
            }
        }).start();
       return updateWeatherInfo;
    }
    //发送网络请求后收到的天气预报JSON
    private String getWeatherByJson(String weatherJson) {
        int  updateTime =  0;
        Log.d(TAG,weatherJson);
        try {
            JSONObject jsonObject = new JSONObject(weatherJson);
            JSONObject weatherInfo = jsonObject.getJSONObject("f");
            updateTime = Integer.parseInt(weatherInfo.getString("f0").substring(8,12));
            JSONArray jsonArray = new JSONArray(weatherInfo.getString("f1"));
            JSONObject areaInfo = jsonObject.getJSONObject("c");
            for(int i = 0; i < jsonArray.length()-2;i++) {
                Log.d(TAG,"updateTime"+updateTime);
                Weather weather = new Weather();
                weather.setCountryCode(areaInfo.getInt("c1"));
                weather.setCountryName(areaInfo.getString("c2"));
                weather.setUpdateTime(updateTime);
                JSONObject jsonObjectWeather = jsonArray.getJSONObject(i);
                weather.setFb(jsonObjectWeather.getString("fb"));
                weather.setFd(jsonObjectWeather.getInt("fd"));
                weather.setFf(jsonObjectWeather.getInt("ff"));
                weather.setFh(jsonObjectWeather.getInt("fh"));
                weather.setFi(jsonObjectWeather.getString("fi"));
                boolean hasDayData = true;
                if (i != 0 || updateTime < 1800) {//有白天数据
                    weather.setFa(jsonObjectWeather.getString("fa"));
                    weather.setFc(jsonObjectWeather.getInt("fc"));
                    weather.setFe(jsonObjectWeather.getInt("fe"));
                    weather.setFf(jsonObjectWeather.getInt("ff"));
                } else{
                    weather.setFc(INVALID_TEMP);
                    hasDayData = false;
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.DAY_OF_MONTH, i);
                Date date = cal.getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                int weatherDate  = Integer.parseInt(formatter.format(date));
                weather.setWeatherDate(weatherDate);
                Log.d(TAG,"LLLLmm"+weatherDate);
                weatherDB.saveWeather(weather, hasDayData);
                Log.d(TAG, "LLLLnn" + i);
                if(i == 0)//当天的数据
                {
                    getWeatherByWeather(weather);
                }
                Log.d(TAG,"LLLL"+i);
            }
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