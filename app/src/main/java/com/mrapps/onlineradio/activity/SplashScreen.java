package com.mrapps.onlineradio.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mrapps.onlineradio.utils.FirstLaunch;
import com.mrapps.onlineradio.databinding.ActivitySplashScreenBinding;

public class SplashScreen extends AppCompatActivity {

    ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new Thread(() -> {
            for (int i = 0; i <= 100; i++) {
                try {
                    Thread.sleep(20);
                    binding.progressBar.setProgress(i);


                    if (i == 100) {

                        if (new FirstLaunch(SplashScreen.this).isFirstTimeLaunch()) {
                            startActivity(new Intent(SplashScreen.this, Intro.class));
                            finish();
                        } else {
                            startActivity(new Intent(SplashScreen.this, MainActivity.class));
                            finish();
                        }

                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }).start();
    }
}