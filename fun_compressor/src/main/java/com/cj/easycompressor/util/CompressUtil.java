package com.cj.easycompressor.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.cj.easycompressor.config.ImageOption;
import com.cj.easycompressor.core.EasyCompressor;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by mayikang on 2018/9/12.
 */

public class CompressUtil {

    private CompressUtil() {

    }

    public static CompressUtil create() {
        return Holder.instance;
    }

    private static class Holder {
        private static final CompressUtil instance = new CompressUtil();
    }


    public File invokeCompress(@NonNull String filePath, @NonNull String suffix) {

        if (TextUtils.isEmpty(filePath)) {
            return null;
        }

        File file = new File(filePath);
        if (file == null) {
            return null;
        }

        //判断是否达到开始压缩的阈值
        if (file.length() <= EasyCompressor.getOptions().getMinSize()) {
            Log.e(EasyCompressor.TAG, "--未达到压缩阈值，不需要压缩--：" + filePath);
            return file;
        }

        return qualityCompress(scaledCompress(filePath), suffix);
    }


    /*************二次采样压缩图片分辨率*************/
    private Bitmap scaledCompress(String path) {

        //屏幕宽高
        int screenWidth = ScreenUtil.getScreenWidth(EasyCompressor.getContext());
        int screenHeight = ScreenUtil.getScreenHeight(EasyCompressor.getContext());

        //图片原始宽高
        int w = 0;
        int h = 0;

        //第一次采样，获取实际尺寸
        BitmapFactory.Options options = new BitmapFactory.Options();
        //解码模式默认是 argb_8888 32位 1像素占4字节
        options.inPreferredConfig = Bitmap.Config.RGB_565;//rgb_565 5+6+5=1位，一个像素占用两个字节
        //设置值读取图片边界
        options.inJustDecodeBounds = true;
        //第一次采样：读取原始图片宽高，不加载到内存
        BitmapFactory.decodeFile(path, options);

        //获取图片宽高
        w = options.outWidth;
        h = options.outHeight;

        //压缩后的宽高
        int compressedW = w;
        int compressedH = h;


        /******计算缩放后的图片宽高******/
        //是否需要缩放
        boolean needScaled = false;

        //1、原图是宽图
        if (w > h) {
            //原图宽度大于屏幕宽度
            if (w > screenWidth) {
                needScaled = true;
                int pi = w / screenWidth;
                //原图宽度在屏幕宽度的两倍以上，引入质量控制系数 pi*2/3
                //todo 这里可以将压缩等级划分的更精细，针对原图和屏幕尺寸不同比例，采用不同等级的缩放方式
                int newPI = calculatePI(pi);
                compressedW = screenWidth * newPI;
                //compressedW = screenWidth * pi * 2 / 3;
                compressedH = h * compressedW / w;
            }
        }

        //2、原图是长图
        if (w < h) {
            if (h > screenHeight) {
                needScaled = true;
                int pi = h / screenHeight;
                //原图长度在屏幕长度的两倍以上
                int newPI = calculatePI(pi);
                compressedH = screenHeight * newPI;
                //compressedH = screenHeight * pi * 2 / 3;
                compressedW = compressedH * w / h;
            }
        }

        //3、原图是方形图，直接缩放到
        if (w == h) {
            if (w > screenWidth) {
                needScaled = true;
                int pi = w / screenWidth;
                //原图宽为屏幕宽度的2倍以上
                int newPI = calculatePI(pi);
                compressedH = compressedW = screenWidth * newPI;
                //compressedH = compressedW = screenWidth * pi * 2 / 3;
            }
        }

        //设置缩放比例
        if (needScaled) {
            options.inSampleSize = calculateInSampleSize(w, h, compressedW, compressedH);
        }

        /****第二次采样，申请内存，加载的是等比缩放之后的缩略图，避免OOM****/
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }


    /**
     * @param width     原始宽度
     * @param height    原始高度
     * @param reqWidth  缩放后宽度
     * @param reqHeight 缩放后高度
     * @return
     */
    private int calculateInSampleSize(int width, int height, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    //质量压缩
    private File qualityCompress(Bitmap image, String suffix) {

        //第一次转入stream中
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (TextUtils.equals(suffix, ImageOption.IMG_PNG_SUFFIX)) {
            image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        } else {
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        }

        int options = 100;
        //质量压缩步进量
        final int step=3;
        //循环判断如果压缩后图片是否大于3M,大于继续压缩
        while ( (options-step)>0 && baos.toByteArray().length > EasyCompressor.getOptions().getTargetSize() && (EasyCompressor.getOptions().isNeedQuality() ? ((options - step) > EasyCompressor.getOptions().getMinQuality()) : true)) {
            // Clean up os
            baos.reset();
            // interval 10
            options -= step;
            if (TextUtils.equals(suffix, ImageOption.IMG_PNG_SUFFIX)) {
                image.compress(Bitmap.CompressFormat.PNG, options, baos);
            } else {
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            }
        }

        //质量压缩之后的图片保存到文件中
        return FileUtil.getInstance().write2Local(baos.toByteArray(), suffix);
    }

    private int calculatePI(int pi) {

        if (pi > 1 && pi <= 2) {
            pi = 8 / 10;
        } else if (pi > 2 && pi <= 3) {
            pi = 9 / 10;
        } else if (pi > 3 && pi <= 4) {
            pi = 1;
        } else if (pi > 4 && pi <= 5) {
            pi = 11 / 10;
        } else if (pi > 5 && pi <= 6) {
            pi = 12 / 10;
        } else if (pi > 6 && pi <= 7) {
            pi = 13 / 10;
        } else if (pi > 7 && pi <= 8) {
            pi = 14 / 10;
        } else if (pi > 8 && pi <= 9) {
            pi = 15 / 10;
        } else if (pi > 9 && pi <= 10) {
            pi = 16 / 10;
        } else if (pi > 10 && pi <= 11) {
            pi = 17 / 10;
        } else if (pi > 11 && pi <= 12) {
            pi = 18 / 10;
        } else if (pi > 12) {
            pi = 19 / 10;
        }


        return pi;
    }

}
