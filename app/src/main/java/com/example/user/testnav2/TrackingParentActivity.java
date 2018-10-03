package com.example.user.testnav2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class TrackingParentActivity extends AppCompatActivity {

    private Button wipeButton;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_parent);

        wipeButton = findViewById(R.id.trackingParentWipeSettings);

        configureEditButton();
        configureWipeButton();

        try {
            configureTrackingStatus();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void configureWipeButton() {
        wipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asyncTrackingOff();
            }
        });
    }


    public void asyncTrackingOff() {
        TrackingParentActivity.trackingOff t = new TrackingParentActivity.trackingOff();
        t.turnOff();
    }

    public class trackingOff extends AsyncTask<Void,Void,Void> {
        private String urls;
        protected void turnOff() {
            String parentid = DeviceIDGenerator.getID(TrackingParentActivity.this);
            urls = "http://13.59.24.178/stopTrackingParent.php?parentid=" + parentid;
            execute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String tempstring;
            try{
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
                tempstring = builder.toString();
                configureTrackingStatus();
            } catch (IOException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            Log.e("doInBackground","finishes");
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Log.e("help","onPost happens");
            mPreferences = PreferenceManager.getDefaultSharedPreferences(TrackingParentActivity.this);
            mEditor = mPreferences.edit();
            mEditor.putBoolean("isParent", false);
            mEditor.apply();
            finish();
        }
    }

    private void configureEditButton() {
        Button trackingParentEditButton = (Button) findViewById(R.id.trackingParentButton);
        trackingParentEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TrackingParentActivity.this, TrackingParentEditActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            configureTrackingStatus();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    protected void configureTrackingStatus() throws ExecutionException, InterruptedException {
        TextView trackingDisplay = (TextView) findViewById(R.id.trackingParentStatusText);
        DeviceIDGenerator didg = new DeviceIDGenerator();
        String deviceID = didg.getID(TrackingParentActivity.this);
        String url = "http://13.59.24.178/trackingStatusParent.php?parentid=" + deviceID;
        String example = new AsyncTaskRestClient().execute(url).get();
        String empty = "[]";
        if (example.equals(empty)) {
            String notPairedText = "You are not currently paired";
            wipeButton.setVisibility(View.INVISIBLE);
            trackingDisplay.setText(notPairedText);
            trackingDisplay.setTextColor(this.getResources().getColor(R.color.red));
        } else {
            try {
                JSONArray ja = new JSONArray(example);
                String code = "Successfully Paired.";
                wipeButton.setVisibility(View.VISIBLE);
                trackingDisplay.setText(code);
                trackingDisplay.setTextColor(this.getResources().getColor(R.color.green));
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }


    }
}
