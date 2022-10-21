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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.ichano.rvs.viewer.Media;
import com.ichano.rvs.viewer.Viewer;
import com.ichano.rvs.viewer.bean.MediaDataDesc;
import com.ichano.rvs.viewer.callback.MediaStreamStateCallback;
import com.ichano.rvs.viewer.codec.AudioType;
import com.ichano.rvs.viewer.constant.MediaStreamState;
import com.zhongyun.viewer.R;
import com.zhongyun.viewer.utils.CommUtil;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PlayRtspVideoView extends BaseActivity implements OnSeekBarChangeListener, MediaStreamStateCallback
{
	LinearLayout camera_title;
	RelativeLayout wait_relayout, relayout_play_control, surfaceViewLayout, relayout_camera_bg, progressBar_relayout,
			load_relayout;
	AudioThread aThread;
	int[] audioConfig = new int[2];
	private String connectUrl;
	int connectCount, orientationStatus = 1;
	ImageButton pause;
	ImageButton full_screen;
	ImageButton pause_image;

	TextView play_time, total_time;
	SeekBar video_seek;
	boolean isAction = true;
	// 播放云视频相关参数
	private boolean isCloudVideo = false;
	private String eid;
	private String avsUser;
	private String avsPasswd;

	private boolean isPlaying;
	MarginLayoutParams relayout_play_control_params;
//	AdViewContent adViewContent;
	Animation animation_alpha_in;
	String cid, videodata;

	// ///////////////new avs///////////////////////////////////
	private Media media;
//	private AvsInfoBean avsInfoBean;
	private long vodStreamId;
	private long decoderId = 0;
	MarginLayoutParams progressBar_relayout_params, camera_bg_params, glsurfaceviewlayout_params;
	View main;
	private Thread getDescThread,getTimeThread;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (null == savedInstanceState)
		{
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			isShowConnect = true;
			main = getLayoutInflater().inflate(R.layout.playrtspvideoview, null);
			setContentView(main);
			initView();
			cid = getIntent().getStringExtra("avsCid");
			if (isCloudVideo)
			{
				isShowNetWorkDialog = false;
				avsUser = getIntent().getStringExtra("avsUser");
				avsPasswd = getIntent().getStringExtra("avsPasswd");
				eid = getIntent().getStringExtra("eid");
				media = Viewer.getViewer().getMedia();
				media.setMediaStreamStateCallback(this);
			} else
			{
				media = Viewer.getViewer().getMedia();
				media.setMediaStreamStateCallback(this);
			}
			if (CommUtil.getAndroidVersion() < 14)
			{
				video_seek.setPadding(CommUtil.dip2px(this, 12), 0, CommUtil.dip2px(this, 12), 0);
			}
//			if (avsInfoBean == null)
//			{
//				avsInfoBean = AvsInfoCache.getInstance().getAvsInfo(cid);
//				if (avsInfoBean == null)
//				{
//					showToast(R.string.warnning_request_failed);
//					isPlaying = false;
//					finish();
//				}
//			}
			initRtsp();
		} else
		{
			isAction = false;
		}
	}

	private void initView()
	{
		cid = getIntent().getExtras().getString("avsCid");
		isCloudVideo = getIntent().getBooleanExtra("isCloudVideo", false);
		videodata = getIntent().getStringExtra("videodate");
		TextView title = (TextView) findViewById(R.id.video_title);
		title.setText(videodata);
		camera_title = (LinearLayout) findViewById(R.id.camera_title);
		wait_relayout = (RelativeLayout) findViewById(R.id.wait_relayout);
		progressBar_relayout = (RelativeLayout) findViewById(R.id.progressBar_relayout);
		progressBar_relayout_params = (MarginLayoutParams) progressBar_relayout.getLayoutParams();
		progressBar_relayout_params.height = CommUtil.getPixelsWidth(this) * 3 / 4;
		progressBar_relayout.setLayoutParams(progressBar_relayout_params);
		relayout_play_control = (RelativeLayout) findViewById(R.id.relayout_play_control);
		relayout_play_control_params = (MarginLayoutParams) relayout_play_control.getLayoutParams();

		relayout_play_control.setOnClickListener(this);
		pause = (ImageButton) findViewById(R.id.pause);
		pause.setOnClickListener(this);
		full_screen = (ImageButton) findViewById(R.id.full_screen);
		pause_image = (ImageButton) findViewById(R.id.pause_image);
		pause_image.setOnClickListener(this);
		play_time = (TextView) findViewById(R.id.play_time);
		total_time = (TextView) findViewById(R.id.total_time);
		video_seek = (SeekBar) findViewById(R.id.video_seek);
		relayout_camera_bg = (RelativeLayout) findViewById(R.id.relayout_camera_bg);
		camera_bg_params = (MarginLayoutParams) relayout_camera_bg.getLayoutParams();
		int totaltime = getIntent().getIntExtra("filetime", 0);
		video_seek.setMax(totaltime);
		showTime(totaltime, total_time);
		video_seek.setEnabled(false);
		pause.setEnabled(false);
		full_screen.setEnabled(false);
		full_screen.setOnClickListener(this);
		findViewById(R.id.back_linlayout).setOnClickListener(this);
		findViewById(R.id.ipc_warn_img).setOnClickListener(this);
		video_seek.setOnSeekBarChangeListener(this);
		surfaceViewLayout = (RelativeLayout) findViewById(R.id.glsurfaceviewlayout);
		connectUrl = "rtsp://127.0.01:554/record?recordtype=" + getIntent().getIntExtra("filetype", 0) + "&filename=" + videodata;
		animation_alpha_in = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
//		adViewContent = (AdViewContent) findViewById(R.id.adView_layout);
		load_relayout = (RelativeLayout) findViewById(R.id.load_relayout);
	}

	private void initRtsp()
	{
		wait_relayout.setVisibility(View.VISIBLE);
		glSurfaceView = new GLSurfaceView(this);
		glSurfaceView.setEGLContextClientVersion(2);
		if (isCloudVideo)
		{
			vodStreamId = media.openCloudRecordFileStream(Long.valueOf(cid), eid);
			Log.d("media", "vod stream:" + vodStreamId);
			myRenderer = new MyRenderer(this, vodStreamId, media, handler);
		} else
		{
			vodStreamId = media.openRemoteRecordFileStream(Long.valueOf(cid), videodata);
			Log.d("media", "vod stream:" + vodStreamId);
			myRenderer = new MyRenderer(this, vodStreamId, media, handler);
		}
		glSurfaceView.setRenderer(myRenderer);
		surfaceViewLayout.addView(glSurfaceView);
		surfaceViewLayout.setOnClickListener(this);
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		if (v.getId() == R.id.back_linlayout)
		{
			relaseUi();
			finish();
		} else if (v.getId() == R.id.glsurfaceviewlayout)
		{
			if (orientationStatus == 2)
			{
				if (relayout_play_control.getVisibility() == View.GONE)
				{
					relayout_play_control.setVisibility(View.VISIBLE);
					handler.postDelayed(runnable, 5000);
				} else
				{
					relayout_play_control.setVisibility(View.GONE);
					handler.removeCallbacks(runnable);
				}
			}
		} else if (v.getId() == R.id.full_screen)
		{
			if (orientationStatus == 1)
			{
				orientationStatus = 2;
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			} else if (orientationStatus == 2)
			{
				orientationStatus = 1;
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					if (Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1)
					{
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
					}
				}
			}, 2000);
		} else if (v.getId() == R.id.ipc_warn_img)
		{
			showToast(R.string.recording_video_not_support_seek);
		} else if (v.getId() == R.id.pause)
		{
			if (isPlaying)
			{
				pause.setImageResource(R.drawable.video_pause);
				{
					media.pauseStream(vodStreamId);
				} 
				isPlaying = false;
				if(null != aThread){
					aThread.pauseAudioPlayback();
				}
			} else
			{
				pause.setImageResource(R.drawable.video_play);
				{
					media.resumeStream(vodStreamId);
				}
				isPlaying = true;
				if(null != aThread){
					aThread.resumeAudioPlayback();
				}
				handler.postDelayed(runnable, 5000);
			}
		}
	}

	MyRenderer myRenderer;

	@Override
	protected void onResume()
	{
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		if (isPlaying)
		{
			if(null != aThread){
				aThread.resumeAudioPlayback();
			}
			media.resumeStream(vodStreamId);
		}
	}

	GLSurfaceView glSurfaceView;

	@Override
	protected void onStop()
	{
		super.onStop();
		if (isPlaying)
		{
			if (null != aThread)
			{
				aThread.pauseAudioPlayback();
			}
			if(null != media){
				media.pauseStream(vodStreamId);
			}
		}
		if(getDescThread != null)
		{
			getDescThread.interrupt();
			getDescThread = null;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		// 竖屏
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			WindowManager.LayoutParams windowparams = getWindow().getAttributes();
			windowparams.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().setAttributes(windowparams);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			camera_bg_params.topMargin = CommUtil.dip2px(this, 55);
			relayout_camera_bg.setLayoutParams(camera_bg_params);
			camera_title.setVisibility(View.VISIBLE);
//			adViewContent.setVisibility(View.VISIBLE);
			orientationStatus = 1;
			CommUtil.hideOrShowNavigationBar(this, false);
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			// 横屏
			WindowManager.LayoutParams windowparams = getWindow().getAttributes();
			windowparams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			getWindow().setAttributes(windowparams);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			camera_bg_params.topMargin = 0;
			relayout_camera_bg.setLayoutParams(camera_bg_params);
			camera_title.setVisibility(View.GONE);
//			adViewContent.setVisibility(View.GONE);
			orientationStatus = 2;
			relayout_play_control.setVisibility(View.GONE);
			handler.postDelayed(runnable, 5000);
			CommUtil.hideOrShowNavigationBar(this, true);
		}
	}

	void setOrientation()
	{
		if(null != myRenderer){
			relayout_play_control.setVisibility(View.VISIBLE);
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
			{
				full_screen.setImageResource(R.drawable.video_fullscreen);
				relayout_play_control_params.bottomMargin = (myRenderer.gl_h - myRenderer.display_h) / 2
						- CommUtil.dip2px(this, 43);
				relayout_play_control.setLayoutParams(relayout_play_control_params);
//				adViewContent.setVisibility(View.VISIBLE);
			} else
			{
				full_screen.setImageResource(R.drawable.video_unfullscreen);
				relayout_play_control_params.bottomMargin = 0;
				relayout_play_control.setLayoutParams(relayout_play_control_params);
//				adViewContent.setVisibility(View.GONE);
			}
		}
	}

	Runnable runnable = new Runnable()
	{
		@Override
		public void run()
		{
			if (relayout_play_control.getVisibility() == View.VISIBLE && orientationStatus == 2)
			{
				relayout_play_control.startAnimation(animation_alpha_in);
				relayout_play_control.setVisibility(View.GONE);
			}
		}
	};
	
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			relaseUi();
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy()
	{
	    relaseUi();
		super.onDestroy();
	}
	
	private void relaseUi(){
		isGetTime = false;
		isPlaying = false;
		if (null != aThread)
		{
			aThread.deinitAudio();
			aThread = null;
		}
		if(null != getTimeThread){
			getTimeThread.interrupt();
			getTimeThread = null;
		}
		if(null != myRenderer){
			myRenderer.isCloseStream = true;
			myRenderer.isShowVideo = false;
			myRenderer.clear();
			myRenderer = null;
		}
		glSurfaceView = null;
		//surfaceViewLayout.removeAllViews();
		//relayout_play_control.removeAllViews();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
		}
		if(decoderId != 0){
			media.destoryAVDecoder(decoderId);
			decoderId = 0;
		}
		if (null != media)
		{
			Log.e("close video", decoderId+",,,,,"+vodStreamId);
			media.closeStream(vodStreamId);
			media.setMediaStreamStateCallback(null);
			media = null;
		}
		
		
		
	}
	
	@Override
	public void onMediaStreamState(long streamId, MediaStreamState streamState)
	{
		Log.d("media", "streamId:" + streamId + ",streamState:" + streamState.intValue());
		if (streamId == vodStreamId)
		{
			if (streamState == MediaStreamState.CREATED)
			{

				getDescThread = new Thread(new Runnable() {
					@Override
					public void run() {
						MediaDataDesc desc = null;
						while (desc == null && null != getDescThread) {
							desc = media.getStreamDesc(vodStreamId);
							if (desc != null){
								Message message = Message.obtain();
								message.obj = desc;
								message.what = 1006;
								handler.sendMessage(message);
								Log.e("media", "return");
								return;
							}
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								Log.e("media", e.toString());
							}
						}
					}
				});
				getDescThread.start();
			} else
			{
				relaseUi();
				finish();
			}
		}
	}

	int time;
	boolean isGetTime = true, isShowTime = true;
	Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			if (msg.what == 1003)
			{
				openDialogMessage(R.string.alert_title, R.string.sd_edit_warn, false);
			} else if (msg.what == 1)
			{
				if (isShowTime)
				{
					video_seek.setProgress(time);
					showTime(time, play_time);
					load_relayout.setVisibility(View.GONE);
				}
			} else if (msg.what == 8000)
			{
				wait_relayout.setVisibility(View.GONE);
				pause.setEnabled(true);
				full_screen.setEnabled(true);
				setOrientation();
//				if ((avsInfoBean.getBasicInfo().getStreamerType() != 5) || isCloudVideo)
//				{
					video_seek.setEnabled(true);
					findViewById(R.id.ipc_warn_img).setVisibility(View.GONE);
//				}
				//getTime();
			}else if(msg.what == 1005){
				PlayRtspVideoView.this.finish();
			}else if(msg.what == 1006){
				if(null != media && myRenderer != null){
					MediaDataDesc desc = (MediaDataDesc)msg.obj;
					Log.d("media","video :" + desc.getVideoType().intValue()+ "," + desc.getVideoWidth() + ","+ desc.getVideoHeight());
					Log.d("media","audio :" + desc.getAudioType().intValue()+ "," + desc.getSampRate());
	
					decoderId = media.initAVDecoder(desc.getAudioType(),desc.getSampRate());
	
					myRenderer.setStreamDecoder(decoderId,desc.getVideoWidth(), desc.getVideoHeight());
					isPlaying = true;
					if (desc.getAudioType() == AudioType.INVALID)
						return;
					aThread = new AudioThread(desc.getSampRate(),desc.getChannel(), vodStreamId, decoderId,media);
					aThread.startPlayAudio();
				}
			}else if(msg.what == 1099){
				if (isShowTime)
				{
					time = Integer.parseInt(msg.obj.toString());
					if(time != 0){
						video_seek.setProgress(time);
						showTime(time, play_time);
						load_relayout.setVisibility(View.GONE);
					}
				}
			}
		}
	};

	/*private void getTime()
	{
		getTimeThread = new Thread(){
			public void run(){
				while (isGetTime)
				{
					try
					{
						if (isShowTime)
						{
							if(null != myRenderer){
								time = myRenderer.getCurTime();
								if(time != 0){
									handler.sendEmptyMessage(1);
								}
							}
							Thread.sleep(1000);
						}
					} catch (InterruptedException e)
					{
						
					}
				}
			}
		};
		getTimeThread.start();
	}*/

	private void showTime(int time, TextView view)
	{
		time = time / 1000;
		int hour = time / (60 * 60);
		int min = time / 60 - (hour * 60);
		int sen = time - (time / 60) * 60;
		view.setText(String.format("%02d:%02d", min, sen));
	}

	public void onStopTrackingTouch(SeekBar arg0)
	{
		if(null != myRenderer){
			media.seekStream(vodStreamId, time);
			load_relayout.setVisibility(View.VISIBLE);
			myRenderer.setCurTime(0);
			myRenderer.isShowVideo = true;
			isShowTime = true;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0)
	{
		isShowTime = false;
		if(null != myRenderer){
			myRenderer.isShowVideo = false;
		}
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2)
	{
		time = arg1;
		showTime(arg1, play_time);
	}
}
