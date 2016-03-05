package com.example.administrator.mywheather.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.mywheather.model.City;
import com.example.administrator.mywheather.model.Province;

import java.security.Provider;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/4.
 */
public class MyWeatherDB {
    //数据库名
    public static final String DB_NAME = "My_Weather";
    //数据库版本
    public static final int VERSION = 1;  //static final 属于类的变量，且只能赋值一次
    private static MyWeatherDB myWeatherDB;
    private SQLiteDatabase db;        //管理和操作sqlite数据库

    //将构造方法私有化
    private MyWeatherDB(Context context){
        MyWeatherOpenHelper dbHelper = new MyWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db = dbHelper.getWritableDatabase();
    }

    //获取MyWeatherDB的实例
    public synchronized  static MyWeatherDB getInstance(Context context){//synchronized 同步，成员锁，一次只能有一个线程进入该方法，
                                                                        //其他线程若想调用此方法，只能排队等候
        if (myWeatherDB == null){
            myWeatherDB = new MyWeatherDB(context);       //啥用？？？？？？？？
        }
        return myWeatherDB;
    }

    //将Province实例存储到数据库
    public void saveProvince( Province province){
        if (province != null){
            ContentValues values = new ContentValues();//用来代替sql进行数据库的操作，键值对，只能存储基本类型的数据
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);

        }
    }

    //从数据库读取全国所有的省份信息。
    public List<Province> loadProvinces(){
        List<Province> list = new ArrayList<>();
        Cursor cursor = db.query("Province",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));//getColumnIndex("id")  //返回指定列的名字，不存在返回-1
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            }while (cursor.moveToNext());
        }
        if (cursor != null){  //记得关闭游标
            cursor.close();
        }

        return list;
    }

    //将City实例存储到数据库
    public void saveCity(City city){
        if (city != null){
            ContentValues values = new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvinceId());
            db.insert("City",null,values);
        }
    }
    //从数据库读取某省下所有城市的信息
    public List<City> loadCities(int provinceId){
        List<City> list = new ArrayList<>();

        Cursor cursor = db.query("City",null,"province_id = ?",new String[]{String.valueOf(provinceId) },null,null,null);
        if (cursor.moveToFirst()){
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                list.add(city);

            }while(cursor.moveToNext());
        }
        return list;
    }

}


