package com.example.demo.AlgDynamic.evo;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Point;
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

import com.example.demo.AlgDynamic.customizeView.CpotView;
import com.example.demo.AlgDynamic.entity.TableString;
import com.example.demo.AlgDynamic.entity.ThreePoint;
import com.example.demo.FileUtil.FileUtil;
import com.example.demo.R;
import com.example.demo.adapter.ListViewAdapter;
import com.example.demo.thread.ThreadNotify;
import com.example.demo.view.BottomScrollView;
import com.example.demo.view.TableView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class DynamicCpotActivity extends Activity implements View.OnClickListener{
    private int signal = 0;
    private CpotView cpotView;
    private TableView tableView_t,tableView_s;
    private Button bt_nextstep,bt_stepover,bt_stop,bt_start,bt_get_polygon;
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
    private int VertexNum = 0;

    //最优值数组
    private int[][] t = null;
    //最优断开位置数组
    private int[][] s = null;
    //接受view里面的VertexPointList
//    private List<Point> VertexPointList = new ArrayList<Point>();

//    List<ThreePoint> TriangulationProcList = new ArrayList<ThreePoint>();

    private TableString tableString;

    private static final String[] strs = new String[] {
            "//凸多边形的最优三角剖分问题：解最优值实现",
            "public void minWeightTriangulation(int n, int **t, int **s) ",
            "{ ",
            "    for (int i = 1; i <= n; i++) ",
            "    t[i][i] = 0; //2个顶点的 多边形 ",
            "    for (int r = 2; r <= n; r++) // 逐步增加顶点数 ",
            "    { ",
            "        for (int i = 1; i <= n - r + 1; i++) ",
            "\t{",
            "\t    int j = i + r - 1;",
            "\t    t[i][j] = t[i + 1][j] + weight(i - 1, i, j); ",
            "\t    s[i][j] = i; ",
            "\t    for (int k = i + 1; k < j; k++) //逐步移动顶点k的位置 ",
            "\t    { ",
            "\t\tint u = t[i][k] + t[k + 1][j] + weight(i - 1, k, j); ",
            "\t\tif (u < t[i][j])",
            "\t\t{ ",
            "\t\t    t[i][j] = u; ",
            "\t\t    s[i][j] = k; ",
            "\t\t} ",
            "\t    } ",
            "\t} ",
            "    } ",
            "}"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_cpot);
        initView();
        initAction();
        fixSlideConflict();
    }

    public void initView() {
        cpotView = (CpotView)findViewById(R.id.CpotView);
        mScrollView = (BottomScrollView) findViewById(R.id.main_scroll_view);
        bt_start = (Button)findViewById(R.id.bt_start);
        bt_nextstep = (Button)findViewById(R.id.bt_nextstep);
        bt_stepover = (Button)findViewById(R.id.bt_stepover);
        bt_stop = (Button)findViewById(R.id.bt_stop);
        bt_get_polygon = (Button)findViewById(R.id.bt_get_polygon);
        et_input = (EditText)findViewById(R.id.et_input);
        lv_code_list = (ListView)findViewById(R.id.lv_code_list);
        lv_list_code_proc = (ListView)findViewById(R.id.lv_code_list_prod);
        tv_intro = (TextView)findViewById(R.id.tv_intro);
        tableView_t = (TableView) findViewById(R.id.table_t);
        tableView_t.setColumnCount(8);
        tableView_s = (TableView) findViewById(R.id.table_s);
        tableView_s.setColumnCount(8);

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
        bt_get_polygon.setOnClickListener(this);

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        tableString = new TableString();
                        tableString = (TableString)msg.obj;
                        tableView_t.clearTableContents()
                                .setHeader(tableString.getmTableStr()[0]);
                        tableView_s.clearTableContents()
                                .setHeader(tableString.getsTableStr()[0]);
                        for (int i = 0; i < VertexNum-1; i++) {
                            tableView_t.addContent(tableString.getmTableStr()[i+1]);
                            tableView_s.addContent(tableString.getsTableStr()[i+1]);
                        }
                        tableView_t.refreshTable();
                        tableView_s.refreshTable();
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
                        tableView_t.clearTableContents()
                                .refreshTable();
                        tableView_s.clearTableContents()
                                .refreshTable();
                        break;
                }
            }
        };
    }

    public void initAction() {
        FileUtil fileUtil = FileUtil.getInstance();
        //读取txt
        InputStream inputStream = getResources().openRawResource(R.raw.dynamic_prog_cpot_redu);
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
        String[][] tTableStr = new String[VertexNum][VertexNum];
        String[][] sTableStr = new String[VertexNum][VertexNum];
        TableString tableStr = new TableString();

        public ThreadRun(Object lock) {
            this.lock = lock;
        }

        public void drawTable() {
            msg = new Message();
            tableStr.setmTableStr(tTableStr);
            tableStr.setsTableStr(sTableStr);
            msg.what = 1;
            msg.obj = tableStr;
            handler.sendMessage(msg);
        }

        public void initResultTable() {
            t = new int[VertexNum][VertexNum];
            s = new int[VertexNum][VertexNum];
            for (int i = 0; i < VertexNum; i++) {
                for (int j = 0; j < VertexNum; j++){
                    t[i][j] = -1;
                    s[i][j] = -1;
                    tTableStr[i][j] = " ";
                    sTableStr[i][j] = " ";
                }
            }
            for (int i = 0; i < VertexNum ; i++) {
                t[0][i] = i;
                t[i][0] = i;
                tTableStr[0][i] = i + "";
                tTableStr[i][0] = i + "";
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

        public void minWeightTriangulation(int n, int[][] t, int[][] s) {
            HightLightShowSingleLineCode(1);

            for (int i = 1; i <= n; i++)
            {
                HightLightShowSingleLineCode(3);

                HightLightShowSingleLineCode(4);
                t[i][i] = 0; //2个顶点的 多边形
                tTableStr[i][i] = t[i][i] + "";

                AddListCodeProc("i = " + i + ",t[" + i + "][" + i + "] = 0");
                drawTable();
                Pause();
            }
            for (int r = 2; r <= n; r++) // 逐步增加顶点数
            {
                HightLightShowSingleLineCode(5);
                for (int i = 1; i <= n - r + 1; i++)
                {
                    HightLightShowSingleLineCode(7);

                    HightLightShowSingleLineCode(9);
                    int j = i + r - 1;
                    AddListCodeProc("r = " + r + ", i = " + i + ", j = " + j);

                    HightLightShowSingleLineCode(10);
                    t[i][j] = t[i + 1][j] + weight(i - 1, i, j);
                    tTableStr[i][j] = t[i][j] + "";
                    String temp1 = "t[" + i + "][" + j + "] = t[" + (i + 1) + "][" + j + "] + weight(" + (i - 1) + "," + i + "," + j + ")";
                    String temp2 = " = " + t[i + 1][j] + " + " + weight(i - 1, i, j);
                    AddListCodeProc(temp1 + temp2 + " = " + t[i][j]);
                    drawTable();


                    Pause();


                    HightLightShowSingleLineCode(11);
                    AddListCodeProc("s[" + i + "][" + j + "] = " + i);
                    s[i][j] = i;
                    sTableStr[i][j] = s[i][j] + "";
                    drawTable();

                    Pause();

                    for (int k = i + 1; k < j; k++) //逐步移动顶点k的位置
                    {
                        HightLightShowSingleLineCode(12);

                        AddListCodeProc("k = " + k);
                        HightLightShowSingleLineCode(14);
                        int u = t[i][k] + t[k + 1][j] + weight(i - 1, k, j);
                        AddListCodeProc("u = " + "t[" + i + "][" + k + " + t[" + (k + 1) + "][" + j + "] + weight(" + (i - 1) + "," + k + "," + j + ") = " + u);

                        HightLightShowSingleLineCode(15);
                        if (u < t[i][j])
                        {
                            HightLightShowSingleLineCode(17);
                            AddListCodeProc("u < t[" + i + "][" + j + "] = " + t[i][j] + ",  t[" + i + "][" + j + "] = u = " + u);
                            t[i][j] = u;
                            tTableStr[i][j] = t[i][j] + "";
                            drawTable();

                            Pause();

                            HightLightShowSingleLineCode(18);
                            AddListCodeProc("s[" + i + "][" + j + "] = " + k);
                            s[i][j] = k;
                            sTableStr[i][j] = s[i][j] + "";
                            drawTable();

                            Pause();
                        }
                    }
                }
            }
            HightLightShowSingleLineCode(0);
        }

        //index1三角形第一个顶点序号,index2三角形第二个顶点序号,index3三角形第三个顶点序号
        public int weight(int index1, int index2, int index3)
        {
            Point p1 = cpotView.VertexPointList.get(index1);
            Point p2 = cpotView.VertexPointList.get(index2);
            Point p3 = cpotView.VertexPointList.get(index3);

            double dist3 = Math.sqrt(Math.pow((p1.x - p2.x) * 1.0, 2.0) + Math.pow((p1.y - p2.y) * 1.0, 2.0));
            double dist2 = Math.sqrt(Math.pow((p1.x - p3.x) * 1.0, 2.0) + Math.pow((p1.y - p3.y) * 1.0, 2.0));
            double dist1 = Math.sqrt(Math.pow((p3.x - p2.x) * 1.0, 2.0) + Math.pow((p3.y - p2.y) * 1.0, 2.0));

            int result = (int)(dist1 + dist2 + dist3);
            return result;
        }

        public void TraceBack(int i, int j, int[][] s)
        {
            if (i == j) return;
            if (i == 0){
                AddListCodeProc(String.format("与顶点%d和顶点%d之间构成三角形的是顶点%d",i,j,0));
                cpotView.TriangulationProcList.add(new ThreePoint(i, 0, j));
                cpotView.refresh();
                Pause();
                TraceBack(i, 0, s);
                TraceBack(1, j, s);
            } else {
                AddListCodeProc(String.format("与顶点%d和顶点%d之间构成三角形的是顶点%d",i,j,s[i][j]));
                cpotView.TriangulationProcList.add(new ThreePoint(i, s[i][j], j));
                cpotView.refresh();
                Pause();
                TraceBack(i, s[i][j], s);
                TraceBack(s[i][j]+1, j, s);
            }
        }

        public void run() {
            initResultTable();
            drawTable();
            AddListCodeProc("开始计算最优剖分");
            minWeightTriangulation(VertexNum -1, t, s);
            AddListCodeProc("计算最优剖分结束!");
            cpotView.isTriangulationProc = true;
            AddListCodeProc("开始剖分三角形");
            TraceBack(0, VertexNum - 1, s);
            AddListCodeProc("三角形剖分结束!");
            ++signal;
        }
    }

    public void reset() {
        cpotView.VertexPointList.clear();
        cpotView.isTriangulationProc = false;
        cpotView.isRunning = false;
        cpotView.isGetVertex = false;
        cpotView.isStepOver = false;
        signal = 0;
        t = null;
        s = null;
        et_input.setText("");
        VertexNum = 0;
        cpotView.refresh();
        msg = new Message();
        msg.what = 3;
        handler.sendMessage(msg);
    }

    @Override
    public void onClick(View v) {
        String str = et_input.getText().toString();
        Pattern pattern = Pattern.compile("^[0-9]*$");
        //如果是0-9组成的数字字符
        if (pattern.matcher(str).matches() && !str.equals("")){
            VertexNum = Integer.parseInt(str);
        }
        switch (v.getId()) {
            case R.id.bt_get_polygon:
                if (!cpotView.isGetVertex) {
                    if (VertexNum >=3 && VertexNum <=8 ) {
                        cpotView.isGetVertex = true;
                        cpotView.VertexNum = VertexNum;
                        cpotView.GetPolygon();
                        cpotView.refresh();
                    } else if (VertexNum > 8) {
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
                            .setMessage("亲，已经生成多边形！")
                            .setPositiveButton("确定" ,  null )
                            .show();
                }
                break;
            case R.id.bt_start:
                if (cpotView.isGetVertex){
                    if (!cpotView.isRunning) {
                        cpotView.isRunning = true;
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
                            .setMessage("亲，请先生成多边形！")
                            .setPositiveButton("确定" ,  null )
                            .show();
                }
                break;
            case R.id.bt_stepover:
                if (cpotView.isRunning) {
                    cpotView.isStepOver = true;
                }
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread (new Runnable() {
                            public void run() {
                                if (cpotView.isStepOver && signal == 0) {
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
