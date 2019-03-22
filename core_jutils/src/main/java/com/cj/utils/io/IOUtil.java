package com.cj.utils.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;

/**
 * Author:chris - jason
 * Date:2019/3/5.
 * Package:com.cj.utils.io
 */
//IO操作、变换工具类
public class IOUtil {


    private IOUtil(){

    }

    private static class Holder{
        public static IOUtil instance = new IOUtil();
    }

    public static IOUtil getInstance(){
        return Holder.instance;
    }

    //通过url链接，获取图片的byte 数组
    public byte[] getByteArrayFromUrl(final String url) {

        CountDownLatch latch = new CountDownLatch(1);//创建一个CountDownLatch，只有一个任务数

        UrlDecodeTask thread = new UrlDecodeTask(url, latch);//创建并启动我们的异步任务
        thread.start();

        try {
            //阻塞主线程
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //等待异步任务执行完成之后，主线程就会继续执行
        InputStream is = thread.getInputStream();
        return is == null ? null : inputStreamToByte(is);

    }

    //输入流转byte数组
    public byte[] inputStreamToByte(InputStream is) {
        try {
            ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
            int ch;
            while ((ch = is.read()) != -1) {
                bytestream.write(ch);
            }
            byte imgdata[] = bytestream.toByteArray();
            bytestream.close();
            return imgdata;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //读取磁盘文件 --》byte数组
    public byte[] readFromFile(String fileName, int offset, int len) {
        if (fileName == null) {
            return null;
        }

        File file = new File(fileName);
        if (!file.exists()) {
            log("文件不存在："+fileName);
            return null;
        }

        if (len == -1) {
            len = (int) file.length();
        }


        if (offset < 0) {
            log( "readFromFile invalid offset:" + offset);
            return null;
        }
        if (len <= 0) {
            log("readFromFile invalid len:" + len);
            return null;
        }
        if (offset + len > (int) file.length()) {
            log("readFromFile invalid file len:" + file.length());
            return null;
        }

        byte[] b = null;
        try {
            RandomAccessFile in = new RandomAccessFile(fileName, "r");
            b = new byte[len]; // ���������ļ���С������
            in.seek(offset);
            in.readFully(b);
            in.close();

        } catch (Exception e) {
            log( "readFromFile : errMsg = " + e.getMessage());
            e.printStackTrace();
        }
        return b;
    }


    private void log(String msg){
        System.out.println(msg);
    }


}
