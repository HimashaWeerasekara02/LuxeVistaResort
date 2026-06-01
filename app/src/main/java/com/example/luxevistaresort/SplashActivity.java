package com.example.luxevistaresort;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    // Duration of the splash screen in milliseconds
    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Use a Handler to delay the start of the next activity
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // This is the line to change.
                // We are now starting HomeActivity instead of LoginActivity.
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                // Finish this activity so the user can't navigate back to it
                finish();
            }
        }, SPLASH_DELAY);
    }
}

