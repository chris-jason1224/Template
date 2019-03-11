package com.cj.common.provider.fun$compressor.compress;

import java.io.File;
import java.util.List;

/**
 * Author:chris - jason
 * Date:2019/3/11.
 * Package:com.cj.common.provider.fun$compressor.compress
 * 批量压缩结果回调接口
 */
public interface IBatchCompressCallback {

    void onSuccess(List<File> files);

    void onFailed(Throwable throwable);

}
