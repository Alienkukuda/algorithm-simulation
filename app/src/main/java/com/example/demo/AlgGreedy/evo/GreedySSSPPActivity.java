package com.example.demo.AlgGreedy.evo;

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
import com.example.demo.AlgGreedy.customizeView.GreedySSSPPView;
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

import static com.example.demo.AlgGreedy.customizeView.GreedySSSPPView.MaxValue;

public class GreedySSSPPActivity extends Activity implements View.OnClickListener {
    private int signal = 0;
    private GreedySSSPPView greedySSSPPView;
    private TableView tableView;
    private Button bt_nextstep,bt_stepover,bt_stop,bt_start,bt_get_img;
    private EditText et_input,et_source_point;
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
    //顶点个数
    private int VertexNum = 0;

    private boolean isCorrect = false;
    //表格数组
    private String[][] TableStr = null;
    //源头顶点
    private int SourceVertexIndex = -1;
    //邻接矩阵
    private int[][] c = null;
    //源头顶点到顶点i的最短特殊路径长度
    public int[] dist = null;

    private static final String[] strs = new String[] {
            "//Dijkstra算法实现",
            "void Dijkstra(int n, int v, int *dist, int *prev, int **c) ",
            "{ ",
            "    bool s[maxint]; //判断是否已存入该点到S集合中 ",
            "    for(int i = 0; i <n ; ++i)",
            "    { ",
            "        dist[i] = c[v][i]; ",
            "        s[i] = 0; //所有点都未加入集合S ",
            "        if(dist[i] == maxint) prev[i] = 0; ",
            "        else prev[i] = v; ",
            "    } ",
            "    dist[v] = 0; ",
            "    s[v] = 1;",
            "    for(int i = 1; i < n; ++i)",
            "    { ",
            "        //找出当前未使用的点j的dist[j]最小值 ",
            "        int tmp = maxint, u = v;",
            "        for(int j = 0; j < n; j++) ",
            "        {",
            "            if((!s[j]) && dist[j] < tmp)",
            "            { ",
            "                u = j; ",
            "                tmp = dist[j]; ",
            "            } ",
            "\t}",
            "        s[u] = 1; //表示u点已加入S集合 ",
            "        for(int j = 0; j < n; j++) //更新dist ",
            "\t{",
            "            if((!s[j]) && c[u][j] < maxint)",
            "            { ",
            "                int newdist = dist[u] + c[u][j]; ",
            "                if(newdist < dist[j])",
            "                { ",
            "                    dist[j] = newdist; ",
            "                    prev[j] = u; ",
            "                }  ",
            "\t    }",
            "        }",
            "    } ",
            "}"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greedy_ssspp);
        initView();
        initAction();
        fixSlideConflict();
    }

    public class ThreadRun implements Runnable {
        private Object lock;

        public ThreadRun(Object lock) {
            this.lock = lock;
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

        public void Dijkstra(int n, int v, int[] dist, int[] prev, int[][] c)
        {
            int rowCount = 2;
            HightLightShowSingleLineCode(1);
            int maxint = MaxValue;
            HightLightShowSingleLineCode(3);
            boolean[] s = new  boolean[VertexNum]; // 判断是否已存入该点到S集合中
            AddListCodeProc("S集合 " + showSetSMsg(s));

            for(int i = 0; i < n; i++)
            {
                HightLightShowSingleLineCode(4);

                HightLightShowSingleLineCode(6);
                dist[i] = c[v][i];
                AddListCodeProc("dist[" + i + "] = c[" + v + "][" + i + "] = " + (dist[i] == MaxValue ? "maxint" : dist[i]));
                HightLightShowSingleLineCode(7);
                s[i] = false;   //所有点都未加入集合S

                AddListCodeProc("s[" + i + "] = " + "False " + showSetSMsg(s));

                HightLightShowSingleLineCode(8);
                if (dist[i] == maxint)
                {
                    prev[i] = -1;
                    AddListCodeProc("prev[" + i + "] = -1");
                }
                else
                {
                    HightLightShowSingleLineCode(9);
                    prev[i] = v;
                    AddListCodeProc("prev[" + i + "] = " + v);
                }
            }

            HightLightShowSingleLineCode(11);
            dist[v] = 0;
            AddListCodeProc("dist[" + v + "] = 0");
            HightLightShowSingleLineCode(12);
            s[v] = true;
            AddListCodeProc("s[" + v + "] = True " + showSetSMsg(s));

            for (int i = 1; i < n; i++)
            {
                HightLightShowSingleLineCode(13);
                AddListCodeProc("i = " + i);
                //找出当前未使用的点j的dist[j]最小值
                HightLightShowSingleLineCode(16);
                int tmp = maxint, u = v;
                AddListCodeProc("temp = maxint, u = " + v);
                for (int j = 0; j < n; j++)
                {
                    HightLightShowSingleLineCode(17);
                    AddListCodeProc("j = " + j);
                    HightLightShowSingleLineCode(18);

                    if ((!s[j]) && dist[j] < tmp)
                    {
                        HightLightShowSingleLineCode(19);

                        HightLightShowSingleLineCode(21);
                        u = j;
                        AddListCodeProc("u = " + j);
                        HightLightShowSingleLineCode(22);
                        tmp = dist[j];
                        AddListCodeProc("temp = dist[" + j + "] = " + tmp);
                    }
                    HightLightShowSingleLineCode(24);
                }

                HightLightShowSingleLineCode(25);
                s[u] = true; // 表示u点已加入S集合
                AddListCodeProc("s[" + u + "] = True " + showSetSMsg(s));

                for (int j = 0; j < n; j++) // 更新dist
                {
                    HightLightShowSingleLineCode(26);
                    AddListCodeProc("j = " + j);
                    HightLightShowSingleLineCode(27);

                    if ((!s[j]) && c[u][j] < maxint)
                    {
                        HightLightShowSingleLineCode(28);
                        AddListCodeProc("s[" + j + "] = " + s[j] + ", c[" + u + "][" + j + "] = " + (maxint == MaxValue ? "maxint" : maxint));

                        HightLightShowSingleLineCode(30);
                        int newdist = dist[u] + c[u][j];
                        AddListCodeProc("newdist = dist[" + u + "] + c[" + u + "][" + j + "] = " + dist[u] + " + " + c[u][j] + " = " + newdist);
                        if (newdist < dist[j])
                        {
                            HightLightShowSingleLineCode(31);
                            AddListCodeProc("dist[" + j + "] = " + dist[j]);

                            HightLightShowSingleLineCode(33);
                            dist[j] = newdist;
                            AddListCodeProc("dist[" + j + "] = " + dist[j]);
                            HightLightShowSingleLineCode(34);
                            prev[j] = u;
                            AddListCodeProc("prev[" + j + "] = " + u);
                        }
                    }
                    HightLightShowSingleLineCode(37);
                }
                AddListCodeProc("更新dist");

                updataDist(s, dist, String.valueOf(u),rowCount);
                rowCount++;
            }
            HightLightShowSingleLineCode(0);
        }

        private void updataDist(boolean[] setS,int[] dist,String u,int rowCount)
        {
            TableStr[rowCount][0] = rowCount - 1 +"";

            String s = "{";
            for (int i = 0; i < setS.length; i++)
            {
                if (setS[i])
                {
                    s += i + ",";
                }
            }
            if (s.endsWith(","))
            {
                s = s.substring(0,s.length()-1);
            }
            s += "}";
            TableStr[rowCount][1] = s;
            TableStr[rowCount][2] = u;

            int count = 3;
            for (int i = 0; i < VertexNum; i++)
            {
                if (i != SourceVertexIndex)
                {
                    TableStr[rowCount][count++] = dist[i] == MaxValue ? "∞" : String.valueOf(dist[i]);
                }
            }
            drawTable();

        }

        private String showSetSMsg(boolean[] s)
        {
            String str = "";
            for (int i = 0; i < s.length; i++)
            {
                str += "s[" + i + "]=" + (s[i] ? "1" : "0") + " ";
            }
            return str;
            //SetShowProcMsg("集合S:" + str);
        }

        public void drawTable() {
            msg = new Message();
            msg.what = 1;
            msg.obj = TableStr;
            handler.sendMessage(msg);
        }

        public void doWork() {
            int[] prev = new int[VertexNum];
            dist = greedySSSPPView.dist;
            AddListCodeProc("开始查找");
            Dijkstra(VertexNum, SourceVertexIndex, dist, prev, c);
            AddListCodeProc("查找结束!");
        }

        public void run() {
            doWork();
            ++signal;
        }
    }

    public void initResultTable() {
        TableStr = new String[VertexNum+1][VertexNum+2];
        for (int i = 0; i < VertexNum+1; i++) {
            for (int j = 0; j < VertexNum+2; j++){
                TableStr[i][j] = " ";
            }
        }
        TableStr[0][0] = "迭代";
        TableStr[0][1] = "S";
        TableStr[0][2] = "u";
    }

    public void initTableData() {
        int count = 3;
        for (int i = 0; i < VertexNum; i++) {
            if (SourceVertexIndex!=i) {
                TableStr[0][count] = "dist[" + i + "]";
                TableStr[1][count++] = c[SourceVertexIndex][i] == MaxValue ? "∞" : String.valueOf(c[SourceVertexIndex][i]);
            }
        }
        TableStr[1][0] = "初始";
        TableStr[1][1] = "{" + SourceVertexIndex + "}";
        TableStr[1][0] = "-";
    }

    public void drawResultTable() {
        tableView.clearTableContents()
                .setHeader(TableStr[0]);
        for (int i = 1; i < VertexNum + 1; i++) {
            tableView.addContent(TableStr[i]);
        }
        tableView.refreshTable();
    }

    public void initView() {
        greedySSSPPView = (GreedySSSPPView)findViewById(R.id.GreedySSSPPView);
        mScrollView = (BottomScrollView) findViewById(R.id.main_scroll_view);
        bt_get_img = (Button)findViewById(R.id.bt_get_img);
        bt_nextstep = (Button)findViewById(R.id.bt_nextstep);
        bt_stepover = (Button)findViewById(R.id.bt_stepover);
        bt_stop = (Button)findViewById(R.id.bt_stop);
        et_input = (EditText)findViewById(R.id.et_input);
        et_source_point =(EditText)findViewById(R.id.et_source_point);
        bt_start = (Button)findViewById(R.id.bt_start);
        lv_code_list = (ListView)findViewById(R.id.lv_code_list);
        lv_list_code_proc = (ListView)findViewById(R.id.lv_code_list_prod);
        tv_intro = (TextView)findViewById(R.id.tv_intro);
        tableView = (TableView) findViewById(R.id.table);
        tableView.setColumnCount(10);

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
        bt_get_img.setOnClickListener(this);

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        String[][] str = (String[][])msg.obj;
                        tableView.clearTableContents()
                                .setHeader(str[0]);
                        for (int i = 0; i < VertexNum; i++) {
                            tableView.addContent(str[i+1]);
                        }
                        tableView.refreshTable();
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
                        et_input.setText("");
                        et_source_point.setText("");
                        mListViewAdapter.notifyDataSetChanged();
                        tableView.clearTableContents()
                                .refreshTable();
                        break;
                }
            }
        };
    }

    public void initAction() {
        FileUtil fileUtil = FileUtil.getInstance();
        //读取txt
        InputStream inputStream = getResources().openRawResource(R.raw.greedy_alg_ssspp_redu);
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

    public void clear() {
        SourceVertexIndex = -1;
        signal = 0;
        VertexNum = 0;
        dist = null;
        c = null;
        isCorrect = false;
        TableStr = null;
        msg = new Message();
        msg.what = 3;
        handler.sendMessage(msg);
    }

    public void onClick(View v) {
        String str = et_input.getText().toString();
        String courcePointStr = et_source_point.getText().toString();
        Pattern pattern = Pattern.compile("^[0-9]*$");
        //如果是0-9组成的数字字符
        if (pattern.matcher(str).matches() && !str.equals("")){
            VertexNum = Integer.parseInt(str);
        }
        switch (v.getId()) {
            case R.id.bt_get_img:
                if (!greedySSSPPView.isRunning) {
                    if (VertexNum >=3 && VertexNum <=8 ) {
                        greedySSSPPView.VertexNum = VertexNum;
                        greedySSSPPView.initGraph();
                        c = greedySSSPPView.c;
                        greedySSSPPView.isGetGraph = true;
                        greedySSSPPView.refresh();
                        initResultTable();
                        drawResultTable();

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
                            .setMessage("亲，重新开始请停止运行！")
                            .setPositiveButton("确定" ,  null )
                            .show();
                }
                break;
            case R.id.bt_start:
                if (pattern.matcher(courcePointStr).matches() && !courcePointStr.equals("")){
                    int SourcePoint = Integer.parseInt(courcePointStr);
                    for (int i = 0; i < VertexNum; i++) {
                        if(SourcePoint == i){
                            isCorrect = true;
                            SourceVertexIndex = SourcePoint;
                        }
                    }
                }
                if (isCorrect) {
                    if (!greedySSSPPView.isRunning){
                        greedySSSPPView.isRunning = true;
                        initTableData();
                        drawResultTable();
                        thread1 = new Thread(new ThreadRun(lock));
                        thread1.start();
                    } else {
                        builder.setTitle("提示")
                                .setMessage("亲，程序已经开始了！")
                                .setPositiveButton("确定" ,  null )
                                .show();
                    }
                }else {
                    builder.setTitle("提示")
                            .setMessage("亲，请填写正确的源头顶点！")
                            .setPositiveButton("确定" ,  null )
                            .show();
                }
                break;
            case R.id.bt_stepover:
                if (greedySSSPPView.isRunning) {
                    greedySSSPPView.isStepOver = true;
                }
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread (new Runnable() {
                            public void run() {
                                if (greedySSSPPView.isStepOver && signal == 0) {
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
                greedySSSPPView.reset();
                clear();
                break;
        }
    }
}
