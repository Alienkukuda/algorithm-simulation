package com.example.demo.ShowNavigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.demo.AlgBackTrack.evo.BackTrack0And1KProbActivity;
import com.example.demo.AlgBackTrack.evo.BackTrackLoadingProbActivity;
import com.example.demo.AlgBackTrack.evo.BackTrackNQueenProbActivity;
import com.example.demo.AlgRecurAndDivCon.evo.RecurAndDivConBinSearchActivity;
import com.example.demo.AlgRecurAndDivCon.evo.RecurAndDivConMergeSortActivity;
import com.example.demo.AlgRecurAndDivCon.evo.RecurAndDivConQuickSortActivity;
import com.example.demo.AlgRecurAndDivCon.evo.RecurAndDivConTowerOfHanoiActivity;
import com.example.demo.R;

public class ShowBackTrack extends Activity implements View.OnClickListener {
    private Button bt1,bt2,bt3;
    private Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_backtrack);

        initView();
    }

    private void initView(){
        bt1 = (Button)findViewById(R.id.bt_0and1k_pro);
        bt2 = (Button)findViewById(R.id.bt_loading_prob);
        bt3 = (Button)findViewById(R.id.bt_nqueen_prob);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
    }

    @Override
    public void onClick(View whichbtn) {
        // TODO Auto-generated method stub

        switch (whichbtn.getId()) {
            case R.id.bt_0and1k_pro:
                intent.setClass(ShowBackTrack.this,  BackTrack0And1KProbActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_loading_prob:
                intent.setClass(ShowBackTrack.this,  BackTrackLoadingProbActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_nqueen_prob:
                intent.setClass(ShowBackTrack.this,  BackTrackNQueenProbActivity.class);
                startActivity(intent);
                break;
        }
    }
}
