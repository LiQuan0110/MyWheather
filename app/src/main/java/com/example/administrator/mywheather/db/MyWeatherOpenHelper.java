package com.example.administrator.mywheather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/3/4.
 */
public class MyWeatherOpenHelper extends SQLiteOpenHelper {
    //Province表建表语句
    public static final String CREATE_PROVINCE = "create table Province(" +
            "id integer primary key autoincrement," +
            "province_name text," +
            "province_code text)";

    //City表建表语句
    public static final String CREATE_CITY = "create table City(" +
            "id integer primary key autoincrement," +
            "city_name text," +
            "city_code text," +
            "province_id integer)";

    //County表建表语句
    public static final String CREATE_COUNTY = "create table County(" +
            "id integer primary key autoincrement," +
            "country_name text," +
            "country_code text," +
            "city_id integer)";
    // 父类构造器
    public MyWeatherOpenHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){//游标用来指向数据库的某一行
        super(context,name,factory,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {//当数据库首次创建时执行该方法，建表等初始化在该方法中执行
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//当打开数据库传入的版本号与当期的版本号不一致时调用

    }
}
