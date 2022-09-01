package com.example.faceirctrlone;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

@SuppressWarnings("deprecation")
public class FaceIrCtrlOne extends Activity implements SurfaceHolder.Callback{
	private Camera mCamera; 

	//  private Button returnTomainmenu;
	private SurfaceView mSurfaceView; 
	private SurfaceHolder holder; 

	private int count;
	/**
	 * Android红外线遥控官方Demo
	 * 
	 * @description：
	 * @author ldm
	 * @date 2016-4-28 下午5:06:28
	 */
	// Android4.4之后 红外遥控ConsumerIrManager，可以被小米4调用
	private ConsumerIrManager mCIR;


	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
		/* 隐藏标题栏 */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/* 设定屏幕显示为横向 */
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(com.example.facedetectirctrl.R.layout.activity_main);
		
		// 获取系统的红外遥控服务
		mCIR = (ConsumerIrManager) getSystemService(Context.CONSUMER_IR_SERVICE);

		mSurfaceView = (SurfaceView) findViewById(com.example.facedetectirctrl.R.id.mSurfaceView); 
		holder = mSurfaceView.getHolder();
		holder.addCallback(FaceIrCtrlOne.this); 
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	@SuppressWarnings("static-access")
	public void surfaceCreated(SurfaceHolder surfaceholder) 
	{ 
		try
		{
			/* 打开相机 */
			mCamera = mCamera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
			mCamera.setPreviewDisplay(holder);
		}
		catch (IOException exception)
		{
			mCamera.release();
			mCamera = null;
		}     
	}

	public void surfaceChanged(SurfaceHolder surfaceholder,
			int format,int w,int h) 
	{
		/* 相机初始化 */
		initCamera();
		count ++;
		Log.i("changed", count + "times");
	} 

	//@Override 
	public void surfaceDestroyed(SurfaceHolder surfaceholder) 
	{ 
		stopCamera();
		mCamera.release();
		mCamera = null;
	}


	/* 相机初始化的method */
	private void initCamera() 
	{ 
		if (mCamera != null) 
		{
			try 
			{     	  
				Camera.Parameters parameters = mCamera.getParameters();
				parameters.setPictureFormat(PixelFormat.JPEG); 
				//	        parameters.setPictureSize(2560, 1920);
				//	        parameters.setPictureSize(1024, 768);
				//	        parameters.setPictureSize(720, 480);
				/*尺寸设置不当会引起黑屏*/

				mCamera.setParameters(parameters); 
				mCamera.startPreview();
				mCamera.setFaceDetectionListener(new MyFaceDetectionListener());
				// start face detection only *after* preview has started
				if (parameters.getMaxNumDetectedFaces() > 0){
					// camera supports face detection, so can start it:
					mCamera.startFaceDetection();
				}
			} 
			catch (Exception e) 
			{ 
				e.printStackTrace(); 
			} 
		} 
	} 

	/* 停止相机的method */ 
	private void stopCamera() 
	{ 
		if (mCamera != null) 
		{ 
			try 
			{ 
				/* 停止预览 */
				mCamera.stopPreview(); 
			} 
			catch(Exception e) 
			{ 
				e.printStackTrace(); 
			} 
		} 
	}



