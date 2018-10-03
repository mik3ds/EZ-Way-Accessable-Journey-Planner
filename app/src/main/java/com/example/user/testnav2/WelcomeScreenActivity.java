package com.example.user.testnav2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomeScreenActivity extends AppCompatActivity {
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();
        mEditor.putBoolean("firstTimeRun", true);
        mEditor.apply();
//a
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mapIntent = new Intent(WelcomeScreenActivity.this, MapActivity.class);
                startActivity(mapIntent);
                finish();
            }
        },1000);
    }
}
