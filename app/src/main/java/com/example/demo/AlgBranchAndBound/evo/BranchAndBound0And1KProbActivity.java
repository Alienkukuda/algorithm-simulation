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

import com.example.demo.AlgBranchAndBound.customizeView.BranchAndBound0And1KProbView;
import com.example.demo.AlgBranchAndBound.entity.BpNode;
import com.example.demo.AlgBranchAndBound.entity.BranchBoundBpTextBox;
import com.example.demo.AlgBranchAndBound.entity.HeapNode;
import com.example.demo.AlgBranchAndBound.entity.bbnode;
import com.example.demo.DataStruct.BST;
import com.example.demo.DataStruct.Heap;
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

public class BranchAndBound0And1KProbActivity extends Activity implements View.OnClickListener{
    private BranchAndBound0And1KProbView branchAndBound0And1KProbView;
    private TableView table,table_per,table_best;
    private Button bt_nextstep,bt_stepover,bt_stop,bt_start,bt_get_data;
    private EditText et_input,et_bp_capacity;
    private ListView lv_code_list,lv_list_code_proc;
    private TextView tv_intro,tv_current_weight,tv_current_value,tv_current_best_value,tv_current_floor;
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
    public boolean isRunning = false;
    //是否全速运行
    private boolean isStepOver = false;
    //物品个数
    private int ItemNum = 0;

    //原始物品重量数组
    private int[] sourse_w = null;

    //原始物品价值数组
    private int[] sourse_p = null;

    //初始表
    String[][] tableStr = null;

