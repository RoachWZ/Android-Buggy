package com.iflytek.speech.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.hardware.ConsumerIrManager;
/**
 * Json结果解析类
 */
public class JsonParser {

	//停止 0x10
	public static int[] patternS = { 9000, 4500,
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
/*0000 1000*/560, 560,	560, 560, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690,
			560, 42020, 9000, 2250, 560, 98190 };
	//前进 0x12
	public static int[] pattern1 = { 9000, 4500,
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
/*0100 1000*/560, 560,	560, 1690, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560,1690, 	560, 1690, 	560, 1690,
			560, 42020, 9000, 2250, 560, 98190 };
	//后退 0x18
	public static int[] pattern2 = { 9000, 4500,
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
/*0001 1000*/560, 560,	560, 560, 	560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690,
			560, 42020, 9000, 2250, 560, 98190 };
	//左转 0x14
	public static int[] pattern3 = { 9000, 4500,
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
/*0010 1000*/560, 560,	560, 560, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690,
			560, 42020, 9000, 2250, 560, 98190 };
	//右转 0x16
	public static int[] pattern4 = { 9000, 4500,
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
/*0110 1000*/560, 560,	560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 1690,	560, 1690, 	560, 1690,
			560, 42020, 9000, 2250, 560, 98190 };
	//左自转 0x17
	public static int[] pattern5 = { 9000, 4500,
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
/*1110 1000*/560, 1690,	560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
			560, 560, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690,
			560, 42020, 9000, 2250, 560, 98190 };
	//右自转 0x19
	public static int[] pattern6 = { 9000, 4500,
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
/*1001 1000*/560, 1690,	560, 560, 	560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
			560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560,1690, 	560, 1690, 	560, 1690,
			560, 42020, 9000, 2250, 560, 98190 };
	//全速  0x01
	public static int[] speed = { 9000, 4500,
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
			/*1000 0000*/560,1690,	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
			560,  560, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
			560, 42020, 9000, 2250, 560, 98190 };
	//速度1 0x00
	public static int[] speed1 = { 9000, 4500,
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
			/*0000 0000*/560, 560,	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
			560, 42020, 9000, 2250, 560, 98190 };
	//速度2 0x02
	public static int[] speed2 = { 9000, 4500,
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
			/*0100 0000*/560, 560,	560, 1690, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690,
			560, 42020, 9000, 2250, 560, 98190 };
	//速度3 0x03
	public static int[] speed3 = { 9000, 4500,
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
			/*1100 0000*/560, 1690,	560, 1690, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
			560, 560, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690,
			560, 42020, 9000, 2250, 560, 98190 };


	public static int hz = 38000;
	public static ConsumerIrManager mCIR;

	public static void sendIR(String cmd){
		String mstr = cmd;
		if (mstr.equals("05")) {
			mCIR.transmit(hz, patternS);//停
		} else if (mstr.equals("01")) {
			mCIR.transmit(hz, pattern1);//前
		} else if (mstr.equals("02")) {
			mCIR.transmit(hz, pattern2);//后
		} else if (mstr.equals("03")) {
			mCIR.transmit(hz, pattern3);//左
		} else if (mstr.equals("04")) {
			mCIR.transmit(hz, pattern4);//右
		} else if (mstr.equals("13")) {
			mCIR.transmit(hz, pattern5);//左自转
		} else if (mstr.equals("14")) {
			mCIR.transmit(hz, pattern6);//右自转
		} else if (mstr.equals("s")) {
			mCIR.transmit(hz, speed);//全速
		} else if (mstr.equals("s1")) {
			mCIR.transmit(hz, speed1);//速度1
		} else if (mstr.equals("s2")) {
			mCIR.transmit(hz, speed2);//速度2
		} else if (mstr.equals("s3")) {
			mCIR.transmit(hz, speed3);//速度3
		} else if (mstr.equals("L")) {
//			lightSwitch();				//开关闪关灯
		} else if (mstr.equals("C")) {
//			cameraSwitch();			//后置摄像头
		} else {
			System.out.println("数据错误");
		}
	}

