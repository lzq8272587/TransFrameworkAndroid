package framework.mobisys.netlab.framework;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * Created by LZQ on 11/15/2015.
 * 对通用的Object类型的数据传输进行封装的Requets类
 */
public class LObjectRequest extends Request<byte[]> {

    final String TAG = "LObjectRequest";

    String url = null;


    Response.Listener<byte[]> sListener = null;

    Response.ProgressListener pListener = null;


    /**
     * Creates a new request with the given method (one of the values from {@link Method}),
     * URL, and error listener.  Note that the normal response listener is not provided here as
     * delivery of responses is provided by subclasses, who have a better idea of how to deliver
     * an already-parsed response.
     *
     * @param url
     * @param listener
     */
    public LObjectRequest(String url, Response.Listener<byte[]> listener) {
        super(Request.Method.GET, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LObjectRequest", error.getLocalizedMessage());
            }
        });
        sListener = listener;
    }


    public LObjectRequest(String url, int delay, String tag) {
        super(Request.Method.GET, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LStringRequest", error.getMessage());
            }
        });
        this.delay = delay;
        this.url = url;
        this.tag = tag;
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
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {

        //一般来说，在这个方法里面，会从response里获取data，然后做相应的处理，最后再创建一个T类型的Response，这个Response里面的内容最终会被回调给Developer
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
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
    protected void deliverResponse(byte[] response) {
        sListener.onResponse(response);
    }


    public Response.ProgressListener getProgressListener() {
        return pListener;
    }

    public void setProgressListener(Response.ProgressListener pListener) {
        this.pListener = pListener;
    }

    public Response.Listener<byte[]> getListener() {
        return sListener;
    }

    public void setListener(Response.Listener<byte[]> sListener) {
        this.sListener = sListener;
    }

}
