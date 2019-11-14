package com.cj.common.util.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.memory.NoOpMemoryTrimmableRegistry;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;


/**
 * Author:chris - jason
 * Date:2019/1/29.
 * Package:com.cj.common.util.image
 */

public class ImageLoader implements IImageLoader {

    private ImageLoader() {

    }

    private static class Holder {
        private static final ImageLoader instance = new ImageLoader();
    }

    public static ImageLoader getInstance() {
        return Holder.instance;
    }

    @Override
    public void load(Context context, @NonNull SimpleDraweeView view, int width, int height, String url) {
        Uri uri = Uri.parse(url);
        doLoad(view, uri, width, height, null);
    }

    @Override
    public void load(Context context, @NonNull SimpleDraweeView view, int width, int height, String url, IImageLoadCallback loadCallback) {
        Uri uri = Uri.parse(url);
        doLoad(view, uri, width, height, loadCallback);
    }

    @Override
    public void loadDrawable(Context context, @NonNull SimpleDraweeView view, int width, int height, @DrawableRes int drawable) {
        String pkg = context.getPackageName();
        Uri uri = Uri.parse("res://" + pkg + "/" + drawable);
        doLoad(view, uri, width, height, null);
    }

    @Override
    public void loadDrawable(Context context, @NonNull SimpleDraweeView view, int width, int height, @DrawableRes int drawable, IImageLoadCallback loadCallback) {
        String pkg = context.getPackageName();
        Uri uri = Uri.parse("res://" + pkg + "/" + drawable);
        doLoad(view, uri, width, height, loadCallback);
    }

    @Override
    public void loadFileImage(Context context, @NonNull SimpleDraweeView view, int width, int height, @NonNull File file) {
        Uri uri = Uri.fromFile(file);
        doLoad(view, uri, width, height, null);
    }

    @Override
    public void loadFileImage(Context context, @NonNull SimpleDraweeView view, int width, int height, @NonNull File file, IImageLoadCallback loadCallback) {
        Uri uri = Uri.fromFile(file);
        doLoad(view, uri, width, height, loadCallback);
    }

    @Override
    public void removeOneCache(Uri uri) {
        /**
         *   远程图片	            http://, https://
         *   本地文件	            file://	    FileInputStream
         *   Content provider	    content://	ContentResolver
         *   asset目录下的资源	    asset://	AssetManager
         *   res目录下的资源	        res://	    Resources.openRawResource
         */
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        if (imagePipeline != null) {
            try {
                //imagePipeline.evictFromMemoryCache(uri);//移除内存缓存
                //imagePipeline.evictFromDiskCache(uri);//移除磁盘缓存

                imagePipeline.evictFromCache(uri);//内存磁盘都移除

            } catch (Exception e) {

            }

        }


    }

