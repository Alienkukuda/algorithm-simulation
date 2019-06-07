package com.example.demo.AlgDynamic.entity;

public class TableString {
    private String[][] mTableStr;


    private String[][] sTableStr;

    public TableString(){

    }

    public TableString(String[][] mTableStr, String[][] sTableStr) {
        this.mTableStr = mTableStr;
        this.sTableStr = sTableStr;
    }
    public String[][] getmTableStr() {
        return mTableStr;
    }

    public void setmTableStr(String[][] mTableStr) {
        this.mTableStr = mTableStr;
    }

    public String[][] getsTableStr() {
        return sTableStr;
    }

    public void setsTableStr(String[][] sTableStr) {
        this.sTableStr = sTableStr;
    }


}
