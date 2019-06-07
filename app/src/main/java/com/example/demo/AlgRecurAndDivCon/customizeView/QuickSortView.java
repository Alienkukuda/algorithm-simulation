package com.example.demo.AlgRecurAndDivCon.customizeView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.example.demo.AlgRecurAndDivCon.entity.DataShowHelper;
import com.example.demo.AlgRecurAndDivCon.entity.UnderLineHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author captain
 * @Description quicksort自定义view
 */
public class QuickSortView extends View {
    public int signal = 0;
    public Paint mPaint;
    //屏幕宽度
    private static int ScreenWidth;
    //数据列表
    public int[] dataArray = null;
//    public List<Integer> dataList = new ArrayList<Integer>();
    public List<DataShowHelper> dataShowHelperList = new ArrayList<DataShowHelper>();
    public List<UnderLineHelper> underLineHelperList = new ArrayList<UnderLineHelper>();
    //是否获取数据
    public boolean isGetData = false;
    //是否开始排序
    public boolean isSorting = false;
    //是否全速运行
    public boolean isStepOver = false;

    //回调函数
    private QuickSortActionListener listener;

    //重置showHelper方法的参数
    private static int reset[] = {-1};

    public class ThreadRun implements Runnable {
        private Object lock;

        public ThreadRun(Object lock) {
            this.lock = lock;
        }

        public void QuickSort(int[] a, int p, int r) {
            AddListCodeProc("p = " + p + ", r = " + r);
            if (p < r)
            {
                AddListCodeProc("对位置" + p + "到位置" + r + "排序");
                int q = Partition(a, p, r); //以a[p]分基准为左右两半
                QuickSort(a, p, q - 1); //对左半段排序
                QuickSort(a, q + 1, r); //对右半段排序
                refresh();
                ClearDataShowHelper();
                AddListCodeProc("位置" + p + "到位置" + r + "排序完成!");
            }
        }

        int Partition(int[] a, int p, int r)
        {
            int[] indexArray = new int[2];
            ClearDataShowHelper();

            int i = p, j = r+1;
            int x = a[p];
            System.out.println(x);

            //显示基准
            AddListCodeProc("当前基准：" + x);
            indexArray[0] = p;
            indexArray[1] = -1;
            SetShowSquareIndex(indexArray);
            int zc = 0;


            //将<x的交换到左边区域，>x的交换到 右边区域
            while (true)
            {
                AddListCodeProc(String.format("当前基准:%d，开始查找比%d大的元素", x, x));

                while (a[++i] < x && i < r)
                {
                    indexArray[0] = i;
                    AddListCodeProc(String.format("当前基准:%d，查找比%d大的元素",x,x));
//                    for (DataShowHelper helper:dataShowHelperList) {
//                        System.out.println((zc++)+","+helper.isShowArrowHead+","+helper.isShowSquare+","+helper.isShowCircle);
//                    }
                    SetShowArrowHeadIndex(indexArray);
                }
                indexArray[0] = i;
                SetShowArrowHeadIndex(indexArray);
                PauseSorting();

                AddListCodeProc(String.format("当前基准:%d，开始查找比%d小的元素", x, x));

                while (a[--j] > x)
                {
                    AddListCodeProc(String.format("当前基准:%d，查找比%d小的元素", x, x));
                    indexArray[0] = i;
                    indexArray[1] = j;
                    SetShowArrowHeadIndex(indexArray);
                }
                indexArray[1] = j;
                SetShowArrowHeadIndex(indexArray);
                PauseSorting();

                if (i >= j)
                {
                    break;
                }
                PauseSorting();

                AddListCodeProc("当前基准：" + x + String.format("，开始交换元素"));
                SetShowArrowHeadIndex(reset);
                SetShowCircleIndex(indexArray);

                AddListCodeProc("当前基准：" + x + String.format("，交换元素:%d<->%d", a[i], a[j]));
                //swap(a[i], a[j]);
                int temp = a[i];
                a[i] = a[j];
                a[j] = temp;
                SetShowCircleIndex(reset);
            }
            AddListCodeProc(String.format("把基准%d移到中间", a[p]));
            SetShowArrowHeadIndex(reset);
            indexArray[0] = p;
            indexArray[1] = j;
            SetShowCircleIndex(indexArray);

            a[p] = a[j];
            a[j] = x;
            PauseSorting();
            return j;
        }

        public synchronized void PauseSorting() {
            try {
                synchronized (lock){
                    lock.wait();
                }
            }catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
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

        public void doWork() {
            InitAllDataShow();
            QuickSort(dataArray, 0, dataArray.length - 1);
        }

        public void run(){
            doWork();
//            isStepOver = false;
            ++signal;
        }
    }

