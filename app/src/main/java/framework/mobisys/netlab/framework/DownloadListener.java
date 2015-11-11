package framework.mobisys.netlab.framework;

/**
 * Created by LZQ on 11/11/2015.
 */
public interface DownloadListener<T> {
    void onFinished(T data);

    void onTimeout();

    void onProgress(int percentage);
}
