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

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.ichano.rvs.viewer.Media;
import com.ichano.rvs.viewer.Viewer;
import com.ichano.rvs.viewer.callback.RecvJpegListener;

public class VideoListImage implements RecvJpegListener{
	private File cacheDir;
	private LruCache<String, Bitmap> filesCache;
	private Map<Long, LoadImage> viewMap = new HashMap<Long, LoadImage>();
	private static VideoListImage instance;
	private final BitmapFactory.Options options = new BitmapFactory.Options();
	private Media media;
	Context context;
	static int AvsType;
	public static VideoListImage getInstance(Context context,int serverType)
	{
		AvsType = serverType;
		if (null == instance)
		{
			instance = new VideoListImage(context,serverType);
		}
		return instance;
	}

	public VideoListImage(Context context,int serverType)
	{
		this.context = context;
		this.media = Viewer.getViewer().getMedia();
		cacheDir = FileUtils.createFile(Constants.LOCAL_ICON_PATH);
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		
		int MAXMEMONRY = (int) (Runtime.getRuntime() .maxMemory() / 1024);
		if (filesCache == null){
			filesCache = new LruCache<String, Bitmap>( MAXMEMONRY / 8) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
                }

                @Override
                protected void entryRemoved(boolean evicted, String key,
                        Bitmap oldValue, Bitmap newValue) {
                	if(oldValue!=null&&!oldValue.isRecycled())
   				 	{
	   					 oldValue.recycle();
	   					 oldValue = null;
   				 	}
                }
            };
		}
	}
	
	public void requestJpeg(String url,ImageView imageview,String cid){
		if (url == null)
		{
			return;
		}
		if (imageview.getTag() != null && imageview.getTag().equals(url)){
			Bitmap bitMap = filesCache.get(url);
			if (null != bitMap)
			{
				imageview.setImageBitmap(bitMap);
				return;
			}else{
				if(null == getBitmapFromFile(imageview,url)){
					long reqId = media.requestJpegFile(Long.valueOf(cid),url,this);
					viewMap.put(reqId, new LoadImage(imageview, url));
					return;
				}
			}
		}
	}
	
	@Override
	public void onRecvJpeg(final long requestId,final byte[] data) {
		((Activity)context).runOnUiThread(new Runnable()
		{
			public void run()
			{
				LoadImage loadImage = viewMap.get(requestId);
				if (null != loadImage && data != null && data.length > 0)
				{
//					if(AvsType == Constants.SER_TYPE_ANDROID || AvsType == Constants.SER_TYPE_IPC_LINUX){
						options.inSampleSize = 3;
//					}
					Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length, options);
					if (bm != null)
					{
						loadImage.getImageView().setImageBitmap(bm);
						filesCache.put(loadImage.getUrl(), bm);
						setBitmapToFile(loadImage.getUrl(),bm);
					}
				}
				viewMap.remove(requestId);
			}
		});
	}
	
	FileOutputStream fos = null;
	private void setBitmapToFile(String url,Bitmap bitmap){
		if(FileUtils.hasSDCard()){
			File file = null;
			try {
				file = new File(cacheDir, String.valueOf(url.hashCode()));
				file.createNewFile();
				fos = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
				fos.flush();
				if(fos != null){
					fos.close();
				}
			} catch (Exception e) {
				
			}
		}
	}
	
	private Bitmap getBitmapFromFile(ImageView imageView,String url){
		if(FileUtils.hasSDCard()){
			if(url != null){
				try {
//					if(AvsType == Constants.SER_TYPE_ANDROID || AvsType == Constants.SER_TYPE_IPC_LINUX){
						options.inSampleSize = 3;
//					}
					Bitmap fileBitmap = BitmapFactory.decodeFile(cacheDir+"/"+url.hashCode(), options);
					if(null != fileBitmap){
						imageView.setImageBitmap(fileBitmap);
						filesCache.put(url, fileBitmap);
						return fileBitmap;
					}
				} catch (Exception e) {
					
				} catch(OutOfMemoryError e){
					
				}
			}
		}
		return null;
	}

	public void cleraHash(){
		filesCache.evictAll();
	    viewMap.clear();
	}
	
	
	class LoadImage{
		private ImageView imageView;
		private String url;
		public LoadImage(ImageView imageView,String url){
			this.imageView = imageView;
			this.url = url;
		}
		public ImageView getImageView() {
			return imageView;
		}
		public void setImageView(ImageView imageView) {
			this.imageView = imageView;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
	}
}
