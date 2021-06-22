package com.xxnjdlys.http.okhttp;

import android.os.Handler;
import android.os.Looper;


import java.io.IOException;
import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by mjz on 2016-04-28.
 */
public abstract class AsyncHttpResponseHandler implements Callback {

    static final String DEFAULT_CHARSET = "UTF-8";
    static final String UTF8_BOM = "\uFEFF";

    protected static final int BUFFER_SIZE = 4096;

    public static final MainThreadExecutor MAIN_THREAD_EXECUTOR = new MainThreadExecutor();

    public AsyncHttpResponseHandler() {
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        if (call.isCanceled()) {
            return;
        }
        postFailureResult(404,new Header[0], new byte[0], e);
    }

    @Override
    public final void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        if (call.isCanceled()) {
            return;
        }
        if (!response.isSuccessful()) {
            postFailureResult(response.code(), getHeadersFromResponse(response),
                    new byte[0], new RuntimeException("Unexpected code " + response.code()));
        } else {
            try {
                postSuccessResult(response.code(),
                        getHeadersFromResponse(response),
                        getResponseData(response));
            } catch (Exception e) {
                postFailureResult(response.code(), getHeadersFromResponse(response), new byte[0], e);
            }

        }
    }

    @NonNull
    protected byte[] getResponseData(Response response) throws IOException{
        if(response.request().method().equals("HEAD") || response.body() == null){
            return new byte[0];
        }
        return response.body().bytes();
    }

    public void postFailureResult(@NonNull final int statusCode,@NonNull final Header[] headers,
                                  @NonNull final byte[] responseBody, @NonNull final Throwable error) {
        MAIN_THREAD_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                onFailure(statusCode,headers, responseBody,  error);
            }
        });
    }

    public void postSuccessResult(@NonNull final int statusCode, @NonNull final Header[] headers, @NonNull final byte[] responseBody) {
        MAIN_THREAD_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                onSuccess(statusCode, headers, responseBody);
            }
        });
    }

    private Header[] getHeadersFromResponse (Response response) {
        int headerSize = response.headers().size();
        Header[] headers = new Header[headerSize];
        int index = 0;
        for(String name : response.headers().names()) {
            headers[ index++ ] = new Header(name, response.headers().get(name));
        }
        return headers;
    }

    public abstract void onSuccess(int statusCode, Header[] headers, byte[] responseBody);

    public abstract void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error);

    protected static class MainThreadExecutor implements Executor {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable r) {
            handler.post(r);
        }
    }

}
