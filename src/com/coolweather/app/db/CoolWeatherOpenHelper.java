package com.coolweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

//此类是为了对数据库进行创建和升级
public class CoolWeatherOpenHelper extends SQLiteOpenHelper{
	//构造方法  name:数据库  version:版本号
	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	//建表语句  table Province
	public static final String CREATE_PROVINCE = "create table Province(id integer primary key autoincrement,"+
			"province_name text,"+"province_code text)";
	
	//建表语句 table City
	public static final String CREATE_CITY = "create table City(id integer primary key autoincrement,"+
	"city_name text,"+"city_code text,"+"province_id integer)";
	
	//建表语句 table County
	public static final String CREATE_COUNTY = "create table County(id integer primary key autoincrement,"+
	"county_name text,"+"county_code text,"+"city_id integer)";
	
	//在这里进行创建数据库的工作
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_PROVINCE);
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_COUNTY);
	}
	
	//需进行数据库升级工作时回调这个方法
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
