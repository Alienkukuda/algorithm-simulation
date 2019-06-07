package com.example.demo.AlgBranchAndBound.entity;

public class BranchBoundLoadTextBox {
    public int textBoxCurrentFloor;
    public int textBoxCurrentLoad;
    public int textBoxCurrentBestLoad;
    public int textBoxRemainingContainer;

    public BranchBoundLoadTextBox(){}

    public BranchBoundLoadTextBox(int textBoxCurrentFloor,int textBoxCurrentLoad,int textBoxCurrentBestLoad,int textBoxRemainingContainer){
        this.textBoxCurrentFloor = textBoxCurrentFloor;
        this.textBoxCurrentLoad = textBoxCurrentLoad;
        this.textBoxCurrentBestLoad = textBoxCurrentBestLoad;
        this.textBoxRemainingContainer = textBoxRemainingContainer;
    }
}
