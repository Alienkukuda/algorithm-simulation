package com.example.demo.AlgRecurAndDivCon.entity;


import android.graphics.Point;

/**
 * @Author captain
 * @Description 下划线显示帮助器
 */
public class UnderLineHelper {
    //起点位置
    public Point StartLocation;
    //终点位置
    public Point EndLocation;

    public UnderLineHelper(Point StartLocation, Point EndLocation)
    {
        this.StartLocation = StartLocation;
        this.EndLocation = EndLocation;
    }
}
