package com.example.user.testnav2;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncTaskRestClient extends AsyncTask<String,Void,String>{


    @Override
    protected String doInBackground(String... params){
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