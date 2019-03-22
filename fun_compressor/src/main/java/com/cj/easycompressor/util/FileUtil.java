package com.cj.easycompressor.util;

import android.os.Environment;

import com.cj.easycompressor.core.EasyCompressor;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by mayikang on 2018/9/20.
 */

public class FileUtil {

    private FileUtil(){

    }

    private static class Holder{
        private static final FileUtil instance=new FileUtil();
    }

    public static FileUtil getInstance(){
        return Holder.instance;
    }

    /*
     *  FileOutputStream不管什么时候，写入速度都是最慢的.
     *
     *  BufferedOutputStream在写入的数据量不算大的情况下，速度比BufferedWriter要快，但当数据量变大时(>6M),BufferedWriter的写入速度是最快的。
     *
     *  &recommend:   数据量小的时候，选择BufferedOutputStream，数据量大的时候，选择BufferedWriter
     */


    /**
     * BufferedOutStream写文件
     * @param data
     * @param suffix
     * @return
     */
    public File write2Local(byte[] data,String suffix){

        if(data.length<0){
            try {
                throw new Exception("--质量压缩后要写出的data为null--");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //保存的文件路径
        String outPath = EasyCompressor.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/compress/";
        File dir = new File(outPath);
        //创建文件夹
        if(dir!=null){
            if(!dir.exists()){
                dir.mkdirs();
            }
        }

        //保存的文件
        File tempFile = new File(outPath + System.currentTimeMillis() + suffix);

        if (tempFile != null) {

            if(tempFile.exists()){
                tempFile.delete();
            }
            FileOutputStream fileOutputStream=null;
            BufferedOutputStream bufferedOutputStream= null;
            try {
                boolean create = tempFile.createNewFile();
                if(create){
                    fileOutputStream =new FileOutputStream(tempFile);

                    if(fileOutputStream!=null){
                        bufferedOutputStream=new BufferedOutputStream(fileOutputStream);
                        if(bufferedOutputStream!=null) {
                            bufferedOutputStream.write(data);
                            bufferedOutputStream.flush();
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(bufferedOutputStream!=null){
                    try {
                        bufferedOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(fileOutputStream!=null){
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


        }

        return tempFile;
    }

}
