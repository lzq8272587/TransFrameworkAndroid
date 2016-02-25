package com.android.volley;

import android.os.Handler;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;

import java.util.HashSet;
import java.util.concurrent.BlockingQueue;

/**
 * Created by LZQ on 11/24/2015.
 */
public class BufferDispatcher extends Thread {

    /**
     * TAG
     */
    final String TAG = "BufferDispatcher";

    /**
     * The queue of network requests to service.
     */
    private final BlockingQueue<Request<?>> mNetworkQueue;

    /**
     * The queue of the buffered requests
     */
    private final BlockingQueue<Request<?>> mBufferQueue;
    /**
     * Tail时间
     * 默认设置成3秒
     */
    private final long TAIL_TIME = 3;
    /**
     * 创建ContextAdaptor的实例
     */
    ContextAdaptor ctxadaptor = null;
    /**
     * Used for telling us to die.
     */
    private volatile boolean mQuit = false;
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
     * 在这个Set中，装着所有还没有被丢进Network队列中的Request
     * 设置这个Set的原因是，如果新来了一个Deadline小于nextTime的Request
     * 那么需要把队列中的所有的Request提前到这个Deadline发送
     */
    //private HashSet<Request> mCurrentRequests = null;
    /**
     * 处理每一个Request的Handler
     * 在这个Handler里面会定时把Request交给网络调度队列处理
     */
    private Handler handler = null;
    /**
     * 缓存所有还没有处理的任务
     * 在这个Set中，装着所有还没有被丢进Network队列中的Runnable
     * 设置这个Set的原因是，如果新来了一个Deadline小于nextTime的Request
     * 那么需要把队列中的所有的Request提前到这个Deadline发送
     */
    private HashSet<DelayedRequest> mCurrentRunnable = null;
//    ContextAdaptor cr= new ContextAdaptor();


    /**
     * 构造函数，传入一个buffer队列，一个network队列
     * 这个类的功能就是根据Request的Delay，生成定时任务
     * 在定时任务的时间到了的时候，把任务丢给NetworkDispatcher执行
     *
     * @param mNetworkQueue
     * @param mBufferQueue
     */
    public BufferDispatcher(BlockingQueue<Request<?>> mNetworkQueue, BlockingQueue<Request<?>> mBufferQueue, ContextAdaptor ctxadaptor) {
        this.mNetworkQueue = mNetworkQueue;
        this.mBufferQueue = mBufferQueue;
        handler = new Handler();
        mCurrentRunnable = new HashSet<DelayedRequest>();
        this.ctxadaptor = ctxadaptor;
    }

