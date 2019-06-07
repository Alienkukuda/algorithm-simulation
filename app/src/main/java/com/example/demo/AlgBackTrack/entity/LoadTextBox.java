package com.example.demo.AlgBackTrack.entity;

public class LoadTextBox {
    public int textBoxCurrentBestLoad;
    public int textBoxRemainingLoad;
    public int textBoxCurrentLoad;

    public LoadTextBox(){}

    public LoadTextBox(int textBoxCurrentBestLoad,int textBoxRemainingLoad,int textBoxCurrentLoad){
        this.textBoxCurrentBestLoad = textBoxCurrentBestLoad;
        this.textBoxRemainingLoad = textBoxRemainingLoad;
        this.textBoxCurrentLoad = textBoxCurrentLoad;
    }
}
