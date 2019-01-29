package com.cj.common.util.image;

import android.graphics.drawable.Animatable;
import android.media.Image;
import android.support.annotation.Nullable;

import com.facebook.imagepipeline.image.ImageInfo;

/**
 * Author:chris - jason
 * Date:2019/1/29.
 * Package:com.cj.common.util.image
 */
//图片加载结果回调
public interface IImageLoadCallback {

    //加载成功回调
    void onSuccess(String id, ImageInfo imageInfo,Animatable animatable);

    //加载失败回调
    void onFailed(String id, Throwable throwable);
}
