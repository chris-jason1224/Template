package com.cj.common.provider.fun$business.share;

/**
 * Author:chris - jason
 * Date:2019/2/3.
 * Package:com.cj.common.provider.fun$business.share
 * 微信公司分享参数
 */

public class WeChatShareParams extends ShareParams {

    private String title;//消息标题 不超过512Bytes

    private String description;//消息描述 不超过1kb

    private String text;//要分享的文本内容 ，（微信）长度大于0小于10kb

    private byte[] imageData;//要分享的图片内容 --》 图片的二进制数据  （微信）小于10M

    private String imagePath;//要分享的图片内容 --》 图片的本地路径    （微信）小于10M

    private String webpageUrl;//要分享的网页的连接 --》网页html连接    （微信）小于10kb

    public WeChatShareParams() {
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getWebpageUrl() {
        return webpageUrl;
    }

    public void setWebpageUrl(String webpageUrl) {
        this.webpageUrl = webpageUrl;
    }
}
