package com.xxnjdlys.sample;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.xxnjdlys.http.okhttp.Header;
import com.xxnjdlys.http.okhttp.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "UPLOAD_DOWNLOAD_SAMPLE";

    // fixme: 修改下面上传文件的 url
    private static final String UPLOAD_URL = "http://xxx.xxx.com/file/upload";
    // fixme: 修改下面下载文件的 url
    private static final String DOWNLOAD_URL = "http://xxx.xxx.com/file/download";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnUpload = findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(v ->
                uploadLogFileFromAssets(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                        printLog(jsonObject.toString());
                        toast(jsonObject.toString());
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                        printLog(jsonArray.toString());
                        toast(jsonArray.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
                        printLog(statusCode + " , " + error.getMessage());
                        toast(statusCode + " , " + error.getMessage());
                    }
                })
        );

        Button btnDownload = findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(v ->
                downloadLogFile(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                        printLog(jsonObject.toString());
                        toast(jsonObject.toString());

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                        printLog(jsonArray.toString());
                        toast(jsonArray.toString());

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
                        printLog(statusCode + " , " + error.getMessage());
                        toast(statusCode + " , " + error.getMessage());
                    }
                })
        );
    }

    private void downloadLogFile(JsonHttpResponseHandler handler) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(DOWNLOAD_URL)
                    .get()
                    .build();
            client.newCall(request).enqueue(handler);
        } catch (Exception e) {
            printLog(e.getMessage());
        }
    }

    private void uploadLogFileFromAssets(JsonHttpResponseHandler handler) {
        File file = getFileFromAsset();
        if (file == null) {
            toast("upload file is null.");
            return;
        }
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(),
                            RequestBody.create(MediaType.parse("text/plain"), file))
                    .addFormDataPart("other_field", "other_field_value")
                    .build();
            Request request = new Request.Builder().url(UPLOAD_URL).post(formBody).build();
            client.newCall(request).enqueue(handler);
        } catch (Exception e) {
            printLog(e.getMessage());
        }

    }

    private File getFileFromAsset() {
        AssetManager am = getAssets();
        try {
            InputStream is = am.open("test.log");
            return createFileFromInputStream(is);
        } catch (IOException e) {
            return null;
        }
    }

    private File createFileFromInputStream(InputStream inputStream) {
        try {
            File f = new File(getCacheDir() + "/test.log");
            OutputStream outputStream = new FileOutputStream(f);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return f;
        } catch (IOException e) {
            //Logging exception
        }
        return null;
    }

    private void printLog(String message) {
        if (!TextUtils.isEmpty(message)) {
            Log.i(LOG_TAG, message);
        }
    }

    private void toast(String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        try {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } catch (Exception ignore) {

        }
    }
}