    /**
     * 在这个线程中，会把BufferQueue这个队列中的数据取出来，然后根据它们的delay，设置一系列的定时任务
     * 到达给定的时间之和，就会把Request交付到mNetworkQueue里面
     * 在网络队列中，最终会由NetworkDispatcher把剩下的网络任务执行
     */
    public void run() {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while (true) {
            /**
             * 首先从缓存队列里面取出一个Request
             */
            Request<?> request;
            try {
                // Take a request from the queue.
                request = mBufferQueue.take();
                Log.d(TAG, "Got new Request.");
            } catch (InterruptedException e) {
                // We may have been interrupted because it was time to quit.
                if (mQuit) {
                    return;
                }
                continue;
            }

            /**
             * 不管是哪种类型的Request，统一丢给ContextAdaptor处理。
             */
            ctxadaptor.addRequest(request);
            continue;

            /**
             * 后面全部注释掉
             */

//            //Log.d(TAG, "Current Request: " + request.getClass().getName());
//            //Log.d(TAG, "nextTime= " + nextTime);
//            //Log.d(TAG, "endTime= " + request.getEndTime());
//
//            /**
//             * 如果take得到一个Active的Request，那么直接丢到NetworkQueue里面去处理。
//             */
//            if (request.getProperty() == Request.ACTIVE) {
//                Log.e(TAG, "Take a Active Request.");
//                try {
//                    mNetworkQueue.put(request);
//                    lastSend = SystemClock.elapsedRealtime();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                continue;
//            }
//
//            /**
//             * 走到这一步的，都是Dozy或者Frozen的Request了。
//             */
//
//
//            /**
//             * 如果得到的是一个Dozy的Request
//             */
//            if(request.getProperty()==Request.DOZY) {
//                Log.e(TAG, "Take a Dozy Request, put it to ContextAdaptor.");
//                ctxadaptor.addRequest(request);
//                lastSend = SystemClock.elapsedRealtime();
//                return;
//            }
//
//
//            Log.e(TAG, "Take a Dozy or Frozen Request.");
//            /**
//             * 如果目前还处于一个Tail内，那么立即发送Request
//             * 直接丢进Network队列中
//             */
//            if ((SystemClock.elapsedRealtime() - lastSend) < TAIL_TIME) {
//                Log.d(TAG, "in last tail, send immediately.");
//                try {
//                    mNetworkQueue.put(request);
//                    lastSend = SystemClock.elapsedRealtime();
//                    continue;
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                    continue;
//                }
//            }
//
//            /**
//             * 如果不处于一个Tail时间内了，那么要根据当前Request和队列中还在等待的所有Requests的Deadline更新下一次发送时间
//             */
//
//            /**
//             * 如果目前都没有缓存的Request了，那么这个时候肯定就是把这个Request的Dealine设置成下一个发送的时间
//             * 把这个request丢进HashSet
//             * 还要把这个request丢给handler
//             */
//            if (mCurrentRunnable.size() == 0) {
//                Log.d(TAG, "mCurrentRunnable is empty, set nextTime to the endTime of this new request.");
//                nextTime = request.getEndTime();
//                DelayedRequest dr = new DelayedRequest(request);
//                mCurrentRunnable.add(dr);
//                handler.postDelayed(dr, (nextTime - SystemClock.elapsedRealtime()));
//                continue;
//            }
//
//            /**
//             * 如果当前的Request的Deadline比缓存队列里其它Request还要晚发送，把它们捆绑在一起传输
//             */
//            if (request.getEndTime() > nextTime) {
//                Log.d(TAG, "promote this request to transfer with other requests.");
//                DelayedRequest dr = new DelayedRequest(request);
//                mCurrentRunnable.add(dr);
//                handler.postDelayed(dr, (nextTime - SystemClock.elapsedRealtime()));
//            }
//            /**
//             * 如果当前Request的Deadline比较早，那么所有之前延迟发送的Request需要被提早发送
//             */
//            else {
//                Log.d(TAG, "promote other requests with this request.");
//                nextTime = request.getEndTime();
//                /**
//                 * 对于每一个缓存队列中的Runnable任务
//                 */
//                for (DelayedRequest dr : mCurrentRunnable) {
//                    /**
//                     * 从Handler里面取出它们，然后重新post到Handler里面，更新时间
//                     */
//                    handler.removeCallbacks(dr);
//                    handler.postDelayed(dr, (nextTime - SystemClock.elapsedRealtime()));
//                }
//                /**
//                 * 别忘了把Request也丢进去
//                 */
//                DelayedRequest dr = new DelayedRequest(request);
//                mCurrentRunnable.add(dr);
//                handler.postDelayed(dr, (nextTime - SystemClock.elapsedRealtime()));
//            }
        }
    }

    /**
     * Forces this dispatcher to quit immediately.  If any requests are still in
     * the queue, they are not guaranteed to be processed.
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }


    class DelayedRequest implements Runnable {

        Request r = null;

        public DelayedRequest(Request request) {
            r = request;
        }

        public Request getRequest() {
            return r;
        }

        /**
         * Starts executing the active part of the class' code. This method is
         * called when a thread is started that has been created with a class which
         * implements {@code Runnable}.
         */
        @Override
        public void run() {
            /**
             * 这里把Request交给网络调度器处理
             */
            try {
                Log.d(TAG, "put this request to NetworkQueue, and remove it in the HashSet.");
                mNetworkQueue.put(r);
                mCurrentRunnable.remove(this);
                lastSend = SystemClock.elapsedRealtime();
                Log.d(TAG, "delay= " + (SystemClock.elapsedRealtime() - r.getArrTime()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
