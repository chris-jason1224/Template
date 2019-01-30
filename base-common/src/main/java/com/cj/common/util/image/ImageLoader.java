package com.cj.common.util.image;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;



/**
 * Author:chris - jason
 * Date:2019/1/29.
 * Package:com.cj.common.util.image
 */

public class ImageLoader implements IImageLoader {

    private ImageLoader(){

    }

    @Override
    public void load(Context context,@NonNull SimpleDraweeView view, String url) {
        if(util.verify(url)){
            Uri uri = Uri.parse(url);
            if(uri!=null){
                doLoad(view,uri,null);
            }
        }
    }

    @Override
    public void load(Context context,@NonNull SimpleDraweeView view, String url, IImageLoadCallback loadCallback) {
        if(util.verify(url)){
            Uri uri = Uri.parse(url);
            if(uri!=null){
                doLoad(view,uri,loadCallback);
            }
        }
    }

    @Override
    public void loadDrawable(Context context,@NonNull SimpleDraweeView view,@DrawableRes int drawable) {
        String pkg=context.getPackageName();
        Uri uri=Uri.parse("res://"+pkg+"/"+drawable);
        if(uri!=null){
            doLoad(view,uri,null);
        }
    }

    @Override
    public void loadDrawable(Context context,@NonNull SimpleDraweeView view,@DrawableRes int drawable, IImageLoadCallback loadCallback) {
        String pkg=context.getPackageName();
        Uri uri=Uri.parse("res://"+pkg+"/"+drawable);
        if(uri!=null){
            doLoad(view,uri,loadCallback);
        }
    }

    @Override
    public void removeOneCache(Uri uri) {

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        if(imagePipeline!=null){
            try{
                //imagePipeline.evictFromMemoryCache(uri);//移除内存缓存
                //imagePipeline.evictFromDiskCache(uri);//移除磁盘缓存


                imagePipeline.evictFromCache(uri);//内存磁盘都移除

            }catch (Exception e){

            }

        }


    }

    @Override
    public void removeAllCache() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        if(imagePipeline!=null){
            try{
                //imagePipeline.clearMemoryCaches();//移除所有内存缓存
                //imagePipeline.clearDiskCaches();//移除所有磁盘缓存
                imagePipeline.clearCaches();//移除所有内存+磁盘缓存
            }catch (Exception e){

            }
        }

    }

    private static class Holder{
        private static final ImageLoader instance = new ImageLoader();
    }

    public static ImageLoader getInstance(){
        return Holder.instance;
    }

    //在application中初始化fresco
    public static void init(@NonNull Context context){
        if(!Fresco.hasBeenInitialized()){
            //自定义ImagePipelineConfig
            Fresco.initialize(context);
        }
    }


    /**
     * 实际调用图片加载方法
     * @param view
     * @param uri 图片请求资源路径
     * @param loadCallback 加载结果回调接口，可为空
     */
    private void doLoad(@NonNull SimpleDraweeView view,@NonNull Uri uri,@Nullable final IImageLoadCallback loadCallback){

        //加载监听器 ControllerListener
        ControllerListener<ImageInfo> controllerListener = new BaseControllerListener<ImageInfo>(){
            //加载成功
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                if(loadCallback!=null){
                    loadCallback.onSuccess(id,imageInfo,animatable);
                }
            }

            //加载失败
            @Override
            public void onFailure(String id, Throwable throwable) {
                if(loadCallback!=null){
                    loadCallback.onFailed(id,throwable);
                }
            }
        };

        //自定义图片请求
        ImageRequest imageRequest = ImageRequestBuilder.
                newBuilderWithSource(uri).//请求图片资源
                setProgressiveRenderingEnabled(true).//支持JPEG网络图渐进式加载
                setLocalThumbnailPreviewsEnabled(true).//支持先加载JPEG本地图片的EXIF缩略图
                build();

        //DraweeController
        DraweeController draweeController = Fresco.newDraweeControllerBuilder().
                setControllerListener(controllerListener).
                setImageRequest(imageRequest).
                setAutoPlayAnimations(true).//自动播放gif动图
                setTapToRetryEnabled(true).//点击图片重试加载
                setOldController(view.getController()).//获取SimpleDraweeView的监听器，未改变的属性可以不用重新设置，避免额外的内存开销
                build();

        view.setController(draweeController);

    }



}
