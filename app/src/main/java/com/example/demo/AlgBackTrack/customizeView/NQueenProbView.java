package com.example.demo.AlgBackTrack.customizeView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.example.demo.AlgBackTrack.entity.DrawColorIndex;

import java.util.ArrayList;
import java.util.List;

public class NQueenProbView extends View {
    public Paint mPaint;
    public int signal = 0;
    //皇后个数
    public int QueenNum = 0;
    //方案表格
    public String[][] tableShowStr = null;
    //解决方案个数
    public int SolutionSum = 0;
    //当前解
    public int[] x = null;
    //解决方案列表
    public List<List<Integer>> SolutionList = new ArrayList<List<Integer>>();
    public List<DrawColorIndex> DrawColorIndexList = new ArrayList<DrawColorIndex>();
    //是否获取皇后个数
    public boolean isGetQueenNum = false;
    private int PixelAdder = 120;
    //屏幕宽度
    private static int ScreenWidth;

    private NQueenActionLister listener;

    //初始化
    public NQueenProbView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        if (isGetQueenNum) {
            drawTable(canvas);
        }
    }

    public void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(2);
        mPaint.setTextAlign(Paint.Align.CENTER);
        ScreenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    //画图层
    private void drawTable(Canvas canvas) {
        Point start = new Point(100, 50);
        DrawColorIndex temp = null;
        //画方格
        for (int i = 0; i < QueenNum; i++)
        {
            for (int j = 0; j < QueenNum; j++)
            {
                Point point = new Point(start.x + i * PixelAdder, start.y + j * PixelAdder);
                for (DrawColorIndex p:DrawColorIndexList){
                    if (p.DrawBackGroundColorX == j+1&&p.DrawBackGroundColorY == i+1){
                        temp = p;
                    }
                }
                if (temp != null && temp.DrawBackGroundColorX != -1 && temp.DrawBackGroundColorY != -1)
                {
                    mPaint.setColor(Color.BLUE);
                    mPaint.setStyle(Paint.Style.FILL);
                    canvas.drawRect(point.x,point.y,point.x+PixelAdder,point.y+PixelAdder,mPaint);
                    temp = null;
                }else {
                    mPaint.setColor(Color.BLACK);
                    mPaint.setStyle(Paint.Style.STROKE);
                    canvas.drawRect(point.x,point.y,point.x+PixelAdder,point.y+PixelAdder,mPaint);
                }
            }
        }
    }

    public class ThreadRun implements Runnable {
        private Object lock;

        public ThreadRun(Object lock) {
            this.lock = lock;
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

        public synchronized void AddListCodeProc(String str) {
            listener.addListCodeProc(str);

            try {
                synchronized (lock) {
                    lock.wait();
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }

        public void doWork() {
            tableShowStr = new String[20][QueenNum+1];
            for (int i = 0; i < QueenNum + 1; i++){
                if (i != 0){
                    tableShowStr[0][i] = i + "";
                }
            }
            tableShowStr[0][0] = "皇后";
            nQueen();
            ++signal;
        }

        public void run() {
            doWork();
        }

        private void nQueen() {
            x = new int[QueenNum + 1];
            for (int i = 0; i <= QueenNum; i++)
            {
                x[i] = 0;
            }
            SolutionSum = 0;
            Backtrack(1);
            DrawColorIndexList.clear();
            AddListCodeProc("解决方案查找完毕!一共有" + SolutionSum + "种解决方案");
            refresh();
        }

        //n皇后约束函数
        private boolean Place(int k)
        {
            for (int j = 1; j < k; j++)
                if ((Math.abs(k - j) == Math.abs(x[j] - x[k])) || (x[j] == x[k]))
                    return false;
            return true;
        }

        //n皇后核心函数
        private void Backtrack(int t)
        {
            if (t > QueenNum)
            {
                AddListCodeProc(String.format("t = %d,大于%d，解决方案数加1",t,QueenNum));

                //this.DrawColorIndexList.Clear();
                //PauseAndNextStep();

                SolutionSum++; //解方案数加1
                List<Integer> temp = new ArrayList<Integer>();
                for (int i = 0; i < x.length; i++)
                {
                    temp.add(x[i]);
                }
                SolutionList.add(temp);

                ShowSolution(temp, SolutionSum);
            }
            else
            {
                for (int i = 1; i <= QueenNum; i++)
                {
                    DrawColorIndex temp = null;
                    //先把所有大于t的清除
                    for (DrawColorIndex p:DrawColorIndexList){
                        if (p.DrawBackGroundColorX == t) {
                            temp = p;
                        }
                    }
                    while (temp != null)
                    {
                        DrawColorIndexList.remove(temp);
                        temp = null;
                        for (DrawColorIndex p:DrawColorIndexList){
                            if (p.DrawBackGroundColorX >= t) {
                                temp = p;
                            }
                        }
                    }

                    x[t] = i;
                    AddListCodeProc(String.format("t = %d", t) + String.format(",将皇后%d放在%d行%d列中", t, t, x[t]));
                    DrawColorIndex willInsert = new DrawColorIndex(t, i);
                    DrawColorIndexList.add(willInsert);
                    refresh();
                    Pause();

                    if (Place(t))
                    {
                        AddListCodeProc(String.format("t = %d",t) + String.format(",皇后%d放在%d行%d列满足条件，进行下一步递归", t, t, x[t]));

                        Backtrack(t + 1);
                    }
                    else
                    {
                        AddListCodeProc(String.format("皇后%d放在%d行%d列不满足条件,移除", t, t, x[t]));
                        DrawColorIndexList.remove(willInsert);
                        refresh();
                        Pause();
                    }
                }
            }
        }

        // 把解决方案显示在dataGridViewSolution上
        private void  ShowSolution(List<Integer> temp, int rowIndex){

            for (int i = 0; i < temp.size(); i++){
                tableShowStr[rowIndex][i] = i==0? "方案" + rowIndex : String.valueOf(temp.get(i));
            }
            listener.refreshTable(rowIndex);
        }
    }

    public void refresh() {
        postInvalidate();
    }

    public void clear() {
        QueenNum = 0;
        SolutionSum = 0;
        x = null;
        SolutionList.clear();
        isGetQueenNum = false;
        signal = 0;
    }

    public interface NQueenActionLister {
        //步骤跟踪输出
        void addListCodeProc(String newProc);
        //清空proclist
        void clearAll();
        //刷新表格结果
        void refreshTable(int rowIndex);
    }

    public void setHuffmanCodeActionLister(NQueenActionLister ls) {
        listener = ls;
    }
}
