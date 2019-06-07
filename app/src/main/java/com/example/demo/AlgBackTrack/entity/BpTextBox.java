package com.example.demo.AlgBackTrack.entity;

public class BpTextBox {
    public int textBoxCurrentWeight;
    public int textBoxCurrentValue;
    public int textBoxCurrentBestValue;

    public BpTextBox(){}

    public BpTextBox(int textBoxCurrentWeight,int textBoxCurrentValue,int textBoxCurrentBestValue){
        this.textBoxCurrentBestValue = textBoxCurrentBestValue;
        this.textBoxCurrentWeight = textBoxCurrentWeight;
        this.textBoxCurrentValue = textBoxCurrentValue;
    }
}
