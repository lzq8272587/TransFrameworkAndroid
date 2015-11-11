package framework.mobisys.netlab.framework;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by LZQ on 11/11/2015.
 */
public class DownloadRequest<T> {

    URI uri;
    T data;
    String tag;
    long delay;
    long timeout;
    int intertime;
    UploadListener<T> uplistener;

    public DownloadRequest(String uri, T data, String tag, long delay, long timeout, int intertime, UploadListener<T> uplistener) throws URISyntaxException {
        this(new URI(uri), data, tag, delay, timeout, intertime, uplistener);
    }

    public DownloadRequest(URI uri, T data, String tag, long delay, long timeout, int intertime, UploadListener<T> uplistener) {
        this.uri = uri;
        this.data = data;
        this.tag = tag;
        this.delay = delay;
        this.timeout = timeout;
        this.intertime = intertime;
        this.uplistener = uplistener;
    }
}
