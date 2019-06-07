package com.example.demo.AlgRecurAndDivCon.entity;


import android.graphics.Point;

/**
 * @Author captain
 * @Description 元素类
 */
public class Element {
    //圈内数字
    public int num;
    //外围圆圈半径大小
    public int circleRadius = 35;
    //是否画竖线
    public boolean isDrawingVLine = false;
    //中心位置坐标
    public Point centerLocation;

    public Element(int num,boolean isDrawingVLine,Point centerLocation)
    {
        this.num = num;
        this.isDrawingVLine = isDrawingVLine;
        this.centerLocation = centerLocation;
    }
}
