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

import com.example.demo.AlgBranchAndBound.customizeView.BranchAndBoundSSSPPView;
import com.example.demo.AlgBranchAndBound.entity.MinHeapNode;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import static com.example.demo.AlgBranchAndBound.customizeView.BranchAndBoundSSSPPView.MaxValue;

public class BranchAndBoundSSSPPActivity extends Activity implements View.OnClickListener {
    private int signal = 0;
    private BranchAndBoundSSSPPView branchAndBoundSSSPPView;
    private TableView table_distance,table_pre;
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
    //源头顶点是否正确
    private boolean isCorrect = false;
    //表格数组dis
    private String[] tableDisStrHeader = null;
    //表格数组pre
    private String[] tablePreStrHeader = null;
    //dis的Content
    private String[][] tableDisStrContentArray;
    //pre的Content
    private String[][] tablePreStrContentArray;
    //源头顶点
    private int SourceVertexIndex = -1;
    //是否开始
    public boolean isRunning = false;
    //是否全速运行
    public boolean isStepOver = false;
    //源头顶点到顶点i的最短特殊路径长度
    public int[] dist = null;
    //邻接矩阵
    private int[][] c = null;
    //前驱顶点数组
    private int[] prev = null;

    private int rowIndex = 1;

    private static final String[] strs = new String[] {
            "//队列优先式实现-核心函数",
            "template <class Type> ",
            "void Graph<Type>::ShortestPaths(int v) ",
            "{ ",
            "    MinHeap<MinHeadNode<Type>> H(1000); //定义初始扩展结点 ",
            "    MinHeapNode<Type> E; ",
            "    E.i=v;     ",
            "    E.length=0; ",
            "    dist[v]=0;",
            "    while (true) ",
            "    { ",
            "        for(int j =1; j <= n; j++)",
            "        { ",
            "            if((c[E.i][j]<inf)&&(E.length+c[E.i][j]<dist[j])) ",
            "            { ",
            "                //顶点i到顶点j可达，且满足控制约束 ",
            "                dist[j]=E.length+c[E.i][j]; ",
            "                prev[j]=E.i; // 加入活结点优先队列 ",
            "                MinHeapNode<Type> N; ",
            "                N.i=j; ",
            "                N.length=dist[j]; ",
            "                H.Insert(N); ",
            "            }",
            "        } ",
            "        if (H.Empty)",
            "        {",
            "            //优先队列空 ",
            "            break;",
            "        }",
            "        else",
            "        {",
            "            //取下一扩展结点 ",
            "            E = H.MinData;",
            "            H.DeleteMin();",
            "        }",
            "    } ",
            "}"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_bound_ssspp);
        initView();
        initAction();
        fixSlideConflict();
    }

    public class ThreadRun implements Runnable {
        private Object lock;

        public ThreadRun(Object lock) {
            this.lock = lock;
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

        public synchronized void AddListCodeProc(String str) {
            msg = new Message();
            msg.what = 3;
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

        public void doWork() {
            prev = new int[VertexNum];
            AddListCodeProc("开始搜索");
            ShortestPaths(SourceVertexIndex);
            AddListCodeProc("结束搜索");
        }

        //队列优先式实现-核心函数
        private void ShortestPaths(int v){
            int rowCount = 1;
            MinHeapNode[] minHeapNodes = new MinHeapNode[1000];
            Heap<MinHeapNode> H = new Heap<MinHeapNode>(minHeapNodes); //定义初始扩展结点
            MinHeapNode E = new MinHeapNode(v, 0);
            for (int i = 0; i < VertexNum; i++)
                dist[i] = i == v ? 0 : MaxValue;
            UpdataDist(rowCount);

            for (int i = 0; i < VertexNum; i++)
                prev[i] = -1;
            UpdataPrev(rowCount);
            rowCount++;

            while (true)
            {
                AddListCodeProc(String.format("E.i = %d,E.length = %d", E.i, E.length));
                for (int j = 0; j < VertexNum; j++)
                {
                    if (E.i != j)
                    {
                        if (c[E.i][j] < MaxValue)
                        {

                            if (E.length + c[E.i][j] < dist[j])
                            {
                                //顶点i到顶点j可达，且满足控制约束
                                dist[j] = E.length + c[E.i][j];
                                UpdataDist(rowCount);

                                prev[j] = E.i; // 加入活结点优先队列
                                UpdataPrev(rowCount);
                                rowCount++;

                                MinHeapNode N = new MinHeapNode(j, dist[j]);
                                H.Insert(N);
                                String temp = "j=" + j + ",dist[" + j + "]=" + (dist[j] == MaxValue ? "∞" : dist[j]) + ",prev[" + j + "]=" + prev[j];
                                AddListCodeProc(temp);
                                AddListCodeProc("把" + j + "结点插入队列，队列：" + ToString_BranchAndBoundSSSPP(H));
                            }
                            else
                            {
                                //E.length + c[E.i, j] >= dist[j]
                            }
                        }
                        else if(c[E.i][j] == MaxValue)
                        {
                            String temp = "j=" + j + ",c[E.i][" + j + "] =∞, 不处理" + "    队列：" + ToString_BranchAndBoundSSSPP(H);
                            AddListCodeProc(temp);
                        }
                    }
                    else
                    {
                        //E.i == j
                    }

                }

                if (H.Empty())
                {
                    //优先队列为空
                    AddListCodeProc("优先队列为空，结束循环");
                    break;
                }
                else
                {
                    //取下一扩展结点
                    E = H.MinData();
                    AddListCodeProc("取下一扩展结点,j=" + E.i);
                    H.DeleteMin();
                }
            }
        }
        //更新距离
        private void UpdataDist(int rowindex)
        {
            int columnCount = 1;
            tableDisStrContentArray[rowindex-1][0] = rowindex + "";

            for (int i = 0; i < VertexNum; i++)
            {
                if (i != SourceVertexIndex)
                {
                    tableDisStrContentArray[rowindex-1][columnCount++] = dist[i] == MaxValue ? "∞" : String.valueOf(dist[i]);
                }
            }
            drawDisTableContent();
            Pause();
        }
        //更新前驱
        private void UpdataPrev(int rowindex)
        {
            int columnCount = 1;
            tablePreStrContentArray[rowindex-1][0] = rowindex + "";

            for (int i = 0; i < VertexNum; i++)
            {
                if (i != SourceVertexIndex)
                {
                    tablePreStrContentArray[rowindex-1][columnCount++] = prev[i] == -1 ? " " : String.valueOf(prev[i]);
                }
            }
            drawPreTableContent();
            Pause();
        }

        public void drawDisTableContent(){
            msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
        }

        public void  drawPreTableContent(){
            msg = new Message();
            msg.what = 2;
            handler.sendMessage(msg);
        }

        public String ToString_BranchAndBoundSSSPP(Heap<MinHeapNode> H)
        {
            String str = "[";
            for (int i = 1; i <= H.getSize(); i++)
            {
                str += "(" + H.getData()[i].i + "," + H.getData()[i].length + ")";
                if (i != H.getSize())
                    str += ",";
            }
            return str + "]";
        }

        public void run() {
            doWork();
            ++signal;
        }
    }

    public void initResultTable() {
        tableDisStrHeader = new String[VertexNum];
        tablePreStrHeader = new String[VertexNum];
        tableDisStrContentArray = new String[15][VertexNum];
        tablePreStrContentArray = new String[15][VertexNum];
        for (int i = 0; i < VertexNum; i++){
            tableDisStrHeader[i] = " ";
            tablePreStrHeader[i] = " ";
        }
        tableDisStrHeader[0] = "距离";
        tablePreStrHeader[0] = "前驱";
        int count = 1;
        for (int i = 0; i < VertexNum; i++) {
            if (SourceVertexIndex!=i) {
                tableDisStrHeader[count] = "dist[" + i + "]";
                tablePreStrHeader[count++] = "prev[" + i + "]";
            }
        }
    }

    public void drawResultTableHeader() {
        table_distance.clearTableContents()
                .setHeader(tableDisStrHeader)
                .refreshTable();
        table_pre.clearTableContents()
                .setHeader(tablePreStrHeader)
                .refreshTable();
    }

    public void initView() {
        branchAndBoundSSSPPView = (BranchAndBoundSSSPPView)findViewById(R.id.BranchAndBoundSSSPPView);
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
        table_distance = (TableView) findViewById(R.id.table_distance);
        table_distance.setColumnCount(8);
        table_pre = (TableView) findViewById(R.id.table_pre);
        table_pre.setColumnCount(8);

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
                        table_distance.clearTableContents()
                                .setHeader(tableDisStrHeader);
                        for (int i = 0; i < rowIndex; i++) {
                            table_distance.addContent(tableDisStrContentArray[i]);
                        }
                        table_distance.refreshTable();

//                        String[][] str = (String[][])msg.obj;
                        break;
                    case 2:
                        table_pre.clearTableContents()
                                .setHeader(tablePreStrHeader);
                        for (int i = 0; i < rowIndex; i++) {
                            table_pre.addContent(tablePreStrContentArray[i]);
                        }
                        table_pre.refreshTable();
                        rowIndex++;
                        break;
                    case 3:
                        String newProcWithCount = (count++) + ":" +msg.obj;
                        procItem.add(newProcWithCount);
                        mListViewAdapter.notifyDataSetChanged();
                        int index = procItem.size() - 1;
                        lv_list_code_proc.performItemClick(lv_list_code_proc.getChildAt(index), index, lv_list_code_proc.getItemIdAtPosition(index));
                        break;
                    case 4:
                        count = 0;
                        procItem.clear();
                        et_input.setText("");
                        et_source_point.setText("");
                        mListViewAdapter.notifyDataSetChanged();
                        tableDisStrHeader = null;
                        tablePreStrHeader = null;
                        tableDisStrContentArray = null;
                        tablePreStrContentArray= null;
                        rowIndex = 1;
                        table_pre.clearTableContents()
                                .refreshTable();
                        table_distance.clearTableContents()
                                    .refreshTable();
                        break;
                }
            }
        };
    }

    public void initAction() {
        FileUtil fileUtil = FileUtil.getInstance();
        //读取txt
        InputStream inputStream = getResources().openRawResource(R.raw.branch_bound_ssspp_redu);
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
        VertexNum = 0;
        signal = 0;
        dist = null;
        prev = null;
        c = null;
        isCorrect = false;
        isRunning = false;
        isStepOver = false;

        msg = new Message();
        msg.what = 4;
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
                if (!isRunning) {
                    if (VertexNum >=3 && VertexNum <=8 ) {
                        branchAndBoundSSSPPView.VertexNum = VertexNum;
                        branchAndBoundSSSPPView.initGraph();
                        c = branchAndBoundSSSPPView.c;
                        dist = branchAndBoundSSSPPView.dist;
                        branchAndBoundSSSPPView.isGetGraph = true;
                        branchAndBoundSSSPPView.refresh();
//                        drawResultTable();
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
                    if (!isRunning){
                        isRunning = true;
//                        initResultTable();
//                        initTableData();
                        initResultTable();
                        drawResultTableHeader();
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
                if (isRunning && !isStepOver) {
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
                                    System.out.println("k");
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
                branchAndBoundSSSPPView.reset();
                clear();
                break;
        }
    }
}
