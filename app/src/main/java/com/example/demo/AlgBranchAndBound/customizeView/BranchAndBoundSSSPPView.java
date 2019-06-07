package com.example.demo.AlgBranchAndBound.customizeView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.example.demo.AlgGreedy.entity.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BranchAndBoundSSSPPView extends View {
    public Paint mPaint;
    //屏幕宽度
    private static int ScreenWidth;
    //是否生成有向图
    public boolean isGetGraph = false;
    //顶点个数
    public int VertexNum = 0;
    //保存所有顶点信息
    public List<Vertex> vertexList = new ArrayList<Vertex>();
    //源头顶点到顶点i的最短特殊路径长度
    public int[] dist = null;
    //邻接矩阵
    public int[][] c = null;
    //大圆半径
    int Radius = 250;
    //最大值
    public static int MaxValue = 2147483647;

    //初始化
    public BranchAndBoundSSSPPView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        if (isGetGraph) {
//            initGraph();
            drawGraph(canvas);
        }
    }

    public void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mPaint.setStrokeWidth(2);
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void drawGraph(Canvas canvas) {
//        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
//        float textHeight = (fontMetrics.descent - fontMetrics.ascent)/2;;
        mPaint.setTextSize(50);
        int arrowSize = 20;
        //首先画所有顶点
        for (int i = 0; i < vertexList.size(); i++)
        {
            mPaint.setColor(Color.RED);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setTextSize(50);

            Vertex vertex = vertexList.get(i);
            canvas.drawCircle(vertex.Location.x,vertex.Location.y,vertex.Radius,mPaint);
            mPaint.setColor(Color.BLUE);
            mPaint.setStyle(Paint.Style.FILL);
            //画圈内数字
            canvas.drawText(String.valueOf(vertex.Index),vertex.Location.x,vertex.Location.y+15,mPaint);
        }

        //画邻接矩阵的边和权值
        Random rgb = new Random();
        int red;
        int green;
        int blue;
        for (int i = 0; i < VertexNum; i++) {
            for (int j = 0; j < VertexNum; j++) {
                //如果权值不是-1,画从i顶点指向j顶点的箭头
                if (this.c[i][j] > 0 && this.c[i][j] < MaxValue){
                    Point start = vertexList.get(i).Location;  //起始顶点圆心坐标
                    Point end = vertexList.get(j).Location;    //终止顶点圆心坐标
                    int radius = Vertex.Radius; //顶点半径
                    double pi = 3.1415926f;
                    double dist = Math.sqrt(Math.pow(Math.abs((start.x - end.x)) * 1.0, 2.0) + Math.pow(Math.abs((start.y - end.y)) * 1.0, 2.0));
                    double sin = Math.abs((start.x - end.x)) * 1.0 / dist;
                    double cos = Math.abs((start.y - end.y)) * 1.0 / dist;
                    int sin_radius = (int)(sin * radius);
                    int cos_radius = (int)(cos * radius);
                    Point p1 = new Point(), p2 = new Point();
                    if (start.x == end.x && start.y > end.y)   //两个顶点横坐标相同
                    {
                        p1 = new Point(start.x, start.y - cos_radius);
                        p2 = new Point(end.x, end.y + cos_radius);
                    }
                    else if (start.x == end.x && start.y < end.y)   //两个顶点横坐标相同
                    {
                        p1 = new Point(start.x, start.y + cos_radius);
                        p2 = new Point(end.x, end.y - cos_radius);
                    }
                    else if (start.y == end.y && start.x > end.x)  //两个顶点纵坐标相同
                    {
                        p1 = new Point(start.x - sin_radius, start.y);
                        p2 = new Point(end.x + sin_radius, end.y);
                    }
                    else if (start.y == end.y && start.x < end.x)  //两个顶点纵坐标相同
                    {
                        p1 = new Point(start.x + sin_radius, start.y);
                        p2 = new Point(end.x - sin_radius, end.y);
                    }
                    else if (start.x < end.x && start.y > end.y)    //箭头向右上
                    {
                        p1 = new Point(start.x + sin_radius, start.y - cos_radius);
                        p2 = new Point(end.x - sin_radius, end.y + cos_radius);
                    }
                    else if (start.x < end.x && start.y < end.y)    //箭头向右下
                    {
                        p1 = new Point(start.x + sin_radius, start.y + cos_radius);
                        p2 = new Point(end.x - sin_radius, end.y - cos_radius);
                    }
                    else if (start.x > end.x && start.y > end.y)    //箭头向左上
                    {
                        p1 = new Point(start.x - sin_radius, start.y - cos_radius);
                        p2 = new Point(end.x + sin_radius, end.y + cos_radius);
                    }
                    else if (start.x > end.x && start.y < end.y)    //箭头向左下
                    {
                        p1 = new Point(start.x - sin_radius, start.y + cos_radius);
                        p2 = new Point(end.x + sin_radius, end.y - cos_radius);
                    }

                    red = rgb.nextInt(255);
                    green = rgb.nextInt(255);
                    blue = rgb.nextInt(255);
                    int temp = Color.rgb(red, green, blue);
                    mPaint.setColor(temp);
                    mPaint.setTextSize(30);
                    //画箭头
                    DrawArrows(canvas,arrowSize,p1.x,p1.y,p2.x,p2.y);
                    //画权值
                    Point p = new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
                    canvas.drawText(String.valueOf(c[i][j]),p.x,p.y,mPaint);
                }
            }
        }
    }

    private void DrawArrows(Canvas canvas, int arrowSize, float x1,
                            float y1, float x2, float y2) {

        // 画直线
        canvas.drawLine(x1, y1, x2, y2, mPaint);

        // 箭头中的第一条线的起点
        int x3 = 0;
        int y3 = 0;

        // 箭头中的第二条线的起点
        int x4 = 0;
        int y4 = 0;

        double awrad = Math.atan(3.5 / 8);
        double[] arrXY_1 = rotateVec(x2 - x1, y2 - y1, awrad, arrowSize);
        double[] arrXY_2 = rotateVec(x2 - x1, y2 - y1, -awrad, arrowSize);

        // 第一端点
        Double X3 = Double.valueOf(x2 - arrXY_1[0]);
        x3 = X3.intValue();
        Double Y3 = Double.valueOf(y2 - arrXY_1[1]);
        y3 = Y3.intValue();

        // 第二端点
        Double X4 = Double.valueOf(x2 - arrXY_2[0]);
        x4 = X4.intValue();
        Double Y4 = Double.valueOf(y2 - arrXY_2[1]);
        y4 = Y4.intValue();

        canvas.drawLine(x3, y3, x2, y2, mPaint);
        canvas.drawLine(x4, y4, x2, y2, mPaint);
    }

    private double[] rotateVec(float px, float py, double ang, int arrowSize) {
        double mathstr[] = new double[2];
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);
        double d = Math.sqrt(vx * vx + vy * vy);
        vx = vx / d * arrowSize;
        vy = vy / d * arrowSize;
        mathstr[0] = vx;
        mathstr[1] = vy;
        return mathstr;
    }

    public void initGraph() {
        vertexList.clear();
        int LocX = 500;
        int LocY = 325;
        Point center = new Point(LocX,LocY);
        int baseAngle = 360 / VertexNum;
        double pi = 3.1415926f;
        for (int i = 0; i < VertexNum; i++)
        {
            double tempSin = Math.sin(baseAngle * i / 180.0 * pi);
            double tempCos = Math.cos(baseAngle * i / 180.0 * pi);

            int X = center.x + (int)(tempSin * Radius);
            int Y = center.y - (int)(tempCos * Radius);

            this.vertexList.add(new Vertex(i, new Point(X, Y)));
        }

        dist = new int[VertexNum];
        c = new int[VertexNum][VertexNum];
        Random random = new Random();
        int[] distance = {5, 5, MaxValue, 5, 10, 10, 10, MaxValue, 20, 20,
                20, 10, MaxValue, MaxValue, 15, MaxValue, 15, 20, MaxValue, 30, 30 };
        for (int i = 0; i < VertexNum; i++)
        {
            for (int j = 0; j <= i; j++)
            {
                if (j == i)
                    c[j][i] = 0;
                else
                {
                    c[j][i] = distance[random.nextInt(20)];
                    c[i][j] = MaxValue;
                }
            }
        }
    }

    public void reset() {
        VertexNum = 0;
        isGetGraph = false;
        dist = null;
        c = null;
        vertexList.clear();
        refresh();
    }

    public void refresh() {
        postInvalidate();
    }
}
