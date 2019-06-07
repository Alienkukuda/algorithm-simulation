package com.example.demo.AlgRecurAndDivCon.entity;

import android.graphics.Point;

/**
 * @Author captain
 * @Description 箭头类
 */
public class ArrowHead {
    //箭头坐标坐标
    public Point arrowHeadLocation;
    //是否显示箭头
    public boolean isDisplay = false;

    public ArrowHead(Point arrowHeadLocation)
    {
        this.arrowHeadLocation = arrowHeadLocation;
    }
}
