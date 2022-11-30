package io.agora.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import hlq.service.SendSocketService;
import hlq.view.activity.BluetoothActivity;
import io.agora.AgoraAPI;
import io.agora.AgoraAPIOnlySignal;
import io.agora.IAgoraAPI;
import wz.SharedSave.SharedHelper;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.agora.tutorials1v1vcall.R;

public class VideoReceiveChatViewActivity extends AppCompatActivity implements SensorEventListener{

    private String channelName = "demoChannel1";
    private String selfName = "0";
    private String appID = "";

    /*（红外遥控已废弃，改为蓝牙连接）
    //下面的数组是一种交替的载波序列模式，通过毫秒测量
    //引导码，地址码，地址码，数据码，数据反码
    //第三行数据码反置，比如0x12=0001 0010反置为 0100 1000

    //停止 0x10
    int[] patternS = { 9000, 4500,
            560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
            560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
            560, 560,	560, 560, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
            560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690,
            560, 42020, 9000, 2250, 560, 98190 };
    //前进 0x12
    int[] pattern1 = { 9000, 4500,
            560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
            560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
            560, 560,	560, 1690, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
            560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560,1690, 	560, 1690, 	560, 1690,
            560, 42020, 9000, 2250, 560, 98190 };
    //后退 0x18
    int[] pattern2 = { 9000, 4500,
            560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
            560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
            560, 560,	560, 560, 	560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
            560, 1690, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690,
            560, 42020, 9000, 2250, 560, 98190 };
    //左转 0x14
    int[] pattern3 = { 9000, 4500,
            560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
            560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
            560, 560,	560, 560, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
            560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690,
            560, 42020, 9000, 2250, 560, 98190 };
    //右转 0x16
    int[] pattern4 = { 9000, 4500,
            560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
            560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
            560, 560,	560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
            560, 1690, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 1690,	560, 1690, 	560, 1690,
            560, 42020, 9000, 2250, 560, 98190 };
    //左自转 0x17
    int[] pattern5 = { 9000, 4500,
            560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
            560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
            560, 1690,	560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
            560, 560, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690,
            560, 42020, 9000, 2250, 560, 98190 };
    //右自转 0x19
    int[] pattern6 = { 9000, 4500,
            560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
            560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
            560, 1690,	560, 560, 	560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
            560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560,1690, 	560, 1690, 	560, 1690,
            560, 42020, 9000, 2250, 560, 98190 };
    //全速  0x01
    int[] speed = { 9000, 4500,
            560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
            560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
            560,1690,	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
            560,  560, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
            560, 42020, 9000, 2250, 560, 98190 };
    //速度1 0x00
    int[] speed1 = { 9000, 4500,
            560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
            560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
            560, 560,	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
            560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
            560, 42020, 9000, 2250, 560, 98190 };
    //速度2 0x02
    int[] speed2 = { 9000, 4500,
            560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
            560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
            560, 560,	560, 1690, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
            560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690,
            560, 42020, 9000, 2250, 560, 98190 };
    //速度3 0x03
    int[] speed3 = { 9000, 4500,
            560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
            560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
            560, 1690,	560, 1690, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
            560, 560, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690,
            560, 42020, 9000, 2250, 560, 98190 };

    private int hz = 38000;
    // Android4.4之后 红外遥控ConsumerIrManager
    private ConsumerIrManager mCIR;
    */
    private Sensor sensor;
    private SensorManager sm;
    private SensorEvent event;

    private String mstr;
    private boolean left_flag = false;
    private boolean right_flag = false;
    private boolean lr_stop_flag = false;
    private boolean forward_flag = false;
    private boolean backward_flag = false;
    private boolean fb_stop_flag = false;
    private boolean stop_flag = true;

    private boolean SensorGYROSCOPEflag = false;
    private TextView tv;
    private TextView left_setValue;
    private TextView right_setValue;
    private SharedHelper sh;
    private Context mContext;

    private static final String LOG_TAG = VideoReceiveChatViewActivity.class.getSimpleName();

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

