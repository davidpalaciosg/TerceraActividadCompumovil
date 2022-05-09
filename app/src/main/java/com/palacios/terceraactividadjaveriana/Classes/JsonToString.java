package com.palacios.terceraactividadjaveriana.Classes;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class JsonToString {
    //Build the string from Json file
    public static String loadJSONFromAsset(String fileName, Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
