package com.example.demo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.demo.FileUtil.FileUtil;

import java.io.InputStream;

public class AboutSystemActivity extends Activity {
    private TextView tv_intro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_system);
        initView();
        initAction();
    }

    public void initView() {
        tv_intro = (TextView)findViewById(R.id.tv_intro);
    }

    public void initAction() {
        FileUtil fileUtil = FileUtil.getInstance();
        //读取txt
        InputStream inputStream = getResources().openRawResource(R.raw.about_system);
        String s = fileUtil.getString(inputStream);
        tv_intro.setText(s);

    }
}
