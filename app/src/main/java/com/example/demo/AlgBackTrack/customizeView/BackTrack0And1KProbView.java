package com.example.demo.AlgBackTrack.customizeView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class BackTrack0And1KProbView extends View {
    public Paint mPaint;
    //屏幕宽度
    private static int ScreenWidth;

    //初始化
    public BackTrack0And1KProbView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
    }

    public void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ScreenWidth = getResources().getDisplayMetrics().widthPixels;
    }
}
