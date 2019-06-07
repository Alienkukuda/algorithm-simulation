package com.example.demo.AlgRecurAndDivCon.evo;

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

import com.example.demo.AlgRecurAndDivCon.customizeView.QuickSortView;
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

public class RecurAndDivConQuickSortActivity extends Activity implements View.OnClickListener {
    private QuickSortView quickSortView;
    private TableView tableView;
    private Button bt_nextstep,bt_stepover,bt_stop,bt_start_sort,bt_get_data;
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
    private static final String[] strs = new String[] {
            "//快速排序源代码(QuickSort函数)",
            "template<class Type>",
            "void QuickSort(Type a[], int p, int r)",
            "{",
            "    if (p < r) ",
            "    {",
            "        //以a[p]分基准为左右两半",
            "        int q=Partition(a,p,r); ",
            "        //对左半段排序",
            "        QuickSort (a,p,q-1); ",
            "        //对右半段排序",
            "        QuickSort (a,q+1,r); ",
            "    }",
            "}",
            "//快速排序源代码(Partition函数)",
            "template<class Type>",
            "int Partition(Type a[], int p, int r)",
            "{",
            "        int i = p, j = r+1; ",
            "        Type x = a[p];",
            "        //将<x的交换到左边区域，>x的交换到右边区域",
            "        while (true) ",
            "        {",
            "            while(a[++i] < x && i < r); ",
            "            while(a[--j] > x);",
            "            if (i >= j)  break; ",
            "            swap(a[i], a[j]);",
            "        }",
            "        a[p] = a[j];  ",
            "        a[j] = x;  ",
            "        return j;",
            "}"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recur_div_con_quick_sort);
        initView();
        initAction();
        fixSlideConflict();
    }

    public void initView() {
        quickSortView = (QuickSortView) findViewById(R.id.QuickSortView);
        mScrollView = (BottomScrollView) findViewById(R.id.main_scroll_view);
        bt_nextstep = (Button) findViewById(R.id.bt_nextstep);
        bt_stepover = (Button) findViewById(R.id.bt_stepover);
        bt_stop = (Button) findViewById(R.id.bt_stop);
        bt_start_sort = (Button) findViewById(R.id.bt_start_sort);
        bt_get_data = (Button)findViewById(R.id.bt_get_data);
        et_input = (EditText) findViewById(R.id.et_input);
        lv_code_list = (ListView) findViewById(R.id.lv_code_list);
        lv_list_code_proc = (ListView) findViewById(R.id.lv_code_list_prod);
        tv_intro = (TextView) findViewById(R.id.tv_intro);
        tableView = (TableView) findViewById(R.id.table);
        tableView.setColumnCount(13);
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
        bt_start_sort.setOnClickListener(this);
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
                        et_input.setText("");
                        count = 0;
                        procItem.clear();
                        mListViewAdapter.notifyDataSetChanged();
                        break;
                }
            }
        };
    }

    public void initAction() {
        FileUtil fileUtil = FileUtil.getInstance();
        //读取txt
        InputStream inputStream = getResources().openRawResource(R.raw.rec_divcon_quicksort_redu);
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

        quickSortView.setQuickSortActionListener(new QuickSortView.QuickSortActionListener() {

            @Override
            public void addListCodeProc(String newProc) {
                msg = new Message();
                msg.what = 1;
                msg.obj = newProc;
                handler.sendMessage(msg);
            }

            public void clearAll() {
                msg = new Message();
                msg.what = 2;
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

    @Override
    public void onClick(View v) {
        int elementSum = 0;
        //随机数
        int randomNum;
        String str = et_input.getText().toString();
        Pattern pattern = Pattern.compile("^[0-9]*$");
        //如果是0-9组成的数字字符
        if (pattern.matcher(str).matches() && !str.equals("")){
            elementSum = Integer.parseInt(str);
        }
        switch (v.getId()) {
            case R.id.bt_start_sort:
                if (quickSortView.isGetData){
                    if (!quickSortView.isSorting) {
                        quickSortView.isSorting = true;
                        //刷新
                        quickSortView.refresh();
                        thread1 = new Thread(quickSortView.new ThreadRun(lock));
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
            case R.id.bt_get_data:
                if ( !quickSortView.isGetData) {
                    if (elementSum >=2 && elementSum <= 12 ) {
                        quickSortView.isGetData = true;
                        Random random = new Random();
                        String[] headerStr = new String[elementSum+1];
                        String[] contentStr = new String[elementSum+1];
                        headerStr[0]="序号";
                        contentStr[0] = "数据";
                        quickSortView.dataArray = new int[elementSum];
                        for (int i = 1; i <= elementSum ; i++) {
                            randomNum = random.nextInt(90)+10;
//                            quickSortView.dataList.add(randomNum);
                            quickSortView.dataArray[i-1] = randomNum;
                            headerStr[i] = i + "";
                            contentStr[i] = randomNum + "";
                        }
                        //表格显示数据
                        tableView.clearTableContents()
                                .setHeader(headerStr)
                                .addContent(contentStr)
                                .refreshTable();

                    } else if (elementSum > 12) {
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
            case R.id.bt_stepover:
                if (quickSortView.isSorting) {
                    quickSortView.isStepOver = true;
                }
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread (new Runnable() {
                            public void run() {
                                if (quickSortView.isStepOver && quickSortView.signal == 0) {
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
                quickSortView.reset();
                break;
        }
    }
}
