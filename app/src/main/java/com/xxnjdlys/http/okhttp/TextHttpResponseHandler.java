package com.xxnjdlys.http.okhttp;


import org.jetbrains.annotations.NotNull;

public abstract class TextHttpResponseHandler extends AsyncHttpResponseHandler {

    @Override
    public final void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        onSuccess(statusCode, headers, getResponseString(responseBody, DEFAULT_CHARSET));
    }

    @Override
    public final void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
        onFailure(statusCode, headers, getResponseString(responseBody, DEFAULT_CHARSET), error);
    }

    public abstract void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error);

    public abstract void onSuccess(int statusCode, Header[] headers, String responseString);

    /**
     * Attempts to encode response bytes as string of set encoding
     *
     * @param charset     charset to create string with
     * @param stringBytes response bytes
     * @return String of set encoding or null
     */
    private static String getResponseString(@NotNull byte[] stringBytes, String charset) {
        try {
            String toReturn = new String(stringBytes, charset);
            if (toReturn.startsWith(UTF8_BOM)) {
                return toReturn.substring(1);
            }
            return toReturn;
        } catch (Throwable e) {
            return "";
        }
    }

}
