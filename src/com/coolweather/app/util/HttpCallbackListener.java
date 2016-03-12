package com.coolweather.app.util;

//此接口是回调服务返回的结果
public interface HttpCallbackListener {
	public void OnFinish(String responce);
	public void onError(Exception e);
}
