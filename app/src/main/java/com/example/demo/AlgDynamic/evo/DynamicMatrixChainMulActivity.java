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

import com.example.demo.AlgDynamic.entity.TableString;
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

public class DynamicMatrixChainMulActivity extends Activity implements View.OnClickListener {
    private int signal = 0;
    private TableView tableView,mtableView,stableView;
    private Button bt_nextstep,bt_stepover,bt_stop,bt_start,bt_get_matrix;
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

    //是否获取矩阵数据
    private boolean isGetMatrix = false;
    //是否运行
    private boolean isRunning = false;
    //是否全速运行
    private boolean isStepOver = false;
    //矩阵个数
    private int MatrixNum = 0;
    //矩阵维数
    private int[] p = null;
    //最优值数组
    private int[][] m = null;
    //最优断开位置数组
    private int[][] s = null;

    private TableString tableString;

    private static final String[] strs = new String[] {
            "//动态规划法(三)：计算最优值(实现)",
            "void MatrixChain(int *p，int n，int **m，int **s)",
            "{",
            "    for (int i = 1; i <= n; i++) ",
            "\tm[i][i] = 0; //矩阵链长度为1",
            "    for (int r = 2; r <= n; r++)  //矩阵链长度为r，逐步增加",
            "    { ",
            "\tfor (int i = 1; i <= n - r+1; i++) ",
            "\t{",
            "\t    int j=i+r-1;",
            "\t    m[i][j] = m[i+1][j]+ p[i-1]*p[i]*p[j];",
            "\t    s[i][j] = i;",
            "\t    for (int k = i+1; k < j; k++) //计算长度为r的最优值",
            "\t    {",
            "\t\tint t = m[i][k] + m[k+1][j] + p[i-1]*p[k]*p[j];",
            "\t\tif (t < m[i][j]) ",
            "\t\t{ ",
            "\t\t     m[i][j] = t; ",
            "\t\t     s[i][j] = k;",
            "\t\t}",
            "\t    }",
            "\t}",
            "    }",
            "}",
            "",
            "//构造最优解",
            "void Traceback( int i, int j, int **s)",
            "{ ",
            "    if(i==j) return;",
            "    Traceback(i, s[i][j], s);",
            "    Traceback(s[i][j]+1, j, s);",
            "    cout<<\"Multiply A\"<<i<<\",\"<<s[i][j]; ",
            "    cout<<\" and A\"<<(s[i][j]+1)<<\",\"<<j<<endl;",
            "}"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_matrix_chain_mul);
        initView();
        initAction();
        fixSlideConflict();
    }

    public class ThreadRun implements Runnable {
        private Object lock;
        String[][] mTableStr = new String[MatrixNum+1][MatrixNum+1];
        String[][] sTableStr = new String[MatrixNum+1][MatrixNum+1];
        TableString tableStr = new TableString();

        public ThreadRun(Object lock) {
            this.lock = lock;
        }

        public void drawTable() {
            msg = new Message();
            tableStr.setmTableStr(mTableStr);
            tableStr.setsTableStr(sTableStr);
            msg.what = 1;
            msg.obj = tableStr;
            handler.sendMessage(msg);
        }

        public void initResultTable() {
            m = new int[MatrixNum+1][MatrixNum+1];
            s = new int[MatrixNum+1][MatrixNum+1];
            for (int i = 0; i < MatrixNum+1; i++) {
                for (int j = 0; j < MatrixNum+1; j++){
                    m[i][j] = -1;
                    s[i][j] = -1;
                    mTableStr[i][j] = " ";
                    sTableStr[i][j] = " ";
                }
            }
            for (int i = 0; i < MatrixNum+1 ; i++) {
                m[0][i] = i;
                m[i][0] = i;
                mTableStr[0][i] = i + "";
                mTableStr[i][0] = i + "";
                s[0][i] = i;
                s[i][0] = i;
                sTableStr[0][i] = i + "";
                sTableStr[i][0] = i + "";
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

        public void MatrixChain(int[] p, int n, int[][] m, int[][] s)
        {
            HightLightShowSingleLineCode(1);
            for (int i = 1; i <= n; i++)//3
            {
                HightLightShowSingleLineCode(3);
                HightLightShowSingleLineCode(4);
                AddListCodeProc("i = " + i + ",m[" + i + "][" + i + "] = 0");
                m[i][i] = 0; //矩阵链长度为1 //4
                mTableStr[i][i] = m[i][i] + "";
                drawTable();
                Pause();
            }
            for (int r = 2; r <= n; r++)  //矩阵链长度为r，逐步增加 //5
            {
                HightLightShowSingleLineCode(5);
                for (int i = 1; i <= n - r + 1; i++) //7
                {
                    HightLightShowSingleLineCode(7);

                    HightLightShowSingleLineCode(9);
                    int j = i + r - 1;//9
                    AddListCodeProc("r = " + r + ", i = " + i + ", j = " + j);

                    HightLightShowSingleLineCode(10);
                    m[i][j] = m[i + 1][j] + p[i - 1] * p[i] * p[j];//10
                    mTableStr[i][j] = m[i][j] + "";
                    String temp1 = "m[" + i + "][" + j + "] = m[" + (i + 1) + "][" + j + "] + p[" + (i - 1) + "] * p[" + i + "] * p[" + j + "]";
                    String temp2 = " = " + m[i + 1][j] + " + " + p[i - 1] + " * " + p[i] + " * " + p[j];
                    AddListCodeProc(temp1 + temp2 + " = " + m[i][j]);
                    drawTable();
                    Pause();

                    HightLightShowSingleLineCode(11);
                    AddListCodeProc("s[" + i + "][" + j + "] = " + i);
                    s[i][j] = i;//11
                    sTableStr[i][j] = s[i][j] + "";
                    drawTable();

                    Pause();

                    for (int k = i + 1; k < j; k++) //计算长度为r的最优值 //12
                    {
                        HightLightShowSingleLineCode(12);

                        AddListCodeProc("k = " + k);
                        HightLightShowSingleLineCode(14);
                        int t = m[i][k] + m[k + 1][j] + p[i - 1] * p[k] * p[j];//14
                        AddListCodeProc("t = " + "m[" + i + "][" + k + " + m[" + (k + 1) + "][" + j + "] + p[" + (i - 1) + "] * p[" + k + "] * p[" + j + "] = " + t);

                        HightLightShowSingleLineCode(15);
                        if (t < m[i][j]) //15
                        {
                            HightLightShowSingleLineCode(17);
                            AddListCodeProc("t < m[" + i + "][" + j + "] = " + m[i][j] + ",  m[" + i + "][" + j + "] = t = " + t);
                            m[i][j] = t;//17
                            mTableStr[i][j] = m[i][j] + "";
                            drawTable();
                            Pause();

                            HightLightShowSingleLineCode(18);
                            AddListCodeProc("s[" + i + "][" + j + "] = " + k);
                            s[i][j] = k;//18
                            sTableStr[i][j] = s[i][j] + "";

                            Pause();
                        }
                    }
                }
            }
            HightLightShowSingleLineCode(0);
        }

        //构造最优解
        public void Traceback(int i, int j, int[][] s)
        {
            AddListCodeProc("Traceback i = " + i + ", j = " + j);
            if (i == j)
            {
                AddListCodeProc("Traceback i = j return");
                return;
            }
            AddListCodeProc("s[" + i + "][" + j + "] = " + s[i][j]);
            Traceback(i, s[i][j], s);
            Traceback(s[i][j]+1, j, s);
            String str = "(A" + i + "---A" + s[i][j] + ") * (A" + (s[i][j]+1) + "---A" + j + ")";
            AddListCodeProc(str);
        }

        //构造最优解表达式字符串
        public String getStringTraceback(int i, int j, int[][] s)
        {
            if (i == j) return "A" + i;
            return "(" + getStringTraceback(i,s[i][j],s) + "*" + getStringTraceback(s[i][j]+1,j,s) + ")";
        }

        public void doWork() {
            initResultTable();
            drawTable();
            AddListCodeProc("开始计算最优值");
            MatrixChain(p, MatrixNum, m, s);
            AddListCodeProc("计算最优值结束!");
            AddListCodeProc("开始构造最优解");
            Traceback(1, MatrixNum, s);
            AddListCodeProc("构造最优解结束!");
            String result = getStringTraceback(1, MatrixNum, s);
            AddListCodeProc("最优值计算表达式：" + result);
        }

        public void run() {
            doWork();
            ++signal;
        }
    }

        public void initView() {
            mScrollView = (BottomScrollView) findViewById(R.id.main_scroll_view);
            bt_start = (Button)findViewById(R.id.bt_start);
            bt_nextstep = (Button)findViewById(R.id.bt_nextstep);
            bt_stepover = (Button)findViewById(R.id.bt_stepover);
            bt_stop = (Button)findViewById(R.id.bt_stop);
            bt_get_matrix = (Button)findViewById(R.id.bt_get_matrix);
            et_input = (EditText)findViewById(R.id.et_input);
            lv_code_list = (ListView)findViewById(R.id.lv_code_list);
            lv_list_code_proc = (ListView)findViewById(R.id.lv_code_list_prod);
            tv_intro = (TextView)findViewById(R.id.tv_intro);
            tableView = (TableView) findViewById(R.id.table);
            tableView.setColumnCount(18);
            mtableView = (TableView) findViewById(R.id.table_m);
            mtableView.setColumnCount(7);
            stableView = (TableView) findViewById(R.id.table_s);
            stableView.setColumnCount(7);

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
            bt_get_matrix.setOnClickListener(this);

            handler=new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case 1:
                            tableString = new TableString();
                            tableString = (TableString)msg.obj;
                            mtableView.clearTableContents()
                                    .setHeader(tableString.getmTableStr()[0]);
                            stableView.clearTableContents()
                                    .setHeader(tableString.getsTableStr()[0]);
                            for (int i = 0; i < MatrixNum; i++) {
                                mtableView.addContent(tableString.getmTableStr()[i+1]);
                                stableView.addContent(tableString.getsTableStr()[i+1]);
                            }
                            mtableView.refreshTable();
                            stableView.refreshTable();
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
                            stableView.clearTableContents()
                                    .refreshTable();

                            break;
                    }
                }
            };
    }

    public void initAction() {
        FileUtil fileUtil = FileUtil.getInstance();
        //读取txt
        InputStream inputStream = getResources().openRawResource(R.raw.dynamic_prog_matrix_chain_mul_redu);
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

    public void showTable() {
        Random random = new Random();
        String[] headerStr = new String[MatrixNum*3];
        String[] contentStr = new String[MatrixNum*3];
        p = new int[MatrixNum + 1];
        for (int i = 0; i <= MatrixNum; i++) {
            p[i] = random.nextInt(35)+5;
        }
        for(int i = 0; i < MatrixNum * 3; i++) {
            if (i % 3 == 1) {
                headerStr[i] = "A"+(i/3+1);
            } else {
                headerStr[i] = " ";
            }
        }
        for (int i = 0; i < MatrixNum * 3; i++) {
            if (i % 3 == 0)
                contentStr[i] = p[i / 3] + "";
            if (i % 3 == 1)
                contentStr[i] = "x";
            if (i % 3 == 2)
                contentStr[i] = p[i / 3 + 1] + "";
        }
        //表格显示数据
        tableView.clearTableContents()
                .setHeader(headerStr)
                .addContent(contentStr)
                .refreshTable();
    }

    public void reset() {
        isRunning = false;
        isGetMatrix = false;
        isStepOver = false;
        signal = 0;
        p = null;
        m = null;
        s = null;
        et_input.setText("");
        MatrixNum = 0;
        msg = new Message();
        msg.what = 3;
        handler.sendMessage(msg);
    }

    @Override
    public void onClick(View v) {
        //随机数
        int randomNum;
        String str = et_input.getText().toString();
        Pattern pattern = Pattern.compile("^[0-9]*$");
        //如果是0-9组成的数字字符
        if (pattern.matcher(str).matches() && !str.equals("")){
            MatrixNum = Integer.parseInt(str);
        }
        switch (v.getId()) {
            case R.id.bt_get_matrix:
                if (!isGetMatrix) {
                    if (MatrixNum >=2 && MatrixNum <=6 ) {
                        isGetMatrix = true;
                        showTable();

                    } else if (MatrixNum > 6) {
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
                            .setMessage("亲，已经获取数据！")
                            .setPositiveButton("确定" ,  null )
                            .show();
                }
                break;
            case R.id.bt_start:
                if (isGetMatrix) {
                    if (!isRunning) {
                        isRunning = true;
                        thread1 = new Thread(new ThreadRun(lock));
                        thread1.start();
                    } else {
                        builder.setTitle("提示")
                                .setMessage("亲，重新开始请停止运行！")
                                .setPositiveButton("确定" ,  null )
                                .show();
                    }
                } else {
                    builder.setTitle("提示")
                            .setMessage("亲，请先获取矩阵维数！")
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