    private static final String[] strs = new String[] {
            "//实现-核心函数MaxKnapsack",
            "template<class Typew, class Typep> ",
            "Typep Knap<Typew,Typep>::MaxKnapsack()  ",
            "{   //优先队列式分支界限法， bestx返回最大价值 ",
            "    H=new MaxHeap<HeapNode<Typep,Typew>>(1000); ",
            "    bestx=new int[n+1]; ",
            "    int i=1; E=0; cw=cp=0; Typep bestp=0; ",
            "    Typep up=Bound(1); //价值上界 ",
            "    while(i!=n+1)  //搜索子集空间树 ",
            "    {",
            "\t//检查当前扩展结点的左儿子结点 ",
            "\tTypew wt=cw+w[i]; ",
            "\tif(wt<=c)  //左儿子结点为可行结点",
            "\t{",
            "\t    if(cp+p[i]>bestp) ",
            "\t\tbestp=cp+p[i]; ",
            "\t    AddLiveNode(up,cp+p[i],cw+w[i],true,i+1); ",
            "\t} ",
            "        up=Bound(i+1);",
            "",
            "        //检查当前扩展结点的右儿子结点 ",
            "\tif(up>=bestp)",
            "\t    AddLiveNode(up,cp,cw,false,i+1); ",
            "",
            "\t//取下一扩展结点 ",
            "\tHeapNode<Typep,Typew> N; ",
            "\tH->DeleteMax(N); ",
            "\tE=N.ptr;",
            "\tcw=N.weight; ",
            "\tcp=N.profit;",
            "\tup=N.uprofit; ",
            "\ti=N.level; ",
            "    } //搜索子集空间树while循环结束 ",
            "",
            "    //构造当前最优解 ",
            "    for(int j=n;j>0;j--)",
            "    { ",
            "\tbestx[j]=E->LChild; ",
            "\tE=E->parent; ",
            "    } ",
            "    return cp; ",
            "}"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_bound0and1k_prob);
        initView();
        initAction();
        fixSlideConflict();
    }

    public void initView() {
        mScrollView = (BottomScrollView) findViewById(R.id.main_scroll_view);
        branchAndBound0And1KProbView = (BranchAndBound0And1KProbView)findViewById(R.id.BranchAndBound0And1KProbView);
        table = (TableView) findViewById(R.id.table);
        table.setColumnCount(5);
        table_per = (TableView) findViewById(R.id.table_per);
        table_per.setColumnCount(5);
        table_best = (TableView) findViewById(R.id.table_best);
        table_best.setColumnCount(5);
        bt_start = (Button) findViewById(R.id.bt_start);
        bt_nextstep = (Button) findViewById(R.id.bt_nextstep);
        bt_stepover = (Button) findViewById(R.id.bt_stepover);
        bt_stop = (Button) findViewById(R.id.bt_stop);
        bt_get_data = (Button) findViewById(R.id.bt_get_data);
        et_input = (EditText) findViewById(R.id.et_input);
        et_bp_capacity = (EditText) findViewById(R.id.et_bp_capacity);
        lv_code_list = (ListView) findViewById(R.id.lv_code_list);
        lv_list_code_proc = (ListView) findViewById(R.id.lv_code_list_prod);
        tv_intro = (TextView) findViewById(R.id.tv_intro);
        tv_current_weight = (TextView) findViewById(R.id.tv_current_weight);
        tv_current_value = (TextView) findViewById(R.id.tv_current_value);
        tv_current_best_value = (TextView) findViewById(R.id.tv_current_best_value);
        tv_current_floor = (TextView) findViewById(R.id.tv_current_floor);

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
                        BranchBoundBpTextBox load = (BranchBoundBpTextBox)msg.obj;
                        tv_current_weight.setText(String.valueOf(load.textBoxCurrentWeight));
                        tv_current_value.setText(String.valueOf(load.textBoxCurrentValue));
                        tv_current_best_value.setText(String.valueOf(load.textBoxCurrentBestValue));
                        tv_current_floor.setText(String.valueOf(load.textBoxCurrentNodeLevel));
                        break;
                    case 3:
                        String[][] tablePerStr = (String[][])msg.obj;
                        table_per.clearTableContents()
                                .setHeader(tablePerStr[0])
                                .addContent(tablePerStr[1])
                                .addContent(tablePerStr[2])
                                .refreshTable();
                        break;
                    case 4:
                        String[][] tableBestStr = (String[][])msg.obj;
                        table_best.clearTableContents()
                                .setHeader(tableBestStr[0])
                                .addContent(tableBestStr[1])
                                .refreshTable();
                        break;
                    case 5:
                        tableStr = null;
                        branchAndBound0And1KProbView.tablePerStr = null;
                        branchAndBound0And1KProbView.tableBestStr = null;
                        count = 0;
                        branchAndBound0And1KProbView.Tree = null;
                        branchAndBound0And1KProbView.refresh();
                        procItem.clear();
                        mListViewAdapter.notifyDataSetChanged();
                        tv_current_weight.setText("");
                        tv_current_value.setText("");
                        tv_current_best_value.setText("");
                        tv_current_floor.setText("");
                        et_input.setText("");
                        et_bp_capacity.setText("");
                        table.clearTableContents()
                                .refreshTable();
                        table_best.clearTableContents()
                                .refreshTable();
                        table_per.clearTableContents()
                                .refreshTable();
                        break;
                }
            }
        };
    }

    public void initAction() {
        FileUtil fileUtil = FileUtil.getInstance();
        //读取txt
        InputStream inputStream = getResources().openRawResource(R.raw.branch_bound0and1k_prob_redu);
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
        branchAndBound0And1KProbView.setBranchAndBound0And1KProbActionListener(new BranchAndBound0And1KProbView.BranchAndBound0And1KProbActionListener() {


            public void addListCodeProc(String newProc){
                msg = new Message();
                msg.what = 1;
                msg.obj = newProc;
                handler.sendMessage(msg);
            }

            public void updateTextBox(BranchBoundBpTextBox textBox) {
                msg = new Message();
                msg.what = 2;
                msg.obj = textBox;
                handler.sendMessage(msg);
            }

            public void showItemArraySorted(String[][] tablePerStr){
                msg = new Message();
                msg.what = 3;
                msg.obj = tablePerStr;
                handler.sendMessage(msg);
            }
            //显示最优解
            public void showBestSolution(String[][] tableBestStr){
                msg = new Message();
                msg.what = 4;
                msg.obj = tableBestStr;
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
        sourse_w = new int[ItemNum + 1];
        sourse_p = new int[ItemNum + 1];
        tableStr = new String[3][ItemNum + 1];
        branchAndBound0And1KProbView.tablePerStr = new String[3][ItemNum + 1];
        branchAndBound0And1KProbView.tableBestStr = new String[2][ItemNum + 1];

        Random random = new Random();
        for (int i = 0; i <= ItemNum; i++) {
            if (i != 0) {
                sourse_w[i] = random.nextInt(5) + 1;
                sourse_p[i] = random.nextInt(5) + 1;
            }
        }
        branchAndBound0And1KProbView.sourse_w = sourse_w;
        branchAndBound0And1KProbView.sourse_p = sourse_p;

        for (int i = 0; i < ItemNum + 1; i++) {
            if (i != 0) {
                tableStr[0][i] = i + "";
                branchAndBound0And1KProbView.tableBestStr[0][i] = i + "";
                branchAndBound0And1KProbView.tablePerStr[0][i] = i + "";
                tableStr[1][i] = sourse_w[i] + "";
                tableStr[2][i] = sourse_p[i] + "";
            }
        }
        tableStr[0][0] = "序号";
        tableStr[1][0] = "重量";
        tableStr[2][0] = "价值";
        branchAndBound0And1KProbView.tablePerStr[0][0] = "序号";
        branchAndBound0And1KProbView.tablePerStr[1][0] = "重量";
        branchAndBound0And1KProbView.tablePerStr[2][0] = "价值";
        branchAndBound0And1KProbView.tableBestStr[0][0] = "序号";
        branchAndBound0And1KProbView.tableBestStr[1][0] = "取值";
    }

    public void showTable() {
        table.clearTableContents()
                .setHeader(tableStr[0])
                .addContent(tableStr[1])
                .addContent(tableStr[2])
                .refreshTable();
    }

    public void reset() {
        ItemNum = 0;
        isGetItemData = false;
        isStepOver = false;
        isRunning = false;
        sourse_w = null;
        sourse_p = null;
        tableStr = null;
        branchAndBound0And1KProbView.clear();
        msg = new Message();
        msg.what = 5;
        handler.sendMessage(msg);
    }

    @Override
    public void onClick(View v) {
        String str = et_input.getText().toString();
        String KnapsackVolumeStr = et_bp_capacity.getText().toString();
        Pattern pattern = Pattern.compile("^[0-9]*$");
        if (pattern.matcher(str).matches() && !str.equals("")) {
            ItemNum = Integer.parseInt(str);
        }
        switch (v.getId()) {
            case R.id.bt_get_data:
                if (!isGetItemData) {
                    if (ItemNum >= 1 && ItemNum <= 4) {
                        isGetItemData = true;
                        branchAndBound0And1KProbView.ItemNum = ItemNum;
                        initTable();
                        showTable();
                    } else if (ItemNum > 4) {
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
                if (isGetItemData) {
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
                                branchAndBound0And1KProbView.c = c;
                                isRunning = true;
                                thread1 = new Thread(branchAndBound0And1KProbView.new ThreadRun(lock));
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
                                if (isStepOver && branchAndBound0And1KProbView.signal == 0) {
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
