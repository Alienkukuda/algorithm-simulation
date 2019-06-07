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

import com.example.demo.AlgRecurAndDivCon.customizeView.BinSearchView;
import com.example.demo.AlgRecurAndDivCon.customizeView.TowerOfHanoiView;
import com.example.demo.AlgRecurAndDivCon.entity.TextBox;
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

public class RecurAndDivConBinSearchActivity extends Activity implements View.OnClickListener {
    private BinSearchView binSearchView;
    private TableView tableView;
    private Button bt_nextstep,bt_stepover,bt_stop,bt_start_search,bt_get_data;
    private EditText et_input,et_search_num;
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
            "//二分搜索技术",
            "template<class Type>",
            "int BinarySearch(Type a[], const Type& x, int l, int r)",
            "{",
            "    while (r >= l)",
            "    {",
            "        int m = ( l + r ) / 2;",
            "        if (x == a[m]) return m;",
            "        if (x < a[m]) r = m - 1;",
            "        else l = m + 1;",
            "    }",
            "    return -1;",
            "}"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recur_div_con_bin_search);
        initView();
        initAction();
        fixSlideConflict();
    }

    public void initView() {
        binSearchView = (BinSearchView)findViewById(R.id.BinSearchView);
        mScrollView = (BottomScrollView) findViewById(R.id.main_scroll_view);
        bt_nextstep = (Button)findViewById(R.id.bt_nextstep);
        bt_stepover = (Button)findViewById(R.id.bt_stepover);
        bt_stop = (Button)findViewById(R.id.bt_stop);
        bt_start_search = (Button)findViewById(R.id.bt_start_search);
        bt_get_data = (Button)findViewById(R.id.bt_get_data);
        et_input = (EditText)findViewById(R.id.et_input);
        et_search_num = (EditText)findViewById(R.id.et_search_num);
        lv_code_list = (ListView)findViewById(R.id.lv_code_list);
        lv_list_code_proc = (ListView)findViewById(R.id.lv_code_list_prod);
        tv_intro = (TextView)findViewById(R.id.tv_intro);
        tableView = (TableView) findViewById(R.id.table);
        //自定义tableview一列的宽度只会在第一次定死，所以尽可能将列数定到max
        tableView.setColumnCount(13);
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
        bt_start_search.setOnClickListener(this);
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
                        et_search_num.setText("");
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
        InputStream inputStream = getResources().openRawResource(R.raw.rec_divcon_binsearch_redu);
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

        binSearchView.setBinSearchActionListener(new BinSearchView.BinSearchActionListener() {
            @Override
            public void HightLightShowSingleLineCode(int index) {
                lv_code_list.performItemClick(lv_code_list.getChildAt(index), index, lv_code_list.getItemIdAtPosition(index));
            }

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
        String searchNumStr = et_search_num.getText().toString();
        Pattern pattern = Pattern.compile("^[0-9]*$");
        //如果是0-9组成的数字字符
        if (pattern.matcher(str).matches() && !str.equals("")){
            elementSum = Integer.parseInt(str);
        }
        switch (v.getId()) {
            case R.id.bt_get_data:
                if ( !binSearchView.isGetData) {
                    if (elementSum >=1 && elementSum <= 12 ) {
                        binSearchView.isGetData = true;
                        Random random = new Random();
//                    String headerStr = "\"序号\"",contentStr = "\"数据\"";
                        String[] headerStr = new String[elementSum+1];
                        String[] contentStr = new String[elementSum+1];
                        headerStr[0]="序号";
                        contentStr[0] = "数据";
                        for (int i = 1; i <= elementSum ; i++) {
                            randomNum = random.nextInt(90)+10;
                            binSearchView.dataList.add(randomNum);
                            headerStr[i] = i + "";
                            contentStr[i] = randomNum + "";
                        }
                        //表格显示数据
                        tableView.clearTableContents()
                                .setHeader(headerStr)
                                .addContent(contentStr)
                                .refreshTable();
                        binSearchView.SetAllElementLocation();
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
            case R.id.bt_start_search:
                if (binSearchView.isGetData) {
                    if (pattern.matcher(searchNumStr).matches() && !searchNumStr.equals("")) {
                        if (!binSearchView.isRunning) {
                            binSearchView.findNum = Integer.parseInt(searchNumStr);
                            binSearchView.isRunning = true;
                            thread1 = new Thread(binSearchView.new ThreadRun(lock));
                            thread1.start();
                        } else {
                            builder.setTitle("提示")
                                    .setMessage("亲，重新开始请停止运行！")
                                    .setPositiveButton("确定" ,  null )
                                    .show();
                        }
                    } else {
                        builder.setTitle("提示")
                                .setMessage("亲，请正确填写要查找的元素！")
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
                if (binSearchView.isRunning) {
                    binSearchView.isStepOver = true;
                }
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread (new Runnable() {
                            public void run() {
                                if (binSearchView.isStepOver && binSearchView.signal == 0) {
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
                binSearchView.reset();
                tableView.clearTableContents()
                        .refreshTable();
                break;
        }
    }
}
