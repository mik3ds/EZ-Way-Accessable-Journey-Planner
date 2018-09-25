package com.example.user.testnav2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
/**
 * Created by mark on 9/25/2018.
 */

public class Tutorial1 extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial1);

        Button back = (Button)findViewById(R.id.button1);
        Button next = (Button)findViewById(R.id.button2);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Tutorial1.this, Tutorial2.class));
            }
        });
    }
}
