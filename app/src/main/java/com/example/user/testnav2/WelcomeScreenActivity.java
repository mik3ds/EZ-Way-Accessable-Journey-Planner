package com.example.user.testnav2;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
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
