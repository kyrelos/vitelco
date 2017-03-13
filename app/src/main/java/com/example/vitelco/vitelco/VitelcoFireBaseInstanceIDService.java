package com.example.vitelco.vitelco;
import android.content.SharedPreferences;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class VitelcoFireBaseInstanceIDService extends FirebaseInstanceIdService {
    SharedPreferences prefs;
    // duPqUip-1LI:APA91bHkgcpMU0FtZ--4MqxRkal7ipJVWi1b8Mo_j2vxBwdqD2ztLkfMTLzXEH8ORKTbJwUVgfKUgXFo0m7UIclnBfdHiHq31ziTry3WUG7W3hFPOt7QgPTciWBNrL2QSez56ZFGIiCZ

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