        @Override
        public void onConnectionStateChanged(int state, int reason) {
            super.onConnectionStateChanged(state, reason);
            switch (state){
                case 1:showLongToast("CONNECTION_STATE_DISCONNECTED");break;
                case 2:showLongToast("CONNECTION_STATE_CONNECTING ");break;
                case 3:showLongToast("CONNECTION_STATE_CONNECTED");break;
                case 4:showLongToast("CONNECTION_STATE_RECONNECTING");break;
                case 5:showLongToast("CONNECTION_STATE_FAILED ");break;
            }
//            if(state != 3) simpleDialog();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat_view);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏

        Intent intent = getIntent();
        appID = intent.getStringExtra("appID");
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
            initAgoraEngineAndJoinChannel();
        }

        // 获取系统的红外遥控服务（已废弃，改为蓝牙连接）
//        mCIR = (ConsumerIrManager) getSystemService(Context.CONSUMER_IR_SERVICE);

        /*监视连接状态，在OPPO A51上有bug，已停用*/
//       Monitor m = new Monitor();
//        m.start();

        initSensorGYROSCOPE();
        tv = (TextView) findViewById(R.id.SensorGYROSCOPE_value);


        Button btn_saveValue = (Button) findViewById(R.id.saveValueButton);
        left_setValue =  findViewById(R.id.left_setValue);
        right_setValue =  findViewById(R.id.right_setValue);
        mContext = getApplicationContext();
        sh = new SharedHelper(mContext);

        btn_saveValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String leftSetValue = left_setValue.getText().toString();
                String rightSetValue = right_setValue.getText().toString();

                sh.save("leftSetValue",leftSetValue);
                sh.save("rightSetValue",rightSetValue);

                Toast.makeText(VideoReceiveChatViewActivity.this, "信息已写入 SharedPreference 中", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /*监视连接状态，在OPPO A51上有bug，已停用*/
    /*private class Monitor extends Thread{
        TextView tv = findViewById(R.id.tip_title);
        int connectionState;
        @Override
        public void run() {
            super.run();
            while (true){
                connectionState = mRtcEngine.getConnectionState();
                switch (connectionState){
                    case 1:simpleDialog();return;
                    case 2:tv.setText("Building Network Connections");break;
                    case 3:tv.setText("Network Connected");break;
                    case 4:tv.setText("Reestablishing Network Connection");break;
                    case 5:simpleDialog();return;
                }
            }
        }

    }*/

    /**
     * 初始化陀螺仪传感器
     */
    private void initSensorGYROSCOPE() {
        //获得传感器服务
        sm=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //获得陀螺仪传感器
        sensor = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        this.event=event;

        //Log.d("陀螺仪", " x:" + event.values[0] + " y:" + event.values[1]  + " z:" + event.values[2]);
        //获得陀螺仪传感器中的数值
        float x = event.values[0];


        if(!fb_stop_flag && !left_flag && !right_flag && SensorGYROSCOPEflag){
            float lSetValue = Float.parseFloat(left_setValue.getText().toString());
            float rSetValue = Float.parseFloat(right_setValue.getText().toString());
            tv.setText("陀螺仪 x:"+ event.values[0] );

            if ( x<-rSetValue) {//向右偏移，向左回轮
                //mCIR.transmit(hz, pattern3);//左
                SendSocketService.sendMessage("ONC");
            } else if ( x>lSetValue) {//向左偏移，向右回轮
                //mCIR.transmit(hz, pattern4);//右
                SendSocketService.sendMessage("OND");
            } else {
                //mCIR.transmit(hz, pattern6);//右自转 rc_car左右停止
                SendSocketService.sendMessage("ONL");
            }
        }else {
            tv.setText("陀螺仪 关闭" );
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        String leftSetValue = sh.read("leftSetValue").toString();
        String rightSetValue = sh.read("rightSetValue").toString();

        left_setValue.setText(leftSetValue);
        right_setValue.setText(rightSetValue);

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

        sm.unregisterListener(this);//注销传感器的监听 别忘记注销，否则耗电贼快
    }

    public void onSlaveLocalbluetoothClicked(View view) {

        Intent intent = new Intent(VideoReceiveChatViewActivity.this, BluetoothActivity.class);
        startActivity(intent);
    }

    public void onSlaveLocalVideoMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.muteLocalVideoStream(iv.isSelected());

        FrameLayout container = (FrameLayout) findViewById(R.id.slave_local_video_view_container);
        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);
        surfaceView.setZOrderMediaOverlay(!iv.isSelected());
        surfaceView.setVisibility(iv.isSelected() ? View.GONE : View.VISIBLE);
    }

