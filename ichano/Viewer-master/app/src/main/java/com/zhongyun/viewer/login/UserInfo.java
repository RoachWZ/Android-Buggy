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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.zhongyun.viewer.utils.PrefUtils;
import com.zhongyun.viewer.utils.StringUtils;

public class UserInfo {

	public static final String CLIENT_CID = "CLIENT_CID";
	public static final String IS_LOGIN = "IS_LOGIN";
	public static final String USER_NAME = "USER_NAME";
	public static final String USER_SESSION_ID = "USER_SESSION_ID";
	public static final String USER_RECOMMAND_URL = "USER_RECOMMAND_URL";
	public static final String TS = "TS";
	
	private static UserInfo mUserInfo;
	private Context mContext;
	public boolean isLogin;
	public long clientCid = 0;
	public String name;
	public String sessionId;
	public String recommandURL;
	public String ts = "";
	
	private UserInfo(Context context){
		mContext = context;
		String str = PrefUtils.getString(mContext, CLIENT_CID);
		if(!StringUtils.isEmpty(str)){
			clientCid = Long.parseLong(str);
		}
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		isLogin = sp.getBoolean(IS_LOGIN, false);
		name = sp.getString(USER_NAME, "");
		sessionId = sp.getString(USER_SESSION_ID, "");
		recommandURL = sp.getString(USER_RECOMMAND_URL, "");
		ts = sp.getString(TS, "");
	}
	
	public static UserInfo getUserInfo(Context context){
		if(null == mUserInfo){
			mUserInfo = new UserInfo(context);
		}
		return mUserInfo;
	}
	
	public void saveClientCid(long cid){
		if(cid > 0){
			if(cid != clientCid)
				clientCid = cid;
				PrefUtils.putString(mContext, CLIENT_CID, String.valueOf(cid));
		}
	}
	
	public void setLoginInfo(boolean isLogin, String name, String sessionId, String recommandURL){
		this.isLogin = isLogin;
		this.name = name;
		this.sessionId = sessionId;
		this.recommandURL = recommandURL;
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		Editor editor = sp.edit();
		editor.putBoolean(IS_LOGIN, isLogin);
		editor.putString(USER_NAME, name);
		editor.putString(USER_SESSION_ID, sessionId);
		editor.putString(USER_RECOMMAND_URL, recommandURL);
		editor.commit();
	}
	
	public void setTS(String ts){
		if(!ts.equals(this.ts)){
			this.ts = ts;
			PrefUtils.putString(mContext, TS, ts);
		}
	}
}
