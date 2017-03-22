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
import java.security.cert.CertificateException;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

    public static OkHttpClient getAllTrustingOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder.build();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }
    }
}
