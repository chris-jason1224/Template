package com.cj.common.ipc;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author:chris - jason
 * Date:2019-05-31.
 * Package:com.cj.common.ipc
 * IPC通讯的消息实体
 */
public class PostDataEntity implements Parcelable {

    private long id;//消息id，采用时间戳保证一致

    private String data;//数据用json格式发送

    public PostDataEntity(long id, String data) {
        this.id = id;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.data);
    }

    public PostDataEntity() {
    }



    protected PostDataEntity(Parcel in) {
        this.id = in.readLong();
        this.data = in.readString();
    }

    public static final Creator<PostDataEntity> CREATOR = new Creator<PostDataEntity>() {
        @Override
        public PostDataEntity createFromParcel(Parcel source) {
            return new PostDataEntity(source);
        }

        @Override
        public PostDataEntity[] newArray(int size) {
            return new PostDataEntity[size];
        }
    };
}
