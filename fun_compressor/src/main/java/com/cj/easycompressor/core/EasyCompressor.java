package com.cj.easycompressor.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.cj.easycompressor.callback.BatchCompressCallback;
import com.cj.easycompressor.callback.CompressCallback;
import com.cj.easycompressor.config.CompressOptions;
import com.cj.easycompressor.config.ImageOption;
import com.cj.easycompressor.util.CompressUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mayikang on 2018/9/12.
 */

public class EasyCompressor implements IEasyCompressor {
    public static String TAG = "EasyCompressor";
    private static Context mContext;
    //压缩参数
    private static volatile CompressOptions mOptions;

    private EasyCompressor() {
    }

    /**
     * EasyCompressor的初始化方法，只调用一次即可
     *
     * @param context
     */
    public static void init(@NonNull Context context) {
        //保证只需要初始化一次
        if (mContext == null) {
            mContext = context;
        }
    }

    /**
     * EasyCompressor调用入口
     *
     * @return
     */
    public static EasyCompressor getInstance(CompressOptions options) {
        if (mContext == null) {
            try {
                throw new Exception("---请先初始化EasyCompressor---");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //注入压缩参数
        mOptions = options;
        return Holder.instance;
    }

    private static class Holder {
        private static final EasyCompressor instance = new EasyCompressor();
    }

    public static Context getContext() {
        if (mContext == null) {
            try {
                throw new Exception("请先初始化EasyCompressor！！！");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mContext;
    }

    public static CompressOptions getOptions() {
        if (mOptions == null) {
            mOptions = new CompressOptions();
        }
        return mOptions;
    }

    @Override
    public void compress(@NonNull final String filePath, @NonNull final CompressCallback callback) {

        getCompressedFile(filePath).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<File>() {
            @Override
            public void accept(File file) throws Exception {
                if (callback != null) {
                    callback.onSuccess(file);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (callback != null) {
                    callback.onFailed(throwable);
                }
            }
        });

    }

    @Override
    public void batchCompress(@NonNull final List<String> filePaths, @NonNull final BatchCompressCallback callback) {
        final List<File> files = new ArrayList<>();

        Observable.fromIterable(filePaths).concatMap(new Function<String, ObservableSource<File>>() {
            @Override
            public ObservableSource<File> apply(String s) throws Exception {
                return getCompressedFile(s);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<File>() {
            @Override
            public void accept(File file) throws Exception {
                files.add(file);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if(callback!=null){
                    callback.onFailed(throwable);
                }
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                if(callback!=null){
                    callback.onSuccess(files);
                }
            }
        });

    }


    private Observable<File> getCompressedFile(final String filePath) {

        return Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> emitter) throws Exception {
                try {
                    String lowerPath = filePath.toLowerCase();
                    //图片后缀名
                    String suffix = ImageOption.IMG_PNG_SUFFIX;

                    if (lowerPath.endsWith(ImageOption.IMG_JPG_SUFFIX)) {
                        suffix = ImageOption.IMG_JPG_SUFFIX;
                    } else if (lowerPath.endsWith(ImageOption.IMG_JPEG_SUFFIX)) {
                        suffix = ImageOption.IMG_JPEG_SUFFIX;
                    }
                    File file = CompressUtil.create().invokeCompress(filePath, suffix);
                    emitter.onNext(file);
                } catch (Exception e) {
                    emitter.onError(e);
                }

                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }


}
