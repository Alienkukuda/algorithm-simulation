package com.example.demo.ShowNavigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.demo.AlgDynamic.evo.Dynamic0And1KProbActivity;
import com.example.demo.AlgDynamic.evo.DynamicCpotActivity;
import com.example.demo.AlgDynamic.evo.DynamicMatrixChainMulActivity;
import com.example.demo.AlgRecurAndDivCon.evo.RecurAndDivConBinSearchActivity;
import com.example.demo.AlgRecurAndDivCon.evo.RecurAndDivConMergeSortActivity;
import com.example.demo.AlgRecurAndDivCon.evo.RecurAndDivConQuickSortActivity;
import com.example.demo.AlgRecurAndDivCon.evo.RecurAndDivConTowerOfHanoiActivity;
import com.example.demo.R;

public class ShowDynamic extends Activity implements View.OnClickListener {
    private Button bt1,bt2,bt3;
    private Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_dynamic);

        initView();
    }

    private void initView(){
        bt1 = (Button)findViewById(R.id.bt_matrix_chain_mul);
        bt2 = (Button)findViewById(R.id.bt_cpot);
        bt3 = (Button)findViewById(R.id.bt_0and1k_pro);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
    }

    @Override
    public void onClick(View whichbtn) {
        // TODO Auto-generated method stub

        switch (whichbtn.getId()) {
            case R.id.bt_matrix_chain_mul:
                intent.setClass(ShowDynamic.this,  DynamicMatrixChainMulActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_cpot:
                intent.setClass(ShowDynamic.this,  DynamicCpotActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_0and1k_pro:
                intent.setClass(ShowDynamic.this,  Dynamic0And1KProbActivity.class);
                startActivity(intent);
                break;
        }
    }
}
