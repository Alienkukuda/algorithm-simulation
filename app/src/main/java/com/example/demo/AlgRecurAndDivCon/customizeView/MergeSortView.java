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

public class MergeSortView extends View {
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
    private MergeSortActionListener listener;

    //重置showHelper方法的参数
    private static int reset[] = {-1};

    private int indexArray[] = new int[1];
    //画下划线Y坐标每次叠加量
    private int UnderLineYAdder = 0;

    private boolean CopyInMerge = true;
    private List<Integer> MergeDataList = new ArrayList<Integer>();
    private String MergeDataMsg = "";


    public class ThreadRun implements Runnable {
        private Object lock;

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

        public synchronized void PauseSorting() {
            try {
                synchronized (lock){
                    lock.wait();
                }
            }catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }


        public void MergeSort(int[] a, int left, int right) {
            HightLightShowSingleLineCode(2);
            HightLightShowSingleLineCode(4);
            if (left < right) //至少有2个元素
            {
                ClearDataShowHelper();
                AddListCodeProc(String.format("开始对位置%d--%d排序，left = %d，right = %d", left, right,left,right));
                int middle = (left + right) / 2;  //取中点将数组分成左右两半
                HightLightShowSingleLineCode(7);
                AddListCodeProc("middle = " + middle);

                AddListCodeProc(String.format("对位置%d--%d排序，left = %d，right = %d", left, middle, left, middle));

                int length = 35;
                //计算下划线起点坐标
                Point center1 = dataShowHelperList.get(left).CenterLocation;
                Point p1 = new Point(center1.x - length,center1.y + length + UnderLineYAdder);

                //计算下划线终点坐标
                Point center2 = dataShowHelperList.get(middle).CenterLocation;
                Point p2 = new Point(center2.x + length,center2.y + length + UnderLineYAdder);

                UnderLineYAdder += 5;
                UnderLineHelper helper1 = new UnderLineHelper(p1, p2);
                underLineHelperList.add(helper1);
                refresh();

                PauseSorting();

                HightLightShowSingleLineCode(10);
                MergeSort(a, left, middle);  //对左边的排序
                AddListCodeProc(String.format("位置%d--%d排序完成", left, middle));

                underLineHelperList.remove(helper1);
                refresh();
                UnderLineYAdder -= 5;

                AddListCodeProc(String.format("对位置%d--%d排序，left = %d，right = %d", middle + 1, right,middle + 1,right));

                //计算下划线起点坐标
                center1 = dataShowHelperList.get(middle + 1).CenterLocation;
                p1 = new Point(center1.x - length, center1.y + length + UnderLineYAdder);

                //计算下划线终点坐标
                center2 = dataShowHelperList.get(right).CenterLocation;
                p2 = new Point(center2.x + length, center2.y + length + UnderLineYAdder);

                UnderLineYAdder += 5;
                UnderLineHelper helper2 = new UnderLineHelper(p1, p2);
                underLineHelperList.add(helper2);
                refresh();

                PauseSorting();

                HightLightShowSingleLineCode(13);
                MergeSort(a, middle + 1, right);  //对右边的排序
                AddListCodeProc(String.format("位置%d--%d排序完成", middle + 1, right));

                underLineHelperList.remove(helper2);
                refresh();
                UnderLineYAdder -= 5;

                HightLightShowSingleLineCode(16);
                int[] b = new int[right - left + 1];//申请一个保存数据的空间

                PauseSorting();

                HightLightShowSingleLineCode(19);

                Merge(a, b, left, middle, right);  //将左右排序结果合并到数组b
                PauseSorting();
                CopyInMerge = false;

                HightLightShowSingleLineCode(24);
                Copy(a, b, left, 0, right - left + 1); //将排序结果b复制回数组a，从b[0]开始赋值到a[left],共赋值right-left+1个元素
                HightLightShowSingleLineCode(25);

                AddListCodeProc(String.format("位置%d--%d排序完成",left,right));
            }
        }

