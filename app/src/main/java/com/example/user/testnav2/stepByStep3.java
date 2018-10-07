package com.example.user.testnav2;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mark on 10/7/2018.
 */

public class stepByStep3 extends Activity{
    private List<StepInfo> infolist = new ArrayList<StepInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        stepAdapter adapter = new stepAdapter(stepByStep3.this, R.layout.stepinfo, infolist);
        try {
            initstepinfo();
        } catch (JSONException e) {
            Log.e("initstepinfo","failed");
            e.printStackTrace();
        }
        ListView listview = (ListView) findViewById(R.id.list_view);
        listview.setAdapter(adapter);
    }

    private void initstepinfo() throws JSONException {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        JSONArray ja = new JSONArray(mPreferences.getString("instructions","[]"));
        int i = 0;
        StepInfo tempStep;
        while (i<ja.length()) {
            if (ja.getJSONObject(i).getString("travel_mode").equals("WALKING")) {
                tempStep = new StepInfo(ja.getJSONObject(i).getString("html_instructions"), R.drawable.round_icon_wheelchair);

            } else if (ja.getJSONObject(i).getString("travel_mode").equals("TRANSIT")) {
                String s = ja.getJSONObject(i).getString("html_instructions").substring(0,5);
                if (s.contains("Train")) {
                    tempStep = new StepInfo(ja.getJSONObject(i).getString("html_instructions"), R.drawable.round_icon_train);

                } else if (s.contains("Tram")) {
                    tempStep = new StepInfo(ja.getJSONObject(i).getString("html_instructions"), R.drawable.round_icon_tram);

                } else if (s.contains("Bus")) {
                    tempStep = new StepInfo(ja.getJSONObject(i).getString("html_instructions"), R.drawable.round_icon_bus);

                } else {
                    tempStep = new StepInfo(ja.getJSONObject(i).getString("html_instructions"), R.drawable.ic_arrow_back_white);
                }
            } else {
                tempStep = new StepInfo(ja.getJSONObject(i).getString("html_instructions"), R.drawable.ic_arrow_back_white);
            }
            i++;
            infolist.add(tempStep);
        }
    }
}
