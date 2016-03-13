package com.coolweather.app.util;

//此接口是回调服务返回的结果
public interface HttpCallbackListener {
	 void OnFinish(String responce);
	 void onError(Exception e);
}
