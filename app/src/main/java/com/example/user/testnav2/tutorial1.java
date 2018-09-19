package com.example.user.testnav2;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class tutorial1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mapIntent = new Intent(tutorial1.this, MapActivity.class);
                startActivity(mapIntent);
                finish();
            }
        },5000);
    }
}