    //初始化
    public QuickSortView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        InitAllDataShow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        if (isSorting) {
            DrawingAllDataShow(canvas);
        }
    }

    public void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(2);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(50);
        ScreenWidth = getResources().getDisplayMetrics().widthPixels;
    }


    //清空排序辅助图形显示
    public void ClearDataShowHelper() {
        SetShowSquareIndex(reset);
        SetShowCircleIndex(reset);
        SetShowArrowHeadIndex(reset);
    }

    public void InitAllDataShow() {
        int LocX = 75;
        int LocY = 125;
        if (dataArray != null){
            for (int i = 0;i < dataArray.length; i++) {
                DataShowHelper helper = new DataShowHelper(new Point(LocX, LocY), false, false, false);
                dataShowHelperList.add(helper);
                LocX += 80;
            }

        }
    }

    public void DrawingAllDataShow(Canvas canvas) {
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float textHeight=(-fontMetrics.ascent-fontMetrics.descent)/2;
        int index = 0;
        for (DataShowHelper helper:dataShowHelperList) {
            Point center = helper.CenterLocation;
            int side_length = helper.CircleRadius;
            //变线
            mPaint.setStyle(Paint.Style.STROKE);
            if (helper.isShowCircle) {
                mPaint.setColor(Color.BLACK);
                canvas.drawCircle(center.x,center.y,side_length,mPaint);
            }
            if (helper.isShowSquare) {
                mPaint.setColor(Color.BLUE);
                canvas.drawRect(center.x-side_length,center.y-side_length,center.x+side_length,center.y+side_length,mPaint);
            }
            if (helper.isShowArrowHead) {
                mPaint.setColor(Color.GREEN);
                Point point = new Point(center.x, center.y + side_length);
                canvas.drawLine(point.x,point.y,point.x,point.y+100,mPaint);
                canvas.drawLine(point.x,point.y,point.x-30,point.y+30,mPaint);
                canvas.drawLine(point.x,point.y,point.x+30,point.y+30,mPaint);
            }
            mPaint.setColor(Color. RED);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(String.valueOf(dataArray[index++]),helper.CenterLocation.x,helper.CenterLocation.y+textHeight,mPaint);
        }
    }

    //设置显示圆圈的位置
    public void SetShowCircleIndex(int[] indexArray) {
        if (indexArray.length == 1 && indexArray[0] ==-1){
            for (DataShowHelper helper:dataShowHelperList)
                helper.isShowCircle = false;
        } else {
            for (int i = 0; i < dataShowHelperList.size() ; i++) {
                boolean isInIndex = false;
                for (int j=0; j < indexArray.length ; j++) {
                    if (i == indexArray[j])
                        isInIndex = true;
                }
                dataShowHelperList.get(i).isShowCircle = isInIndex;
            }
        }
        refresh();
    }

    //设置显示方框的位置
    public void SetShowSquareIndex(int[] indexArray) {
        if (indexArray.length == 1 && indexArray[0] ==-1){
            for (DataShowHelper helper:dataShowHelperList)
                helper.isShowSquare = false;
        } else {
            for (int i = 0; i < dataShowHelperList.size() ; i++) {
                boolean isInIndex = false;
                for (int j=0; j < indexArray.length ; j++) {
                    if (i == indexArray[j])
                        isInIndex = true;
                }
                dataShowHelperList.get(i).isShowSquare = isInIndex;
            }
        }
        refresh();
    }

    //设置显示箭头的位置
    public void SetShowArrowHeadIndex(int[] indexArray) {
        if (indexArray.length == 1 && indexArray[0] ==-1){
            for (DataShowHelper helper:dataShowHelperList)
                helper.isShowArrowHead = false;
        } else {
            for (int i = 0; i < dataShowHelperList.size() ; i++) {
                boolean isInIndex = false;
                for (int j=0; j < indexArray.length ; j++) {
                    if (i == indexArray[j])
                        isInIndex = true;
                }
                dataShowHelperList.get(i).isShowArrowHead = isInIndex;
            }
        }
        refresh();
    }

    public void reset() {
        isSorting = false;
        isGetData = false;
        isStepOver = false;
        signal = 0;
        dataShowHelperList.clear();
//        dataList.clear();
        dataArray = null;
        refresh();
        listener.clearAll();
    }

    //刷新界面
    public void refresh() {
        postInvalidate();
    }

    public interface QuickSortActionListener {
        //步骤跟踪输出
        void addListCodeProc(String newProc);
        //清空proclist
        void clearAll();

    }

    public void setQuickSortActionListener(QuickSortActionListener ls) {
        listener = ls;
    }
}
