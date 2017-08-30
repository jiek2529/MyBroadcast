package com.example.gaopj.mybroadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Hello", this.getClass().getName());
        Log.e("Hello", "hello="+intent.getStringExtra("hello"));
        Log.e("Hello", "time="+intent.getLongExtra("time", 0));
    }
}
