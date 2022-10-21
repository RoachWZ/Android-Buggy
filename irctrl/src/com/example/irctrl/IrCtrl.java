package com.example.irctrl;

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.hardware.ConsumerIrManager;
public class IrCtrl extends Activity {

	
		 
	/**
	 * Android红外线遥控官方Demo
	 * 
	 * @description：
	 * @author ldm
	 * @date 2016-4-28 下午5:06:28
	 */
	/**
	 * Android红外线遥控Demo
	 * 
	 * @description：
	 * @author roachwz
	 * @date 2019-10-20 00:43:28
	 */
	/*2019-03-03
	 * 有问题，此处安卓程序，和小车单片机程序的数据码不一样，但能对应执行
	 * 单片机 0x76 3 5 4
	 * 安卓     0x12 8 6 4
	 * 
	 * 可能和接收有关系，只有反置了之后才能接收正常
	 *
	 *2019-05-13
	 * 红外接收已解决，是单片机端红外接收程序的问题
	 * 单片机 0x12 8 4 6
	 * 安卓     0x12 8 4 6
	 * */

	//下面的数组是一种交替的载波序列模式，通过毫秒测量
	//引导码，地址码，地址码，数据码，数据反码
	//第三行数据码反置，比如0x12=0001 0010反置为 0100 1000

	//停止 0x10
	int[] patternS = { 9000, 4500, 
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 
/*0000 1000*/560, 560,	560, 560, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560, 
			560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690, 
			560, 42020, 9000, 2250, 560, 98190 };
	//前进 0x12
	int[] pattern1 = { 9000, 4500, 
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
/*0100 1000*/560, 560,	560, 1690, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560, 
			560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560,1690, 	560, 1690, 	560, 1690, 
			560, 42020, 9000, 2250, 560, 98190 };
	//后退 0x18
	int[] pattern2 = { 9000, 4500, 
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560, 
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 
/*0001 1000*/560, 560,	560, 560, 	560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560, 560, 
			560, 1690, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690, 
			560, 42020, 9000, 2250, 560, 98190 };
	//左转 0x14
	int[] pattern3 = { 9000, 4500, 
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 
/*0010 1000*/560, 560,	560, 560, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560, 
			560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690, 
			560, 42020, 9000, 2250, 560, 98190 };
	//右转 0x16
	int[] pattern4 = { 9000, 4500, 
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
/*0110 1000*/560, 560,	560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560, 	
			560, 1690, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 1690,	560, 1690, 	560, 1690, 
			560, 42020, 9000, 2250, 560, 98190 };
	//左自转 0x17
	int[] pattern5 = { 9000, 4500, 
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 
/*1110 1000*/560, 1690,	560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560, 
			560, 560, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690, 
			560, 42020, 9000, 2250, 560, 98190 };
	//右自转 0x19
	int[] pattern6 = { 9000, 4500, 
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
/*1001 1000*/560, 1690,	560, 560, 	560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560, 560, 
			560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560,1690, 	560, 1690, 	560, 1690, 
			560, 42020, 9000, 2250, 560, 98190 };
	//全速  0x01
		int[] speed = { 9000, 4500, 
				560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560, 
				560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 
	/*1000 0000*/560,1690,	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 
				560,  560, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 
				560, 42020, 9000, 2250, 560, 98190 };
		//速度1 0x00
		int[] speed1 = { 9000, 4500, 
				560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560, 
				560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 
	/*0000 0000*/560, 560,	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 
				560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 
				560, 42020, 9000, 2250, 560, 98190 };
		//速度2 0x02
		int[] speed2 = { 9000, 4500, 
				560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
				560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 
	/*0100 0000*/560, 560,	560, 1690, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	
				560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 
				560, 42020, 9000, 2250, 560, 98190 };
		//速度3 0x03
		int[] speed3 = { 9000, 4500, 
				560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
				560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
	/*1100 0000*/560, 1690,	560, 1690, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	
				560, 560, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 
				560, 42020, 9000, 2250, 560, 98190 };


	    private static final String TAG = "ConsumerIrTest";
	    
