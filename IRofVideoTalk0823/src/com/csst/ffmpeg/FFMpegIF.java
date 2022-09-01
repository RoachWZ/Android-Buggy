package com.csst.ffmpeg;

import android.graphics.Bitmap;

public class FFMpegIF {

//	static public native int PlayerInit();
//	static public native int PlayerPrepare(String filename);
//	static public native int PlayerMain();
//	static public native int PlayerExit();
//	static public native int PlayerSeekTo(int msec);
//	static public native int PlayerPause();
//	static public native int PlayerIsPlay();
//	static public native int PlayerGetDuration();
//	static public native int PlayergetCurrentPosition();
	
	static public native int    GetFFmpegVer();    
	static public native void   Init();
	static public native void   DeInit();
	static public native void   Release();
	static public native int    GetWidth();
	static public native int    GetHeight();
	static public native int    StartDecode(String url);
	static public native void   StopDecode();
	static public native int    Decoding(Bitmap bitmap);
	static public native int    DecodingArray(int[] out);
	
	static public native int    StartEncode(String url, int w, int h, int fps);
	static public native void   StopEncode();
	static public native int    Encoding(byte[] in, int insize,byte[] out);

	static {
		System.loadLibrary("ffmpegif");
	}

	
}
