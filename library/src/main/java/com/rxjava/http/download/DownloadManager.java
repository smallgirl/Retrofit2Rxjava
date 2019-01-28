package com.rxjava.http.download;


import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
/**
 * Created by wanghaitao on 2018/2/28.
 * <p>
 * 保存下载的文件
 */

public class DownloadManager {


    /**
     * 保存文件
     * @param response     ResponseBody
     * @return 返回
     * @throws IOException
     */
    public File saveFile(ResponseBody response, final String fileDir, final String fileName, ProgressListener progressListener) throws IOException {
        long contentLength = response.contentLength();
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = response.byteStream();

            long sum = 0;

            File dir = new File(fileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir,fileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                Log.e("tag","contentLength----"+contentLength);

                final long finalSum = sum;
                if(contentLength<=1){
                    progressListener.onResponseProgress(1, 2, 50, false, file.getAbsolutePath());
                }else {
                    progressListener.onResponseProgress(finalSum, contentLength, (int) ((finalSum * 1.0f / contentLength)*100), finalSum == contentLength, file.getAbsolutePath());
                }
            }
            if(contentLength<=1){
                Log.e("tag","contentLength----alll");
                progressListener.onResponseProgress(1, 1, 100, true, file.getAbsolutePath());
            }
            fos.flush();

            return file;

        } finally {
            try {
                response.close();
                if (is != null)
                    is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
            }

        }
    }


}