	public static void judge(String word){
		if(word.equals("前进")){
			sendIR("01");
		}else if(word.equals("后退")){
			sendIR("02");
		}else if(word.equals("左转")){
			sendIR("03");
		}else if(word.equals("后转")){
			sendIR("04");
		}
	}

	public static String parseIatResult(String json) {
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			JSONArray words = joResult.getJSONArray("ws");
			for (int i = 0; i < words.length(); i++) {
				// 转写结果词，默认使用第一个结果
				JSONArray items = words.getJSONObject(i).getJSONArray("cw");
				JSONObject obj = items.getJSONObject(0);
				ret.append(obj.getString("w"));
//				如果需要多候选结果，解析数组其他字段
//				for(int j = 0; j < items.length(); j++)
//				{
//					JSONObject obj = items.getJSONObject(j);
//					ret.append(obj.getString("w"));
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return ret.toString();
	}
	
	public static String parseGrammarResult(String json, String engType) {
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			JSONArray words = joResult.getJSONArray("ws");
			// 云端和本地结果分情况解析
			if ("cloud".equals(engType)) {
				for (int i = 0; i < words.length(); i++) {
					JSONArray items = words.getJSONObject(i).getJSONArray("cw");
					for(int j = 0; j < items.length(); j++)
					{
						JSONObject obj = items.getJSONObject(j);
						if(obj.getString("w").contains("nomatch"))
						{
							ret.append("没有匹配结果.");
							return ret.toString();
						}
						ret.append("【结果】" + obj.getString("w"));
						ret.append("【置信度】" + obj.getInt("sc"));
						ret.append("\n");
					}
				}
			} else if ("local".equals(engType)) {
				ret.append("【结果】");
				for (int i = 0; i < words.length(); i++) {
					JSONObject wsItem = words.getJSONObject(i);
					JSONArray items = wsItem.getJSONArray("cw");
					if ("<contact>".equals(wsItem.getString("slot"))) {
						// 可能会有多个联系人供选择，用中括号括起来，这些候选项具有相同的置信度
						ret.append("【");
						for(int j = 0; j < items.length(); j++)
						{
							JSONObject obj = items.getJSONObject(j);
							if(obj.getString("w").contains("nomatch"))
							{
								ret.append("没有匹配结果.");
								return ret.toString();
							}
							ret.append(obj.getString("w")).append("|");						
						}
						ret.setCharAt(ret.length() - 1, '】');
					} else {
						//本地多候选按照置信度高低排序，一般选取第一个结果即可
						JSONObject obj = items.getJSONObject(0);
						if(obj.getString("w").contains("nomatch"))
						{
							ret.append("没有匹配结果.");
							return ret.toString();
						}
						ret.append(obj.getString("w"));						
					}
				}
				ret.append("【置信度】" + joResult.getInt("sc"));
				ret.append("\n");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			ret.append("没有匹配结果.");
		} 
		return ret.toString();
	}
	
	public static String parseGrammarResult(String json) {
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			JSONArray words = joResult.getJSONArray("ws");
			for (int i = 0; i < words.length(); i++) {
				JSONArray items = words.getJSONObject(i).getJSONArray("cw");
				for(int j = 0; j < items.length(); j++)
				{
					JSONObject obj = items.getJSONObject(j);
					if(obj.getString("w").contains("nomatch"))
					{
						ret.append("没有匹配结果.");
						return ret.toString();
					}
					ret.append("@"+obj.getString("w"));
//					ret.append("【结果】" + obj.getString("w"));
//					ret.append("【置信度】" + obj.getInt("sc"));

//					judge(obj.getString("w"));//

//					ret.append("\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ret.append("没有匹配结果.");
		} 
		return ret.toString();
	}

	public static String parseTransResult(String json,String key) {
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);
			String errorCode = joResult.optString("ret");
			if(!errorCode.equals("0")) {
				return joResult.optString("errmsg");
			}
			JSONObject transResult = joResult.optJSONObject("trans_result");
			ret.append(transResult.optString(key));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret.toString();
	}
}
