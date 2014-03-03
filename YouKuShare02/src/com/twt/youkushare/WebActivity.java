package com.twt.youkushare;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewDatabase;

public class WebActivity extends Activity {
	WebView mWebView;
	WebViewDatabase mWVD;
	RequestUserInfo mRequestUserInfo;
	String TAG = "WebActivity";
	// String url = "file:///android_asset/index.html";
	String mUrl = "http://www.youku.com/";
	// String url = "http://www.youku.com/user_TLogin/";
	// String mTLoginUrl = "http://www.youku.com/index/mTLogin";
	String mTLoginUrl = "http://www.youku.com/index/mlogin";
	String cookie;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cookie = getIntent().getStringExtra("cookie");
		TLog.i(TAG,"cookie:"+cookie);
		init();
		// new Thread() {
		// @Override
		// public void run() {
		// TLog.i(TAG, "Runnable");
		// mRequestUserInfo = new RequestUserInfo();
		// handler.sendEmptyMessage(0);
		//
		// }
		// }.start();

		// mWebView.addJavascriptInterface(new InJavaScriptLocalObj(),
		// "local_obj");

	}

	@SuppressLint("SetJavaScriptEnabled")
	private void init() {
		TLog.i(TAG, "init");
		setContentView(R.layout.activity_web);
		mWebView = (WebView) findViewById(R.id.WebView);
		mWVD = WebViewDatabase.getInstance(this);
		WebSettings nWebSettings = mWebView.getSettings();
		nWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		nWebSettings.setJavaScriptEnabled(true);
		nWebSettings.setPluginState(PluginState.ON);
		nWebSettings.setSavePassword(false);
		mWebView.setWebChromeClient(new YouKuWebChromeClient());
		mWebView.setWebViewClient(new YouKuWebViewClient());
		
		synCookies(this,mUrl);
		mWebView.loadUrl(mUrl);
//		String username = mRequestUserInfo.getUser().getUserName();
//		String passwrod = mRequestUserInfo.getUser().getPassword();
//		String postData = "username=" + username + "&password=" + passwrod;
//		mWebView.postUrl(mTLoginUrl, postData.getBytes());
	}

	public void synCookies(Context context, String url) {
		CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
		cookieManager.removeSessionCookie();// 移除
		cookieManager.setCookie(url, cookie);// cookies是在HttpClient中获得的cookie
		CookieSyncManager.getInstance().sync();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			TLog.i(TAG, "userInfo:" + mRequestUserInfo.getUser().toString());
			init();
		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web, menu);
		return true;
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mWVD.clearUsernamePassword();
		mWVD.clearHttpAuthUsernamePassword();
		mWVD.clearFormData();
		mWebView.clearCache(true);
		mWebView.clearHistory();
		CookieSyncManager.createInstance(mWebView.getContext());
		CookieSyncManager.getInstance().startSync();
		CookieManager.getInstance().removeSessionCookie();

		TLog.i(TAG, "onStop");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			mWebView.goBack(); // goBack()
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	class YouKuWebViewClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onFormResubmission(WebView view, Message dontResend,
				Message resend) {
			// TODO Auto-generated method stub
			TLog.d(TAG, "onFormResubmission");
			super.onFormResubmission(view, dontResend, resend);
		}

		@Override
		public void onLoadResource(WebView view, String url) {
			// TODO Auto-generated method stub
			TLog.d(TAG, "onLoadResource");
			TLog.d(TAG, "url:" + url);
			super.onLoadResource(view, url);
		}

		@Override
		public void onReceivedLoginRequest(WebView view, String realm,
				String account, String args) {
			// TODO Auto-generated method stub
			TLog.d(TAG, "onReceivedTLoginRequest");
			super.onReceivedLoginRequest(view, realm, account, args);
		}

		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			TLog.d(TAG, "onPageStarted");
			super.onPageStarted(view, url, favicon);
		}

		public void onPageFinished(WebView view, String url) {
			TLog.d(TAG, "onPageFinished ");
			if (url.equals("http://www.youku.com/index/mlogin")) {
				mWebView.loadUrl(mUrl);
				return;
			}
			super.onPageFinished(view, url);
		}

	}

	class YouKuWebChromeClient extends WebChromeClient {

		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			// TODO Auto-generated method stub
			TLog.i(TAG, "onJsAlert");
			return super.onJsAlert(view, url, message, result);
		}

		@Override
		public void onReceivedTouchIconUrl(WebView view, String url,
				boolean precomposed) {
			// TODO Auto-generated method stub
			TLog.i(TAG, "onReceivedTouchIconUrl");
			super.onReceivedTouchIconUrl(view, url, precomposed);
		}

		@Override
		public boolean onCreateWindow(WebView view, boolean isDiaTLog,
				boolean isUserGesture, Message resultMsg) {
			// TODO Auto-generated method stub
			TLog.i(TAG, "onCreateWindow");
			return super.onCreateWindow(view, isDiaTLog, isUserGesture,
					resultMsg);
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message,
				JsResult result) {
			// TODO Auto-generated method stub
			TLog.i(TAG, "onJsConfirm");
			return super.onJsConfirm(view, url, message, result);
		}

		@Override
		public void onRequestFocus(WebView view) {
			// TODO Auto-generated method stub
			TLog.i(TAG, "onRequestFocus");
			super.onRequestFocus(view);
		}

	}

	final class InJavaScriptLocalObj {
		public void showSource(String html) {
			TLog.i("HTML", html);
		}
	}
}
