package com.example.demo.AlgDynamic.entity;

import android.graphics.Color;

import java.util.Random;

public class ThreePoint {

    public int index1;
    public int index2;
    public int index3;
    public int color;
    public ThreePoint(int index1, int index2, int index3)
    {
        this.index1 = index1;
        this.index2 = index2;
        this.index3 = index3;
        Random random= new Random();
        this.color = Color.rgb(random.nextInt(255),random.nextInt(255),random.nextInt(255));
    }
}
