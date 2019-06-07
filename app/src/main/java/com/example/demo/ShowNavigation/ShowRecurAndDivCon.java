package com.example.demo.ShowNavigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.demo.AlgRecurAndDivCon.Intro.IntroRecurAndDivCon;
import com.example.demo.AlgRecurAndDivCon.evo.RecurAndDivConBinSearchActivity;
import com.example.demo.AlgRecurAndDivCon.evo.RecurAndDivConMergeSortActivity;
import com.example.demo.AlgRecurAndDivCon.evo.RecurAndDivConQuickSortActivity;
import com.example.demo.AlgRecurAndDivCon.evo.RecurAndDivConTowerOfHanoiActivity;
import com.example.demo.MainActivity;
import com.example.demo.R;

public class ShowRecurAndDivCon extends Activity implements View.OnClickListener {
    private Button bt1,bt2,bt3,bt4;
    private Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_recur_divcon);

        initView();
    }

    private void initView(){
        bt1 = (Button)findViewById(R.id.bt_tower_of_hanoi);
        bt2 = (Button)findViewById(R.id.bt_bin_search);
        bt3 = (Button)findViewById(R.id.bt_merge_sort);
        bt4 = (Button)findViewById(R.id.bt_quick_sort);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
        bt4.setOnClickListener(this);
    }

    @Override
    public void onClick(View whichbtn) {
        // TODO Auto-generated method stub

        switch (whichbtn.getId()) {
            case R.id.bt_tower_of_hanoi:
                intent.setClass(ShowRecurAndDivCon.this,  RecurAndDivConTowerOfHanoiActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_bin_search:
                intent.setClass(ShowRecurAndDivCon.this,  RecurAndDivConBinSearchActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_merge_sort:
                intent.setClass(ShowRecurAndDivCon.this,  RecurAndDivConMergeSortActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_quick_sort:
                intent.setClass(ShowRecurAndDivCon.this,  RecurAndDivConQuickSortActivity.class);
                startActivity(intent);
                break;
        }
    }
}