    public void onSlaveLocalAudioMuteClicked(View view) {
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

    public void onClickedSensorGYROSCOPE(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
            SensorGYROSCOPEflag = false;
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            SensorGYROSCOPEflag = true;
        }
    }


    public void onSlaveEncCallClicked(View view) {
        finish();
    }


    /**
     * 简单的Dialog
     */
    private void simpleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(VideoReceiveChatViewActivity.this);//这里的Context只能用Activity的
        builder.setIcon(R.drawable.ic_launcher).setTitle("Error").setMessage("Network is not connected！Please check the network and reconnect.").setNegativeButton("Exist",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Exist", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        AlertDialog dialog = builder.create();//一定要记得create
        dialog.show();
    }

    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), appID, mRtcEventHandler);


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
        FrameLayout container = (FrameLayout) findViewById(R.id.slave_local_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));
    }

    private void joinSignalChannel() {
        // 加入频道
        m_agoraAPI.channelJoin(channelName);
        addCallback();
    }

    private void joinVideoChannel() {
        String token = getString(R.string.agora_access_token);
        if(token.isEmpty()) {
            token = null;
        }
        mRtcEngine.joinChannel(token, channelName, "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
    }

    private void addCallback() {
        if (m_agoraAPI == null) {
            return;
        }

        m_agoraAPI.callbackSet(new AgoraAPI.CallBack() {

            @Override
            public void onChannelUserJoined(String account, int uid) {
                super.onChannelUserJoined(account, uid);
//                channelUserCount++;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        textViewTitle.setText(channelName + "(" + channelUserCount + ")");
//                    }
//                });
            }

            @Override
            public void onChannelUserLeaved(String account, int uid) {
                super.onChannelUserLeaved(account, uid);
//                channelUserCount--;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        textViewTitle.setText(channelName + "(" + channelUserCount + ")");
//                    }
//                });
            }

            @Override
            public void onLogout(final int i) {
                Log.i(LOG_TAG, "onLogout  i = " + i);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (i == IAgoraAPI.ECODE_LOGOUT_E_KICKED) { //other login the account
                            showLongToast("Other login account ,you are logout.");

                        } else if (i == IAgoraAPI.ECODE_LOGOUT_E_NET) { //net
                            showLongToast("Logout for Network can not be.");
                            finish();

                        }
                        Intent intent = new Intent();
                        intent.putExtra("result", "finish");
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });

            }


            @Override
            public void onQueryUserStatusResult(final String name, final String status) {
                Log.i(LOG_TAG, "onQueryUserStatusResult  name = " + name + "  status = " + status);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        if (status.equals("1")) {
//                            if (stateSingleMode) {
//                                textViewTitle.setText(channelName + getString(R.string.str_online));
//                            }
//
//                        } else if (status.equals("0")) {
//                            if (stateSingleMode) {
//                                textViewTitle.setText(channelName + getString(R.string.str_not_online));
//                            }
//
//                        }
//                    }
//                });
            }

            @Override
            public void onMessageInstantReceive(final String account, int uid, final String msg) {
                Log.i(LOG_TAG, "onMessageInstantReceive  account = " + account + " uid = " + uid + " msg = " + msg);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (account.equals(channelName)) {
//
//                            MessageBean messageBean = new MessageBean(account, msg, false);
//                            messageBean.setBackground(getMessageColor(account));
//                            messageBeanList.add(messageBean);
//                            adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
//                            recyclerView.scrollToPosition(messageBeanList.size() - 1);
//                        } else {
//
//                            Constant.addMessageBean(account, msg);
//
//                        }
//
//                    }
//                });
            }

            @Override
            public void onMessageChannelReceive(String channelID, final String account, int uid, final String msg) {
                Log.i(LOG_TAG, "onMessageChannelReceive  account = " + account + " uid = " + uid + " msg = " + msg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //self message had added
                        if (!account.equals(selfName)) {

                             mstr = msg;
                            if (mstr.equals("05")) {
                                //mCIR.transmit(hz, patternS);//停
                                SendSocketService.sendMessage("ONF");
                                forward_flag = false;
                                backward_flag = false;
                                fb_stop_flag = true;
                                left_flag = false;
                                right_flag = false;
                                lr_stop_flag = true;
                                stop_flag = true;

                            } else if (mstr.equals("01")) {
                                //mCIR.transmit(hz, pattern1);//前
                                SendSocketService.sendMessage("ONA");
                                forward_flag = true;
                                backward_flag = false;
                                fb_stop_flag = false;
                                stop_flag = false;

                            } else if (mstr.equals("02")) {
                                //mCIR.transmit(hz, pattern2);//后
                                SendSocketService.sendMessage("ONB");
                                forward_flag = false;
                                backward_flag = true;
                                fb_stop_flag = false;
                                stop_flag = false;

                            } else if (mstr.equals("03")) {
                                //mCIR.transmit(hz, pattern3);//左
                                SendSocketService.sendMessage("ONC");
                                left_flag = true;
                                right_flag = false;
                                lr_stop_flag = false;
                                stop_flag = false;

                            } else if (mstr.equals("04")) {
                                //mCIR.transmit(hz, pattern4);//右
                                SendSocketService.sendMessage("OND");
                                left_flag = false;
                                right_flag = true;
                                lr_stop_flag = false;
                                stop_flag = false;

                            } else if (mstr.equals("13")) {
                                //mCIR.transmit(hz, pattern5);//左自转 rc_car前后停止
                                SendSocketService.sendMessage("ONR");
                                forward_flag = false;
                                backward_flag = false;
                                fb_stop_flag = true;

                            } else if (mstr.equals("14")) {
                                //mCIR.transmit(hz, pattern6);//右自转 rc_car左右停止
                                SendSocketService.sendMessage("ONL");
                                left_flag = false;
                                right_flag = false;
                                lr_stop_flag = true;

                            } else if (mstr.equals("s")) {
                                //mCIR.transmit(hz, speed);//全速
                            } else if (mstr.equals("s1")) {
                                //mCIR.transmit(hz, speed1);//速度1
                            } else if (mstr.equals("s2")) {
                                //mCIR.transmit(hz, speed2);//速度2
                            } else if (mstr.equals("s3")) {
                                //mCIR.transmit(hz, speed3);//速度3
                            } else if (mstr.equals("L")) {
                                lightSwitch();				//开关闪关灯
                            } else if (mstr.equals("C")) {
                                cameraSwitch();			//后置摄像头
                            } else {
                                System.out.println("数据错误");
                            }
                        }

                    }
                });
            }

            @Override
            public void onMessageSendSuccess(String messageID) {

            }

            @Override
            public void onMessageSendError(String messageID, int ecode) {

            }

            @Override
            public void onError(String name, int ecode, String desc) {
                Log.i(LOG_TAG, "onError  name = " + name + " ecode = " + ecode + " desc = " + desc);
            }

            @Override
            public void onLog(String txt) {
                super.onLog(txt);
            }
        });
    }

    private void cameraSwitch() {
        mRtcEngine.switchCamera();
    }

    private boolean LightFlag=false;
    private void lightSwitch() {
        if(!LightFlag){
            mRtcEngine.setCameraTorchOn(true);
            LightFlag=true;
        }else {
            mRtcEngine.setCameraTorchOn(false);
            LightFlag=false;
        }

    }

    private void setupRemoteVideo(int uid) {
        FrameLayout container = (FrameLayout) findViewById(R.id.slave_remote_video_view_container);

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
        FrameLayout container = (FrameLayout) findViewById(R.id.slave_remote_video_view_container);
        container.removeAllViews();

//        View tipMsg = findViewById(R.id.quick_tips_when_use_agora_sdk); // optional UI
//        tipMsg.setVisibility(View.VISIBLE);
    }

    private void onRemoteUserVideoMuted(int uid, boolean muted) {
        FrameLayout container = (FrameLayout) findViewById(R.id.slave_remote_video_view_container);

        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);

        Object tag = surfaceView.getTag();
        if (tag != null && (Integer) tag == uid) {
            surfaceView.setVisibility(muted ? View.GONE : View.VISIBLE);
        }
    }

}
