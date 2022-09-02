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
	    private static final String TAG = "ConsumerIrTest";
	    
	    // Android4.4之后 红外遥控ConsumerIrManager，可以被小米4调用
	    private ConsumerIrManager mCIR;

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
	        
	    }

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
	            mCIR.transmit(38000, pattern);
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
	    	            
	            mCIR.transmit(38000, pattern);
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
	            mCIR.transmit(38000, pattern);
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
	    	            
	    	            
	            mCIR.transmit(38000, pattern);
	        }
	    };
	    
	}
