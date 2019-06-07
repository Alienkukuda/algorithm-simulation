package com.example.demo.AlgBranchAndBound.entity;

public class BranchBoundBpTextBox {
    public int textBoxCurrentWeight;
    public int textBoxCurrentValue;
    public int textBoxCurrentBestValue;
    public int textBoxCurrentNodeLevel;

    public BranchBoundBpTextBox(){}

    public BranchBoundBpTextBox(int textBoxCurrentWeight,int textBoxCurrentValue,int textBoxCurrentBestValue,int textBoxCurrentNodeLevel){
        this.textBoxCurrentWeight = textBoxCurrentWeight;
        this.textBoxCurrentValue = textBoxCurrentValue;
        this.textBoxCurrentBestValue = textBoxCurrentBestValue;
        this.textBoxCurrentNodeLevel = textBoxCurrentNodeLevel;
    }
}
