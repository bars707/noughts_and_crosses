package com.example.noughts_and_crosses;

import android.widget.ImageView;

public interface Bot{
    void reset();                                          //забыть всё
    //передача параметров перед началом игры
    void setCross(int id_figure);                          //передача id крестика
    void setNought(int id_figure);                         //передача id нолика
    void setNone(int id_figure);                           //передача id нолика
    void setMyFigure(int id_figure);                       //установка фигуры бота
    void setMap(int[][] map, int num_cols, int num_rows);  //передача карты игры

    //передача параметров во время игры
    int[] getBotStep(int num_step);                         //передача номера хода и ожидание результата

    //возврат данных бота
    int getFigure();

}
