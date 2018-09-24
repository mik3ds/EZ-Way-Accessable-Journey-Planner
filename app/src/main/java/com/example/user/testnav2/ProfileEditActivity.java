package com.example.user.testnav2;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ProfileEditActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private String trackStatus;

    private EditText mName;
    private Button mSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        mName = (EditText) findViewById(R.id.editProfileNameEntry);
        mSave = (Button) findViewById(R.id.editprofileSaveButton);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        getUserData();
        configureSaveButton();
        configureToggle();
    }



    //Get user data method
    protected void getUserData() {
        String oldName = mPreferences.getString(getString(R.string.username), "");
        mName.setText(oldName);
    }

    //Set user data method
    protected void saveUserData() {
        String newName = mName.getText().toString();
        String gender = "f";
        RadioButton maleButton = (RadioButton) findViewById(R.id.editProfileBoyButton);
        if (maleButton.isChecked()) {
            gender = "m";
        }
        mEditor = mPreferences.edit();
        mEditor.putString(getString(R.string.username), newName);
        mEditor.putString("gender", gender);
        mEditor.apply();
    }

    //Save user data button
    protected void configureSaveButton() {
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserData();
                finish();
            }
        });
    }

    //Configure toggle button
    private void configureToggle() {
        RadioGroup rg = (RadioGroup) findViewById(R.id.editProfileGenderRadioGroup);
        rg.clearCheck();

        String gender = mPreferences.getString("gender", "f");
        if (gender.equals("m")) {
            rg.check(R.id.editProfileBoyButton);
        } else {
            rg.check(R.id.editProfileGirlButton);
        }
    }
}
