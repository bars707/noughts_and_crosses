package com.example.noughts_and_crosses;

import android.util.Log;

import java.util.Random;

public class BotMedium implements Bot {

    final private String APP_LOG = "app_log";

    private int my_figure;
    private int enemy_figure;
    private int figure_cross;
    private int figure_nought;
    private int figure_none;
    private int[][] map = null;
    private int num_cols;
    private int num_rows;
    private int current_step;

    @Override
    public void reset() {
        my_figure = 0;
        enemy_figure = 0;
        figure_cross = 0;
        figure_nought = 0;
        figure_none = 0;
        map = null;
        current_step = 0;
        num_cols = 0;
        num_rows = 0;
    }

    @Override
    public void setCross(int id_figure) {
        figure_cross = id_figure;
    }

    @Override
    public void setNought(int id_figure) {
        figure_nought = id_figure;
    }

    @Override
    public void setNone(int id_figure) {
        figure_none = id_figure;
    }

    @Override
    public void setMyFigure(int id_figure) {
        my_figure = id_figure;
    }

    @Override
    public void setEnemyFigure(int id_figure) { enemy_figure = id_figure; }

    @Override
    public void setMap(int[][] map, int num_cols, int num_rows) {
        this.map = map;
        this.num_cols = num_cols;
        this.num_rows = num_rows;
    }

    @Override
    public int[] getBotStep(int num_step) {
        current_step = num_step;

        int[] out_step = null;

        //поиск победных позиций для бота
        out_step = searchWinPos(my_figure);
        if(out_step == null)
        {
            //поиск победных позиций для противника
            out_step = searchWinPos(enemy_figure);
            if(out_step == null)
            {
                int step_col = 0;
                int step_row = 0;
                Random random = new Random();
                do {
                    step_col = random.nextInt(num_cols);
                    step_row = random.nextInt(num_rows);
                }while (!ifFreeItem(step_col, step_row));
                out_step = new int[2];
                out_step[0] = step_col;
                out_step[1] = step_row;
            }
        }

        Log.d(APP_LOG, "class BotLow: I am step: " + out_step[0] + " | " + out_step[1]);

        return out_step;
    }

    @Override
    public int getFigure() {
        return my_figure;
    }

    private boolean ifFreeItem(int col, int row)
    {
        if(map == null)
        {
            Log.e(APP_LOG, "class BotMedium: map == null");
            return false;
        }

        if(map[col][row] == figure_none)
        {
            return true;
        }
        return false;
    }

    private int[] searchWinPos(int id_figure)
    {
        if(map == null)
            return null;

        int[] potential_step_pos = new int[2];

        //поиск горизонтальной позиции
        for(int cc=0; cc < num_cols; cc++)
        {
            int hor_sum = 0; //количество горизонтальных фигуры
            int potential_pos_sum = 0;
            for(int rr=0; rr < num_rows; rr++)
            {
                if(map[cc][rr] == id_figure)
                {
                    hor_sum++;
                }
                else if (map[cc][rr] == figure_none)
                {
                    potential_pos_sum++;
                    potential_step_pos[0] = cc;
                    potential_step_pos[1] = rr;
                }
            }

            if (hor_sum == 2 && potential_pos_sum == 1)
            {
                return potential_step_pos;
            }
        }

        //поиск вертикальной позиции
        for(int rr=0; rr < num_rows; rr++)
        {
            int ver_sum = 0; //количество вертикальных фигуры
            int potential_pos_sum = 0;
            for(int cc=0; cc < num_cols; cc++)
            {
                if(map[cc][rr] == id_figure)
                {
                    ver_sum++;
                }
                else if (map[cc][rr] == figure_none)
                {
                    potential_pos_sum++;
                    potential_step_pos[0] = cc;
                    potential_step_pos[1] = rr;
                }
            }

            if (ver_sum == 2 && potential_pos_sum == 1)
            {
                return potential_step_pos;
            }
        }

        int diag_sum = 0; //количество вертикальных фигуры
        int potential_pos_sum = 0;
        //поиск диоганальной позиции позиции
        for(int ii=0; ii < num_rows; ii++)
        {
            if(map[ii][ii] == id_figure)
            {
                diag_sum++;
            }
            else if (map[ii][ii] == figure_none)
            {
                potential_pos_sum++;
                potential_step_pos[0] = ii;
                potential_step_pos[1] = ii;
            }
        }
        if (diag_sum == 2 && potential_pos_sum == 1)
        {
            return potential_step_pos;
        }

        //поиск диоганальной обратной позиции позиции
        int diag2_sum = 0; //количество вертикальных фигуры
        int potential2_pos_sum = 0;
        for(int ii=0; ii < num_rows; ii++)
        {
            if(map[(ii-2)*(-1)][ii] == id_figure)
            {
                diag2_sum++;
            }
            else if (map[(ii-2)*(-1)][ii] == figure_none)
            {
                potential2_pos_sum++;
                potential_step_pos[0] = (ii-2)*(-1);
                potential_step_pos[1] = ii;
            }
        }
        if (diag2_sum == 2 && potential2_pos_sum == 1)
        {
            return potential_step_pos;
        }
        return null;
    }
}
