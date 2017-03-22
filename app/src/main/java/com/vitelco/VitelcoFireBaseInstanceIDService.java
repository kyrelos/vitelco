package com.vitelco;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VitelcoFireBaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "InstanceIDService";

    public VitelcoFireBaseInstanceIDService() {
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(Constants.TOKEN_KEY, refreshedToken);
        ed.apply();
        Log.d(TAG, "refreshed_token:" + refreshedToken);

        String phoneNumber = prefs.getString(Constants.MSISDN_KEY, "");
        if (!phoneNumber.isEmpty()) {
            //post token to Api
            final OkHttpClient client = Utils.getAllTrustingOkHttpClient();
            if (client == null) {
                Log.e(TAG, "getAllTrustingOkHttpClient returned null");
                return;
            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(Constants.REQUEST_TYPE_KEY, Constants.TOKEN_REFRESH_REQUEST);
                jsonObject.put(Constants.MSISDN_KEY, phoneNumber);
                jsonObject.put(Constants.TOKEN_KEY, refreshedToken);
            } catch (JSONException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }

            try {
                String data = jsonObject.toString();
                String url = Constants.API_URL + "/notification/update";
                Log.d(TAG, "Posting data:" + data + " to url: " + url);
                Request request = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(Constants.JSON, data))
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, e.getLocalizedMessage(), e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, "Got response, status code: " + response.code() + ", body: " + response.body().string());
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }
        }
    }
}
