package com.example.demo.AlgBranchAndBound.entity;

import android.graphics.Point;

public class BpNode implements Comparable<BpNode>{
    //点名称
    public char name;
    //结点价值上限
    public int up = 0;
    //结点对应的当前背包价值
    public int cp = 0;
    //结点对应的当前背包重量
    public int cw = 0;
    //结点中心位置
    public Point CenterLocation;
    //外围圆圈半径大小
    public int CircleRadius = 22;//23
    //是否显示数据
    public boolean isShowData = false;

    public BpNode() { }

    public BpNode(char name)
    {
        this.name = name;
    }

    public BpNode(char name, Point CenterLocation)
    {
        this.name = name;
        this.CenterLocation = CenterLocation;
    }

    public BpNode(char name, int up)
    {
        this.name = name;
        this.up = up;
    }

    public int compareTo(BpNode anotherNode) {
        return compare(name, anotherNode.name);
    }

    public static int compare(int x, int y) {
        return (x > y) ? -1 : ((x == y) ? 0 : 1);
    }

    public String ToString() {
        return String.valueOf(name);
    }
}
