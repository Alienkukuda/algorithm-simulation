package com.example.demo.AlgBranchAndBound.entity;

public class MinHeapNode implements Comparable<MinHeapNode>{
    //顶点编号
    public int i = -1;
    //当前路长
    public int length = -1;

    public MinHeapNode(int i, int length)
    {
        this.i = i;
        this.length = length;
    }

    public int compareTo(MinHeapNode anotherMinHeapNode) {
        return compare(length, anotherMinHeapNode.length);
    }
    public static int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }
}
