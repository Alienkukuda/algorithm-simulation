package com.example.demo.AlgGreedy.entity;

import android.graphics.Point;

public class Node implements Comparable<Node> {
    public boolean isLeaf = false;
    public int data;
    public CodeChar codeChar;
    public Point CenterLocation;
    public int CircleRadius = 22;//23

    public Node(int data, Point CenterLocation)
    {
        this.data = data;
        this.CenterLocation = CenterLocation;
    }

    public Node(int data)
    {
        this.data = data;
    }

    public Node(int data, boolean isLeaf)
    {
        this.data = data;
        this.isLeaf = isLeaf;
    }

    public Node(int data, boolean isLeaf, CodeChar codeChar)
    {
        this.data = data;
        this.isLeaf = isLeaf;
        this.codeChar = codeChar;
    }


    public int compareTo(Node anotherNode) {
        return compare(data, anotherNode.data);
    }

    public static int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }
}
