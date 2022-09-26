package com.mrapps.onlineradio.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.mrapps.onlineradio.R;
import com.mrapps.onlineradio.fragment.SettingsFragment;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager().beginTransaction().replace(R.id.settings_fragment_container, new SettingsFragment()).commit();

    }
}