package com.example.user.testnav2;

import android.content.SharedPreferences;
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

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Main1ActivityEdit extends AppCompatActivity {

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private EditText mName;
    private Button mSave;

    private LocationUtils LU;


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
    }




    protected void getUserData() {
        String oldName = mPreferences.getString(getString(R.string.username), "");
        mName.setText(oldName);
    }

    protected void saveUserData() {
        String newName = mName.getText().toString();
        mEditor.putString(getString(R.string.username), newName);
        mEditor.commit();
    }

    protected void configureSaveButton() {
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserData();
                finish();
            }
        });
    }

    private void configureTrackingStatus() {

        TextView trackingDisplay = (TextView) findViewById(R.id.trackingEditOutput);
        DeviceIDGenerator didg = new DeviceIDGenerator();
        String deviceID = didg.getID(Main1ActivityEdit.this);
        trackingDisplay.setText(deviceID);
        String url = "http://13.59.24.178/trackingStatusChild.php?childid=" + deviceID;
        String example = "[]";
        try {
            example = new AsyncTaskRestClient().execute(url).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        String empty = "[]";
        if (example.equals(empty)) {
            trackingDisplay.setText("Disabled");
            trackingDisplay.setTextColor(this.getResources().getColor(R.color.traffic_red));
        } else {
            trackingDisplay.setText("Enabled");
            trackingDisplay.setTextColor(this.getResources().getColor(R.color.traffic_green));
        }
    }

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
                Boolean isParent = false;
                if (rb.getText().equals("Tracking on")) {
                    //turn tracking on
                    String childName = mPreferences.getString(getString(R.string.username), "");
                    DeviceIDGenerator didg = new DeviceIDGenerator();
                    String deviceID = didg.getID(Main1ActivityEdit.this);
                    isParent = true;
                    mEditor.putBoolean("isParent", isParent);
                    mEditor.commit();
//                    List temp = LU.getLocation();
//                    double lat = (double) temp.get(0);
//                    double lon = (double) temp.get(1);
//                    String url = "http://13.59.24.178/trackerSignUp.php?name=" + childName + "&childid=" + deviceID + "&lat=" + lat + "&lon=" + lon;
                    String url = "http://13.59.24.178/trackerSignUp.php?name=" + childName + "&childid=" + deviceID + "&lat=145.0380897&lon=-37.8779852";
                    Log.e("FIT5120", url);
                    String result = "";
                    try {
                        result = new AsyncTaskRestClient().execute(url).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    Log.e("onCheckedChanged", result);
                    configureTrackingStatus();
                } else {
                    isParent = false;
                    mEditor.putBoolean("isParent", isParent);
                    mEditor.commit();
                    DeviceIDGenerator didg = new DeviceIDGenerator();
                    String deviceID = didg.getID(Main1ActivityEdit.this);
                    String url = "http://13.59.24.178/stopTracking.php?childid=" + deviceID;
                    String result = "";

                    try {
                        result = new AsyncTaskRestClient().execute(url).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    Log.e("FIT5120", url);
                    Log.e("onCheckedChanged", result);
                    configureTrackingStatus();
                }
            }
        });
    }
}
