package com.cj.easycompressor;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cj.common.provider.fun$compressor.compress.IBatchCompressCallback;
import com.cj.common.provider.fun$compressor.compress.ICompressCallback;
import com.cj.common.provider.fun$compressor.compress.ICompressProvider;
import com.cj.easycompressor.callback.BatchCompressCallback;
import com.cj.easycompressor.callback.CompressCallback;
import com.cj.easycompressor.core.EasyCompressor;

import java.io.File;
import java.util.List;

/**
 * Author:chris - jason
 * Date:2019/3/11.
 * Package:com.cj.easycompressor
 * 图片压缩服务
 */
@Route(path = "/fun_compressor/SEV/com.cj.easycompressor.CompressService")
public class CompressService implements ICompressProvider {

    @Override
    public void invokeCompress(String filePath, final ICompressCallback callback) {

        EasyCompressor.getInstance(null).compress(filePath, new CompressCallback() {
            @Override
            public void onSuccess(File compressedFile) {
                if (callback != null) {
                    callback.onSuccess(compressedFile);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (callback != null) {
                    callback.onFailed(throwable);
                }
            }
        });

    }

    @Override
    public void invokeBatchCompress(List<String> filePathList, final IBatchCompressCallback batchCallback) {

        EasyCompressor.getInstance(null).batchCompress(filePathList, new BatchCompressCallback() {
            @Override
            public void onSuccess(List<File> files) {
                if (batchCallback != null) {
                    batchCallback.onSuccess(files);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (batchCallback != null) {
                    batchCallback.onFailed(throwable);
                }
            }
        });

    }

    @Override
    public void init(Context context) {

    }
}
