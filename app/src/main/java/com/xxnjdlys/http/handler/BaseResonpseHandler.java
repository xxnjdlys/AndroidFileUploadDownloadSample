package com.xxnjdlys.http.handler;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.concurrent.Executor;

import androidx.annotation.UiThread;
import okhttp3.Call;
import okhttp3.Callback;

/**
 * Created by xxnjdlys.
 * Date : 02/26/2019.
 * Time : 23:16.
 */
public abstract class BaseResonpseHandler<T> implements Callback {

    protected MainThreadExecutor mainThreadExecutor = new MainThreadExecutor();

    public final void onFailure(Call call, IOException e) {
        if (call.isCanceled()) {
            return;
        }
        postFailureResponse(404, e);
    }

    protected void postFailureResponse(final int statusCode, final Throwable error) {
        mainThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                onFailure(statusCode, error);
            }
        });
    }

    protected void postSuccessResponse(final T result) {
        mainThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                onSuccess(result);
            }
        });
    }

    protected void postProgress(final float progress) {
        mainThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                onProgress(progress);
            }
        });
    }

    @UiThread
    public abstract void onFailure(int statusCode, Throwable error);

    @UiThread
    public abstract void onSuccess(T response);

    @UiThread
    public void onProgress(float progress) {

    }

    public static class MainThreadExecutor implements Executor {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable r) {
            handler.post(r);
        }
    }
}
