package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 
 * @author huangchuzhou
 *全国所有省市的数据都是从服务器端获取到的
 *此类是和服务器进行交互
 */
public class HttpUtil {
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener ){
		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpURLConnection connection = null;
				URL url;
				try {
					url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					InputStream is = connection.getInputStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					StringBuilder responce = new StringBuilder();
					String line;
					while ((line = br.readLine()) != null) {
						responce.append(line);
					}
					 if (listener != null) {
						listener.OnFinish(responce.toString());
					  }
				} catch (Exception e) {
					if (listener != null) {
						listener.onError(e);
					}
				}finally{
					if (connection != null) {
						connection.disconnect();
					}
				} 
			}
		}).start();
	}
}
