
/*
 * Copyright (C) 2015 iChano incorporation's Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhongyun.viewer.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.xmlpull.v1.XmlPullParser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.util.Xml;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

@SuppressLint("NewApi")
public class CommUtil {
	private static CommUtil instace;
	public static CommUtil getInstance(){
		if(null == instace){
			instace = new CommUtil();
		}
		return instace;
	}

	public static final String randomString(int length) {
		Random randGen = null;
		char[] numbersAndLetters = null;
		Object initLock = new Object();
		if (length < 1) {
			return null;
		}
		if (randGen == null) {
			synchronized (initLock) {
				if (randGen == null) {
					randGen = new Random();
					numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
							+ "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
				}
			}
		}
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
		}
		return new String(randBuffer);
	}
	public static void main(String[] args) {
	}
	
	/**
	 * 获取设备宽度
	 */
	public static int getPixelsWidth(Activity context){
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
	
	/**
	 * 获取设备高度
	 */
	public static int getPixelsHeight(Activity context){
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}
	/**
	* 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	*/
	public static int dip2px(Context context, float dpValue) {
	  final float scale = context.getResources().getDisplayMetrics().density;
	  return (int) (dpValue * scale + 0.5f);
	}

	/**
	* 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	*/
	public static int px2dip(Context context, float pxValue) {
	  final float scale = context.getResources().getDisplayMetrics().density;
	  return (int) (pxValue / scale + 0.5f);
	}
	/**
	* 根据手机的分辨率从 px(像素) 的单位 转成为 sp
	*/
	public static int px2sp(Context context, float pxValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (pxValue / fontScale + 0.5f);  
    }  
	/**
	* 判断是手机还是pad
	* @param context
	* @return
	*/
	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	//获取NavigationBar高度
	public static int getNavigationBarHeight(Context context) {  
        Resources resources = context.getResources();  
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");  
        //获取NavigationBar的高度  
        int height = resources.getDimensionPixelSize(resourceId);  
        return height;
    }  
	
	//获取是否有NavigationBar
	public static boolean checkDeviceHasNavigationBar(Context context) {   
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();  
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);  
  
        if (!hasMenuKey && !hasBackKey) {  
            return true;  
        }  
        return false;  
    }
	
	/**
	 * 获取系统版本号
	 */
	
	public static int getAndroidVersion(){
		return android.os.Build.VERSION.SDK_INT;
	}
	/**
	 * 获取当前分辨率下指定单位对应的像素大小（根据设备信息）
	 * px,dip,sp -> px
	 * 
	 * Paint.setTextSize()单位为px
	 * 
	 * 代码摘自：TextView.setTextSize()
	 * 
	 * @param unit  TypedValue.COMPLEX_UNIT_*
	 * @param size
	 * @return
	 */
	public static float getRawSize(Context context,int unit, float size) {
	       Resources r;

	       if (context == null)
	           r = Resources.getSystem();
	       else
	           r = context.getResources();
	        
	       return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
	}
	/**
	 * 刷新sd内容
	 */
	public static void refreshContent(Context context,String path){
		Uri data = Uri.parse("file://"+path);
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
	}
	
	/**
	 * 获取图片缩略图
	 */
	public static Bitmap getPictureImage(String urlPath) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeFile(urlPath, options);
		options.inJustDecodeBounds = false;
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / 100;
		int beHeight = h / 80;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		bitmap = BitmapFactory.decodeFile(urlPath, options);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, 100, 80,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}
	
	/**
	 * 获取视频缩略图
	 */
	public static Bitmap getVideoImage(String urlPath){
		Bitmap bitmap = null;
		bitmap = ThumbnailUtils.createVideoThumbnail(urlPath, MediaStore.Images.Thumbnails.MICRO_KIND);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, 100, 80,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}
	
	public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
	
//	public static String blowfishEcvrypt(String content){
//		try {
//		Blowfish cipher = new Blowfish();
//		cipher.setKey("dayton9780");
//		int lens = content.getBytes("utf-8").length;
//		int len8;
//		if ((lens % 8) != 0) {
//			len8 = ((lens + 7) / 8) * 8;
//			for (int ii = lens; ii < len8; ii++) {
//				content += " ";
//			}
//		} else {
//			len8 = lens;
//		}
//		byte[] enc_in = content.getBytes("utf-8");
//		byte[] enc_out = new byte[len8];
//		cipher.encrypt(enc_in, 0, enc_out, 0, len8);
//		return Base64.encodeToString(enc_out, Base64.DEFAULT);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return "";
//		}
//	}
//	public static String blowfishDecrypt(String content){
//		Blowfish cipher = new Blowfish();
//		cipher.setKey("dayton9780");
//		byte[] a = Base64.decode(content, Base64.DEFAULT);
//		byte[] dec_out = new byte[a.length];
//		cipher.decrypt(a, 0, dec_out, 0, a.length);
//		String response = new String(dec_out);
//		return response.trim();
//	}
	
	//版本判断
	public static boolean versionJudgment(int avstype,String versions){
		if(avstype == 0x17){
			return false;
		}else{
			if(CommonUtil.notEmpty(versions)){
				String [] version = versions.split("[.]");
				if(Integer.parseInt(version[0])<2){
					return true;
				}else{
					if(Integer.parseInt(version[0])==2 && Integer.parseInt(version[1])==0 && Integer.parseInt(version[2])<3){
						return true;
					}
				}
			}else{
				return false;
			}
		}
		return false;
	}
	
	public static boolean appVersionCheck(String avs_version) {
		String[] version = avs_version.split("[.]");
		if(version.length > 2){
			if(Integer.parseInt(version[0]) < 3 || (Integer.parseInt(version[0]) == 3 && Integer.parseInt(version[2]) < 1)){
				return false;
			}
		}
		return true;
	}
	
