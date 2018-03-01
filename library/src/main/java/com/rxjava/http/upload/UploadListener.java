package com.rxjava.http.upload;

/**
 * Created by wanghaitao on 2018/2/28.
 * <p>
 * 上传文件回调
 */

public interface UploadListener {
    void onRequestProgress(long bytesWritten, long contentLength,int progress);
}
