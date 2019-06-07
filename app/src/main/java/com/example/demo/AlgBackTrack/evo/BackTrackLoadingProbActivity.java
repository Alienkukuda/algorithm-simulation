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
import com.example.demo.AlgBackTrack.customizeView.BackTrackLoadingProbView;
import com.example.demo.AlgBackTrack.entity.LoadTextBox;
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

public class BackTrackLoadingProbActivity extends Activity implements View.OnClickListener {
    private int signal = 0;
    private TableView table, tableCur, tableCurBest;
    private Button bt_nextstep, bt_stepover, bt_stop, bt_start, bt_get_data;
    private EditText et_input, et_ship_load;
    private ListView lv_code_list, lv_list_code_proc;
    private TextView tv_intro, tv_current_best_load, tv_remaining_load, tv_current_load;
    //锁对象
    private final Object lock = new Object();
    private Thread thread1, thread2;
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

    private boolean isGetContainerData = false;
    //是否运行
    private boolean isRunning = false;
    //是否全速运行
    private boolean isStepOver = false;
    //集装箱个数
    private int ContainerNum = 0;
    //当前解
    private int[] x = null;
    //当前最优解
    private int[] bestx = null;
    //集装箱重量数组
    private int[] w = null;
    //第一艘轮船的载重量
    private int c = -1;
    //当前载重量
    private int cw = -1;
    //当前最优载重量
    private int bestw = -1;
    // 剩余集装箱重量
    private int r = -1;
    //初始表
    String[][] tableStr = null;
    //单位质量价值表
    String[][] tableCurStr = null;
    //当前解表
    String[][] tableCurBestStr = null;

