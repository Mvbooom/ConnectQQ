package com.example.administrator.connectqq;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/8/8.
 */

public class Util {
    public static Bitmap getBitmap(String imageUri){
        Bitmap bitmap = null;
        try{
            URL url = new URL(imageUri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            //后面就可以直接使用getInputStream()
            connection.connect();
            InputStream is = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }
}
