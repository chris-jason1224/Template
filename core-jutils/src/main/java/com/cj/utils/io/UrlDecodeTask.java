package com.cj.utils.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CountDownLatch;

public class UrlDecodeTask extends Thread {
        private String url;
        private CountDownLatch countDownLatch;
        private InputStream[] isArray = new InputStream[1];

        public UrlDecodeTask(String url, CountDownLatch countDownLatch) {
            this.url = url;
            this.countDownLatch = countDownLatch;
        }
        public InputStream getInputStream() {
            return isArray[0];
        }

        @Override
        public void run() {
            try {
                URL htmlUrl = new URL(url);
                URLConnection connection = htmlUrl.openConnection();
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                int responseCode = httpConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    isArray[0] = httpConnection.getInputStream();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }

        }
    }