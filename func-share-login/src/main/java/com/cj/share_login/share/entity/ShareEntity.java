package com.cj.share_login.share.entity;

/**
 * Created by mayikang on 2018/3/14.
 */

public class ShareEntity {

    //分享到的平台：1-微信 2-朋友圈 3-QQ 4-QQ空间 5-新浪微博
    //@link SharePlatformType
    private int platform;

    //分享内容的类型：1-网页  2-图片  3-文字
    //@link ShareContentType
    private int shareType;

    //标题
    private String title;

    //要分享的链接
    private String url;

    //文本描述
    private String text;

    //要附带的图片地址
    private String imgPath;


    public ShareEntity() {
    }

    public ShareEntity(int platform) {
        this.platform = platform;
    }

    public ShareEntity(int platform, int shareType, String title, String url, String text, String imgPath) {
        this.platform = platform;
        this.shareType = shareType;
        this.title = title;
        this.url = url;
        this.text = text;
        this.imgPath = imgPath;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getShareType() {
        return shareType;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public void setShareType(int shareType) {
        this.shareType = shareType;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ShareEntity{" +
                "platform=" + platform +
                ", shareType=" + shareType +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", text='" + text + '\'' +
                ", imgPath='" + imgPath + '\'' +
                '}';
    }


}
