package com.vitelco;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class VitelcoTransaction extends BaseActivity {

    private String pin, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        message = getString(R.string.transaction_title);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String msg = extras.getString(Constants.MESSAGE_KEY);
            if (!StringUtils.isBlank(msg)) {
                message = msg;
            }
        }
        new MaterialDialog.Builder(this)
                .cancelable(false)
                .titleColorRes(R.color.colorPrimary)
                .contentColorRes(R.color.dark_grey)
                .backgroundColorRes(R.color.white)
                .positiveColorRes(R.color.colorAccent)
                .negativeColorRes(R.color.colorAccent)
                .title(R.string.transaction_title).content(message)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.e(TAG, "Ok transaction");
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.e(TAG, "Cancel transaction");
                        cancel();
                    }
                })
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .inputRangeRes(4, 4, R.color.material_red_500)
                .input(getString(R.string.pin), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        pin = input.toString();
                        Log.e(TAG, "Input: " + pin);
                        ok();
                    }
                })
                .build().show();
    }

    private void ok() {
        postUserResponse(pin, Constants.ACCEPTED);
        Toast.makeText(activity, getString(R.string.ok_thanks), Toast.LENGTH_LONG).show();
        quitApp();
    }

    private void cancel() {
        postUserResponse(pin, Constants.REJECTED);
        Toast.makeText(activity, getString(R.string.cancelled), Toast.LENGTH_LONG).show();
        quitApp();
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
