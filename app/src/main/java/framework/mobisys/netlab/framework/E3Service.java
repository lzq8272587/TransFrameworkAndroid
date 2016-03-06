package framework.mobisys.netlab.framework;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.android.volley.ERequest;
import com.android.volley.Request;
import com.android.volley.Response;


public class E3Service extends Service {

    private RemoteCallbackList<ICallback> mCallbacks = new RemoteCallbackList<ICallback>();
    private String TAG = "E3Service";
    private int BUFFER_SIZE = 1024;
    // private IService.Stub
    private E3Framework e3Framework = null;
    private final E3RemoteService.Stub e3Binder = new E3RemoteService.Stub() {

        /**
         * Request the process ID of this service, to do evil things with it.
         */
        @Override
        public int getPid() throws RemoteException {
            return 0;
        }

        /**
         * Demonstrates some basic types that you can use as parameters
         * and return values in AIDL.
         *
         * @param anInt
         * @param aLong
         * @param aBoolean
         * @param aFloat
         * @param aDouble
         * @param aString
         */
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void putERequest(String url, int delay, String tag, String sProperty, final ICallback callback) throws RemoteException {
            final ERequest er = e3Framework.createRequest(url, ERequest.ACTIVE, "NewByteRequest");
            er.setShouldCache(false);
            er.sProperty = sProperty;
            e3Framework.putERequest(er, new Response.Listener<byte[]>() {
                @Override
                public void onResponse(byte[] response) {
                    try {
                        callback.CallbackByte(response, er.url);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void putStringRequest(String url, int delay, String tag, final ICallback callback) throws RemoteException {
            Log.d(TAG, url + delay + tag);
            LStringRequest sr = e3Framework.createStringRequest(url, delay, tag);
            final ERequest text_er = e3Framework.createRequest(url, ERequest.ACTIVE, "New Text Request");
            text_er.setShouldCache(false);
            text_er.setEndTime(text_er.getEndTime() + 1000);

            e3Framework.putERequest(text_er, new Response.Listener<byte[]>() {
                @Override
                public void onResponse(byte[] response) {
                    try {
                        callback.CallbackByte(response,text_er.url);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
//            e3Framework.putStringRequest(sr, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    try {
//                        Log.d(TAG, "onResponse, get the result for request.");
//                        callback.CallbackString(response);
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

        }

        @Override
        public void putObjectRequest(final String url, int delay, String tag, final ICallback callback) throws RemoteException {
            Log.d(TAG, url + delay + tag);
            LObjectRequest or = e3Framework.createObjectRequest(url, delay, tag);
            e3Framework.putObjectRequest(or, new Response.Listener<byte[]>() {
                @Override
                public void onResponse(byte[] response) {
                    try {
                        Log.d(TAG, "onResponse, get the result for Object request. length=" + response.length);
                        byte[] b = new byte[BUFFER_SIZE];
                        Log.d(TAG, "before callbackobject.");
                        callback.CallbackByte(b,url);
                        Log.d(TAG, "after callbackobject.");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
        }

        @Override
        public String performStringRequest() throws RemoteException {
            return null;
        }

        @Override
        public byte[] performObjectRequest() throws RemoteException {
            return new byte[0];
        }

        @Override
        public void deleteRequest(String tag) throws RemoteException {

        }

        @Override
        public void registerCallback(ICallback cb) throws RemoteException {
            if (cb != null) {
                Log.d(TAG, "registerCallback is called.");
                //mCallbacks.register(cb);
                cb.showResult(666);
            }
        }

        @Override
        public void unregisterCallback(ICallback cb) throws RemoteException {
            if (cb != null) {
                //mCallbacks.unregister(cb);
            }
        }
    };

    public E3Service() {
        E3Framework e3 = E3Framework.getInstance(this);
    }

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate E3Service. create the framework");
        e3Framework = E3Framework.getInstance(this);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        Log.d(TAG, "onBind E3Service.");
        return e3Binder;
    }
}