    private static final String[] strs = new String[] {
            "//含构造最优解的核心函数BackTrack",
            "template<class T> ",
            "void Loading<T>::BackTrack(int i)//从第i 层节点搜索",
            "{ ",
            "    if (i > n) //位于叶节点 ",
            "    {",
            "        if (cw > bestw) ",
            "        { ",
            "            bestw = cw; ",
            "\t    for(j=1; j<=n; j++) ",
            "\t        bestx[j]=x[j];",
            "        } ",
            "        return; ",
            "    } ",
            "    r - = w[i]; //检查子树 ",
            "    if (cw + w[i] <= c) //尝试x[i] = 1 ",
            "    {",
            "\tx[i] = 0;",
            "        cw += w[i]; ",
            "        BackTrack( i + 1 ) ; ",
            "        cw -= w[i];",
            "    } ",
            "    if (cw + r > bestw) //尝试x[i] = 0",
            "    { ",
            "\tx[i] = 0;",
            "        BackTrack( i + 1 ) ; ",
            "    }",
            "    r += w[i]; ",
            "}"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_track_loading_prob);
        initView();
        initAction();
        fixSlideConflict();
    }

    public void initView() {
        mScrollView = (BottomScrollView) findViewById(R.id.main_scroll_view);
        table = (TableView) findViewById(R.id.table);
        table.setColumnCount(7);
        tableCur = (TableView) findViewById(R.id.table_cur);
        tableCur.setColumnCount(7);
        tableCurBest = (TableView) findViewById(R.id.table_cur_best);
        tableCurBest.setColumnCount(7);
        bt_start = (Button) findViewById(R.id.bt_start);
        bt_nextstep = (Button) findViewById(R.id.bt_nextstep);
        bt_stepover = (Button) findViewById(R.id.bt_stepover);
        bt_stop = (Button) findViewById(R.id.bt_stop);
        bt_get_data = (Button) findViewById(R.id.bt_get_data);
        et_input = (EditText) findViewById(R.id.et_input);
        et_ship_load = (EditText) findViewById(R.id.et_ship_load);
        lv_code_list = (ListView) findViewById(R.id.lv_code_list);
        lv_list_code_proc = (ListView) findViewById(R.id.lv_code_list_prod);
        tv_intro = (TextView) findViewById(R.id.tv_intro);
        tv_current_best_load = (TextView) findViewById(R.id.tv_current_best_load);
        tv_remaining_load = (TextView) findViewById(R.id.tv_remaining_load);
        tv_current_load = (TextView) findViewById(R.id.tv_current_load);

        builder = new AlertDialog.Builder(this);

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
                        tableCur.clearTableContents()
                                .setHeader(tableCurStr[0])
                                .addContent(tableCurStr[1])
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
                        LoadTextBox load = (LoadTextBox)msg.obj;
                        tv_current_best_load.setText(String.valueOf(load.textBoxCurrentBestLoad));
                        tv_remaining_load.setText(String.valueOf(load.textBoxRemainingLoad));
                        tv_current_load.setText(String.valueOf(load.textBoxCurrentLoad));
                        break;
                    case 4:
                        tableCurBest.clearTableContents()
                                .setHeader(tableCurBestStr[0])
                                .addContent(tableCurBestStr[1])
                                .refreshTable();
                        break;
                    case 5:
                        tableStr = null;
                        tableCurStr = null;
                        tableCurBestStr = null;
                        count = 0;
                        procItem.clear();
                        mListViewAdapter.notifyDataSetChanged();
                        tv_current_best_load.setText("");
                        tv_remaining_load.setText("");
                        tv_current_load.setText("");
                        et_input.setText("");
                        et_ship_load.setText("");
                        table.clearTableContents()
                            .refreshTable();
                        tableCur.clearTableContents()
                                .refreshTable();
                        tableCurBest.clearTableContents()
                                .refreshTable();
                        break;
                }
            }
        };
    }

    public void initAction() {
        FileUtil fileUtil = FileUtil.getInstance();
        //读取txt
        InputStream inputStream = getResources().openRawResource(R.raw.back_track_loading_prob_redu);
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

                if (action == MotionEvent.ACTION_DOWN) {
                    mLastY = event.getY();
                }
                if (action == MotionEvent.ACTION_MOVE && procItem.size() != 0) {
                    int top = lv_list_code_proc.getChildAt(0).getTop();
                    float nowY = event.getY();
                    if (!isSvToBottom) {
                        // 允许scrollview拦截点击事件, scrollView滑动
                        mScrollView.requestDisallowInterceptTouchEvent(false);
                    } else if (top == 0 && nowY - mLastY > THRESHOLD_Y_LIST_VIEW) {
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



    public void initTable() {
        w = new int[ContainerNum + 1];
        tableStr = new String[2][ContainerNum + 1];
        tableCurStr = new String[2][ContainerNum + 1];
        tableCurBestStr = new String[2][ContainerNum + 1];
        Random random = new Random();
        for (int i = 0; i <= ContainerNum; i++) {
            if (i != 0) {
                w[i] = random.nextInt(5) + 1;
            }
        }

        for (int i = 0; i < ContainerNum + 1; i++) {
            if (i != 0) {
                tableStr[0][i] = i + "";
                tableStr[1][i] = w[i] + "";
            }
        }
        tableStr[0][0] = "序号";
        tableStr[1][0] = "重量";
    }

    public void showTable() {
        table.clearTableContents()
                .setHeader(tableStr[0])
                .addContent(tableStr[1])
                .refreshTable();
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
                synchronized (lock) {
                    lock.wait();
                }
            } catch (InterruptedException ie) {
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

        public void doWork() {
            MaxLoading();
        }
        //构造最优解主函数
        private void MaxLoading()
        {
            x = new int[ContainerNum + 1];
            bestx = new int[ContainerNum + 1];
            bestw = 0;
            cw = 0;
            r = 0;
            for (int i = 1; i <= ContainerNum; i++)
            {
                r += w[i];
            }
            AddListCodeProc("开始搜索");
            UpdateData();
            Backtrack(1);
            AddListCodeProc("搜索结束");
        }
        //递归回溯
        private void Backtrack(int i)
        {
            AddListCodeProc("搜索到第" + i + "层节点");
            //搜索到第i层节点
            if (i > ContainerNum)
            {
                AddListCodeProc(String.format("i = %d,大于集装箱总数%d",i,ContainerNum));
                if (cw > bestw)
                {
                    AddListCodeProc(String.format("当前载重量cw = %d,大于 当前最优载重量bestw = %d", cw, bestw));

                    AddListCodeProc("更新当前最优解和当前最优载重量");
                    for (int j = 1; j <= ContainerNum; j++)
                    {
                        bestx[j] = x[j];
                        bestw = cw;
                    }
                    UpdateBestSolution();
                    UpdateData();
                }
                else
                {
                    AddListCodeProc(String.format("当前载重量cw = %d,小于或者等于 当前最优载重量bestw = %d,不更新", cw, bestw));
                }

                return;
            }

            //搜索子树
            r -= w[i];
            UpdateData();

            if (cw + w[i] <= c)  //搜索左子树
            {
                AddListCodeProc(String.format("当前载重量cw=%d,集装箱重量w[%d]=%d,cw+w[%d] = %d, <=轮船载重量c=%d", cw, i, w[i], i, cw + w[i],c));

                x[i] = 1;

                UpdateCurrentSolution();

                cw += w[i];

                UpdateData();

                Backtrack(i + 1);

                cw -= w[i];

                UpdateData();

                AddListCodeProc(String.format("左子树搜索完毕，返回上一层,i=%d", i));
            }

            if (cw + r > bestw)  //搜索右子树
            {
                AddListCodeProc(String.format("当前载重量cw=%d,剩余集装箱重量r=%d,cw+r= %d, > 当前最优载重量bestw=%d", cw, r, cw + r, bestw));

                x[i] = 0;

                UpdateCurrentSolution();

                Backtrack(i + 1);

                AddListCodeProc(String.format("右子树搜索完毕，返回上一层,i=%d", i));
            }

            r += w[i];

            AddListCodeProc(String.format("左子树和右子树搜索完毕，返回上一层,i=%d", i));

            UpdateData();
        }

        //更新相关数据
        private void UpdateData()
        {
            LoadTextBox loadTextBox = new LoadTextBox(bestw,r,cw);
            msg = new Message();
            msg.what = 3;
            msg.obj = loadTextBox;
            handler.sendMessage(msg);
            Pause();

        }

        //更新当前解
        private void UpdateCurrentSolution()
        {
            for (int i = 0; i <= ContainerNum; i++) {
                if (i == 0){
                    tableCurStr[0][i] = "序号";
                    tableCurStr[1][i] = "取值";
                } else {
                    tableCurStr[0][i] = i + "";
                    tableCurStr[1][i] = x[i] + "";
                }
            }
            msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
            Pause();
        }

        //更新当前最优解
        private void UpdateBestSolution()
        {
            for (int i = 0; i <= ContainerNum; i++) {
                if (i == 0){
                    tableCurBestStr[0][i] = "序号";
                    tableCurBestStr[1][i] = "取值";
                } else {
                    tableCurBestStr[0][i] = i + "";
                    tableCurBestStr[1][i] = x[i] + "";
                }
            }
            msg = new Message();
            msg.what = 4;
            handler.sendMessage(msg);
            Pause();
        }

        public void run() {
            doWork();
            ++signal;
        }
    }

    public void reset() {
        isRunning = false;
        isGetContainerData = false;
        isStepOver = false;
        ContainerNum = 0;
        signal = 0;
        x = null;
        bestx = null;
        w = null;
        c = -1;
        cw = -1;
        r = -1;
        msg = new Message();
        msg.what = 5;
        handler.sendMessage(msg);
    }

    @Override
    public void onClick(View v) {
        String str = et_input.getText().toString();
        String KnapsackVolumeStr = et_ship_load.getText().toString();
        Pattern pattern = Pattern.compile("^[0-9]*$");
        if (pattern.matcher(str).matches() && !str.equals("")) {
            ContainerNum = Integer.parseInt(str);
        }
        switch (v.getId()) {
            case R.id.bt_get_data:
                if (!isGetContainerData) {
                    if (ContainerNum >= 1 && ContainerNum <= 6) {
                        System.out.println(ContainerNum);
                        isGetContainerData = true;
                        initTable();
                        showTable();
                    } else if (ContainerNum > 6) {
                        builder.setTitle("提示")
                                .setMessage("亲，个数太多了！")
                                .setPositiveButton("确定", null)
                                .show();
                    } else {
                        builder.setTitle("提示")
                                .setMessage("亲，请正确填写个数！")
                                .setPositiveButton("确定", null)
                                .show();
                    }
                } else {
                    builder.setTitle("提示")
                            .setMessage("亲，已经获取数据！")
                            .setPositiveButton("确定", null)
                            .show();
                }
                break;
            case R.id.bt_start:
                if (isGetContainerData) {
                    if (pattern.matcher(KnapsackVolumeStr).matches() && !KnapsackVolumeStr.equals("")) {
                        c = Integer.parseInt(KnapsackVolumeStr);
                        System.out.println(c);
                        if (c < 1) {
                            builder.setTitle("提示")
                                    .setMessage("亲，重量过小！")
                                    .setPositiveButton("确定" ,  null )
                                    .show();
                        } else if (c >=100) {
                            builder.setTitle("提示")
                                    .setMessage("亲，重量过大！")
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
                                .setMessage("亲，请正确填写载重量！")
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
