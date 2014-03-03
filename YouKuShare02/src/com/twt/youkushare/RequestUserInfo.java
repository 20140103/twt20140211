package com.twt.youkushare;

import java.io.IOException;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RequestUserInfo {
	private static String TAG = "RequestUserInfo";
	private UserInfo user;
	private String mUserInfosUrl = "http://121.199.10.53/war3/userinofs.php";
	JsonUtils mJsonUtils;

	public RequestUserInfo() {
		TLog.i(TAG, "RequestUserInfo");
		mJsonUtils = new JsonUtils();
		String nJsonStr = connServerForResult(mUserInfosUrl);
		TLog.i(TAG, nJsonStr);
		JSONArray nJsonArray = mJsonUtils.parseJsonMulti(nJsonStr);
		tactics(nJsonArray);
	}

	public UserInfo getUser() {
		return user;
	}

	private String connServerForResult(String strUrl) {

		// HttpGet对象
		HttpGet httpRequest = new HttpGet(strUrl);
		String strResult = "";
		try {
			// HttpClient对象
			HttpClient httpClient = new DefaultHttpClient();
			// 获得HttpResponse对象
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 取得返回的数据
				strResult = EntityUtils.toString(httpResponse.getEntity());
			}
		} catch (ClientProtocolException e) {
			TLog.i(TAG, e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return strResult;

	}

	private void tactics(JSONArray jsonArray) {
		if (jsonArray.length() > 0) {

			try {
				Random random = new Random();
				int i = random.nextInt(jsonArray.length());
				JSONObject jsonObj;
				jsonObj = ((JSONObject) jsonArray.opt(i));
				user = new UserInfo();
				user.setPassword(jsonObj.getString("password"));
				user.setUserName(jsonObj.getString("username"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	class UserInfo {
		private String userName;
		private String password;

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		@Override
		public String toString() {
			return "UserInfo [userName=" + userName + ", password=" + password
					+ "]";
		}
		
		
	}

}
