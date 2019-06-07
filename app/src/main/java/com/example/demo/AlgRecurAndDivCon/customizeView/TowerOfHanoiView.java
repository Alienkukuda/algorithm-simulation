package com.example.demo.AlgRecurAndDivCon.customizeView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Size;
import android.view.View;

import com.example.demo.AlgRecurAndDivCon.entity.Pillar;
import com.example.demo.AlgRecurAndDivCon.entity.Plate;
import com.example.demo.AlgRecurAndDivCon.entity.TextBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
/**
 * @Author captain
 * @Description hanoi自定义view
 */

public class TowerOfHanoiView extends View {
    public int signal = 0;
    public Paint mPaint;
    //柱子1,2,3
    public Pillar pillar1,pillar2,pillar3;
    //盘子是否移动中
    public boolean isPlateSlide = false;
    //盘子列表
    public List<Plate> plateList = new ArrayList<Plate>();
    //盘子总数
    public int plateSum = 0;
    //是否开始运行
    public boolean isRunning = false;
    //是否全速运行
    public boolean isStepOver = false;
    //是否可以设置isStepOver为true，可以理解为信号量
    public boolean isChange = false;

    //计时器
    public Timer timer;
    public TimerTask task;
    //屏幕宽度
    private static int ScreenWidth;
    //移动步数
    private final static int movePix = 1;

    //move方法的全局参数
    private Plate plateA = null;
    private Point pointB;
    private Pillar pillarA;

    //回调接口
    private HanoiActionListener listener;
    //
    private TextBox textBox;

    public class ThreadRun implements Runnable {
        private Object lock;

        public ThreadRun(Object lock) {
            this.lock = lock;
        }
        //从a移到b
        @SuppressLint("NewApi")
        public synchronized void move(Pillar a,Pillar b) {
            try {
                synchronized (lock){
//                    System.out.println(isPlateSlide);
//                    System.out.println("第一次wait前");
                    lock.wait();
//        Plate plateA;
                    plateA = null;
                    pillarA = a;
                    //要移动的盘子是柱子a上最小编号的盘子
                    int minPlateIndex = 999;
                    for (int i = a.plateNumList.size(); i > 0; i--)
                    {
                        if (a.getPlateNumList().get(i - 1) < minPlateIndex)
                            minPlateIndex = a.getPlateNumList().get(i - 1);
                    }
                    for (Plate p: plateList){
                        if(p.getIndex() == minPlateIndex){
                            plateA = p;
                            break;
                        }
                    }

                    //将要移动到柱子b上的位置
                    int posY = b.getBottomLocation().y;
                    for (int i = 0; i < b.getPlateNumList().size(); i++)
                    {
                        posY -= 30;
                    }
//        Point pointB;
                    pointB = new Point(b.getBottomLocation().x, posY);

                    //将盘子从柱子a上移动到柱子b上，移动过程


                    timer = new Timer();
                    task = new TimerTask() {
                        @Override
                        public void run() {
                            isPlateSlide = true;
                            if (plateA.location.x == pillarA.TopLocation.x && plateA.location.y > pillarA.TopLocation.y) {//首先从柱子上移动到柱子顶部
                                plateA.location.y -= movePix;
                                refresh();
                            } else if (plateA.location.x < pointB.x && plateA.location.y == pillarA.TopLocation.y) {//然后从柱子a顶部移动柱子b顶部
                                plateA.location.x += movePix;
                                refresh();
                            } else if (plateA.location.x > pointB.x && plateA.location.y == pillarA.TopLocation.y) {//然后从柱子a顶部移动柱子b顶部
                                plateA.location.x -= movePix;
                                refresh();
                            } else if (plateA.location.x == pointB.x && plateA.location.y < pointB.y) {//最后从柱子b的顶部移动到柱子b上
                                plateA.location.y += movePix;
                                refresh();
                            } else {
                                isPlateSlide = false;
                                task.cancel();
                                task = null;
                            }
                        }
                    };
                    timer.schedule(task, 0, 1);

                    //将盘子从柱子a上移动到柱子b上，移动过程
                    //首先从柱子上移动到柱子顶部
//        for (int i = plateA.location.y; i >= a.getTopLocation().y; i -= movePix)
//        {
//            plateA.location.y = i;
//        }

                    //然后从柱子a顶部移动柱子b顶部
//        if (plateA.getLocation().x > pointB.x)
//        {
//            for (int i = plateA.getLocation().x; i >= pointB.x; i -= movePix)
//            {
//                plateA.location.x = i;
//            }
//        }
//        else
//        {
//            for (int i = plateA.getLocation().x; i <= pointB.x; i += movePix)
//            {
//                plateA.location.x = i;
//            }
//        }


                    //最后从柱子b的顶部移动到柱子b上
//        for (int i = plateA.getLocation().y; i <= pointB.y; i += movePix)
//        {
//            plateA.location.y = i;
//            invalidate();
//        }
                    //柱子a上的盘子数量减1
                    a.plateSum -= 1;
                    a.getPlateNumList().remove(new Integer(minPlateIndex));
                    //柱子b上的盘子数量加1
                    b.plateSum += 1;
                    b.getPlateNumList().add(minPlateIndex);
//                    System.out.println(isPlateSlide);
//                    System.out.println("第二次wait前");
                    lock.wait();
                }
            } catch (InterruptedException ie){
//                System.out.println("move方法中断了");
//                System.out.println( Thread.currentThread().getName() + "--" + Thread.currentThread().getState() + "--" + Thread.currentThread().isInterrupted());
                Thread.currentThread().interrupt();
//                System.out.println( Thread.currentThread().getName() + "--" + Thread.currentThread().getState() + "--" + Thread.currentThread().isInterrupted());
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
//                System.out.println(index+"高亮中断了");

                //保持中断状态
                Thread.currentThread().interrupt();
            }
        }
        //a为源塔，b为目的塔，c为过渡塔
        public void hanoi(int n, Pillar a, Pillar b, Pillar c) {
            HightLightShowSingleLineCode(1);    //显示函数头
            listener.addListCodeProc(String.format("n={%d},{%s}为源塔，{%s}为目的塔，{%s}为过渡塔", n,a.PillarName,b.PillarName,c.PillarName));
            textBox.setCurrentN(n);
            textBox.setSourceTower(a.PillarName);
            textBox.setMidTower(b.PillarName);
            textBox.setDestTower(c.PillarName);
            listener.updateTextBox(textBox);
            HightLightShowSingleLineCode(3);
            if (n == 1)
            {
                HightLightShowSingleLineCode(5);
                move(a, b);
            }
            else
            {
                HightLightShowSingleLineCode(7);
                HightLightShowSingleLineCode(9);
                listener.addListCodeProc(String.format("将{%s}上的{%d}个盘子借助{%s}移动到{%s}上",a.PillarName,n-1,b.PillarName,c.PillarName));
                hanoi(n - 1, a, c, b);//步骤2): 将n-1个盘从a->c,借助b来实现，此时a为源塔，c为目的塔，b为过渡塔
                HightLightShowSingleLineCode(10);
                move(a, b);       //步骤3): 将底层的一个盘从a->b
                HightLightShowSingleLineCode(11);
                listener.addListCodeProc(String.format("将{%s}上的{%d}个盘子借助{%s}移动到{%s}上", c.PillarName, n - 1, a.PillarName, b.PillarName));
                hanoi(n - 1, c, b, a);//步骤4): 将n-1个盘从c->b,借助a来实现，此时c为源塔，b为目的塔，a为过渡塔
            }
        }
        public void run() {
            hanoi(pillar1.plateSum, pillar1, pillar3, pillar2);
            isStepOver = false;
            isChange = false;
            ++signal;
//            System.out.println("我完事了");
        }
    }

