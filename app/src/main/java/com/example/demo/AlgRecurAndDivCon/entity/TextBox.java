package com.example.demo.AlgRecurAndDivCon.entity;

/**
 * @Author captain
 * @Description hanoi中textbox类
 */
public class TextBox {
    private String sourceTower;
    private String destTower;
    private String midTower;
    private int currentN;

    public TextBox(){

    }

    public int getCurrentN() {
        return currentN;
    }

    public void setCurrentN(int currentN) {
        this.currentN = currentN;
    }

    public String getSourceTower() {
        return sourceTower;
    }

    public void setSourceTower(String sourceTower) {
        this.sourceTower = sourceTower;
    }

    public String getDestTower() {
        return destTower;
    }

    public void setDestTower(String destTower) {
        this.destTower = destTower;
    }

    public String getMidTower() {
        return midTower;
    }

    public void setMidTower(String midTower) {
        this.midTower = midTower;
    }
}
