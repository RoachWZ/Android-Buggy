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
package com.zhongyun.viewer.login;

import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;

import com.zhongyun.viewer.BaseActivity;
import com.zhongyun.viewer.MyViewerHelper;
import com.zhongyun.viewer.R;
import com.zhongyun.viewer.cameralist.CameraListActivity;

public class LoginActivity extends BaseActivity{

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dismissProgressDialog();
			switch (msg.what) {
			case UserLoginHandler.LOGIN_SUCCESS:
				startActivity(new Intent(LoginActivity.this, CameraListActivity.class));
				finish();
				break;
			case UserLoginHandler.LOGIN_FAIL:
				Toast.makeText(LoginActivity.this, R.string.login_fail, Toast.LENGTH_LONG).show();
				break;
				default:
					break;
			}
		}
	};
	private UserLoginHandler mUserLoginHandler;
	private ThirdLoginApi mThirdLoginApi;
	private ThirdLoginApi.OnLoginListener mThirdLoginListener = new ThirdLoginApi.OnLoginListener() {
		
		@Override
		public boolean onLogin(String platform, HashMap<String, Object> res) {
		       if(platform == null){
		        return false;
		       }
		       
		       showProgressDialog(R.string.wait_for_login);
		       Platform myPlatform = ShareSDK.getPlatform(platform);
		       String uid = myPlatform.getDb().getUserId();
		       String thirdSymbol = myPlatform.getName();
		       String nickName =  myPlatform.getDb().getUserName();
		       // 0 无性别，1男 ， 2女
		       int sex = 0;
		       if("m".equals(myPlatform.getDb().getUserGender())){
		        sex = 1;
		       }else{
		        sex = 2;
		       }
		       
		       mUserLoginHandler.setOtherLoginData(uid,thirdSymbol,nickName,sex);
		       mUserLoginHandler.doThing(UserLoginHandler.THIRD_LOGIN);
			return true;
		}
	};
	private String mQQ;
	private String mWechat;
	private Dialog mExitDialog;
	private boolean needLogout = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mUserLoginHandler = new UserLoginHandler(this, mHandler);
		mThirdLoginApi = new ThirdLoginApi(this);
		mThirdLoginApi.setOnLoginListener(mThirdLoginListener);
		Platform[] platformlist = ShareSDK.getPlatformList();
		for (Platform p : platformlist) {
			String name = p.getName();
			if("QQ".equals(name)){
				mQQ = name;
			}else if("Wechat".equals(name)){
				mWechat = name;
			}
		}
		
		Button qqLogin = (Button) findViewById(R.id.qq_login);
		qqLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mThirdLoginApi.setPlatform(mQQ);
				mThirdLoginApi.login();
			}
		});
		
		Button wechatLogin = (Button) findViewById(R.id.wechat_login);
		wechatLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mThirdLoginApi.setPlatform(mWechat);
				mThirdLoginApi.login();
			}
		});
		
		Button notLogin = (Button) findViewById(R.id.not_login);
		notLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LoginActivity.this, CameraListActivity.class));
				finish();
			}
		});
	}
	
	private void showExitDlg(){
		if(null != mExitDialog){
			mExitDialog.show();
		}else{
			mExitDialog = new AlertDialog.Builder(LoginActivity.this)
			.setTitle(R.string.exit_str)
			.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					needLogout = true;
					LoginActivity.this.finish();
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(needLogout){
			MyViewerHelper.getInstance(getApplicationContext()).logout();
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}
}
