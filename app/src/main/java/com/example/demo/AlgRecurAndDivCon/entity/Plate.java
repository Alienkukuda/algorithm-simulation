package com.example.demo.AlgRecurAndDivCon.entity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Size;

/**
 * @Author captain
 * @Description Plate类
 */
public class Plate {
    //盘子编号,按从小到大顺序编号
    public int index;
    //盘子下边框的中心位置
    public Point location;
    //盘子大小
    public Size size;
    //盘子颜色
    //private Color color;

    public Plate(){}
    @SuppressLint("NewApi")
    public Plate(int index, Point p)
    {
        this.index = index;
        this.location = p;
        //0-30 1-50 2-70
        this.size = new Size(60 + index * 40, 20);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }
}
