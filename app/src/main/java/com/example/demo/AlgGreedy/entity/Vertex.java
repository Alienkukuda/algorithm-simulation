package com.example.demo.AlgGreedy.entity;

import android.graphics.Color;
import android.graphics.Point;

public class Vertex {
    // 顶点编号
    public int Index;
    // 顶点中心坐标
    public Point Location;
    // 顶点圆圈半径大小
    public static int Radius = 35;

    public Vertex(int index, Point Location)
    {
        this.Index = index;
        this.Location = Location;
    }
}
