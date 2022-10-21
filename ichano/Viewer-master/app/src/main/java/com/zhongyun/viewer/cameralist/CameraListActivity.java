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
import java.util.Locale;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.ichano.rvs.viewer.Viewer;
import com.ichano.rvs.viewer.bean.StreamerInfo;
import com.ichano.rvs.viewer.constant.StreamerPresenceState;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;
import com.zhongyun.viewer.BaseActivity;
import com.zhongyun.viewer.GuideActivity;
import com.zhongyun.viewer.MyViewerHelper;
import com.zhongyun.viewer.R;
import com.zhongyun.viewer.WatchActivity;
import com.zhongyun.viewer.db.CameraInfo;
import com.zhongyun.viewer.db.CameraInfoManager;
import com.zhongyun.viewer.login.LoginActivity;
import com.zhongyun.viewer.login.UserInfo;
import com.zhongyun.viewer.setting.CameraSettingsTabActivity;
import com.zhongyun.viewer.utils.AppUtils;
import com.zhongyun.viewer.utils.Constants;
import com.zhongyun.viewer.utils.ImageDownloader;
import com.zhongyun.viewer.utils.StringUtils;
import com.zhongyun.viewer.video.RecordingVideoTypeList;
import com.zhongyun.zxing.client.android.Intents;
import com.zhongyun.zxing.journeyapps.barcodescanner.CaptureActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CameraListActivity extends BaseActivity
	implements MyViewerHelper.CameraStateListener, View.OnClickListener, AdapterView.OnItemClickListener, OnRefreshListener<ListView>{

	private static final String TAG = CameraListActivity.class.getSimpleName();
	private final static int SCANNIN_GREQUEST_CODE = 1;
	private final static String DEFAULT_USER = "admin";
	private final static int DELAY_TIME = 1000;

	private static final String DISCLAIMER_URL_CN = "file:///android_asset/iChanoPrivacyPolicyCN.html";
	private static final String DISCLAIMER_URL_EN = "file:///android_asset/iChanoPrivacyPolicyEN.html";
	private boolean mShowChinese;
	
	private Viewer mViewer;
	private MyViewerHelper mMyViewerHelper;
	private Bitmap mCameraDefaulThumb;
	private List<CameraInfo> mCameraInfos;
	private CameraInfoManager mCameraInfoManager;
	private CameraListAdapter mCameraListAdapter;
	private PullToRefreshListView mCameraListView;
	private DrawerLayout mUserLayout;
//	private Toolbar mToolbar;
	private LayoutInflater mLayoutInflater;
	private Dialog mAboutDialog;
	private Dialog mDisclaimerDialog;
	private Dialog mAddCameraDlg;
	private Dialog mExitDialog;
	private Dialog mShowAddLayoutDialog;
	private boolean isExitWithLogout = false;
	
	private CameraListHandler mCameraListHandler;
	private AddCidHandler mAddCidHandler;
	private EditCidHandler mEditCidHandler;
	
	private TextView titlebar_back_text;
	private ImageView titlebar_opt_image;
	private LinearLayout add_layout;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case CameraListHandler.SYNC_SUCCESS:
				List<CameraInfoBean> list = (List<CameraInfoBean>) msg.obj;
				if(null != list){
					mCameraInfoManager.deleteAll();
					mMyViewerHelper.removeAllCameraInfos();
					if(list.size() > 0){
						for(CameraInfoBean bean : list){
							addStreamer(Long.parseLong(bean.getCid()), bean.getCuser(), bean.getCpasswd());
						}
					}
				}
				mCameraListView.onRefreshComplete();
				Log.e(TAG, "syc cid success.");
				break;
			case CameraListHandler.SYNC_FAIL:
				mCameraListView.onRefreshComplete();
				Log.e(TAG, "syc cid fail.");
				break;
			case AddCidHandler.ADD_CID_SUCCESS:
				CameraInfoBean bean = (CameraInfoBean) msg.obj;
				addStreamer(Long.parseLong(bean.getCid()), bean.getCuser(), bean.getCpasswd());
				break;
			case EditCidHandler.EDIT_SUCCESS:
				CameraInfo info = (CameraInfo) msg.obj;
				removeStreamer(info.getCid());
				mCameraInfoManager.delete(info);
				mMyViewerHelper.removeCameraInfo(info);
				mCameraListAdapter.notifyDataSetChanged();
				
				break;
				default:
					break;
			}
		}
	};
	private UserInfo mUserInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_list);
		mUserInfo = UserInfo.getUserInfo(this);
		mLayoutInflater = LayoutInflater.from(this);
		mUserLayout = (DrawerLayout) findViewById(R.id.user_layout);
		mCameraListView = (PullToRefreshListView) findViewById(R.id.cameraList);
		mCameraListView.setOnRefreshListener(this);
		
		findViewById(R.id.help).setOnClickListener(this);
		findViewById(R.id.feedback).setOnClickListener(this);
		findViewById(R.id.about).setOnClickListener(this);
		findViewById(R.id.disclaimer).setOnClickListener(this);
		findViewById(R.id.business).setOnClickListener(this);
		Button logout = (Button) findViewById(R.id.logout);
		logout.setOnClickListener(this);
		if(!mUserInfo.isLogin) logout.setVisibility(View.INVISIBLE);
		
		TextView userNameView = (TextView) findViewById(R.id.user_name);
		String name = getResources().getString(R.string.not_login);
		userNameView.setText(StringUtils.isEmpty(mUserInfo.name) ? name : mUserInfo.name);
