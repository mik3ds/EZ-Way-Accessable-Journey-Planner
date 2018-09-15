package com.example.user.testnav2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;

    ProgressDialog nDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button navbutton1 = (Button) findViewById(R.id.navbutton1);
        Button navbutton2 = (Button) findViewById(R.id.navbutton2);
        Button navbutton3 = (Button) findViewById(R.id.navbutton3);
        Button navbutton4 = (Button) findViewById(R.id.buttonSlidingTest);



        navbutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Main1Activity.class));
            }
        });
        navbutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
            }
        });
        navbutton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nDialog.show();
                startActivity(new Intent(MainActivity.this, MapActivity.class));
            }
        });
        navbutton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TrackMapActivity.class));
            }
        });
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(mPreferences.getBoolean("isParent", false) == false){
            navbutton3.setVisibility(Button.VISIBLE);
            navbutton4.setVisibility(Button.GONE);
        }else{
            navbutton3.setVisibility(Button.INVISIBLE);
            navbutton4.setVisibility(Button.VISIBLE);
        }


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

//        configureNavButton1();
//        configureNavButton2();
//        configureNavButton3();
//        configureNavButton4();
        configureUserName();
        configureWeatherTextDisplay();



        changeBackground();

        nDialog = new ProgressDialog(MainActivity.this);
        nDialog.setMessage("Loading..");
        nDialog.setTitle("Getting Data");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(true);


    }

    @Override
    protected void onStop() {
        nDialog.hide();
        super.onStop();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.relativeLayoutMain);
        AnimationDrawable ad = (AnimationDrawable) layout.getBackground();
        ad.start();
    }

//    private void configureNavButton1() {
//        Button navbutton1 = (Button) findViewById(R.id.navbutton1);
//        navbutton1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, Main1Activity.class));
//            }
//        });
//    }
//
//    private void configureNavButton2() {
//        Button navbutton2 = (Button) findViewById(R.id.navbutton2);
//        navbutton2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, Main2Activity.class));
//            }
//        });
//    }

//    private void configureNavButton3() {
//        Button navbutton3 = (Button) findViewById(R.id.navbutton3);
//        navbutton3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                nDialog.show();
//                startActivity(new Intent(MainActivity.this, MapActivity.class));
//            }
//        });
//    }
//
//    private void configureNavButton4() {
//        Button navbutton4 = (Button) findViewById(R.id.buttonSlidingTest);
//        navbutton4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, TrackMapActivity.class));
//            }
//        });
//    }

    private void configureUserName() {
        TextView welcome = (TextView) findViewById(R.id.welcometext);
        String displayName = mPreferences.getString(getString(R.string.username), "Guest");
        String temp = "Welcome, " + displayName + "!";
        welcome.setText(temp);
    }

    private void configureWeatherBackground(JSONObject obj) {
        int weatherCode = 0;
        try {
            weatherCode = obj.getJSONArray("weather").getJSONObject(0).getInt("id");
        } catch (Throwable t) {
            Log.e("FIT5120", "Could not load JSON");
        }

        changeBackground();
    }

    private void changeBackground() {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.relativeLayoutMain);
        layout.setBackgroundResource(R.drawable.animation_list);

        AnimationDrawable ad = (AnimationDrawable) layout.getBackground();
        ad.setEnterFadeDuration(5000);
        ad.setExitFadeDuration(2000);
    }

    private void configureWeatherTextDisplay() {
        TextView weatherdisplay = (TextView) findViewById(R.id.homeWeatherText);

        weatherGetter wg = new weatherGetter();
        String weather = wg.doInBackground();
        JSONObject obj = null;
        JSONObject obj2 = null;
        try {
            obj = new JSONObject(weather);
            obj2 = obj;
            obj = obj.getJSONObject("main");
            String temp = obj.getString("temp");
            String temp2 = "It is currently " + temp + " degrees in Melbourne";
            weatherdisplay.setText(temp2);

        } catch (Throwable t) {
            Log.e("FIT5120", "Could not load JSON");
        }
        configureWeatherBackground(obj2);
    }


}
