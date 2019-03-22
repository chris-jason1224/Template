package com.cj.easycompressor.core;

import com.cj.easycompressor.callback.BatchCompressCallback;
import com.cj.easycompressor.callback.CompressCallback;

import java.util.List;

/**
 * Created by mayikang on 2018/9/12.
 */

/**
 * 压缩功能代理接口
 */
public interface IEasyCompressor {

    /******单个压缩******/
    void compress(String filePath,CompressCallback callback);

    /******批量压缩*****/
    void batchCompress(List<String> filePaths, BatchCompressCallback callback);

}