//		mToolbar = (Toolbar) findViewById(R.id.toolbar);
//		mToolbar.setTitle(R.string.app_name);
//		setSupportActionBar(mToolbar);
//		mToolbar.setOnMenuItemClickListener(this);
//		mToolbar.setNavigationIcon(R.drawable.navigation_icon);
//		mToolbar.setNavigationOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				if(!mUserLayout.isDrawerOpen(Gravity.LEFT))
//					mUserLayout.openDrawer(Gravity.LEFT);
//			}
//		});
		
		mViewer = Viewer.getViewer();
		mMyViewerHelper = MyViewerHelper.getInstance(getApplicationContext());
		mMyViewerHelper.addCameraStateListener(this);
		mCameraDefaulThumb = BitmapFactory.decodeResource(getResources(), R.drawable.avs_type_android);
		
		mCameraInfoManager = new CameraInfoManager(this);
		mCameraInfos = mMyViewerHelper.getAllCameraInfos();
		for (CameraInfo info : mCameraInfos) {
			addStreamer(info.getCid(), info.getCameraUser(), info.getCameraPwd());
		}
		mCameraListAdapter = new CameraListAdapter(this, mCameraInfos);
		mCameraListView.setAdapter(mCameraListAdapter);
		mCameraListView.setOnItemClickListener(this);
//        mCameraListView.setOnScrollChangeListener(new OnScrollChangeListener() {
//            
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY,
//                    int oldScrollX, int oldScrollY) {
//                mCameraListAdapter.closeAllItems();
//            }
//        });

		mShowChinese = "zh".equals(Locale.getDefault().getLanguage().toLowerCase());
		
		mCameraListHandler = new CameraListHandler(this, mHandler);
		mCameraListHandler.doThing(CameraListHandler.SYNC_CID_REQUEST);
		mAddCidHandler = new AddCidHandler(this, mHandler);
		mEditCidHandler = new EditCidHandler(this, mHandler);
		
		//update
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
		findViewById(R.id.back_linlayout).setOnClickListener(this);
		findViewById(R.id.titlebar_back_image).setBackgroundResource(R.drawable.navigation_icon);
		titlebar_back_text = (TextView) findViewById(R.id.titlebar_back_text);
		titlebar_back_text.setText(R.string.app_name);
		titlebar_opt_image = (ImageView) findViewById(R.id.titlebar_opt_image);
		titlebar_opt_image.setBackgroundResource(R.drawable.add_icon);//(getResources().getDrawable(R.drawable.add_icon));
		findViewById(R.id.opt_linlayout).setOnClickListener(this);
		add_layout = (LinearLayout) findViewById(R.id.add_layout);
