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
import com.zhongyun.viewer.http.JsonReturnCode;
import com.zhongyun.viewer.http.JsonSerializer;
import com.zhongyun.viewer.http.UserHttpApi;
import com.zhongyun.viewer.http.bean.JsonReturn;
import com.zhongyun.viewer.login.UserInfo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
/**
 * 
 * @author handler返回值
 *	0.成功 1.失败 2，session失效，3.qrcode失效
 */
public class AddCidHandler implements Callable<String>, CallbackMessage<String>{
	Context context;
	String sessionid;
	int request;
	Handler handler;
	String cidStr,userStr,passStr;
	int cidFlag;
	private UserInfo mUserInfo;
	
	public static final int ADD_CID_REQUEST = 0;
	public static final int GET_CID_REQUEST = 1;
	
	public static final int ADD_CID_SUCCESS = 2000;
	public static final int ADD_CID_FAIL = 2001;
	public static final int NOT_LOGIN = 2002;
	
	public AddCidHandler(Context context,Handler handler){
		this.context = context;
		this.handler = handler;
		mUserInfo = UserInfo.getUserInfo(context);
	}
	
	public void setRequestValue(String cidStr,String userStr,String PassStr,int cidFlag){
		this.cidStr = cidStr;
		this.userStr= userStr;
		this.passStr = PassStr;
		this.cidFlag = cidFlag;
		sessionid = mUserInfo.sessionId;
	}
	
	public void doThing(int request){
		this.request = request;
		AsyncUtil.doAsync(request, context, this, this);
	}
	
	@Override
	public String call(){
		try{
			if(request == GET_CID_REQUEST){
				return UserHttpApi.getInstance().getCidByCode(cidStr);
			}else if(request == ADD_CID_REQUEST){
				return UserHttpApi.getInstance().addCid(sessionid,cidStr,userStr, passStr);
			}else if(request == -1){
				//return UserHttpApi.getInstance().cidAddCheck(cidStr);
			}
		}catch(Exception e){
			return null;
		}
		return null;
	}
	
	@Override
	public void onComplete(int requestid, String pCallbackValue) {
		if(requestid == ADD_CID_REQUEST){
			doResult(pCallbackValue);
		}else if(requestid == -1){
			try {
				JsonReturn Resp = JsonSerializer.deSerialize(pCallbackValue, JsonReturn.class);
				if (Resp.getCode() == JsonReturnCode.code_succ) {
					CameraInfoBean avaBean = new CameraInfoBean(cidStr,userStr,passStr);
					handler.sendEmptyMessage(0);
				}else if(Resp.getCode() == JsonReturnCode.code_fail){
					handler.sendEmptyMessage(1);
				}
			}catch(Exception e){
				e.printStackTrace();
				handler.sendEmptyMessage(1);
			}
		}else if(requestid == GET_CID_REQUEST){
			try {
				JsonReturn Resp = JsonSerializer.deSerialize(pCallbackValue, JsonReturn.class);
				if (Resp.getCode() == JsonReturnCode.code_succ) {
					GetCidByCodeResponse cid =  JsonSerializer.deSerialize(pCallbackValue, GetCidByCodeResponse.class);
					cidStr = cid.getDesc().getCid();
					if(cidFlag == 3){
						Message msg = handler.obtainMessage();
						msg.what = 4;
						msg.obj = cidStr;
						handler.sendMessage(msg);
					}else if(cidFlag == 4){
						Message message = handler.obtainMessage();
						message.what = 5;
						message.obj = cidStr;
						handler.sendMessage(message);
					}else{
						doThing(ADD_CID_REQUEST);
					}
				}else if(Resp.getCode() == JsonReturnCode.nothing){
					handler.sendEmptyMessage(ADD_CID_FAIL);
				}else{
					handler.sendEmptyMessage(ADD_CID_FAIL);
				}
			} catch (Exception e) {
				e.printStackTrace();
				handler.sendEmptyMessage(ADD_CID_FAIL);
			}
		}
	}

	
	private void doResult(String result){
		try {
			JsonReturn Resp = JsonSerializer.deSerialize(result, JsonReturn.class);
			if (Resp.getCode() == JsonReturnCode.code_succ || Resp.getCode() == JsonReturnCode.url_userd) {
				AddCidResponse addCidResp = JsonSerializer.deSerialize(result, AddCidResponse.class);
				mUserInfo.setTS(addCidResp.getDesc().getTs());
				if(cidFlag != -1){
					CameraInfoBean avaBean = new CameraInfoBean(addCidResp.getDesc().getCidobj().getCid(),addCidResp.getDesc().getCidobj().getCuser(),addCidResp.getDesc().getCidobj().getCpasswd());
//					Binds binds = new Binds(addCidResp.getDesc().getCidobj().getBind().getBind_flag(),addCidResp.getDesc().getCidobj().getBind().isBind_by_self());
//					avaBean.setBind(binds);
//					Cloud cloud = new Cloud(addCidResp.getDesc().getCidobj().getCloud().getPtype(), addCidResp.getDesc().getCidobj().getCloud().getPoid(), addCidResp.getDesc().getCidobj().getCloud().getExpireday(),addCidResp.getDesc().getCidobj().getCloud().getRenewexpireday());
//					avaBean.setCloud(cloud);
					
					Message message = handler.obtainMessage();
					message.what = ADD_CID_SUCCESS;
					message.obj = avaBean;
					handler.sendMessage(message);
				}
			}else if(Resp.getCode() == JsonReturnCode.not_login){
				handler.sendEmptyMessage(NOT_LOGIN);
			}else if(Resp.getCode() == JsonReturnCode.cid_num_invalid){
				handler.sendEmptyMessage(ADD_CID_FAIL);
			}else {
				handler.sendEmptyMessage(ADD_CID_FAIL);
			}
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendEmptyMessage(ADD_CID_FAIL);
		}
	}
}
