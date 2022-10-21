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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;

public class FileUtils {

	public static boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)){
			return true;
		}else{
			return false;
		}
	}
	
	public static File mkdirsOnSDCard(String pathName){
		File file = new File(Environment.getExternalStorageDirectory(), pathName);
		if (!file.exists()){
			file.mkdirs();
		}
		return file;
	}
	
	public static boolean copy(String input, String output) throws Exception {
        int BUFSIZE = 65536;
        try {
            FileInputStream fis = new FileInputStream(input);
            FileOutputStream fos = new FileOutputStream(output);

            int s;
            byte[] buf = new byte[BUFSIZE];
            while ((s = fis.read(buf)) > -1) {
                fos.write(buf, 0, s);
            }
        } catch (Exception ex) {
            throw new Exception("makehome" + ex.getMessage());
        }
        return true;
    }

    public static boolean checkSDCard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }
    
    /**
     * 刷新sd内容
     */
    public static void refreshContent(Context context,String path){
        Uri data = Uri.parse("file://"+path);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
    }
    /**
     * 
     * @param 获取sd卡容量
     * @return
     */
    public static double getSdCardContent(){
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
             File path = Environment.getExternalStorageDirectory();   
             StatFs sf = new StatFs(path.getPath());   
             long blockSize = sf.getBlockSize();   
             long freeBlocks = sf.getAvailableBlocks();  
             return (freeBlocks * blockSize)/1024 /1024;  
        } else {
            return 0;
        }
    }
    
    /**
     * 将输入流转换成字符串
     * 
     * @param inStream
     */
    public static String convertString(InputStream inStream) {
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = inStream.read(buf)) > 0) {
                bos.write(buf, 0, len);
            }
            return new String(bos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null)
                    bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    /**
     * 创建临时文件
     * 
     * @param srcPath
     * @param destPath
     * @return
     */
    public static boolean createTempFile(String srcPath, String destPath) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {

            FileInputStream in = new FileInputStream(srcPath);
            bis = new BufferedInputStream(in);
            bos = new BufferedOutputStream(new FileOutputStream(destPath));
            int len = 0;
            byte[] buf = new byte[4098];
            while ((len = bis.read(buf)) > 0) {
                bos.write(buf, 0, len);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static byte[] res2byte(Context context, int resId) {
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resId);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 60, bos);
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }
    
    
    public static File createFile(String url){
        File cacheDir = new File(Environment.getExternalStorageDirectory(), url);
        if (!cacheDir.exists())
        {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }
    
    public static void deleteFile(String name,String url){
        File cacheDir = new File(Environment.getExternalStorageDirectory(), url);
        File f = new File(cacheDir, name);
        if (f.exists()) {
            f.delete();
        }
    }
    
    public static void deletelistFiles(String url){
        File cacheDir = new File (Environment.getExternalStorageDirectory(), url);
        if(null != cacheDir){
            File[] file = cacheDir.listFiles();
            if(null != file){
                if(file.length>0){
                    for (int i = 0; i < file.length; i++) {
                         file[i].delete();
                    }
                }
            }
        }
    }
}
