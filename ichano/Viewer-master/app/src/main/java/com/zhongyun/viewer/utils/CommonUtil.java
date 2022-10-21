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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ichano.rvs.viewer.constant.StreamerPresenceState;

public class CommonUtil {

	private static CharsetEncoder utf8encorder = Charset.forName("UTF-8").newEncoder();
	
	public static String htmlspecialchars(String str) {
		str = str.replaceAll("&", "&amp;");
		str = str.replaceAll("<", "&lt;");
		str = str.replaceAll(">", "&gt;");
		str = str.replaceAll("\"", "&quot;");
		return str;
	}

	/**
	 * 找最后一个空格（包括全角、半角空格）前的文字
	 * 
	 * @param nodeValue
	 * @return
	 */
	public static String getStringAfterLastBlank(String nodeValue) {
		String tmp = nodeValue.trim();
		int index = tmp.lastIndexOf(' ');
		if (index < 0) {
			index = tmp.lastIndexOf('　');
		}
		if (index > 0) {
			return tmp.substring(index + 1).trim();
		}
		return tmp.trim();
	}
	
	public static long parseLong(String num) {
		Long lon = -1l;
		try {
			lon = Long.parseLong(num);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return lon;
	}

	public static int parseInt(String num) {
		Integer intNum = 0;
		try {
			intNum = Integer.parseInt(num);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return intNum;
	}

	/**
	 * 是否都是数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}
	
	/**
	 * 是否都是字母
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isWord(String str) {
		try{
			Pattern pattern = Pattern.compile("[A-Za-z]*");
			return pattern.matcher(str).matches();
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 
	 */
	public static boolean isLegal(String str) {
		try{
			Pattern pattern = Pattern.compile("[\\s@%#|?/&_+=\\-\'~$:;<>,.{}()*!\"\\^\\]\\[a-zA-Z0-9_\u4e00-\u9fa5]*");
			return pattern.matcher(str).matches();
		}catch(Exception e){
			return false;
		}
	}

	/**
	 * 字符在str中出现的次数，连续的不行
	 * 
	 * @param str
	 * @param con
	 * @returni
	 */
	public static int numberOfStr(String str, String con) {
		str = " " + str;
		if (str.endsWith(con)) {
			return str.split(con).length;
		} else {
			return str.split(con).length - 1;
		}
	}

	/**
	 * 去掉特殊无效字符
	 * 
	 * @param source
	 * @return
	 */
	public static String filterSpecialChars(String source) {
		if (CommonUtil.isEmpty(source))
			return "";

		StringBuilder sb = new StringBuilder();
		int total = source.length();
		for (int i = 0; i < total; i++) {
			char c = source.charAt(i);
			if (c < 128 || (c > '\u4E00' && c < '\u9FA5')) {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	/**
	 * 是否是移动号码
	 * 
	 * @param mobile
	 * @return
	 */
	public static boolean isChinaMobile(String mobile) {
		if (mobile.startsWith("1349")) {
			return false;
		}
		return (mobile.startsWith("134") || mobile.startsWith("135") || mobile.startsWith("136") || mobile.startsWith("137")
				|| mobile.startsWith("138") || mobile.startsWith("139") || mobile.startsWith("150") || mobile.startsWith("151")
				|| mobile.startsWith("157") || mobile.startsWith("158") || mobile.startsWith("159") || mobile.startsWith("188"));
	}

	/**
	 * 是否是联通号码
	 * 
	 * @param mobile
	 * @return
	 */
	public static boolean isChinaUnicom(String mobile) {
		return (mobile.startsWith("130") || mobile.startsWith("131") || mobile.startsWith("132") || mobile.startsWith("1349")
				|| mobile.startsWith("155") || mobile.startsWith("156") || mobile.startsWith("186"));
	}

	/**
	 * 是否是电信号码
	 * 
	 * @param mobile
	 * @return
	 */
	public static boolean isChinaTelecom(String mobile) {
		return (mobile.startsWith("133") || mobile.startsWith("153") || mobile.startsWith("189") || mobile.startsWith("187") || mobile.startsWith("180"));
	}

	/**
	 * 找第一个空格（包括全角、半角空格）前的文字
	 * 
	 * @param nodeValue
	 * @return
	 */
	public static String getStringBeforeFirstBlank(String nodeValue) {
		String tmp = nodeValue.trim();
		int index = tmp.indexOf(' ');
		if (index < 0) {
			index = tmp.indexOf('　');
		}
		if (index > 0) {
			return tmp.substring(0, index).trim();
		}
		return tmp.trim();
	}

	/**
	 * 提取原始字符串nodeValue中prefix以后的子串
	 * 
	 * @param nodeValue
	 * @param prefix
	 * @return
	 */
	public static String stripAfter(String nodeValue, String prefix) {
		if (isEmpty(nodeValue))
			return "";

		String tmp = nodeValue.trim();
		int begin = 0;
		if (!isEmpty(prefix)) {
			int index = tmp.indexOf(prefix);
			if (index >= 0) {
				begin = index + prefix.length();
				return tmp.substring(begin).trim();
			}
		}

		return null;
	}

	/**
	 * 提取原始字符串nodeValue中suffix以前的子串
	 * 
	 * @param nodeValue
	 * @param prefix
	 * @return
	 */
	public static String stripBefore(String nodeValue, String suffix) {
		if (isEmpty(nodeValue))
			return "";

		String tmp = nodeValue.trim();
		int end = tmp.length();
		if (!isEmpty(suffix)) {
			int index = tmp.indexOf(suffix);
			if (index >= 0) {
				end = index;
				return tmp.substring(0, end).trim();
			}
		}

		return null;
	}

	/**
	 * 在字符串nodeValue中找"prefix"以后，"suffix"以前的字符串<br/>
	 * 如果prefix为空，则找end以前的字符串；如果suffix为空，则找before以后的字符串
	 * 
	 * @param nodeValue
	 * @param prefix
	 * @param suffix
	 * @return
	 */
	public static String strip(String nodeValue, String prefix, String suffix) {
		if (isEmpty(nodeValue))
			return "";

		String tmp = nodeValue.trim();
		int begin = 0;
		int end = tmp.length();

		if (!isEmpty(prefix)) {
			int index = tmp.indexOf(prefix);
			if (index >= 0)
				begin = index + prefix.length();
			else
				return ""; // 不包含前缀
		}

		if (!isEmpty(suffix)) {
			int index = tmp.indexOf(suffix, begin);
			if (index > 0)
				end = index;
			else
				return ""; // 不包含后缀
		}

		return end > begin ? tmp.substring(begin, end).trim() : tmp;
	}

	/**
	 * 查找字符串中的手机号码或电话号码.
	 * 匹配座机或手机号码的正则表达式是：((\d{3,4}[-_－—]?)?\d{7,8}([-_－—]?\d{1,7})?)|(0?1\d{10})
	 * 
	 * @param source
	 * @return
	 */
	public static String searchMobileOrTel(String source) {
		return searchAndReturn(source, "((\\d{3,4}[-_－—]?)?\\d{7,8}([-_－—]?\\d{1,7})?)|(0?1\\d{10})");
	}

	public static String searchAndReturn(String source, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(source);
		boolean b = matcher.find();
		if (b) {
			return source.substring(matcher.start(), matcher.end());
		}
		return null;
	}

	public static String regexText(String source, String regex, int group) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(source);
		boolean b = matcher.find();
		if (b) {
			return matcher.group(group);
		}
		return null;
	}

	
	public static boolean isMobile(String mobile) {
		Pattern pattern = Pattern.compile("1[3458]\\d{9}");
		Matcher matcher = pattern.matcher(mobile);
		boolean b = matcher.matches();
		if (b)
			return true;
		else
			return false;
	}

	public static boolean isMobileOrTel(String mobile) {
		if (mobile.startsWith("13") || mobile.startsWith("15") || mobile.startsWith("18"))
			return isMobile(mobile);
		Pattern pattern = Pattern.compile("(\\d{3,4}[-_－—]?)?\\d{7,8}([-_－—]?\\d{1,7})?");
		Matcher matcher = pattern.matcher(mobile);
		boolean b = matcher.matches();
		if (b)
			return true;
		else
			return false;
	}

	public static boolean isMobileOrTelByPattern(String mobile) {
		Pattern pattern = Pattern.compile("1[358]\\d{9}|(\\d{3,4}[-_－—]?)?\\d{7,8}([-_－—]?\\d{1,7})?");
		Matcher matcher = pattern.matcher(mobile);
		boolean b = matcher.matches();
		if (b)
			return true;
		else
			return false;
	}

	private static String encodeUrlParam(String value, String charset) throws UnsupportedEncodingException {
		if (value == null) {
			return "";
		}

		try {
			String decoded = URLDecoder.decode(value, charset);

			String result = "";
			for (int i = 0; i < decoded.length(); i++) {
				char ch = decoded.charAt(i);
				result += (ch == '#') ? "#" : URLEncoder.encode(String.valueOf(ch), charset);
			}

			return result;
		} catch (IllegalArgumentException e) {
			return value;
		}
	}

	public static String encodeUrl(String url, String charset) throws UnsupportedEncodingException {
		if (url == null) {
			return "";
		}

		int index = url.indexOf("?");
		if (index >= 0) {

			String result = url.substring(0, index + 1);
			String paramsPart = url.substring(index + 1);
			StringTokenizer tokenizer = new StringTokenizer(paramsPart, "&");
			while (tokenizer.hasMoreTokens()) {
				String definition = tokenizer.nextToken();
				int eqIndex = definition.indexOf("=");
				if (eqIndex >= 0) {
					String paramName = definition.substring(0, eqIndex);
					String paramValue = definition.substring(eqIndex + 1);
					result += paramName + "=" + encodeUrlParam(paramValue, charset) + "&";
				} else {
					result += encodeUrlParam(definition, charset) + "&";
				}
			}

			if (result.endsWith("&")) {
				result = result.substring(0, result.length() - 1);
			}

			return result;

		}

		return url;
	}

	/**
	 * Checks if specified link is full URL.
	 * 
	 * @param link
	 * @return True, if full URl, false otherwise.
	 */
	public static boolean isFullUrl(String link) {
		if (link == null) {
			return false;
		}
		link = link.trim().toLowerCase();
		return link.startsWith("http://") || link.startsWith("https://") || link.startsWith("file://");
	}

	/**
	 * Calculates full URL for specified page URL and link which could be full,
	 * absolute or relative like there can be found in A or IMG tags.
	 */
	public static String fullUrl(String pageUrl, String link) {
		if (link == null)
			return "";
		if (isFullUrl(link)) {
			return link;
		} else if (link != null && link.startsWith("?")) {
			int qindex = pageUrl.indexOf('?');
			int len = pageUrl.length();
			if (qindex < 0) {
				return pageUrl + link;
			} else if (qindex == len - 1) {
				return pageUrl.substring(0, len - 1) + link;
			} else {
				return pageUrl + "&" + link.substring(1);
			}
		}

		boolean isLinkAbsolute = link.startsWith("/");

		if (!isFullUrl(pageUrl)) {
			pageUrl = "http://" + pageUrl;
		}

		int slashIndex = isLinkAbsolute ? pageUrl.indexOf("/", 8) : pageUrl.lastIndexOf("/");
		if (slashIndex <= 8) {
			pageUrl += "/";
		} else {
			pageUrl = pageUrl.substring(0, slashIndex + 1);
		}

		return isLinkAbsolute ? pageUrl + link.substring(1) : pageUrl + link;
	}

	public static String convertHtmlCode(String line) {
		StringBuffer sb = new StringBuffer();
		int start = 0;
		boolean flag = false;
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == '&' && (i + 1) < line.length() && line.charAt(i + 1) == '#') {
				flag = true;
				start = i;
			}
			if (flag) {
				if (line.charAt(i) == ';') {
					String tmp = line.substring(start + 2, i);
					try {
						int code = Integer.parseInt(tmp);
						sb.append((char) code);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					flag = false;
				}
			} else {
				sb.append(line.charAt(i));
			}
		}
		return sb.toString();
	}

	/**
	 * Checks if a string is empty (ie is null or empty).
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.equals("") || str.equals("　") || str.equals(" ") || str.equals("null");
	}

	public static boolean notEmpty(String str) {
		return (str != null && !str.equals("") && !str.equals("null") && !str.equals("　") && !str.equals(" "));
	}

	public static String hexToString(String s) {
		int i = 0;
		StringBuffer buf = new StringBuffer(1000);

		if (s == null)
			return ""; // if null we return null (to avoid any errors)

		while (i <= s.length() - 6) {
			char c = s.charAt(i);
			char next = s.charAt(i + 1);
			if (c == '\\' && (next == 'u' || next == 'U')) {
				String tmp = s.substring(i + 2, i + 6);
				try {
					int code = Integer.parseInt(tmp, 16);
					char unicode = (char) code;
					buf.append(unicode);
					i += 6;
				} catch (NumberFormatException e) {
					e.printStackTrace();
					buf.append(c);
					i++;
				}
			} else {
				buf.append(c);
				i++;
			}

		}

		return buf.toString();
	}

	/**
	 * 将万以下的中文数字转换为罗马数字
	 * 
	 * @param s
	 * @return
	 */
	private static int toInt(String s) {
		int sum = 0;
		for (int i = 0; i < s.length(); i++) {
			int result = toInt(s.charAt(i));
			if (result >= 0 && result <= 10) {
				if (i + 1 < s.length()) {
					char next = s.charAt(i + 1);
					if (next == '千' || next == '仟')
						sum += result * 1000;
					else if (next == '百' || next == '佰')
						sum += result * 100;
					else if (next == '十' || next == '拾') {
						if (s.charAt(i) == '零')
							sum += 10;
						else
							sum += result * 10;
					} else {
						if (i - 1 > 0) {
							char prev = s.charAt(i - 1);
							if (prev == '千' || prev == '仟')
								sum += result * 100;
							else if (prev == '百' || prev == '佰')
								sum += result * 10;
							else if (prev == '十' || prev == '拾')
								sum += result;
						} else {
							sum += result;
						}
					}

				} else if (i - 1 > 0) {
					char prev = s.charAt(i - 1);
					if (prev == '千' || prev == '仟')
						sum += result * 100;
					else if (prev == '百' || prev == '佰')
						sum += result * 10;
					else
						sum += result;

				} else
					sum += result;
			}
		}
		return sum;
	}

	static char[] NUMBERS = { '零', '一', '二', '三', '四', '五', '六', '七', '八', '九', '十' };
	static char[] NUMBERS_TW = { '零', '壹', '貳', '叁', '肆', '伍', '陸', '柒', '捌', '玖', '拾' };

	public static int toInt(char c) {
		if (c == '两')
			return 2;

		for (int i = 0; i < 11; i++) {
			if (c == NUMBERS[i] || c == NUMBERS_TW[i])
				return i;
		}
		if (c >= '0' && c <= '9')
			return c - '0';
		return -1;
	}

	/**
	 * 将一串数字变成阿拉伯数字。可以是中文（简体或繁体），也可以是字符串的阿拉伯数字。
	 * 
	 * @param s
	 * @return
	 * @throws NumberFormatException
	 */
	public static int str2Int(String s) throws NumberFormatException {
		if (s.matches(".*[亿|万|千|百|十].*")) {
			int yi = s.indexOf('亿');
			int sum = 0;
			if (yi > 0) {
				sum += toInt(s.substring(0, yi)) * 10 ^ 8;
				String rest = s.substring(yi + 1);
				int wan = rest.indexOf('万');
				if (wan < 0)
					wan = rest.indexOf('萬');
				if (wan > 0) {
					sum += toInt(rest.substring(0, wan)) * 10000 * 10000;
					rest = s.substring(wan + 1);
					sum += toInt(rest);
				} else
					sum += toInt(rest);
			} else {
				int wan = s.indexOf('万');
				if (wan < 0)
					wan = s.indexOf('萬');
				if (wan > 0) {
					sum += toInt(s.substring(0, wan)) * 10000;
					if (wan + 1 == s.length() - 1) {// 如二万五, 五是5000
						sum += toInt(s.charAt(wan + 1)) * 1000;

					} else {
						String rest = s.substring(wan + 1);
						sum += toInt(rest);

					}
				} else
					sum += toInt(s);
			}
			return sum;
		} else {
			if (s.charAt(0) > 256)
				return toInt(s);
			else
				return (int) Double.parseDouble(s);
		}
	}

	public static String getHost(String urlString) {
		URL url;
		try {
			url = new URL(urlString);
			return url.getHost();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static boolean isEmail(String strEmail) {
		Pattern pattern = Pattern.compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
		Matcher matcher = pattern.matcher(strEmail);
		return matcher.matches();
	}
	
	public static boolean isSpeciaCharacters(String str){
		Pattern pattern = Pattern.compile("[/\\:*?<>|\"\n\t]");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
	
	public static byte[] md5(String strObj){
		MessageDigest md;   
		   try {   
		    md = MessageDigest.getInstance("MD5");
		    md.update(strObj.getBytes());   
		    byte[] pwd = md.digest();
		    return pwd;   
		   } catch (Exception e) {   
		    e.printStackTrace();   
		   }   
		   return null;   
	}
	public static String md5_32(String plainText) {
        String re_md5 = new String();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
 
            int i;
 
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
 
            re_md5 = buf.toString();
 
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return re_md5;
    }
	//sha1加密
	public static String sha1(String strSrc) {
		MessageDigest md = null;
		String strDes = "";

		byte[] bt = strSrc.getBytes();
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(bt);
			byte[] encryptStr = md.digest();
			String tmp = null;
			for (int i = 0; i < encryptStr.length; i++) {
				tmp = (Integer.toHexString(encryptStr[i] & 0xFF));
				if (tmp.length() == 1) {
					strDes += "0";
				}
				strDes += tmp;
			}
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Invalid algorithm.");
			return null;
		}
		return strDes;
	}
	
	public static String getLanguageEnv() {  
	       Locale l = Locale.getDefault();
	       String language = l.getLanguage();
	       String country = l.getCountry().toLowerCase();
	       if ("zh".equalsIgnoreCase(language)) {  
	           if ("cn".equals(country)) {
	               language = "zh-CN";  
	           } else if ("tw".equals(country)) {  
	               language = "zh-TW";  
	           }  
	       }
	       return language;  
	} 
	
	public static int getLanguageValue() {
		int lang = 0;
		Locale l = Locale.getDefault();
		String language = l.getLanguage();
		String country = l.getCountry().toLowerCase();
		if ("zh".equalsIgnoreCase(language)) {
			if ("cn".equals(country)) {
				lang = 1;
			} else if ("tw".equals(country)) {
				lang = 3;
			}
		} else {
			lang = 2;
		}
		return lang;
	}
	public static boolean isUtf8Str(String str)
	{
		return utf8encorder.canEncode(str);
	}
	
//	public static void compareCid(List<AvsBean> avsList){
//		final int CID_STATUS_ONLINE = StreamerPresenceState.ONLINE.intValue();
//		final int CID_STATUS_PWDERROR = StreamerPresenceState.USRNAME_PWD_ERR.intValue();
//		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
//		Collections.sort(avsList, new Comparator<Object>() {
//			@Override
//			public int compare(Object arg0, Object arg1) {
//				try{
//					AvsBean avs0 = (AvsBean) arg0;
//					AvsBean avs1 = (AvsBean) arg1;
//					int status0 = avs0.getStatus();
//					String cid0 = avs0.getCid();
//					int status1 = avs1.getStatus();
//					String cid1 = avs1.getCid();
//					int	ptype = avs0.getCloud().getPtype();
//					int	ptype1 = avs1.getCloud().getPtype();
//					
//					if (CID_STATUS_ONLINE == status0 || CID_STATUS_PWDERROR == status0) {
//						if (CID_STATUS_ONLINE == status1 || CID_STATUS_PWDERROR == status1) {
//							if (ptype != Constants.CLOUDTYPE_NO_SERVICE) {
//								if (ptype1 != Constants.CLOUDTYPE_NO_SERVICE) {
//									if(CID_STATUS_ONLINE == status0){
//										if(CID_STATUS_ONLINE == status1){
//											return cid0.compareTo(cid1);
//										}else{
//											return -1;
//										}
//									}else{
//										if(CID_STATUS_ONLINE == status1){
//											return 1;
//										}else{
//											return cid0.compareTo(cid1);
//										}
//									}
//								} else {
//									return -1;
//								}
//							} else {
//								if (ptype1 != Constants.CLOUDTYPE_NO_SERVICE) {
//									return 1;
//								} else {
//									if(CID_STATUS_ONLINE == status0){
//										if(CID_STATUS_ONLINE == status1){
//											return cid0.compareTo(cid1);
//										}else{
//											return -1;
//										}
//									}else{
//										if (CID_STATUS_ONLINE == status1) {
//											return 1;
//										} else {
//											return cid0.compareTo(cid1);
//										}
//									}
//								}
//							}
//						} else {
//							return -1;
//						}
//					} else {
//						if (CID_STATUS_ONLINE == status1 || CID_STATUS_PWDERROR == status1) {
//							return 1;
//						} else {
//							return cid0.compareTo(cid1);
//						}
//					}
//				}catch(Exception e){
//					return -1;
//				}
//			}
//		});
//	}
}
