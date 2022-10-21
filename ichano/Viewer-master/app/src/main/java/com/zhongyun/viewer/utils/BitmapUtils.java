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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

public class BitmapUtils {

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
}
