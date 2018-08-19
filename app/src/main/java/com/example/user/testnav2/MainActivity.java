package com.example.user.testnav2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureNavButton1();
        configureNavButtton2();
    }

    private void configureNavButton1() {
        Button navbutton1 = (Button) findViewById(R.id.navbutton1);
        navbutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Main1Activity.class));
            }
        });
    }

    private void configureNavButtton2() {
        Button navbutton2 = (Button) findViewById(R.id.navbutton2);
        navbutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
            }
        });
    }

}
