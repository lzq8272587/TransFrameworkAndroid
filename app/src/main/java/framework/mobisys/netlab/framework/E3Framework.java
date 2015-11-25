package framework.mobisys.netlab.framework;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by LZQ on 11/11/2015.
 */
public class E3Framework implements Parcelable {

    private static E3Framework instance = null;
    private static E3Service service = null;
    private final String TAG = "E3Framework";
    private Context ctx;
    private RequestQueue queue = null;

    private E3Framework(Context c) {
        ctx = c;
        /**
         * 调用这个函数的时候，queue已经创建并且启动了
         */
        queue = Volley.newRequestQueue(ctx);

        /**
         * 启动后台Service
         */
        Log.d(TAG, "start e3 service");
        c.startService(new Intent(c, E3Service.class));
    }

    public static E3Framework getInstance(Context c) {
        if (instance != null) {
            return instance;
        } else {
            instance = new E3Framework(c);
            return instance;
        }
    }

    public void addUploadRequest(UploadRequest ur) {
        /**
         **这个版本中，利用volley帮助我们上传所需要的内容
         */
    }

    public void addDownloadRequest(final StringDownloadRequest dr) {
        /**
         * 根据具体Request的内容，下载相应的内容
         */
        String url = dr.uri;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dr.uplistener.onFinished(response.toString());
                        System.out.println("Load successfully!");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        System.out.println(error.getLocalizedMessage());
                    }
                });
        // Access the RequestQueue through your singleton class.
        RequestQueue queue = Volley.newRequestQueue(ctx);
        System.out.println("Add request to queue");
        queue.add(jsObjRequest);
    }


    /**
     * 创建一个StringRequest，同时设置这个Request的url，最大可容忍delay，并且设置一个标签
     *
     * @param url
     * @param delay
     * @param tag
     * @return
     */
    public LStringRequest createStringRequest(String url, int delay, String tag) {
        LStringRequest lsr = new LStringRequest(url, delay, tag);
        return lsr;
    }

    /**
     * 利用指定的url，最大可容忍delay，以及tag创建ObjectRequest
     *
     * @param url
     * @param delay
     * @param tag
     * @return
     */
    public LObjectRequest createObjectRequest(String url, int delay, String tag) {
        LObjectRequest lor = new LObjectRequest(url, delay, tag);
        return lor;
    }

    /**
     * 以同步方式执行一个StringRequest，在返回之前会一直阻塞
     *
     * @param lsr
     * @return
     */
    public String perfromStringRequest(LStringRequest lsr) {
        final boolean[] stop = {false};
        final String[] result = new String[1];
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                result[0] = response;
                stop[0] = true;
                System.out.println("have fetched string, set tag to true.");
            }
        };
        lsr.setListener(listener);
        queue.add(lsr);
        System.out.println("get into loop.");
        /**
         * ???怎么把异步改成同步？
         */
        System.out.println("get out of loop.");
        return result[0];
    }

    /**
     * 以同步方式执行一个ObjectRequest，在返回之前会一直阻塞
     * 感觉这两个函数写的很丑陋
     *
     * @param lor
     * @return
     */
    public byte[] performObjectRequest(LObjectRequest lor) {
        final boolean[] stop = {false};
        final byte[][] result = new byte[1][];
        Response.Listener listener = new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {
                result[0] = response;
                stop[0] = true;
            }
        };
        lor.setListener(listener);
        queue.add(lor);
        while (!stop[0]) {
        }
        return result[0];
    }


    public void putStringRequest(LStringRequest lsr, Response.Listener rl) {
        lsr.setListener(rl);
        queue.add(lsr);
    }

    public void putObjectRequest(LObjectRequest lor, Response.Listener rl, Response.ProgressListener pl) {
        lor.setListener(rl);
        lor.setProgressListener(pl);
        queue.add(lor);
    }

    public void deleteRequest(String tag) {
        queue.cancelAll(tag);
    }


    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
