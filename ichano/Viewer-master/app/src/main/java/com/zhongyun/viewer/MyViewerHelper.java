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
package com.zhongyun.viewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.ichano.rvs.viewer.Viewer;
import com.ichano.rvs.viewer.bean.StreamerInfo;
import com.ichano.rvs.viewer.callback.RecvJpegListener;
import com.ichano.rvs.viewer.constant.LoginError;
import com.ichano.rvs.viewer.constant.LoginState;
import com.ichano.rvs.viewer.constant.RvsJpegType;
import com.ichano.rvs.viewer.constant.RvsSessionState;
import com.ichano.rvs.viewer.constant.StreamerConfigState;
import com.ichano.rvs.viewer.constant.StreamerPresenceState;
import com.ichano.rvs.viewer.ui.ViewerInitHelper;
import com.zhongyun.viewer.db.CameraInfo;
import com.zhongyun.viewer.db.CameraInfoManager;
import com.zhongyun.viewer.login.UserInfo;
import com.zhongyun.viewer.utils.Constants;
import com.zhongyun.viewer.utils.ImageDownloader;
import com.zhongyun.viewer.utils.PrefUtils;

public class MyViewerHelper extends ViewerInitHelper{

	private static final String TAG = "MyViewerHelper";
	private static MyViewerHelper mViewer;
	private UserInfo mUserInfo;
	private LoginListener mLoginListener;
	private List<CameraStateListener> mCameraStateListeners = new ArrayList<CameraStateListener>();
	
	private static List<CameraInfo> mCameraInfos;
	private static CameraInfoManager mCameraInfoManager;
	
	private Handler mHandler = new Handler();
	private final static long GET_THUMB_PERIOD = 600000;
	@SuppressLint("UseSparseArrays")
	private HashMap<Long, Long> mThumbsGetTime = new HashMap<Long, Long>();
	private HashMap<Long, Long> mThumbRequestMap = new HashMap<Long, Long>();
	private static Context mContext;
	public static MyViewerHelper getInstance(Context applicationContext){
		if(null == mViewer){
			mViewer = new MyViewerHelper(applicationContext);
			
			mCameraInfoManager = new CameraInfoManager(applicationContext);
			mCameraInfos = mCameraInfoManager.getAllCameraInfos();
			if(null == mCameraInfos) mCameraInfos = new ArrayList<CameraInfo>();
			mContext = applicationContext;
		}
		mViewer.login();
		return mViewer;
	}
	
	public List<CameraInfo> getAllCameraInfos(){
		return mCameraInfos;
	}
	
	public void removeAllCameraInfos(){
		mCameraInfos.clear();
	}
	
	public CameraInfo getCameraInfo(long cid){
		for (CameraInfo info : mCameraInfos) {
			if(cid == info.getCid()){
				return info;
			}
		}
		return null;
	}
	
	public void addCameraInfo(CameraInfo info){
		mCameraInfos.add(info);
	}
	
	public void removeCameraInfo(CameraInfo info){
		mCameraInfos.remove(info);
	}
	
	private MyViewerHelper(Context applicationContext) {
		super(applicationContext);
		mUserInfo = UserInfo.getUserInfo(applicationContext);
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
	public void onLoginResult(LoginState loginState, int progressRate, LoginError errorCode) {
		if(LoginState.CONNECTED == loginState){
			long cid = Viewer.getViewer().getCID();
			mUserInfo.saveClientCid(cid);
			if(null != mLoginListener) mLoginListener.onLoginResult(true);
		}else if(LoginState.DISCONNECT == loginState){
			if(null != mLoginListener) mLoginListener.onLoginResult(false); 
			// When you are going to publish an app, you need register on our website (dev.ichano.com) to obtain license code.
			if(errorCode == LoginError.ERR_WRONG_PACKAGE){
				Toast.makeText(context, R.string.wrong_package_name, Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onSessionStateChange(final long streamerCID, final RvsSessionState sessionState) {
		long lastTime = (null == mThumbsGetTime.get(streamerCID)) ? 0 : mThumbsGetTime.get(streamerCID);
		long cur = System.currentTimeMillis();
		
		//do not get thumb so busy.
		if(cur - lastTime > GET_THUMB_PERIOD){
			mThumbsGetTime.put(streamerCID, cur);
			long requestId =  Viewer.getViewer().getMedia().requestJpeg(streamerCID, 0, 0, RvsJpegType.ICON, new RecvJpegListener() {
				
				@Override
				public void onRecvJpeg(long requestId,byte[] data) {
					if(null == mThumbRequestMap.get(requestId)) return;
					long cid = mThumbRequestMap.get(requestId);
					Bitmap bmp = ImageDownloader.getInstance().getDefaultBmp(mContext) ;
					if(data!=null){
					    Bitmap bmpMem = ImageDownloader.getInstance().putBitmapData(String.valueOf(streamerCID), data);
					    if(bmpMem!=null) bmp = bmpMem;
					}
					CameraInfo info = getCameraInfo(cid);
					if(null != info && null != bmp){
						info.setCameraThumb(bmp);
						mCameraInfoManager.update(info);
						mHandler.post(new Runnable() {
							
							@Override
							public void run() {
								if(mCameraStateListeners.size() > 0){
									 for (CameraStateListener l : mCameraStateListeners) {
										 l.onCameraConnectionChange(streamerCID, RvsSessionState.CONNECTED == sessionState);
									}
								 }
							}
						});
					}
				}
			});
			mThumbRequestMap.put(requestId, streamerCID);
		}
	}

	@Override
	public void onStreamerConfigState(long streamerCID, StreamerConfigState state) {
		
	}

	@Override
	public void onStreamerPresenceState(long streamerCid, StreamerPresenceState state) {
		CameraInfo info = getCameraInfo(streamerCid);
		if(null != info){
			StreamerInfo  sinfo = Viewer.getViewer().getStreamerInfoMgr().getStreamerInfo(streamerCid);
			String name = sinfo.getDeviceName();
			if(null != name && (!info.getCameraName().equals(name))){
				info.setCameraName(name);
				mCameraInfoManager.update(info);
			}
			if(StreamerPresenceState.USRNAME_PWD_ERR == state && info.getPwdIsRight()){
				info.setIsOnline(false);
				info.setPwdIsRight(false);
				if(mCameraStateListeners.size() > 0){
					 for (CameraStateListener l : mCameraStateListeners) {
						 l.onCameraStateChange(streamerCid, state);
					}
				 }
			}else {
				boolean online = false;
				if(StreamerPresenceState.ONLINE == state){
					online = true;
					info.setPwdIsRight(true);
				}
				if(info.getIsOnline() != online){
					info.setIsOnline(online);
					 if(mCameraStateListeners.size() > 0){
						 for (CameraStateListener l : mCameraStateListeners) {
							 l.onCameraStateChange(streamerCid, state);
						}
					 }
				}
			}
		}
	}
	
	@Override
	public void onUpdateCID(long cid) {
		mUserInfo.saveClientCid(cid);
	}

	public void setLoginListener(LoginListener l){
		mLoginListener = l;
	}
	
	public void addCameraStateListener(CameraStateListener l){
		if(!mCameraStateListeners.contains(l)){
			mCameraStateListeners.add(l);
		}
	}
	
	public void removeCameraStateListener(CameraStateListener l){
		mCameraStateListeners.remove(l);
	}
	
	public interface LoginListener{
		public void onLoginResult(boolean success);
	}
	
	public interface CameraStateListener{
		public void onCameraStateChange(long streamerCID, StreamerPresenceState state);
		public void onCameraConnectionChange(long streamerCID, boolean connected);
	}
}
