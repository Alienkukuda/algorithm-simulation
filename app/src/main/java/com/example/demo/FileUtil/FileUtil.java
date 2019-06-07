package com.example.demo.FileUtil;

import android.service.autofill.FillEventHistory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * @Author captain
 * @Description 文件读取类
 */
public class FileUtil {
    private static FileUtil instance = new FileUtil();

    private FileUtil(){}

    public static FileUtil getInstance() {
        return instance;
    }
    public String getString(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer stringBuffer = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) !=null){
                stringBuffer.append(line);
                stringBuffer.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }
}
