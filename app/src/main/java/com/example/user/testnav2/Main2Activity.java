package com.example.user.testnav2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.concurrent.ExecutionException;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        configureHomeButton2();
        configureTrackingElements();
    }

    private void configureTrackingElements() {
        Button saveCode = (Button) findViewById(R.id.saveChildCode);
        final TextView tv = (TextView) findViewById(R.id.pairStatus);
        final EditText codeEntry = (EditText) findViewById(R.id.enterTrackingCode);
        final EditText nameEntry = (EditText) findViewById(R.id.enterChildName);


        saveCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = String.valueOf(codeEntry.getText());
                String name = String.valueOf(nameEntry.getText());
                DeviceIDGenerator didg = new DeviceIDGenerator();
                String deviceID = didg.getID(Main2Activity.this);
                final String url = "http://13.59.24.178/linkParent2.php?name=" + name + "&code=" + code + "&parentID=" + deviceID;
                final String example = "[]";
                String temp = example;
                try {
                    temp = new AsyncTaskRestClient().execute(url).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                if (temp.equals("[]")) {
                    tv.setText("Error");
                } else {
                    try {
                        JSONArray ja = new JSONArray(temp);
                        double lat = ja.getJSONObject(0).getDouble("childLat");
                        double lon = ja.getJSONObject(0).getDouble("childLon");
                        String details = ja.getJSONObject(0).getString("details");

                        Intent intent = new Intent(Main2Activity.this, TrackMapActvity.class);
                        intent.putExtra("lat", lat);
                        intent.putExtra("lon", lon);

                        startActivity(intent);
                      //  String response = name + " was located at " + lat + "," + lon + " on " + details;
                    //    tv.setText(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });


    }

    private void configureHomeButton2() {
        Button homebutton2 = (Button) findViewById(R.id.homebuttonmain2);
        homebutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
