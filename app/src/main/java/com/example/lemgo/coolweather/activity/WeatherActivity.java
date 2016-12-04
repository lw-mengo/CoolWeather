package com.example.lemgo.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lemgo.coolweather.R;
import com.example.lemgo.coolweather.util.HttpCallbackListener;
import com.example.lemgo.coolweather.util.HttpUtil;
import com.example.lemgo.coolweather.util.Utility;

/**
 * Created by Administrator on 2016/12/3 0003.
 */

public class WeatherActivity extends Activity implements View.OnClickListener {
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDateText;
    private Button switchCity;
    private Button refreshWeather;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh);

        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);



        String countyCode = getIntent().getStringExtra("county_code");
        if(!TextUtils.isEmpty(countyCode)){
            publishText.setText("tongbuzhong....");
            weatherInfoLayout.setVisibility(View.VISIBLE);
            cityNameText.setVisibility(View.VISIBLE);
            queryWeatherCode(countyCode);
        }else{
            showWeather();
        }
    }


    private void queryWeatherCode(String countyCode) {
        String address =  "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";

        queryFromServer(address,"countyCode");
    }
    private void queryWeatherInfo(String weatherCode) {
        String  address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";

        queryFromServer(address,"weatherCode");
    }
    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequset(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                if("countyCode".equals(type)){
                    if(!TextUtils.isEmpty(response)){
                        String[] array =response.split("\\|");
                        if(array!=null&&array.length==2){
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                }else if("weatherCode".equals(type)){
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("tongbu shibai!");
                    }
                });
            }
        });
    }
    private void showWeather() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(sharedPreferences.getString("city_name",""));
        temp1Text.setText(sharedPreferences.getString("temp1",""));
        temp2Text.setText(sharedPreferences.getString("temp2",""));
        weatherDespText.setText(sharedPreferences.getString("weather_desp",""));
        publishText.setText("今天"+sharedPreferences.getString("publish_time","")+"发布");
        currentDateText.setText(sharedPreferences.getString("current_date",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case  R.id.switch_city:
                Intent intent = new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh:
                publishText.setText("tongbu zhong....");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = preferences.getString("weather_code","");
                if(!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }


    }
}
