package com.example.noughts_and_crosses;

import android.util.Log;

public class BotMedium implements Bot {

    final private String APP_LOG = "app_log";

    private int my_figure;
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
    public void setMap(int[][] map, int num_cols, int num_rows) {
        this.map = map;
        this.num_cols = num_cols;
        this.num_rows = num_rows;
    }

    @Override
    public int[] getBotStep(int num_step) {
        return new int[0];
    }

    @Override
    public int getFigure() {
        return my_figure;
    }

    private boolean ifFreeItem(int col, int row)
    {
        if(map == null)
        {
            Log.e(APP_LOG, "class BotLow: map == null");
            return false;
        }

        if(map[col][row] == figure_none)
        {
            return true;
        }
        return false;
    }
}
