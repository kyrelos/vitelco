package com.vitelco;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class VitelcoTransaction extends BaseActivity {

    private EditText pinEditText;
    private String pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        pinEditText = (EditText) findViewById(R.id.pinEditText);
        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidInput()) {
                    cancel();
                }
            }
        });
        findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidInput()) {
                    ok();
                }
            }
        });
    }

    private boolean isValidInput() {
        pin = pinEditText.getText().toString().trim();
        if(pin.isEmpty()){
            pinEditText.setError("Required");
            return false;
        }
        return true;
    }

    private void ok() {
        postUserResponse(pin, Constants.ACCEPTED);
    }

    private void cancel() {
        postUserResponse(pin, Constants.REJECTED);
    }

    private void postUserResponse(String pin, String status) {
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        String transactionId = prefs.getString(Constants.TRANSACTION_ID_KEY, "");
        String notificationId = prefs.getString(Constants.NOTIFICATION_ID_KEY, "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.REQUEST_TYPE_KEY, Constants.PUSH_RESPONSE_REQUEST);
            jsonObject.put(Constants.TRANSACTION_ID_KEY, transactionId);
            jsonObject.put(Constants.NOTIFICATION_ID_KEY, notificationId);
            jsonObject.put("status", status);
            jsonObject.put("pin", pin);
            Intent serviceIntent = new Intent(activity, PostPushResponseService.class);
            Bundle extras = new Bundle();
            extras.putString("data", jsonObject.toString());
            serviceIntent.putExtras(extras);
            startService(serviceIntent);
        } catch (JSONException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