	    // Android4.4之后 红外遥控ConsumerIrManager，可以被小米4调用
	    private ConsumerIrManager mCIR;
	    private int hz = 38000;

	    @SuppressLint("InlinedApi")
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        // 获取系统的红外遥控服务
	        mCIR = (ConsumerIrManager) getSystemService(Context.CONSUMER_IR_SERVICE);
	        initViewsAndEvents();
	    }

	    private void initViewsAndEvents() {
	        findViewById(R.id.send_button1).setOnClickListener(mSend1ClickListener);
	        findViewById(R.id.send_button2).setOnClickListener(mSend2ClickListener);
	        findViewById(R.id.send_button3).setOnClickListener(mSend3ClickListener);
	        findViewById(R.id.send_button4).setOnClickListener(mSend4ClickListener);
	        findViewById(R.id.buttonStop).setOnClickListener(mStopClickListener);
	        findViewById(R.id.buttonSpeed2).setOnClickListener(mSpeed2ClickListener);
	        findViewById(R.id.buttonSpeed3).setOnClickListener(mSpeed3ClickListener);
	        findViewById(R.id.buttonSpeed4).setOnClickListener(mFullspeedClickListener);
	        
	    }

	    View.OnClickListener mStopClickListener = new View.OnClickListener() {
	    	@TargetApi(Build.VERSION_CODES.KITKAT)
	    	public void onClick(View v) {
	    		if (!mCIR.hasIrEmitter()) {
	    			Log.e(TAG, "未找到红外发身器！");
	    			return;
	    		}
	    		
	    		
	    		
	    		// 在38KHz条件下进行模式转换
	    		mCIR.transmit(hz, patternS);
	    	}
	    };
	    View.OnClickListener mSend1ClickListener = new View.OnClickListener() {
	        @TargetApi(Build.VERSION_CODES.KITKAT)
	        public void onClick(View v) {
	            if (!mCIR.hasIrEmitter()) {
	                Log.e(TAG, "未找到红外发身器！");
	                return;
	            }

	            // 一种交替的载波序列模式，通过毫秒测量
	          int[] pattern = { 9000, 4500,
	            560, 560, 560, 560, 560, 560, 560, 560,		 560, 560, 560, 560, 560, 560, 560, 560, 
	            560,1690, 560,1690, 560,1690, 560,1690,		 560,1690, 560,1690, 560,1690, 560,1690,
	            560, 560, 560,1690, 560, 560, 560, 560,		 560,1690, 560, 560, 560, 560, 560, 560, 
	            560,1690, 560, 560, 560,1690, 560,1690,		 560, 560, 560,1690, 560,1690, 560,1690, 
	            560,42020, 9000,2250,560,98190 };
	            
	            // 在38KHz条件下进行模式转换
	            mCIR.transmit(hz, pattern1);
	        }
	    };
	    View.OnClickListener mSend2ClickListener = new View.OnClickListener() {
	        @TargetApi(Build.VERSION_CODES.KITKAT)
	        public void onClick(View v) {
	            if (!mCIR.hasIrEmitter()) {
	                Log.e(TAG, "未找到红外发身器！");
	                return;
	            }

	            int[] pattern = { 9000, 4500,
	    	            560, 560, 560, 560, 560, 560, 560, 560,		 560, 560, 560, 560, 560, 560, 560, 560, 
	    	            560,1690, 560,1690, 560,1690, 560,1690,		 560,1690, 560,1690, 560,1690, 560,1690,
	    	            560, 560, 560, 560, 560, 560, 560,1690,		 560,1690, 560, 560, 560, 560, 560, 560, 
	    	            560,1690, 560,1690, 560,1690, 560, 560,		 560, 560, 560,1690, 560,1690, 560,1690, 
	    	            560,42020, 9000,2250,560,98190 };
	    	            
	            mCIR.transmit(hz, pattern2);
	        }
	    };

	    View.OnClickListener mSend3ClickListener = new View.OnClickListener() {
	        @TargetApi(Build.VERSION_CODES.KITKAT)
	        public void onClick(View v) {
	            if (!mCIR.hasIrEmitter()) {
	                Log.e(TAG, "未找到红外发身器！");
	                return;
	            }

	            // 一种交替的载波序列模式，通过毫秒测量
	            int[] pattern = { 9000, 4500,
	    	            560, 560, 560, 560, 560, 560, 560, 560,		 560, 560, 560, 560, 560, 560, 560, 560, 
	    	            560,1690, 560,1690, 560,1690, 560,1690,		 560,1690, 560,1690, 560,1690, 560,1690,
	    	            560, 560, 560,1690, 560,1690, 560, 560,		 560,1690, 560, 560, 560, 560, 560, 560, 
	    	            560,1690, 560, 560, 560, 560, 560,1690,		 560, 560, 560,1690, 560,1690, 560,1690, 
	    	            560,42020, 9000,2250,560,98190 };
	          
	            // 在38KHz条件下进行模式转换
	            mCIR.transmit(hz, pattern3);
	        }
	    };
	    View.OnClickListener mSend4ClickListener = new View.OnClickListener() {
	        @TargetApi(Build.VERSION_CODES.KITKAT)
	        public void onClick(View v) {
	            if (!mCIR.hasIrEmitter()) {
	                Log.e(TAG, "未找到红外发身器！");
	                return;
	            }

	            int[] pattern = { 9000, 4500,
	    	            560, 560, 560, 560, 560, 560, 560, 560,		 560, 560, 560, 560, 560, 560, 560, 560, 
	    	            560,1690, 560,1690, 560,1690, 560,1690,		 560,1690, 560,1690, 560,1690, 560,1690,
	    	            560, 560, 560, 560, 560,1690, 560, 560,		 560,1690, 560, 560, 560, 560, 560, 560, 
	    	            560,1690, 560,1690, 560, 560, 560,1690,		 560, 560, 560,1690, 560,1690, 560,1690, 
	    	            560,42020, 9000,2250,560,98190 };
	    	            
	    	            
	            mCIR.transmit(hz, pattern4);
	        }
	    };
	    View.OnClickListener mSpeed2ClickListener = new View.OnClickListener() {
	        @TargetApi(Build.VERSION_CODES.KITKAT)
	        public void onClick(View v) {
	            if (!mCIR.hasIrEmitter()) {
	                Log.e(TAG, "未找到红外发身器！");
	                return;
	            }
	            mCIR.transmit(hz, speed2);
	        }
	    };
	    View.OnClickListener mSpeed3ClickListener = new View.OnClickListener() {
	    	@TargetApi(Build.VERSION_CODES.KITKAT)
	    	public void onClick(View v) {
	    		if (!mCIR.hasIrEmitter()) {
	    			Log.e(TAG, "未找到红外发身器！");
	    			return;
	    		}
	    		mCIR.transmit(hz, speed3);
	    	}
	    };
	    View.OnClickListener mFullspeedClickListener = new View.OnClickListener() {
	    	@TargetApi(Build.VERSION_CODES.KITKAT)
	    	public void onClick(View v) {
	    		if (!mCIR.hasIrEmitter()) {
	    			Log.e(TAG, "未找到红外发身器！");
	    			return;
	    		}
	    		mCIR.transmit(hz, speed);
	    	}
	    };
	    View.OnClickListener mSend13ClickListener = new View.OnClickListener() {
	    	@TargetApi(Build.VERSION_CODES.KITKAT)
	    	public void onClick(View v) {
	    		if (!mCIR.hasIrEmitter()) {
	    			Log.e(TAG, "未找到红外发身器！");
	    			return;
	    		}
	    		mCIR.transmit(hz, pattern5);
	    	}
	    };
	    View.OnClickListener mSend14ClickListener = new View.OnClickListener() {
	    	@TargetApi(Build.VERSION_CODES.KITKAT)
	    	public void onClick(View v) {
	    		if (!mCIR.hasIrEmitter()) {
	    			Log.e(TAG, "未找到红外发身器！");
	    			return;
	    		}
	    		mCIR.transmit(hz, pattern6);
	    	}
	    };
	}
