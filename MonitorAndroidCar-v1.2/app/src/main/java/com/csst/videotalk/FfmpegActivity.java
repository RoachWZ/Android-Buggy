package com.csst.videotalk;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.csst.ffmpeg.control.ConnectListeningServer;
import com.onLineDetect.service.OnLineDetectService;

import hlq.view.activity.BluetoothActivity;
import io.agora.tutorials1v1vcall.R;

public class FfmpegActivity extends Activity {
	/** Called when the activity is first created. */
	String TAG="FfmpegActivity";
	ImageButton video_Talk,video_monitor;
	EditText edit_To;
	TextView textViewShowIP;
	private ListView denifinitonList;
	private List<Map<String, Object>> MovieDefinitions=new ArrayList<Map<String, Object>>();      //Data of MoiveSourceGridView
	String localIp="";
	String localIpHead="";
	String VideoTalk="com.csst.ffmpeg.video_talk";
	String VideoMonitor="com.csst.ffmpeg.video_monitor";
	private Socket clientSocket = null;
	private PrintStream outStream=null;

	private static  String HOSTIP="192.168.10.177";
	private static final int PORT=5432;   //和被叫的监听线程通信的端口

	OnLineDetectService.NetRateBinder netRateBinder;

	/**
	 * 代码是在手机上运行的，也包含一个平板上的apk(平板的ui好看点但是bug多，先开启wifi)
	 * 对讲的两个设备应在同一个WLAN中。输入Ip后点击开始了其他的看代码吧！
	 *
	 *
	 */
	private ServiceConnection conn=new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			Log.e(TAG,"Activity 断开连接service");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			Log.e(TAG,"Activity 准备连接 service");
			netRateBinder=(OnLineDetectService.NetRateBinder)service;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);
		textViewShowIP=(TextView)findViewById(R.id.textViewShowIP);
		edit_To = (EditText)findViewById(R.id.edit_to);
		video_Talk=(ImageButton) findViewById(R.id.imageButton1);
		video_monitor=(ImageButton) findViewById(R.id.imageButton2);
		ButtonListenser btnListener = new ButtonListenser();
		video_Talk.setOnClickListener(btnListener);
		video_monitor.setOnClickListener(btnListener);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
		localIp=getlocalIp();
		new ConnectListeningServer(this).start();
		//this.setTitle(this.getTitle()+"  (请把设备至于同一 WLAN 中)"+"  -- "+getlocalIp()+" # 交流QQ：480474041");
		textViewShowIP.setText("  (请把设备至于同一 WLAN 中)"+"  -- "+getlocalIp());
		if(localIp!=""){
			localIpHead=localIp.substring(0, localIp.lastIndexOf('.')+1);
		}
	}


	/**
	 * int--> (ip)int
	 *
	 * @return
	 */
	public String getlocalIp(){
		WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo mWifiInfo = wifiManager.getConnectionInfo();
		if(mWifiInfo ==null){
			return "ip=null";
		}else{
			int ipAddress=mWifiInfo.getIpAddress();
			Log.e("222","!!=="+ipAddress+"===");
			if(ipAddress==0)  return "ip=null";
			return "ip="+((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."
					+(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));
		}
	}


	/**
	 * 开启对讲线程
	 *
	 */
	public void startCallSomeBody(){
		new Thread(){
			@Override
			public void run(){
				boolean connectFlag=true;
				HOSTIP=edit_To.getText().toString();
				if(!HOSTIP.equals(localIp)){
					int time=0;
					while(connectFlag){
						try {
							clientSocket = new Socket(HOSTIP,PORT);
							clientSocket.setSoTimeout(5000);
						} catch (Exception e) {
							System.out.println("Client: 连接错误"+e);
						}
						if(clientSocket!=null){
							Log.e(TAG,"被叫的信息："+clientSocket.getInetAddress().getHostAddress());
							connectFlag=false;
							Intent intent = new Intent();
							intent.setClass(FfmpegActivity.this, VideoTalkActivity.class);
							intent.putExtra("CONNECTIP",clientSocket.getInetAddress().getHostAddress());
							intent.putExtra("CONNECTTYPE", "DOCALLER");
							FfmpegActivity.this.startActivity(intent);
						}else{
							try {
								sleep(500);
								time++;
								System.out.println("连接不上啊");
								if(time==20){
									connectFlag=false;
//								    	Toast.makeText(getApplicationContext(), "对方不在线！",
//								    		     Toast.LENGTH_SHORT).show();
								}

							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

//		 				Intent intent = new Intent();
//		 				intent.setClass(FfmpegActivity.this, MainActivity.class);
//		 				intent.putExtra("CONNECTIP",HOSTIP);
//		 				intent.putExtra("CONNECTTYPE", "DOCALLER");
//		 				FfmpegActivity.this.startActivity(intent);

				}else{
//				    	Toast.makeText(getApplicationContext(), "你不能呼叫自己！",
//				    		     Toast.LENGTH_SHORT).show();
				}

			}
		}.start();
	}
	class ButtonListenser implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId() == R.id.imageButton1) {
				startCallSomeBody();
			}else if(v.getId() == R.id.imageButton2){
				startCallSomeBody();  //备用的
			}
		}
	}//buttonlistening is over here

	public void onSlaveLocalModelBluetoothClicked(View view) {

		Intent intent = new Intent(FfmpegActivity.this, BluetoothActivity.class);
		startActivity(intent);
	}

	//=======================与Activity生命周期相关========================================
	/**
	 * 启动Activity 的时候会被回调，
	 *
	 */
	@Override
	protected void onStart(){
		super.onStart();
//		Intent intent = new Intent();
//		intent.setAction("ZLB.LIVE_TV.OnLineDetectService");
//        bindService(intent, conn, Service.BIND_AUTO_CREATE);
		Log.d(TAG, "--onStart");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.d(TAG, "--onResume");
		if(clientSocket!=null){
			try {
				clientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG, "--onpause");

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub

		super.onStop();
		super.onPause();
		Log.d(TAG, "--onStop");

	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
//    	if(netRateBinder!=null){
//        	netRateBinder.stopGetNetRate();
//    	}
// 		unbindService(conn);
		super.onDestroy();

	}

}