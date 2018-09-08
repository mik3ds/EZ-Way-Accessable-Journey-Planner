package com.example.user.testnav2;


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

    public static void getToiLoc(){
        getRequest();
    }

    public static String getRequest(){
        //initialise
        HttpURLConnection conn = null;
        String textResult = "";
        //Making HTTP request
        try{
            URL url = new URL(toi_URL);
            //open the connection
            conn = (HttpURLConnection) url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to GET
            conn.setRequestMethod("GET");
            //add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            conn.disconnect();
        }
        return textResult;
    }

}
