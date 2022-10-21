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

import java.util.Hashtable;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.ichano.rvs.streamer.constant.LoginError;
import com.ichano.rvs.streamer.constant.LoginState;
import com.ichano.rvs.streamer.constant.RvsSessionState;
import com.ichano.rvs.streamer.ui.AvsInitHelper;

public class MyAvsHelper extends AvsInitHelper{

	private static final String TAG = MyAvsHelper.class.getSimpleName();
	public static final String DEFAULT_USER_NAME = "admin";
	private TextView mCidView;
	private TextView mPwdView;
	private TextView mLogStateView;
	private TextView mDeviceNameView;
	private ImageView mBarCodeView;
	public String[] userNameAndPwd;
	public String deviceName;
	public boolean haveLogin;
	
	public MyAvsHelper(Context applicationContext) {
		super(applicationContext);
	}
	
	public void setViews(TextView cid, TextView pwd, TextView status, TextView deviceName, ImageView barCodeView){
		this.mCidView = cid;
		this.mPwdView = pwd;
		this.mLogStateView = status;
		this.mDeviceNameView = deviceName;
		this.mBarCodeView = barCodeView;
	}

	@Override
	public boolean enableAECorNot() {
		return false;
	}

	@Override
	public String getAppID() {
		return "open_source";
	}

	@Override
	public String getCompanyID() {
		return "open_source";
	}

	@Override
	public long getCompanyKey() {
		return 0;
	}

	@Override
	public String getLicense() {
		return "open_source";
	}

	@Override
	public int getMaxSessionNum() {
		return 1;
	}

	@Override
	public void onDeviceNameChange(String deviceName) {
		Log.i(TAG, "deviceName = " + deviceName);
		this.deviceName = deviceName;
		mDeviceNameView.setText(deviceName);
	}

	@Override
	public void onLoginResult(LoginState loginState, int progressRate, LoginError errorCode) {
		Log.i(TAG, "loginState = " + loginState + ", progressRate = " + progressRate + ", errorCode = " + errorCode);
		if(LoginState.CONNECTED == loginState){
			String cid = streamer.getCID();
			mCidView.setText(cid);
			String[] namePwd = streamer.getUserNameAndPwd();
			if(null != namePwd){
				userNameAndPwd = namePwd;
				mPwdView.setText(namePwd[1]);
				mBarCodeView.setImageBitmap(getBarCode(cid, namePwd[0], namePwd[1]));
			}else{
				mPwdView.setText(DEFAULT_USER_NAME);
				streamer.setUserNameAndPwd(DEFAULT_USER_NAME, DEFAULT_USER_NAME);
				mBarCodeView.setImageBitmap(getBarCode(cid, DEFAULT_USER_NAME, DEFAULT_USER_NAME));
				userNameAndPwd = new String[] {DEFAULT_USER_NAME, DEFAULT_USER_NAME};
			}
			
			String deviceName = streamer.getDeviceName();
			if(null == deviceName){
				this.deviceName = "android-" + cid;
				mDeviceNameView.setText(deviceName);
			}else{
				this.deviceName = deviceName;
				mDeviceNameView.setText(deviceName);
			}
			mLogStateView.setText(context.getString(R.string.connected));
			haveLogin = true;
		}else if(LoginState.CONNECTING == loginState){
			mLogStateView.setText(context.getString(R.string.connecting));
			haveLogin = false;
		}else if(LoginState.DISCONNECT == loginState){
			mLogStateView.setText(context.getString(R.string.disconnected));
			//��������ɲ�Ʒ����Ҫ�����ǵĹ���ע�Ტ�õ���Ȩ������ֻ������ʾʹ�á�
			if(errorCode == LoginError.ERR_WRONG_PACKAGE){
				Toast.makeText(context, R.string.wrong_package_name, Toast.LENGTH_LONG).show();
			}
			haveLogin = false;
		}
	}
	

	@Override
	public void onSessionStateChange(long remoteCID, RvsSessionState sessionState) {
		
	}

	@Override
	public void onUpdateCID(long cid) {
		mCidView.setText(String.valueOf(cid));
		Log.i(TAG, "cid = " + cid);
	}

	@Override
	public void onUpdateUserName() {
		String[] namePwd = streamer.getUserNameAndPwd();
		userNameAndPwd = namePwd;
		mPwdView.setText(namePwd[1]);
		Log.i(TAG, "namePwd = " + namePwd);
	}

	@Override
	public boolean enableDebug() {
		return true;
	}
 
	// Just in case TextUtils.isEmpty() not found on certain system
	private boolean stringIsEmpty(String str){
		if(null == str){
			return true;
		}else if("".equals(str)){
			return true;
		}
		return false;
	}
	
	private Bitmap getBarCode(String cid, String userName, String userPwd){
		try{
			Long.parseLong(cid);
		}catch(NumberFormatException e){
			return null;
		}
		if (stringIsEmpty(userName) || stringIsEmpty(userPwd)){
			return null;
		}
		int size = (int) context.getResources().getDimension(R.dimen.qr_size);
		BitMatrix martrix;
		try{
			Hashtable hints = new Hashtable();
			hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

			String qr = "cid=" + cid + "&username=" + userName + "&password=" + userPwd + "&flag=0";
			martrix = new MultiFormatWriter().encode(qr, BarcodeFormat.QR_CODE, size, size, hints);
			int width = martrix.getWidth();
			int height = martrix.getHeight();
			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++){
				for (int x = 0; x < width; x++){
					if (martrix.get(x, y)){
						pixels[y * width + x] = 0xff000000;
					}
				}
			}
			Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			return bitmap;
		} catch (WriterException e){
			e.printStackTrace();
			return null;
		}
	}
}
