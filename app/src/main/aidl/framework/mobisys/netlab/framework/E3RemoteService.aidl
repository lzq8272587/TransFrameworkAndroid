// E3RemoteService.aidl
package framework.mobisys.netlab.framework;

// Declare any non-default types here with import statements

interface E3RemoteService {
    /** Request the process ID of this service, to do evil things with it. */
    int getPid();
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

//     E3Framework getFrameworkInstance();
//     LStringRequest createStringRequest(String url, int delay, String tag);
//     LObjectRequest createObjectRequest(String url, int delay, String tag);
//     String perfromStringRequest(LStringRequest lsr);
//     byte[] performObjectRequest(LObjectRequest lor);
//     void putStringRequest(LStringRequest lsr, Response.Listener rl);
//     void putObjectRequest(LObjectRequest lor, Response.Listener rl, Response.ProgressListener pl);
//     void deleteRequest(String tag);

}