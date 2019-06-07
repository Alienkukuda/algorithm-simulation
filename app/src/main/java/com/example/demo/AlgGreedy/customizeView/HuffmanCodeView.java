package com.example.demo.AlgGreedy.customizeView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.example.demo.AlgDynamic.entity.TableString;
import com.example.demo.AlgGreedy.entity.CharString;
import com.example.demo.AlgGreedy.entity.CodeChar;
import com.example.demo.AlgGreedy.entity.Node;
import com.example.demo.AlgGreedy.entity.Tree;
import com.example.demo.DataStruct.BST;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HuffmanCodeView extends View {
    public int signal = 0;
    public Paint mPaint;
    //屏幕宽度
    private static int ScreenWidth;
    //是否获取编码字符
    public boolean isGetCodeTable = false;
    //是否运行
    public boolean isRunning = false;
    //是否全速运行
    public boolean isStepOver = false;
    //编码字符个数
    public int CodeCharNum = 0;
    //编码字符频率表
    public List<CodeChar> codeCharList = new ArrayList<CodeChar>();
    //树列表
    public List<Tree> treeList = new ArrayList<Tree>();
    //画树的坐标
    public List<Point> treeLocationList = new ArrayList<Point>();

    //回调函数
    private HuffmanCodeActionLister listener;

    //初始化
    public HuffmanCodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        drawTree(canvas);
    }

    public void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(2);
        mPaint.setTextAlign(Paint.Align.CENTER);
        ScreenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    public class ThreadRun implements Runnable {
        private Object lock;
        int adderCount = 0;

        public ThreadRun(Object lock) {
            this.lock = lock;
        }

        public synchronized void AddListCodeProc(String str) {
            if (!(listener == null)) {
                listener.addListCodeProc(str);
            }
            try {
                synchronized (lock){
                    lock.wait();
                }
            }catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }

        public synchronized void Pause() {
            try {
                synchronized (lock){
                    lock.wait();
                }
            }catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }

        public void doWork() {
            int LocX = 50;
            int LocY = 120;
            for (int i = 0; i < codeCharList.size(); i++)
            {
                int value = codeCharList.get(i).Rate;
                Point point = new Point(LocX, LocY);
                treeLocationList.add(point);
                BST<Node> bst = new BST<Node>();
                Node node = new Node(value, true, codeCharList.get(i));
                bst.Insert(node);
                //Tree tree = new Tree(value, bst, point);
                Tree tree = new Tree(value, bst);
                treeList.add(tree);
                LocX += 100;
            }
            refresh();
            Pause();

            AddListCodeProc("对字符编码频率排序!");

            //字符编码表排序
            Collections.sort(treeList, new Comparator<Tree>() {
                @Override
                public int compare(Tree tree1,Tree tree2) {
                    return (tree1.Value  < tree2.Value ) ? -1 : ((tree1.Value  == tree2.Value ) ? 0 : 1);
                }
            });
            refresh();
            AddListCodeProc("字符编码频率排序完成!");

            //开始合并树
            MergeTree();
        }

        private void MergeTree()
        {
            Tree min1 = null, min2 = null;
            while (treeList.size() > 1)
            {
                adderCount++;
                //找到权值最小的两棵树
                AddListCodeProc("查找权值最小的两棵树并合并");
                min1 = treeList.get(0);
                for (int i = 1; i < treeList.size(); i++)
                {
                    if (treeList.get(i).Value < min1.Value)
                    {
                        min1 = treeList.get(i);
                    }
                }
                treeList.remove(min1);

                min2 = treeList.get(0);
                for (int i = 1; i < treeList.size(); i++)
                {
                    if (treeList.get(i).Value < min1.Value && treeList.get(i) != min1)
                    {
                        min2 = treeList.get(i);
                    }
                }
                treeList.remove(min2);

                //合并权值最小的两棵树为一棵树
                Tree tree = MergeTwoMinTree(min1, min2);
                treeList.add(tree);
                resetNewLocationRefresh();

                AddListCodeProc("合并完成");
                AddListCodeProc("重新排序");

                Collections.sort(treeList, new Comparator<Tree>() {
                    @Override
                    public int compare(Tree tree1,Tree tree2) {
                        return (tree1.Value  < tree2.Value ) ? -1 : ((tree1.Value  == tree2.Value ) ? 0 : 1);
                    }
                });
                resetNewLocationRefresh();
            }
        }

        private void resetNewLocationRefresh()
        {
            //重置画图坐标
            treeLocationList.clear();

            int LocX = 80;
            int LocY = 120;
            LocX += 60*adderCount;
            int adderX = 70+20*adderCount;
            for (int i = 0; i < treeList.size(); i++)
            {
                Point point = new Point(LocX, LocY);
                treeLocationList.add(point);
                LocX += adderX;
            }
            refresh();
            Pause();
        }

        private Tree MergeTwoMinTree(Tree t1, Tree t2)
        {
            int value = t1.Value + t2.Value;
            BST<Node> bst = new BST<Node>();
            bst.Insert(new Node(value, false));
            bst.MergeTwoTree(t1.BinTree, t2.BinTree);
            Tree result = new Tree(value, bst);
            return result;
        }

        private void ShowCodeResult()
        {
            List <CharString> list = getCodingList();
            String[] ThirdTableStr = new String[CodeCharNum+1];
            ThirdTableStr[0] = "编码";
            for (int i = 0; i < list.size(); i++) {
                int temp = list.get(i).name = codeCharList.get(0).Char;
                ThirdTableStr[i+1] = list.get(i).code;
            }
            listener.refreshTable(ThirdTableStr);
        }

        // 获取字符编码列表
        public List<CharString> getCodingList()
        {
            List<CharString> list = new ArrayList<CharString>();
            getCodingAux(treeList.get(0).BinTree.myRoot,list);
            return list;
        }

        private String CodingString = "";

        private void getCodingAux(BST<Node>.BinNode<Node> subtreeRoot,List<CharString> list)
        {
            if (subtreeRoot != null)
            {
                if (subtreeRoot.Left == null && subtreeRoot.Right == null)
                {
                    list.add(new CharString(subtreeRoot.Data.codeChar.Char, CodingString));
                }
                CodingString += "0";
                getCodingAux(subtreeRoot.Left,list);
                CodingString = CodingString.substring(0,CodingString.length() - 1);

                CodingString += "1";
                getCodingAux(subtreeRoot.Right,list);
                CodingString = CodingString.substring(0,CodingString.length() - 1);
            }
        }

        public void run() {
            AddListCodeProc("开始构造哈夫曼编码!");
            doWork();
            AddListCodeProc("构造哈夫曼编码完成!");
            ShowCodeResult();
            ++signal;
        }
    }

    public void drawTree(Canvas canvas) {
        for (int i = 0; i < treeList.size(); i++) {
            System.out.println("test");
            BST<Node> bst = treeList.get(i).BinTree;
            DrawingTree_GreedyAlgHuffmanCode(bst,bst.myRoot, treeLocationList.get(i), canvas);
        }
    }

    //递归先序画图,哈夫曼编码使用
    private void DrawingTree_GreedyAlgHuffmanCode(BST<Node> subtree, BST<Node>.BinNode<Node> subtreeRoot, Point subtreeRootLocation, Canvas canvas) {
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float textHeight=(-fontMetrics.ascent-fontMetrics.descent)/2;
        if (subtreeRoot != null)
        {
            //先画根
            Node node = subtreeRoot.Data;

            int height = subtree.heightAux(subtreeRoot) - 1;
//            int width = subtree.middleWidthAux(subtreeRoot) + 1;
            //int SpaceLenth = BaseSpaceLenth + height * height  * 15;
            //int Angle = BaseAngle + height * height * 10;
            int SpaceLenth , Angle;

            if (height<3){
                SpaceLenth = 15 + height * 15;
                Angle = 20 + height * 7;
            } else {
                SpaceLenth = 25 + height * 17;
                Angle = 25 + height * 7;
            }

            int radius = node.CircleRadius;
            Point center = subtreeRootLocation;
            //画圆圈
            mPaint.setColor(Color.BLUE);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(center.x,center.y,radius,mPaint);
            //画值
            mPaint.setColor(Color.RED);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setTextSize(32);
            canvas.drawText(String.valueOf(node.data),center.x,center.y+textHeight,mPaint);
            //画编码字符
            if (node.isLeaf) {
                canvas.drawText(String.valueOf(node.codeChar.Char),center.x,center.y+textHeight+40,mPaint);
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
                canvas.drawLine(ArrowHeadStartX,ArrowHeadStartY,ArrowHeadEndX,ArrowHeadEndY,mPaint);

                //画0
                Point p = new Point((ArrowHeadStart.x + ArrowHeadEnd.x) / 2, (ArrowHeadStart.y + ArrowHeadEnd.y) / 2);
                mPaint.setColor(Color.GREEN);
                mPaint.setTextSize(25);
                canvas.drawText("0",p.x,p.y,mPaint);
            }

            //再画左子树
            DrawingTree_GreedyAlgHuffmanCode(subtree, subtreeRoot.Left, new Point(subtreeRootLocation.x - tempX, subtreeRootLocation.y + tempY),canvas);

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
                canvas.drawLine(ArrowHeadStartX,ArrowHeadStartY,ArrowHeadEndX,ArrowHeadEndY,mPaint);

                //画1
                Point p = new Point((ArrowHeadStart.x + ArrowHeadEnd.x) / 2, (ArrowHeadStart.y + ArrowHeadEnd.y) / 2);
                mPaint.setColor(Color.GREEN);
                canvas.drawText("1",p.x,p.y,mPaint);
            }

            //再画右子树
            DrawingTree_GreedyAlgHuffmanCode(subtree, subtreeRoot.Right, new Point(subtreeRootLocation.x + tempX, subtreeRootLocation.y + tempY),canvas);
        }
    }


    public void refresh() {
        postInvalidate();
    }

    public void reset(){
        isRunning = false;
        isGetCodeTable = false;
        isStepOver = false;
        CodeCharNum = 0;
        codeCharList.clear();
        treeList.clear();
        treeLocationList.clear();
        signal = 0;
        listener.clearAll();
    }

    public interface HuffmanCodeActionLister {
        //步骤跟踪输出
        void addListCodeProc(String newProc);
        //清空proclist
        void clearAll();
        //刷新表格结果
        void refreshTable(String[] ThirdTableStr);
    }

    public void setHuffmanCodeActionLister(HuffmanCodeActionLister ls) {
        listener = ls;
    }
}
