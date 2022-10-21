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
package com.zhongyun.viewer.cameralist;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.zhongyun.viewer.async.AsyncUtil;
import com.zhongyun.viewer.async.Callable;
import com.zhongyun.viewer.async.CallbackMessage;
import com.zhongyun.viewer.http.JsonReturnCode;
import com.zhongyun.viewer.http.JsonSerializer;
import com.zhongyun.viewer.http.UserHttpApi;
import com.zhongyun.viewer.http.bean.JsonReturn;
import com.zhongyun.viewer.login.UserInfo;

public class CameraListHandler implements Callable<String>, CallbackMessage<String>{
	private static final String TAG = CameraListHandler.class.getSimpleName();
	Context context;
	String sessionid;
	int request;
	Handler handler;
	private UserInfo mUserInfo;
	public static final int SYNC_CID_REQUEST = 0;
	public static final int SYNC_SUCCESS = 1000;
	public static final int SYNC_FAIL = 1001;
	public static final int NOT_LOGIN = 1002;
	
	public CameraListHandler(Context context,Handler handler){
		this.context = context;
		this.handler = handler;
		mUserInfo = UserInfo.getUserInfo(context);
	}
	
	public void doThing(int request){
		sessionid = mUserInfo.sessionId;
		if(request == 5){
			//loginHandler.doThing(5);
		}else{
			this.request = request;
			AsyncUtil.doAsync(request, context, this, this);
		}
	}

	@Override
	public void onComplete(int requestid, String pCallbackValue) {
		if (requestid == SYNC_CID_REQUEST) {
			try {
				JsonReturn Resp = JsonSerializer.deSerialize(pCallbackValue, JsonReturn.class);
				if (Resp.getCode() == JsonReturnCode.code_succ) {
					SyncCidResponse syncCidResp = JsonSerializer.deSerialize(pCallbackValue, SyncCidResponse.class);
					if (!syncCidResp.getDesc().getCidlist().isEmpty()) {
						//setCidInfo(syncCidResp.getDesc().getCidlist(),syncCidResp.getCode());
						Log.e(TAG, "cid list size = " + syncCidResp.getDesc().getCidlist().size());
					}
					mUserInfo.setTS(syncCidResp.getDesc().getTs());
					Message message = Message.obtain();
					message.what = SYNC_SUCCESS;
					message.obj = syncCidResp.getDesc().getCidlist();
					handler.sendMessage(message);
				}else{
					if (Resp.getCode() == JsonReturnCode.not_login) {
						handler.sendEmptyMessage(NOT_LOGIN);
					} else{
						handler.sendEmptyMessage(SYNC_FAIL);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				handler.sendEmptyMessage(SYNC_FAIL);
			}
		}
	}

	@Override
	public String call() {
		try{
			if (request == SYNC_CID_REQUEST) {
				try {
					return UserHttpApi.getInstance().synccid(sessionid, mUserInfo.ts);
				} catch (Exception e) {
					e.printStackTrace();
					MobclickAgent.onEvent(context, "synccid");
					return "";
				}
			} else {
				return "";
			}
		}catch(Exception e){
			return "";
		}
	}
	
	private void setCidInfo(List<CameraInfoBean> listBean,int code){
		for (CameraInfoBean bean : listBean) {
			String cid = bean.getCid();
			String cuser = bean.getCuser();
			String cpasswd = bean.getCpasswd();
			int bind_flag = bean.getBind().getBind_flag();
			int bind_self = bean.getBind().isBind_by_self()?1:0;
			int cloud_type = bean.getCloud().getPtype();
			int cloud_poid = bean.getCloud().getPoid();
			String cloud_expireaday = bean.getCloud().getExpireday();
			String cloud_renewexpireday = bean.getCloud().getRenewexpireday();
			//cIdOperation.addCid(cuser, cpasswd, cid, bind_flag, bind_self, cloud_type, cloud_poid, cloud_expireaday,cloud_renewexpireday);
		}
	}
}
