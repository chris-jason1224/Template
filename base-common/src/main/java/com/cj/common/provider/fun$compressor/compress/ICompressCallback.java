package com.cj.common.provider.fun$compressor.compress;

import java.io.File;

/**
 * Author:chris - jason
 * Date:2019/3/11.
 * Package:com.cj.common.provider.fun$compressor.compress
 * 单张图片压缩结果回调
 */
public interface ICompressCallback {
    void onSuccess(File compressedFile);

    void onFailed(Throwable throwable);
}
