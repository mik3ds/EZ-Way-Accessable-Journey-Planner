package com.example.user.testnav2;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Main1ActivityEdit extends AppCompatActivity {

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private EditText mName;
    private Button mSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1_edit);

        mName = (EditText) findViewById(R.id.editProfName);
        mSave = (Button) findViewById(R.id.saveprofilebutton);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();

        configureSaveButton();
        getUserData();

    }

    protected void getUserData() {
        String oldName = mPreferences.getString(getString(R.string.username), "");
        mName.setText(oldName);
    }

    protected void saveUserData() {
        String newName = mName.getText().toString();
        mEditor.putString(getString(R.string.username), newName);
        mEditor.commit();
    }

    protected void configureSaveButton() {
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserData();
                finish();
            }
        });
    }
}
