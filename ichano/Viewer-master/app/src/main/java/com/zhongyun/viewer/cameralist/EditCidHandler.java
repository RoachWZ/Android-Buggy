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

import com.zhongyun.viewer.async.AsyncUtil;
import com.zhongyun.viewer.async.Callable;
import com.zhongyun.viewer.async.CallbackMessage;
import com.zhongyun.viewer.db.CameraInfo;
import com.zhongyun.viewer.http.JsonReturnCode;
import com.zhongyun.viewer.http.JsonSerializer;
import com.zhongyun.viewer.http.UserHttpApi;
import com.zhongyun.viewer.http.bean.JsonReturn;
import com.zhongyun.viewer.login.UserInfo;
import com.zhongyun.viewer.utils.StringUtils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class EditCidHandler implements Callable<String>, CallbackMessage<String>{
	Context context;
	String sessionid;
	int request;
	Handler handler;
	int cidFlag;
	private UserInfo mUserInfo;
	private CameraInfo mCameraInfo;
	public static final int DELETE_CID = 0;
	public static final int UPDATE_CID = 1;
	public static final int EDIT_SUCCESS = 3000;
	public static final int EDIT_FAIL = 3001;
	public static final int NOT_LOGIN = 3002;
	
	
	public EditCidHandler(Context context,Handler handler){
		this.context = context;
		this.handler = handler;
		mUserInfo = UserInfo.getUserInfo(context);
	}
	
	public void setRequestValue(CameraInfo info){
		mCameraInfo = info;
		sessionid = mUserInfo.sessionId;
	}
	
	public void doThing(int request){
		this.request = request;
		AsyncUtil.doAsync(request, context, this, this);
	}
	@Override
	public void onComplete(int requestid, String pCallbackValue) {	
		try {
			JsonReturn Resp = JsonSerializer.deSerialize(pCallbackValue, JsonReturn.class);
			if (Resp.getCode() == JsonReturnCode.code_succ || Resp.getCode() == JsonReturnCode.url_userd) {
				EditCidResponse editCidResp = JsonSerializer.deSerialize(pCallbackValue, EditCidResponse.class);
				if(requestid==DELETE_CID || requestid==UPDATE_CID){
					mUserInfo.setTS(editCidResp.getDesc().getTs());
				}
				Message message = handler.obtainMessage();
				message.what = EDIT_SUCCESS;
				message.obj = mCameraInfo;
				handler.sendMessage(message);
			}else if(Resp.getCode() == JsonReturnCode.not_login){
				handler.sendEmptyMessage(NOT_LOGIN);
			}else if(Resp.getCode() == JsonReturnCode.delete_userd){
				handler.sendEmptyMessage(EDIT_SUCCESS);
			} else {
				handler.sendEmptyMessage(EDIT_FAIL);
			}
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendEmptyMessage(EDIT_FAIL);
		}
	}

	@Override
	public String call() throws Exception {
		if (request == DELETE_CID){
			return UserHttpApi.getInstance().delCid(String.valueOf(mCameraInfo.getCid()), sessionid);
		}else if(request==UPDATE_CID){
			return UserHttpApi.getInstance().cid_update(sessionid,String.valueOf(mCameraInfo.getCid()), mCameraInfo.getCameraUser(), mCameraInfo.getCameraPwd());
		}
		return null;
	}
	
	public void deleteCid(){
		if(!StringUtils.isEmpty(sessionid)){
			doThing(DELETE_CID);
		}
	}

	public void editCid(){
		if(!StringUtils.isEmpty(sessionid)){
			doThing(UPDATE_CID);
		}
	}
	
}
