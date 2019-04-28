package com.example.sphtech;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.junit.Test;

import static org.junit.Assert.*;

public class MainActivityTest extends AppCompatActivity {

    @Test
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //loginMethodDisplay();

        this.findViewById(R.id.loginMethod).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivityTest.this, DataActivity.class);
                startActivity(intent);
            }
        });
    }

    @Test
    public void onStart() {
        super.onStart();
    }
}