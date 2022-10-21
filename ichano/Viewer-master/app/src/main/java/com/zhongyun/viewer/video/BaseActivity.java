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
package com.zhongyun.viewer.video;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.ichano.rvs.viewer.constant.RvsSessionState;
import com.zhongyun.viewer.R;
import com.zhongyun.viewer.utils.Constants;
import com.zhongyun.viewer.utils.NetWorkUtil;

public class BaseActivity extends Activity implements OnClickListener {
	public static String TAG = "";
	public static final int HIDDEN_NONE = 0, HIDDEN_BACK = 1, HIDDEN_OPT = 2;
	public static final int ERRORRESULT = 4;
	protected boolean isDestory = false,isExit = true,isShowUpdateInfo = false,isShowNetWorkDialog = true;
	public SharedPreferences userInfo,agent_pref;
	protected ProgressDialog dialog = null;
	protected boolean isShowConnect = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TAG = this.getClass().getSimpleName();
		super.onCreate(savedInstanceState);
		userInfo = getSharedPreferences("", MODE_PRIVATE);
		agent_pref = getSharedPreferences("AgentPrefs", 0);
		if(null != savedInstanceState){
			finish();
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.CONNECTIVITY_CHANGE_ACTION);
		filter.setPriority(1000);
		registerReceiver(broadReceiver, filter);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	public void progressDialog(int context){
		if(null == dialog){
			dialog = new ProgressDialog(this);
			dialog.setCanceledOnTouchOutside(false);
		}
		dialog.setMessage(getString(context));
		dialog.show();
	}
	public void progressDialogs(){
		if(null == dialog){
			dialog = new ProgressDialog(this);
			dialog.setCanceledOnTouchOutside(false);
		}
		dialog.show();
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putBoolean("isaction", false);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadReceiver);
		isDestory = true;
		if(null != dialog){
			dialog.dismiss();
			dialog = null;
		}
	}
	
	public void customTitleBar(int layoutId, int titleId, int backId, int optId, int hiddenType) {
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, layoutId);
		TextView titleText = (TextView) findViewById(R.id.title);
		titleText.setText(titleId);
		LinearLayout back_linlayout = (LinearLayout) findViewById(R.id.back_linlayout);
		LinearLayout opt_linlayout = (LinearLayout) findViewById(R.id.opt_linlayout);
		TextView optBtn = (TextView) findViewById(R.id.opt);
		if (hiddenType == HIDDEN_BACK) {
			back_linlayout.setVisibility(View.GONE);
			optBtn.setText(optId);
		} else if (hiddenType == HIDDEN_OPT) {
			opt_linlayout.setVisibility(View.GONE);
		} else if (hiddenType == (HIDDEN_OPT | HIDDEN_BACK)) {
			back_linlayout.setVisibility(View.GONE);
			opt_linlayout.setVisibility(View.GONE);
		}else{
			optBtn.setText(optId);
		}
		opt_linlayout.setOnClickListener(this);
		back_linlayout.setOnClickListener(this);
	}
	
	
	public void customTitleBar(int layoutId, String title, int backId, int optId, int hiddenType) {
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, layoutId);
		TextView titleText = (TextView) findViewById(R.id.title);
		titleText.setText(title);
		LinearLayout back_linlayout = (LinearLayout) findViewById(R.id.back_linlayout);
		LinearLayout opt_linlayout = (LinearLayout) findViewById(R.id.opt_linlayout);
		TextView optBtn = (TextView) findViewById(R.id.opt);
		
		if (hiddenType == HIDDEN_BACK) {
			back_linlayout.setVisibility(View.GONE);
			optBtn.setText(optId);
		} else if (hiddenType == HIDDEN_OPT) {
			opt_linlayout.setVisibility(View.GONE);
		} else if (hiddenType == (HIDDEN_OPT | HIDDEN_BACK)) {
			back_linlayout.setVisibility(View.GONE);
			opt_linlayout.setVisibility(View.GONE);
		}else{
			optBtn.setText(optId);
		}
		opt_linlayout.setOnClickListener(this);
		back_linlayout.setOnClickListener(this);
	}
	
