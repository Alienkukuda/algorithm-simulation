package com.example.demo.ShowNavigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.demo.AlgBranchAndBound.evo.BranchAndBound0And1KProbActivity;
import com.example.demo.AlgBranchAndBound.evo.BranchAndBoundLoadingProbActivity;
import com.example.demo.AlgBranchAndBound.evo.BranchAndBoundSSSPPActivity;
import com.example.demo.R;

public class ShowBranchAndBound extends Activity implements View.OnClickListener {
    private Button bt1,bt2,bt3;
    private Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_branch_bound);

        initView();
    }

    private void initView(){
        bt1 = (Button)findViewById(R.id.bt_ssspp);
        bt2 = (Button)findViewById(R.id.bt_loading_prob);
        bt3 = (Button)findViewById(R.id.bt_0and1k_pro);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
    }

    @Override
    public void onClick(View whichbtn) {
        // TODO Auto-generated method stub

        switch (whichbtn.getId()) {
            case R.id.bt_ssspp:
                intent.setClass(ShowBranchAndBound.this, BranchAndBoundSSSPPActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_loading_prob:
                intent.setClass(ShowBranchAndBound.this, BranchAndBoundLoadingProbActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_0and1k_pro:
                intent.setClass(ShowBranchAndBound.this, BranchAndBound0And1KProbActivity.class);
                startActivity(intent);
                break;
        }
    }
}
