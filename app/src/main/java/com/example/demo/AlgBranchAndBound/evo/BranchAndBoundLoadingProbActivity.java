package com.example.demo.AlgBranchAndBound.evo;

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

import com.example.demo.AlgBranchAndBound.customizeView.BranchAndBoundLoadingView;
import com.example.demo.AlgBranchAndBound.entity.BranchBoundLoadTextBox;
import com.example.demo.AlgBranchAndBound.entity.LoadNode;
import com.example.demo.DataStruct.BST;
import com.example.demo.FileUtil.FileUtil;
import com.example.demo.R;
import com.example.demo.adapter.ListViewAdapter;
import com.example.demo.thread.ThreadNotify;
import com.example.demo.view.BottomScrollView;
import com.example.demo.view.TableView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class BranchAndBoundLoadingProbActivity extends Activity implements View.OnClickListener{
    private BranchAndBoundLoadingView branchAndBoundLoadingView;
    private TableView table;
    private Button bt_nextstep,bt_stepover,bt_stop,bt_start,bt_get_data;
    private EditText et_input,et_ship_load;
    private ListView lv_code_list,lv_list_code_proc;
    private TextView tv_intro,tv_current_floor,tv_current_load,tv_current_best_load,tv_remaining_container_capacity;
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


    private boolean isGetContainerData = false;
    //是否运行
    public boolean isRunning = false;
    //是否全速运行
    private boolean isStepOver = false;
    //集装箱个数
    private int ContainerNum = 0;
    //集装箱重量数组
    private int[] w = null;
    //初始表
    String[][] tableStr = null;

    private static final String[] strs = new String[] {
            "//队列式实现-优化后的核心函数",
            "template <class Type> ",
            "Type MaxLoading(Type w[], Type c, int n)",
            "{ ",
            "    //初始化 ",
            "    Queue<Type> Q;    //活结点队列 ",
            "    Q.Add(-1);    //同层结点结尾标志 ",
            "    int i = 1;    //当前扩展结点所处的层 ",
            "    Type Ew = 0,    //扩展结点对应的载重量 ",
            "\tbestW = 0;    //当前最优载重量 ",
            "\tr = 0;    //剩余集装箱的重量 ",
            "    for( int j = 2; j <= n; j++)",
            "    { ",
            "\tr += w[j]; ",
            "    }",
            "    while (true) //检查左儿子结点",
            "    {",
            "\tType wt = Ew + w[i] ;",
            "\tif (wt <= c) //可行结点",
            "\t{ ",
            "\t    if(wt > bestw) ",
            "\t        bestw = wt;//提前更新bestw",
            "\t    //加入活结点队列",
            "\t    if(i < n) Q.Add(wt);",
            "\t}",
            "\tif(Ew + r > bestw && i < n) ",
            "\t    Q.add(Ew);",
            "\t//可能含最优解,不含最优解的右儿子被剪枝",
            "\tQ.Delete(Ew); //取下一扩展结点",
            "\tif (Ew == -1) // 同层结点尾部",
            "\t{ ",
            "\t    if (Q.IsEmpty()) ",
            "\t        return bestw;",
            "\t    Q.Add(-1); //同层结点尾部标志",
            "\t    Q.Delete(Ew); //取下一扩展结点",
            "\t    i++; //进入下一层",
            "\t    r -= w[i];",
            "\t}",
            "    }",
            "}"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_bound_loading_prob);
        initView();
        initAction();
        fixSlideConflict();
    }

    public void initView() {
        mScrollView = (BottomScrollView) findViewById(R.id.main_scroll_view);
        branchAndBoundLoadingView = (BranchAndBoundLoadingView)findViewById(R.id.BranchAndBoundLoadingView);
        table = (TableView) findViewById(R.id.table);
        table.setColumnCount(5);
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
        tv_current_floor = (TextView) findViewById(R.id.tv_current_floor);
        tv_current_load = (TextView) findViewById(R.id.tv_current_load);
        tv_current_best_load = (TextView) findViewById(R.id.tv_current_best_load);
        tv_remaining_container_capacity = (TextView) findViewById(R.id.tv_remaining_container_capacity);

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
                        String newProcWithCount = (count++) + ":" +msg.obj;
                        procItem.add(newProcWithCount);
                        mListViewAdapter.notifyDataSetChanged();
                        int index = procItem.size() - 1;
                        lv_list_code_proc.performItemClick(lv_list_code_proc.getChildAt(index), index, lv_list_code_proc.getItemIdAtPosition(index));
                        break;
                    case 2:
                        BranchBoundLoadTextBox load = (BranchBoundLoadTextBox)msg.obj;
                        tv_current_floor.setText(String.valueOf(load.textBoxCurrentFloor));
                        tv_current_best_load.setText(String.valueOf(load.textBoxCurrentBestLoad));
                        tv_remaining_container_capacity.setText(String.valueOf(load.textBoxRemainingContainer));
                        tv_current_load.setText(String.valueOf(load.textBoxCurrentLoad));
                        break;
                    case 3:
                        tableStr = null;
                        count = 0;
                        branchAndBoundLoadingView.Tree = null;
                        branchAndBoundLoadingView.refresh();
                        procItem.clear();
                        mListViewAdapter.notifyDataSetChanged();
                        tv_current_floor.setText("");
                        tv_current_best_load.setText("");
                        tv_remaining_container_capacity.setText("");
                        tv_current_load.setText("");
                        et_input.setText("");
                        et_ship_load.setText("");
                        table.clearTableContents()
                                .refreshTable();
                        break;
                }
            }
        };
    }

    public void initAction() {
        FileUtil fileUtil = FileUtil.getInstance();
        //读取txt
        InputStream inputStream = getResources().openRawResource(R.raw.branch_bound_loading_prob_redu);
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

        //监听view的高亮代码
        branchAndBoundLoadingView.setBranchAndBoundLoadingActionListener(new BranchAndBoundLoadingView.BranchAndBoundLoadingActionListener(){


            public void addListCodeProc(String newProc){
                msg = new Message();
                msg.what = 1;
                msg.obj = newProc;
                handler.sendMessage(msg);
            }

            public void updateTextBox(BranchBoundLoadTextBox textBox) {
                msg = new Message();
                msg.what = 2;
                msg.obj = textBox;
                handler.sendMessage(msg);
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

        Random random = new Random();
        for (int i = 0; i <= ContainerNum; i++) {
            if (i != 0) {
                w[i] = random.nextInt(5) + 1;
            }
        }
        branchAndBoundLoadingView.w = w;

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

    public void reset() {
        ContainerNum = 0;
        isGetContainerData = false;
        isStepOver = false;
        isRunning = false;
        w = null;
        tableStr = null;
        branchAndBoundLoadingView.clear();
        msg = new Message();
        msg.what = 3;
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
                    if (ContainerNum >= 1 && ContainerNum <= 4) {
                        isGetContainerData = true;
                        branchAndBoundLoadingView.ContainerNum = ContainerNum;
                        initTable();
                        showTable();
                    } else if (ContainerNum > 4) {
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
                        int c = Integer.parseInt(KnapsackVolumeStr);
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
                                branchAndBoundLoadingView.c = c;
                                isRunning = true;
                                thread1 = new Thread(branchAndBoundLoadingView.new ThreadRun(lock));
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
                                if (isStepOver && branchAndBoundLoadingView.signal == 0) {
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
