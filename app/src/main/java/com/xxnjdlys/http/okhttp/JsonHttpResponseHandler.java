package com.xxnjdlys.http.okhttp;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public abstract class JsonHttpResponseHandler extends TextHttpResponseHandler {


    public abstract void onSuccess (int statusCode, Header[] headers, JSONObject jsonObject);

    public abstract void onSuccess (int statusCode, Header[] headers, JSONArray jsonArray);

    @Override
    public abstract void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error);

    @Override
    public final void onSuccess(int statusCode, Header[] headers, String responseString) {
        if (statusCode != 204 ) { // 204 no content
            try {
                Object json = parseContentToJson(responseString);
                if (json != null) {
                    if (json instanceof JSONObject) {
                        onSuccess(statusCode, headers, (JSONObject)json);
                    } else if (json instanceof JSONArray) {
                        onSuccess(statusCode, headers, (JSONArray)json);
                    } else {
                        onFailure(statusCode, headers, responseString.getBytes(), new RuntimeException("Parse json failed"));
                    }
                } else {
                    onFailure(statusCode, headers, responseString.getBytes(), new RuntimeException("Parse json failed"));
                }
            } catch (JSONException e) {
                onFailure(statusCode, headers, responseString.getBytes(), e);
            }
        } else {
            onFailure(statusCode, headers, responseString.getBytes(), new RuntimeException("Response has no content"));
        }
    }

    private Object parseContentToJson (String jsonString)  throws JSONException {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        Object result = null;
        jsonString = jsonString.trim();
        if (jsonString.startsWith(UTF8_BOM)) {
            jsonString = jsonString.substring(1);
        }
        if (jsonString.startsWith("{") || jsonString.startsWith("[")) {
            try{
                result = new JSONTokener(jsonString).nextValue();
            }catch (Exception ignore){
            }
        }
        return result;
    }

}
