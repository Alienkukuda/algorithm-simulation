package com.example.demo.DataStruct;

import com.example.demo.AlgGreedy.entity.CharString;
import com.example.demo.AlgGreedy.entity.Node;

import java.util.ArrayList;
import java.util.List;

public class BST<T extends Comparable<T>> {

    //二叉树节点类
    public class BinNode<T> {
        //节点数据
        public T Data;
        //左子树根节点
        public BinNode<T> Left;
        //右子树根节点
        public BinNode<T> Right;

        public BinNode()
        {
            this.Left = null;
            this.Right = null;
        }

        public BinNode(T Data)
        {
            this.Data = Data;
            this.Left = null;
            this.Right = null;
        }

        public BinNode(T Data, BinNode<T> Left, BinNode<T> Right)
        {
            this.Data = Data;
            this.Left = Left;
            this.Right = Right;
        }
    }

    //二叉树根节点
    public BinNode<T> myRoot;

    public BST()
    {

    }

    public BST(BinNode<T> myRoot)
    {
        this.myRoot = myRoot;
    }

    public BST(BST<T> orig)
    {
        if (orig.myRoot == null)
            myRoot = null;
        else
            myRoot = copy(orig.myRoot);
    }

    //复制一棵树
    private BinNode<T> copy(BinNode<T> subtree)
    {
        if (subtree != null)
        {
            BinNode<T> leftPtr, rightPtr;
            leftPtr = copy(subtree.Left);
            rightPtr = copy(subtree.Right);
            return new BinNode<T>(subtree.Data, leftPtr, rightPtr);
        }
        return null;
    }

    //判断一棵树是否为空
    public boolean Empty()
    {
        if (this.myRoot == null)
            return true;
        return false;
    }

    //树的高度
    public int Height()
    {
        return heightAux(myRoot);
    }

    //左子树的高度
    public int LeftChildHeight()
    {
        if (myRoot.Left != null)
        {
            return heightAux(myRoot.Left);
        }
        else
        {
            return 0;
        }
    }

    //右子树的高度
    public int RightChildHeight()
    {
        if (myRoot.Right != null)
        {
            return heightAux(myRoot.Right);
        }
        else
        {
            return 0;
        }
    }

    //左子树的宽度
    public int LeftChildWidth()
    {
        int left = 0;
        if (myRoot != null)
        {
            BinNode<T> temp = myRoot.Left;
            while (temp != null)
            {
                if (temp.Left != null)
                {
                    left++;
                }
                temp = temp.Left;
            }
        }
        return left;
    }

    //右子树的宽度
    public int RightChildWidth()
    {
        int right = 0;
        if (myRoot != null)
        {
            BinNode<T> temp = myRoot.Right;
            while (temp != null)
            {
                if (temp.Right != null)
                {
                    right++;
                }
                temp = temp.Right;
            }
        }
        return right;
    }

    //二叉树高度
    public int heightAux(BinNode<T> subtreeRoot)
    {
        if(subtreeRoot == null)
            return 0;
        int x = heightAux(subtreeRoot.Left);
        int y = heightAux(subtreeRoot.Right);
        if(x > y)
            return x + 1;
        else
            return y + 1;
    }

    //子树中间宽度
    public int middleWidthAux(BinNode<T> subtreeRoot)
    {
        int left = 0, right = 0;
        if (subtreeRoot != null)
        {
            BinNode<T> temp = subtreeRoot.Left;
            while (temp != null)
            {
                if (temp.Right != null)
                {
                    left++;
                }
                temp = temp.Right;
            }
            temp = subtreeRoot.Right;
            while (temp != null)
            {
                if (temp.Left != null)
                {
                    right++;
                }
                temp = temp.Left;
            }
            return Math.max(left, right);
        }
        else
        {
            return 0;
        }
    }

    //查找节点
    public boolean Search(T item)
    {
        BinNode<T> locptr = myRoot;
        boolean found = false;
        while (!found && locptr != null)
        {
            if (item.compareTo(locptr.Data) < 0)
                locptr = locptr.Left;
            else if (locptr.Data.compareTo(item) < 0)
                locptr = locptr.Right;
            else
                found = true;
        }
        return found;
    }

    //插入节点
    public void Insert(T item)
    {
        BinNode<T> locptr = myRoot,   // search pointer
                parent = null;        // pointer to parent of current node
        boolean found = false;     // indicates if item already in BST
        while (!found && locptr != null)
        {
            parent = locptr;
            if (item.compareTo(locptr.Data) < 0)
                locptr = locptr.Left;
            else if (locptr.Data.compareTo(item) < 0)
                locptr = locptr.Right;
            else
                found = true;
        }
        if (!found)
        {                                 // construct node containing item
            locptr = new BinNode<T>(item);
            if (parent == null)               // empty tree
                myRoot = locptr;
            else if (item.compareTo(parent.Data) < 0)  // insert to left of parent
                parent.Left = locptr;
            else                           // insert to right of parent
                parent.Right = locptr;
        }
    }

    //删除一个节点
    public void Remove(T item)
    {
        boolean found = false;                      // signals if item is found
        BinNode<T> x = null,                        // points to node to be deleted
                parent = null;                       //    "    " parent of x and xSucc
        InsertUserSearch(item, found, x, parent);

        if (!found)
        {
            return;
        }
        //else
        if (x.Left != null && x.Right != null)
        {                                // node has 2 children
            // Find x's inorder successor and its parent
            BinNode<T> xSucc = x.Right;
            parent = x;
            while (xSucc.Left != null)       // descend left
            {
                parent = xSucc;
                xSucc = xSucc.Left;
            }

            // Move contents of xSucc to x and change x
            // to point to successor, which will be removed.
            x.Data = xSucc.Data;
            x = xSucc;
        } // end if node has 2 children

        // Now proceed with case where node has null or 2 child
        BinNode<T>  subtree = x.Left;             // pointer to a subtree of x
        if (subtree == null)
            subtree = x.Right;
        if (parent == null)                  // root being removed
            myRoot = subtree;
        else if (parent.Left == x)       // left child of parent
            parent.Left = subtree;
        else                              // right child of parent
            parent.Right = subtree;
        //delete x;
    }
    //删除一个节点使用到的函数
    private void InsertUserSearch(T item, boolean found, BinNode<T> locptr, BinNode<T> parent)
    {
        locptr = myRoot;
        parent = null;
        found = false;
        while (!found && locptr != null)
        {
            if (item.compareTo(locptr.Data) < 0)       // descend left
            {
                parent = locptr;
                locptr = locptr.Left;
            }
            else if (locptr.Data.compareTo(item) < 0)  // descend right
            {
                parent = locptr;
                locptr = locptr.Right;
            }
            else                           // item found
            {
                found = true;
            }
        }
    }

    //合并两棵树,用于哈夫曼编码
    public void MergeTwoTree(BST<T> t1, BST<T> t2)
    {
        this.myRoot.Left = t1.myRoot;
        this.myRoot.Right = t2.myRoot;
    }
}
