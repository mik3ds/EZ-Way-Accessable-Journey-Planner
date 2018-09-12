package com.example.user.testnav2;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 * Created by mark on 9/8/2018.
 */

public class RestClient {
    private static final String base_URL = "http://13.59.24.178/";



    public static JSONArray getToiLoc(Double lat, Double lon){
        //initialise
        HttpURLConnection conn = null;
        JSONArray JSONResult = new JSONArray();

        LocationUtils LU = new LocationUtils();
        String toi_URL = base_URL + "nearbyToilets.php?lat=" + lon + "&lon=" + lat;

        //Making HTTP request
        try{
            URL url = new URL(toi_URL);
            //open the connection
            conn = (HttpURLConnection) url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            StringBuilder sb = new StringBuilder();

            //set the connection method to GET
            conn.setRequestMethod("GET");
            //add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //read the input stream and store it as string
            int responseCode = conn.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){

                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null)
                    sb.append(line);
            }
            JSONResult = new JSONArray(sb.toString());//String to JSONArray
            } catch (IOException | JSONException e){
                e.printStackTrace();
            } finally
         {
            conn.disconnect();
        }
        return JSONResult;
    }


    public static JSONArray getStaLoc(Double lat, Double lon){
        //initialise
        HttpURLConnection conn = null;
        JSONArray JSONResult = new JSONArray();

        LocationUtils LU = new LocationUtils();
        String toi_URL = base_URL + "nearbyStations.php?lat=" + lon + "&lon=" + lat;

        //Making HTTP request
        try{
            URL url = new URL(toi_URL);
            //open the connection
            conn = (HttpURLConnection) url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            StringBuilder sb = new StringBuilder();

            //set the connection method to GET
            conn.setRequestMethod("GET");
            //add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //read the input stream and store it as string
            int responseCode = conn.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){

                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null)
                    sb.append(line);
            }
            JSONResult = new JSONArray(sb.toString());//String to JSONArray
        } catch (IOException | JSONException e){
            e.printStackTrace();
        } finally
        {
            assert conn != null;
            conn.disconnect();
        }
        return JSONResult;
    }


}
