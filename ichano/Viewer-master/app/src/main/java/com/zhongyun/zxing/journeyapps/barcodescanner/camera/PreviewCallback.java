package com.zhongyun.zxing.journeyapps.barcodescanner.camera;

import com.zhongyun.zxing.journeyapps.barcodescanner.SourceData;


/**
 * Callback for camera previews.
 */
public interface PreviewCallback {
    void onPreview(SourceData sourceData);
}
