package com.xxnjdlys.http.handler;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by xxnjdlys.
 * Date : 02/26/2019.
 * Time : 22:00.
 */
public abstract class JSONResponseHandler extends BaseResonpseHandler<JSONObject> {

    public final void onResponse(Call call, Response response) {
        if (call.isCanceled()) {
            return;
        }
        if (response == null) {
            postFailureResponse(404, new IOException("empty response."));
        } else if (!response.isSuccessful()) {
            postFailureResponse(response.code(), new IOException("unexpected response code."));
        } else {
            try {
                JSONObject result = new JSONObject(response.body().string());
                postSuccessResponse(result);
            } catch (Exception e) {
                postFailureResponse(response.code(), e);
            }
        }
    }
}
