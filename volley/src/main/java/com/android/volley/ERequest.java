package com.android.volley;

import android.os.SystemClock;
import android.util.Log;

import com.android.volley.toolbox.HttpHeaderParser;

/**
 * Created by LZQ on 2/23/2016.
 */
public class ERequest extends Request<byte[]> {

    /**
     * 重新定义Request，现在将此前的StringRequest和ObjectRequest统称为ERequest。
     * ERequest类似于此前的ObjectRequest，任何数据类型都可以转化成Object类型。
     */


    /**
     * Tag for debug.
     */
    final String TAG = "ERequest";

    /**
     * 每一个Request会对应一个URL。
     */
    public String url = null;
    /**
     * 监听Response的Listener。
     */
    Response.Listener<byte[]> rListener = null;

    /**
     * 监听处理进展的Listener。
     */
    Response.ProgressListener pListener = null;

    /**
     * 监听超时的Listener
     */
    Response.TimeoutListener tListener = null;


    /**
     * 带Delay的构造函数，可以指定一个Request最大能够忍受的Timeout时延。
     *
     * @param url
     * @param delay
     * @param tag
     */
    public ERequest(String url, int property, int delay, String tag) {
        super(Request.Method.GET, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LStringRequest", error.getMessage());
            }
        });
        this.delay = delay;
        this.url = url;
        this.tag = tag;
        this.property = property;
        setArrTime(SystemClock.elapsedRealtime());
        setEndTime(getArrTime() + delay * 1000);
    }


    /**
     * 不带Delay的构造函数，默认设置超时时间，10s。
     *
     * @param url
     * @param property
     * @param tag
     */
    public ERequest(String url, int property, String tag) {
        this(url, property, 5, tag);
    }


    /**
     * 设置Request处理进度的监听器。
     *
     * @param pListener
     */
    public void setProgressListener(Response.ProgressListener pListener) {
        this.pListener = pListener;
    }


    /**
     * 设置监听Response的监听器。
     *
     * @param sListener
     */
    public void setListener(Response.Listener<byte[]> sListener) {
        this.rListener = sListener;
    }

    /**
     * 设置监听Timeout的监听器。
     *
     * @param tListener
     */
    public void setTimeoutListener(Response.TimeoutListener tListener) {
        this.tListener = tListener;
    }


    /**
     * 处理Response的地方，最终会得到一个包含byte数组的Response。
     *
     * @param response Response from the network
     * @return
     */
    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        /**
         * 一般来说，在这个方法里面，会从response里获取data，然后做相应的处理，最后再创建一个T类型的Response，这个Response里面的内容最终会被回调给Developer.
         */
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }


    /**
     * 最后Response接收到后，就会调用预先设定好的Listener里面的onResponse()方法。
     *
     * @param response
     */
    @Override
    protected void deliverResponse(byte[] response) {
        rListener.onResponse(response);
    }


}