//	//安装更新文件
//	static String apkName;
//	public static void installApk(Context context){
//		File cacheDir = new File(Environment.getExternalStorageDirectory(), ConfigSetting.local_apk_url.concat("/").concat(apkName));
//		Intent tent = new Intent();
//		tent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		tent.setAction(android.content.Intent.ACTION_VIEW);
//		tent.setDataAndType(Uri.fromFile(cacheDir),"application/vnd.android.package-archive");
//		context.startActivity(tent);
//	}
	
//	//下载新版本
//	public static Long downLoadFile(Context context,String downLoadUrl){
//		apkName = context.getString(R.string.app_name).concat(".apk");
//		DownloadManager manager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
//		Uri uri = Uri.parse(downLoadUrl);
//		Request request = new Request(uri);
//		File cacheDir = new File(Environment.getExternalStorageDirectory(), ConfigSetting.local_apk_url);
//		if (!cacheDir.exists()) {
//			cacheDir.mkdirs();
//		}
//		File f = new File(cacheDir, apkName);
//		if(null != f){
//			f.delete();
//		}
//		request.setDestinationInExternalPublicDir(ConfigSetting.local_apk_url, apkName);
//		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);  
//		return manager.enqueue(request);
//	}
	//获取版本
	public static int checkVersion(Context context){
		String[] version = getVersionName(context).split("[.]");
		int sysVersion = (Integer.parseInt(version[0]) << 24) & 0xFF000000;
		int programMainVersion = (Integer.parseInt(version[1]) << 16) & 0x00FF0000;
		int programVersion = (Integer.parseInt(version[2]) << 8) & 0x0000FF00;
		int minVersion = 0;
		if(version.length>3){
			minVersion = (Integer.parseInt(version[3])) & 0x000000FF;
		}
		return sysVersion+programMainVersion+programVersion+minVersion;
	}
	
	public static String getVersionCode(int resultVersion){
		if((resultVersion & 0x000000FF) == 0){
			return ((resultVersion & 0xFF000000) >> 24) + "."+ ((resultVersion & 0x00FF0000) >> 16) + "."+ ((resultVersion & 0x0000FF00) >> 8);
		}else{
			return ((resultVersion & 0xFF000000) >> 24) + "."+ ((resultVersion & 0x00FF0000) >> 16) + "."+ ((resultVersion & 0x0000FF00) >> 8) +"."+(resultVersion & 0x000000FF);
		}
	}
	
	public static String getVersionName(Context context){
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			String version = packInfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
//	public static List<TimeZoneBean> getTimeZoneList(Context context){
//		List<TimeZoneBean> timeZonelist = new ArrayList<TimeZoneBean>(0);
//		TimeZoneBean timezone = null;
//		InputStream in;
//		try {
//			if ("zh-CN".equals(CommonUtil.getLanguageEnv())) {
//				in = context.getResources().getAssets().open("timezones_zh-CN.xml");
//			}else if("zh-TW".equals(CommonUtil.getLanguageEnv())) {
//				in = context.getResources().getAssets().open("timezones_zh-TW.xml");
//			}else {
//				in = context.getResources().getAssets().open("timezones_en.xml");
//			}
//			XmlPullParser parser = Xml.newPullParser();
//			parser.setInput(in, "utf-8");
//			int event = parser.getEventType();
//			while(event != XmlPullParser.END_DOCUMENT){
//				 switch (event) { 
//		            case XmlPullParser.START_DOCUMENT:   
//		                break;  
//		            case XmlPullParser.START_TAG:  
//						if (parser.getName().equals("timezone")) {
//							timezone = new TimeZoneBean();
//						} else if (parser.getName().equals("timezone_id")) {
//							event = parser.next();
//							timezone.setId(parser.getText());
//						}else if (parser.getName().equals("timezone_time")) {
//							event = parser.next();
//							timezone.setTime(parser.getText());
//						}else if (parser.getName().equals("timezone_rawoffset")) {
//							event = parser.next();
//							timezone.setRawoffset(parser.getText());
//						}else if (parser.getName().equals("timezone_name")) {
//							event = parser.next();
//							timezone.setName(parser.getText());
//						}
//						
//					break;
//		            case XmlPullParser.END_TAG:  
//		                if (parser.getName().equals("timezone")) {  
//		                	timeZonelist.add(timezone); 
//		                	timezone = null;      
//		                }  
//		                break;  
//				 }
//				 event = parser.next(); 
//			}
//			in.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return timeZonelist;
//	}
	
//	public static boolean isLogin(String sessionid,final Activity context){
//		if(CommonUtil.isEmpty(sessionid)){
//			String warning = context.getResources().getString(R.string.alert_title);
//			String prompt = context.getResources().getString(R.string.warnning_experience_select_function_alert);
//			final Builder builder = new AlertDialog.Builder(context);
//			builder.setTitle(warning);
//			builder.setMessage(prompt);
//			builder.setNeutralButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int which) {
//					builder.create().dismiss();
//				}
//			});
//			builder.setPositiveButton(R.string.warning_alert_ok_btn, new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int which) {
//					builder.create().dismiss();
//					context.startActivityForResult(new Intent(context, UserLoginToCidList.class),5000);
//				}
//			});
//			builder.show();
//			return false;
//		}
//		return true;
//	}
//	
//	//保存配置信息
//	public static void saveConfig(SharedPreferences configInfo) {
//		configInfo.edit().putInt("token_type", ConfigSetting.token_type)
//				.putInt("user_system", ConfigSetting.user_system)
//				.putString("appid", ConfigSetting.appId)
//				.putBoolean("isNeedShowAdvert", ConfigSetting.isNeedShowAdvert)
//				.putBoolean("isNeedShowShop", ConfigSetting.isNeedShowShop)
//				.putBoolean("isNeedShowVideo", ConfigSetting.isNeedShowVideo)
//				.putString("market_url", ConfigSetting.market_url)
//				.putString("local_video_url", ConfigSetting.local_video_url)
//				.putString("local_pic_url", ConfigSetting.local_pic_url)
//				.putString("local_icon_url", ConfigSetting.local_icon_url)
//				.putInt("viewVount", ConfigSetting.viewVount)
//				.putInt("app_type", ConfigSetting.app_type)
//				.putString("app_channel", ConfigSetting.app_channel)
//				.putString("local_apk_url", ConfigSetting.local_apk_url)
//				.putString("local_cidicon_url", ConfigSetting.local_cidicon_url)
//				.commit();
//	}
//	public static void getConfig(SharedPreferences configInfo){
//		ConfigSetting.token_type = configInfo.getInt("token_type", 0);
//		ConfigSetting.user_system = configInfo.getInt("user_system", 0);
//		ConfigSetting.isNeedShowAdvert = configInfo.getBoolean("isNeedShowAdvert", false);
//		ConfigSetting.isNeedShowShop = configInfo.getBoolean("isNeedShowShop", false);
//		ConfigSetting.isNeedShowVideo = configInfo.getBoolean("isNeedShowVideo", false);
//		ConfigSetting.market_url = configInfo.getString("market_url", "");
//		ConfigSetting.local_video_url = configInfo.getString("local_video_url", "");
//		ConfigSetting.local_pic_url = configInfo.getString("local_pic_url", "");
//		ConfigSetting.local_icon_url = configInfo.getString("local_icon_url", "");
//		ConfigSetting.appId = configInfo.getString("appid", "");
//		ConfigSetting.viewVount = configInfo.getInt("viewVount", 0);
//		ConfigSetting.app_type = configInfo.getInt("app_type", 0);
//		ConfigSetting.app_channel = configInfo.getString("configInfo", "");
//		ConfigSetting.local_apk_url = configInfo.getString("local_apk_url", "");
//		ConfigSetting.local_cidicon_url = configInfo.getString("local_cidicon_url", "");
//	}
	
	public static <T> List<T> deepCopy(List<T> src) {  
		try{
		    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();  
		    ObjectOutputStream out = new ObjectOutputStream(byteOut);  
		    out.writeObject(src);  
		  
		    ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());  
		    ObjectInputStream in = new ObjectInputStream(byteIn);  
		    @SuppressWarnings("unchecked")  
		    List<T> dest = (List<T>) in.readObject();  
		    return dest;  
		}catch(Exception e){
			return null;
		}
	}  
	
	 public static void hideOrShowNavigationBar(Activity context,boolean isHide) {
		 
		 if( android.os.Build.VERSION.SDK_INT > 14 ){
			 //WindowManager.LayoutParams attrs = context.getWindow().getAttributes();  
			 //attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN; // 全屏显示  
			 //attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN); // 取消全屏显示  
			 //context.getWindow().setAttributes(attrs);  
			 if(isHide){
				 int uiFlags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
				 if( android.os.Build.VERSION.SDK_INT >= 19 ){ 
					 uiFlags |= 0x00001000;
				 } else {
				     uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
				 }
				 context.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
			 }else{
				 context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
			 }
		 }
	 } 
}
