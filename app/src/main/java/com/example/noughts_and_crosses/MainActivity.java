package com.example.noughts_and_crosses;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public MediaPlayer music_player;

    public String data_name = "noughts_and_crosses_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //скрыть строку состояния телефона
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //музыка
        music_player = MediaPlayer.create(this, R.raw.music);
        music_player.setLooping(true);

        //чтение уровней громкости
        String buf_str = getString(R.string.music_volume);
        buf_str = loadParams(buf_str);
        if(!buf_str.isEmpty()) {
            float volume_sound = getVolume(Integer.parseInt(buf_str));
            music_player.setVolume(volume_sound, volume_sound);
            music_player.start();
        }
    }

    public void slotStart(View view) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra(getString(R.string.cmd_use_bot), false);
        startActivity(intent);
        music_player.stop();
        finish();
    }

    public void slotOptions(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        music_player.stop();
        finish();
    }

    public void slotExit(View view) {
        music_player.stop();
        finish();
    }

    public String loadParams(String key) {
        SharedPreferences sPref;
        sPref = getSharedPreferences(data_name, Context.MODE_PRIVATE);
        String savedText = sPref.getString(key, "70");
        return savedText;
    }

    private float getVolume(int in_progress)
    {
        float volume = (float) (1 - (Math.log(100 - in_progress) / Math.log(100)));
        return volume;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void slotStartBot(View view) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra(getString(R.string.cmd_use_bot), true);
        startActivity(intent);
        music_player.stop();
        finish();
    }
}
