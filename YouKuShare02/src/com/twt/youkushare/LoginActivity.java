package com.twt.youkushare;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
	List<String> cookisList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login);
		mUserName = (EditText) findViewById(R.id.username);
		mPassWord = (EditText) findViewById(R.id.password);
		mLoginButton = (Button) findViewById(R.id.login);
		mLoginButton.setOnClickListener(this);

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
				TLog.i(TAG, "msg.what = 2");
				requestUserInfo();
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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!isNetworkAvailable(this)) {
			showSetNetworkUI(this);
		} else {
			requestUserInfo();
		}
	}

	private void requestUserInfo() {
		new Thread() {
			@Override
			public void run() {
				TLog.i(TAG, "Runnable");
				mRequestUserInfo = new RequestUserInfo();
				handler.sendEmptyMessage(0);

			}
		}.start();
	}

	private void startWebActivity() {
		Intent intent = new Intent(this, WebActivity.class);
		String[] cookies = new String[5];
		for (int i = 0; i < cookisList.size(); i++) {
			cookies[i] = cookisList.get(i);
		}
		intent.putExtra("cookies", cookies);
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
			DataOutputStream dop = new DataOutputStream(
					connection.getOutputStream());
			dop.writeBytes(postData);
			dop.flush();
			dop.close();
			cookie = connection.getHeaderField("set-cookie");
			cookisList = connection.getHeaderFields().get("set-cookie");
			TLog.i(TAG, cookisList.toString());
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

	public void showSetNetworkUI(final Context context) {
		// 提示对话框
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("网络设置提示")
				.setMessage("网络连接不可用,是否进行设置?")
				.setPositiveButton("设置", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent = null;
						intent = new Intent(
								android.provider.Settings.ACTION_WIFI_SETTINGS);
						context.startActivity(intent);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						LoginActivity.this.finish();
						dialog.dismiss();
					}
				}).show();
	}

	public boolean isNetworkAvailable(Context context) {

		ConnectivityManager manager = (ConnectivityManager) context
				.getApplicationContext().getSystemService(
						Context.CONNECTIVITY_SERVICE);

		if (manager == null) {
			return false;
		}

		NetworkInfo networkinfo = manager.getActiveNetworkInfo();

		if (networkinfo == null || !networkinfo.isAvailable()) {
			return false;
		}

		return true;
	}

}
