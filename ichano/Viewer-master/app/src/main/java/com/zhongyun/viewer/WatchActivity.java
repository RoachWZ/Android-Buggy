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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ichano.rvs.viewer.ui.GLMediaView;
import com.ichano.rvs.viewer.ui.GLMediaView.LinkCameraStatusListener;
import com.ichano.rvs.viewer.ui.GLMediaView.SwitchFrontRearCameraResultCallback;
import com.ichano.rvs.viewer.ui.GLMediaView.ToggleCameraFlashResultCallback;
import com.umeng.analytics.MobclickAgent;
import com.zhongyun.viewer.setting.BaseSettingFragment;
import com.zhongyun.viewer.utils.Constants;
import com.zhongyun.viewer.utils.ZYDateUtils;
import com.zhongyun.viewer.utils.FileUtils;
import com.zhongyun.viewer.video.RecordingVideoTypeList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

public class WatchActivity extends BaseActivity 
	implements View.OnClickListener,OnPageChangeListener{

	private static final String TIME_UP_ERROR = "TIME_UP";
	private static final int DEFAULT_CAMERA_INDEX = 0;
//	private Toolbar mToolbar;
	private GLMediaView mGLMediaView;
	private long mCid;
	private Handler mHandler = new Handler();
	private Dialog mLinkFailDlg;
	private Dialog mExitDialog;
	private ProgressDialog mWaitingDialog;
	
	private LinearLayout mSoundSwitcherView;
	private ImageView mSoundSwitcherIconView;
	private TextView mSoundSwitcherNameView;
	
	private LinearLayout mRecordVideoView;
	private ImageView mRecordVideoIconView;
	private TextView mRecordVideoNameView;
	private String mRecordVideoPath,mCaptureImgPath;
	
	private LinearLayout mHoldTalkView;
	private ImageView mHoldTalkIconView;
	
	ViewPager viewPager;
	View view1, view2,view3;
	ArrayList<View> mViews;
	private int currentIndex;
	private ImageView mCaptureImgIconView;
	private ImageView mSwitchCamareIconView;
	private ImageView mFlashIconView;
	RelativeLayout   bottom_arrow_left_layout, bottom_arrow_right_layout;
	ImageView titlebar_opt_image,titlebar_back_image;
	TextView opt,titlebar_back_text;
	private String command = "{\"msgname\":\"upgradeReq\",\"requestid\":\"\",\"param\":{\"startUpgrade\":\"yes\"}}";
    private FrameLayout ContainView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watch);
		initOperateView();
		initViewPager();
//		mToolbar = (Toolbar) findViewById(R.id.toolbar);
//		mGLMediaView = (GLMediaView) findViewById(R.id.media_view);
		mCid = getIntent().getLongExtra(Constants.INTENT_CID, 0);
