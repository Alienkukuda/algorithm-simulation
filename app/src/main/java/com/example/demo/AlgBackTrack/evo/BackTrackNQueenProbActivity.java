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

import com.example.demo.AlgBackTrack.customizeView.NQueenProbView;
import com.example.demo.AlgBackTrack.entity.DrawColorIndex;
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

public class BackTrackNQueenProbActivity extends Activity implements View.OnClickListener{
    private NQueenProbView nQueenProbView;
    private TableView table_show;
    private Button bt_nextstep,bt_stepover,bt_stop,bt_start,bt_get_data;
    private EditText et_input;
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

    //是否开始
    public boolean isRunning = false;
    //是否全速运行
    public boolean isStepOver = false;
    //皇后个数
    private int QueenNum = 0;

    private static final String[] strs = new String[] {
            "//回溯法实现-主类、约束判断和主程序：",
            "class Queen  //主类",
            "{ ",
            "    friend int nQueen(int);",
            "private:",
            "    bool Place(int k); //约束函数",
            "    void Backtrack(int t); //核心函数",
            "    int n, //皇后数",
            "       *x; // 当前解",
            "    long sum; //当前已找到的可行方案",
            "};",
            "",
            "bool Queen::Place(int k) //约束判断",
            "{  ",
            "    for(int j=1;j<k; j++) ",
            "        if((abs(k-j) == abs(x[j]-x[k])) || (x[j]==x[k])) ",
            "\t    return false; ",
            "    return true; ",
            "}",
            "",
            "",
            "//主程序 ",
            "int nQueen(int n)",
            "{ ",
            "    Queen Q; ",
            "    int *p= new int[n+1];//生成结果数组 ",
            "    for(int i=0; i<=n; i++)  //初始化结果数组 ",
            "\tp[i]=0; ",
            "    Q.x=p; ",
            "    Q.Backtrack(1); ",
            "    delete [] p; ",
            "    return Q.sum; ",
            "} ",
            "",
            "//递归回溯法实现-核心函数BackTrack：",
            "void Queen::Backtrack(int t)",
            "{ ",
            "    if( t>n )",
            "\tsum++; //解方案数加1 ",
            "    else",
            "    { ",
            "\tfor(int i=1; i<=n; i++)",
            "\t{ ",
            "\t    x[t]=i; ",
            "\t    if(Place(t))  Backtrack(t+1); ",
            "\t} ",
            "    } ",
            "}",
            "",
            "//迭代回溯法实现-核心函数BackTrack：",
            "void Queen::Backtrack(void) ",
            "{ ",
            "    x[1]=0; ",
            "    int k=1; ",
            "    while(k>0) ",
            "    { ",
            "\tx[k]+=1; ",
            "\twhile((x[k]<=n) && !(Place(k))) x[k]+=1; ",
            "\tif(x[k]<=n)",
            "\t{ ",
            "\t    if(k==n)  sum++;",
            "\t    else",
            "\t    { ",
            "\t\tk++; ",
            "\t\tx[k]=0; ",
            "\t    } ",
            "\t}",
            "\telse   k--; //回溯 ",
            "    } ",
            "}"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_track_nqueen_prob);
        initView();
        initAction();
        fixSlideConflict();
    }



    public void initView() {
        nQueenProbView = (NQueenProbView) findViewById(R.id.NQueenProbView);
        mScrollView = (BottomScrollView) findViewById(R.id.main_scroll_view);
        bt_get_data = (Button)findViewById(R.id.bt_get_data);
        bt_start = (Button)findViewById(R.id.bt_start);
        bt_nextstep = (Button)findViewById(R.id.bt_nextstep);
        bt_stepover = (Button)findViewById(R.id.bt_stepover);
        bt_stop = (Button)findViewById(R.id.bt_stop);
        et_input = (EditText)findViewById(R.id.et_input);
        lv_code_list = (ListView)findViewById(R.id.lv_code_list);
        lv_list_code_proc = (ListView)findViewById(R.id.lv_code_list_prod);
        tv_intro = (TextView)findViewById(R.id.tv_intro);
        table_show = (TableView) findViewById(R.id.table_show);
        table_show.setColumnCount(6);

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
                        String newProcWithCount = (count++) + ":" +msg.obj;
                        procItem.add(newProcWithCount);
                        mListViewAdapter.notifyDataSetChanged();
                        int index = procItem.size() - 1;
                        lv_list_code_proc.performItemClick(lv_list_code_proc.getChildAt(index), index, lv_list_code_proc.getItemIdAtPosition(index));
                        break;
                    case 2:
                        count = 0;
                        nQueenProbView.tableShowStr = null;
                        procItem.clear();
                        et_input.setText("");
                        mListViewAdapter.notifyDataSetChanged();
                        table_show.clearTableContents()
                                .refreshTable();
                        nQueenProbView.refresh();
                        break;
                    case 3:
                        int rowIndex = msg.arg1;
                        table_show.clearTableContents()
                                .setHeader(nQueenProbView.tableShowStr[0]);
                        for (int i = 1; i <=rowIndex; i++) {
                            table_show.addContent(nQueenProbView.tableShowStr[i]);
                        }
                        table_show.refreshTable();
                        break;
                }
            }
        };
    }

    public void initAction() {
        FileUtil fileUtil = FileUtil.getInstance();
        //读取txt
        InputStream inputStream = getResources().openRawResource(R.raw.back_track_nqueen_prob_redu);
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

        nQueenProbView.setHuffmanCodeActionLister(new NQueenProbView.NQueenActionLister() {
            @Override
            public void addListCodeProc(String newProc) {
                msg = new Message();
                msg.what = 1;
                msg.obj = newProc;
                handler.sendMessage(msg);
            }

            @Override
            public void clearAll() {
                msg = new Message();
                msg.what = 2;
                handler.sendMessage(msg);
            }

            @Override
            public void refreshTable(int rowIndex) {
                msg = new Message();
                msg.what = 3;
                msg.arg1 = rowIndex;
                handler.sendMessage(msg);
            }
        });
    }

    public void fixSlideConflict() {
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

    public void reset() {
        isRunning = false;
        isStepOver = false;
        QueenNum = 0;
        nQueenProbView.clear();
        msg = new Message();
        msg.what = 2;
        handler.sendMessage(msg);
    }

    public void onClick(View v) {
        String str = et_input.getText().toString();
        Pattern pattern = Pattern.compile("^[0-9]*$");
        //如果是0-9组成的数字字符
        if (pattern.matcher(str).matches() && !str.equals("")){
            QueenNum = Integer.parseInt(str);
        }
        switch (v.getId()) {
            case R.id.bt_get_data:
                if (!nQueenProbView.isGetQueenNum) {
                    if (QueenNum >=2 && QueenNum <=5 ) {
                        nQueenProbView.QueenNum = QueenNum;
                        nQueenProbView.isGetQueenNum = true;
                        nQueenProbView.refresh();
                    } else if (QueenNum > 5) {
                        builder.setTitle("提示")
                                .setMessage("亲，个数太多了！")
                                .setPositiveButton("确定" ,  null )
                                .show();
                    } else {
                        builder.setTitle("提示")
                                .setMessage("亲，请正确填写元素个数！")
                                .setPositiveButton("确定" ,  null )
                                .show();
                    }
                } else {
                    builder.setTitle("提示")
                            .setMessage("亲，重新开始请停止运行！")
                            .setPositiveButton("确定" ,  null )
                            .show();
                }
                break;
            case R.id.bt_start:
                if (nQueenProbView.isGetQueenNum) {
                    if (!isRunning) {
                        isRunning = true;
                        thread1 = new Thread(nQueenProbView.new ThreadRun(lock));
                        thread1.start();
                    } else {
                        builder.setTitle("提示")
                                .setMessage("亲，重新开始请停止运行！")
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
                                if (isStepOver && nQueenProbView.signal == 0) {
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
