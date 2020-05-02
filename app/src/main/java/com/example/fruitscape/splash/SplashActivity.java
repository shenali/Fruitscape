package com.example.fruitscape.splash;

import android.os.Bundle;

import com.example.fruitscape.main.MainActivity;
import com.example.fruitscape.R;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SplashTheme);
        startActivity(MainActivity.getIntent(this));
        finish();
    }
}