        public void Merge(int[] a,int[] b, int left, int middle, int right)
        {
            HightLightShowSingleLineCode(30);

            HightLightShowSingleLineCode(32);
            int i=0, left1=middle+1;
            AddListCodeProc("i = " + i + "，left1 = " + left1);

            MergeDataMsg = String.format("对%d--%d部分数据和%d--%d部分数据合并", left, middle, middle + 1, right);
            AddListCodeProc(MergeDataMsg);

            MergeDataList.clear();
            MergeDataMsg += "\n合并数据：";

            HightLightShowSingleLineCode(33);
            while(left<=middle || left1<=right)
            {
                HightLightShowSingleLineCode(35);
                if(left==middle+1)
                {
                    CopyInMerge = true;
                    HightLightShowSingleLineCode(38);
                    Copy(b, a, i, left1, right - left1 + 1); break; //从a数组元素left1开始赋值
                }
                HightLightShowSingleLineCode(40);
                if(left1==right+1)
                {
                    CopyInMerge = true;
                    HightLightShowSingleLineCode(43);
                    Copy(b, a, i, left, middle - left + 1); break; //从a数组元素left开始赋值
                }
                HightLightShowSingleLineCode(45);
                if(a[left]>a[left1])
                {
                    b[i]=a[left1];
                    HightLightShowSingleLineCode(47);

                    SetShowCircleIndex(reset);
                    indexArray[0] =left1;
                    SetShowCircleIndex(indexArray);
                    PauseSorting();
                    MergeDataList.add(a[left1]);
                    MergeDataMsg += a[left1] + "  ";
                    AddListCodeProc(MergeDataMsg);
                    PauseSorting();

                    left1++;
                    HightLightShowSingleLineCode(48);

                    i++;
                    HightLightShowSingleLineCode(49);
                }
                else
                {
                    HightLightShowSingleLineCode(52);

                    b[i]=a[left];
                    HightLightShowSingleLineCode(53);

                    SetShowCircleIndex(reset);
                    indexArray[0] = left;
                    SetShowCircleIndex(indexArray);
                    MergeDataList.add(a[left]);
                    PauseSorting();
                    MergeDataMsg += a[left] + "  ";
                    AddListCodeProc(MergeDataMsg);
                    PauseSorting();

                    left++;
                    HightLightShowSingleLineCode(54);

                    i++;
                    HightLightShowSingleLineCode(55);
                }
            }
            SetShowCircleIndex(reset);
        }

        //将src赋值到dest(从srcLeft到srcRight)，共size个元素
        public void Copy(int[] dest, int[] src, int destLeft, int srcLeft, int size)
        {
            for (int i = srcLeft; i < srcLeft + size; i++)
            {
                if (CopyInMerge)
                {
                    SetShowCircleIndex(reset);
                    indexArray[0] = i;
                    SetShowCircleIndex(indexArray);
                    PauseSorting();
                    MergeDataList.add(src[i]);
                    MergeDataMsg += src[i] + "  ";
                    AddListCodeProc(MergeDataMsg);
                    PauseSorting();
                }
                dest[destLeft++] = src[i];
            }
        }

        public void doWork() {
            InitAllDataShow();
            indexArray[0] = 999;
            SetShowArrowHeadIndex(indexArray);
            MergeSort(dataArray, 0, dataArray.length - 1);
        }

        public void run(){
            doWork();
            ++signal;
        }
    }

    //初始化
    public MergeSortView(Context context, AttributeSet attrs) {
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
    }



    //初始化数据显示
    public void InitAllDataShow() {
        int LocX = 75;
        int LocY = 125;
        if (dataArray != null){
            for (int i = 0;i < dataArray.length; i++) {
                DataShowHelper helper = new DataShowHelper(new Point(LocX, LocY), false, false, false,false);
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
                mPaint.setColor(Color.rgb(128,128,0));
                canvas.drawCircle(center.x,center.y,side_length,mPaint);
            }
            if (helper.isShowSquare) {
                mPaint.setColor(Color.BLUE);
                canvas.drawRect(center.x-side_length,center.y-side_length,center.x+side_length,center.y+side_length,mPaint);
            }
            if (helper.isShowArrowHead) {
                mPaint.setColor(Color.GREEN);
                Point point = new Point(center.x, center.y + side_length + 30);
                canvas.drawLine(point.x,point.y,point.x,point.y+100,mPaint);
                canvas.drawLine(point.x,point.y,point.x-30,point.y+30,mPaint);
                canvas.drawLine(point.x,point.y,point.x+30,point.y+30,mPaint);
            }
//            if (helper.isShowUnderLine) {
//                mPaint.setColor(Color.BLACK);
//                canvas.drawLine(center.x-side_length,center.y+side_length,center.x+side_length,center.y+side_length,mPaint);
//            }
            mPaint.setColor(Color. RED);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(String.valueOf(index),helper.CenterLocation.x,helper.CenterLocation.y+side_length+170+textHeight,mPaint);
            canvas.drawText(String.valueOf(dataArray[index++]),helper.CenterLocation.x,helper.CenterLocation.y+textHeight,mPaint);
        }

        for (UnderLineHelper helper:underLineHelperList)
        {
            //变线
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.BLACK);
            canvas.drawLine(helper.StartLocation.x,helper.StartLocation.y,helper.EndLocation.x,helper.EndLocation.y,mPaint) ;
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
        } else if (indexArray.length == 1 && indexArray[0] == 999){
            for (DataShowHelper helper:dataShowHelperList)
                helper.isShowArrowHead = true;
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
        dataShowHelperList.clear();
        underLineHelperList.clear();
        MergeDataList.clear();
        MergeDataMsg = "";
        CopyInMerge = true;
        signal = 0;
//        dataList.clear();
        dataArray = null;
        refresh();
        listener.HightLightShowSingleLineCode(0);
        listener.clearAll();
    }

    //刷新界面
    public void refresh() {
        postInvalidate();
    }

    public interface MergeSortActionListener {
        //代码高亮
        void HightLightShowSingleLineCode(int index);
        //步骤跟踪输出
        void addListCodeProc(String newProc);
        //清空proclist
        void clearAll();
    }

    public void setMergeSortActionListener(MergeSortActionListener ls) {
        listener = ls;
    }
}
