package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

//此类把一些常用的数据库操作封装起来，方便与我们的后续工作
public class CoolWeatherDB {
	//数据库的名称
	public static final String DB_NAME = "cool_weather";
	//数据库的版本号
	public static final int VERSION = 1;

	private static CoolWeatherDB coolWeatherDB;
	/**
	 * 数据库的实例 (通过CoolWeatherOpenHelper 的 getReadableDatabase()/getWritableDatabase()可以
	 * 获得SQLiteDatabase对象，通过该对象可以对数据库进行操作；)
	 */
	private SQLiteDatabase db;

	/**
	 * 构造方法私有化 （目的在于不想让别的类调用这个对象的实例，一个类只能有一个实例化对象 ，
	 * 单例设计模式 。外部不能通过new 来获取类的对象的实例化）
	 */
	private CoolWeatherDB(Context context){
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	/**
	 * Java语言的关键字，当它用来修饰一个方法或者一个代码块的时候，
	 * 能够保证在同一时刻最多只有一个线程执行该段代码
	 */
	public synchronized static CoolWeatherDB getInstance(Context context){
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
			
		}
		return coolWeatherDB;
	}

	/**
	 * ContentValues 和HashTable类似都是一种存储的机制 但是两者最大的区别就在于，
	 * ContenValues只能存储基本类型的数据，像string，int之类的，不能存储对象这种东西，而HashTable却可以存储对象。
	 * 
	 * 将province的实例存储到数据库中
	 */
	public void saveProvince(Province province){
		if (province != null) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("province_name", province.getProvinceName());
			contentValues.put("province_code", province.getProvinceCode());
			db.insert("Province", null, contentValues);
		}
	}

	/**    
	 * Cursor 是每行的集合。
	 * 使用 moveToFirst() 定位第一行。
	 * 你必须知道每一列的名称。
	 * 你必须知道每一列的数据类型。
	 * Cursor 是一个随机的数据源。
	 * 所有的数据都是通过下标取得。
	 * cursor.moveToFirst（）指向查询结果的第一个位置
	 * 
	 * 从数据库中读取全国所有省份的信息
	 */
	public List<Province> loadProvinces(){
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				
				list.add(province);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close(); //关闭游标，释放资源
		}
		return list;
	}
	
	/**
	 * 将City的实例存储到数据库中
	 */
	public void saveCity(City city){
		if (city != null) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("city_name", city.getCityName());
			contentValues.put("city_code", city.getCityCode());
			contentValues.put("province_id", city.getProvinceId());
			db.insert("City", null, contentValues);
		}
	}
	
	/**
	 * 从数据库中读取某省下所有城市的信息；
	 */
	public List<City> loadCity(int provinceId){
		List<City> list = new ArrayList<City>();
		//查询语句的第三个参数指定where约束条件      第四个参数为where的占位符提供具体的值
		//String.valueOf(int i) : 将 int 变量 i 转换成字符串
		Cursor cursor = db.query("City", null, "province_id = ?",new String[]{String.valueOf(provinceId)} , null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}
	
	public void saveCounty(County county){
		if (county != null) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("county_name", county.getCountyName());
			contentValues.put("county_code", county.getCountyCode());
			contentValues.put("city_id", county.getCityId());
			db.insert("County", null, contentValues);
		}
	}
	
	/**
	 * 从数据库中读取全国某省所有县的信息
	 */
	public List<County> loadCounty(int cityId){
		List<County> list = new ArrayList<County>();
		//查询语句的第三个参数指定where约束条件      第四个参数为where的占位符提供具体的值
		//String.valueOf(int i) : 将 int 变量 i 转换成字符串
		Cursor cursor = db.query("County", null, "city_id = ?",new String[]{String.valueOf(cityId)} , null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				list.add(county);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}

}
