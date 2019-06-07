package com.example.demo.AlgDynamic.customizeView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.example.demo.AlgDynamic.entity.ThreePoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CpotView extends View {

    public Paint mPaint;
    //是否获取矩阵数据
    public boolean isGetVertex = false;
    //是否开始
    public boolean isRunning = false;
    //是否全速运行
    public boolean isStepOver = false;

    //多边形个数
    public int VertexNum;
    public List<Point> VertexPointList = new ArrayList<Point>();
    //是否开始剖分三角形
    public boolean isTriangulationProc = false;

    public List<ThreePoint> TriangulationProcList = new ArrayList<ThreePoint>();


    //屏幕宽度
    private static int ScreenWidth;


    //初始化
    public CpotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        if (isGetVertex) {
            drawPolygon(canvas);
        }
    }

    public void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(2);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(50);
        ScreenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    //初始化顶点
    public void GetPolygon() {
        int Radius = 120;
        int LocX = 500;
        int LocY = 250;
        Point center = new Point(LocX, LocY);
        int baseAngle = 360 / VertexNum;
        double pi = 3.1415926f;
        Random random = new Random();
        for (int i = 0; i < VertexNum; i++) {
            double tempSin = Math.sin(baseAngle * i / 180.0 * pi);
            double tempCos = Math.cos(baseAngle * i / 180.0 * pi);

            int X = (int)(center.x + tempSin * (Radius + random.nextInt(45)));
            int Y = (int)(center.y - tempCos * (Radius + random.nextInt(45)));

            VertexPointList.add(new Point(X, Y));
        }
    }

    public void drawPolygon(Canvas canvas) {
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float textHeight=(-fontMetrics.ascent-fontMetrics.descent)/2;
        for (int i = 0; i < VertexNum; i++) {
            Point point = new Point(VertexPointList.get(i).x,VertexPointList.get(i).y);
            mPaint.setColor(Color.RED);
            mPaint.setStyle(Paint.Style.FILL);
            //画点
            canvas.drawPoint(point.x,point.y,mPaint);
            //画顶点名称

            int Xadder = point.x >= 500 ? 30 : -30;
            int Yadder = point.y >= 250 ? 30 : -30;
            canvas.drawText("v" + i,point.x + Xadder,point.y + Yadder +textHeight,mPaint);

            //画直线
            if (i != 0)
            {
                mPaint.setColor(Color. BLUE);
                canvas.drawLine(VertexPointList.get(i-1).x,VertexPointList.get(i-1).y,
                        VertexPointList.get(i).x,VertexPointList.get(i).y,mPaint);
            }
        }
        //画最后一条直线
        if (VertexNum > 2)
        {
            canvas.drawLine(VertexPointList.get(VertexNum-1).x,VertexPointList.get(VertexNum-1).y,
                    VertexPointList.get(0).x,VertexPointList.get(0).y,mPaint);
        }

        //画三角剖分部分
        if (isTriangulationProc)
        {
            for (ThreePoint p:TriangulationProcList){
                mPaint.setColor(p.color);
                canvas.drawLine(VertexPointList.get(p.index1).x,VertexPointList.get(p.index1).y,
                        VertexPointList.get(p.index2).x,VertexPointList.get(p.index2).y,mPaint);
                canvas.drawLine(VertexPointList.get(p.index2).x,VertexPointList.get(p.index2).y,
                        VertexPointList.get(p.index3).x,VertexPointList.get(p.index3).y,mPaint);
                canvas.drawLine(VertexPointList.get(p.index1).x,VertexPointList.get(p.index1).y,
                        VertexPointList.get(p.index3).x,VertexPointList.get(p.index3).y,mPaint);
            }
        }
    }

    public void refresh() {
        postInvalidate();
    }


}
