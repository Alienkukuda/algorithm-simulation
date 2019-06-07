package com.example.demo.AlgBranchAndBound.entity;

//优先队列结点类
public class HeapNode implements Comparable<HeapNode>{
    //结点名称
    public String name = "";
    //结点的价值上限
    public int uprofit;
    //结点所相应的价值
    public int profit;
    //结点所相应的重量
    public int weight;
    //活结点在子集树中所处的层序号
    public int level;
    //指向活结点在子集树中相应结点的指针
    public bbnode ptr;

    public HeapNode()
    {
    }

    public HeapNode(int uprofit, int profit, int weight, int level, bbnode ptr)
    {
        this.uprofit = uprofit;
        this.profit = profit;
        this.weight = weight;
        this.level = level;
        this.ptr = ptr;
    }

    public int compareTo(HeapNode anotherHeapNode) {
        return compare(uprofit, anotherHeapNode.uprofit);
    }
    public static int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }
}
