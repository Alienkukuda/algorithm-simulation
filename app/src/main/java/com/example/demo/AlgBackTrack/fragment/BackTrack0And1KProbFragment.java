package com.example.demo.AlgBackTrack.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.demo.FileUtil.FileUtil;
import com.example.demo.R;

import java.io.InputStream;


public class BackTrack0And1KProbFragment extends Fragment {
    private TextView tv;
    private FileUtil fileUtil = FileUtil.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 展开布局
        View view = inflater.inflate(R.layout.fragment_back_track0and1k_prob, container, false);
        //获取tv
        tv = (TextView) view.findViewById(R.id.tv);
        //读取txt
        InputStream inputStream = getResources().openRawResource(R.raw.back_track0and1k_prob);
        String s = fileUtil.getString(inputStream);
        tv.setText(s);
        return view;
    }
}
