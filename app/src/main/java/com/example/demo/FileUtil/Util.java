package com.example.demo.FileUtil;

import android.content.Context;

/**
 * @Author captain
 * @Description 表格view用到的工具类
 */
public class Util {

    public static int dip2px(Context context, float dipValue) {
        return (int) (dipValue * context.getResources().getDisplayMetrics().density);
    }

}