//		findViewById(R.id.add_cid).setOnClickListener(this);
//		findViewById(R.id.add_cid_by_qr).setOnClickListener(this);
	}
	
	// Add a streamer
	public void addStreamer(long streamerCid, String user, String pass){
		boolean ret = mViewer.connectStreamer(streamerCid, user, pass);
		CameraInfo info = mMyViewerHelper.getCameraInfo(streamerCid);
		if(ret) {
			if(null == info){
				StreamerInfo  sinfo = mViewer.getStreamerInfoMgr().getStreamerInfo(streamerCid);
				info = new CameraInfo();
				info.setCid(streamerCid);
				String name = sinfo.getDeviceName();
				info.setCameraName((null == name) ? "" : name);
				info.setCameraUser(user);
				info.setCameraPwd(pass);
				info.setCameraThumb(mCameraDefaulThumb);
				info.setIsOnline(false);
				info.setPwdIsRight(true);
				info.setOS(sinfo.getOsVersion());
				mCameraInfoManager.addCameraInfo(info);
				mMyViewerHelper.addCameraInfo(info);
				mCameraListAdapter.notifyDataSetChanged();
			}
		}else{
			if(null != info){
				info.setPwdIsRight(false);
				mCameraListAdapter.notifyDataSetChanged();
			}
		}
	}
	
	// delete a streamer
	public void removeStreamer(long streamerCid){
		mViewer.disconnectStreamer(streamerCid);
	}
	
	@Override
	public void onCameraConnectionChange(long streamerCID, boolean connected) {
		mCameraListAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onCameraStateChange(long streamerCid, StreamerPresenceState state) {
		mCameraListAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		case R.id.help:
			Intent guideIntent = new Intent();
			guideIntent.setClass(getApplicationContext(), GuideActivity.class);
			guideIntent.putExtra(GuideActivity.START_AVS_ACTIVITY, false);
			startActivity(guideIntent);
			break;
		case R.id.feedback:
			FeedbackAgent agent = new FeedbackAgent(this);
			agent.startFeedbackActivity();
			break;
		case R.id.about:
			showAboutDialog();
			break;
		case R.id.disclaimer:
			showDisclaimerDlg();
			break;
		case R.id.business:
			try{
				Intent data=new Intent(Intent.ACTION_SENDTO); 
				data.setData(Uri.parse("mailto:business@ichano.com"));
				startActivity(data);
			}catch (Exception e) {
				Toast.makeText(getApplicationContext(), R.string.mail_to, Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.logout:
			mUserInfo.setLoginInfo(false, "", "", "");
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), LoginActivity.class);
			startActivity(intent);
			isExitWithLogout = true;
			mMyViewerHelper.removeAllCameraInfos();
			mCameraInfoManager.deleteAll();
			finish();
			break;
		case R.id.back_linlayout:
			if(!mUserLayout.isDrawerOpen(Gravity.LEFT))
				mUserLayout.openDrawer(Gravity.LEFT);
			break;
		case R.id.opt_linlayout:
			openAddDialog();
			break;
		case R.id.add_cid:
			if(mShowAddLayoutDialog != null){
				mShowAddLayoutDialog.dismiss();
				titlebar_opt_image.setBackgroundResource(R.drawable.add_icon);
			}
			showAddCameraDlg();
			break;
		case R.id.add_cid_by_qr:
			if(mShowAddLayoutDialog != null){
				mShowAddLayoutDialog.dismiss();
				titlebar_opt_image.setBackgroundResource(R.drawable.add_icon);
			}
			Intent intent1 = new Intent();
			intent1.setClass(this, CaptureActivity.class);
			intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent1, SCANNIN_GREQUEST_CODE);
			break;
			default:
				break;
		}
	}
	
	private void openAddDialog() {
		// TODO Auto-generated method stub
		if(mShowAddLayoutDialog != null){
			mShowAddLayoutDialog.show();
		}else{
			View view = mLayoutInflater.inflate(R.layout.add_camera_layout, null);
			view.findViewById(R.id.add_cid).setOnClickListener(this);
			view.findViewById(R.id.add_cid_by_qr).setOnClickListener(this);
			mShowAddLayoutDialog = new AlertDialog.Builder(this)
			.setTitle("    ")
			.setView(view)
			.setCancelable(false)
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					arg0.dismiss();
					titlebar_opt_image.setBackgroundResource(R.drawable.add_icon);
				}
			}).create();
			mShowAddLayoutDialog.show();
		}
		titlebar_opt_image.setBackgroundResource(R.drawable.add_icon_2);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		CameraInfo cameraInfo = (CameraInfo) parent.getAdapter().getItem(position);
		if(cameraInfo.getIsOnline()){
			Intent intent = new Intent(this, WatchActivity.class);
			intent.putExtra(Constants.INTENT_CID, cameraInfo.getCid());
			intent.putExtra(Constants.INTENT_CAMERA_NAME, cameraInfo.getCameraName());
			startActivity(intent);
		}else{
			Toast.makeText(this, R.string.camera_offline, Toast.LENGTH_LONG);
		}
	}
	
	public void showAddCameraDlg(){
		if(null != mAddCameraDlg){
			mAddCameraDlg.show();
		}else{
			View view = mLayoutInflater.inflate(R.layout.add_camera_dialog, null);
			final EditText cidView = (EditText) view.findViewById(R.id.cid);
			final EditText passwordView = (EditText) view.findViewById(R.id.password);
			mAddCameraDlg = new AlertDialog.Builder(this)
			.setView(view)
			.setTitle(R.string.add_camera_dlg_title)
			.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String cid = cidView.getText().toString();
					String pwd = passwordView.getText().toString();
					if(null == cid || null == pwd){
						Toast.makeText(CameraListActivity.this, R.string.empty_info, Toast.LENGTH_LONG).show();
						return;
					}
					if("".equals(cid) || "".equals(pwd)){
						Toast.makeText(CameraListActivity.this, R.string.empty_info, Toast.LENGTH_LONG).show();
						return;
					}
					long cidLong = 0;
					try{
						cidLong = Long.parseLong(cid);
					}catch(NumberFormatException e){
						Toast.makeText(CameraListActivity.this, R.string.invalid_cid, Toast.LENGTH_LONG).show();
						return;
					}

					if(mUserInfo.isLogin){
						mAddCidHandler.setRequestValue(cid, DEFAULT_USER, pwd, 1);
						mAddCidHandler.doThing(AddCidHandler.ADD_CID_REQUEST);
					}else{
						addStreamer(cidLong, DEFAULT_USER, pwd);
					}
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.create();
			mAddCameraDlg.show();
		}
	}
	public void showAboutDialog(){
		if(null != mAboutDialog){
			mAboutDialog.show();
		}else{
			View view = mLayoutInflater.inflate(R.layout.about_dialog, null);
			TextView aboutView = (TextView) view.findViewById(R.id.about);
			aboutView.setText(String.format(getString(R.string.about_str), 
					getString(R.string.app_name), AppUtils.getAppVersionName(CameraListActivity.this)));
			mAboutDialog = new AlertDialog.Builder(this)
			.setView(view)
			.setTitle(R.string.about)
			.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.create();
			mAboutDialog.show();
		}
	}
	
	private void showDisclaimerDlg(){
		if(null != mDisclaimerDialog){
			mDisclaimerDialog.show();
		}else{
			WebView webView = new WebView(CameraListActivity.this);
			webView.loadUrl(mShowChinese ? DISCLAIMER_URL_CN : DISCLAIMER_URL_EN);
			mDisclaimerDialog = new AlertDialog.Builder(this)
			.setView(webView)
			.setTitle(R.string.disclaimer)
			.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.create();
			mDisclaimerDialog.show();
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popup_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
	
//	@Override
//	public boolean onMenuItemClick(MenuItem item) {
//		switch (item.getItemId()) {
//		case R.id.add_cid:
//			showAddCameraDlg();
//			break;
//		case R.id.add_cid_by_qr:
//			Intent intent = new Intent();
//			intent.setClass(this, CaptureActivity.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
//			break;
//		default:
//			break;
//		}
//		return false;
//	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if(resultCode == RESULT_OK){
				Bundle bundle = data.getExtras();
				String barcode = bundle.getString(Intents.Scan.RESULT);
				if(!(barcode.contains(Constants.BARCODE_DEVICE_ID) || barcode.contains(Constants.BARCODE_CID))){
					Toast.makeText(this, R.string.invalid_barcode, Toast.LENGTH_LONG).show();
					return;
				}
				if(!(barcode.contains(Constants.BARCODE_USER_NAME) || barcode.contains(Constants.BARCODE_NAME))){
					Toast.makeText(this, R.string.invalid_barcode, Toast.LENGTH_LONG).show();
					return;
				}
				if(!barcode.contains(Constants.BARCODE_PASSWORD)){
					Toast.makeText(this, R.string.invalid_barcode, Toast.LENGTH_LONG).show();
					return;
				}
				String[] results = barcode.split(Constants.BARCODE_SPLITER);
				if(results.length != 4){
					Toast.makeText(this, R.string.invalid_barcode, Toast.LENGTH_LONG).show();
					return;
				}
				if(results[0].contains(Constants.BARCODE_DEVICE_ID)){
					String cid = results[0].replace(Constants.BARCODE_DEVICE_ID, "");
					String userName = results[1].replace(Constants.BARCODE_USER_NAME, "");
					String password = results[2].replace(Constants.BARCODE_PASSWORD, "");
					Log.i(TAG,"device_id = " + cid + ", userName = " + userName + ", password = " + password);
					mAddCidHandler.setRequestValue(cid, userName, password, 1);
					mAddCidHandler.doThing(AddCidHandler.GET_CID_REQUEST);
				}else{
					String cid = results[0].replace(Constants.BARCODE_CID, "");
					String userName = results[1].replace(Constants.BARCODE_USER_NAME, "");
					String password = results[2].replace(Constants.BARCODE_PASSWORD, "");
					Log.i(TAG,"cid = " + cid + ", userName = " + userName + ", password = " + password);
					
					if(mUserInfo.isLogin){
						mAddCidHandler.setRequestValue(cid, userName, password, 1);
						mAddCidHandler.doThing(AddCidHandler.ADD_CID_REQUEST);
					}else{
						addStreamer(Long.parseLong(cid), userName, password);
					}
				}
			}
			break;
		}
    }
	
	private void showExitDlg(){
		if(null != mExitDialog){
			mExitDialog.show();
		}else{
			mExitDialog = new AlertDialog.Builder(CameraListActivity.this)
			.setTitle(R.string.exit_str)
			.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					CameraListActivity.this.finish();
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.create();
			mExitDialog.show();
		}
	}
	
	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		if(mUserLayout.isDrawerOpen(Gravity.LEFT)){
			mUserLayout.closeDrawer(Gravity.LEFT);
		}else{
			showExitDlg();
		}
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
	protected void onDestroy(){
		super.onDestroy();
		for (CameraInfo info : mCameraInfos) {
			removeStreamer(info.getCid());
		}
		mMyViewerHelper.removeCameraStateListener(this);
		if(!isExitWithLogout){
			mMyViewerHelper.logout();
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		if(mCameraListAdapter!=null){
		    mCameraListAdapter.clearCache();
		}
	}
	
	public  class CameraListAdapter extends BaseSwipeAdapter{

		private LayoutInflater mLayoutInflater;
		private List<CameraInfo> mCameraInfos;
		private Context mContext;
		public CameraListAdapter(Context context, List<CameraInfo> infos){
			mLayoutInflater = LayoutInflater.from(context);
			mCameraInfos = infos;
			mContext = context;
		}
		public void clearCache(){
		    
	    }
		
//		@Override
//		public int getCount() {
//			return mCameraInfos.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return mCameraInfos.get(position);
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			View view = null;
//			if(null == convertView){
//				view = mLayoutInflater.inflate(R.layout.camera_list_item, null);
//			}else{
//				view = convertView;
//			}
//			
//			ImageView thumbView = (ImageView) view.findViewById(R.id.thumb);
//			TextView cameraName = (TextView) view.findViewById(R.id.cameraName);
//			ImageView cameraStateView = (ImageView) view.findViewById(R.id.cameraState);
//			TextView cameraStateTxtView = (TextView) view.findViewById(R.id.cameraStateTxt);
//			ImageView editView = (ImageView) view.findViewById(R.id.edit);
//			ImageView deleteView = (ImageView) view.findViewById(R.id.delete);
//			final CameraInfo info = mCameraInfos.get(position);
//			thumbView.setImageBitmap(info.getCameraThumb());
//			cameraName.setText(info.getCameraName());
//			cameraStateView.setImageResource(getStateDrawable(info));
//			cameraStateTxtView.setText(getStateTxtDrawable(info));
//			editView.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					if(info.getIsOnline()){
//					    Intent intent = new Intent();
//                      intent.setClass(getApplicationContext(), CameraSettingsTabActivity.class);
//                      intent.putExtra(Constants.INTENT_CID, info.getCid());
//                      startActivity(intent);
////					    startActivity(new Intent(CameraListActivity.this, CameraSettingsTabActivity.class));
////						Intent intent = new Intent();
////						intent.setClass(getApplicationContext(), CameraInfoSettingActivity.class);
////						intent.putExtra(Constants.INTENT_CID, info.getCid());
////						startActivity(intent);
//					}else{
//						Toast.makeText(CameraListActivity.this, R.string.camera_offline, Toast.LENGTH_LONG).show();
//					}
//				}
//			});
//			deleteView.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					new AlertDialog.Builder(CameraListActivity.this)
//					.setTitle(R.string.delete_camera_dlg_title)
//					.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
//						
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							if(mUserInfo.isLogin){
//								mEditCidHandler.setRequestValue(info);
//								mEditCidHandler.doThing(EditCidHandler.DELETE_CID);
//							}else{
//								removeStreamer(info.getCid());
//								mCameraInfoManager.delete(info);
//								mMyViewerHelper.removeCameraInfo(info);
//								mCameraListAdapter.notifyDataSetChanged();	
//							}
//						}
//					})
//					.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//						
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							dialog.dismiss();
//						}
//					})
//					.create().show();
//				}
//			});
//			return view;
//		}
		
		private int getStateDrawable(CameraInfo info){
			if(info.getPwdIsRight()){
				return info.getIsOnline() ? R.drawable.avs_status_connected : R.drawable.avs_status_unknow;
			}else{
				return R.drawable.avs_status_pwderror;
			}
		}
		
		private int getStateTxtDrawable(CameraInfo info){
			if(info.getPwdIsRight()){
				return info.getIsOnline() ? R.string.online : R.string.offline;
			}else{
				return R.string.pwd_wrong;
			}
		}

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mCameraInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return mCameraInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void fillValues(int arg0, View arg1) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public View generateView(int position, ViewGroup parent) {
           View view  = mLayoutInflater.inflate(R.layout.camera_list_item, null);
           final CameraInfo info = mCameraInfos.get(position);
           
           final SwipeLayout swipeLayout = (SwipeLayout)view.findViewById(getSwipeLayoutResourceId(position));
           swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
           view.setOnFocusChangeListener(new OnFocusChangeListener() {
            
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    swipeLayout.close();
                }
            }
        });
           swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);//(SwipeLayout.DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper));
           swipeLayout.addSwipeListener(new SimpleSwipeListener() {
               @Override
               public void onOpen(SwipeLayout layout) {
               }
           });
           swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
               @Override
               public void onDoubleClick(SwipeLayout layout, boolean surface) {
                   Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
               }
           });
           view.findViewById(R.id.delete1).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {

                   new AlertDialog.Builder(CameraListActivity.this)
                   .setTitle(R.string.delete_camera_dlg_title)
                   .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                       
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           if(mUserInfo.isLogin){
                               mEditCidHandler.setRequestValue(info);
                               mEditCidHandler.doThing(EditCidHandler.DELETE_CID);
                           }else{
                               removeStreamer(info.getCid());
                               mCameraInfoManager.delete(info);
                               mMyViewerHelper.removeCameraInfo(info);
                               mCameraListAdapter.notifyDataSetChanged();  
                           }
                       }
                   })
                   .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                       
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                       }
                   })
                   .create().show();
               
               }
           });
          ImageView thumbView = (ImageView) view.findViewById(R.id.thumb);
          TextView cameraName = (TextView) view.findViewById(R.id.cameraName);
          ImageView cameraStateView = (ImageView) view.findViewById(R.id.cameraState);
          TextView cameraStateTxtView = (TextView) view.findViewById(R.id.cameraStateTxt);
          ImageView editView = (ImageView) view.findViewById(R.id.edit);
          ImageView video = (ImageView) view.findViewById(R.id.video);
          Bitmap bmp=ImageDownloader.getInstance().getBitmapFromCache(String.valueOf(info.getCid()));
          if(bmp==null){
              thumbView.setImageBitmap(ImageDownloader.getInstance().getDefaultBmp(CameraListActivity.this));
          }else{
              thumbView.setImageBitmap(bmp);
          }
          
          cameraName.setText(info.getCameraName());
          cameraStateView.setImageResource(getStateDrawable(info));
          cameraStateTxtView.setText(getStateTxtDrawable(info));
          editView.setOnClickListener(new OnClickListener() {
              
              @Override
              public void onClick(View v) {
                  if(info.getIsOnline()){
                      Intent intent = new Intent();
                      intent.setClass(getApplicationContext(), CameraSettingsTabActivity.class);
                      intent.putExtra(Constants.INTENT_CID, info.getCid());
                      startActivity(intent);
                  }else{
                      Toast.makeText(CameraListActivity.this, R.string.camera_offline, Toast.LENGTH_LONG).show();
                  }
              }
          });
          video.setOnClickListener(new OnClickListener() {
              
              @Override
              public void onClick(View v) {
                  startActivity(new Intent(CameraListActivity.this, RecordingVideoTypeList.class).putExtra(Constants.INTENT_CID, String.valueOf(info.getCid())));
              }
          });
          return view;
        }

        @Override
        public int getSwipeLayoutResourceId(int arg0) {
            // TODO Auto-generated method stub
            return R.id.swipe;
        }
        @Override
        public void notifyDataSetChanged() {
            mCameraListView.setAdapter(mCameraListAdapter);
            closeAllItems();
            super.notifyDataSetChanged();
        }
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		if(mUserInfo.isLogin){
			mCameraListHandler.doThing(CameraListHandler.SYNC_CID_REQUEST);
		}else{
			mHandler.postDelayed(new Runnable() {
	            @Override
	            public void run() {
	    			mCameraListView.onRefreshComplete();
	            }
	        }, DELAY_TIME);
		}
	}

}
