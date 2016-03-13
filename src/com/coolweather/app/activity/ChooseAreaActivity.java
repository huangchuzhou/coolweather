package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class ChooseAreaActivity extends Activity {
	private ListView list_view;
	private TextView title_text;
	private ProgressDialog progressDialog;
	private ArrayAdapter<String> adapter;
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	private CoolWeatherDB coolWeatherDB;
	//当前的级别
	private int currentLevel;
	
	//省列表
	private List<Province> provinceList;  
	//城市列表
	private List<City> cityList;
	//县列表
	private List<County> countyList;
	//选中的省份
	private Province selectedProvince;
	//选中的城市
	private City selectedcCity;
	
	private List<String> dataList = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_area);
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (sharedPreferences.getBoolean("city_selected", false)) {
			Intent intent =new Intent(ChooseAreaActivity.this,WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		
		title_text = (TextView)findViewById(R.id.title_text);
		list_view = (ListView)findViewById(R.id.list_view);
		adapter = new ArrayAdapter<String>(ChooseAreaActivity.this, android.R.layout.simple_list_item_1, dataList);
		list_view.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(ChooseAreaActivity.this);
		list_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(position);
					queryCities();
				}else if(currentLevel == LEVEL_CITY) {
					selectedcCity = cityList.get(position);
					queryCounties();
				}else if (currentLevel == LEVEL_COUNTY) {
					String countyCode = countyList.get(position).getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
			}
			
		});
		queryProvinces();
		
	}
	
	/**
	 * 查询全国所有省，优先从数据库查询，没有再到服务器上查询
	 */
	private void queryProvinces(){
		provinceList = coolWeatherDB.loadProvinces();
		
		if (provinceList.size()>0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			list_view.setSelection(0);
			title_text.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		}else {
			queryFromService(null,"province");
		}
	}
	/**
	 * 查询某省全城，优先从数据库查询，没有再到服务器上查询
	 */
	private void queryCities(){
		cityList = coolWeatherDB.loadCity(selectedProvince.getId());
		if (cityList.size()>0) {
			dataList.clear();             //移除所有的元素
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();  //动态更新ListView;不用创建新的Activity实例
			currentLevel = LEVEL_CITY;
			list_view.setSelection(0);
			title_text.setText(selectedProvince.getProvinceName());
		}else {
			queryFromService(selectedProvince.getProvinceCode(), "city");
		}
	}
	/**
	 *  查询某城全村，优先从数据库查询，没有再到服务器上查询
	 */
	private void queryCounties(){
		countyList = coolWeatherDB.loadCounty(selectedcCity.getId());
		if (countyList.size()>0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			currentLevel = LEVEL_COUNTY;
			list_view.setSelection(0);
			title_text.setText(selectedcCity.getCityName());
		}else {
			queryFromService(selectedcCity.getCityCode(), "county");
		}
	}
	/**
	 * 根据传入的代号和类型从服务器上查询省市县数据
	 */
	private void queryFromService(String code,final String type){
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
						
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_LONG).show();
					}
				});
			}
			
			@Override
			public void OnFinish(String responce) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvinceResponce(coolWeatherDB, responce);
				}else if ("city".equals(type)) {
					result = Utility.handlerCityResponce(coolWeatherDB, responce, selectedProvince.getId());
				}else if("county".equals(type)){
					result = Utility.handlerCountyResponce(coolWeatherDB, responce, selectedcCity.getId());
				}
				if (result) {
					runOnUiThread(new Runnable() {
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							}else if("city".equals(type)){
								queryCities();
							}else if ("county".equals(type)) {
								queryCounties();
							}
						}
					});
				}
			}
		});
	
	}
	
	public void showProgressDialog(){
		if (progressDialog == null) {
			
			progressDialog = new ProgressDialog(ChooseAreaActivity.this);
			progressDialog.setMessage("正在加载中...");
			progressDialog.setCanceledOnTouchOutside(false); //点击屏幕其他地方，dialog对话框不会消失
		}
		progressDialog.show();
	}
	
	public void closeProgressDialog(){
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	public void onBackPressed(){
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		}else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		}else {
			finish();
		}
	}
}
