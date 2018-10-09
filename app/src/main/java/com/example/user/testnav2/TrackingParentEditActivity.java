package com.example.user.testnav2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class TrackingParentEditActivity extends AppCompatActivity {
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_parent_edit);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();

        configureHomeButton2();
        configureTrackingElements();
    }

    private void configureTrackingElements() {
        Button saveCode = (Button) findViewById(R.id.saveChildCode);
        final TextView tv = (TextView) findViewById(R.id.pairStatus);
        final EditText codeEntry = (EditText) findViewById(R.id.enterTrackingCode);
        final EditText nameEntry = (EditText) findViewById(R.id.enterChildName);

        //Save input from user when button has been clicked
        saveCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = String.valueOf(codeEntry.getText());
                String name = String.valueOf(nameEntry.getText());
                DeviceIDGenerator didg = new DeviceIDGenerator();
                String deviceID = didg.getID(TrackingParentEditActivity.this);
                mEditor.putString("deviceID", deviceID);
                mEditor.putString("name", name);
                mEditor.putString("code", code);
                mEditor.commit();
                final String url = "http://13.59.24.178/linkParent2.php?name=" + name + "&code=" + code + "&parentID=" + deviceID;
                final String example = "[]";
                String temp = example;
                try {
                    temp = new AsyncTaskRestClient().execute(url).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                if (temp.equals("[]")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TrackingParentEditActivity.this);
                    builder.setTitle("Pair failed");
                    builder.setMessage("Wrong child name or wrong pair code, please try again.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            TrackingParentEditActivity.this.recreate();
                            codeEntry.getText().clear();
                            nameEntry.getText().clear();
                        }
                    });
                    AlertDialog ad = builder.create();
                    ad.show();
                } else {
                    try {
                        JSONArray ja = new JSONArray(temp);
                        double lat = ja.getJSONObject(0).getDouble("childLat");
                        double lon = ja.getJSONObject(0).getDouble("childLon");
                        String details = ja.getJSONObject(0).getString("details");

                        mEditor.putString("childlat",Double.toString(lat));
                        mEditor.putString("childlon",Double.toString(lon));
                        mEditor.apply();

                        if(!mPreferences.getBoolean("isParent", false)){
                            mEditor = mPreferences.edit();
                            mEditor.putBoolean("isParent", true);
                            mEditor.putBoolean("firstTimeRun",true);
                            mEditor.apply();
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(TrackingParentEditActivity.this);
                        builder.setTitle("Pair Success");
                        builder.setMessage("You are paired with your child.");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(TrackingParentEditActivity.this, MapActivity.class));
                            }
                        });
                        AlertDialog ad = builder.create();
                        ad.show();
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
