package framework.mobisys.netlab.framework;

import android.os.SystemClock;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;

/**
 * Created by LZQ on 11/14/2015.
 */
public class LStringRequest extends Request<String> {

    final String TAG = "LStringRequest";

    String url = null;


    Listener<String> sListener = null;


    Response.ProgressListener pListener = null;


    /**
     * 重载之后的构造函数，使用者只需申明需要获取的URL地址，以及获取成功之后的事情即可
     *
     * @param url
     * @param ml
     */
    public LStringRequest(String url, Listener<String> ml) {
        super(Request.Method.GET, url, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LStringRequest", error.getMessage());
            }
        });
        sListener = ml;
        this.url = url;
    }

    /**
     * 另外，构造函数还可以申明是否需要使用进度条
     *
     * @param url
     * @param ml
     * @param pl
     */
    public LStringRequest(String url, Listener<String> ml, Response.ProgressListener pl) {
        this(url, ml);
        pListener = pl;
    }

    /**
     * 重新按照新的API接口定义构造函数
     */
    public LStringRequest(String url, int delay, String tag) {
        super(Request.Method.GET, url, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LStringRequest", error.getMessage());
            }
        });
        this.delay = delay;
        this.url = url;
        this.tag = tag;
        setArrTime(SystemClock.elapsedRealtime());
        setEndTime(getArrTime() + delay * 1000);
    }

    /**
     * Subclasses must implement this to parse the raw network response
     * and return an appropriate response type. This method will be
     * called from a worker thread.  The response will not be delivered
     * if you return null.
     *
     * @param response Response from the network
     * @return The parsed response, or null in the case of an error
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    /**
     * Subclasses must implement this to perform delivery of the parsed
     * response to their listeners.  The given response is guaranteed to
     * be non-null; responses that fail to parse are not delivered.
     *
     * @param response The parsed response returned by
     *                 {@link #parseNetworkResponse(NetworkResponse)}
     */
    @Override
    protected void deliverResponse(String response) {
        sListener.onResponse(response);
    }

    public Listener<String> getListener() {
        return sListener;
    }

    public void setListener(Listener<String> sListener) {
        this.sListener = sListener;
    }

    public Response.ProgressListener getProgressListener() {
        return pListener;
    }

    public void setProgressListener(Response.ProgressListener pListener) {
        this.pListener = pListener;
    }
}
