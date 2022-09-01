package org.wangzheng.cameraview;

import org.wangzheng.CameraTest.R;
import org.wangzheng.ctrol.CtrlThread;
import org.wangzheng.imagecompress.ImageCompress;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.ConsumerIrManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

@SuppressWarnings("deprecation")
public class CameraTest extends Activity {

	SurfaceView sView;
	SurfaceHolder surfaceHolder;
	int screenWidth, screenHeight;
	Camera camera; // 定义系统所用的照相机
	boolean isPreview = false; // 是否在浏览中
	private String ipname;
	// Android4.4之后 红外遥控ConsumerIrManager，可以被小米4调用
	private ConsumerIrManager mCIR;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		//应用运行时，保持屏幕高亮，不锁屏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   
		// 设置全屏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);
		// 获取系统的红外遥控服务
		mCIR = (ConsumerIrManager) getSystemService(Context.CONSUMER_IR_SERVICE);
		// 获取IP地址
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		ipname = data.getString("ipname");
		Thread ctrl = new CtrlThread(ipname, mCIR);
		ctrl.start();

		screenWidth = 640;
		screenHeight = 480;
		sView = (SurfaceView) findViewById(R.id.sView); // 获取界面中SurfaceView组件
		surfaceHolder = sView.getHolder(); // 获得SurfaceView的SurfaceHolder

		// 为surfaceHolder添加一个回调监听器
		surfaceHolder.addCallback(new Callback() {
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				initCamera(); // 打开摄像头
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// 如果camera不为null ,释放摄像头
				if (camera != null) {
					if (isPreview)
						camera.stopPreview();
					camera.release();
					camera = null;
				}
				System.exit(0);
			}
		});
		// 设置该SurfaceView自己不维护缓冲
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}


	private void initCamera() {
		if (!isPreview) {
			camera = Camera.open();
		}
		if (camera != null && !isPreview) {
			try {
				Camera.Parameters parameters = camera.getParameters();
				parameters.setPreviewSize(screenWidth, screenHeight); // 设置预览照片的大小
				parameters.setPreviewFpsRange(20, 30); // 每秒显示20~30帧
				parameters.setPictureFormat(ImageFormat.NV21); // 设置图片格式
				parameters.setPictureSize(screenWidth, screenHeight); // 设置照片的大小
				// camera.setParameters(parameters); // android2.3.3以后不需要此行代码
				camera.setPreviewDisplay(surfaceHolder); // 通过SurfaceView显示取景画面
				camera.setPreviewCallback(new ImageCompress(ipname)); // 设置回调的类
				camera.startPreview(); // 开始预览
				camera.autoFocus(null); // 自动对焦
			} catch (Exception e) {
				e.printStackTrace();
			}
			isPreview = true;
		}
	}

}