//	public void noTitleBarIcon(int layoutId, int titleId, int optBackId, int optDrawableId) {
//		TextView titleText = (TextView) findViewById(R.id.title);
//		titleText.setText(titleId);
//		ImageButton optBtn = (ImageButton) findViewById(R.id.opt);
//		ImageButton backBtn = (ImageButton) findViewById(R.id.back);
//		if(optDrawableId == -1){
//			optBtn.setVisibility(View.GONE);
//		} else {
//			if (optBtn != null) {
//				optBtn.setImageResource(optDrawableId);
//				optBtn.setOnClickListener(this);
//			}
//		}
//
//		if(optBackId == -1){
//			backBtn.setVisibility(View.GONE);
//		} else {
//			if (backBtn != null) {
//				backBtn.setOnClickListener(this);
//				backBtn.setImageResource(optBackId);
//			}
//		}
//	}
//
//	public void customTitleBar(int layoutId, int titleId, int optId) {
//		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, layoutId);
//		TextView titleText = (TextView) findViewById(R.id.title);
//		titleText.setText(titleId);
//		Button optBtn = (Button) findViewById(R.id.opt);
//		Button backBtn = (Button) findViewById(R.id.back);
//		if (backBtn != null) {
//			backBtn.setOnClickListener(this);
//		}
//		optBtn.setOnClickListener(this);
//		optBtn.setText(optId);
//		titleText.setText(titleId);
//	}
//	
//	public void customTitleBar2(int layoutId, int titleId, int optId) {
//		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, layoutId);
//		TextView titleText = (TextView) findViewById(R.id.title);
//		titleText.setText(titleId);
//		ImageButton optBtn = (ImageButton) findViewById(R.id.opt);
//		Button backBtn = (Button) findViewById(R.id.back);
//		if (backBtn != null) {
//			backBtn.setOnClickListener(this);
//		}
//		optBtn.setOnClickListener(this);
//		titleText.setText(titleId);
//	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.back_linlayout) {
			this.finish();
		}
	}

	public void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	public void showLongToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	public void showToast(int messageId) {
		Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
	}
	int lastType = -1;
	private final BroadcastReceiver broadReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.CONNECTIVITY_CHANGE_ACTION)) {
				int type = NetWorkUtil.netWorkIsAvailable(BaseActivity.this);
				if (type == 0 && lastType != type && isShowNetWorkDialog) {
					openDialogMessage(R.string.alert_title,R.string.network_disconnect,false,true);
				}
				lastType = type;
			}
//			else if(intent.getAction().equals(Constants.AVS_SESSION_STATE)){
//				if(CommonUtil.notEmpty(Constants.connectCid)){
//					if(isShowConnect && (Constants.connectCid).equals(intent.getStringExtra("cid"))){
//						if(intent.getIntExtra("connected", 0) != RvsSessionState.CONNECTED.intValue()){
//							openDialogMessage(R.string.alert_title,R.string.warnning_tunnel_disconnected,false,true);
//						}
//					}
//				}
//			}
		}
	};
	
	public void openDialogMessage(int title,int message){
		final Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				builder.create().dismiss();
			}
		});
		builder.show();
	}
	public void openDialogMessage(int title,String message){
		final Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				builder.create().dismiss();
			}
		});
		builder.show();
	}
	
	public void openDialogMessage(int title,int message,boolean isCancelable){
		final Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setCancelable(isCancelable);
		builder.setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				builder.create().dismiss();
				finish();
			}
		});
		builder.show();
	}
	public void openDialogMessage(int title,int message,boolean isCancelable,boolean killAllActivity){
		final Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setCancelable(isCancelable);
		builder.setPositiveButton(R.string.confirm_btn, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				builder.create().dismiss();
				if(isExit){
//					application.exit();
				}
			}
		});
		builder.show();
	}
	public void openDialogMessageTwoButton(int title,int message,boolean isCancelable){
		final Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setCancelable(isCancelable);
		builder.setNeutralButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				builder.create().dismiss();
			}
		});
		builder.setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				builder.create().dismiss();
				finish();
			}
		});
		builder.show();
	}

}
