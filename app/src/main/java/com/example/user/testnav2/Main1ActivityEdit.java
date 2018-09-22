package com.example.user.testnav2;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class    Main1ActivityEdit extends AppCompatActivity {

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private String trackStatus;

    private EditText mName;
    private Button mSave;

    private LocationUtils LU;

    //Initialise variables
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1_edit);
        mName = (EditText) findViewById(R.id.editProfName);
        mSave = (Button) findViewById(R.id.saveprofilebutton);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();
        configureSaveButton();
        getUserData();
        configureTrackingStatus();
        configureToggle();
        LU = new LocationUtils();
    }



    //Get user data method
    protected void getUserData() {
        String oldName = mPreferences.getString(getString(R.string.username), "");
        mName.setText(oldName);
    }

    //Set user data method
    protected void saveUserData() {
        String newName = mName.getText().toString();
        mEditor.putString(getString(R.string.username), newName);
        mEditor.commit();
    }

    //Save user data button
    protected void configureSaveButton() {
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserData();
                finish();
            }
        });
    }

    //Configure Tracking status
    private void configureTrackingStatus() {
        TextView trackingDisplay = (TextView) findViewById(R.id.trackingEditOutput);
        DeviceIDGenerator didg = new DeviceIDGenerator();
        String deviceID = didg.getID(Main1ActivityEdit.this);
        trackingDisplay.setText(deviceID);
        String url = "http://13.59.24.178/trackingStatusChild.php?childid=" + deviceID;
        String example = "[]";
        asyncTrackingStatus(url);

    }

    //Configure toggle button
    private void configureToggle() {
        RadioGroup rg = (RadioGroup) findViewById(R.id.toggleTrackingRadioGroup);
        rg.clearCheck();

        TextView trackingDisplay = (TextView) findViewById(R.id.trackingEditOutput);
        if (trackingDisplay.getText() == "Disabled") {
            rg.check(R.id.toggleOff);
        } else {
            rg.check(R.id.toggleOn);
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton) radioGroup.findViewById(i);
                if (rb.getText().equals("Tracking on")) {
                    //turn tracking on
                    String childName = mPreferences.getString(getString(R.string.username), "");
                    DeviceIDGenerator didg = new DeviceIDGenerator();
                    String deviceID = didg.getID(Main1ActivityEdit.this);
                    mEditor.putBoolean("isParent", false);
                    mEditor.commit();
                    String url = "http://13.59.24.178/trackerSignUp.php?name=" + childName + "&childid=" + deviceID + "&lat=0.0&lon=0.0";
                    asyncEnableTracking(url);
                    String result = "";
                    asyncEnableTracking(url);
                    configureTrackingStatus();
                } else {
                    mEditor.putBoolean("isParent", false);
                    mEditor.commit();
                    DeviceIDGenerator didg = new DeviceIDGenerator();
                    String deviceID = didg.getID(Main1ActivityEdit.this);
                    String url = "http://13.59.24.178/stopTracking.php?childid=" + deviceID;
                    String result = "";
                    asyncStopTracking(url);
                    configureTrackingStatus();
                }
            }
        });
    }

    public void asyncEnableTracking(String url) {
        Main1ActivityEdit.EnableTrackingAsyncTask t = new Main1ActivityEdit.EnableTrackingAsyncTask(this);
        t.enableTracking(url);
    }

    private class EnableTrackingAsyncTask extends AsyncTask<Void,Void,Void> {
        private String savedURL;
        private String tempString;
        private WeakReference<Main1ActivityEdit> activityWeakReference;
        EnableTrackingAsyncTask(Main1ActivityEdit activity) {
            activityWeakReference = new WeakReference<>(activity);
        }
        protected void enableTracking(String url) {
            savedURL = url;
            execute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                URL url = new URL(savedURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }
                urlConnection.disconnect();
                tempString = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void v) {

        }
    }

    public void asyncTrackingStatus(String url) {
        Main1ActivityEdit.TrackingStatusAsyncTask t = new Main1ActivityEdit.TrackingStatusAsyncTask(this);
        t.checkTracking(url);
    }

    private class TrackingStatusAsyncTask extends AsyncTask<Void,Void,String> {
        private String savedURL;
        private String tempString;
        private WeakReference<Main1ActivityEdit> activityWeakReference;
        TrackingStatusAsyncTask(Main1ActivityEdit activity) {
            activityWeakReference = new WeakReference<>(activity);
        }
        protected void checkTracking(String url) {
            savedURL = url;
            execute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                URL url = new URL(savedURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }
                urlConnection.disconnect();
                tempString = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TextView trackingDisplay = (TextView) findViewById(R.id.trackingEditOutput);

            String empty = "[]";
            if (tempString.equals(empty)) {
                trackingDisplay.setText("Disabled");
                trackingDisplay.setTextColor(getApplicationContext().getResources().getColor(R.color.traffic_red));
                mEditor.putBoolean("isParent", true);
                mEditor.commit();
            } else {
                trackingDisplay.setText("Enabled");
                trackingDisplay.setTextColor(getApplicationContext().getResources().getColor(R.color.traffic_green));
                mEditor.putBoolean("isParent", false);
                mEditor.commit();
            }
        }
    }

    public void asyncStopTracking(String url) {
        Main1ActivityEdit.StopTrackingAsyncTask t = new Main1ActivityEdit.StopTrackingAsyncTask(this);
        t.stopTracking(url);
    }

    private class StopTrackingAsyncTask extends AsyncTask<Void,Void,String> {
        private String savedURL;
        private String tempString;
        private WeakReference<Main1ActivityEdit> activityWeakReference;
        StopTrackingAsyncTask(Main1ActivityEdit activity) {
            activityWeakReference = new WeakReference<>(activity);
        }
        protected void stopTracking(String url) {
            savedURL = url;
            execute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                URL url = new URL(savedURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }
                urlConnection.disconnect();
                tempString = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            trackStatus = tempString;
        }
    }
}