package com.example.demo.DataStruct;

public class Heap<T extends Comparable<T>> {

    //堆数据
    public T[] Data = null;
    //数据个数
    public int Size = 0;

    public int getSize() {
        return Size;
    }

    public T[] getData() {
        return Data;
    }

    public Heap(T[] t)
    {
        this.Data = t;
        this.Size = 0;
    }

    //是否为空
    public boolean Empty()
    {
        return Size == 0;
    }

    //删除最小元素
    public void DeleteMin()
    {
        if (this.Size != 0) {
            Data[1] = Data[Size];
            Size--;
            //下调
            percolate_down(1, Size);
        }

    }

    //获取最小元素
    public T MinData() {
        if (Size == 0) {
           return null;
        }
        return Data[1];
    }

    //插入
    public void Insert(T data)
    {
            Size++;
            Data[Size] = data;
            //上调
            percolate_up(1, Size);
    }

    //下调算法
    private void percolate_down(int r, int n)
    {
        int c = 2 * r;
        while(c <= n)
        {
            if(c < n && Data[c].compareTo(Data[c+1]) < 0)
                c++;
            if (Data[r].compareTo(Data[c]) < 0)
            {
                T temp = Data[r];
                Data[r] = Data[c];
                Data[c] = temp;
                r = c;
                c = 2 * c;
            }
            else
            {
                break;
            }
        }
    }

    //上调算法
    private void percolate_up(int r, int n)
    {
        int loc = this.Size;
        int parent = loc / 2;
        while (parent >= 1 && this.Data[loc].compareTo(this.Data[parent]) > 0)
        {
            T temp = Data[loc];
            Data[loc] = Data[parent];
            Data[parent] = temp;
            loc = parent;
            parent = loc / 2;
        }
    }

}
