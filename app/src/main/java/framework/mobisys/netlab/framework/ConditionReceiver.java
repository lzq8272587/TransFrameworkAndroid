package framework.mobisys.netlab.framework;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.ERequest;
import com.android.volley.Request;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ConditionReceiver extends BroadcastReceiver {

    private final int NETWORK_TYPE_INVALID = 0;
    private final int NETWORK_TYPE_WIFI = 1;
    private final int NETWORK_TYPE_WAP = 2;
    private final int NETWORK_TYPE_2G = 3;
    private final int NETWORK_TYPE_3G = 4;
    /**
     * Request Size的阈值，超过这个大小的Request要被区分对待。
     */
    private final int REQUEST_THRESH = 1024 * 1024 * 10;
    /**
     * 电池电量的阈值，低于这个电量的时候，会推迟非Active的Request。
     */
    private final int BATTERY_THRESH = 30;
    /**
     * The queue of network requests to service.
     */
    private final BlockingQueue<Request<?>> mNetworkQueue;
    /**
     * Tail时间
     * 默认设置成3秒
     */
    private final long TAIL_TIME = 3;
    String TAG = "ConditionReceiver";
    String TopApp = null;
    String CurrentConnectivity = null;
    int Battery = 0;
    Context ctx = null;
    /**
     * 缓存被延迟处理的Request，这些Request是指一定要等到特定的环境下才会被处理的Request，例如WIFI下。
     */
    HashSet<ERequest> DelayedRequest = new HashSet();
    /**
     * 被挂起的Request，这些是需要被捆绑处理的Request。
     */
    HashSet<ERequest> PendingRequest = new HashSet();
    /**
     * 唤醒机制，在指定时间唤醒设备
     */
    AlarmManager am = null;
    PendingIntent pendingIntent = null;
    /**
     * 上次发送时间，初始化为一个最小值
     */
    private long lastSend = -1;
    /**
     * 下一次要发送的时间
     * 这个时间可以理解为还在等待处理的BufferQueue中所有Requests
     * 的发送时间
     */
    private long nextTime = Long.MAX_VALUE;


    /**
     * 构造函数，在构造函数中获得Context
     *
     * @param ctx
     */
    public ConditionReceiver(Context ctx, BlockingQueue<Request<?>> mNetworkQueue) {
        this.ctx = ctx;
        this.mNetworkQueue = mNetworkQueue;
        am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctx, ConditionReceiver.class);
        intent.setAction("wakeup_pending_request");
        pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    /**
     * 判断是否非前台进程
     *
     * @param AppInfo
     * @return
     */
    public boolean isForeground(String AppInfo) {
        String ForegroundProcess = getForegroundProcessName();
        if (ForegroundProcess == null) {
            return false;
        }
        return AppInfo.equals(ForegroundProcess);
    }

    /**
     * 获取前台进程的名字
     *
     * @return
     */
    private String getForegroundProcessName() {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = manager.getRunningAppProcesses();
        //.getRunningTasks(Integer.MAX_VALUE);//(Integer.MAX_VALUE);//.getRunningTasks(Integer.MAX_VALUE);
        String ForegroundPorcess = runningAppProcessInfos.get(0).processName;
        return ForegroundPorcess;
    }

    private boolean isFastMobileNetwork() {
        TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

        Log.d(TAG, "Network Type: " + telephonyManager.getNetworkType());
        /**
         * 通过检测到的网络类型判断当前是哪一种制式的网络。
         */
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }

    /**
     * 判断网络类型
     */
    public int getNetWorkType() {
        ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        int mNetWorkType = 0;
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            Log.e(TAG, type);
            if (type.equalsIgnoreCase("WIFI")) {
                mNetWorkType = NETWORK_TYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                String proxyHost = android.net.Proxy.getDefaultHost();

                mNetWorkType = TextUtils.isEmpty(proxyHost)
                        ? (isFastMobileNetwork() ? NETWORK_TYPE_3G : NETWORK_TYPE_2G)
                        : NETWORK_TYPE_WAP;
            }
        } else {
            mNetWorkType = NETWORK_TYPE_INVALID;
        }
        return mNetWorkType;
    }

    /**
     * 获取当前剩余电量。这个可以通过Android的Framework实现。
     * 也可以直接读取/sys/class/power_supply 这个目录下的文件获取。
     *
     * @return
     */
    public int getBatteryCapacity() {
        return 100;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d(TAG, intent.getAction());
        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            /**
             * 监听到网络连接发生变化
             */


        } else if (intent.getAction().equals("android.intent.action.BATTERY_LOW")) {
            /**
             * 监听到低电量事件
             */
        } else if (intent.getAction().equals("wakeup_pending_request")) {
            /**
             * 监听到定时任务启动了
             */
            Log.e(TAG, "Wakeup Pending Requests");
        }

    }

    /**
     * 如果是Dozy或者Frozen Request，丢给Adaptation处理
     *
     * @param eRequest
     */
    public void addRequest(ERequest eRequest) {
        /**
         * 处理Dozy类型的Request，只有在某些特定环境下才会延迟处理。
         */
        if (eRequest.getProperty() == ERequest.DOZY) {
            if (getBatteryCapacity() < BATTERY_THRESH) {
                /**
                 * Case 1: 如果剩余电量小于可用阈值，那么一定延迟处理。
                 */
                DelayedRequest.add(eRequest);
                return;
            }

            /**
             * Case 2: 如果是前台程序，那么一定立即处理，或者，如果当前处于WIFI环境下，那么一定立即处理
             */
            if (eRequest.getAppInfo().equals(getForegroundProcessName()) || (getNetWorkType() == NETWORK_TYPE_WIFI)) {
                try {
                    mNetworkQueue.put(eRequest);
                    lastSend = SystemClock.elapsedRealtime();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return;
            }

            /**
             * Case 3: 电量允许发送，但是在Cellular环境下，并且是后台程序。这个时候要根据Size进行判断。
             * 但是当前无法获得Size，就统一看做需要延迟处理了。因为大Request的处理很简单，延迟到有WIFI传输的时候就可以了。
             * 所以现在我们重点考虑，小的零碎数据的捆绑传输。
             */

            /**
             * Case 3.1: 如果目前还处于一个Tail内，那么立即发送Request
             * 直接丢进Network队列中
             */
            if ((SystemClock.elapsedRealtime() - lastSend) < TAIL_TIME) {
                Log.d(TAG, "in last tail, send immediately.");
                try {
                    mNetworkQueue.put(eRequest);
                    lastSend = SystemClock.elapsedRealtime();
                    return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }

            /**
             * 如果不处于一个Tail时间内了，那么要根据当前Request和队列中还在等待的所有Requests的Deadline更新下一次发送时间
             */

            /**
             * 如果目前都没有缓存的Request了，那么这个时候肯定就是把这个Request的Dealine设置成下一个发送的时间
             * 设置定时任务，到时候再触发。
             */
            if (PendingRequest.size() == 0) {
                Log.d(TAG, "mCurrentRunnable is empty, set nextTime to the endTime of this new request.");
                nextTime = eRequest.getEndTime();
                PendingRequest.add(eRequest);
                /**
                 * 设置定时任务，延迟到快要Timeout的时候再执行发送任务
                 */
                setPendingRequest(nextTime);
                return;
            }


        }
        /**
         * 处理Frozen类型的Request，在绝大部分环境下都被延迟处理。
         * 除非处于WIFI环境，或者电量高于某一个特定阈值。
         */
        else if (eRequest.getProperty() == ERequest.FROZEN) {
            if (getBatteryCapacity() > 30 && (getNetWorkType() == NETWORK_TYPE_WIFI)) {
                /**
                 * 满足条件，把Request丢给NetworkQueue处理
                 */
                try {
                    mNetworkQueue.put(eRequest);
                    lastSend = SystemClock.elapsedRealtime();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                /**
                 * 不满足条件，丢进等待集合中
                 */
                DelayedRequest.add(eRequest);
            }
        }

    }

    /**
     * 设置定时任务，到给定时间再处理对应的eRequest
     * 这个方法本质上就是要在nextTime，下一个发送时间唤醒，统一处理PendingRequest集合中的Request。
     */
    private void setPendingRequest(long delay) {
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, delay, pendingIntent);
    }


}
