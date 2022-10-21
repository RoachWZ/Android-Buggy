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

import com.umeng.analytics.MobclickAgent;
import com.zhongyun.avs.utils.PrefUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;


public class LoadingActivity extends Activity{
	
	private static final int DELAY = 1000;
	private Handler mHandler = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				boolean haveShowGuide = PrefUtils.getBoolean(LoadingActivity.this, PrefUtils.HAVE_SHOW_GUIDE);
				Intent intent = new Intent();
				if(haveShowGuide){
					intent.setClass(getApplicationContext(), AvsActivity.class);
				}else{
					intent.setClass(getApplicationContext(), GuideActivity.class);
					intent.putExtra(GuideActivity.START_AVS_ACTIVITY, true);
				}
				startActivity(intent);
				finish();
			}
		}, DELAY);
		
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
	public void onBackPressed() {
		//do not let user exit;
		//super.onBackPressed();
	}
}