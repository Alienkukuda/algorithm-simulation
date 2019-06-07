package com.example.demo.ShowNavigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.demo.AlgDynamic.evo.Dynamic0And1KProbActivity;
import com.example.demo.AlgDynamic.evo.DynamicCpotActivity;
import com.example.demo.AlgDynamic.evo.DynamicMatrixChainMulActivity;
import com.example.demo.AlgGreedy.evo.GreedyHuffmanCodeActivity;
import com.example.demo.AlgGreedy.evo.GreedySSSPPActivity;
import com.example.demo.R;

public class ShowGreedy extends Activity implements View.OnClickListener {
    private Button bt1,bt2;
    private Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_greedy);

        initView();
    }

    private void initView(){
        bt1 = (Button)findViewById(R.id.bt_huffman_code);
        bt2 = (Button)findViewById(R.id.bt_ssspp);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
    }

    @Override
    public void onClick(View whichbtn) {
        // TODO Auto-generated method stub

        switch (whichbtn.getId()) {
            case R.id.bt_huffman_code:
                intent.setClass(ShowGreedy.this,  GreedyHuffmanCodeActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_ssspp:
                intent.setClass(ShowGreedy.this,  GreedySSSPPActivity.class);
                startActivity(intent);
                break;
        }
    }
}
