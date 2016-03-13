package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

/**
 * 
 * @author huangchuzhou
 *由于服务器返回的数据都是“代号|城市，代号|城市”这种格式的
 *此工具类是用来解析和处理这种数据的
 */
public class Utility {
	/**
	 *  解释和处理服务器返回的省级数据
	 *  responce 是服务器返回的数据
	 */
	public synchronized static boolean handleProvinceResponce(CoolWeatherDB coolWeatherDB,String responce){
		//TextUtile.isEmpty 这个方法传入的参数无论是null 或者是“”都会返回一个true;
		if (!TextUtils.isEmpty(responce)) {
			
			//String 的spilt方法是按格式用于分割字符串并返回一个字符串数组
			String[] allProvinces = responce.split(",");
			if (allProvinces != null && allProvinces.length >0) {
				
				for (String p : allProvinces) {
					String[] arrays = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(arrays[0]);
					province.setProvinceName(arrays[1]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解释和处理服务器返回的城市数据
	 */
	public synchronized static boolean handlerCityResponce(CoolWeatherDB coolWeatherDB,String responce,int provinceId){
		if (!TextUtils.isEmpty(responce)) {
			String[] allCities = responce.split(",");
			if (allCities != null && allCities.length >0) {
				
				for (String p : allCities) {
					String[] arrays = p.split("\\|");
					City city = new City();
					city.setCityCode(arrays[0]);
					city.setCityName(arrays[1]);
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解释和处理服务器返回的县数据
	 */
	public synchronized static boolean handlerCountyResponce(CoolWeatherDB coolWeatherDB,String responce,int cityId){
		if (!TextUtils.isEmpty(responce)) {
			String[] allCounties = responce.split(","); 
			if (allCounties != null && allCounties.length >0) {
				
				for (String p : allCounties) {
					String[] arrays = p.split("\\|");
					County county = new County();
					county.setCountyCode(arrays[0]);
					county.setCountyName(arrays[1]);
					county.setCityId(cityId);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解释和处理服务器返回的JSON数据，并将解析出的数据存储到本地
	 */
	
	public static void handleWeatherResponse(Context context , String responce){
		try {
			JSONObject jsonObject = new JSONObject(responce);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *将服务器返回的所有天气信息存储到SharedPerference文件中 
	 *
	 */
	public static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, String temp2, String weatherDesp, String publishTime){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", simpleDateFormat.format(new Date()));
		editor.commit();
	}
}
