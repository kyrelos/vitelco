package com.vitelco;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class VitelcoMessagingService extends FirebaseMessagingService {

    private static final String TAG = "VitelcoMessagingService";

    public VitelcoMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Context context = getApplicationContext();
        Map<String, String> dataMap = remoteMessage.getData();
        //extract details
        String message = dataMap.get("v_message");
        String messageType = dataMap.get("v_message_type");
        String notificationId = dataMap.get("v_notification_id");
        if (StringUtils.isBlank(message) && StringUtils.isBlank(messageType) && StringUtils.isBlank(notificationId)) {
            message = remoteMessage.getNotification().getBody();
            messageType = Constants.NORMAL_NOTIFICATION_TYPE;
            notificationId = RandomStringUtils.randomAlphanumeric(32);
        }
        Log.e(TAG, "Received message with details; message: " + message + " message_type: " + messageType + " notificationId: " + notificationId);
        Bundle extras = new Bundle();
        extras.putString(Constants.MESSAGE_KEY, message);
        extras.putString(Constants.MESSAGE_TYPE_KEY, messageType);
        extras.putString(Constants.NOTIFICATION_ID_KEY, notificationId);
        //if message type is normal, just show the notification, else make the USSD like dialog
        if (messageType.equals(Constants.NORMAL_NOTIFICATION_TYPE)) {
            //new Utils(context).makeNotification(message, extras);
        } else {
            String transactionId = dataMap.get("v_transaction_id");
            extras.putString(Constants.TRANSACTION_ID_KEY, transactionId);

            SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor ed = prefs.edit();
            ed.putString(Constants.TRANSACTION_ID_KEY, transactionId);
            ed.putString(Constants.NOTIFICATION_ID_KEY, notificationId);
            ed.apply();

            //start the dialog activity
            Intent dialogActivityIntent = new Intent(context, VitelcoTransaction.class);
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
