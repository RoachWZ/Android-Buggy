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

import com.zhongyun.viewer.async.AsyncUtil;
import com.zhongyun.viewer.async.Callable;
import com.zhongyun.viewer.async.CallbackMessage;
import com.zhongyun.viewer.http.JsonReturnCode;
import com.zhongyun.viewer.http.JsonSerializer;
import com.zhongyun.viewer.http.UserHttpApi;
import com.zhongyun.viewer.http.bean.JsonReturn;
import com.zhongyun.viewer.utils.PrefUtils;
import com.zhongyun.viewer.utils.StringUtils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class UserLoginHandler implements Callable<String>, CallbackMessage<String>{
	
	public static final int THIRD_LOGIN = 1;
	Context context;
	int request;
	Handler handler;
	String email,pwd;
	int cidFlag;
	boolean isResigter = false;
	private UserInfo mUserInfo;
	public static final int LOGIN_SUCCESS = 0;
	public static final int LOGIN_FAIL = 1;
	
	public UserLoginHandler(Context context, Handler handler){
		this.context = context;
		this.handler = handler;
		mUserInfo = UserInfo.getUserInfo(context.getApplicationContext());
	}
	
	public void setRequestValue(String email,String pwd){
		this.email = email;
		this.pwd= pwd;
	}
	
	public void doThing(int request){
		this.request = request;
		AsyncUtil.doAsync(request, context, this, this);
	}
	
	@Override
	public void onComplete(int requestid, String pCallbackValue) {
		if (StringUtils.isEmpty(pCallbackValue)) {
			handler.sendEmptyMessage(1);
			return;
		}
		if (requestid == THIRD_LOGIN) {
			try {
				JsonReturn Resp = JsonSerializer.deSerialize(pCallbackValue, JsonReturn.class);
				if (Resp.getCode() == JsonReturnCode.code_succ) {
					UserLoginResponse userResp = JsonSerializer.deSerialize(pCallbackValue, UserLoginResponse.class);
					if (userResp.getCode() == JsonReturnCode.code_succ) {
						mUserInfo.setLoginInfo(true, nickName, userResp.getDesc().getSessionid(), userResp.getDesc().getRecommandurl());
						handler.sendEmptyMessage(0);
					}
				}else {
					if (Resp.getCode() == JsonReturnCode.code_invaid) {
						handler.sendEmptyMessage(1);
					} else if (Resp.getCode() == JsonReturnCode.code_fail) {
						handler.sendEmptyMessage(1);
					} else if (Resp.getCode() == JsonReturnCode.not_login) {
						handler.sendEmptyMessage(1003);
					} else if (Resp.getCode() == JsonReturnCode.account_nothing) {
						handler.sendEmptyMessage(1005);
					} else if(Resp.getCode() == JsonReturnCode.not_username){
						handler.sendEmptyMessage(1020);
					} else {
						handler.sendEmptyMessage(1);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				handler.sendEmptyMessage(1);
			}
		}
	}
	
	String uid, thirdSymbol, nickName;
	int sex;
	public void setOtherLoginData(String uid, String thirdSymbol, String nickName, int sex){
		this.uid = uid;
		this.thirdSymbol = thirdSymbol;
		this.nickName = nickName;
		this.sex = sex;
	}
	
	@Override
	public String call() throws Exception {
		if(mUserInfo.clientCid <= 0){
			return null;
		}else{
			if(request == THIRD_LOGIN){
			    return UserHttpApi.getInstance().loginWithOther(uid, "default", thirdSymbol, nickName, sex, String.valueOf(mUserInfo.clientCid),1);
			}
		}
		return null;
	}
}
