package com.cj.utils.safe;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;  
  
public class AESUtil implements IEncryption{

    private static final String KEY_AES = "AES";
    private final String key ="A12fEUHdeghef21g";

    private AESUtil(){}

    @Override
    public String encrypt(String content) {

        try {
            return encrypt(content,key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String decrypt(String content) {

        try {
            return decrypt(content,key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static class Holder{
        private static final AESUtil instance = new AESUtil();
    }

    public static AESUtil getInstance(){
        return Holder.instance;
    }

    private  String encrypt(String src, String key) throws Exception {
        if (key == null || key.length() != 16) {  
            throw new Exception("key不满足条件");  
        }   
        byte[] raw = key.getBytes();  
        SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_AES);  
        Cipher cipher = Cipher.getInstance(KEY_AES);  
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);  
        byte[] encrypted = cipher.doFinal(src.getBytes());  
        return byte2hex(encrypted);  
    }  
  
    private  String decrypt(String src, String key) throws Exception {
        if (key == null || key.length() != 16) {  
            throw new Exception("key不满足条件");  
        }  
        byte[] raw = key.getBytes();  
        SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_AES);  
        Cipher cipher = Cipher.getInstance(KEY_AES);  
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);  
        byte[] encrypted1 = hex2byte(src);  
        byte[] original = cipher.doFinal(encrypted1);  
        String originalString = new String(original);  
        return originalString;  
    }  
  
    private  byte[] hex2byte(String strhex) {
        if (strhex == null) {  
            return null;  
        }  
        int l = strhex.length();  
        if (l % 2 == 1) {  
            return null;  
        }  
        byte[] b = new byte[l / 2];  
        for (int i = 0; i != l / 2; i++) {  
            b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2),  
                    16);  
        }  
        return b;  
    }  
  
    private  String byte2hex(byte[] b) {
        String hs = "";  
        String stmp = "";  
        for (int n = 0; n < b.length; n++) {  
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));  
            if (stmp.length() == 1) {  
                hs = hs + "0" + stmp;  
            } else {  
                hs = hs + stmp;  
            }  
        }  
        return hs.toUpperCase();  
    }

}  