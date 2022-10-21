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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ichano.rvs.viewer.Viewer;
import com.ichano.rvs.viewer.callback.RecvJpegListener;
import com.ichano.rvs.viewer.constant.RvsJpegType;
import com.ichano.rvs.viewer.constant.RvsSessionState;
import com.ichano.rvs.viewer.constant.StreamerPresenceState;
import com.zhongyun.viewer.MyViewerHelper;
import com.zhongyun.viewer.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

public class ImageDownloader {
    private LruCache<String, Bitmap> mMemoryCache;
    private LruCache<String, Bitmap> mMemoryCache_sd;
    private final BitmapFactory.Options options = new BitmapFactory.Options();
    // List<String> list = new ArrayList<String>();
    long sendRequestId;
    Context context;
    private static ImageDownloader instance;
    ListView listvew;
    File cacheDir;

    public static ImageDownloader getInstance() {
        if (null == instance) {
            instance = new ImageDownloader();
        }
        return instance;
    }

    public ImageDownloader() {
        options.inJustDecodeBounds = false;
        cacheDir = FileUtils.createFile(Constants.LOCAL_CID_ICON_PATH);

        int MAXMEMONRY = (int) (Runtime.getRuntime().maxMemory() / 1024);
        if (null == mMemoryCache) {
            mMemoryCache = new LruCache<String, Bitmap>(MAXMEMONRY / 8) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
                }

                @Override
                protected void entryRemoved(boolean evicted, String key,
                        Bitmap oldValue, Bitmap newValue) {
                    if (oldValue != null && !oldValue.isRecycled()) {
                        oldValue.recycle();
                        oldValue = null;
                    }

                }
            };
        }

        if (null == mMemoryCache_sd) {
            mMemoryCache_sd = new LruCache<String, Bitmap>(MAXMEMONRY / 8) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
                }

                @Override
                protected void entryRemoved(boolean evicted, String key,
                        Bitmap oldValue, Bitmap newValue) {
                    if (oldValue != null && !oldValue.isRecycled()) {
                        oldValue.recycle();
                        oldValue = null;
                    }

                }
            };
        }
    }

    public Bitmap getBitmapFromCache(String load_cid) {
        Bitmap currBitmap = null;
        if (null != mMemoryCache) {
            currBitmap = mMemoryCache.get(load_cid);
            if (currBitmap != null)
                return currBitmap;
        }
        currBitmap = getBitmapFromFile(load_cid);
        return currBitmap;
    }

    public Bitmap getDefaultBmp(Context context){
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.avs_type_android);
    }
    public Bitmap putBitmapData(final String cid, final byte[] data) {
        Bitmap bitmap =null;
        if (null != data && data.length > 0) {
            options.inSampleSize = 1;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                    options);
            if (null != bitmap) {
                if (null != mMemoryCache) {
                    mMemoryCache.put(cid, bitmap);
                }
                setBitmapToFile(cid, bitmap);
                removeImageCache(cid);
            }
        }
        return bitmap;

    }

    public void destory() {
        try {
            clearCache();
            // list.clear();
            // instance = null;
        } catch (IllegalArgumentException e) {

        }
    }

    public void clearCache() {
        if (mMemoryCache != null) {
            if (mMemoryCache.size() > 0) {
                mMemoryCache.evictAll();
            }
            mMemoryCache = null;
        }

        if (mMemoryCache_sd != null) {
            if (mMemoryCache_sd.size() > 0) {
                mMemoryCache_sd.evictAll();
            }
            mMemoryCache_sd = null;
        }
    }

    public synchronized void removeImageCache(String key) {
        if (key != null) {
            if (mMemoryCache_sd != null) {
                Bitmap bm = mMemoryCache_sd.remove(key);
                if (bm != null)
                    bm.recycle();
            }
        }
    }

    /**
     * 从文件中拿图片
     * 
     * @param mActivity
     * @param imageName
     *            图片名字
     * @param path
     *            图片路径
     * @return
     */
    private Bitmap getBitmapFromFile(String cid) {
        if (FileUtils.hasSDCard()) {
            if (cid != null) {
                try {
                    Bitmap fileBitmap = mMemoryCache_sd.get(cid);
                    if (null != fileBitmap) {
                        return fileBitmap;
                    } else {
                        options.inSampleSize = 1;
                        Bitmap bitmap = BitmapFactory.decodeFile(cacheDir + "/"
                                + cid.hashCode(), options);
                        if (null != bitmap) {
                            mMemoryCache_sd.put(cid, bitmap);
                        }
                        return bitmap;
                    }
                } catch (Exception e) {

                    return null;
                } catch (OutOfMemoryError e) {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    FileOutputStream fos = null;

    private void setBitmapToFile(String cid, Bitmap bitmap) {
        if (FileUtils.hasSDCard()) {
            File file = null;
            try {
                file = new File(cacheDir, String.valueOf(cid.hashCode()));
                file.createNewFile();
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.flush();
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {

            }
        }
    }
}