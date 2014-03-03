package com.twt.youkushare;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {
	private static String TAG = "JsonUtils";

	public JSONArray parseJsonMulti(String strResult) {
		TLog.i(TAG, "parseJsonMulti:strResult-->" + strResult);
		JSONArray jsonObjs = null;
		try {

			jsonObjs = new JSONObject(strResult).getJSONArray("userinfos");
			TLog.i(TAG,
					"parseJsonMulti:jsonObjs.length()-->" + jsonObjs.length());
			for (int i = 0; i < jsonObjs.length(); i++) {

				JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));

				TLog.i(TAG, "parseJsonMulti:jjsonObj-->" + jsonObj.toString());
				String password = jsonObj.getString("password");

				String username = jsonObj.getString("username");
				TLog.i(TAG, "username:" + username + "  password:" + password);
			}
		} catch (JSONException e) {

			System.out.println("Jsons parse error !");

			e.printStackTrace();

		}
		return jsonObjs;
	}

}
