package com.cj.common.http.repository;


import android.content.Context;
import android.os.Build;
import android.webkit.WebSettings;

import com.cj.common.BuildConfig;
import com.cj.common.base.BaseApp;
import com.cj.common.http.base.BaseProgressResponseBody;
import com.cj.common.util.AndroidSystemUtil;
import com.cj.common.util.DiskCacheUtil;
import com.cj.manager.basement.BaseApplication;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mayikang on 2018/8/20.
 */

public class RetrofitFactory {

    public static final String BASE_URL = "http://192.168.2.13:8080";
    private Retrofit retrofit;

    private RetrofitFactory() {
        createRetrofit();
    }

    public static RetrofitFactory getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        public static final RetrofitFactory instance = new RetrofitFactory();
    }


    //网络缓存，只会缓存GET方式
    //todo 后面把数据库模块规划好之后，设计一个POST缓存逻辑
    private Cache getCache(Context context) {
        return new Cache(new File(context.getCacheDir(), "httpCache"), 1024 * 1024 * 10);
    }


    private OkHttpClient getHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        //debug模式添加日志拦截器
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }

        builder.cache(getCache(BaseApplication.getInstance().getApplicationContext()))//缓存
                .readTimeout(5, TimeUnit.SECONDS)//读取超时
                .writeTimeout(5, TimeUnit.SECONDS)//写入超时
                .connectTimeout(15, TimeUnit.SECONDS)//连接超时
                .retryOnConnectionFailure(true);//自动重连

        //添加请求拦截器
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //原始请求
                Request original_request = chain.request();
                //修改后的请求
                Request.Builder builder = original_request.newBuilder();
                builder.
                        //添加token，由登录成功后保存到本地
                        addHeader("UserToken", DiskCacheUtil.getInstance().getToken())
                        //设置User-Agent
                        .addHeader("User-Agent", Build.VERSION.SDK_INT > 17 ? WebSettings.getDefaultUserAgent(BaseApplication.getInstance().getApplicationContext()) : System.getProperty("http.agent"))
                        //app版本号
                        .addHeader("AppVersionCode", AndroidSystemUtil.getInstance().getAppVersionCode(BaseApplication.getInstance().getApplicationContext()) + "")
                        //app版本名
                        .addHeader("AppVersionName", AndroidSystemUtil.getInstance().getAppVersionName(BaseApplication.getInstance().getApplicationContext()) + "")
                        //Content-Type
                        .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                        //
                        .addHeader("Accept-Encoding", "gzip,deflate")
                        //
                        .addHeader("Connection", "keep-alive")
                        //
                        .addHeader("Accept", "*/*");

                return chain.proceed(builder.build());
            }
        });

        //监听加载进度
        builder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                return response.newBuilder().body(new BaseProgressResponseBody(response.body(),
                        new BaseProgressResponseBody.ProgressListener() {
                            @Override
                            public void onProgress(long totalSize, long downSize) {
                                //todo 这里回调请求结果的进度

                            }
                        })).build();
            }
        });

        return builder.build();
    }

    //创建Retrofit实体
    private void createRetrofit() {

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson()))
                .build();
    }

    private Gson gson() {
        Gson gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .setDateFormat(DateFormat.LONG)
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)//会把字段首字母大写
                .setPrettyPrinting()
                .setVersion(1.0)
                .create();

        return gson;
    }

    public <T> T createApi(Class<T> clazz) {
        return retrofit.create(clazz);
    }

}

