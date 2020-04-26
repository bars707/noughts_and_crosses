package com.example.noughts_and_crosses;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static android.view.WindowManager.*;
import static android.view.WindowManager.LayoutParams.*;

public class SettingsActivity extends AppCompatActivity {

    public SeekBar music_seek = null;
    public SeekBar sound_seek = null;
    Spinner spinner = null;

    MediaPlayer music_player = null;
    MediaPlayer sound_player = null;

    public String data_name = "noughts_and_crosses_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        //скрыть строку состояния телефона
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        music_seek = findViewById(R.id.music_seek);
        sound_seek = findViewById(R.id.sounds_seek);

        music_player = MediaPlayer.create(this, R.raw.music);
        music_player.setLooping(true);

        sound_player = MediaPlayer.create(this, R.raw.tick);
        sound_player.setLooping(true);

        spinner = findViewById(R.id.lvl_spinner);

        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                float volume = getVolume(progress);

                switch(seekBar.getId())
                {
                    case R.id.music_seek:
//                        Log.d("SEEK_POS_MUSIC", String.valueOf(progress));
                        music_player.setVolume(volume, volume);
                        break;

                    case R.id.sounds_seek:
                        sound_player.setVolume(volume, volume);
//                        Log.d("SEEK_POS_SOUND", String.valueOf(progress));
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                switch(seekBar.getId())
                {
                    case R.id.music_seek:
//                        Log.d("SEEK_POS_MUSIC", "START");
//                        music_player.reset();
                        float volume_music = getVolume(seekBar.getProgress());
                        music_player.setVolume(volume_music, volume_music);
                        music_player.start();

                        break;

                    case R.id.sounds_seek:
//                        Log.d("SEEK_POS_SOUND", "START");
                        float volume_sound = getVolume(seekBar.getProgress());
                        music_player.setVolume(volume_sound, volume_sound);
                        sound_player.start();
                        break;
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                String buf_str;
                String buf_key;

                switch(seekBar.getId())
                {
                    case R.id.music_seek:
//                        Log.d("SEEK_POS_MUSIC", "STOP");
                        music_player.pause();

                        buf_str = String.valueOf(seekBar.getProgress());
                        buf_key = getString(R.string.music_volume);
                        saveParams(buf_key, buf_str);
                        break;

                    case R.id.sounds_seek:
//                        Log.d("SEEK_POS_SOUND", "STOP");
                        sound_player.pause();

                        buf_str = String.valueOf(seekBar.getProgress());
                        buf_key = getString(R.string.sound_volume);
                        saveParams(buf_key, buf_str);
                        break;
                }

            }
        };

        music_seek.setOnSeekBarChangeListener(seekBarChangeListener);
        sound_seek.setOnSeekBarChangeListener(seekBarChangeListener);

        //обработка выбора уровня сложности бота
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String buf_str = String.valueOf(spinner.getSelectedItemPosition());
                String buf_key = getString(R.string.lvl_bot);
                saveParams(buf_key, buf_str);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        //чтение уровней громкости
        String buf_str = getString(R.string.music_volume);
        buf_str = loadParams(buf_str);
        music_seek.setProgress(Integer.parseInt(buf_str));

        buf_str = getString(R.string.sound_volume);
        buf_str = loadParams(buf_str);
        sound_seek.setProgress(Integer.parseInt(buf_str));
    }

    private void saveParams(String key, String value) {
        SharedPreferences sPref;
        sPref = getSharedPreferences(data_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(key, value);
        ed.apply();
    }

    private String loadParams(String key) {
        SharedPreferences sPref;
        sPref = getSharedPreferences(data_name, Context.MODE_PRIVATE);
        String savedText = sPref.getString(key, "0");
        return savedText;
    }

    private float getVolume(int in_progress)
    {
        float volume = (float) (1 - (Math.log(100 - in_progress) / Math.log(100)));
        return volume;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}