package com.example.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

import com.example.demo.AlgBackTrack.Intro.IntroBackTrack;
import com.example.demo.AlgBranchAndBound.Intro.IntroBranchBound;
import com.example.demo.AlgDynamic.Intro.IntroDynamic;
import com.example.demo.AlgGreedy.Intro.IntroGreedy;
import com.example.demo.AlgRecurAndDivCon.Intro.IntroRecurAndDivCon;
import com.example.demo.ShowNavigation.ShowBackTrack;
import com.example.demo.ShowNavigation.ShowBranchAndBound;
import com.example.demo.ShowNavigation.ShowDynamic;
import com.example.demo.ShowNavigation.ShowGreedy;
import com.example.demo.ShowNavigation.ShowRecurAndDivCon;

public class MainActivity extends Activity {
    private Button bt_recurAndDivCon,bt_dynamic,bt_greedy,bt_backTrack,bt_branchAndBound,bt_about_system;
    private Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_recurAndDivCon = (Button) findViewById(R.id.bt_recur_divcon);
        bt_dynamic = (Button) findViewById(R.id.bt_dynamic);
        bt_greedy = (Button) findViewById(R.id.bt_greedy);
        bt_backTrack = (Button) findViewById(R.id.bt_backtrack);
        bt_branchAndBound = (Button) findViewById(R.id.bt_branch_bound);
        bt_about_system = (Button) findViewById(R.id.bt_system_help);

        bt_recurAndDivCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // View当前PopupMenu显示的相对View的位置
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, bt_recurAndDivCon);
                // menu布局
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                // menu的item点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.intro:
                                intent.setClass(MainActivity.this,  IntroRecurAndDivCon.class);
                                startActivity(intent);
                                break;
                            case R.id.show:
                                intent.setClass(MainActivity.this,  ShowRecurAndDivCon.class);
                                startActivity(intent);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();

            }
        });

        bt_dynamic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, bt_dynamic);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.intro:
                                intent.setClass(MainActivity.this,  IntroDynamic.class);
                                startActivity(intent);
                                break;
                            case R.id.show:
                                intent.setClass(MainActivity.this,  ShowDynamic.class);
                                startActivity(intent);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        bt_greedy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, bt_dynamic);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.intro:
                                intent.setClass(MainActivity.this,  IntroGreedy.class);
                                startActivity(intent);
                                break;
                            case R.id.show:
                                intent.setClass(MainActivity.this,  ShowGreedy.class);
                                startActivity(intent);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        bt_backTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, bt_dynamic);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.intro:
                                intent.setClass(MainActivity.this,  IntroBackTrack.class);
                                startActivity(intent);
                                break;
                            case R.id.show:
                                intent.setClass(MainActivity.this,  ShowBackTrack.class);
                                startActivity(intent);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        bt_branchAndBound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, bt_dynamic);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.intro:
                                intent.setClass(MainActivity.this,  IntroBranchBound.class);
                                startActivity(intent);
                                break;
                            case R.id.show:
                                intent.setClass(MainActivity.this,  ShowBranchAndBound.class);
                                startActivity(intent);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        bt_about_system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(MainActivity.this,  AboutSystemActivity.class);
                startActivity(intent);
            }
        });
    }
}
