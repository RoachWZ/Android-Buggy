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
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

public class ThirdLoginApi implements Callback {
	private static final int MSG_AUTH_CANCEL = 1;
	private static final int MSG_AUTH_ERROR= 2;
	private static final int MSG_AUTH_COMPLETE = 3;

	private OnLoginListener loginListener;
	private String platform;
	private Context context;
	private Handler handler;

	public ThirdLoginApi(Context context) {
		this.context = context.getApplicationContext();
		//初始化SDK
		ShareSDK.initSDK(context);
		handler = new Handler(Looper.getMainLooper(), this);
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public void setOnLoginListener(OnLoginListener login){
		this.loginListener=login;
	}

	public void login() {
		if (platform == null) {
			return;
		}

		Platform plat = ShareSDK.getPlatform(platform);
		if (plat == null) {
			return;
		}

		if (plat.isValid()) {
			plat.removeAccount(true);
			return;
		}

		//使用SSO授权，通过客户单授权
		plat.SSOSetting(false);
		plat.setPlatformActionListener(new PlatformActionListener() {
			public void onComplete(Platform plat, int action,HashMap<String, Object> res) {
				if (action == Platform.ACTION_USER_INFOR) {
					Message msg = new Message();
					msg.what = MSG_AUTH_COMPLETE;
					msg.arg2 = action;
					msg.obj =  new Object[] {plat.getName(), res};
					handler.sendMessage(msg);
				}
			}

			public void onError(Platform plat, int action, Throwable t) {
				if (action == Platform.ACTION_USER_INFOR) {
					Message msg = new Message();
					msg.what = MSG_AUTH_ERROR;
					msg.arg2 = action;
					msg.obj = t;
					handler.sendMessage(msg);
				}
				t.printStackTrace();
			}

			public void onCancel(Platform plat, int action) {
				if (action == Platform.ACTION_USER_INFOR) {
					Message msg = new Message();
					msg.what = MSG_AUTH_CANCEL;
					msg.arg2 = action;
					msg.obj = plat;
					handler.sendMessage(msg);
				}
			}
		});
		plat.showUser(null);
	}

	/**处理操作结果*/
	public boolean handleMessage(Message msg) {
		switch(msg.what) {
			case MSG_AUTH_CANCEL: {
				// 取消
				Toast.makeText(context, "canceled", Toast.LENGTH_SHORT).show();
			} break;
			case MSG_AUTH_ERROR: {
				// 失败
				Throwable t = (Throwable) msg.obj;
				String text = "caught error: " + t.getMessage();
				Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
				t.printStackTrace();
			} break;
			case MSG_AUTH_COMPLETE: {
				// 成功
				Object[] objs = (Object[]) msg.obj;
				String plat = (String) objs[0];
				@SuppressWarnings("unchecked")
				HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
				if(loginListener!= null) loginListener.onLogin(plat, res);
			} break;
		}
		return false;
	}
	
	
	/**
	 * 第三方登录操作过程中会回调这个接口中的方法，不同方法衔接第
	 * 三方登录与用户应用登录/注册的逻辑，故使用第三方登录时一定要实
	 * 现本接口的不同方法，否则第三方登录是没有意义的。
	 */
	public interface OnLoginListener {

		/**
		 * 登录完成调用此接口，返回登录者在第三方社交平台上的用户数据。实现此方法时要根据
		 * res参数中的用户资料，结合开发者应用的用户系统数据，判断此登录者是否需要先注册，
		 * 如果需要注册，则返回true
		 */
		public boolean onLogin(String platform, HashMap<String, Object> res);

		/**
		 * 完成注册信息填写后此方法将被调用，开发者可于此处根据info参数判断注册者填写的信
		 * 息是否合法。若合法，则调用自身应用的注册服务进入注册流程并返回true以关闭注册页
		 * 面，否则注册页将不会关闭，用户需要修改注册信息
		 */
		//public boolean onRegister(UserInfo info);

	}
}
