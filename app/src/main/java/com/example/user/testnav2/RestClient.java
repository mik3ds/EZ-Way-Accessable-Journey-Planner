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
import java.util.Scanner;
/**
 * Created by mark on 9/8/2018.
 */

public class RestClient {
    private static final String toi_URL = "http://13.59.24.178/SamplePage.php";



    public static JSONArray getToiLoc(){
        //initialise
        HttpURLConnection conn = null;
        JSONArray JSONResult = new JSONArray();
        //Making HTTP request
        try{
            int i = 0;
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
            //Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            int responseCode = conn.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){

                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null)
                    sb.append(line);
            }
            JSONResult = new JSONArray(sb.toString());//String to JSONArray
            }catch (ProtocolException e) {
                e.printStackTrace();
            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }catch (JSONException e) {
                e.printStackTrace();
            }finally
         {
            conn.disconnect();
        }
        return JSONResult;
    }

}
