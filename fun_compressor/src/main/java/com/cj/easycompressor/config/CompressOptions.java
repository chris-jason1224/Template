package com.cj.easycompressor.config;

/**
 * Created by mayikang on 2018/9/12.
 */

/**
 * EasyCompressor压缩参数
 */
public class CompressOptions {

    private long targetSize= 3 / 2 * 1024 * 1024;//压缩后文件大小,default 1.5M

    private boolean needQuality=true;//压缩尺寸时是否需要保证最小质量，true：压缩后质量不能低于minQuality false：不限制压缩后质量，只保证压缩到targetSize

    private int minQuality=65;//最小压缩质量（0-100）default 65%

    private int minSize= 1024*500;//文件开始压缩的阈值，默认500K以下的图片不需要压缩处理

    public CompressOptions(){

    }

    public CompressOptions(long targetSize, boolean needQuality, int minQuality, int minSize) {
        this.targetSize = targetSize;
        this.needQuality = needQuality;
        this.minQuality = minQuality;
        this.minSize = minSize;
    }

    public long getTargetSize() {
        return targetSize;
    }

    public void setTargetSize(long targetSize) {
        this.targetSize = targetSize;
    }

    public int getMinQuality() {
        return minQuality;
    }

    public void setMinQuality(int minQuality) {
        this.minQuality = minQuality;
    }

    public boolean isNeedQuality() {
        return needQuality;
    }

    public void setNeedQuality(boolean needQuality) {
        this.needQuality = needQuality;
    }

    public int getMinSize() {
        return minSize;
    }

    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }
}
