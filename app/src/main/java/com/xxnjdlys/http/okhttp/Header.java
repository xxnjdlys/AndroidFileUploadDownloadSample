package com.xxnjdlys.http.okhttp;

import android.text.TextUtils;
import android.util.Pair;

/**
 * Created by mjz on 2016-05-02.
 */
public class Header {

    private Pair<String, String> pair;

    public Header (String key, String value) {
        pair = new Pair<>(key, value);
    }

    public String getName () {
        if (!TextUtils.isEmpty(pair.first)) {
            return pair.first;
        }
        return "";
    }


    public String getValue() {
        if (!TextUtils.isEmpty(pair.second)) {
            return pair.second;
        }
        return "";
    }

}
