package com.example.sphtech;

import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.example.sphtech.util.BiometricUtils;
import com.example.sphtech.util.HttpPostObject;
import com.example.sphtech.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //loginMethodDisplay();

        this.findViewById(R.id.loginMethod).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, DataActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart (){
        super.onStart();

    }

}
