package com.example.user.testnav2;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

public class StepByStep2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_by_step2);
        try {
            setup();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setup() throws JSONException {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        JSONArray ja = new JSONArray(mPreferences.getString("instructions","[]"));

        TextView tv = findViewById(R.id.stepbystepText);


        String example = "";

        int i = 0;
        while (ja.length()>i) {
            example += ja.getJSONObject(i).getString("html_instructions");
            example += " ";
            example += ja.getJSONObject(i).getJSONObject("duration").getString("text");
            example += System.getProperty("line.separator");
            i++;
        }
        tv.setText(example);

    }
}
