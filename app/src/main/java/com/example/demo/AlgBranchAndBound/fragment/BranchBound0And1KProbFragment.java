package com.example.demo.AlgBranchAndBound.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.demo.FileUtil.FileUtil;
import com.example.demo.R;

import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class BranchBound0And1KProbFragment extends Fragment {
    private TextView tv;
    private FileUtil fileUtil = FileUtil.getInstance();

    public BranchBound0And1KProbFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_branch_bound0and1k_prob, container, false);
        //获取tv
        tv = (TextView) view.findViewById(R.id.tv);
        //读取txt
        InputStream inputStream = getResources().openRawResource(R.raw.branch_bound0and1k_prob);
        String s = fileUtil.getString(inputStream);
        tv.setText(s);
        return view;
    }

}
