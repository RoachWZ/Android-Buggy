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

import com.zhongyun.viewer.utils.AppUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Window;

public class BaseActivity extends Activity{

	protected ProgressDialog dialog = null;
	private boolean isActivityVisible = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		AppUtils.setStatusBarTransparent(this, getResources().getColor(R.color.title_red));
	}
	
	public void showProgressDialog(int stringId){
		if(isActivityVisible){
			if(null == dialog){
				dialog = new ProgressDialog(this);
				dialog.setCanceledOnTouchOutside(false);
			}
			dialog.setMessage(getString(stringId));
			dialog.show();
		}
	}
	
	public void showProgressDialogs(){
		if(isActivityVisible){
			if(null == dialog){
				dialog = new ProgressDialog(this);
				dialog.setCanceledOnTouchOutside(false);
			}
			dialog.show();
		}
	}
	
	public void dismissProgressDialog(){
		dialog.dismiss();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		isActivityVisible = true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		isActivityVisible = false;
	}
	
}