	private class MyFaceDetectionListener implements Camera.FaceDetectionListener {
		private int faceX=0;
		private int faceY=0;
		boolean fMoveFlag = false;//设置标志位,只执行一次,不连续发送
		boolean bMoveFlag = false;
		boolean lMoveFlag = false;
		boolean rMoveFlag = false;
		boolean stopFlag = false;
		
		
		@Override
		public void onFaceDetection(Camera.Face[] faces, Camera camera) {
			if (faces.length > 0){
				Log.d("FaceDetection", "face detected: "+ faces.length +
						" Face 1 Location X: " + faces[0].rect.centerX() +
						"Y: " + faces[0].rect.centerY() );
				//	                Ctrl irCtrl=new Ctrl(faces[0].rect.centerX(),faces[0].rect.centerY());
				//	                irCtrl.start();
				faceX=faces[0].rect.centerX();
				faceY=faces[0].rect.centerY();
				if(faceY<-100&&!fMoveFlag){
					int[] pattern = { 9000, 4500,
							560, 560, 560, 560, 560, 560, 560, 560,		 560, 560, 560, 560, 560, 560, 560, 560, 
							560,1690, 560,1690, 560,1690, 560,1690,		 560,1690, 560,1690, 560,1690, 560,1690,
							560, 560, 560, 560, 560, 560, 560,1690,		 560,1690, 560, 560, 560, 560, 560, 560, 
							560,1690, 560,1690, 560,1690, 560, 560,		 560, 560, 560,1690, 560,1690, 560,1690, 
							560,42020, 9000,2250,560,98190 };

					// 在38KHz条件下进行模式转换
					mCIR.transmit(38000, pattern);
					fMoveFlag=true;
					bMoveFlag=false;
					stopFlag=false;
					//mCIR.transmit(38000, pattern);
				}
				else if(faceY>100&&!bMoveFlag){
					int[] pattern = { 9000, 4500,
							560, 560, 560, 560, 560, 560, 560, 560,		 560, 560, 560, 560, 560, 560, 560, 560, 
							560,1690, 560,1690, 560,1690, 560,1690,		 560,1690, 560,1690, 560,1690, 560,1690,
							560, 560, 560,1690, 560, 560, 560, 560,		 560,1690, 560, 560, 560, 560, 560, 560, 
							560,1690, 560, 560, 560,1690, 560,1690,		 560, 560, 560,1690, 560,1690, 560,1690, 
							560,42020, 9000,2250,560,98190 };

					mCIR.transmit(38000, pattern);
					bMoveFlag=true;
					fMoveFlag=false;
					stopFlag=false;
					//mCIR.transmit(38000, pattern);
				}
				else if(faceX<-200&&!lMoveFlag){
					// 一种交替的载波序列模式，通过毫秒测量
					int[] pattern = { 9000, 4500,
							560, 560, 560, 560, 560, 560, 560, 560,		 560, 560, 560, 560, 560, 560, 560, 560, 
							560,1690, 560,1690, 560,1690, 560,1690,		 560,1690, 560,1690, 560,1690, 560,1690,
							560, 560, 560, 560, 560,1690, 560, 560,		 560,1690, 560, 560, 560, 560, 560, 560, 
							560,1690, 560,1690, 560, 560, 560,1690,		 560, 560, 560,1690, 560,1690, 560,1690, 
							560,42020, 9000,2250,560,98190 };

					// 在38KHz条件下进行模式转换
					mCIR.transmit(38000, pattern);
					lMoveFlag=true;
					rMoveFlag=false;
					stopFlag=false;
					//mCIR.transmit(38000, pattern);
					/*for(int i=0;i<2000;i++){
//						for(;;){}
					}
					
					if(!stopFlag){
						int[] patternS = { 9000, 4500, 
								560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
								560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 
								560, 560,	560, 560, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560, 
								560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690, 
								560, 42020, 9000, 2250, 560, 98190 };
						mCIR.transmit(38000, patternS);
						stopFlag=true;
						rMoveFlag=false;
						lMoveFlag=false;
						bMoveFlag=false;
						fMoveFlag=false;
						mCIR.transmit(38000, patternS);
					}*/
				}
				else if(faceX>200&&!rMoveFlag){
					
					int[] pattern = { 9000, 4500,
							560, 560, 560, 560, 560, 560, 560, 560,		 560, 560, 560, 560, 560, 560, 560, 560, 
							560,1690, 560,1690, 560,1690, 560,1690,		 560,1690, 560,1690, 560,1690, 560,1690,
							560, 560, 560,1690, 560,1690, 560, 560,		 560,1690, 560, 560, 560, 560, 560, 560, 
							560,1690, 560, 560, 560, 560, 560,1690,		 560, 560, 560,1690, 560,1690, 560,1690, 
							560,42020, 9000,2250,560,98190 };

					mCIR.transmit(38000, pattern);
					rMoveFlag=true;
					lMoveFlag=false;
					stopFlag=false;
					//mCIR.transmit(38000, pattern);
					/*for(int i=0;i<2000;i++){
						
					}
					if(!stopFlag){
						int[] patternS = { 9000, 4500, 
								560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
								560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 
								560, 560,	560, 560, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560, 
								560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690, 
								560, 42020, 9000, 2250, 560, 98190 };
						mCIR.transmit(38000, patternS);
						stopFlag=true;
						rMoveFlag=false;
						lMoveFlag=false;
						bMoveFlag=false;
						fMoveFlag=false;
						mCIR.transmit(38000, patternS);
					}*/
				}
				else if(faceX<200&&faceX>-200&&faceY<100&&faceY>-100){

					if(!stopFlag){
						int[] patternS = { 9000, 4500, 
								560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
								560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 
								560, 560,	560, 560, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560, 
								560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690, 
								560, 42020, 9000, 2250, 560, 98190 };
						mCIR.transmit(38000, patternS);
						stopFlag=true;
						rMoveFlag=false;
						lMoveFlag=false;
						bMoveFlag=false;
						fMoveFlag=false;
						//mCIR.transmit(38000, patternS);
					}
				}
			}else{
				System.currentTimeMillis();
				if(!stopFlag){
					int[] patternS = { 9000, 4500, 
							560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
							560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 
							560, 560,	560, 560, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560, 
							560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690, 
							560, 42020, 9000, 2250, 560, 98190 };
					mCIR.transmit(38000, patternS);
					stopFlag=true;
					rMoveFlag=false;
					lMoveFlag=false;
					bMoveFlag=false;
					fMoveFlag=false;
					//mCIR.transmit(38000, patternS);
				}
			}
		}
	}
}
