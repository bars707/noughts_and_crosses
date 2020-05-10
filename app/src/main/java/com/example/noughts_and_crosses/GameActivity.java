package com.example.noughts_and_crosses;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    final private String APP_LOG = "app_log";

    //элементы imageView
    public ImageView imageView_c0_r0 = null;
    public ImageView imageView_c1_r0 = null;
    public ImageView imageView_c2_r0 = null;
    public ImageView imageView_c0_r1 = null;
    public ImageView imageView_c1_r1 = null;
    public ImageView imageView_c2_r1 = null;
    public ImageView imageView_c0_r2 = null;
    public ImageView imageView_c1_r2 = null;
    public ImageView imageView_c2_r2 = null;

    public TextView textView_who_move = null;

    //id фигур
    final int FIGURE_IMG_NONE = R.drawable.none;        //ничего
    final int FIGURE_IMG_CROSS = R.drawable.cross;      //крестик
    final int FIGURE_IMG_NOUGHT = R.drawable.nought;    //нолик

    final int FIGURE_IMG_CROSS_WIN = R.drawable.cross_win;  //крестик
    final int FIGURE_IMG_NOUGHT_WIN = R.drawable.nought_win; //нолик

    final int RESULT_GAME_CONTINUE = 0;                 //игра продолжается
    final int RESULT_GAME_FAIL = 1;                     //ни кто не победил
    final int RESULT_GAME_WIN_CROSS = 2;                //победил крестик
    final int RESULT_GAME_WIN_NOUGHT = 3;               //победил нолик

    int current_move_figure = 0;    //фигура которая сейчас ходит
    boolean lock_game = true;       //блокировка игры, если игра закончилась
    int state_game = 0;             //статус игры
    int step_number;

    //размер поля
    final int size_col = 3;
    final int size_row = 3;
    public int[][] map = new int[size_col][size_row];     //карта поля

    //карта с элементами ImageView если кто победит покрасим их в зелёный
    public ImageView[][] map_view_images = new ImageView[size_col][size_row];

    public MediaPlayer play_cross = null;
    public MediaPlayer play_nought = null;
    public MediaPlayer play_win = null;
    public MediaPlayer play_fail = null;
    public MediaPlayer play_gong = null;
    public MediaPlayer music_player = null;

    public String data_name = "noughts_and_crosses_data";

    public boolean use_bot = false; //используется ли бот в этой сессии
    Bot bot;                        //бот

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //скрыть строку состояния телефона
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        play_cross = MediaPlayer.create(this, R.raw.cross);
        play_nought = MediaPlayer.create(this, R.raw.nought);
        play_win = MediaPlayer.create(this, R.raw.win);
        play_fail = MediaPlayer.create(this, R.raw.fail);
        play_gong = MediaPlayer.create(this, R.raw.gong);
        music_player = MediaPlayer.create(this, R.raw.music);
        music_player.setLooping(true);

        //чтение уровней громкости
        String buf_str = getString(R.string.music_volume);
        buf_str = loadParams(buf_str, "70");
        if (!buf_str.isEmpty()) {
            float volume_sound = getVolume(Integer.parseInt(buf_str));
            play_cross.setVolume(volume_sound, volume_sound);
            play_nought.setVolume(volume_sound, volume_sound);
            play_win.setVolume(volume_sound, volume_sound);
            play_fail.setVolume(volume_sound, volume_sound);
            play_gong.setVolume(volume_sound, volume_sound);
        }

        //чтение уровней громкости
        buf_str = getString(R.string.music_volume);
        buf_str = loadParams(buf_str, "70");
        if (!buf_str.isEmpty()) {
            float volume_sound = getVolume(Integer.parseInt(buf_str));
            music_player.setVolume(volume_sound, volume_sound);
            music_player.start();
        }

        play_gong.start();

        imageView_c0_r0 = findViewById(R.id.iv_c0_r0);
        imageView_c1_r0 = findViewById(R.id.iv_c1_r0);
        imageView_c2_r0 = findViewById(R.id.iv_c2_r0);
        imageView_c0_r1 = findViewById(R.id.iv_c0_r1);
        imageView_c1_r1 = findViewById(R.id.iv_c1_r1);
        imageView_c2_r1 = findViewById(R.id.iv_c2_r1);
        imageView_c0_r2 = findViewById(R.id.iv_c0_r2);
        imageView_c1_r2 = findViewById(R.id.iv_c1_r2);
        imageView_c2_r2 = findViewById(R.id.iv_c2_r2);

        textView_who_move = findViewById(R.id.tw_who_move);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (lock_game) return;

                switch (v.getId()) {
                    case R.id.iv_c0_r0:
                        workMove(0, 0, current_move_figure, imageView_c0_r0);
                        break;
                    case R.id.iv_c1_r0:
                        workMove(1, 0, current_move_figure, imageView_c1_r0);
                        break;
                    case R.id.iv_c2_r0:
                        workMove(2, 0, current_move_figure, imageView_c2_r0);
                        break;
                    case R.id.iv_c0_r1:
                        workMove(0, 1, current_move_figure, imageView_c0_r1);
                        break;
                    case R.id.iv_c1_r1:
                        workMove(1, 1, current_move_figure, imageView_c1_r1);
                        break;
                    case R.id.iv_c2_r1:
                        workMove(2, 1, current_move_figure, imageView_c2_r1);
                        break;
                    case R.id.iv_c0_r2:
                        workMove(0, 2, current_move_figure, imageView_c0_r2);
                        break;
                    case R.id.iv_c1_r2:
                        workMove(1, 2, current_move_figure, imageView_c1_r2);
                        break;
                    case R.id.iv_c2_r2:
                        workMove(2, 2, current_move_figure, imageView_c2_r2);
                        break;

                    default: {
                    }
                }
                if(use_bot) {
                    botStep();
                }
            }
        };

        imageView_c0_r0.setOnClickListener(clickListener);
        imageView_c1_r0.setOnClickListener(clickListener);
        imageView_c2_r0.setOnClickListener(clickListener);
        imageView_c0_r1.setOnClickListener(clickListener);
        imageView_c1_r1.setOnClickListener(clickListener);
        imageView_c2_r1.setOnClickListener(clickListener);
        imageView_c0_r2.setOnClickListener(clickListener);
        imageView_c1_r2.setOnClickListener(clickListener);
        imageView_c2_r2.setOnClickListener(clickListener);

        Intent intent = getIntent();
        use_bot = intent.getBooleanExtra(getString(R.string.cmd_use_bot), false);

        initForm();

        if (use_bot) {
            buf_str = getString(R.string.lvl_bot);
            buf_str = loadParams(buf_str, "0");
            if (!buf_str.isEmpty()) {
                switch (Integer.parseInt(buf_str)) {
                    case 0: bot = new BotLow(); break;
                    case 1: bot = new BotMedium(); break;
                }
                initBot(bot);
                Log.d(APP_LOG, "class GameActivity - create bot");

            }
        }
        setWhoMove(0);  //определение кто ходит первым
    }

    @Override
    public void onPause() {
        super.onPause();
        music_player.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        music_player.start();
    }

    public void slotBack(View view) {
        Intent intent = new Intent(GameActivity.this, MainActivity.class);
        startActivity(intent);
        music_player.stop();
        finish();
    }

    private void workMove(int pos_col, int pos_row, int type_figure, ImageView select_imgview) {
        //если выбранное поле не занято
        if (ifFreeItem(pos_col, pos_row)) {
            //нарисовать фигуру
            drawFigure(type_figure, select_imgview);

            //проигрывание звука рисования
            if (type_figure == FIGURE_IMG_CROSS) {
                play_cross.start();
            }
            if (type_figure == FIGURE_IMG_NOUGHT) {
                play_nought.start();
            }

            //если это привело к концу игры
            state_game = ifEndGame();

            //просмотр результата
            switch (state_game) {
                case RESULT_GAME_CONTINUE:
                    setWhoMove(current_move_figure);
//                    Toast.makeText(,"Дальше", Toast.LENGTH_SHORT).show();
                    step_number++;
                    break;
                case RESULT_GAME_FAIL:
                    Toast.makeText(getBaseContext(), R.string.final_text_fail, Toast.LENGTH_LONG).show();
                    play_fail.start();
                    //initForm();
                    break;
                case RESULT_GAME_WIN_CROSS:
                    Toast.makeText(getBaseContext(), R.string.final_text_win_cross, Toast.LENGTH_LONG).show();
                    lock_game = true;
                    play_win.start();
                    //initForm();
                    break;
                case RESULT_GAME_WIN_NOUGHT:
                    Toast.makeText(getBaseContext(), R.string.final_text_win_nought, Toast.LENGTH_LONG).show();
                    lock_game = true;
                    play_win.start();
                    //initForm();
                    break;
            }
        }
    }

    private int ifEndGame() {

        //ПОИСК ПОБЕДИТЕЛЯ ПО ВЕТИКАЛИ
        for (int cc = 0; cc < 3; cc++) {
            if (map[cc][0] == map[cc][1] && map[cc][1] == map[cc][2] && map[cc][2] != FIGURE_IMG_NONE) {
                int is_winner = map[cc][0];

                if (is_winner == FIGURE_IMG_CROSS) {
                    drawFigure(FIGURE_IMG_CROSS_WIN, map_view_images[cc][0]);
                    drawFigure(FIGURE_IMG_CROSS_WIN, map_view_images[cc][1]);
                    drawFigure(FIGURE_IMG_CROSS_WIN, map_view_images[cc][2]);
                    return RESULT_GAME_WIN_CROSS;
                }
                if (is_winner == FIGURE_IMG_NOUGHT) {
                    drawFigure(FIGURE_IMG_NOUGHT_WIN, map_view_images[cc][0]);
                    drawFigure(FIGURE_IMG_NOUGHT_WIN, map_view_images[cc][1]);
                    drawFigure(FIGURE_IMG_NOUGHT_WIN, map_view_images[cc][2]);
                    return RESULT_GAME_WIN_NOUGHT;
                }
            }
        }

        //ПОИСК ПОБЕДИТЕЛЯ ПО ГОРИЗОНТАЛИ
        for (int rr = 0; rr < 3; rr++) {
            if (map[0][rr] == map[1][rr] && map[1][rr] == map[2][rr] && map[2][rr] != FIGURE_IMG_NONE) {
                int is_winner = map[0][rr];
                if (is_winner == FIGURE_IMG_CROSS) {
                    drawFigure(FIGURE_IMG_CROSS_WIN, map_view_images[0][rr]);
                    drawFigure(FIGURE_IMG_CROSS_WIN, map_view_images[1][rr]);
                    drawFigure(FIGURE_IMG_CROSS_WIN, map_view_images[2][rr]);
                    return RESULT_GAME_WIN_CROSS;
                }
                if (is_winner == FIGURE_IMG_NOUGHT) {
                    drawFigure(FIGURE_IMG_NOUGHT_WIN, map_view_images[0][rr]);
                    drawFigure(FIGURE_IMG_NOUGHT_WIN, map_view_images[1][rr]);
                    drawFigure(FIGURE_IMG_NOUGHT_WIN, map_view_images[2][rr]);
                    return RESULT_GAME_WIN_NOUGHT;
                }
            }
        }

        //ПОИСК ПОБЕДИТЕЛЯ ПО ДИОГАНАЛИ (СЛЕВА - НАПРАВО)
        if (map[0][0] == map[1][1] && map[1][1] == map[2][2] && map[2][2] != FIGURE_IMG_NONE) {
            if (map[1][1] == FIGURE_IMG_CROSS) {
                drawFigure(FIGURE_IMG_CROSS_WIN, map_view_images[0][0]);
                drawFigure(FIGURE_IMG_CROSS_WIN, map_view_images[1][1]);
                drawFigure(FIGURE_IMG_CROSS_WIN, map_view_images[2][2]);
                return RESULT_GAME_WIN_CROSS;
            }
            if (map[1][1] == FIGURE_IMG_NOUGHT) {
                drawFigure(FIGURE_IMG_NOUGHT_WIN, map_view_images[0][0]);
                drawFigure(FIGURE_IMG_NOUGHT_WIN, map_view_images[1][1]);
                drawFigure(FIGURE_IMG_NOUGHT_WIN, map_view_images[2][2]);
                return RESULT_GAME_WIN_NOUGHT;
            }
        }

        //ПОИСК ПОБЕДИТЕЛЯ ПО ДИОГАНАЛИ (СПРАВА - НАЛЕВО)
        if (map[2][0] == map[1][1] && map[1][1] == map[0][2] && map[0][2] != FIGURE_IMG_NONE) {
            if (map[1][1] == FIGURE_IMG_CROSS) {
                drawFigure(FIGURE_IMG_CROSS_WIN, map_view_images[2][0]);
                drawFigure(FIGURE_IMG_CROSS_WIN, map_view_images[1][1]);
                drawFigure(FIGURE_IMG_CROSS_WIN, map_view_images[0][2]);
                return RESULT_GAME_WIN_CROSS;
            }
            if (map[1][1] == FIGURE_IMG_NOUGHT) {
                drawFigure(FIGURE_IMG_NOUGHT_WIN, map_view_images[2][0]);
                drawFigure(FIGURE_IMG_NOUGHT_WIN, map_view_images[1][1]);
                drawFigure(FIGURE_IMG_NOUGHT_WIN, map_view_images[0][2]);
                return RESULT_GAME_WIN_NOUGHT;
            }
        }

        //ПОИСК СВОБОДНОЙ КЛЕТКИ
        boolean have_free_pos = false;
        for (int cc = 0; cc < 3; cc++) {
            for (int rr = 0; rr < 3; rr++) {
                if (map[cc][rr] == FIGURE_IMG_NONE) {
                    have_free_pos = true;
                    break;
                }
            }
        }

        //если есть пустые клетки
        if (have_free_pos) {
            return RESULT_GAME_CONTINUE;
        } else //если нет пустых клеток
        {
            return RESULT_GAME_FAIL;
        }
    }

    private boolean ifFreeItem(int pos_col, int pos_row) {
        if (map[pos_col][pos_row] == FIGURE_IMG_NONE) {
            map[pos_col][pos_row] = current_move_figure;
            return true;
        }
        return false;
    }

    private void initForm() {
        int id_figure = FIGURE_IMG_NONE;

        drawFigure(id_figure, imageView_c0_r0);
        drawFigure(id_figure, imageView_c1_r0);
        drawFigure(id_figure, imageView_c2_r0);
        drawFigure(id_figure, imageView_c0_r1);
        drawFigure(id_figure, imageView_c1_r1);
        drawFigure(id_figure, imageView_c2_r1);
        drawFigure(id_figure, imageView_c0_r2);
        drawFigure(id_figure, imageView_c1_r2);
        drawFigure(id_figure, imageView_c2_r2);

        //очистка виртуального поля игры
        for (int cc = 0; cc < size_col; cc++) {
            for (int rr = 0; rr < size_row; rr++) {
                map[cc][rr] = FIGURE_IMG_NONE;
            }
        }

        //заполняю массив, чтобы потом подкрасить победителя
        map_view_images[0][0] = imageView_c0_r0;
        map_view_images[1][0] = imageView_c1_r0;
        map_view_images[2][0] = imageView_c2_r0;
        map_view_images[0][1] = imageView_c0_r1;
        map_view_images[1][1] = imageView_c1_r1;
        map_view_images[2][1] = imageView_c2_r1;
        map_view_images[0][2] = imageView_c0_r2;
        map_view_images[1][2] = imageView_c1_r2;
        map_view_images[2][2] = imageView_c2_r2;

        step_number = 0;

        lock_game = false;
    }

    private void drawFigure(int type_figure, ImageView imageView) {
        if (imageView == null)
            return;
        imageView.setImageResource(type_figure);
    }

    public void slotAnew(View view) {
        play_gong.start();

        initForm();
        if (use_bot) {
            initBot(bot);
        }
        setWhoMove(0);  //определение кто ходит первым
    }

    private void setWhoMove(int who_last_move) {
        if (who_last_move == 0) {
            current_move_figure = FIGURE_IMG_CROSS;     //крестик
        }
        else {
            //замена ходящего
            if (current_move_figure == FIGURE_IMG_NOUGHT) {
                current_move_figure = FIGURE_IMG_CROSS;
            } else {
                current_move_figure = FIGURE_IMG_NOUGHT;
            }
        }

        StringBuilder text_step = new StringBuilder();

        //установка текста подсказки кто ходит
        if (current_move_figure == FIGURE_IMG_NOUGHT) {
            text_step.append(getString(R.string.game_nought));
        } else {
            text_step.append(getString(R.string.game_cross));
        }

        if(use_bot) {
            text_step.insert(0," [");
            if (current_move_figure == bot.getFigure()) {
                text_step.insert(0, getString(R.string.name_bot));
            }
            else {
                text_step.insert(0, getString(R.string.name_user));
            }
            text_step.append("]");
        }
        text_step.insert(0, getString(R.string.game_step) + " ");
        textView_who_move.setText(text_step);
    }

    public String loadParams(String key, String default_val) {
        SharedPreferences sPref;
        sPref = getSharedPreferences(data_name, Context.MODE_PRIVATE);
        String savedText = sPref.getString(key, default_val);
        return savedText;
    }

    private float getVolume(int in_progress) {
        float volume = (float) (1 - (Math.log(100 - in_progress) / Math.log(100)));
        return volume;
    }

    @Override
    public void onBackPressed() {
        slotBack(null);
    }

    public void initBot(Bot in_bot) {
        in_bot.reset();

        Random Who_step_first = new Random();

        bot.setCross(FIGURE_IMG_CROSS);
        bot.setNought(FIGURE_IMG_NOUGHT);
        bot.setNone(FIGURE_IMG_NONE);
        bot.setMap(map, 3, 3);

        //если == 1 ходит первым бот
        if (Who_step_first.nextInt(2) == 1) {
            bot.setMyFigure(FIGURE_IMG_CROSS);  //бот крестик
            bot.setEnemyFigure(FIGURE_IMG_NOUGHT);
            Log.d(APP_LOG, "class GameActivity - бот ходит первым");
            botStep();

        } else {
            bot.setMyFigure(FIGURE_IMG_NOUGHT);  //бот нолик
            bot.setEnemyFigure(FIGURE_IMG_CROSS);
            Log.d(APP_LOG, "class GameActivity - человек ходит первым");
        }
    }

    public void botStep()
    {
        new AsyncThinkBot().execute();
    }

    class AsyncThinkBot extends AsyncTask<Void, Void, int[]> {
        @Override
        protected int[] doInBackground(Void... voids) {
            //если ходит не бот или игра заблокирована
            if(current_move_figure != bot.getFigure() || lock_game) {
                Log.d(APP_LOG, "class GameActivity - lock_game: true");
                return null;
            }
            return bot.getBotStep(step_number);
        }

        protected void onPostExecute(int[] pos_step)
        {
            if(pos_step == null)
            {
                Log.d(APP_LOG, "class GameActivity - бот не смог найти куда идти");
                return;
            }

            Log.d(APP_LOG, "class GameActivity - бот начинает ходить");
            lock_game = true;
            workMove(pos_step[0], pos_step[1], bot.getFigure(), map_view_images[pos_step[0]][pos_step[1]]);
            if (state_game == RESULT_GAME_CONTINUE) {
                lock_game = false;
            }
            Log.d(APP_LOG, "class GameActivity - ход бота закончен");
        }
    }

}
