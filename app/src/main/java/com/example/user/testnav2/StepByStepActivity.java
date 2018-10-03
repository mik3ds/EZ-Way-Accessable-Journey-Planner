package com.example.user.testnav2;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StepByStepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_by_step);
        try {
            setup();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setup() throws JSONException {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        JSONArray ja = new JSONArray(mPreferences.getString("instructions","[]"));
        ListView lv = findViewById(R.id.stepbystepListView);
        List<JSONObject> listJ = new ArrayList<>();
        Log.e("helpp",ja.toString());
        int i = 0;
        while (i<ja.length()) {
            listJ.add(ja.getJSONObject(i));
            i++;
        }

//        ArrayAdapter<JSONArray> arrayAdapter = new ArrayAdapter<JSONArray>(this,R.id.list_item,R.id.stepbystepTextView,ja);
//        ArrayAdapter<JSONObject> arrayAdapter = new ArrayAdapter<JSONArray> (this,R.id.list_item,R.id.stepbystepTextView,listJ);
        ArrayAdapter<JSONObject> arrayAdapter = new ArrayAdapter<>(this,R.layout.list_item,R.id.stepbystepTextView,listJ);
        lv.setAdapter(arrayAdapter);

    }
}
