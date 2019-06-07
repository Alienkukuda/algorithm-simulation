package com.example.demo.AlgDynamic.evo;

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

import com.example.demo.FileUtil.FileUtil;
import com.example.demo.R;
import com.example.demo.adapter.ListViewAdapter;
import com.example.demo.thread.ThreadNotify;
import com.example.demo.view.BottomScrollView;
import com.example.demo.view.TableView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class Dynamic0And1KProbActivity extends Activity implements View.OnClickListener {
    private int signal = 0;
    private TableView tableView,mtableView;
    private Button bt_nextstep,bt_stepover,bt_stop,bt_start,bt_get_data;
    private EditText et_input,et_bp_capacity;
    private ListView lv_code_list,lv_list_code_proc;
    private TextView tv_intro;
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
    //物品重量数组
    private int[] w = null;
    //物品价值数组
    private int[] v = null;
    //最优值数组
    private int[][] m = null;
    //背包容量
    private int KnapsackVolume = 0;
    //重量价值字符串
    String[][] TableStr = null;
    //结果字符串
    String[][] mTableStr = null;

    private static final String[] strs = new String[] {
            "//0-1背包问题：最优值m(1,c)的实现",
            "void knapsack(int v[],int *w,int c,int n,int**m)",
            "{",
            "    //1) 仅可选物品n时，容量为j的子问题的最优值",
            "    int jmax=min(w[n]-1,c); ",
            "    for(int j=0; j<=jmax; j++) ",
            "\tm[n][j]=0; //注意j为整数",
            "    for(int j=w[n]; j<=c; j++) ",
            "\tm[n][j]=v[n];",
            "    for(int i=n-1;i>1;i--)//2)逐步增加物品数至n及容量至c",
            "    { ",
            "        //仅可选物品i时，容量为j的子问题的最优值",
            "\tjmax=min(w[i]-1,c); ",
            "\tfor(int j=0; j<=jmax; j++) ",
            "\t    m[i][j]=m[i+1][j];",
            "\tfor(int j=w[i]; j<=c; j++) ",
            "\t    m[i][j]=max(m[i+1][j], m[i+1][j-w[i]]+v[i]);",
            "    }",
            "    m[1][c]=m[2][c]; //处理物品1，最后一件的边界情况",
            "    if(c>=w[1]) ",
            "\tm[1][c]=max(m[1][c],m[2][c-w[1]]+v[1]);",
            "}",
            "",
            "//0-1背包问题：构造最优解。",
            "int traceback(int **m, int *w, int c, int n, int *x)",
            "{",
            "    for(int i=1; i<n; i++) ",
            "    {",
            "\tif(m[i][c]==m[i+1][c])",
            "\t    x[i]=0; //二者相等说明物品i不装入",
            "\telse",
            "\t{",
            "\t    x[i]=1;",
            "\t    c= c- w[i];",
            "\t}",
            "    }",
            "    x[n]=(m[n][c])?1:0;",
            "}"};

//    private TableString tableString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic0and1k_prob);
        initView();
        initAction();
        fixSlideConflict();
    }

    public void initView() {
        mScrollView = (BottomScrollView) findViewById(R.id.main_scroll_view);
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
        tableView = (TableView) findViewById(R.id.table);
        tableView.setColumnCount(7);
        mtableView = (TableView) findViewById(R.id.table_m);
        mtableView.setColumnCount(17);

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
                        mTableStr = (String[][])msg.obj;
                        mtableView.clearTableContents()
                                .setHeader(mTableStr[0]);
                        for (int i = 0; i < ItemNum; i++) {
                            mtableView.addContent(mTableStr[i+1]);
                        }
                        mtableView.refreshTable();
                        break;
                    case 2:
                        String newProcWithCount = (count++) + ":" +msg.obj;
                        procItem.add(newProcWithCount);
                        mListViewAdapter.notifyDataSetChanged();
                        int index = procItem.size() - 1;
                        lv_list_code_proc.performItemClick(lv_list_code_proc.getChildAt(index), index, lv_list_code_proc.getItemIdAtPosition(index));
                        break;
                    case 3:
                        count = 0;
                        procItem.clear();
                        mListViewAdapter.notifyDataSetChanged();
                        mtableView.clearTableContents()
                                .refreshTable();
                        tableView.clearTableContents()
                                .refreshTable();
                        break;
                    case 4:
                        showTable();
                        break;
                }
            }
        };
    }
    public void initAction() {
        FileUtil fileUtil = FileUtil.getInstance();
        //读取txt
        InputStream inputStream = getResources().openRawResource(R.raw.dynamic_prog0and1k_prob_redu);
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
        //演示字符串
        String[][] mTableStr;
        int[] result;


        public ThreadRun(Object lock) {
            this.lock = lock;
        }

        public void drawTable() {
            msg = new Message();
            msg.what = 1;
            msg.obj = mTableStr;
            handler.sendMessage(msg);
        }

        public void initMTable() {
            mTableStr = new String[ItemNum+1][KnapsackVolume+2];
            result = new int[ItemNum + 1];
            m = new int[ItemNum+1][KnapsackVolume+1];

            for (int i = 0; i < ItemNum + 1; i++) {
                for (int j = 0; j < KnapsackVolume + 2; j++) {
                    if (i == 0 ){
                        mTableStr[i][j] = j - 1 + "";
                    } else {
                        mTableStr[i][j] = " ";
                    }

                }
            }
            for (int i = 1; i < ItemNum + 1 ; i++) {
                mTableStr[i][0] = i + "";
            }

        }

        public synchronized void HightLightShowSingleLineCode(int index) {//代码高亮
           /*
            这里写调用listView.performItemClick的方法
           */
            lv_code_list.performItemClick(lv_code_list.getChildAt(index), index, lv_code_list.getItemIdAtPosition(index));

            //线程暂停等点击下一步
            try {
                synchronized (lock){
                    lock.wait();
                }
            }catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
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

        public void Knapsack(int[] v,int[] w,int c,int n,int[][] m) {
            HightLightShowSingleLineCode(1);
            //1) 仅可选物品n时，容量为j的子问题的最优值
            HightLightShowSingleLineCode(4);
            int jmax=Math.min(w[n]-1,c);
            AddListCodeProc("jmax = " + jmax);

            for (int j = 0; j <= jmax; j++)
            {
                HightLightShowSingleLineCode(5);

                HightLightShowSingleLineCode(6);
                m[n][j] = 0; //注意j为整数

                AddListCodeProc("j = " + j + ",m[" + n + "][" + j + "] = 0");
                mTableStr[n][j+1] = m[n][j]+"";
                drawTable();
                Pause();
            }
            for (int j = w[n]; j <= c; j++)
            {
                HightLightShowSingleLineCode(7);

                HightLightShowSingleLineCode(8);
                m[n][j] = v[n];

                AddListCodeProc("j = " + j + ",m[" + n + "][" + j + "] = v[" + n + "] = " + m[n][j]);
                mTableStr[n][j+1] = m[n][j]+"";
                drawTable();
                Pause();
            }
            for(int i = n-1;i > 1;i--) //2) 逐步增加物品数至n及容量至c
            {
                HightLightShowSingleLineCode(9);
                AddListCodeProc("i = " + i);
                //仅可选物品i时，容量为j的子问题的最优值
                HightLightShowSingleLineCode(12);
                jmax=Math.min(w[i]-1,c);
                AddListCodeProc("jmax = " + jmax);
                for (int j = 0; j <= jmax; j++)
                {
                    HightLightShowSingleLineCode(13);
                    AddListCodeProc("j = " + j);

                    HightLightShowSingleLineCode(14);
                    m[i][j] = m[i + 1][j];
                    AddListCodeProc("m[" + i + "][" + j + "] = m[" + (i + 1) + "][" + j + "] = " + m[i][j]);

                    mTableStr[i][j+1] = m[i][j]+"";
                    drawTable();
                    Pause();
                }
                for (int j = w[i]; j <= c; j++)
                {
                    HightLightShowSingleLineCode(15);
                    AddListCodeProc("j = " + j);

                    HightLightShowSingleLineCode(16);
                    m[i][j] = Math.max(m[i + 1][j], m[i + 1][j - w[i]] + v[i]);
                    AddListCodeProc("m[" + (i+1) + "][" + j + "] = " + m[i+1][j]);
                    AddListCodeProc("m[" + (i + 1) + "][" + (j - w[i]) + "] + v[" + i + "] = " + m[i + 1][j - w[i]] + " + " + v[i] + " = " + (m[i + 1][j - w[i]] + v[i]));
                    AddListCodeProc("m[" + i + "][" + j + "] = " + m[i][j]);

                    mTableStr[i][j+1] = m[i][j]+"";
                    drawTable();
                    Pause();
                }

            }

            HightLightShowSingleLineCode(18);
            m[1][c] = m[2][c]; //处理物品1，最后一件的边界情况
            AddListCodeProc("m[1][" + c + "] = m[2][" + c + "] = " + m[1][c]);

            mTableStr[1][c+1] = m[1][c]+"";
            drawTable();
            Pause();

            if (c >= w[1])
            {
                HightLightShowSingleLineCode(19);


                HightLightShowSingleLineCode(20);
                m[1][c] = Math.max(m[1][c], m[2][c - w[1]] + v[1]);
                AddListCodeProc("m[1][" + c + "] = " + m[1][c]);
                AddListCodeProc("m[2][" + (c - w[1]) + "] + v[1] = " + m[2][c - w[1]] + " + " + v[1] + " = " + (m[2][c - w[1]] + v[1]));
                AddListCodeProc("m[1][" + c + "] = " + m[1][c]);

                mTableStr[1][c+1] = m[1][c]+"";
                drawTable();
                Pause();
            }
            for (int i = 0; i < 4; i++) {
                System.out.println(m[1][i]);
            }
            HightLightShowSingleLineCode(0);
        }

        private void  Traceback(int[][] m, int[] w, int c, int n, int[] x){
            HightLightShowSingleLineCode(24);
            for (int i = 1; i < n; i++)
            {
                HightLightShowSingleLineCode(26);

                AddListCodeProc(String.format("i=%d,n=%d", i, n));
                if (m[i][c] == m[i + 1][c])
                {
                    HightLightShowSingleLineCode(28);

                    AddListCodeProc(String.format("m[%d,%d] = %d,m[%d,%d] = %d,二者相等物品%d不装入,x[%d] = 0",i, c, m[i][c], i + 1, c, m[i + 1][c],i,i));
                    HightLightShowSingleLineCode(29);
                    x[i] = 0; //二者相等说明物品i不装入
                }
                else
                {
                    HightLightShowSingleLineCode(30);

                    HightLightShowSingleLineCode(32);
                    x[i] = 1;

                    HightLightShowSingleLineCode(33);
                    c = c - w[i];

                    AddListCodeProc(String.format("m[%d,%d] = %d,m[%d,%d] = %d,二者不相等物品%d装入,x[%d] = 1",i, c, m[i][c], i + 1, c, m[i + 1][c], i, i));
                }
            }

            HightLightShowSingleLineCode(36);
            x[n] = (m[n][c] != 0) ? 1 : 0;

            AddListCodeProc(String.format("最后一个，m[{0},{1}] = {2},x[{3}] = {4}",n,c,m[n][c],n,x[n]));

            HightLightShowSingleLineCode(23);
        }

        public void doWork() {
            initMTable();
            drawTable();
            AddListCodeProc("开始求解最优值数组!");
            Knapsack(v, w, KnapsackVolume, ItemNum, m);
            AddListCodeProc("最优值数组求解结束!");
            AddListCodeProc("开始求解n元结果向量");
            Traceback(m, w, KnapsackVolume, ItemNum, result);
            AddListCodeProc("n元结果向量求解结束!");
            for (int i = 1; i < ItemNum + 1; i++) {
                TableStr[3][i] = result[i] + "";
            }
            TableStr[3][0] = "结果";
            //显示结果行，子线程不能修改ui
            msg = new Message();
            msg.what = 4;
            handler.sendMessage(msg);
        }

        public void run() {
            doWork();
            ++signal;
        }
    }

    public void initTable() {
        TableStr = new String[4][ItemNum+1];
        w = new int[ItemNum + 1];
        v = new int[ItemNum + 1];
        Random random = new Random();
        for (int i = 0; i <= ItemNum; i++){
            if (i != 0) {
                w[i] = random.nextInt(5) + 1;
                v[i] = random.nextInt(5) + 1;
            }
        }

        for (int i = 0; i < ItemNum+1; i++) {
            TableStr[0][i] = i + "";
            TableStr[1][i] = w[i] + "";
            TableStr[2][i] = v[i] + "";
            TableStr[3][i] = " ";
        }
        TableStr[0][0] = "序号";
        TableStr[1][0] = "重量";
        TableStr[2][0] = "价值";
    }

    public void showTable() {
        tableView.clearTableContents()
                .setHeader(TableStr[0])
                .addContent(TableStr[1])
                .addContent(TableStr[2])
                .addContent(TableStr[3])
                .refreshTable();
    }

    public void reset(){
        isRunning = false;
        isGetItemData = false;
        isStepOver = false;
        KnapsackVolume = 0;
        ItemNum = 0;
        signal = 0;
        TableStr = null;
        mTableStr = null;
        et_input.setText("");
        et_bp_capacity.setText("");
        msg = new Message();
        msg.what = 3;
        handler.sendMessage(msg);
        lv_code_list.performItemClick(lv_code_list.getChildAt(0), 0, lv_code_list.getItemIdAtPosition(0));
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
                    if (ItemNum >=2 && ItemNum <= 12) {
                        isGetItemData =true;
                        initTable();
                        showTable();
                    } else if (ItemNum > 12) {
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
                        KnapsackVolume = Integer.parseInt(KnapsackVolumeStr);
                        if (KnapsackVolume < 1) {
                            builder.setTitle("提示")
                                    .setMessage("亲，背包容量过小！")
                                    .setPositiveButton("确定" ,  null )
                                    .show();
                        } else if (KnapsackVolume >15) {
                            builder.setTitle("提示")
                                    .setMessage("亲，背包容量过大！")
                                    .setPositiveButton("确定" ,  null )
                                    .show();
                        } else {
                            if (!isRunning) {
                                KnapsackVolume = Integer.parseInt(KnapsackVolumeStr);
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
