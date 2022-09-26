package com.mrapps.onlineradio.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.mrapps.onlineradio.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SwitchPreference autoPlay = findPreference("pref_key_switch");
        Preference share = findPreference("pref_key_share");
        Preference privacy = findPreference("pref_key_privacy");


        if (share != null) {
            share.setOnPreferenceClickListener(preference -> {
                shareApp();
                return true;
            });
        }

        if (privacy != null) {
            privacy.setOnPreferenceClickListener(preference -> {
                privacyPolicy();
                return true;
            });
        }


    }

    private void privacyPolicy() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "https://google.com");
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    private void shareApp() {
        // Send Message Intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Download and Enjoy Online Radio");
        intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.mrapps.onlineradio");
        startActivity(Intent.createChooser(intent, "Share App"));

    }
}
