package com.cj.easycompressor.callback;

import java.io.File;

/**
 * Created by mayikang on 2018/9/12.
 */

/**
 * 压缩结果回调接口
 */
public interface CompressCallback {

    void onSuccess(File compressedFile);

    void onFailed(Throwable throwable);

}