    @Override
    public void removeAllCache() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        if (imagePipeline != null) {
            try {
                //imagePipeline.clearMemoryCaches();//移除所有内存缓存
                //imagePipeline.clearDiskCaches();//移除所有磁盘缓存
                imagePipeline.clearCaches();//移除所有内存+磁盘缓存
            } catch (Exception e) {

            }
        }

    }

    //在application中初始化fresco
    public static void init(@NonNull Context context) {
        if (!Fresco.hasBeenInitialized()) {
            //自定义ImagePipelineConfig
            //https://blog.csdn.net/chwnpp2/article/details/51063492
            ImagePipelineConfig.Builder imagePipelineConfigBuilder = ImagePipelineConfig.newBuilder(context);

            /**
             * 向下采样
             * 必须和ImageRequest的ResizeOptions一起使用，作用就是在图片解码时根据ResizeOptions所设的宽高的像素进行解码，
             * 这样解码出来可以得到一个更小的Bitmap。ResizeOptions和DownsampleEnabled参数都不影响原图片的大小，
             * 影响的是EncodeImage的大小，进而影响Decode出来的Bitmap的大小，ResizeOptions须和此参数结合使用是因为单独使用ResizeOptions的话只支持JPEG图，
             * 所以需支持png、jpg、webp需要先设置此参数。
             */
            imagePipelineConfigBuilder.setDownsampleEnabled(true);
            imagePipelineConfigBuilder.setResizeAndRotateEnabledForNetwork(true);

            //设置图片格式
            imagePipelineConfigBuilder.setBitmapsConfig(Bitmap.Config.RGB_565);

            //已解码内存缓存策略
            imagePipelineConfigBuilder.setBitmapMemoryCacheParamsSupplier(new Supplier<MemoryCacheParams>() {
                public MemoryCacheParams get() {
                    int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
                    int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 5;//取手机内存最大值的五分之一作为可用的最大内存数
                    // 可用最大内存数，以字节为单位
                    // 内存中允许的最多图片数量
                    // 内存中准备清理但是尚未删除的总图片所可用的最大内存数，以字节为单位
                    // 内存中准备清除的图片最大数量
                    // 内存中单图片的最大大小
                    MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(MAX_MEMORY_CACHE_SIZE, Integer.MAX_VALUE, MAX_MEMORY_CACHE_SIZE, Integer.MAX_VALUE, Integer.MAX_VALUE);
                    return bitmapCacheParams;
                }
            });

            //未解码内存缓存策略
            imagePipelineConfigBuilder.setEncodedMemoryCacheParamsSupplier(new Supplier<MemoryCacheParams>() {
                public MemoryCacheParams get() {
                    int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
                    int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 5;//取手机内存最大值的五分之一作为可用的最大内存数
                    MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(MAX_MEMORY_CACHE_SIZE, Integer.MAX_VALUE, MAX_MEMORY_CACHE_SIZE, Integer.MAX_VALUE, Integer.MAX_VALUE);
                    //设置大小，可参考上面已解码的内存缓存
                    return bitmapCacheParams;
                }
            });

            //内存紧张时策略
            MemoryTrimmableRegistry memoryTrimmableRegistry = NoOpMemoryTrimmableRegistry.getInstance();
            memoryTrimmableRegistry.registerMemoryTrimmable(new MemoryTrimmable() {
                @Override
                public void trim(MemoryTrimType trimType) {
                    final double suggestedTrimRatio = trimType.getSuggestedTrimRatio();
                    if (MemoryTrimType.OnCloseToDalvikHeapLimit.getSuggestedTrimRatio() == suggestedTrimRatio
                            || MemoryTrimType.OnSystemLowMemoryWhileAppInBackground.getSuggestedTrimRatio() == suggestedTrimRatio
                            || MemoryTrimType.OnSystemLowMemoryWhileAppInForeground.getSuggestedTrimRatio() == suggestedTrimRatio) {
                        //清空内存缓存
                        ImagePipelineFactory.getInstance().getImagePipeline().clearMemoryCaches();
                    }
                }
            });

            imagePipelineConfigBuilder.setMemoryTrimmableRegistry(memoryTrimmableRegistry);


            Fresco.initialize(context, imagePipelineConfigBuilder.build());
        }
    }

    /**
     * 实际调用图片加载方法
     *
     * @param view
     * @param uri          图片请求资源路径
     * @param loadCallback 加载结果回调接口，可为空
     */
    private void doLoad(@NonNull SimpleDraweeView view, @NonNull Uri uri, int width, int height, @Nullable final IImageLoadCallback loadCallback) {

        //加载监听器 ControllerListener
        ControllerListener<ImageInfo> controllerListener = new BaseControllerListener<ImageInfo>() {
            //加载成功
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                if (loadCallback != null) {
                    loadCallback.onSuccess(id, imageInfo, animatable);
                }
            }

            //加载失败
            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
                if (loadCallback != null) {
                    loadCallback.onFailed(id, throwable);
                }
            }
        };

        //自定义图片请求
        ImageRequest imageRequest = getImageRequest(width, height, uri);

        //DraweeController
        DraweeController draweeController = Fresco.newDraweeControllerBuilder().
                setControllerListener(controllerListener).
                setImageRequest(imageRequest).
                setAutoPlayAnimations(true).//自动播放gif动图
                setTapToRetryEnabled(false).//禁止点击图片重试加载
                setOldController(view.getController()).//获取SimpleDraweeView的监听器，未改变的属性可以不用重新设置，避免额外的内存开销
                build();

        view.setController(draweeController);

    }

    //裁剪图片尺寸
    private ImageRequest getImageRequest(int width, int height, Uri uri) {

        //自定义图片请求
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);

        //调整解码图片的大小
        if (width > 0 && height > 0) {
            builder.setResizeOptions(new ResizeOptions(width, height));
        }

        builder.setProgressiveRenderingEnabled(true).//支持JPEG网络图渐进式加载
                setLocalThumbnailPreviewsEnabled(true).//支持先加载JPEG本地图片的EXIF缩略图
                setLocalThumbnailPreviewsEnabled(true);//优先展示本地缩略图

        return builder.build();
    }

}
