package com.example.demo.AlgRecurAndDivCon.customizeView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.example.demo.AlgRecurAndDivCon.entity.ArrowHead;
import com.example.demo.AlgRecurAndDivCon.entity.Element;
import com.example.demo.AlgRecurAndDivCon.entity.TextBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Author captain
 * @Description 二分搜索技术自定义view
 */
public class BinSearchView extends View {
    public int signal = 0;
    public Paint mPaint;
    //元素个数
    public int elementSum = 0;
    //要查找的元素
    public int findNum;
    //元素列表
    public List<Element> elementList = new ArrayList<Element>();
    //数据列表
    public List<Integer> dataList = new ArrayList<Integer>();
    //箭头
    public ArrowHead arrowHead;
    //是否获取数据
    public boolean isGetData = false;
    //是否开始运行
    public boolean isRunning = false;
    //是否全速运行
    public boolean isStepOver = false;

    //屏幕宽度
    private static int ScreenWidth;
    //点击响应按钮会ondraw，定义view的显示标志
    private boolean isDisplay = false;

    //回调函数
    private BinSearchActionListener listener;

    public class ThreadRun implements Runnable {
        private Object lock;

        public ThreadRun(Object lock) {
            this.lock = lock;
        }

        public int BinarySearch(List<Element> list, int findNum) {
            HightLightShowSingleLineCode(2);
            int count = 0;
            int l = 0,r = list.size();
            while (l <= r) {
                HightLightShowSingleLineCode(4);
                AddListCodeProc(String.format("l = %d, r = %d", l, r));

                int m = (l+r)/2;
                HightLightShowSingleLineCode(6);
                AddListCodeProc(String.format("m = %d",m));
                //箭头指向m位置
                Point point = new Point(elementList.get(m).centerLocation.x,elementList.get(m).centerLocation.y + elementList.get(m).circleRadius);
                arrowHead.arrowHeadLocation = point;
                arrowHead.isDisplay = true;
                refresh();
                //查找次数
                count ++;
                AddListCodeProc("当前比较元素位置：" + m + ", 当前位置的值：" + list.get(m).num + "  查找次数：" + count);
                if (findNum == list.get(m).num)
                {
                    HightLightShowSingleLineCode(7);
                    return m;
                }
                if (findNum < list.get(m).num)
                {
                    r = m - 1;
                    HightLightShowSingleLineCode(8);
                    AddListCodeProc(String.format("r = m - 1,r = %d",r));
                }
                else {
                    l = m + 1;
                    HightLightShowSingleLineCode(9);
                    AddListCodeProc(String.format("r = m + 1,r = %d",l));
                }
            }
            HightLightShowSingleLineCode(11);
            return -1;
        }

        @SuppressLint("NewApi")
        public void doWork() {
            AddListCodeProc("显示数据!!!");
            isDisplay = true;
            SetAllElementLocation();
            refresh();
            AddListCodeProc("对数据排序!!!");
            Collections.sort(dataList);
            SetAllElementLocation();
            refresh();
            AddListCodeProc("开始查找!!!");
            int result = BinarySearch(elementList, findNum);
            if (result == -1)
            {
                AddListCodeProc("查找失败!!!");
            }
            else
            {
                AddListCodeProc("查找成功!!!");
            }
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

        public synchronized void HightLightShowSingleLineCode(int index) {//代码高亮

           /*
            这里写调用listView.performItemClick的方法
           */
            if (!(listener == null)){
                listener.HightLightShowSingleLineCode(index);
            }
            //线程暂停等点击下一步
            try {
                synchronized (lock){
                    lock.wait();
                }
            }catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }

        public void run() {
            doWork();
//            isStepOver = false;
            ++signal;
        }
    }

    //初始化
    public BinSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        SetAllElementLocation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        if (isDisplay) {
            DrawingAllElement(canvas);
            DrawinArrowHead(canvas);
        }
    }

    public void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(2);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(50);
        ScreenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    //初始化所有元素
    public void SetAllElementLocation() {
        elementList.clear();
        int LocX = 75;
        int LocY = 125;
        for (int i:dataList)
        {
            Element ele = new Element(i, false, new Point(LocX, LocY));
            this.elementList.add(ele);
            LocX += 80;
        }
        arrowHead = new ArrowHead(new Point(0, 0));
    }

    //画所有元素
    public void DrawingAllElement(Canvas canvas) {
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float textHeight=(-fontMetrics.ascent-fontMetrics.descent)/2;
        for (Element element:elementList)
        {
           mPaint.setColor(Color.RED);
           mPaint.setStyle(Paint.Style.STROKE);
           canvas.drawCircle(element.centerLocation.x,element.centerLocation.y,element.circleRadius,mPaint);
           mPaint.setColor(Color. BLUE);
           mPaint.setStyle(Paint.Style.FILL);
           canvas.drawText(String.valueOf(element.num),element.centerLocation.x,element.centerLocation.y+textHeight,mPaint);
        }
    }

    //画箭头
    public void DrawinArrowHead(Canvas canvas) {
        mPaint.setColor(Color.GREEN);
        if (this.arrowHead.isDisplay)
        {
            Point point = arrowHead.arrowHeadLocation;
            canvas.drawLine(point.x,point.y,point.x,point.y+100,mPaint);
            canvas.drawLine(point.x,point.y,point.x-30,point.y+30,mPaint);
            canvas.drawLine(point.x,point.y,point.x+30,point.y+30,mPaint);
        }
    }

    public void reset() {
        isRunning = false;
        isGetData = false;
        isStepOver = false;
        elementList.clear();
        dataList.clear();
        arrowHead.isDisplay = false;
        isDisplay = false;
        signal =0;
        refresh();
        listener.clearAll();
        //代码第一行高亮，重置
        listener.HightLightShowSingleLineCode(0);
    }

    //刷新界面
    public void refresh() {
        postInvalidate();
    }

    public interface BinSearchActionListener {
        //代码高亮
        void HightLightShowSingleLineCode(int index);
        //步骤跟踪输出
        void addListCodeProc(String newProc);
        //清空proclist
        void clearAll();
    }

    public void setBinSearchActionListener(BinSearchActionListener ls) {
        listener = ls;
    }
}
