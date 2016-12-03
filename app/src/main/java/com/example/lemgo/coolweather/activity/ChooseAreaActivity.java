package com.example.lemgo.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lemgo.coolweather.R;
import com.example.lemgo.coolweather.db.CoolWeatherDb;
import com.example.lemgo.coolweather.model.City;
import com.example.lemgo.coolweather.model.County;
import com.example.lemgo.coolweather.model.Province;
import com.example.lemgo.coolweather.util.HttpCallbackListener;
import com.example.lemgo.coolweather.util.HttpUtil;
import com.example.lemgo.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/3 0003.
 */

public class ChooseAreaActivity extends Activity {
    public static  final int LEVEL_PROVINCE = 0;
    public static  final int LEVEL_CITY = 1;
    public static  final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView textView;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDb coolWeatherDb;
    private List<String> dataList  =new ArrayList<String>();

    private List<Province> provinceList;

    private List<City> cityList;

    private  List<County> countyList;

    private Province selectedProvice;

    private City selectedCity;

    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView  = (ListView) findViewById(R.id.list_view);
        textView = (TextView) findViewById(R.id.title_text);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        coolWeatherDb = CoolWeatherDb.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvice=provinceList.get(i);
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity =cityList.get(i);
                    queryCounties();

                }
            }
        });
        queryProvinces();
    }

    private void queryProvinces() {
        provinceList=coolWeatherDb.loadProvinces();
        if(provinceList.size()>0){
            dataList.clear();
            for(Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText("china");
            currentLevel = LEVEL_PROVINCE;
        }else{
            queryFromServer(null,"province");
        }
    }
    private void queryCities() {
        cityList=coolWeatherDb.loadCities(selectedProvice.getId());
        if(cityList.size()>0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(selectedProvice.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else{
            queryFromServer(selectedProvice.getProvinceCode(),"city");
        }
    }
    private void queryCounties() {
        countyList=coolWeatherDb.loadCounties(selectedCity.getId());
        if(countyList.size()>0){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else{
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }

    private void queryFromServer(final String code, final String type) {
        String address;
        if(!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else{
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequset(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if("province".equals(type)){
                    result  = Utility.handleProvinceResponse(coolWeatherDb,response);
                }else if("city".equals(type)){
                    result =  Utility.handleCitiesResponse(coolWeatherDb,response,selectedProvice.getId());
                }else if("county".equals(type)){
                    result = Utility.handleCotuntiesResponse(coolWeatherDb,response,selectedCity.getId());
                }
                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }

            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"jiazaishibai",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    private void showProgressDialog() {
        /*
        xianshi duihua kaung
         */
        if(progressDialog == null ){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("zhengzaijiazai.....");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if(currentLevel==LEVEL_COUNTY){
            queryCities();
        }else if(currentLevel==LEVEL_CITY){
            queryProvinces();
        }else{
            finish();
        }
    }
}

