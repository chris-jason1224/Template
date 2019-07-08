package com.cj.common.util.image;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;

/**
 * Author:chris - jason
 * Date:2019/1/29.
 * Package:com.cj.common.util.image
 */

public interface IImageLoader {

    //加载网络图
    void load(Context context,@NonNull SimpleDraweeView view, @NonNull String url);
    void load(Context context,@NonNull SimpleDraweeView view,@NonNull String url,IImageLoadCallback loadCallback);

    //加载本地资源图
    void loadDrawable(Context context,@NonNull SimpleDraweeView view,@DrawableRes int drawable);
    void loadDrawable(Context context,@NonNull SimpleDraweeView view,@DrawableRes int drawable,IImageLoadCallback loadCallback);

    //加载本地文件图
//    void loadFileImage(Context context,@NonNull SimpleDraweeView view,@NonNull File file);
//    void loadFileImage(Context context,@NonNull SimpleDraweeView view,@NonNull File file,IImageLoadCallback loadCallback);


    /*****删除缓存*******/
    void removeOneCache(Uri uri);

    void removeAllCache();

}
