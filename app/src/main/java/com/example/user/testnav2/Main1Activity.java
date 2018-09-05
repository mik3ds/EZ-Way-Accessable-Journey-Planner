package com.example.user.testnav2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Main1Activity extends AppCompatActivity {

    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        configureHomeButton1();
        configureEditProfileButton();
        configureProfileData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    private void configureHomeButton1() {
        Button homebutton1 = (Button) findViewById(R.id.homebuttonmain1);
        homebutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void configureEditProfileButton() {
        Button main1edit = (Button) findViewById(R.id.main1edit);
        main1edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Main1Activity.this, Main1ActivityEdit.class));
            }
        });
    }

    protected void configureProfileData() {
        TextView namedisplay1 = (TextView) findViewById(R.id.nameDisplay1);
        String displayName = mPreferences.getString(getString(R.string.username), "Guest");
        namedisplay1.setText(displayName);
    }
}
