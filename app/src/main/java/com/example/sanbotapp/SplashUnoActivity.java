package com.example.sanbotapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sanbotapp.R;

public class SplashUnoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashuno);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashUnoActivity.this, SplashDosActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500); // 1.5 segundos
    }
}