package framework.mobisys.netlab.framework;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.toolbox.HurlStack;


/**
 * Created by jym on 2015/11/20.
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {

    private static final String TAG = ConnectionChangeReceiver.class.getSimpleName();

//    public static void 4个静态线程
    //network dispatcher
    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.e(TAG, "网络状态改变");

        //获得网络连接服务
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // State state = connManager.getActiveNetworkInfo().getState();
        // 获取WIFI网络连接状态
        NetworkInfo.State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        // 判断是否正在使用WIFI网络
        if (NetworkInfo.State.CONNECTED == state) {
            HurlStack.isNetworkConnected = true;
        }
        // 获取GPRS网络连接状态
        state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        // 判断是否正在使用GPRS网络
        if (NetworkInfo.State.CONNECTED == state) {
            HurlStack.isNetworkConnected = true;
        }

        if (!HurlStack.isNetworkConnected) {
            Toast.makeText(context, "Network Disconnected!", Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(context, "Network Connected.", Toast.LENGTH_LONG).show();
//            synchronized (HurlStack.HurlStackThread){
//                try {
//                    HurlStack.HurlStackThread.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }
}



