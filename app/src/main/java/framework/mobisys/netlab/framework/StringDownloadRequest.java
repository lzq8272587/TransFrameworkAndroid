package framework.mobisys.netlab.framework;

/**
 * Created by LZQ on 11/11/2015.
 */
public class StringDownloadRequest<T> {

    String uri;
    String tag;
    long delay;
    long timeout;
    int intertime;
    UploadListener<String> uplistener;

    public StringDownloadRequest(String uri, String tag, long delay, long timeout, int intertime, UploadListener<String> uplistener) {
        this.uri = uri;
        this.tag = tag;
        this.delay = delay;
        this.timeout = timeout;
        this.intertime = intertime;
        this.uplistener = uplistener;
    }
}
