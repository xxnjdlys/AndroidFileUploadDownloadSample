package com.xxnjdlys.http.handler;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public abstract class TextResponseHandler extends BaseResonpseHandler<String> {

    private static final String UTF8_BOM = "\uFEFF";

    @Override
    public void onResponse(Call call, Response response) {
        if (call.isCanceled()) {
            return;
        }
        if (response == null) {
            postFailureResponse(404, new IOException("empty response"));
        } else if (!response.isSuccessful()) {
            postFailureResponse(response.code(), new IOException("unexpected response code"));
        } else {
            try {
                String data = getResponseString(response.body().bytes());
                postSuccessResponse(data);
            } catch (Exception e) {
                postFailureResponse(response.code(), e);
            }
        }
    }

    private String getResponseString(@NotNull byte[] stringBytes) {
        try {
            String toReturn = new String(stringBytes, "UTF-8");
            if (toReturn.startsWith(UTF8_BOM)) {
                return toReturn.substring(1);
            }
            return toReturn;
        } catch (Throwable e) {
            return "";
        }
    }
}
