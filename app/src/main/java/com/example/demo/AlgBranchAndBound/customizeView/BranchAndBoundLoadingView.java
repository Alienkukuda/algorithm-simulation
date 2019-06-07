package com.example.demo.AlgBranchAndBound.customizeView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.example.demo.AlgBranchAndBound.entity.BranchBoundLoadTextBox;
import com.example.demo.AlgBranchAndBound.entity.LoadNode;
import com.example.demo.DataStruct.BST;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BranchAndBoundLoadingView extends View {
    public int signal = 0;
    public Paint mPaint;
    //解空间二叉树
    public BST<LoadNode> Tree = null;
    public char BaseNameChar = 'A';

    //用于搜索过程打印队列信息
    public Queue<LoadNode> PrintMsgQueue = null;
    //画箭头的结点
    public LoadNode DrawingArrowHeadNode = null;
    //集装箱个数
    public int ContainerNum = 0;
    //集装箱重量数组
    public int[] w = null;
    //第一艘轮船的载重量
    public int c = -1;
    //屏幕宽度
    private static int ScreenWidth;

    private BranchAndBoundLoadingActionListener listener;

    //初始化
    public BranchAndBoundLoadingView(Context context, AttributeSet attrs) {
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
            DrawingTreeForBranchAndBoundLoadingProb(canvas,Tree.myRoot,new Point(550,100),DrawingArrowHeadNode);
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

        public void doWork() {
            int result = -1;
            BaseNameChar = 'A';
            Tree = MakeTree(w);
            refresh();
            result = MaxLoading(w, c, ContainerNum);
            AddListCodeProc("搜索结束,result=" + result);
        }

        public BST<LoadNode> MakeTree(int[] wi)
        {
            BST<LoadNode> tree = new BST<LoadNode>();
            tree.Insert(new LoadNode(BaseNameChar++, 0));
            if (BaseNameChar == 'Z' + 1) BaseNameChar = 'a';

            MakeTreeAux(tree, wi, 1);
            return tree;
        }

        public void MakeTreeAux(BST<LoadNode> parentTree, int[] wi, int index)
        {
            Queue<BST<LoadNode>> Q = new LinkedList<BST<LoadNode>>();
            Q.offer(parentTree);

            BST<LoadNode> levelFinishFlag = new BST<LoadNode>();
            levelFinishFlag.Insert(new LoadNode(' ', -999));    //同层结束标志
            Q.offer(levelFinishFlag);

            while (true)
            {
                if (index >= wi.length)
                {
                    break;
                }
                else
                {
                    BST<LoadNode> node = Q.poll();
                    if (BranchAndBoundLoadingProbNodeEw(node) != -999)   //不是结束标志
                    {
                        BST<LoadNode> left = new BST<LoadNode>();
                        left.Insert(new LoadNode(BaseNameChar++, BranchAndBoundLoadingProbNodeEw(node) + wi[index]));
                        if (BaseNameChar == 'Z' + 1) BaseNameChar = 'a';


                        BST<LoadNode> right = new BST<LoadNode>();
                        right.Insert(new LoadNode(BaseNameChar++, BranchAndBoundLoadingProbNodeEw(node)));
                        if (BaseNameChar == 'Z' + 1) BaseNameChar = 'a';

                        AddChildTree(node,left, right);

                        Q.offer(left);
                        Q.offer(right);
                    }
                    else    //是结束标志
                    {
                        index++;

                        levelFinishFlag = new BST<LoadNode>();
                        levelFinishFlag.Insert(new LoadNode(' ', -999));    //同层结束标志
                        Q.offer(levelFinishFlag);
                    }
                }
            }
        }

        //增加左右子树,用于分支限界法装载问题
        private void AddChildTree(BST<LoadNode> parentTree, BST<LoadNode> left, BST<LoadNode> right)
        {
            parentTree.myRoot.Left = left.myRoot;
            parentTree.myRoot.Right = right.myRoot;
        }

        private int BranchAndBoundLoadingProbNodeEw(BST<LoadNode> parentTree)
        {
            if (parentTree.myRoot.Data instanceof LoadNode)
            {
                return parentTree.myRoot.Data.Ew;
            }
            else
            {
                return -1;
            }
        }

        //获取根结点
        private LoadNode BranchAndBoundLoadingProbNode() {
            if (Tree.myRoot.Data != null)
                return Tree.myRoot.Data;
            else
                return null;
        }

        //设置画箭头的结点
        private void SetDrawingArrowHeadNode(LoadNode node)
        {
            DrawingArrowHeadNode = node;
//            refresh();
            Pause();
        }

        //打印队列信息
        private String getPrintMsgQueueString()
        {
            List<LoadNode> list = new ArrayList<LoadNode>(PrintMsgQueue);

            String result = "[ ";
            for (int i = 0; i < list.size(); i++)
            {
                if (list.get(i) != null)
                {
                    if (list.get(i).Ew == -999)
                    {
                        result += "-1";
                    }
                    else if (list.get(i).name != ' ')
                    {
                        result += list.get(i).name;
                    }
                    else
                    {
                        result += " ";
                    }
                }
                else
                {
                    result += " ";
                }


                if (i != list.size() - 1)
                {
                    result += ",";
                }
            }
            result += " ]";
            return result;
        }

        //更新相关数据
        private void UpdateData(int i,int Ew,int bestw,int r)
        {
            BranchBoundLoadTextBox loadTextBox = new BranchBoundLoadTextBox(i,Ew,bestw,r);
            listener.updateTextBox(loadTextBox);
            Pause();
        }

        //在树中查找结点的儿子,在分支限界法装载问题中使用
        private LoadNode getBranchAndBoundLoadingProbNodeInTree(LoadNode node, boolean left)
        {

            BST<LoadNode>.BinNode<LoadNode> temp = getBranchAndBoundLoadingProbNodeInTreeAux(Tree.myRoot, node);
            if (temp != null)
            {
                if (left)
                {
                    if (temp.Left != null)
                        return temp.Left.Data;
                    else
                        return null;
                }
                else
                {
                    if (temp.Right != null)
                        return temp.Right.Data;
                    else
                        return null;
                }
            }
            else
            {
                return null;
            }
        }

        private BST<LoadNode>.BinNode<LoadNode> getBranchAndBoundLoadingProbNodeInTreeAux(BST<LoadNode>.BinNode<LoadNode> node, LoadNode nodeData)
        {
            if (node != null)
            {
                if (node.Data == nodeData)
                {
                    return node;
                }
                else if (node.Left != null)
                {
                    BST<LoadNode>.BinNode<LoadNode> temp = getBranchAndBoundLoadingProbNodeInTreeAux(node.Left, nodeData);
                    if (temp != null)
                    {
                        return temp;
                    }
                    else if (node.Right != null)
                    {
                        temp = getBranchAndBoundLoadingProbNodeInTreeAux(node.Right, nodeData);
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

        private String getNodeName(LoadNode node)
        {
            String result = "";
            if(node != null)
            {
                result += (node.Ew == -999 ? "-1" : String.valueOf(node.name));
            }
            return result;
        }

        private int MaxLoading(int[] wi, int ci, int n)
        {
            //初始化
            Queue<Integer> Q = new LinkedList<Integer>();    //活结点队列
            PrintMsgQueue = new LinkedList<LoadNode>();

            Q.offer(-1);  //同层结点结尾标志
            PrintMsgQueue.offer(new LoadNode(' ', -999));
            LoadNode node = BranchAndBoundLoadingProbNode();
            SetDrawingArrowHeadNode(node);
            refresh();

            int i = 1;  //当前扩展结点所处的层
            int Ew = 0,     //扩展结点对应的载重量
                    bestw = 0,  //当前最优载重量
                    r = 0;      //剩余集装箱的重量

            //UpdateData(i, Ew, bestw, r);

            for (int j = 2; j <= n; j++)
            {
                r += wi[j];
            }
            //UpdateData(i, Ew, bestw, r);

            while (true)
            {
                UpdateData(i, Ew, bestw, r);
                Pause();
                int wt = Ew + wi[i]; // 检查左儿子结点,左儿子结点的重量


                LoadNode leftNodetemp = getBranchAndBoundLoadingProbNodeInTree(node, true);
                AddListCodeProc("检查左儿子结点" + getNodeName(leftNodetemp));
                SetDrawingArrowHeadNode(leftNodetemp);
                refresh();

                if (wt <= ci) //可行结点
                {
                    AddListCodeProc("Ew(A)+w[" + i + "]=" + wt + " <= " + ci);
                    if (wt > bestw)
                    {
                        AddListCodeProc("Ew(A)+w[" + i + "] > bestw=" + bestw + ",更新bestw");
                        bestw = wt; //提前更新bestw
                        UpdateData(i, Ew, bestw, r);
                    }
                    if (i < n)
                    {
                        Q.offer(wt);

                        PrintMsgQueue.offer(leftNodetemp);
                        AddListCodeProc("将左儿子结点" + getNodeName(leftNodetemp) + "插入队列，队列:" + getPrintMsgQueueString());
                    }
                }
                else
                {
                    AddListCodeProc("Ew(A)+w[" + i + "]=" + wt + " > " + ci + ", 左儿子结点舍弃");
                }

                // 检查右儿子结点
                LoadNode rightNodetemp = getBranchAndBoundLoadingProbNodeInTree(node, false);
                AddListCodeProc("检查右儿子结点" + getNodeName(rightNodetemp) + " Ew + r = " + (Ew + r) + ", bestw = " + bestw);
                SetDrawingArrowHeadNode(rightNodetemp);
                refresh();
                if (Ew + r > bestw && i < n)
                {
                    Q.offer(Ew);  //可能含最优解，不含最优解的右儿子被剪枝

                    PrintMsgQueue.offer(rightNodetemp);
                    AddListCodeProc("将右儿子结点" + getNodeName(rightNodetemp) + "插入队列，队列:" + getPrintMsgQueueString());
                }
                else
                {
                    AddListCodeProc("右儿子结点不满足条件不插入队列，队列:" + getPrintMsgQueueString());
                }

                try
                {
                    Ew = Q.poll();   //取下一扩展结点

                    node = PrintMsgQueue.poll();
                    AddListCodeProc("取下一扩展结点:" + getNodeName(node) + " 队列:" + getPrintMsgQueueString());
                    SetDrawingArrowHeadNode(node);
                    refresh();

                    if (Ew == -1)   //同层结点尾部标志
                    {
                        if (Q.size() == 0)
                        {
                            AddListCodeProc("队列为空返回");
                            return bestw;
                        }
                        Q.offer(-1);
                        PrintMsgQueue.offer(new LoadNode(' ', -999));
                        AddListCodeProc("将同层结束标志-1加入队列，队列:" + getPrintMsgQueueString());

                        Ew = Q.poll();   //取下一扩展结点
                        node = PrintMsgQueue.poll();
                        AddListCodeProc("取下一扩展结点:" + getNodeName(node) + " 队列:" + getPrintMsgQueueString());
                        SetDrawingArrowHeadNode(node);
                        refresh();


                        i++;                //进入下一层
                        AddListCodeProc("进入下一层 i=" + i);
                        UpdateData(i, Ew, bestw, r);

                        r -= wi[i];          //剩余集装箱重量
                        //UpdateData(i, Ew, bestw, r);
                    }
                }
                catch (Exception ex)
                {
                    //LogRecordHelper.WriteLog("在" + this.Text + "中队列操作异常信息:" + ex.Message);
                    return bestw;
                }
            }
        }

        public void run() {
            doWork();
            ++signal;
        }
    }

    //刷新界面
    public void refresh() {
        postInvalidate();
    }

    public void clear() {
        signal = 0;
        BaseNameChar = 'A';
        c = -1;
        PrintMsgQueue = null;
        DrawingArrowHeadNode = null;

        ContainerNum = 0;
        refresh();
    }

    private void DrawingTreeForBranchAndBoundLoadingProb(Canvas canvas, BST<LoadNode>.BinNode<LoadNode> subtreeRoot, Point subtreeRootLocation, LoadNode ArrowHeadNode) {
        if (subtreeRoot != null) {
            LoadNode node = subtreeRoot.Data;

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
            if (node == ArrowHeadNode)
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
            mPaint.setColor(Color.RED);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(String.valueOf(node.name),center.x,center.y+10,mPaint);
            //画结点载重量
            canvas.drawText(String.valueOf(node.Ew),center.x,center.y+50,mPaint);

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
            }
            //再画左子树
            DrawingTreeForBranchAndBoundLoadingProb(canvas,subtreeRoot.Left,new Point(subtreeRootLocation.x - tempX, subtreeRootLocation.y + tempY),ArrowHeadNode);

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
            }

            //再画右子树
            DrawingTreeForBranchAndBoundLoadingProb(canvas,subtreeRoot.Right,new Point(subtreeRootLocation.x + tempX, subtreeRootLocation.y + tempY),ArrowHeadNode);
        }

    }


    public interface BranchAndBoundLoadingActionListener {
        //代码高亮
//        void HightLightShowSingleLineCode(int index);
        //步骤跟踪输出
        void addListCodeProc(String newProc);
        //参数跟踪
        void updateTextBox(BranchBoundLoadTextBox textBox);
    }

    public void setBranchAndBoundLoadingActionListener(BranchAndBoundLoadingActionListener ls){
        this.listener = ls;
    }
}