//		String title = getIntent().getStringExtra(Constants.INTENT_CAMERA_NAME);
//		mToolbar.setTitle(title);
////		setSupportActionBar(mToolbar);
//		mToolbar.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//            
//            @Override
//            public boolean onMenuItemClick(MenuItem arg0) {
//                int id = arg0.getItemId();
//
//                if (id == R.id.video) {
//                    startActivity(new Intent(WatchActivity.this, RecordingVideoTypeList.class).putExtra(Constants.INTENT_CID, String.valueOf(mCid)));
//                    return true;
//                }
//                return false;
//            }
//        });
//		mGLMediaView.bindCid(mCid, DEFAULT_CAMERA_INDEX);
//		mGLMediaView.openAudio(true);   // open audio capture device銆�
//		mGLMediaView.setOnLinkCameraStatusListener(new LinkCameraStatusListener() {
//			
//			@Override
//			public void startToLink() {
//				mWaitingDialog.show();
//			}
//			
//			@Override
//			public void linkSucces() {
//				mWaitingDialog.dismiss();
//			}
//			
//			@Override
//			public void linkFailed(String msg) {
//				if(TIME_UP_ERROR.equals(msg)){
//					mHandler.post(new Runnable() {
//						
//						@Override
//						public void run() {
//							Toast.makeText(WatchActivity.this, R.string.time_up_error, Toast.LENGTH_LONG).show();
//						}
//					});
//				}else{
//					mHandler.post(new Runnable() {
//						
//						@Override
//						public void run() {
//							mWaitingDialog.dismiss();
//							showLinkFailDlg();
//						}
//					});
//				}
//			}
//		});
		
		mWaitingDialog = new ProgressDialog(this);
		mWaitingDialog.setMessage(getString(R.string.waiting));
		mWaitingDialog.setIndeterminate(true);
		mWaitingDialog.setCancelable(true);
		
	}
	
	private void initOperateView(){
		mRecordVideoPath = FileUtils.mkdirsOnSDCard(Constants.RECORD_VIDEO_PATH).getAbsolutePath();
		mCaptureImgPath =  FileUtils.mkdirsOnSDCard(Constants.CAPTURE_IAMGE_PATH).getAbsolutePath();
		bottom_arrow_left_layout = (RelativeLayout) findViewById(R.id.bottom_arrow_left_layout);
		bottom_arrow_left_layout.setOnClickListener(this);
		bottom_arrow_right_layout = (RelativeLayout) findViewById(R.id.bottom_arrow_right_layout);
		bottom_arrow_right_layout.setOnClickListener(this);
		findViewById(R.id.opt_linlayout).setOnClickListener(this);
		findViewById(R.id.titlebar_opt_image).setBackgroundResource(R.drawable.video_img);
		findViewById(R.id.back_linlayout).setClickable(false);
		titlebar_back_text = (TextView) findViewById(R.id.titlebar_back_text);
		String title = getIntent().getStringExtra(Constants.INTENT_CAMERA_NAME);
		titlebar_back_text.setText(title);
	}
	
	private void initViewPager() {
		viewPager = (ViewPager) findViewById(R.id.setPager);
		LayoutInflater lf = getLayoutInflater().from(this);
		view1 = lf.inflate(R.layout.setcamerapager1, null);
		
		mSoundSwitcherIconView = (ImageView) view1
				.findViewById(R.id.sound_switcher_icon);
		mSoundSwitcherNameView = (TextView) view1
				.findViewById(R.id.sound_switcher_name);
		mRecordVideoIconView = (ImageView) view1
				.findViewById(R.id.record_video_icon);
		mRecordVideoNameView = (TextView) view1
				.findViewById(R.id.record_video_name);
		mHoldTalkIconView = (ImageView) view1.findViewById(R.id.hold_talk);
		
		mSoundSwitcherIconView.setOnClickListener(this);
		mRecordVideoIconView.setOnClickListener(this);
		mHoldTalkIconView.setOnClickListener(this);

		view2 = lf.inflate(R.layout.setcamerapager3, null);
		
		mCaptureImgIconView = (ImageView) view2.findViewById(R.id.capture_img);
		mCaptureImgIconView.setOnClickListener(this);
		mSwitchCamareIconView = (ImageView) view2.findViewById(R.id.switch_img);
		mSwitchCamareIconView.setOnClickListener(this);
		mFlashIconView = (ImageView) view2.findViewById(R.id.flash_img);
		mFlashIconView.setOnClickListener(this);
		mViews = new ArrayList<View>();

		mViews.add(view1);
		mViews.add(view2);
		viewPager.setAdapter(new MyPagerAdapter(mViews));
		viewPager.setOnPageChangeListener(this);
		viewPager.setCurrentItem(0);
		
		ContainView = (FrameLayout)findViewById(R.id.surface_contain);
	}
	
	private void showLinkFailDlg(){
		if(null != mLinkFailDlg){
			mLinkFailDlg.show();
		}else{
			mLinkFailDlg = new AlertDialog.Builder(WatchActivity.this)
			.setTitle(R.string.camera_link_fail)
			.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					WatchActivity.this.finish();
				}
			})
			.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if(KeyEvent.KEYCODE_BACK == keyCode){
						return true;
					}
					return false;
				}
			})
			.create();
			mLinkFailDlg.show();
		}
	}
	@Override
	protected void onStart() {
	    // TODO Auto-generated method stub
	    super.onStart();
	    
	    mHandler.postDelayed(new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                if(ContainView.getChildCount()==0&&mGLMediaView==null){
                    Log.i("MartinSurface","onStart add");
                    mGLMediaView = new GLMediaView(WatchActivity.this);
                    ContainView.addView(mGLMediaView);
                    mGLMediaView.bindCid(mCid, DEFAULT_CAMERA_INDEX);
                    mGLMediaView.openAudio(true);   // open audio capture device銆�
                    mGLMediaView.setOnLinkCameraStatusListener(new LinkCameraStatusListener() {
                        
                        @Override
                        public void startToLink() {
                            mWaitingDialog.show();
                        }
                        
                        @Override
                        public void linkSucces() {
                            mWaitingDialog.dismiss();
                        }
                        
                        @Override
                        public void linkFailed(String msg) {
                            Log.i("MartinSurface","failed:"+msg);
                            if(TIME_UP_ERROR.equals(msg)){
                                mHandler.post(new Runnable() {
                                    
                                    @Override
                                    public void run() {
                                        Toast.makeText(WatchActivity.this, R.string.time_up_error, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }else{
                                mHandler.post(new Runnable() {
                                    
                                    @Override
                                    public void run() {
                                        mWaitingDialog.dismiss();
                                        showLinkFailDlg();
                                    }
                                });
                            }
                        }
                    });
                }

            }
        },300);
	}
	@Override
	protected void onStop() {
	    super.onStop();
	    if(ContainView.getChildCount()>0){
            ContainView.removeView(mGLMediaView);
            mGLMediaView = null;
        }
	}
	
	private void showExitDlg(){
		if(null != mExitDialog){
			mExitDialog.show();
		}else{
			mExitDialog = new AlertDialog.Builder(WatchActivity.this)
			.setTitle(R.string.exit_camera)
			.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					WatchActivity.this.finish();
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
//		super.onBackPressed();
		showExitDlg();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		case R.id.sound_switcher_icon:
			if(mGLMediaView.isSoundOn()){
				mGLMediaView.soundOff();
				mSoundSwitcherIconView.setImageResource(R.drawable.sound_on);
				mSoundSwitcherNameView.setText(R.string.sound_on);
			}else{
				mGLMediaView.soundOn();
				mSoundSwitcherIconView.setImageResource(R.drawable.sound_off);
				mSoundSwitcherNameView.setText(R.string.sound_off);
			}
			break;
		case R.id.record_video_icon:
			if(mGLMediaView.isRecordingVideo()){
				boolean ret = mGLMediaView.stopRecordVideo();
				mRecordVideoIconView.setImageResource(R.drawable.record_off);
				mRecordVideoNameView.setText(R.string.record);
				if(ret){
					String toastStr = getResources().getString(R.string.recording_saved, mRecordVideoPath);
					Toast.makeText(this, toastStr, Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(this, R.string.record_failed, Toast.LENGTH_LONG).show();
				}
			}else{
				if(FileUtils.hasSDCard()){
					String path = mRecordVideoPath + "/" + ZYDateUtils.getTime() + Constants.VIDEO_MP4;
					mGLMediaView.startRecordVideo(path);
					mRecordVideoIconView.setImageResource(R.drawable.record_on);
					mRecordVideoNameView.setText(R.string.recording);
				}
			}
			break;
		case R.id.hold_talk:
			if(mGLMediaView.isSendRevAudio()){
				mGLMediaView.stopSendRevAudio();
				mHoldTalkIconView.setImageResource(R.drawable.hold_talk);
			}else{
				mGLMediaView.startSendRevAudio();
				mHoldTalkIconView.setImageResource(R.drawable.hold_talk_pressed);
			}
			break;
		case R.id.capture_img:
			if(FileUtils.hasSDCard()){
				String path = mCaptureImgPath + "/" + ZYDateUtils.getTime() + Constants.IMG_JPG;
				if(mGLMediaView.takeCapture(path)){
				    showToast(R.string.savepic_succ);
				}else{
				    showToast(R.string.warnning_save_photo_failed);
				}
			}
			break;
		case R.id.switch_img:
			mSwitchCamareIconView.setImageResource(R.drawable.switch_camera_pressed);
			mSwitchCamareIconView.setClickable(false);
			mGLMediaView.switchFrontRearCamera(new SwitchFrontRearCameraResultCallback() {
				
				@Override
				public void onResult(int arg0) {
					if(arg0==0){//success
						mSwitchCamareIconView.setImageResource(R.drawable.switch_camera);
					}else{
					}
					mSwitchCamareIconView.setClickable(true);
				}
			});
			break;
		case R.id.flash_img:
			mFlashIconView.setImageResource(R.drawable.flash_pressed);
			mFlashIconView.setClickable(false);
			mGLMediaView.toggleCameraFlash(new ToggleCameraFlashResultCallback() {
				
				@Override
				public void onResult(int arg0) {
					mFlashIconView.setImageResource(R.drawable.flash_image);
					mFlashIconView.setClickable(true);
				}
			});
			break;
		case R.id.bottom_arrow_left_layout:
			viewPager.setCurrentItem(currentIndex - 1);
			break;
		case R.id.bottom_arrow_right_layout:
			viewPager.setCurrentItem(currentIndex + 1);
			break;
		case R.id.opt_linlayout:
			   startActivity(new Intent(WatchActivity.this, RecordingVideoTypeList.class).putExtra(Constants.INTENT_CID, String.valueOf(mCid)));
			default:
				break;
		}
		
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		currentIndex = arg0;
		if (arg0 == 0)
		{
			bottom_arrow_left_layout.setVisibility(View.GONE);
			bottom_arrow_right_layout.setVisibility(View.VISIBLE);
		} else if (arg0 == mViews.size() - 1)
		{
			bottom_arrow_left_layout.setVisibility(View.VISIBLE);
			bottom_arrow_right_layout.setVisibility(View.GONE);
		} else
		{
			bottom_arrow_left_layout.setVisibility(View.VISIBLE);
			bottom_arrow_right_layout.setVisibility(View.VISIBLE);
		}
		
	}
	public void showToast(int messageId)
	{
		Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_video, menu);
        return super.onCreateOptionsMenu(menu);
    }
	
	public class MyPagerAdapter extends PagerAdapter{
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews)
		{
			this.mListViews = mListViews;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			return arg0 == arg1;
		}

		@Override
		public int getCount()
		{
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1)
		{
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			container.removeView(mListViews.get(position));
		}
	}
}
