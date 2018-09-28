package com.example.user.testnav2;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncTaskRestClient extends AsyncTask<String,Void,String>{


    @Override
    protected String doInBackground(String... params){
        String urls = params[0];
        String result;

        try {
            URL url = new URL(urls);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();

            String inputString;
            while ((inputString = bufferedReader.readLine()) != null) {
                builder.append(inputString);
            }
            urlConnection.disconnect();
            result = builder.toString();
        }
        catch(Exception e){
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    protected String getTrackingStatus(String... params){
        String stringUrl = params[0];
        String result;
        String inputLine;

        try {
            URL myUrl = new URL(stringUrl);

            HttpURLConnection connection =(HttpURLConnection) myUrl.openConnection();

            connection.setRequestMethod("GET");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);

            connection.connect();

            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while((inputLine = reader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }

            reader.close();
            streamReader.close();
            result = stringBuilder.toString();

        }
        catch(IOException e){
            e.printStackTrace();
            result = null;
        }

        return result;
    }



    protected void onPostExecute(String result){
        super.onPostExecute(result);
    }

}