package com.rxjava.http.download;

/**
 * Created by wanghaitao on 2018/2/28.
 * <p>
 * 下载监听
 */

public interface ProgressListener {

    /**
     * 载进度监听
     *
     * @param bytesRead     已经下载文件的大小
     * @param contentLength 文件的大小
     * @param done          是否下载完成
     */
    void onResponseProgress(long bytesRead, long contentLength, int progress, boolean done, String filePath);
    void onError(Exception e);


}
