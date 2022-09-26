package com.mrapps.onlineradio.activity;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.mrapps.onlineradio.R;
import com.mrapps.onlineradio.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    boolean started = false;
    boolean prepared = false;
    String streamlink;
    AudioManager mAudioManager;
    int maxVolume;
    int currentVolume;
    boolean volumeon;
    NotificationManager mNotificationManager;

    ActivityMainBinding binding;
    MediaPlayer mPlayer;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);

        streamlink = "http://relay.181.fm:8054";

        mPlayer = new MediaPlayer();

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        if (currentVolume == 0) {
            binding.volumeImage.setBackgroundResource(R.drawable.ic_volume_off);
        }
        if (currentVolume <= 8 && currentVolume > 0) {
            binding.volumeImage.setBackgroundResource(R.drawable.ic_volume_down);
        }
        if (currentVolume > 8) {
            binding.volumeImage.setBackgroundResource(R.drawable.ic_volume_up);
        }
        if (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
            volumeon = false;
        }
        if (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) > 0) {
            volumeon = true;
        }

        binding.volumeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (volumeon) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                    binding.volumeImage.setBackgroundResource(R.drawable.ic_volume_off);
                    binding.indicatorseekbar.setProgress(0);
                } else {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 8, 0);
                    binding.indicatorseekbar.setProgress(8);
                    binding.volumeImage.setBackgroundResource(R.drawable.ic_volume_down);
                }


            }
        });
        binding.indicatorseekbar.setProgress(currentVolume);
        binding.indicatorseekbar.setMax(maxVolume);
        binding.indicatorseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(), 0);
                if (seekBar.getProgress() == 0) {
                    binding.volumeImage.setBackgroundResource(R.drawable.ic_volume_off);
                    volumeon = false;
                }
                if (seekBar.getProgress() <= 8 && seekBar.getProgress() > 0) {
                    binding.volumeImage.setBackgroundResource(R.drawable.ic_volume_down);
                    volumeon = true;
                }
                if (seekBar.getProgress() > 8) {
                    binding.volumeImage.setBackgroundResource(R.drawable.ic_volume_up);
                    volumeon = true;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        binding.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (started) {
                    binding.playButton.setImageResource(R.drawable.ic_play);
                    mPlayer.pause();
                    started = false;
                    binding.lottieanimation.pauseAnimation();
                    stopNotify();
                } else {

                    if (prepared) {
                        binding.playButton.setImageResource(R.drawable.ic_pause);
                        mPlayer.start();
                        started = true;
                        binding.lottieanimation.playAnimation();
                        startNotify();
                    } else {
                        new Player().execute(streamlink);
                    }
                }


            }
        });


        // get preferences from settings
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("pref_key_switch", false)) {
            autoPlay();
        }


    }

    private void autoPlay() {
        new Player().execute(streamlink);
    }


    private void stopNotify() {
        mNotificationManager.cancelAll();
    }

    private void startNotify() {
        Intent notificationIntent = new Intent(this,
                MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, default_notification_channel_id)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setContentIntent(resultIntent)
                .setSilent(true)
                .setContentTitle("The Beat")
                .setStyle(new NotificationCompat.BigTextStyle())
                .setContentText("181 FM");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(),
                mBuilder.build());


    }


    @Override
    public void onDestroy() {
        mPlayer.release();
        stopNotify();

        super.onDestroy();

    }


    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPlayer.release();
                        stopNotify();
                        finish();
                    }
                }).setNegativeButton("No", null)
                .setNeutralButton("Minimize", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        moveTaskToBack(true);
                    }
                }).show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
        }

        return super.onOptionsItemSelected(item);
    }

    class Player extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {

            try {
                mPlayer.setDataSource(strings[0]);
                mPlayer.prepare();
                prepared = true;

            } catch (Exception e) {
                Log.e("MyAudioStreamingApp", e.getMessage());
                prepared = false;
            }

            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (progressDialog.isShowing()) {
                progressDialog.cancel();
            }

            mPlayer.start();
            started = true;
            binding.playButton.setImageResource(R.drawable.ic_pause);
            binding.lottieanimation.setVisibility(View.VISIBLE);
            binding.lottieanimation.playAnimation();
            startNotify();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Streaming...");
            progressDialog.show();
        }
    }
}

