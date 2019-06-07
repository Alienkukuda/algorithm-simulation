package com.example.demo.AlgBranchAndBound.entity;

//子集树结点类
public class bbnode {
    //指向父结点的指针
    public bbnode parent;
    //左儿子结点标志
    public boolean LChild;


    public bbnode()
    {
    }

    public bbnode(bbnode parent, boolean LChild)
    {
        this.parent = parent;
        this.LChild = LChild;
    }
}
