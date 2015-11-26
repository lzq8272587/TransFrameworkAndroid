package com.android.volley.toolbox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by jym on 2015/11/22.
 */
public class MyThread extends Thread {

    public void run() {
        synchronized (this) {
            // 唤醒当前的wait线程
            notify();
        }
    }

}
