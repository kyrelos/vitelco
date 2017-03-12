package com.example.vitelco.vitelco;
import android.content.SharedPreferences;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class VitelcoFireBaseInstanceIDService extends FirebaseInstanceIdService {
    SharedPreferences prefs;

    public VitelcoFireBaseInstanceIDService() {
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        prefs = getSharedPreferences("fcm_token", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("token", refreshedToken);
        ed.apply();
        System.out.println("refreshed_token:"+ refreshedToken);

    }
}
