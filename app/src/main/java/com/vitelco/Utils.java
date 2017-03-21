package com.vitelco;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by paulmuriithi on 21/03/2017.
 */

public class Utils {

    private static final String TAG = "Utils";
    private BaseActivity activity;
    private Context context;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public Utils(BaseActivity activity) {
        this.activity = activity;
        this.context = activity;
    }

    public Utils(Context context) {
        this.context = context;
    }

    public void makeNotification(String message) {
        makeNotification(message, new Bundle());
    }

    public void makeNotification(String message, Bundle extras) {
        makeNotification(message, new Random(System.currentTimeMillis()).nextInt(), extras);
    }

    public void makeNotification(String message, int notificationId, Bundle extras) {
        makeNotification(activity.getString(R.string.app_name), message, notificationId, Vitelco.class, extras);
    }

    public void makeNotification(String title, String message, int notificationId, Class<?> activity, Bundle extras) {
        makeNotification(title, message, notificationId, activity, R.mipmap.ic_launcher, extras);
    }

    public void makeNotification(String title, String message, int notificationId, Class<?> activity, int smallIcon, Bundle extras) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, activity);
        if (extras == null) {
            extras = new Bundle();
            extras.putString("type", Constants.NORMAL_NOTIFICATION_TYPE);
            extras.putString("message", message);
        }
        extras.putInt("notificationId", notificationId);
        notificationIntent.putExtras(extras);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(smallIcon)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (uri != null) {
            builder.setSound(uri);
        }
        builder.setContentIntent(pendingNotificationIntent);
        notificationManager.notify(notificationId, builder.build());
    }

    public void postToken(final String token, final String msisdn, final boolean showProgress) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.REQUEST_TYPE_KEY, Constants.TOKEN_REFRESH_REQUEST);
            jsonObject.put(Constants.MSISDN_KEY, msisdn);
            jsonObject.put(Constants.TOKEN_KEY, token);
        } catch (JSONException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }

        Request request = new Request.Builder()
                .url(Constants.API_URL + "/updatenotification")
                .post(RequestBody.create(Constants.JSON, jsonObject.toString()))
                .build();

        if (showProgress) {
            activity.showProgress(R.string.login);
        }
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                if (showProgress) {
                    activity.hideProgress();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (showProgress) {
                    activity.hideProgress();
                }
                Log.d(TAG, "Got response, status code: " + response.code() + ", body: " + response.body());
                if (!response.isSuccessful()) {
                    Log.e(TAG, response.body().toString());
                    Toast.makeText(context, context.getString(R.string.login_error), Toast.LENGTH_LONG).show();
                }
                TokenResponse tokenResponse = gson.fromJson(response.body().charStream(), TokenResponse.class);
                SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor ed = prefs.edit();
                ed.putString(Constants.WALLET_ID_KEY, tokenResponse.getWalletId());
                //ed.putString(Constants.MSISDN_KEY,tokenResponse.getMsisdn());
                ed.apply();
            }
        });
    }

    public void postUserResponse(String pin, String status) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        String transactionId = prefs.getString(Constants.TRANSACTION_ID_KEY, "");
        String notificationId = prefs.getString(Constants.NOTIFICATION_ID_KEY, "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.REQUEST_TYPE_KEY, Constants.PUSH_RESPONSE_REQUEST);
            jsonObject.put(Constants.TRANSACTION_ID_KEY, transactionId);
            jsonObject.put(Constants.NOTIFICATION_ID_KEY, notificationId);
            jsonObject.put("status", status);
            jsonObject.put("pin", pin);
            Intent serviceIntent = new Intent(context, PostPushResponseService.class);
            Bundle extras = new Bundle();
            extras.putString("data", jsonObject.toString());
            serviceIntent.putExtras(extras);
            context.startService(serviceIntent);
        } catch (JSONException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }
}
