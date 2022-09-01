package io.agora.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import io.agora.AgoraAPIOnlySignal;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.agora.tutorials1v1vcall.R;

public class VideoSendChatViewActivity extends AppCompatActivity {
    private String channelName = "demoChannel1";
    private String selfName = "1";
    private String appID = "";

    private static final String LOG_TAG = VideoSendChatViewActivity.class.getSimpleName();

    private static final int PERMISSION_REQ_ID = 22;

    // permission WRITE_EXTERNAL_STORAGE is not mandatory for Agora RTC SDK, just incase if you wanna save logs to external sdcard
    private static final String[] REQUESTED_PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private AgoraAPIOnlySignal m_agoraAPI;


    private RtcEngine mRtcEngine;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
                }
            });
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft();
                }
            });
        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVideoMuted(uid, muted);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_ctrl_view);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏

        Intent intent = getIntent();
        appID = intent.getStringExtra("appID");
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
            initAgoraEngineAndJoinChannel();
        }

        initViewsAndEvents();//红外遥控按钮监听初始化
    }

    private void initViewsAndEvents() {
        findViewById(R.id.send_button1).setOnTouchListener(mSend1ClickListener);//前进按钮
        findViewById(R.id.send_button2).setOnTouchListener(mSend2ClickListener);//后退按钮
        findViewById(R.id.send_button3).setOnTouchListener(mSend3ClickListener);//左拐
        findViewById(R.id.send_button4).setOnTouchListener(mSend4ClickListener);//右拐
        findViewById(R.id.send_button_s4).setOnTouchListener(mSendS4ClickListener);//全速
        findViewById(R.id.send_button_s3).setOnTouchListener(mSendS3ClickListener);//速度3
        findViewById(R.id.send_button_s2).setOnTouchListener(mSendS2ClickListener);//速度2
    }

    private void initAgoraEngineAndJoinChannel() {

        initializeAgoraEngine();
        initializeAgoraSignal();
        setupVideoProfile();
        setupLocalVideo();
        joinVideoChannel();
        joinSignalChannel();
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(LOG_TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    REQUESTED_PERMISSIONS,
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED || grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                    showLongToast("Need permissions " + Manifest.permission.RECORD_AUDIO + "/" + Manifest.permission.CAMERA + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    finish();
                    break;
                }

                initAgoraEngineAndJoinChannel();
                break;
            }
        }
    }

    private final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 退出 Agora 信令系统
        m_agoraAPI.logout();
        leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;
    }

    View.OnTouchListener mSend1ClickListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action=event.getAction();
            if(action== MotionEvent.ACTION_DOWN){
                sendCtrl("01");//前进
            }else if (action == MotionEvent.ACTION_UP) {
                sendCtrl("05");//停止
            }
            return false;
        }
    };
    View.OnTouchListener mSend2ClickListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action=event.getAction();
            if(action== MotionEvent.ACTION_DOWN){
                sendCtrl("02");//后退
            }else if (action == MotionEvent.ACTION_UP) {
                sendCtrl("05");//停止
            }
            return false;
        }
    };

    View.OnTouchListener mSend3ClickListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action=event.getAction();
            if(action== MotionEvent.ACTION_DOWN){
                sendCtrl("03");//左转
            }else if (action == MotionEvent.ACTION_UP) {
                sendCtrl("05");//停止
            }
            return false;
        }
    };
    View.OnTouchListener mSend4ClickListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action=event.getAction();
            if(action== MotionEvent.ACTION_DOWN){
                sendCtrl("04");//右转
            }else if (action == MotionEvent.ACTION_UP) {
                sendCtrl("05");//停止
            }
            return false;
        }
    };
    View.OnTouchListener mSendS4ClickListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action=event.getAction();
            if(action== MotionEvent.ACTION_DOWN){
                sendCtrl("s");//全速
            }else if (action == MotionEvent.ACTION_UP) {
//                sendCtrl("s1");//速度1
            }
            return false;
        }
    };
    View.OnTouchListener mSendS3ClickListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action=event.getAction();
            if(action== MotionEvent.ACTION_DOWN){
                sendCtrl("s3");//速度3
            }else if (action == MotionEvent.ACTION_UP) {
//                sendCtrl("s1");//速度1
            }
            return false;
        }
    };
    View.OnTouchListener mSendS2ClickListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action=event.getAction();
            if(action== MotionEvent.ACTION_DOWN){
                sendCtrl("s2");//速度2
            }else if (action == MotionEvent.ACTION_UP) {
//                sendCtrl("s1");//速度1
            }
            return false;
        }
    };

    private void sendCtrl(String ctrlcode) {
        String msg = ctrlcode;
        // 发送频道消息
        m_agoraAPI.messageChannelSend(channelName, msg, "");
    }

    public void onLocalVideoMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.muteLocalVideoStream(iv.isSelected());

        FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);
        surfaceView.setZOrderMediaOverlay(!iv.isSelected());
        surfaceView.setVisibility(iv.isSelected() ? View.GONE : View.VISIBLE);
    }

    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }

    public void onRemoteVoiceMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }
        sendCtrl("M");
    }

    public void onRemoteLightClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }
        sendCtrl("L");
    }

    public void onRemoteSwitchCamClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }
        sendCtrl("C");
    }

    public void onSwitchCameraClicked(View view) {
        mRtcEngine.switchCamera();
    }

    public void onEncCallClicked(View view) {
        finish();
    }

    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(),appID, mRtcEventHandler);


        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void initializeAgoraSignal() {
        // 初始化信令 SDK
        m_agoraAPI = AgoraAPIOnlySignal.getInstance(this, appID);

        // 登录 Agora 信令系统
        m_agoraAPI.login2(appID, selfName, "_no_need_token", 0, "", 5, 1);
    }

    private void setupVideoProfile() {
        mRtcEngine.enableVideo();

//      mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P, false); // Earlier than 2.3.0
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(VideoEncoderConfiguration.VD_320x240, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_LANDSCAPE));
    }

    private void setupLocalVideo() {
        FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));
    }

    private void joinSignalChannel() {
        // 加入频道
        m_agoraAPI.channelJoin(channelName);
    }

    private void joinVideoChannel() {
        String token = getString(R.string.agora_access_token);
        if(token.isEmpty()) {
            token = null;
        }
        mRtcEngine.joinChannel(token, channelName, "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
    }

    private void setupRemoteVideo(int uid) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);

        if (container.getChildCount() >= 1) {
            return;
        }

        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid));

        surfaceView.setTag(uid); // for mark purpose
//        View tipMsg = findViewById(R.id.quick_tips_when_use_agora_sdk); // optional UI
//        tipMsg.setVisibility(View.GONE);
    }

    private void leaveChannel() {
        // 退出频道
        m_agoraAPI.channelLeave(channelName);
        mRtcEngine.leaveChannel();

    }

    private void onRemoteUserLeft() {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);
        container.removeAllViews();

//        View tipMsg = findViewById(R.id.quick_tips_when_use_agora_sdk); // optional UI
//        tipMsg.setVisibility(View.VISIBLE);
    }

    private void onRemoteUserVideoMuted(int uid, boolean muted) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);

        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);

        Object tag = surfaceView.getTag();
        if (tag != null && (Integer) tag == uid) {
            surfaceView.setVisibility(muted ? View.GONE : View.VISIBLE);
        }
    }
}
