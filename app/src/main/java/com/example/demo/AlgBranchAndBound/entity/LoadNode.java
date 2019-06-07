package com.example.demo.AlgBranchAndBound.entity;

import android.graphics.Point;

public class LoadNode implements Comparable<LoadNode>{
    //点名称
    public char name;
    //扩展结点所相应的载重量
    public int Ew;
    //节点中心位置
    public Point CenterLocation;
    //外围圆圈半径大小
    public int CircleRadius = 22;//23

    public LoadNode(char name, Point CenterLocation)
    {
        this.name = name;
        this.CenterLocation = CenterLocation;
    }

    public LoadNode(char name, int Ew)
    {
        this.name = name;
        this.Ew = Ew;
    }

    public int compareTo(LoadNode anotherNode) {
        return compare(name, anotherNode.name);
    }

    public static int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public String ToString()
    {
        return String.valueOf(name);
    }
}
