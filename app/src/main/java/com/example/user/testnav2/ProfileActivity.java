package com.example.user.testnav2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

public class ProfileActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        configureHomeButton1();
        configureEditProfileButton();
        configureProfileData();
        configureProfilePicture();
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    private void configureHomeButton1() {
        Button homebutton1 = (Button) findViewById(R.id.profileHomeButton);
        homebutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void configureEditProfileButton() {
        Button main1edit = (Button) findViewById(R.id.profileEditButton);
        main1edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, ProfileEditActivity.class));
            }
        });
    }


    protected void configureProfileData() {
        TextView namedisplay1 = (TextView) findViewById(R.id.profileNameDisplay);
        String displayName = mPreferences.getString(getString(R.string.username), "Guest");
        namedisplay1.setText(displayName);
    }

    private void configureProfilePicture() {
        ImageView profileDisplay = (ImageView) findViewById(R.id.profilePicture);
        String gender = mPreferences.getString("gender","f");
        if (gender.equals("m")) {
            profileDisplay.setImageResource(getResources().getIdentifier("boy",null,null));
        }
    }
}