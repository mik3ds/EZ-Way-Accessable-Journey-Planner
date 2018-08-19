package com.example.user.testnav2;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class weatherGetter extends AsyncTask<String, String, String>{


    protected String doInBackground(String ...urls) {

        HttpURLConnection con = null;
        InputStream is = null;
        String MelbURL = "http://api.openweathermap.org/data/2.5/weather?id=2158177&units=metric&APPID=6fe38b2ddf629ff4c64d523fa234338f";

        try {
            con = (HttpURLConnection) ( new URL(MelbURL)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine())!=null)
                buffer.append(line + "rn");

            is.close();
            con.disconnect();
            return buffer.toString();


        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try { is.close(); } catch(Throwable t) {}
            try {con.disconnect(); } catch(Throwable t) {}
        }
        return null;
    }
}
