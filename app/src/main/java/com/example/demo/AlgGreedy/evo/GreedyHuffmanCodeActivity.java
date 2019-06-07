package com.example.demo.AlgGreedy.evo;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.demo.AlgGreedy.customizeView.HuffmanCodeView;
import com.example.demo.AlgGreedy.entity.CodeChar;
import com.example.demo.AlgGreedy.entity.Tree;
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

public class GreedyHuffmanCodeActivity extends Activity implements View.OnClickListener {

    private HuffmanCodeView huffmanCodeView;
    private TableView tableView;
    private Button bt_nextstep,bt_stepover,bt_stop,bt_start,bt_get_huffman_code;
    private EditText et_input;
    private ListView lv_list_code_proc;
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

    private int CodeCharNum;

    String[][] TableStr = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greedy_huffman_code);
        initView();
        initAction();
        fixSlideConflict();
    }

    public void initView() {
        mScrollView = (BottomScrollView) findViewById(R.id.main_scroll_view);
        bt_start = (Button)findViewById(R.id.bt_start);
        bt_nextstep = (Button)findViewById(R.id.bt_nextstep);
        bt_stepover = (Button)findViewById(R.id.bt_stepover);
        bt_stop = (Button)findViewById(R.id.bt_stop);
        bt_get_huffman_code = (Button)findViewById(R.id.bt_get_huffman_code);
        et_input = (EditText)findViewById(R.id.et_input);
        lv_list_code_proc = (ListView)findViewById(R.id.lv_code_list_prod);
        tv_intro = (TextView)findViewById(R.id.tv_intro);
        tableView = (TableView) findViewById(R.id.table);
        tableView.setColumnCount(9);
        huffmanCodeView = (HuffmanCodeView) findViewById(R.id.HuffmanCodeView);

        builder =  new AlertDialog.Builder(this);

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
        bt_get_huffman_code.setOnClickListener(this);

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
                        CodeCharNum = 0;
                        TableStr = null;
                        count = 0;
                        procItem.clear();
                        mListViewAdapter.notifyDataSetChanged();
                        huffmanCodeView.refresh();
                        tableView.clearTableContents()
                                .refreshTable();
                        break;
                    case 3:
                        String[] ThirdTableStr = (String[])msg.obj;
                        for (int i = 0; i < ThirdTableStr.length; i++) {
                            TableStr[2][i] = ThirdTableStr[i];
                        }
                        showTable();
                        break;
                }
            }
        };
    }

    public void initAction() {
        FileUtil fileUtil = FileUtil.getInstance();
        //读取txt
        InputStream inputStream = getResources().openRawResource(R.raw.greedy_alg_huffman_code);
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

        huffmanCodeView.setHuffmanCodeActionLister(new HuffmanCodeView.HuffmanCodeActionLister() {
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
            public void refreshTable(String[] ThirdTableStr) {
                msg = new Message();
                msg.what = 3;
                msg.obj = ThirdTableStr;
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

    public void initTable() {
        TableStr = new String[3][CodeCharNum+1];
        char baseChar = 'A';
        Random random = new Random();
        int sum = 0;
        for (int i = 0; i < CodeCharNum; i++){
            int rate;
            if (i == CodeCharNum - 1)  //最后一个编码字符
            {
                rate = 100 - sum;
                CodeChar codeChar = new CodeChar(baseChar++, rate);
                huffmanCodeView.codeCharList.add(codeChar);
            }
            else   //不是最后一个
            {
                if (i == 0)
                {
                    rate = random.nextInt(19)+1;
                }
                else
                {
                    rate = random.nextInt(99 - sum - (CodeCharNum - i))+1;
                }
                CodeChar codeChar = new CodeChar(baseChar++, rate);
                huffmanCodeView.codeCharList.add(codeChar);
                sum += rate;
            }
        }

        for (int i = 1; i < huffmanCodeView.codeCharList.size() + 1; i++) {
            TableStr[0][i] = String.valueOf(huffmanCodeView.codeCharList.get(i-1).Char);
            TableStr[1][i] = String.valueOf(huffmanCodeView.codeCharList.get(i-1).Rate);
            TableStr[2][i] = " ";
        }
        TableStr[0][0] = "编码";
        TableStr[1][0] = "频率";
        TableStr[2][0] = " ";
    }

    public void showTable() {
        tableView.clearTableContents()
                .setHeader(TableStr[0])
                .addContent(TableStr[1])
                .addContent(TableStr[2])
                .refreshTable();
    }

//    public void reset(){
//        isRunning = false;
//        isGetCodeTable = false;
//        isStepOver = false;
//        CodeCharNum = 0;
//        TableStr = null;
//        et_input.setText("");
//        codeCharList.clear();
//        treeList.clear();
//        treeLocationList.clear();
//        msg = new Message();
//        msg.what = 3;
//        handler.sendMessage(msg);
//    }

    @Override
    public void onClick(View v) {
        String str = et_input.getText().toString();
        Pattern pattern = Pattern.compile("^[0-9]*$");
        if (pattern.matcher(str).matches() && !str.equals("")){
            CodeCharNum = Integer.parseInt(str);
        }
        switch (v.getId()) {
            case R.id.bt_get_huffman_code:
                if (!huffmanCodeView.isGetCodeTable) {
                    if (CodeCharNum >=1 && CodeCharNum <=8 ) {
                        huffmanCodeView.isGetCodeTable = true;
                        huffmanCodeView.CodeCharNum = CodeCharNum;
                        initTable();
                        showTable();
                    } else if (CodeCharNum > 8) {
                        builder.setTitle("提示")
                                .setMessage("亲，频率过大或过小！")
                                .setPositiveButton("确定" ,  null )
                                .show();
                    } else {
                        builder.setTitle("提示")
                                .setMessage("亲，请输入正确的频率数值！")
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
                if (huffmanCodeView.isGetCodeTable) {
                    if (!huffmanCodeView.isRunning) {
                        huffmanCodeView.isRunning = true;
                        thread1 = new Thread(huffmanCodeView.new ThreadRun(lock));
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
                if (huffmanCodeView.isRunning) {
                    huffmanCodeView.isStepOver = true;
                }
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread (new Runnable() {
                            public void run() {
                                if (huffmanCodeView.isStepOver && huffmanCodeView.signal == 0) {
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
                huffmanCodeView.reset();
                break;
        }
    }
}
