package framework.mobisys.netlab.framework;

import android.content.Context;

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
public class TransFramework {

    private Context ctx;

    public TransFramework(Context c) {
        ctx = c;
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
}
