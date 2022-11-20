package com.onLineDetect.service;

import java.net.Socket;

import com.csst.videotalk.FfmpegActivity;
import com.csst.videotalk.VideoTalkActivity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * 开启服务，检测是否有用户上线
 *
 * @author androidtv.power@gmail.com
 *
 */
public class OnLineDetectService extends Service {
	//
//	private Handler mHandler;

	final String TAG="OnLineDetectService";

	private NetRateBinder netRateBinder=new NetRateBinder();


	private final int DetectTimeRate=3000;

	private String localIpHead="";
	private Socket clientSocket = null;
	private static final int PORT=5432;   //和被叫的监听线程通信的端口


	/**
	 *
	 *
	 */
	public void detecting(){
		int i=1;
		while(i<255){
			String destIp=(localIpHead+i).trim();

			try {
				Log.e(TAG,"======================准备连接"+destIp);

				clientSocket = new Socket(destIp,PORT);
				clientSocket.setSoTimeout(20);

			} catch (Exception e) {
				Log.e(TAG,i+"没有在线"+e);
//     				    testTimes++;

			}
			Log.e("222","########################################");
			if(clientSocket!=null){
				Log.e("222","在线ip："+clientSocket.getInetAddress().getHostAddress());
				i++;
			}
		}

	}



//	/**
//	 * 定义线程周期性地获取是否有用户上线
//	 */
//	private Runnable mRunnable = new Runnable() {
//		// 每3秒钟获取一次数据，求平均，以减少读取系统文件次数，减少资源消耗
//		@Override
//		public void run() {
////			Log.e(TAG,"检测上线服务正在运行当中...."+localIpHead);
//			detecting();
//
//			mHandler.postDelayed(mRunnable, DetectTimeRate);
//
//		}
//	};

	/**
	 * 通过聚成Binder 来实现Ibinder 类
	 *
	 * @author User
	 *
	 */
	public class NetRateBinder extends Binder{
		public void startGetNetRate(){
			Log.e(TAG,"Yes, start==========");

		}

		public void stopGetNetRate(){
			Log.e(TAG,"no, stop ==========");

		}
	}

	/**
	 * 绑定时回调的方法
	 *
	 */
	@Override
	public IBinder onBind(Intent intent) {
		Log.e(TAG,"---- NetRateBinder is onRun! ----");

		return netRateBinder;
	}

	/**
	 * 解除绑定时回调的方法
	 *
	 */
	@Override
	public boolean onUnbind(Intent intent) {
		Log.e(TAG,"---- NetRateBinder is quited ! ----");
		return true;
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e("LocalService", "Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.

//		mHandler.postDelayed(mRunnable, 500);


		return START_STICKY;
	}


	private String getLocalIpHead(){
		WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		Log.d(TAG, "int ip "+ipAddress);
		if(ipAddress==0)return null;
		String temp= ((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."
				+(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));

		return temp.substring(0, temp.lastIndexOf('.')+1);

	}

	/**
	 * 在服务结束时删除消息队列
	 *
	 */
	@Override
	public void onDestroy() {

//		mHandler.removeCallbacks(mRunnable);
		super.onDestroy();
	};

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG,"---- onCreate ! ----");


//		mHandler = new Handler() {
//			@Override
//			public void handleMessage(Message msg) {
//				super.handleMessage(msg);
//				if (msg.what == 1){
//					
//				}
//			}
//		};
//		mHandler.postDelayed(mRunnable, 500);
		localIpHead=getLocalIpHead();
		new Thread(){
			@Override
			public void run(){
				while(true){


					try {
						sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


					detecting();

				}
			}
		}.start();

	}

}
