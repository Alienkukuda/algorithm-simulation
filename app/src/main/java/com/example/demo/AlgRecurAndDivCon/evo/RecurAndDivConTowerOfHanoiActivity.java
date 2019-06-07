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

import com.example.demo.AlgRecurAndDivCon.entity.TextBox;
import com.example.demo.FileUtil.FileUtil;
import com.example.demo.adapter.ListViewAdapter;
import com.example.demo.thread.ThreadNotify;
import com.example.demo.AlgRecurAndDivCon.customizeView.TowerOfHanoiView;
import com.example.demo.R;
import com.example.demo.view.BottomScrollView;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

/**
 * @Author captain
 * @Description hanoi主界面程序
 */
public class RecurAndDivConTowerOfHanoiActivity extends Activity implements View.OnClickListener {
    private TowerOfHanoiView hanoiView;
    private Button bt_nextstep,bt_stepover,bt_stop,bt_start,bt_reset_plate;
    private EditText et_input;
    private ListView lv_code_list,lv_list_code_proc;
    private TextView tv_current_n,tv_source_tower,tv_dest_tower,tv_mid_tower,tv_intro;
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
            "//汉诺塔问题递归算法",
            "void hanoi(int n, int a, int b, int c)",
            "{",
            "  if (n == 1)",
            "  {",
            "    move(a, b);",
            "  }",
            "  else",
            "  {",
            "    hanoi(n - 1, a, c, b);",
            "    move(a, b);",
            "    hanoi(n - 1, c, b, a);",
            "  }",
            "}"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recur_div_con_tower_of_hanoi);
        initView();
        initAction();
        fixSlideConflict();
    }

    public void initView() {
        hanoiView = (TowerOfHanoiView)findViewById(R.id.TowerOfHanoiView);
        mScrollView = (BottomScrollView) findViewById(R.id.main_scroll_view);
        bt_nextstep = (Button)findViewById(R.id.bt_nextstep);
        bt_stepover = (Button)findViewById(R.id.bt_stepover);
        bt_stop = (Button)findViewById(R.id.bt_stop);
        bt_start = (Button)findViewById(R.id.bt_start);
        bt_reset_plate = (Button)findViewById(R.id.bt_reset_plate);
        et_input = (EditText)findViewById(R.id.et_input);
        lv_code_list = (ListView)findViewById(R.id.lv_code_list);
        lv_list_code_proc = (ListView)findViewById(R.id.lv_code_list_prod);
        tv_current_n = (TextView)findViewById(R.id.tv_current_n);
        tv_dest_tower = (TextView)findViewById(R.id.tv_dest_tower);
        tv_mid_tower = (TextView)findViewById(R.id.tv_mid_tower);
        tv_source_tower = (TextView)findViewById(R.id.tv_source_tower);
        tv_intro = (TextView)findViewById(R.id.tv_intro);

        /*为ListView设置Adapter来绑定数据*/
        lv_code_list.setAdapter(new ArrayAdapter<String>(this, R.layout.code_list_item,R.id.list_item_tv, strs));
        lv_code_list.setDividerHeight(0);

        /*程序步骤*/
        mListViewAdapter = new ListViewAdapter(this, procItem);
        lv_list_code_proc.setAdapter(mListViewAdapter);

        builder =  new AlertDialog.Builder(this);

        //中断线程初始化
        ThreadNotify threadNotify = new ThreadNotify(lock);
        thread2 = new Thread(threadNotify);

        bt_nextstep.setOnClickListener(this);
        bt_stepover.setOnClickListener(this);
        bt_stop.setOnClickListener(this);
        bt_start.setOnClickListener(this);
        bt_reset_plate.setOnClickListener(this);

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
                        TextBox textBox = (TextBox)msg.obj;
                        tv_current_n.setText(String.valueOf(textBox.getCurrentN()));
                        tv_dest_tower.setText(textBox.getDestTower());
                        tv_mid_tower.setText(textBox.getMidTower());
                        tv_source_tower.setText(textBox.getSourceTower());
                        break;
                    case 3:
                        System.out.println("clear现在执行");
                        et_input.setText("");
                        tv_source_tower.setText("");
                        tv_mid_tower.setText("");
                        tv_dest_tower.setText("");
                        tv_current_n.setText("");
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
        InputStream inputStream = getResources().openRawResource(R.raw.rec_divcon_towerofhanoi_redu);
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
        hanoiView.setHanoiActionListener(new TowerOfHanoiView.HanoiActionListener(){

            @Override
            public void HightLightShowSingleLineCode(int index) {
                lv_code_list.performItemClick(lv_code_list.getChildAt(index), index, lv_code_list.getItemIdAtPosition(index));
            }

            public void addListCodeProc(String newProc){
                msg = new Message();
                msg.what = 1;
                msg.obj = newProc;
                handler.sendMessage(msg);
            }

            public void updateTextBox(TextBox textBox) {
                msg = new Message();
                msg.what = 2;
                msg.obj = textBox;
                handler.sendMessage(msg);
            }

            public void clearAll() {
               msg = new Message();
               msg.what = 3;
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

//    private List<String> getListViewData() {
//        List<String> data = new ArrayList<>();
//        for(int i = 1; i <= 20; i ++) {
//            data.add(i + " item");
//        }
//
//        return data;
//    }

    @Override
    public void onClick(View v) {
        int plateNum = 0;
        String str = et_input.getText().toString();
        Pattern pattern = Pattern.compile("^[0-9]*$");
        //如果是0-9组成的数字字符
        if (pattern.matcher(str).matches() && !str.equals("")){
            plateNum = Integer.parseInt(str);
        }
        switch (v.getId()) {
            case R.id.bt_nextstep:
                if (!hanoiView.isPlateSlide){
                    thread2.run();
                }
                break;
            case R.id.bt_stepover:
                if (hanoiView.isRunning && hanoiView.isChange) {
                    hanoiView.isStepOver = true;
                }
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread (new Runnable() {
                            public void run() {
                                if (hanoiView.isStepOver && hanoiView.signal == 0) {
                                    bt_nextstep.performClick();
//                                    System.out.println("输出");
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
                timer.schedule(timerTask,0,50);

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
                System.out.println("我在子线程后输出");
                hanoiView.reset();
                break;
            case R.id.bt_start:
                if (plateNum >= 1 && plateNum <= 8) {
                    if (!hanoiView.isRunning) {
                        hanoiView.plateSum = plateNum;
                        hanoiView.isRunning = true;
                        hanoiView.isChange = true;
                        //重新初始化
                        hanoiView.InitAllPillar();
                        hanoiView.InitAllPlate();
                        hanoiView.refresh();//点击开始后刷新
                        thread1 = new Thread(hanoiView.new ThreadRun(lock));
                        thread1.start();
                    } else {
                        builder.setTitle("提示")
                                .setMessage("亲，重新开始请停止运行！")
                                .setPositiveButton("确定" ,  null )
                                .show();
                    }
                } else if (plateNum >8 ){
                    builder.setTitle("提示")
                            .setMessage("亲，个数太多了！")
                            .setPositiveButton("确定" ,  null )
                            .show();
                } else {
                    builder.setTitle("提示")
                            .setMessage("亲，请正确填写盘子个数！")
                            .setPositiveButton("确定" ,  null )
                            .show();
                }

                break;
            case R.id.bt_reset_plate:
                if (thread1 != null)
                    thread1.interrupt();
                //确保reset方法在thread1结束后执行，睡眠0.2s，不然主线程抢占线程
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("test");
                hanoiView.reset();
                break;
        }
    }
}
