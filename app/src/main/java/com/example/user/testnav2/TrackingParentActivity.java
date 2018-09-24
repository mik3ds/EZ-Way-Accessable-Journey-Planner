package com.example.user.testnav2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class TrackingParentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_parent);

        configureEditButton();
        try {
            configureTrackingStatus();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        trackingDisplay.setText(deviceID);
        String url = "http://13.59.24.178/trackingStatusParent.php?childid=" + deviceID;
        String example = new AsyncTaskRestClient().execute(url).get();
        String empty = "[]";
        if (example.equals(empty)) {
            String notPairedText = "You are not currently paired";
            trackingDisplay.setText(notPairedText);
            trackingDisplay.setTextColor(this.getResources().getColor(R.color.red));
        } else {
            try {
                JSONArray ja = new JSONArray(example);
                String code = "Successfully Paired.";
                trackingDisplay.setText(code);
                trackingDisplay.setTextColor(this.getResources().getColor(R.color.green));
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }


    }
}
