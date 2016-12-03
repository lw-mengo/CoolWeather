package com.example.lemgo.coolweather.util;

import android.text.TextUtils;

import com.example.lemgo.coolweather.db.CoolWeatherDb;
import com.example.lemgo.coolweather.model.City;
import com.example.lemgo.coolweather.model.County;
import com.example.lemgo.coolweather.model.Province;

/**
 * Created by Administrator on 2016/12/3 0003.
 */

public class Utility {
    /*
    解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvinceResponse(CoolWeatherDb coolWeatherDb,String response){
        if(!TextUtils.isEmpty(response)){
            String[] allProvinces  = response.split(",");
            if(allProvinces !=null && allProvinces.length>0){
                for (String p:allProvinces){
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //解析出来的数据存储到Province表中
                    coolWeatherDb.saveProvince(province);
                }
                return  true;
            }
        }
        return  false;
    }


    public  static boolean handleCitiesResponse(CoolWeatherDb coolWeatherDb,String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            String[] allCities  = response.split(",");
            if(allCities !=null && allCities.length>0){
                for (String c:allCities){
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    //解析出来的数据存储到City表中
                    coolWeatherDb.saveCity(city);
                }
                return  true;
            }
        }
        return  false;
    }

    public  static boolean handleCotuntiesResponse(CoolWeatherDb coolWeatherDb,String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            String[] allCounties  = response.split(",");
            if(allCounties !=null && allCounties.length>0){
                for (String c:allCounties){
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    //解析出来的数据存储到County表中
                    coolWeatherDb.saveCounty(county);
                }
                return  true;
            }
        }
        return  false;
    }

}
