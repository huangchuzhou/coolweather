package com.coolweather.app.util;

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
		if (TextUtils.isEmpty(responce)) {
			
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
		if (TextUtils.isEmpty(responce)) {
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
		if (TextUtils.isEmpty(responce)) {
			String[] allCounties = responce.split(","); 
			if (allCounties != null && allCounties.length >0) {
				
				for (String p : allCounties) {
					String[] arrays = p.split("|");
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
}
