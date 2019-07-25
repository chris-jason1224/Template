package com.cj.common.util.image;

import android.graphics.drawable.Animatable;
import android.view.ViewGroup;

import com.cj.manager.basement.BaseApplication;
import com.cj.ui.util.ScreenUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

/**
 * Author:chris - jason
 * Date:2019-07-24.
 * Package:com.iflow.karision_baseui.utils.image
 * SimpleDraweeView 自适应宽高修改
 */
public class FrescoLoadCallback implements IImageLoadCallback {

    private SimpleDraweeView mDraweeView;
    private boolean keepOri;

    /**
     *
     * @param mDraweeView
     * @param keepOri 当图片宽高不足屏幕宽高时，是否按照原始图片尺寸展示
     */
    public FrescoLoadCallback(SimpleDraweeView mDraweeView, boolean keepOri) {
        this.mDraweeView = mDraweeView;
        this.keepOri = keepOri;
    }

    @Override
    public void onSuccess(String id, ImageInfo imageInfo, Animatable animatable) {

        //图片宽高
        int w = imageInfo.getWidth();
        int h = imageInfo.getHeight();

        //是否需要缩放,默认不需要缩放时，将图片等比例拉伸到屏幕宽度或长度大小，
        boolean needScaled = false;

        //缩放后宽高
        int compressedW = 0;
        int compressedH = 0;

        //屏幕宽高
        int screenWidth = ScreenUtil.getScreenWidth(BaseApplication.getInstance());
        int screenHeight = ScreenUtil.getScreenHeight(BaseApplication.getInstance());

        //1、原图是宽图
        if (w > h) {
            //原图宽度大于屏幕宽度
            if (w > screenWidth) {
                double pi = w / screenWidth;
                double newPI = calculatePI(pi);
                if (newPI <= 0) {
                    newPI = 1;
                }
                compressedW = (int) (screenWidth * newPI);
                compressedH = h * compressedW / w;
            } else {
                //是否需要保留图片原始宽高
                if(keepOri){
                    compressedW = w;
                    compressedH = h;
                }else {
                    compressedW = screenWidth;
                    compressedH = h * compressedW / w;
                }
            }
        }

        //2、原图是长图
        if (w < h) {
            if (h > screenHeight) {
                needScaled = true;
                double pi = h / screenHeight;
                //原图长度在屏幕长度的两倍以上
                double newPI = calculatePI(pi);
                if (newPI <= 0) {
                    newPI = 1;
                }
                compressedH = (int) (screenHeight * newPI);
                compressedW = compressedH * w / h;
            } else {
                if(keepOri){
                    compressedW = w;
                    compressedH = h;
                }else {
                    compressedH = screenHeight;
                    compressedW = compressedH * w / h;
                }
            }
        }

        //3、原图是方形图，直接缩放到
        if (w == h) {
            if (w > screenWidth) {
                needScaled = true;
                double pi = w / screenWidth;
                //原图宽为屏幕宽度的2倍以上
                double newPI = calculatePI(pi);
                if (newPI <= 0) {
                    newPI = 1;
                }
                compressedH = compressedW = (int) (screenWidth * newPI);
            } else {
                if(keepOri){
                    compressedW = w;
                    compressedH = h;
                }else {
                    compressedH = compressedW = screenWidth;
                }
            }
        }

        if (compressedW == 0) {
            compressedW = screenWidth;
        }
        if (compressedH == 0) {
            compressedH = screenHeight;
        }

        ViewGroup.LayoutParams lp = mDraweeView.getLayoutParams();

        lp.width = compressedW;
        lp.height = compressedH;

        mDraweeView.setLayoutParams(lp);

    }

    @Override
    public void onFailed(String id, Throwable throwable) {

    }


    private double calculatePI(double pi) {

        if (pi > 1 && pi <= 2) {
            pi = 0.8;
        } else if (pi > 2 && pi <= 3) {
            pi = 0.9;
        } else if (pi > 3 && pi <= 4) {
            pi = 1;
        } else if (pi > 4 && pi <= 5) {
            pi = 1.1;
        } else if (pi > 5 && pi <= 6) {
            pi = 1.2;
        } else if (pi > 6 && pi <= 7) {
            pi = 1.3;
        } else if (pi > 7 && pi <= 8) {
            pi = 1.4;
        } else if (pi > 8 && pi <= 9) {
            pi = 1.5;
        } else if (pi > 9 && pi <= 10) {
            pi = 1.6;
        } else if (pi > 10 && pi <= 11) {
            pi = 1.7;
        } else if (pi > 11 && pi <= 12) {
            pi = 1.8;
        } else if (pi > 12) {
            pi = 1.9;
        }

        return pi;
    }
}
