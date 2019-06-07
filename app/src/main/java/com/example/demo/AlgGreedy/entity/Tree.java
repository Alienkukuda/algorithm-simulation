package com.example.demo.AlgGreedy.entity;

import android.graphics.Point;

import com.example.demo.DataStruct.BST;

public class Tree {
    //权值
    public int Value;
    //二叉树
    public BST<Node> BinTree = null;
    //画图坐标
    public Point Location;

    public Tree(int Value, BST<Node> BinTree, Point Location)
    {
        this.Value = Value;
        this.BinTree = BinTree;
        this.Location = Location;
    }
    public Tree(int Value, BST<Node> BinTree)
    {
        this.Value = Value;
        this.BinTree = BinTree;
    }
}
