package com.cj.common.provider.fun$compressor.compress;

import com.alibaba.android.arouter.facade.template.IProvider;

import java.util.List;

/**
 * Author:chris - jason
 * Date:2019/3/11.
 * Package:com.cj.common.provider.fun$compressor.compress
 * fun-compressor对外提供压缩服务的约束接口
 */
public interface ICompressProvider extends IProvider {

    //单张图片压缩
    void invokeCompress(String filePath, ICompressCallback callback);

    //批量图片压缩
    void invokeBatchCompress(List<String> filePathList,IBatchCompressCallback batchCallback);

}
