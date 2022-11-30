package com.csst.videotalk;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.csst.ffmpeg.FFMpegIF;
import com.csst.ffmpeg.views.BeCalledImageView;
import com.csst.ffmpeg.views.DoCalledImageView;
import io.agora.tutorials1v1vcall.R;

/**
 * 语音对讲
 *
 * @author User
 *
 */
public class AudioTalkActivity extends Activity  {
	enum RunState {
		RUN_STATE_IDLE,
		RUN_STATE_READY,
		RUN_STATE_RUNNING,
		RUN_STATE_STOP,
	};

	private final int beCalledImageDrawRate=100;    //被叫的影响的刷新间隔时间，单位 毫秒

	private String tag = "ffmpegdemo";

	/*设定录像的像素大小*/
	private final int CAMERA_W = 320;
	private final int CAMERA_H = 240;

	private  int port1=5444;    //主叫方使用port1 进行编码通信端口,port2进行解码通信端口
	private  int port2=5446;    //被叫方使用port1 进行解码通信端口,port2进行编码通信端口
	private String connectIp;
	private String connectType;


	/*控件变量声明*/
	private ImageButton btn_photo;
	private ImageButton btn_stop;
	public   static Bitmap beCalledBitmap;

	private BeCalledImageView beCalledImageView;

//	private ImageView beCalledImageView;


	private DoCalledImageView doCalledImageView;

	private RunState decodeState;
	private RunState encodeState;

//    public static int[] colors;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_talk);


		decodeState = RunState.RUN_STATE_IDLE;
		encodeState = RunState.RUN_STATE_IDLE;

		btn_photo = (ImageButton)findViewById(R.id.btn_photo);
		btn_stop = (ImageButton)findViewById(R.id.btn_stop);
		ButtonListenser btnListener = new ButtonListenser();
		btn_photo.setOnClickListener(btnListener);
		btn_stop.setOnClickListener(btnListener);

		//获取解码显示区
		beCalledImageView=(BeCalledImageView)findViewById(R.id.imgview2);
//		beCalledImageView=(ImageView)findViewById(R.id.imgview2);

		doCalledImageView=(DoCalledImageView)findViewById(R.id.previewSV);
		Log.i(tag, "ffmpeg version is " + FFMpegIF.GetFFmpegVer());
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		connectIp=bundle.getString("CONNECTIP");
		connectType=bundle.getString("CONNECTTYPE");

//	    colors = new int[1280*720];
	}





	public void startVideoCall(){
		FFMpegIF.Init();

		//启动编码的线程
		if(encodeState == RunState.RUN_STATE_IDLE) {
			//启动线程，在线程中启动编码
			Log.i(tag, "start encode thread");
			encodeState = RunState.RUN_STATE_READY;
			//create decode thread and run
			EncodeThread encthread = new EncodeThread();
			new Thread(encthread).start();
		}

		//启动解码的线程
		if(decodeState == RunState.RUN_STATE_IDLE) {
			//启动线程，在线程中启动解码，因为解码会一直等待数据，如果不用线程，则程序会阻塞在ffmpegif.StartDecode()中
			Log.i(tag, "start decode thread");
			decodeState = RunState.RUN_STATE_READY;
			//create decode thread and run
			DecodeThread decThread = new DecodeThread();
			new Thread(decThread).start();
		}
	}

	/**
	 * 按键监听部分
	 *
	 * @author User
	 *
	 */
	class ButtonListenser implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId() == R.id.btn_photo) {
				//初始化JNI接口
//				FFMpegIF.Init();
//
//				//启动编码的线程
//				if(encodeState == RunState.RUN_STATE_IDLE) {
//					//启动线程，在线程中启动编码
//					Log.i(tag, "start encode thread");
//					encodeState = RunState.RUN_STATE_READY;
//					//create decode thread and run
//					EncodeThread encthread = new EncodeThread();
//					new Thread(encthread).start();
//				}
//
//				//启动解码的线程
//				if(decodeState == RunState.RUN_STATE_IDLE) {
//					//启动线程，在线程中启动解码，因为解码会一直等待数据，如果不用线程，则程序会阻塞在ffmpegif.StartDecode()中
//					Log.i(tag, "start decode thread");
//					decodeState = RunState.RUN_STATE_READY;
//					//create decode thread and run
//					DecodeThread decThread = new DecodeThread();
//					new Thread(decThread).start();
//				}
			}
			//停止编解码线程
			else if(v.getId() == R.id.btn_stop)
			{
				//结束时先调用DeInit,最后调用Release
				Log.i(tag, "ffmpegif DeInit");
				FFMpegIF.DeInit();

				if(decodeState != RunState.RUN_STATE_IDLE) {
					decodeState = RunState.RUN_STATE_STOP;
					Log.i(tag, "ffmpegif StopDecode");
					FFMpegIF.StopDecode();
					Log.i(tag, "Decode stopped");
				}

				if(encodeState != RunState.RUN_STATE_IDLE) {
					encodeState = RunState.RUN_STATE_STOP;
					Log.i(tag, "ffmpegif StopEncode");
					FFMpegIF.StopEncode();
					Log.i(tag, "Encode stopped");
					encodeState = RunState.RUN_STATE_IDLE;
					Log.i(tag, "Encode stopped 2");
				}

				FFMpegIF.Release();
			}
		}

	}

