package com.twt.youkushare;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity implements OnClickListener {
	private static String TAG = "LoginActivity";
	private EditText mUserName, mPassWord;
	private Button mLoginButton;
	RequestUserInfo mRequestUserInfo;
	String mTLoginUrl = "http://www.youku.com/index/mlogin";
	String postData;
	String cookie;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login);
		mUserName = (EditText) findViewById(R.id.username);
		mPassWord = (EditText) findViewById(R.id.password);
		mLoginButton = (Button) findViewById(R.id.login);
		mLoginButton.setOnClickListener(this);
		new Thread() {
			@Override
			public void run() {
				TLog.i(TAG, "Runnable");
				mRequestUserInfo = new RequestUserInfo();
				handler.sendEmptyMessage(0);

			}
		}.start();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			TLog.i(TAG, "userInfo:" + mRequestUserInfo.getUser().toString());
			if (msg.what == 0) {
				init();
			} else if (msg.what == 1) {
				TLog.i(TAG, "msg.what = 1");
				startWebActivity();
			} else if (msg.what == 2) {
				TLog.i(TAG, "msg.what = 1");
			}
		}

	};

	private void init() {
		TLog.i(TAG, "init");
		String username = mRequestUserInfo.getUser().getUserName();
		String passwrod = mRequestUserInfo.getUser().getPassword();
		mUserName.setText(username);
		mUserName.setEnabled(false);
		mPassWord.setText(passwrod);
		mPassWord.setEnabled(false);
		postData = "username=" + username + "&password=" + passwrod;

	}

	private void startWebActivity() {
		Intent intent = new Intent(this, WebActivity.class);
		intent.putExtra("cookie", cookie);
		startActivity(intent);
		this.finish();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		new Thread() {
			@Override
			public void run() {
				TLog.i(TAG, "Runnable");
				if (executeHttpPost().equals("1")) {
					handler.sendEmptyMessage(1);
				} else {
					handler.sendEmptyMessage(2);
				}

			}
		}.start();
	}

	public String executeHttpPost() {
		String result = null;
		URL url = null;
		HttpURLConnection connection = null;
		InputStreamReader in = null;
		try {
			url = new URL(mTLoginUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("Charset", "utf-8");
//			cookie:_l_lgi=354836168; path=/; domain=.youku.com

//			connection.s
			DataOutputStream dop = new DataOutputStream(
					connection.getOutputStream());
			dop.writeBytes(postData);
			dop.flush();
			dop.close();
			cookie = connection.getHeaderField("set-cookie");
			String[] sessionId = cookie.split(";");
			TLog.i(TAG, "cookie:" + sessionId[0]);
			in = new InputStreamReader(connection.getInputStream());
			BufferedReader bufferedReader = new BufferedReader(in);
			StringBuffer strBuffer = new StringBuffer();
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				strBuffer.append(line);
			}
			result = strBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		TLog.i(TAG, "result:" + result);
		return result;
	}

}
