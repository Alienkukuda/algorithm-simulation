package com.example.demo.AlgBranchAndBound.customizeView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.example.demo.AlgBranchAndBound.entity.BpNode;
import com.example.demo.AlgBranchAndBound.entity.BranchBoundBpTextBox;
import com.example.demo.AlgBranchAndBound.entity.HeapNode;
import com.example.demo.AlgBranchAndBound.entity.ValuePerWeight;
import com.example.demo.AlgBranchAndBound.entity.bbnode;
import com.example.demo.DataStruct.BST;
import com.example.demo.DataStruct.Heap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BranchAndBound0And1KProbView extends View {
    public int signal = 0;
    public Paint mPaint;
    public char BaseNameChar = 'A';
    //物品个数
    public int ItemNum = 0;
    //背包容量
    public int c = 0;
    //原始物品重量数组
    public int[] sourse_w = null;
    //排序后物品重量数组
    public int[] w = null;
    //原始物品价值数组
    public int[] sourse_p = null;
    //画箭头的结点
    public BpNode DrawingArrowHeadNode = null;
    //排序后物品价值数组
    public int[] p = null;
    //解空间二叉树
    public BST<BpNode> Tree = null;
    // 当前背包重量
    public int cw = -1;
    //当前背包价值
    public int cp = -1;
    //单位重量价值数组
    public String[][] tablePerStr = null;
    //最优解
    public String[][] tableBestStr = null;
    //活结点优先队列
    public Heap<HeapNode> H = null;
    //指向扩展结点的指针
    public bbnode E = null;
    //最优解
    public boolean[] bestx = null;
    public List<BpNode> DrawingXNodeList = new ArrayList<BpNode>();
    //屏幕宽度
    private static int ScreenWidth;

    private BranchAndBound0And1KProbActionListener listener;

    //初始化
    public BranchAndBound0And1KProbView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        if (Tree != null)
        {
            DrawingTree_BranchAndBound0And1KProb(canvas,Tree.myRoot,new Point(550,150),DrawingArrowHeadNode,DrawingXNodeList);
        }
    }

    public void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(2);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(32);
        ScreenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    public class ThreadRun implements Runnable {
        private Object lock;

        public ThreadRun(Object lock) {
            this.lock = lock;
        }

        public synchronized void AddListCodeProc(String str) {
            listener.addListCodeProc(str);
            Pause();
        }

        public void Pause() {
            try {
                synchronized (lock){
                    lock.wait();
                }
            }catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }

        public BST<BpNode> MakeTree(int[] w)
        {
            BST<BpNode> tree = new BST<BpNode>();
            tree.Insert(new BpNode(BaseNameChar++, 0));
            if (BaseNameChar == 'Z' + 1) BaseNameChar = 'a';

            MakeTreeAux(tree, 1);
            return tree;
        }

        public void MakeTreeAux(BST<BpNode> parentTree, int index)
        {
            Queue<BST<BpNode>> Q = new LinkedList<BST<BpNode>>();
            Q.offer(parentTree);

            BST<BpNode> levelFinishFlag = new BST<BpNode>();
            levelFinishFlag.Insert(new BpNode(' ', -999));    //同层结束标志
            Q.offer(levelFinishFlag);

            while (true)
            {
                if (index >= w.length)
                {
                    break;
                }
                else
                {
                    BST<BpNode> node = Q.poll();
                    if (BranchAndBound0And1KProbNode(node).up != -999)   //不是结束标志
                    {
                        BST<BpNode> left = new BST<BpNode>();
                        left.Insert(new BpNode(BaseNameChar++,0));
                        if (BaseNameChar == 'Z' + 1) BaseNameChar = 'a';


                        BST<BpNode> right = new BST<BpNode>();
                        right.Insert(new BpNode(BaseNameChar++,0));
                        if (BaseNameChar == 'Z' + 1) BaseNameChar = 'a';

                        AddChildTree(node, left, right);

                        Q.offer(left);
                        Q.offer(right);
                    }
                    else    //是结束标志
                    {
                        index++;

                        levelFinishFlag = new BST<BpNode>();
                        levelFinishFlag.Insert(new BpNode(' ', -999));    //同层结束标志
                        Q.offer(levelFinishFlag);
                    }
                }
            }
        }

        private void AddChildTree(BST<BpNode> parentTree, BST<BpNode> left, BST<BpNode> right)
        {
            parentTree.myRoot.Left = left.myRoot;
            parentTree.myRoot.Right = right.myRoot;
        }

        //获取根结点
        private BpNode BranchAndBound0And1KProbNode(BST<BpNode> parentTree) {
            if (parentTree.myRoot.Data != null)
                return parentTree.myRoot.Data;
            else
                return null;
        }

        public void doWork() {
            int result = -1;
            AddListCodeProc("程序开始");
            result = Knapsack(sourse_p, sourse_w, ItemNum);
            AddListCodeProc("搜索结束,最优价值=" + result);
        }

        private int Knapsack(int[] pi, int[] wi, int n)
        {
            w = new int[n + 1];
            p = new int[n + 1];
            int W = 0;
            int P = 0;
            List<ValuePerWeight> Q = new ArrayList<ValuePerWeight>();
            for (int i = 1; i <= n; i++)
            {
                Q.add(new ValuePerWeight());
                Q.get(i - 1).ID = i;
                Q.get(i - 1).d = 1.0 * pi[i] / wi[i];
                P += pi[i];
                W += wi[i];
            }
            if (W <= c) return P;  //装入所有物品

            //依物品单位重量价值排序
            //字符编码表排序
            //依物品单位重量价值排序
            Collections.sort(Q, new Comparator<ValuePerWeight>() {
                @Override
                public int compare(ValuePerWeight x,ValuePerWeight y) {
                    return (x.d  > y.d ) ? -1 : ((x.d  == y.d ) ? 0 : 1);
                }
            });
            //Q.Sort((x, y) => { return x.d.CompareTo(y.d); });

            for (int i = 1; i <= n; i++)
            {
                p[i] = pi[Q.get(i - 1).ID];
                w[i] = wi[Q.get(i - 1).ID];
                tablePerStr[1][i] = w[i]+"";
                tablePerStr[2][i] = p[i]+"";
            }

            AddListCodeProc("显示单位重量价值排序数组");

            ShowItemArraySorted();

            //画二叉树
            BaseNameChar = 'A';
            Tree = MakeTree(w);
            refresh();
            Pause();

            cp = 0;
            cw = 0;

            UpdateData(cw, cp, 0, 0);

            AddListCodeProc("开始搜索");
            int bestp = MaxKnapsack();

            return bestp;
        }

        private int MaxKnapsack()
        {
            HeapNode[] heapNodes = new HeapNode[1000];
            H = new Heap<HeapNode>(heapNodes);
            bestx = new boolean[ItemNum + 1];
            int i = 1;
            E = new bbnode(null, false);
            BpNode parent = BranchAndBound0And1KProbNode(Tree);
            parent.isShowData = true;
            SetDrawingArrowHeadNode(parent);
            cw = cp = 0;
            int bestp = 0;
            UpdateData(cw, cp, 0, i);

            int up = Bound(1); //价值上界
            UpdateTreeNodeMsg(parent, up, 0, 0);
            AddListCodeProc("up = Bound(1) = " + up);

            HeapNode N = null;
            while (i != ItemNum + 1)  //搜索子集空间树
            {
                UpdateData(cw, cp, 0, i);
                //检查当前扩展结点的左儿子结点
                BpNode node = getBranchAndBound0And1KProbNodeLeftChildInTree(parent);
                AddListCodeProc("检查左儿子结点" + node.name);
                SetDrawingArrowHeadNode(node);
                int wt = cw + w[i];
                AddListCodeProc("wt = cw + w[" + i + "] = " + wt);
                if (wt <= c)  //左儿子结点为可行结点
                {
                    AddListCodeProc("wt <= c = " + c);
                    if (cp + p[i] > bestp)
                    {
                        AddListCodeProc("cp + p[" + i + "] > bestp = " + bestp + "，更新bestp");
                        bestp = cp + p[i];
                        UpdateData(cw, cp, bestp, i);
                    }
                    UpdateTreeNodeMsg(node, up, cp + p[i], cw + w[i]);
                    AddLiveNode(up, cp + p[i], cw + w[i], true, i + 1,String.valueOf(node.name));
                    AddListCodeProc("将左儿子结点" + node.name + "插入队列，队列:" + ToString_BranchAndBound0And1KProb(H));
                }
                else
                {
                    AddListCodeProc("wt > c，左儿子结点不可行，舍弃");
                    SetDrawingXNode(node);
                }
                up = Bound(i + 1);
                AddListCodeProc("up = Bound(" + (i+1) + ") = " + up);

                //检查当前扩展结点的右儿子结点
                node = getBranchAndBound0And1KProbNodeRightChildInTree(parent);
                AddListCodeProc("检查右儿子结点" + node.name);
                SetDrawingArrowHeadNode(node);

                if (up >= bestp)
                {
                    UpdateTreeNodeMsg(node, up, cp, cw);
                    AddLiveNode(up, cp, cw, false, i + 1, String.valueOf(node.name));
                    AddListCodeProc("up(" + up + ") >= bestp(" + bestp + ")，将右儿子儿子结点" + node.name + "插入队列，队列:" + ToString_BranchAndBound0And1KProb(H));
                }
                else
                {
                    AddListCodeProc("up(" + up + ") < bestp(" + bestp + ")，右儿子结点不可行，舍弃");
                    SetDrawingXNode(node);
                }

                //取下一扩展结点
                N = H.MinData();
                H.DeleteMin();
                AddListCodeProc("取下一个扩展结点" + N.name + "，队列:" + ToString_BranchAndBound0And1KProb(H));
                parent = getBranchAndBound0And1KProbNodeByNameInTree(N.name);
                if(parent != null)
                    SetDrawingArrowHeadNode(parent);
                E = N.ptr;
                cw = N.weight;
                cp = N.profit;
                up = N.uprofit;
                i = N.level;
            } //搜索子集空间树while循环结束

            SetDrawingArrowHeadNode(null);
            //构造当前最优解
            AddListCodeProc("构造最优解");
            SetDrawingArrowHeadNode(getBranchAndBound0And1KProbNodeByNameInTree(N.name));

            for (int j = ItemNum; j > 0; j--)
            {
                bestx[j] = E.LChild;
                E = E.parent;
            }
            AddListCodeProc("构造最优解完成");
            UpdateData(N.weight, N.profit, bestp, i - 1);
            ShowBestSolution(bestx);
            return cp;
        }

        private void SetDrawingXNode(BpNode node)
        {
            DrawingXNodeList.add(node);
            refresh();
            Pause();
        }

        private void AddLiveNode(int up, int cpt, int cwt, boolean ch, int lev,String name)
        {
            bbnode b = new bbnode(E, ch);
            HeapNode N = new HeapNode(up, cpt, cwt, lev, b);
            N.name = name;
            H.Insert(N);
        }

        private void ShowBestSolution(boolean[] tbestx){
            for (int j = 1; j < ItemNum + 1; j++) {
                tableBestStr[1][j] = tbestx[j] == true?"1":"0";
            }
            listener.showBestSolution(tableBestStr);
        }

        private void SetDrawingArrowHeadNode(BpNode node)
        {
            DrawingArrowHeadNode = node;
            refresh();
            Pause();
        }

        public String ToString_BranchAndBound0And1KProb(Heap<HeapNode> heap)
        {
            String str = "[";
            for (int i = 1; i <= heap.Size; i++)
            {
                str += (heap.Data[i]).name;
                if (i != heap.Size)
                    str += ",";
            }
            return str + "]";
        }

        public BpNode getBranchAndBound0And1KProbNodeByNameInTree(String name)
        {
            BST<BpNode>.BinNode<BpNode> temp = getBranchAndBound0And1KProbNodeByNameInTreeAux(Tree.myRoot, name);
            if (temp != null)
            {
                return temp.Data;
            }
            else
            {
                return null;
            }
        }

        private BST<BpNode>.BinNode<BpNode> getBranchAndBound0And1KProbNodeByNameInTreeAux( BST<BpNode>.BinNode<BpNode> node, String name)
        {
            if (node != null)
            {
                if ( String.valueOf(node.Data.name).equals(name))
                {
                    return node;
                }
                else if (node.Left != null)
                {
                    BST<BpNode>.BinNode<BpNode> temp = getBranchAndBound0And1KProbNodeByNameInTreeAux(node.Left, name);
                    if (temp != null)
                    {
                        return temp;
                    }
                    else if (node.Right != null)
                    {
                        temp = getBranchAndBound0And1KProbNodeByNameInTreeAux(node.Right, name);
                        if (temp != null)
                        {
                            return temp;
                        }
                        else
                        {
                            return null;
                        }
                    }

                }
                return null;
            }
            else
            {
                return null;
            }
        }

        public BpNode getBranchAndBound0And1KProbNodeRightChildInTree(BpNode node){
            BST<BpNode>.BinNode<BpNode> temp = getBranchAndBound0And1KProbNodeInTreeAux(Tree.myRoot, node);
            if (temp != null)
            {
                if (temp.Right != null)
                {
                    return temp.Right.Data;
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }

        public BpNode getBranchAndBound0And1KProbNodeLeftChildInTree(BpNode node){
            BST<BpNode>.BinNode<BpNode> temp = getBranchAndBound0And1KProbNodeInTreeAux(Tree.myRoot, node);
            if (temp != null)
            {
                if (temp.Left != null)
                {
                    return temp.Left.Data;
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }

        private BST<BpNode>.BinNode<BpNode> getBranchAndBound0And1KProbNodeInTreeAux(BST<BpNode>.BinNode<BpNode> node, BpNode nodeData)
        {
            if (node != null)
            {
                if (node.Data == nodeData)
                {
                    return node;
                }
                else if (node.Left != null)
                {
                    BST<BpNode>.BinNode<BpNode> temp = getBranchAndBound0And1KProbNodeInTreeAux(node.Left, nodeData);
                    if (temp != null)
                    {
                        return temp;
                    }
                    else if (node.Right != null)
                    {
                        temp = getBranchAndBound0And1KProbNodeInTreeAux(node.Right, nodeData);
                        if (temp != null)
                        {
                            return temp;
                        }
                        else
                        {
                            return null;
                        }
                    }

                }
                return null;
            }
            else
            {
                return null;
            }
        }

        private void UpdateTreeNodeMsg(BpNode node, int up, int cpt, int cwt)
        {
            node.isShowData = true;
            node.up = up;
            node.cp = cpt;
            node.cw = cwt;
        }

        private int Bound(int i)
        {
            int cleft = c - cw; // 剩余容量
            int b = cp;
            while (i <= ItemNum && w[i] <= cleft)  //以物品单位重量价值递减序装入物品
            {
                cleft -= w[i];
                b += p[i];
                i++;
            }
            // 装满背包
            if (i <= ItemNum)
            {
                b += (int)((p[i] * 1.0 / w[i] )* cleft);
            }
            return b;
        }

        private void ShowItemArraySorted(){
            listener.showItemArraySorted(tablePerStr);
        }

        private void UpdateData(int cwt,int cpt,int bestpt,int levt)
        {
            BranchBoundBpTextBox bpTextBox = new BranchBoundBpTextBox(cwt,cpt,bestpt,levt);
            listener.updateTextBox(bpTextBox);
            Pause();
        }

        public void run() {
            doWork();
            ++signal;
        }
    }

    private void DrawingTree_BranchAndBound0And1KProb(Canvas canvas, BST<BpNode>.BinNode<BpNode> subtreeRoot, Point subtreeRootLocation, BpNode DrawingArrowHeadNode, List<BpNode> DrawingXNode){
        if (subtreeRoot != null)
        {
            //先画根
            BpNode node = subtreeRoot.Data;

            int height = Tree.heightAux(subtreeRoot) - 1;
            int radius = node.CircleRadius;
            Point center = subtreeRootLocation;
            int SpaceLenth,Angle;

            if (height <= 1) {
                SpaceLenth = 15 + height * 40;
                Angle = 20 + height * 5;
            } else if (height == 2){
                SpaceLenth = 65;
                Angle = 46;
            } else if (height == 3){
                SpaceLenth = 125;
                Angle = 56;
            } else {
                SpaceLenth = 240;
                Angle = 68;
            }

            //画箭头
            if (node == DrawingArrowHeadNode)
            {
                mPaint.setColor(Color.RED);
                mPaint.setStyle(Paint.Style.FILL);
                Point ArrowHeadStart = new Point(center.x, center.y - radius - 40);
                Point ArrowHeadEnd = new Point(center.x, center.y - radius);
                Point ArrowHeadAux1 = new Point(center.x - 5, center.y - radius -10);
                Point ArrowHeadAux2 = new Point(center.x + 5, center.y - radius -10);
                canvas.drawLine(ArrowHeadStart.x,ArrowHeadStart.y,ArrowHeadEnd.x,ArrowHeadEnd.y,mPaint);
                canvas.drawLine(ArrowHeadEnd.x,ArrowHeadEnd.y,ArrowHeadAux1.x,ArrowHeadAux1.y,mPaint);
                canvas.drawLine(ArrowHeadEnd.x,ArrowHeadEnd.y,ArrowHeadAux2.x,ArrowHeadAux2.y,mPaint);
            }

            //画圆圈
            mPaint.setColor(Color.BLUE);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(center.x,center.y,radius,mPaint);
            //画值
//            mPaint.setColor(Color.RED);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(String.valueOf(node.name),center.x,center.y+10,mPaint);

            //画结点相关数据
            if (node.isShowData)
            {
                String upstring = "up=" + node.up;
                String cpstring = "cp=" + node.cp;
                String cwstring = "cw=" + node.cw;
                mPaint.setColor(Color.RED);
                if (subtreeRoot.Left != null || subtreeRoot.Right != null)  //不是叶结点，画在上面
                {
                    canvas.drawText(upstring,center.x,center.y-80,mPaint);
                    canvas.drawText(cpstring,center.x,center.y-55,mPaint);
                    canvas.drawText(cwstring,center.x,center.y-30,mPaint);
                }
                else    //是叶结点，画在下面
                {
                    canvas.drawText(upstring,center.x,center.y+40,mPaint);
                    canvas.drawText(cpstring,center.x,center.y+65,mPaint);
                    canvas.drawText(cwstring,center.x,center.y+90,mPaint);
                }
            }

            double pi = 3.1415926f;
            double tempSin = Math.sin(Angle / 180.0 * pi);
            int tempX = (int)((SpaceLenth+radius) * tempSin);
            double tempCos = Math.cos(Angle / 180.0 * pi);
            int tempY = (int)((SpaceLenth+radius) * tempCos);


            //画左边箭头
            if (subtreeRoot.Left != null)
            {
                int ArrowHeadStartX = (int)(subtreeRootLocation.x - radius * tempSin);
                int ArrowHeadStartY = (int)(subtreeRootLocation.y + radius * tempCos);
                Point ArrowHeadStart = new Point(ArrowHeadStartX, ArrowHeadStartY);

                int ArrowHeadEndX = (int)(subtreeRootLocation.x - tempX + radius * tempSin);
                int ArrowHeadEndY = (int)(subtreeRootLocation.y + tempY - radius * tempCos);
                Point ArrowHeadEnd = new Point(ArrowHeadEndX, ArrowHeadEndY);
                mPaint.setColor(Color.BLUE);
                //画箭头
                canvas.drawLine(ArrowHeadStartX,ArrowHeadStartY,ArrowHeadEndX,ArrowHeadEndY,mPaint);
                //画1
                Point p = new Point((ArrowHeadStart.x + ArrowHeadEnd.x) / 2, (ArrowHeadStart.y + ArrowHeadEnd.y) / 2);
                mPaint.setColor(Color.GREEN);
                canvas.drawText("1",p.x,p.y,mPaint);
                //画×
                mPaint.setColor(Color.RED);
                for (BpNode bpNode:DrawingXNode){
                    if (bpNode.name == subtreeRoot.Left.Data.name){
                        canvas.drawLine(p.x-15,p.y-15,p.x+15,p.y+15,mPaint);
                        canvas.drawLine(p.x+15,p.y-15,p.x-15,p.y+15,mPaint);
                    }
                }
            }

            //再画左子树
            DrawingTree_BranchAndBound0And1KProb(canvas, subtreeRoot.Left, new Point(subtreeRootLocation.x - tempX, subtreeRootLocation.y + tempY), DrawingArrowHeadNode, DrawingXNode);

            //画右边箭头
            if (subtreeRoot.Right != null)
            {
                int ArrowHeadStartX = (int)(subtreeRootLocation.x + radius * tempSin);
                int ArrowHeadStartY = (int)(subtreeRootLocation.y + radius * tempCos);
                Point ArrowHeadStart = new Point(ArrowHeadStartX, ArrowHeadStartY);

                int ArrowHeadEndX = (int)(subtreeRootLocation.x + tempX - radius * tempSin);
                int ArrowHeadEndY = (int)(subtreeRootLocation.y + tempY - radius * tempCos);
                Point ArrowHeadEnd = new Point(ArrowHeadEndX, ArrowHeadEndY);
                mPaint.setColor(Color.BLUE);
                //画箭头
                canvas.drawLine(ArrowHeadStartX,ArrowHeadStartY,ArrowHeadEndX,ArrowHeadEndY,mPaint);
                //画0
                Point p = new Point((ArrowHeadStart.x + ArrowHeadEnd.x) / 2, (ArrowHeadStart.y + ArrowHeadEnd.y) / 2);
                mPaint.setColor(Color.GREEN);
                canvas.drawText("0",p.x,p.y,mPaint);
                mPaint.setColor(Color.RED);
                for (BpNode bpNode:DrawingXNode){
                    if (bpNode.name == subtreeRoot.Right.Data.name){
                        canvas.drawLine(p.x-15,p.y-15,p.x+15,p.y+15,mPaint);
                        canvas.drawLine(p.x+15,p.y-15,p.x-15,p.y+15,mPaint);
                    }
                }
            }

            //再画右子树
            DrawingTree_BranchAndBound0And1KProb(canvas, subtreeRoot.Right, new Point(subtreeRootLocation.x + tempX, subtreeRootLocation.y + tempY), DrawingArrowHeadNode, DrawingXNode);
        }
    }

    public void clear() {
        signal = 0;
        BaseNameChar = 'A';
        c = 0;
        DrawingArrowHeadNode = null;
        ItemNum = 0;
        sourse_w = null;
        w = null;
        sourse_p = null;
        p = null;
        cw = -1;
        cp = -1;
        Heap<HeapNode> H = null;
        bbnode E = null;
        bestx = null;
        DrawingXNodeList.clear();
        refresh();
    }

    public void refresh() {
        postInvalidate();
    }

    public interface BranchAndBound0And1KProbActionListener {
        //步骤跟踪输出
        void addListCodeProc(String newProc);
        //参数跟踪
        void updateTextBox(BranchBoundBpTextBox textBox);
        //显示单位重量价值数组
        void showItemArraySorted(String[][] tablePerStr);
        //显示最优解
        void showBestSolution(String[][] tableBestStr);
    }

    public void setBranchAndBound0And1KProbActionListener(BranchAndBound0And1KProbActionListener ls){
        this.listener = ls;
    }
}