//	 final  Handler UiMangerHandler = new Handler(){
//			@Override
//			public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//				switch(msg.what){
//				case 0:
//					beCalledImageView.setImageBitmap(beCalledBitmap);
//
//					break;
//				}
//			}
//	 };

	/**
	 *
	 * 睡眠等待展示，待优化
	 *
	 */
	public void waitForDisplay(){
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 解码线程
	 *
	 * @author User
	 *
	 */
	class DecodeThread implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(decodeState == RunState.RUN_STATE_READY) {
//				EditText textFrm = (EditText)findViewById(R.id.edit_from);

//				String urlFrm = "rtp://"+textFrm.getText().toString();

				String urlFrm="rtp://"+connectIp+":"+port1;
				Log.i(tag, "from:"+urlFrm);

				if(FFMpegIF.StartDecode(urlFrm)<0) {
					Log.e(tag, "Start decode failed");
					FFMpegIF.StopDecode();
					decodeState = RunState.RUN_STATE_IDLE;
					return;
				}

				Log.i(tag, "Start decode successed");
				decodeState = RunState.RUN_STATE_RUNNING;

				/*获取接收到的视频resolution，以设置bitmap的参数*/
				int w, h;
				do {
					w = FFMpegIF.GetWidth();
					h = FFMpegIF.GetHeight();
					try{
						Thread.sleep(beCalledImageDrawRate); //让当前线程休眠100毫秒
					}catch(InterruptedException ex){
						ex.printStackTrace();
					}
				} while(w==0&&h==0&&decodeState==RunState.RUN_STATE_RUNNING);

				if(decodeState==RunState.RUN_STATE_RUNNING) {
					Log.i(tag, "width="+w+" height="+h);
					beCalledBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
				}
			}
			while(decodeState == RunState.RUN_STATE_RUNNING)
			{

				if(FFMpegIF.Decoding(beCalledBitmap)<=0) {//再试一次
//						if(FFMpegIF.Decoding(beCalledBitmap)<0){
//							Log.e(tag,"还是失败？？？？");
//
//							Thread.yield();
//						}else{
//							beCalledImageView.drawBecalledImage();
//							waitForDisplay();
//						}

				}else{  //解码成功，直接的刷新数据
					if(beCalledImageView!=null){
						beCalledImageView.drawBecalledImage();
						beCalledBitmap.recycle();
						beCalledBitmap = Bitmap.createBitmap(CAMERA_W, CAMERA_H, Bitmap.Config.ARGB_8888);
					}

				}
			}
			decodeState = RunState.RUN_STATE_IDLE;
		}

	}

	/**
	 * 编码的线程
	 *
	 * @author User
	 *
	 */
	class EncodeThread implements Runnable {
		Message 	msg;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(encodeState == RunState.RUN_STATE_READY) {
				String urlTo="rtp://"+connectIp+":"+port2;

				if(FFMpegIF.StartEncode(urlTo, CAMERA_W, CAMERA_H,10)<0) {
					Log.e(tag, "Start encode failed");
					FFMpegIF.StopEncode();
					return;
				}

				encodeState = RunState.RUN_STATE_RUNNING;
				doCalledImageView.startEncodeNow(true);
			}
			Log.i(tag, "Start encode successed");

		}
	}
	@Override
	protected void onStart(){
		new Thread(){
			@Override
			public void run(){
				Log.e(tag,"connectType:"+connectType);

				if(connectType.equals("BECALLER")){
					int temp=port1;
					port1=port2;
					port2=temp;
					Log.e(tag,"端口转换 connectType:"+connectType);
					Log.e(tag,port1+"  "+port2);
				}
				try {
					sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				startVideoCall();

			}
		}.start();
		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		beCalledImageView = null;
		decodeState = RunState.RUN_STATE_STOP;

		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		FFMpegIF.DeInit();

		beCalledImageView=null;

		decodeState = RunState.RUN_STATE_STOP;
		FFMpegIF.StopDecode();



		FFMpegIF.StopEncode();
		encodeState = RunState.RUN_STATE_IDLE;



//		if(decodeState != RunState.RUN_STATE_IDLE) {
//			decodeState = RunState.RUN_STATE_STOP;
//			FFMpegIF.StopDecode();
//		}
//		
//
//		if(encodeState != RunState.RUN_STATE_IDLE) {
//			encodeState = RunState.RUN_STATE_STOP;
//			FFMpegIF.StopEncode();
//			encodeState = RunState.RUN_STATE_IDLE;
//		}

		FFMpegIF.Release();
		super.onDestroy();

	}

}
