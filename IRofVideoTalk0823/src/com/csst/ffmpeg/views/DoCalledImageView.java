package com.csst.ffmpeg.views;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.csst.ffmpeg.FFMpegIF;
import com.csst.videotalk.VideoTalkActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 显示主叫的映像画面,surface 双缓冲
 * 
 * @author User
 *
 */
public class DoCalledImageView extends SurfaceView implements SurfaceHolder.Callback 
{

	private SurfaceHolder doCalledImageViewHolder;        
	private int sWidth ;                  
	private int sHeight;                  
	private int displayTime=1;	                      
	private String TAG="DoCalledImageView";
	private Timer timer=null;                  
	private boolean playFlag=true;            
	private boolean firstSuccessFlag=true;  
	
    //private boolean isPreview = false; 
    public Camera myCamera = null; 
    private AutoFocusCallback myAutoFocusCallback = null; 

	/*设定录像的像素大小*/
//	private final int CAMERA_W = 640;
//	private final int CAMERA_H = 480;
    private int cameraId = 0;
	
    
	private Boolean startEncodeNow=false;
	
	public void startEncodeNow(boolean b){
		this.startEncodeNow=b;
	}
	
	
    /**
     * 本构造方法用于测试使用
     *    
     * @param context  环境变量上下文
     * @param vector 
     */
	public DoCalledImageView(Context context){
		super(context);
	    doCalledImageViewHolder = this.getHolder();// 获取holder
	    doCalledImageViewHolder.addCallback(this);	
	    getHolder().setFormat(PixelFormat.TRANSLUCENT);  
	    setFocusable(true); 
	}
	
	
	
	/**
	 * 本构造方法可以通过资源文件来进行构造并设置样式
	 * @param context     环境变量上下文
	 * @param attrs       资源属性文件
	 */
	public DoCalledImageView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    doCalledImageViewHolder = this.getHolder();// 获取holder
	    doCalledImageViewHolder.addCallback(this);	
	    setFocusable(true); // 设置焦点  

	}

	/**
	 * construction
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public DoCalledImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	  
	
    /**
     * This is called immediately after any structural changes
     * (format or size) have been made to the surface.
     * 
     */
    
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		// TODO Auto-generated method stub
		initCamera();  
	}
	
	
	/**
	 * This is called immediately after the surface is first created.
	 * 
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	    sWidth=doCalledImageViewHolder.getSurfaceFrame().width();    
	    sHeight=doCalledImageViewHolder.getSurfaceFrame().height();  
		Log.d(TAG,"Surface_Width" +sWidth+"Surface_Height"+sHeight);

		/*cameraId = findFrontFacingCamera();
		Log.i(TAG, "front face id="+cameraId);*/
		myCamera = Camera.open(cameraId);  
		
		if(myCamera == null)
			Log.e(TAG, "Open camera failed");
		else {
			try {
				myCamera.setPreviewDisplay(doCalledImageViewHolder);  
				Log.i(TAG, "SurfaceHolder.Callback: surfaceCreated!");
			} catch (IOException e) { 
				// TODO Auto-generated catch block
				if(null != myCamera){
					myCamera.release();
					myCamera = null;
				}
				e.printStackTrace();
			}  
		}
	}
      

	/**
	 * 
	 * Called when surface is Destroyed.
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
		//没有成功的停止！！
		 Log.i(TAG, "SurfaceHolder.Callback：Surface Destroyed"); 

		 if(null != myCamera) 
		 { 
		   myCamera.setPreviewCallback(null); /*在启动PreviewCallback时这个必须在前不然退出出错。 这里实际上注释掉也没关系*/ 

		   myCamera.stopPreview(); 
		   //isPreview = false; 
		   myCamera.release(); 
		   myCamera = null; 
		 }
		doCalledImageViewHolder = null;
	}
 

	/*
	 * 销毁摄像头
	 * 	1、第一步：将摄像头的预览清空
		2、第二步：停止预览效果
		3、第三步：释放摄像头
		因为系统默认只能同时开启一个摄像头不管是前置摄像头还是后置摄像头，所以不用的时候一定要释放
		4、第四步：置空摄像头对象
	 ***/
	public void destroyCamera() {
		if (myCamera == null) {
			return;
		}
		myCamera.setPreviewCallback(null);
		myCamera.stopPreview();
		myCamera.release();
		myCamera = null;
		doCalledImageViewHolder = null;

	}

	/**
	 * 
	 * 睡眠等待展示，待优化
	 * 
	 */
	public void waitForDisplay(){
		 try {
				Thread.sleep(displayTime);
			 } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
	}
	
	//初始化相机  
    public void initCamera() {  
    	
		//自动聚焦变量回调  
		myAutoFocusCallback = new AutoFocusCallback() {  

	        public void onAutoFocus(boolean success, Camera camera) {  
	            // TODO Auto-generated method stub  
	            if(success)//success表示对焦成功  
	            {  
	                Log.i(TAG, "myAutoFocusCallback: success...");  
	                //myCamera.setOneShotPreviewCallback(null);  
	            }  
	            else  
	            {  
	                //未对焦成功  
	                Log.i(TAG, "myAutoFocusCallback: failed...");  
	            }  
	        }  
	    }; 
    	
    	
	    /*if(isPreview) {  
	        myCamera.stopPreview();  
	    }*/  
	    if(null != myCamera) {             
	        
	    	Camera.Parameters myParam = myCamera.getParameters();  
	    	
	    	/*枚举摄像头支持的帧率*/
	        List<int[]> range=myParam.getSupportedPreviewFpsRange();   
	        Log.d(TAG, "range:"+range.size());   
	        for(int j=0;j<range.size();j++) {   
	            int[] r=range.get(j);   
	            for(int k=0;k<r.length;k++) {   
	                Log.d(TAG, "Preview fps:"+r[k]/1000);   
	            }   
	        } 
	        
	
	        //查询camera支持的picturesize和previewsize  
	        List<Size> pictureSizes = myParam.getSupportedPictureSizes();  
	        List<Size> previewSizes = myParam.getSupportedPreviewSizes();  
	        for(int i=0; i<pictureSizes.size(); i++){  
	            Size size = pictureSizes.get(i);  
	            Log.i(TAG, "摄像头支持的pictureSizes: width = "+size.width+"height = "+size.height);  
	        }  
	        for(int i=0; i<previewSizes.size(); i++){  
	            Size size = previewSizes.get(i);  
	            Log.i(TAG, "摄像头支持的previewSizes: width = "+size.width+"height = "+size.height);  
	        }  
	        
	        //设置数据格式
	        myParam.setPictureFormat(ImageFormat.JPEG); //设置拍照后存储的图片格式  
	        myParam.setPreviewFormat(ImageFormat.NV21); //设置预览的数据格式  
	
	        /*设置大小*/
	        myParam.setPreviewSize(VideoTalkActivity.CAMERA_W, VideoTalkActivity.CAMERA_H);  
	        
	        /*设置方向*/
//	        myParam.set("rotation", 90);                
//	        myCamera.setDisplayOrientation(90);    
	        if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
	        	//判断系统版本是否大于等于2.2
//	        	myCamera.setDisplayOrientation(90);//旋转90°，前提是当前页portrait，纵向
	        } 
	        else {
	        	//系统版本在2.2以下的采用下面的方式旋转
	        	if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
	        		myParam.set("orientation", "portrait");
	        		myParam.set("rotation", 90);
	        	}
	        	if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
	        		myParam.set("orientation", "landscape");
	        		myParam.set("rotation", 90);
	        	}
	        }	        
	        
	        /*设置帧率，都不起作用，很奇怪，设完后读回来是正确的，但是w callback频率并没有改变，这个参数会影响编码性能*/
