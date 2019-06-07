package com.example.demo.AlgRecurAndDivCon.entity;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author captain
 * @Description 柱子类
 */
public class Pillar {
    //柱子名字
    public String PillarName;
    //柱子顶部位置
    public Point TopLocation;
    //柱子底部位置
    public Point BottomLocation;
    //柱子上的盘子数量
    public int plateSum = 0;
    //柱子上的盘子编号列表
    public List<Integer> plateNumList = new ArrayList<Integer>();

    public Pillar(){}
    public Pillar(Point TopLocation, Point BottomLocation)
    {
        this.TopLocation = TopLocation;
        this.BottomLocation = BottomLocation;
    }

    public Pillar(String PillarName,Point TopLocation, Point BottomLocation)
    {
        this.PillarName = PillarName;
        this.TopLocation = TopLocation;
        this.BottomLocation = BottomLocation;
    }

    public String getPillarName() {
        return PillarName;
    }

    public void setPillarName(String pillarName) {
        PillarName = pillarName;
    }

    public Point getTopLocation() {
        return TopLocation;
    }

    public void setTopLocation(Point topLocation) {
        TopLocation = topLocation;
    }

    public Point getBottomLocation() {
        return BottomLocation;
    }

    public void setBottomLocation(Point bottomLocation) {
        BottomLocation = bottomLocation;
    }

    public int getPlateSum() {
        return plateSum;
    }

    public void setPlateSum(int plateSum) {
        this.plateSum = plateSum;
    }

    public List<Integer> getPlateNumList() {
        return plateNumList;
    }

    public void setPlateNumList(List<Integer> plateNumList) {
        this.plateNumList = plateNumList;
    }
}
