package com.xxnjdlys.http.okhttp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;
import okhttp3.internal.Util;

public abstract class FileAsyncHttpResponseHandler extends AsyncHttpResponseHandler {

    private boolean mIsStartDownload = false;

    protected File mFile;

    public FileAsyncHttpResponseHandler(File file) {
        this.mFile = file;
    }

    @Override
    public final void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        onSuccess(statusCode, headers, mFile);
    }

    @Override
    public abstract void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error);

    public abstract void onSuccess (int statusCode, Header[] headers, File file);

    /**
     * Fired when the request progress, override to handle in your own code
     *
     * @param bytesWritten offset from start of file
     * @param totalSize    total size of file
     */
    public void onProgress(long bytesWritten, long totalSize) {
    }

    public void onStart(){

    }

    @Override
    protected byte[] getResponseData(Response response) throws IOException {
        if (response != null) {
            long contentLength = response.body().contentLength();
            postStart();
            inputStreamToFile(response.body().byteStream(), mFile, contentLength);
        }
        return null;
    }

    long lastWrite = 0;
    long lastPostProgress = 0;
    public boolean inputStreamToFile (InputStream inputStream, File file, long contentLength) throws IOException{
        if (file == null) {
            return false;
        }
        File dir = file.getParentFile();
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        FileOutputStream buffer = new FileOutputStream(file);
        if (inputStream != null) {
            try {
                byte[] tmp = new byte[BUFFER_SIZE];
                int l;
                long written = 0;
                while ((l = inputStream.read(tmp)) != -1) {
                    buffer.write(tmp, 0, l);
                    written += l;
                    if (System.currentTimeMillis() - lastPostProgress > 1000 || written == contentLength) {
//                        float ks = (float) ((written - lastWrite) / (1024));
//                        float ms = (float) (ks / (1024));
//                        Log.d("baok", "\ninputStreamToFile \n" + "k/s = " + ks + "   m/s = = " + ms);
                        postProgress(written, contentLength);
//                        lastWrite = written;
                        lastPostProgress = System.currentTimeMillis();
                    }
                }
                return true;
            } finally {
                Util.closeQuietly(inputStream);
                buffer.flush();
                Util.closeQuietly(buffer);
            }
        }
        return false;
    }

    private void postStart() {
        if (!mIsStartDownload) {
            MAIN_THREAD_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    onStart();
                }
            });
            mIsStartDownload = true;
        }
    }

    private void postProgress(final long bytesWritten, final long totalSize) {
        MAIN_THREAD_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                onProgress(bytesWritten, totalSize);
            }
        });
    }

}
