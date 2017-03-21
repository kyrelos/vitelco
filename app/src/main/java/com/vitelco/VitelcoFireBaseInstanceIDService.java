package com.vitelco;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

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
        if (phoneNumber.isEmpty()) {
            //post token to Api
            new Utils(getApplicationContext()).postToken(refreshedToken, phoneNumber, false);
        }
    }


}
