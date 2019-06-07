package com.example.demo.AlgBackTrack.Intro;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.example.demo.AlgBackTrack.fragment.BackTrack0And1KProbFragment;
import com.example.demo.AlgBackTrack.fragment.BackTrackFragment;
import com.example.demo.AlgBackTrack.fragment.BackTrackLoadingProbFragment;
import com.example.demo.AlgBackTrack.fragment.BackTrackNQueenProbFragment;
import com.example.demo.R;
import com.example.demo.adapter.MyFragmentPagerAdapter;

import java.util.ArrayList;

/**
 * @Author captain
 * @Description 回溯法介绍主activity，利用ViewPager+Fragment代替FragmentManager+Fragment
 */
public class IntroBackTrack extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    //    private FragmentPagerAdapter mAdapter;
    private ViewPager mViewPager;
    //fragment的集合，对应每个子页面
    private ArrayList<Fragment> fragments;
    /**
     * 底部四个按钮
     */
    private Button tab1;
    private Button tab2;
    private Button tab3;
    private Button tab4;
    //所有按钮的集合
    private Button[] btnArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_backtrack);
        //获取控件
        initView();
    }

    private void initView()
    {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        tab1 = (Button)findViewById(R.id.button1);
        tab2 = (Button)findViewById(R.id.button2);
        tab3 = (Button)findViewById(R.id.button3);
        tab4 = (Button)findViewById(R.id.button4);
        btnArgs = new Button[]{tab1,tab2,tab3,tab4};

        mViewPager.setOnPageChangeListener(this);
        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);
        tab3.setOnClickListener(this);
        tab4.setOnClickListener(this);

        fragments = new ArrayList<Fragment>();
        fragments.add(new BackTrackFragment());
        fragments.add(new BackTrackLoadingProbFragment());
        fragments.add(new BackTrackNQueenProbFragment());
        fragments.add(new BackTrack0And1KProbFragment());
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(adapter);

        resetButtonColor();
        tab1.setTextColor(Color.RED);


    }

    //重置所有按钮的颜色
    private void resetButtonColor(){
        tab1.setBackgroundColor(Color.parseColor("#DCDCDC"));
        tab2.setBackgroundColor(Color.parseColor("#DCDCDC"));
        tab3.setBackgroundColor(Color.parseColor("#DCDCDC"));
        tab4.setBackgroundColor(Color.parseColor("#DCDCDC"));
        tab1.setTextColor(Color.BLACK);
        tab2.setTextColor(Color.BLACK);
        tab3.setTextColor(Color.BLACK);
        tab4.setTextColor(Color.BLACK);
    }

    @Override
    public void onClick(View whichbtn) {
        // TODO Auto-generated method stub

        switch (whichbtn.getId()) {
            case R.id.button1:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.button2:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.button3:
                mViewPager.setCurrentItem(2);
                break;
            case R.id.button4:
                mViewPager.setCurrentItem(3);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub
        //每次滑动首先重置所有按钮的颜色
        resetButtonColor();
        //将滑动到的当前按钮颜色设置为红色
        btnArgs[arg0].setTextColor(Color.RED);
    }
}