    //初始化
    public TowerOfHanoiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        InitAllPillar();
        InitAllPlate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        if (isRunning){
            DrawingAllPillar(canvas);
            DrawingAllPlate(canvas);
        }
    }

    public void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ScreenWidth = getResources().getDisplayMetrics().widthPixels;
        //初始化
        textBox = new TextBox();
    }
    //初始化柱子
    public void InitAllPillar() {
        int PillarTopY = 100;
        int PillarBottomY = 490;
        int Pillar2X = ScreenWidth/2;
        int Pillar1X = 210;
        int Pillar3X = ScreenWidth-210;
        //定义三个柱子的顶部和底部Point
        pillar1 = new Pillar("A",new Point(Pillar1X, PillarTopY), new Point(Pillar1X, PillarBottomY));
        pillar2 = new Pillar("C",new Point(Pillar2X, PillarTopY), new Point(Pillar2X, PillarBottomY));
        pillar3 = new Pillar("B",new Point(Pillar3X, PillarTopY), new Point(Pillar3X, PillarBottomY));
    }
    //初始化盘子
    public void InitAllPlate() {
        pillar1.setPlateSum(plateSum);//初始化柱子1盘子数量
        int posY = pillar1.getBottomLocation().y;
        for (int i = plateSum - 1; i >= 0; i--)
        {
            Plate plate = new Plate(i, new Point(pillar1.getBottomLocation().x,posY));
            plateList.add(plate);
            pillar1.getPlateNumList().add(i);   //把盘子编号加到柱子1的盘子编号列表上
            posY -= 30;
        }
    }

    //画柱子
    public void DrawingAllPillar(Canvas canvas) {
        mPaint.setStrokeWidth(20);
        mPaint.setColor(Color.BLUE);
        canvas.drawLine(20,500,getWidth()-20,500,mPaint);
        canvas.drawLine(pillar1.getBottomLocation().x,pillar1.getBottomLocation().y,pillar1.getTopLocation().x,pillar1.getTopLocation().y,mPaint);
        canvas.drawLine(pillar2.getBottomLocation().x,pillar2.getBottomLocation().y,pillar2.getTopLocation().x,pillar2.getTopLocation().y,mPaint);
        canvas.drawLine(pillar3.getBottomLocation().x,pillar3.getBottomLocation().y,pillar3.getTopLocation().x,pillar3.getTopLocation().y,mPaint);
    }

    //画盘子
    @SuppressLint("NewApi")
    public void DrawingAllPlate(Canvas canvas) {
        mPaint.setColor(Color.RED);
        for (Plate plate: plateList){
            Point location = plate.getLocation();
            Size size = plate.getSize();
            canvas.drawLine(location.x-size.getWidth()/2,location.y-10,location.x+size.getWidth()/2,location.y-10,mPaint);
        }
    }

    //刷新界面
    public void refresh() {
        postInvalidate();
    }

    public void reset() {
        plateSum = 0;
        isRunning = false;
        signal = 0;
        plateList.clear();
        refresh();
        listener.clearAll();
        //代码第一行高亮，重置
        listener.HightLightShowSingleLineCode(0);
    }

    public interface HanoiActionListener {
        //代码高亮
        void HightLightShowSingleLineCode(int index);
        //步骤跟踪输出
        void addListCodeProc(String newProc);
        //参数跟踪
        void updateTextBox(TextBox textBox);
        //清空textbox
        void clearAll();
    }

    public void setHanoiActionListener(HanoiActionListener ls){
        this.listener = ls;
    }

}