//	        myParam.setPreviewFpsRange(20000, 20000);    	//设置无效果
//	        myParam.setPreviewFrameRate(5);				//设置无效果

//	        myParam.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);  //台电平板不支持
	        
	        /*把参数设置到摄像头*/
	        myCamera.setParameters(myParam);
	        
	        /*设置预览回调*/
	        myCamera.setPreviewCallback(mPreviewCallback);

	        myCamera.startPreview();  
	        myCamera.autoFocus(myAutoFocusCallback);  
	        
	        //isPreview = true;  
	    }  
    }

    Camera.PreviewCallback  mPreviewCallback = new Camera.PreviewCallback() {
		
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			/*preview  的回调函数，每显示一帧，就会调用此函数
			考虑到编码所需时间较多，这里调用了一个AsyncTask来完成真正的编码工作*/
			if(startEncodeNow){
				EncodeTask mEncTask= new EncodeTask(data);
				mEncTask.execute((Void)null);
//				Log.e(TAG,"PreviewCallback");
			}
				
		}
	};
	
	private int findFrontFacingCamera() {
		int cameraId = -1;
		//找前置摄像头
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				Log.d(TAG, "Camera found");
				cameraId = i;
				break;
			}
		}
		if(cameraId<0) {
			/*没有前置摄像头就用默认的摄像头*/
			cameraId = 0;
		}
		return cameraId;
	}

	/**
	 * 
	 * 
	 * @author User
	 *
	 */
	private class EncodeTask extends AsyncTask<Void, Void, Void>{

		private byte[] mData;
        //构造函数
		EncodeTask(byte[] data){
			this.mData = data;
		}
        
        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
        	/*调用JNI，完成编码一帧的工作*/
			FFMpegIF.Encoding(mData, mData.length, null);
			return null;
        }
    } 
}

