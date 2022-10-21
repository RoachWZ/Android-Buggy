/*
 * Copyright (C) 2015 iChano incorporation's Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhongyun.avs;

import java.util.Locale;
import com.ichano.rvs.streamer.Streamer;
import com.ichano.rvs.streamer.ui.MediaSurfaceView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class AvsActivity extends Activity 
	implements View.OnClickListener{

	private static final String DISCLAIMER_URL_CN = "file:///android_asset/iChanoPrivacyPolicyCN.html";
	private static final String DISCLAIMER_URL_EN = "file:///android_asset/iChanoPrivacyPolicyEN.html";
	private boolean mShowChinese;
	private MyAvsHelper mMyAvsHelper;
	private MediaSurfaceView mMediaSurfaceView;
	boolean isFirst = true;
	private LayoutInflater mLayoutInflater;
	private Dialog mModifyInfoDialog;
	private Dialog mAboutDialog;
	private Dialog mDisclaimerDialog;
	private Dialog mExitDialog;
	private LinearLayout menu_layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
	    	getWindow().setStatusBarColor(getResources().getColor(R.color.background));
	    }
		mLayoutInflater = LayoutInflater.from(this);
		TextView cidView = (TextView) findViewById(R.id.cid);
		cidView.setOnClickListener(this);
		TextView pwdView = (TextView) findViewById(R.id.pwd);
		pwdView.setOnClickListener(this);
		TextView statusView = (TextView) findViewById(R.id.logState);
		TextView deviceName = (TextView) findViewById(R.id.deviceName);
		ImageView barCodeView = (ImageView) findViewById(R.id.barcode);
		ImageView menuView = (ImageView) findViewById(R.id.menu);
		menuView.setOnClickListener(this);
	    menu_layout = (LinearLayout) findViewById(R.id.menu_layout);
	    menu_layout.setOnClickListener(this);
	    findViewById(R.id.main_layout).setOnClickListener(this);
	    findViewById(R.id.help).setOnClickListener(this);
	    findViewById(R.id.feedback).setOnClickListener(this);
	    findViewById(R.id.about).setOnClickListener(this);
	    findViewById(R.id.disclaimer).setOnClickListener(this);
	    findViewById(R.id.avs_title).setOnClickListener(this);
	    mMyAvsHelper = new MyAvsHelper(getApplicationContext());
	    mMyAvsHelper.setViews(cidView, pwdView, statusView, deviceName, barCodeView);
	    mMyAvsHelper.login();
		final int[] size = mMyAvsHelper.getVideoSize();
		
		mMediaSurfaceView = (MediaSurfaceView) findViewById(R.id.cameraView);
		mMediaSurfaceView.openCamera(mMyAvsHelper);
		
		ViewTreeObserver viewTreeObserver = mMediaSurfaceView.getViewTreeObserver();
		viewTreeObserver.addOnPreDrawListener(new OnPreDrawListener(){
			@Override
			public boolean onPreDraw(){
				if (isFirst){
					int height = mMediaSurfaceView.getMeasuredHeight();
					int width = mMediaSurfaceView.getMeasuredWidth();
					float r = (float)height/(float)width;
					float r2 = (float)size[1]/(float)size[0];
					RelativeLayout.LayoutParams pvLayout = (RelativeLayout.LayoutParams) mMediaSurfaceView.getLayoutParams();
					if (r > r2){
						pvLayout.height = (int) (width*r2);
					}else{
						pvLayout.width = (int) (height/r2);
					}
					isFirst = false;
				}
				return true;
			}
		});		
		
		mShowChinese = "zh".equals(Locale.getDefault().getLanguage().toLowerCase());
		//update
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
		
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
		
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.e("zhongquan","on destroy");
		mMediaSurfaceView.closeCamera();
		mMyAvsHelper.logout();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	private void showModifyInfoDialog(){
		if(!mMyAvsHelper.haveLogin) return;
		if(null != mModifyInfoDialog){
			mModifyInfoDialog.show();
		}else{
			View view = mLayoutInflater.inflate(R.layout.modify_info_dialog, null);
			final EditText deviceNameView = (EditText) view.findViewById(R.id.device_name);
			deviceNameView.setText(mMyAvsHelper.deviceName);
			final EditText passwordView = (EditText) view.findViewById(R.id.password);
			passwordView.setText(mMyAvsHelper.userNameAndPwd[1]);
			mModifyInfoDialog = new AlertDialog.Builder(this)
			.setView(view)
			.setTitle(R.string.modify_info_dlg_title)
			.setPositiveButton(R.string.confirm, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String deviceName = deviceNameView.getText().toString();
					String pwd = passwordView.getText().toString();
					if(null == deviceName || null == pwd){
						Toast.makeText(AvsActivity.this, R.string.empty_info, Toast.LENGTH_LONG).show();
						return;
					}
					if("".equals(deviceName) || "".equals(pwd)){
						Toast.makeText(AvsActivity.this, R.string.empty_info, Toast.LENGTH_LONG).show();
						return;
					}
					if(pwd.length()<6){
						Toast.makeText(AvsActivity.this, R.string.short_pwd, Toast.LENGTH_LONG).show();
						return;
					}
					if(pwd.matches("[a-zA-Z]+")||pwd.matches("[0-9]+")){
						Toast.makeText(AvsActivity.this, R.string.invalid_pwd, Toast.LENGTH_LONG).show();
						return;
					}
					if (!deviceName.matches("[\\[\\]\\{\\}\\(\\)\\*@!\":;,\\.%#\\|\\?\\/_\\+-\\\\='~\\$^&<>a-zA-Z0-9_\u4e00-\u9fa5]*")){
						Toast.makeText(AvsActivity.this, R.string.invalid_device_name, Toast.LENGTH_LONG).show();
						return;
					}
					if(!deviceName.equals(mMyAvsHelper.deviceName)){
						Streamer.getStreamer().setDeviceName(deviceName);
					}
					if(!pwd.equals(mMyAvsHelper.userNameAndPwd[1])){
						Streamer.getStreamer().setUserNameAndPwd(MyAvsHelper.DEFAULT_USER_NAME, pwd);
					}
				}
			})
			.setNegativeButton(R.string.cancel, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.create();
			mModifyInfoDialog.show();
		}
	}
	
	private String getAppVersionName(){
		PackageManager manager = this.getPackageManager();
		PackageInfo info;
		try {
			info = manager.getPackageInfo(this.getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "1.0";
		}
	}
	
	private void showAboutDialog(){
		if(null != mAboutDialog){
			mAboutDialog.show();
		}else{
			View view = mLayoutInflater.inflate(R.layout.about_dialog, null);
			TextView aboutView = (TextView) view.findViewById(R.id.about);
			aboutView.setText(String.format(getString(R.string.about_str), getString(R.string.app_name), getAppVersionName()));
			mAboutDialog = new AlertDialog.Builder(this)
			.setView(view)
			.setTitle(R.string.about)
			.setPositiveButton(R.string.confirm, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.create();
			mAboutDialog.show();
		}
	}
	
	private void showDisclaimerDlg(){
		if(null != mDisclaimerDialog){
			mDisclaimerDialog.show();
		}else{
			WebView webView = new WebView(AvsActivity.this);
			webView.loadUrl(mShowChinese ? DISCLAIMER_URL_CN : DISCLAIMER_URL_EN);
			mDisclaimerDialog = new AlertDialog.Builder(this)
			.setView(webView)
			.setTitle(R.string.disclaimer)
			.setPositiveButton(R.string.confirm, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.create();
			mDisclaimerDialog.show();
		}
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.cid:
		case R.id.pwd:
			showModifyInfoDialog();
			break;
		case R.id.menu:
			if(menu_layout.getVisibility() == View.VISIBLE){
				menu_layout.setVisibility(View.GONE);
			}else{
			menu_layout.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.help:
			menu_layout.setVisibility(View.GONE);
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), GuideActivity.class);
			intent.putExtra(GuideActivity.START_AVS_ACTIVITY, false);
			startActivity(intent);
			break;
		case R.id.feedback:
			menu_layout.setVisibility(View.GONE);
			FeedbackAgent agent = new FeedbackAgent(this);
			agent.startFeedbackActivity();
			break;
		case R.id.about:
			menu_layout.setVisibility(View.GONE);
			showAboutDialog();
			break;
		case R.id.disclaimer:
			menu_layout.setVisibility(View.GONE);
			showDisclaimerDlg();
			break;
		case R.id.avs_title:
		case R.id.main_layout:
			if(menu_layout.getVisibility() == View.VISIBLE){
				menu_layout.setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}
	}
	
	private void showExitDlg(){
		if(null != mExitDialog){
			mExitDialog.show();
		}else{
			mExitDialog = new AlertDialog.Builder(AvsActivity.this)
			.setTitle(R.string.exit_str)
			.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					AvsActivity.this.finish();
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.create();
			mExitDialog.show();
		}
	}
	
	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		showExitDlg();
	}

}
