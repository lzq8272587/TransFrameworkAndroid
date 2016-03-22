package com.android.volley.toolbox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class MyThread extends Thread {

    public void run() {
        synchronized (this) {
            notify();
        }
    }

}
