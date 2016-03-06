package com.example.administrator.mywheather.util;

import android.text.TextUtils;

import com.example.administrator.mywheather.db.MyWeatherDB;
import com.example.administrator.mywheather.model.City;
import com.example.administrator.mywheather.model.County;
import com.example.administrator.mywheather.model.Province;



/**
 * Created by Administrator on 2016/3/6.
 */
public class Utility {
    //解析和处理服务器返回的省级数据
    public synchronized static boolean handleProvincesResponse(MyWeatherDB myWeatherDB,String response){
        if (!TextUtils.isEmpty(response)){//判断字符串是否为空，为空也不会报错，而String.isEmpty会报错
            String[] allProvinces = response.split(",");//split(),将字符串分割为子字符串，然后将结果作为字符串数组返回
            if (allProvinces != null&& allProvinces.length>0){
                for (String p : allProvinces){
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //将解析出来的数据存储到Province表
                    myWeatherDB.saveProvince(province);
                }
                return true;// 为什么需要呢
            }
        }
        return false;
    }

    //解析和处理服务器返回的市级数据
    public static boolean handleCityResponse(MyWeatherDB myWeatherDB,String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length>0){
                for (String c:allCities){
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    //将解析出来的数据存储到City表
                    myWeatherDB.saveCity(city);
                }

                return true;   // 两个return？？？？？？，为什么解析的结果会是一个布尔类型呢
            }
        }
        return false;
    }

    //解析服务器返回的县级数据
    public static boolean handleCountyResponse(MyWeatherDB myWeatherDB,String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            String[] allCounty = response.split(",");
            if (allCounty != null && allCounty.length>0){
                for (String c:allCounty){
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    //将解析出来的数据存储到County表
                    myWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
     return false;
    }
}
