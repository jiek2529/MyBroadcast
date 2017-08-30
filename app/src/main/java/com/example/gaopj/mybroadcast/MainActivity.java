package com.example.gaopj.mybroadcast;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MyBroadcastMainActivity";
    private static final String ACTION_MYPERMISSION = "action_mypermission";
    private static final String COM_JIEK_MYPERMISSION = "com.jiek.mypermission";

    MyReceiver receiver = new MyReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*if (Build.VERSION.SDK_INT >= 23) {//权限请求是异步的，不阻塞UI线程。 20170829;   在android 8.0 26 版本系统上如果不进行申请权限，广播是无法发送出去。详情见README.md
//            checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE");
            requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"
//                    , "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"
                    , COM_JIEK_MYPERMISSION}, 1000);
        }*/
        log("测试是否是阻塞式: 结论是非阻塞式。");
        File file = new File("/mnt/sdcard/");
        if (file.exists() && file.isDirectory()) {
            log("file.length: " + file.length());
        }

        File f = new File(file, "0.txt");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                FileWriter fw = new FileWriter(f, true);// 文件读写权限在360n5（6.0）手机上不用申请就有。抛弃了官方方案。
                BufferedWriter bf = new BufferedWriter(fw);
                bf.append("\ntime:" + System.currentTimeMillis());
                bf.flush();
                bf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        log("permissions: " + Arrays.toString(permissions));
        log("grantResults: " + Arrays.toString(grantResults));
    }

    private void log(String msg) {
        Log.e(TAG, msg);
    }

    public void click_gbr(View view) {
        Intent intent = new Intent(ACTION_MYPERMISSION);
        intent.putExtra("hello", this.getClass().getName());
        intent.putExtra("time", System.currentTimeMillis());
//        sendBroadcast(intent);
        sendBroadcast(intent, COM_JIEK_MYPERMISSION);//带权限广播
        log("sendBroadcast");
    }

    public void click_gbr_ordered(View view) {
        Intent intent = new Intent(ACTION_MYPERMISSION);
        intent.putExtra("hello", this.getClass().getName());
        intent.putExtra("time", System.currentTimeMillis());
        sendOrderedBroadcast(intent, COM_JIEK_MYPERMISSION);
        log("sendOrderedBroadcast");
    }

    public void click_registerReceiver(View view) {
        IntentFilter br_hello = new IntentFilter(ACTION_MYPERMISSION);
//        br_hello.setPriority(999);
        registerReceiver(receiver, br_hello);
    }

    public void click_registerReceiver_priority(View view) {
        IntentFilter br_hello = new IntentFilter(ACTION_MYPERMISSION);
        br_hello.setPriority(999);
        registerReceiver(receiver, br_hello);
    }


    @Override
    protected void onDestroy() {
        for (BroadcastReceiver br : set) {
            unregisterReceiver(br);
        }
        set.clear();
        set = null;
        super.onDestroy();
    }

    Set<BroadcastReceiver> set = new HashSet<>();

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        set.add(receiver);
        return super.registerReceiver(receiver, filter);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        set.remove(receiver);
        super.unregisterReceiver(receiver);
    }
}
