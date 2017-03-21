package com.vitelco;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class VitelcoMessagingService extends FirebaseMessagingService {

    private Context context;

    public VitelcoMessagingService() {
        this.context = getApplicationContext();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> dataMap = remoteMessage.getData();
        //extract details
        String message = dataMap.get(Constants.MESSAGE_KEY);
        String messageType = dataMap.get(Constants.MESSAGE_TYPE_KEY);
        String notificationId = dataMap.get(Constants.NOTIFICATION_ID_KEY);

        Bundle extras = new Bundle();
        extras.putString(Constants.MESSAGE_KEY, message);
        extras.putString(Constants.MESSAGE_TYPE_KEY, messageType);
        extras.putString(Constants.NOTIFICATION_ID_KEY, notificationId);
        //if message type is normal, just show the notification, else make the USSD like dialog
        if (messageType.equals(Constants.NORMAL_NOTIFICATION_TYPE)) {
            new Utils(context).makeNotification(message, extras);
        } else {
            String transactionId = dataMap.get(Constants.TRANSACTION_ID_KEY);
            extras.putString(Constants.TRANSACTION_ID_KEY, transactionId);

            SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor ed = prefs.edit();
            ed.putString(Constants.TRANSACTION_ID_KEY, transactionId);
            ed.putString(Constants.NOTIFICATION_ID_KEY, notificationId);
            ed.apply();

            //start the dialog activity
            Intent dialogActivityIntent = new Intent(context, Vitelco.class);
            dialogActivityIntent.putExtras(extras);
            dialogActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(dialogActivityIntent);
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }


}
