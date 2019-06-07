package com.example.demo.AlgRecurAndDivCon.entity;

import android.graphics.Point;

/**
 * @Author captain
 * @Description 数据显示帮助器
 */
public class DataShowHelper {
    //中心位置
    public Point CenterLocation;
    //外围圆圈半径大小
    public int CircleRadius = 35;
    //是否显示圆圈
    public boolean isShowCircle = false;
    //是否显示正方形
    public boolean isShowSquare = false;
    //是否显示箭头
    public boolean isShowArrowHead = false;
    //是否显示下划线
    public boolean isShowUnderLine = false;

    public DataShowHelper(Point CenterLocation,
                          boolean isShowCircle,
                          boolean isShowSquare,
                          boolean isShowArrowHead)
    {
        this.CenterLocation = CenterLocation;
        this.isShowCircle = isShowCircle;
        this.isShowSquare = isShowSquare;
        this.isShowArrowHead = isShowArrowHead;
    }

    public DataShowHelper(Point CenterLocation,
                          boolean isShowCircle,
                          boolean isShowSquare,
                          boolean isShowArrowHead,
                          boolean isShowUnderLine)
    {
        this.CenterLocation = CenterLocation;
        this.isShowCircle = isShowCircle;
        this.isShowSquare = isShowSquare;
        this.isShowArrowHead = isShowArrowHead;
        this.isShowUnderLine = isShowUnderLine;
    }
}
