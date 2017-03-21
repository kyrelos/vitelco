package com.vitelco;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by paulmuriithi on 21/03/2017.
 */

public class PostPushResponseService extends Service {

    private static final String TAG = "PostPushResponseService";
    private Context context;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();
        Log.e(TAG, "Starting PostPushResponseService");
        Bundle extras = intent.getExtras();
        if (extras != null && extras.containsKey("data")) {
            postResponse(extras.getString("data"));
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void postResponse(String data) {
        final OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.API_URL + "/updatenotification")
                .post(RequestBody.create(Constants.JSON, data))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "Got response, status code: " + response.code() + ", body: " + response.body());
            }
        });
    }

}
