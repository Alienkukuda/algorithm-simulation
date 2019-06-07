package com.example.demo.AlgBackTrack.evo;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.demo.AlgBackTrack.customizeView.BackTrack0And1KProbView;
import com.example.demo.AlgBackTrack.entity.BpTextBox;
import com.example.demo.AlgBackTrack.entity.ValuePerWeight;
import com.example.demo.FileUtil.FileUtil;
import com.example.demo.R;
import com.example.demo.adapter.ListViewAdapter;
import com.example.demo.thread.ThreadNotify;
import com.example.demo.view.BottomScrollView;
import com.example.demo.view.TableView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class BackTrack0And1KProbActivity extends Activity implements View.OnClickListener {
    private int signal = 0;
    private TableView table,tableBest,tablePer;
    private Button bt_nextstep,bt_stepover,bt_stop,bt_start,bt_get_data;
    private EditText et_input,et_bp_capacity;
    private ListView lv_code_list,lv_list_code_proc;
    private TextView tv_intro,tv_current_weight,tv_current_value,tv_current_best_value;
    //锁对象
    private final Object lock = new Object();
    private Thread thread1,thread2;
    //计时器
    private Timer timer;
    private TimerTask timerTask;
    //弹窗
    private AlertDialog.Builder builder;
    //程序步骤listview所需属性
    private int count = 0;
    Handler handler;
    private Message msg;
    private BottomScrollView mScrollView;
    private ListViewAdapter mListViewAdapter;
    private boolean isSvToBottom = false;
    private float mLastY;
    /**
     * listview竖向滑动的阈值
     */
    private static final int THRESHOLD_Y_LIST_VIEW = 20;
    private List<String> procItem = new ArrayList<String>();

    //是否获取物品数据
    private boolean isGetItemData = false;
    //是否运行
    private boolean isRunning = false;
    //是否全速运行
    private boolean isStepOver = false;
    //物品个数
    private int ItemNum = 0;
    //背包容量
    private int c = 0;
    //原始物品重量数组
    private int[] sourse_w = null;
    //排序后物品重量数组
    private int[] w = null;
    //原始物品价值数组
    private int[] sourse_p = null;
    //排序后物品价值数组
    private int[] p = null;
    //当前重量
    private int cw = -1;
    //当前价值
    private int cp = -1;
    //当前最优价值
    private int bestp = -1;
    //当前解
    private int[] x = null;
    //初始表
    String[][] tableStr = null;
    //单位质量价值表
    String[][] tablePerStr = null;
    //当前解表
    String[][] tableBestStr = null;

    private static final String[] strs = new String[] {
            "//0-1背包问题的回溯实现-核心函数BackTrack",
            "void Knap::BackTrack(int i) ",
            "{ ",
            "    if(i > n) ",
            "    { ",
            "\tif(bestp < cp) ",
            "\t    bestp = cp; ",
            "\treturn; ",
            "    } ",
            "    if(cw + w[i] <= c)//记录进入左子树 ",
            "    {",
            "\tx[i] = 1; ",
            "\tcw += w[i]; ",
            "\tcp += p[i]; ",
            "\tBackTrack(i + 1); ",
            "\tcw -= w[i]; ",
            "\tcp -= p[i]; ",
            "    } ",
            "    if(Bound(i+1) > bestp)//记录进入右子树 ",
            "    {",
            "\tx[i] = 0; ",
            "\tBackTrack(i + 1); ",
            "    } ",
            "}",
            "",
            "//0-1背包问题的上界函数",
            "template<class Typew, class Typep>",
            "Typep Knap<Typew, Typep>::Bound(int i)  //计算上界",
            "{",
            "    Typew cleft = c - cw; // 剩余容量",
            "    Typep b = cp;",
            "    while (i <= n && w[i] <= cleft) {//以物品单位重量价值递减序装入物品",
            "\tcleft -= w[i];",
            "\tb += p[i];",
            "\ti++;",
            "    }",
            "    //装满背包",
            "    if (i <= n) ",
            "\tb += p[i] / w[i]  * cleft;",
            "    return b;",
            "}"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_track0and1k_prob);
        initView();
        initAction();
        fixSlideConflict();
    }

    public void initView() {
        mScrollView = (BottomScrollView) findViewById(R.id.main_scroll_view);
        table = (TableView) findViewById(R.id.table);
        table.setColumnCount(9);
        tablePer = (TableView) findViewById(R.id.table_per);
        tablePer.setColumnCount(9);
        tableBest = (TableView) findViewById(R.id.table_best);
        tableBest.setColumnCount(9);
        bt_start = (Button)findViewById(R.id.bt_start);
        bt_nextstep = (Button)findViewById(R.id.bt_nextstep);
        bt_stepover = (Button)findViewById(R.id.bt_stepover);
        bt_stop = (Button)findViewById(R.id.bt_stop);
        bt_get_data = (Button)findViewById(R.id.bt_get_data);
        et_input = (EditText)findViewById(R.id.et_input);
        et_bp_capacity = (EditText)findViewById(R.id.et_bp_capacity);
        lv_code_list = (ListView)findViewById(R.id.lv_code_list);
        lv_list_code_proc = (ListView)findViewById(R.id.lv_code_list_prod);
        tv_intro = (TextView)findViewById(R.id.tv_intro);
        tv_current_weight = (TextView)findViewById(R.id.tv_current_weight);
        tv_current_value = (TextView)findViewById(R.id.tv_current_value);
        tv_current_best_value = (TextView)findViewById(R.id.tv_current_best_value);

        builder =  new AlertDialog.Builder(this);

        /*为ListView设置Adapter来绑定数据*/
        lv_code_list.setAdapter(new ArrayAdapter<String>(this, R.layout.code_list_item,R.id.list_item_tv, strs));
        lv_code_list.setDividerHeight(0);

        /*程序步骤*/
        mListViewAdapter = new ListViewAdapter(this, procItem);
        lv_list_code_proc.setAdapter(mListViewAdapter);

        //中断线程初始化
        ThreadNotify threadNotify = new ThreadNotify(lock);
        thread2 = new Thread(threadNotify);

        bt_nextstep.setOnClickListener(this);
        bt_stepover.setOnClickListener(this);
        bt_stop.setOnClickListener(this);
        bt_start.setOnClickListener(this);
        bt_get_data.setOnClickListener(this);

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        tablePer.clearTableContents()
                                .setHeader(tablePerStr[0])
                                .addContent(tablePerStr[1])
                                .addContent(tablePerStr[2])
                                .refreshTable();
                        break;
                    case 2:
                        String newProcWithCount = (count++) + ":" +msg.obj;
                        procItem.add(newProcWithCount);
                        mListViewAdapter.notifyDataSetChanged();
                        int index = procItem.size() - 1;
                        lv_list_code_proc.performItemClick(lv_list_code_proc.getChildAt(index), index, lv_list_code_proc.getItemIdAtPosition(index));
                        break;
                    case 3:
                        BpTextBox bp = (BpTextBox)msg.obj;
                        tv_current_weight.setText(String.valueOf(bp.textBoxCurrentWeight));
                        tv_current_value.setText(String.valueOf(bp.textBoxCurrentValue));
                        tv_current_best_value.setText(String.valueOf(bp.textBoxCurrentBestValue));
                        break;
                    case 4:
                        tableBest.clearTableContents()
                                .setHeader(tableBestStr[0])
                                .addContent(tableBestStr[1])
                                .refreshTable();
                        break;
                    case 5:
                        count = 0;
                        procItem.clear();
                        mListViewAdapter.notifyDataSetChanged();
                        tv_current_weight.setText("");
                        tv_current_value.setText("");
                        tv_current_best_value.setText("");
                        et_input.setText("");
                        et_bp_capacity.setText("");
                        table.clearTableContents()
                                .refreshTable();
                        tablePer.clearTableContents()
                                .refreshTable();
                        tableBest.clearTableContents()
                                .refreshTable();
                        break;
                }
            }
        };
    }

    public void initAction() {
        FileUtil fileUtil = FileUtil.getInstance();
        //读取txt
        InputStream inputStream = getResources().openRawResource(R.raw.back_track0and1k_prob_redu);
        String s = fileUtil.getString(inputStream);
        tv_intro.setText(s);

        mScrollView.setScrollToBottomListener(new BottomScrollView.OnScrollToBottomListener() {
            @Override
            public void onScrollToBottom() {
                isSvToBottom = true;
            }

            @Override
            public void onNotScrollToBottom() {
                isSvToBottom = false;
            }
        });
    }

    public void fixSlideConflict() {
// ListView滑动冲突解决
        lv_list_code_proc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                if(action == MotionEvent.ACTION_DOWN) {
                    mLastY = event.getY();
                }
                if(action == MotionEvent.ACTION_MOVE && procItem.size()!=0) {
                    int top = lv_list_code_proc.getChildAt(0).getTop();
                    float nowY = event.getY();
                    if(!isSvToBottom) {
                        // 允许scrollview拦截点击事件, scrollView滑动
                        mScrollView.requestDisallowInterceptTouchEvent(false);
                    } else if(top == 0 && nowY - mLastY > THRESHOLD_Y_LIST_VIEW) {
                        // 允许scrollview拦截点击事件, scrollView滑动
                        mScrollView.requestDisallowInterceptTouchEvent(false);
                    } else {
                        // 不允许scrollview拦截点击事件， listView滑动
                        mScrollView.requestDisallowInterceptTouchEvent(true);
                    }
                }
                return false;
            }
        });
    }

    public class ThreadRun implements Runnable {
        private Object lock;

        public ThreadRun(Object lock) {
            this.lock = lock;
        }

        public synchronized void AddListCodeProc(String str) {
            msg = new Message();
            msg.what = 2;
            msg.obj = str;
            handler.sendMessage(msg);

            try {
                synchronized (lock){
                    lock.wait();
                }
            }catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }

        public synchronized void Pause() {
            try {
                synchronized (lock){
                    lock.wait();
                }
            }catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }

        private int Knapsack(int[] pt, int[] wt,int n)
        {
            w = new int[n + 1];
            p = new int[n + 1];
            int W = 0;
            int P = 0;
            List<ValuePerWeight> Q = new ArrayList<ValuePerWeight>();
            for (int i = 1; i <= n; i++)
            {
                Q.add(new ValuePerWeight());
                Q.get(i - 1).ID = i;
                Q.get(i - 1).d = 1.0 * pt[i] / wt[i];
                P += pt[i];
                W += wt[i];
            }
            if (W <= c) return P;  //装入所有物品

            //依物品单位重量价值排序
            Collections.sort(Q, new Comparator<ValuePerWeight>() {
                @Override
                public int compare(ValuePerWeight x,ValuePerWeight y) {
                    return (x.d  > y.d ) ? -1 : ((x.d  == y.d ) ? 0 : 1);
                }
            });
            p = new int[n + 1];
            w = new int[n + 1];
            x = new int[n + 1];
            for (int i = 1; i <= n; i++)
            {
                p[i] = pt[Q.get(i - 1).ID];
                w[i] = wt[Q.get(i - 1).ID];
            }


            AddListCodeProc("显示单位重量价值排序数组");

            ShowItemArraySorted();

            cp = 0;
            cw = 0;
            bestp = 0;

            UpdateData();

            AddListCodeProc("开始搜索");

            this.Backtrack(1);

            return bestp;
        }

        public void ShowItemArraySorted() {
            for (int i = 0; i <= ItemNum; i++) {
                if (i == 0){
                    tablePerStr[0][i] = "序号";
                    tablePerStr[1][i] = "重量";
                    tablePerStr[2][i] = "价值";
                } else {
                    tablePerStr[0][i] = i + "";
                    tablePerStr[1][i] = w[i] + "";
                    tablePerStr[2][i] = p[i] + "";
                }
            }
            msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
        }

        private void Backtrack(int i)
        {
            AddListCodeProc("搜索到第" + i + "层节点");

            if (i > ItemNum)
            {
                AddListCodeProc(String.format("i = %d,大于背包个数%d", i, ItemNum));
                if (bestp < cp)
                {
                    AddListCodeProc(String.format("当前最优价值bestp = %d,小于 当前价值cp = %d", bestp, cp));

                    bestp = cp;

                    AddListCodeProc("更新当前最优价值");
                }
                else
                {
                    AddListCodeProc(String.format("当前最优价值bestp = %d,大于或等于 当前价值cp = %d,不更新当前最优价值", bestp, cp));
                }
                return;
            }
            if (cw + w[i] <= c) //记录进入左子树
            {
                AddListCodeProc(String.format("当前重量cw=%d,排序后物品重量w[%d]=%d,cw+w[%d] = %d, <=背包容量c=%d", cw, i, w[i], i, cw + w[i], c));

                x[i] = 1;

                UpdateCurrentSolution();

                cw += w[i];
                cp += p[i];

                UpdateData();

                Backtrack(i + 1);

                cw -= w[i];
                cp -= p[i];

                UpdateData();

                AddListCodeProc(String.format("左子树搜索完毕，返回上一层,i=%d", i));
            }
            if (Bound(i + 1) > bestp)   //记录进入右子树
            {
                AddListCodeProc(String.format("上界Bound(%d)=%d,大于当前最优价值bestp=%d", i + 1, Bound(i + 1),bestp));

                x[i] = 0;

                UpdateCurrentSolution();

                Backtrack(i + 1);

                AddListCodeProc(String.format("右子树搜索完毕，返回上一层,i=%d", i));
            }
        }

        private int Bound(int i)
        {
            int cleft = c - cw; // 剩余容量
            int b = cp;
            while (i <= ItemNum && w[i] <= cleft)
            {   //以物品单位重量价值递减序装入物品
                cleft -= w[i];
                b += p[i];
                i++;
            }
            // 装满背包
            if (i <= ItemNum)
            {
                //b += p[i] / w[i] * cleft;
                b += (int)((p[i] * 1.0 / w[i]) * cleft);
            }
            return b;
        }
        //boxtext
        private void UpdateData()
        {
            BpTextBox bpTextBox = new BpTextBox(cw,cp,bestp);
            msg = new Message();
            msg.what = 3;
            msg.obj = bpTextBox;
            handler.sendMessage(msg);
            Pause();
        }

        private void UpdateCurrentSolution() {
            for (int i = 0; i <= ItemNum; i++) {
                if (i == 0){
                    tableBestStr[0][i] = "序号";
                    tableBestStr[1][i] = "取值";
                } else {
                    tableBestStr[0][i] = i + "";
                    tableBestStr[1][i] = x[i] + "";
                }
            }
            msg = new Message();
            msg.what = 4;
            handler.sendMessage(msg);
        }

        public void doWork() {
            int result = -1;
            AddListCodeProc("程序开始");
            result = Knapsack(sourse_p, sourse_w, ItemNum);
            AddListCodeProc("搜索结束,最优价值=" + result);
        }

        public void run() {
            doWork();
            ++signal;
        }
    }

    public void initTable() {
        sourse_w = new int[this.ItemNum + 1];
        sourse_p = new int[this.ItemNum + 1];
        tableStr = new String[3][ItemNum+1];
        tablePerStr = new String[3][ItemNum+1];
        tableBestStr = new String[2][ItemNum + 1];
        Random random = new Random();
        for (int i = 0; i <= ItemNum; i++){
            if (i != 0) {
                sourse_w[i] = random.nextInt(5) + 1;
                sourse_p[i] = random.nextInt(5) + 1;
            }
        }

        for (int i = 0; i < ItemNum+1; i++) {
            tableStr[0][i] = i + "";
            tableStr[1][i] = sourse_w[i] + "";
            tableStr[2][i] = sourse_p[i] + "";
        }
        tableStr[0][0] = "序号";
        tableStr[1][0] = "重量";
        tableStr[2][0] = "价值";
    }

    public void showTable() {
        table.clearTableContents()
                .setHeader(tableStr[0])
                .addContent(tableStr[1])
                .addContent(tableStr[2])
                .refreshTable();
    }

    public void reset(){
        isRunning = false;
        isGetItemData = false;
        isStepOver = false;
        c = 0;
        ItemNum = 0;
        signal = 0;
        tableStr = null;
        tablePerStr = null;
        tableBestStr = null;
        sourse_w = null;
        w = null;
        sourse_p = null;
        p = null;
        cw = -1;
        cp = -1;
        bestp = -1;
        x = null;
        msg = new Message();
        msg.what = 5;
        handler.sendMessage(msg);
    }

    @Override
    public void onClick(View v) {
        String str = et_input.getText().toString();
        String KnapsackVolumeStr = et_bp_capacity.getText().toString();
        Pattern pattern = Pattern.compile("^[0-9]*$");
        if (pattern.matcher(str).matches() && !str.equals("")){
            ItemNum = Integer.parseInt(str);
        }
        switch (v.getId()) {
            case R.id.bt_get_data:
                if (!isGetItemData) {
                    if (ItemNum >=1 && ItemNum <= 8) {
                        isGetItemData =true;
                        initTable();
                        showTable();
                    } else if (ItemNum > 8) {
                        builder.setTitle("提示")
                                .setMessage("亲，个数太多了！")
                                .setPositiveButton("确定" ,  null )
                                .show();
                    } else {
                        builder.setTitle("提示")
                                .setMessage("亲，请正确填写物品件数！")
                                .setPositiveButton("确定" ,  null )
                                .show();
                    }
                } else {
                    builder.setTitle("提示")
                            .setMessage("亲，已经获取数据！")
                            .setPositiveButton("确定" ,  null )
                            .show();
                }
                break;
            case R.id.bt_start:
                if (isGetItemData) {
                    if (pattern.matcher(KnapsackVolumeStr).matches() && !KnapsackVolumeStr.equals("")) {
                        c = Integer.parseInt(KnapsackVolumeStr);
                        if (c < 1) {
                            builder.setTitle("提示")
                                    .setMessage("亲，背包容量过小！")
                                    .setPositiveButton("确定" ,  null )
                                    .show();
                        } else if (c >15) {
                            builder.setTitle("提示")
                                    .setMessage("亲，背包容量过大！")
                                    .setPositiveButton("确定" ,  null )
                                    .show();
                        } else {
                            if (!isRunning) {
                                c = Integer.parseInt(KnapsackVolumeStr);
                                isRunning = true;
                                thread1 = new Thread(new ThreadRun(lock));
                                thread1.start();
                            } else {
                                builder.setTitle("提示")
                                        .setMessage("亲，重新开始请停止运行！")
                                        .setPositiveButton("确定" ,  null )
                                        .show();
                            }
                        }
                    }else {
                        builder.setTitle("提示")
                                .setMessage("亲，请正确填写背包容量！")
                                .setPositiveButton("确定" ,  null )
                                .show();
                    }
                } else {
                    builder.setTitle("提示")
                            .setMessage("亲，请先获取数据！")
                            .setPositiveButton("确定" ,  null )
                            .show();
                }
                break;
            case R.id.bt_stepover:
                if (isRunning) {
                    isStepOver = true;
                }
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread (new Runnable() {
                            public void run() {
                                if (isStepOver && signal == 0) {
                                    bt_nextstep.performClick();
                                }
                                else {
                                    if (timer != null) {
                                        timer.cancel();
                                        timer = null;
                                    }

                                    if (timerTask != null) {
                                        timerTask.cancel();
                                        timerTask = null;
                                    }
                                }
                            }
                        });
                    }
                };
                timer.schedule(timerTask,0,10);
                break;
            case R.id.bt_nextstep:
                thread2.run();
                break;
            case R.id.bt_stop:
                if (thread1 != null)
                    thread1.interrupt();
                //确保reset方法在thread1结束后执行，睡眠0.2s，不然主线程抢占线程，但是疑问的是结果不影响
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                reset();
                break;
        }
    }

}
