package com.vitelco;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.vitelco.views.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Vitelco extends BaseActivity {

    private EditText phoneEditText, teamNameEditText;
    private String teamName, phoneNumber, token;
    private CountryCodePicker countryCodePicker;
    private String countryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitelco);
        this.setFinishOnTouchOutside(false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        phoneEditText = (EditText) findViewById(R.id.phoneNumberEditText);
        teamNameEditText = (EditText) findViewById(R.id.teamNameEditText);
        countryCodePicker = (CountryCodePicker) findViewById(R.id.countryCodePicker);
        countryCode = Constants.DEFAULT_COUNTRY_CODE;
        countryCodePicker.setDefaultCountryUsingNameCode(countryCode.toLowerCase());
        countryCodePicker.setCountryForNameCode(countryCode.toLowerCase());
        checkIfLoggedIn();
        findViewById(R.id.continueButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidInput()) {
                    postToken();
                }
            }
        });
    }

    private void postToken() {
        final OkHttpClient client = Utils.getAllTrustingOkHttpClient();
        if (client == null) {
            Log.e(TAG, "getAllTrustingOkHttpClient returned null");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.REQUEST_TYPE_KEY, Constants.TOKEN_REFRESH_REQUEST);
            jsonObject.put(Constants.TEAM_NAME_KEY, teamName);
            jsonObject.put(Constants.MSISDN_KEY, phoneNumber);
            jsonObject.put(Constants.TOKEN_KEY, token);
        } catch (JSONException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }

        try {
            String data = jsonObject.toString();
            String url = Constants.API_URL + "/notification/update";
            Log.e(TAG, "Posting data:" + data + " to url: " + url);
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(Constants.JSON, data))
                    .build();
            activity.showProgress(R.string.login);
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, e.getLocalizedMessage(), e);
                    activity.hideProgress();
                    Toast.makeText(activity, activity.getString(R.string.login_error), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    activity.hideProgress();
                    Log.e(TAG, "Got response, status code: " + response.code() + ", body: " + response.body().string());
                    if (response.isSuccessful()) {
                        SharedPreferences prefs = activity.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor ed = prefs.edit();
                        ed.putString(Constants.LOGGED_IN_KEY, "1");
                        ed.apply();
                        activity.startActivity(new Intent(activity, Home.class));
                        activity.finish();
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(activity, activity.getString(R.string.login_error), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    private boolean isValidInput() {
        teamName = teamNameEditText.getText().toString().trim();
        phoneNumber = phoneEditText.getText().toString().trim();
        countryCode = countryCodePicker.getSelectedCountryNameCode().toUpperCase();

        if (teamName.isEmpty()) {
            teamNameEditText.setError("Required");
            return false;
        }

        if (phoneNumber.isEmpty()) {
            phoneEditText.setError("Required");
            return false;
        }

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber phoneNumberProto = phoneNumberUtil.parse(phoneNumber, countryCode);
            boolean isValid = phoneNumberUtil.isValidNumber(phoneNumberProto);
            if (isValid) {
                phoneNumber = phoneNumberUtil.format(phoneNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
                phoneEditText.setError(null);
            } else {
                phoneEditText.setError(getString(R.string.invalid_phone_number));
                return false;
            }
        } catch (NumberParseException e) {
            phoneEditText.setError(getString(R.string.invalid_phone_number));
            return false;
        }

        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(Constants.MSISDN_KEY, phoneNumber);
        ed.putString(Constants.TEAM_NAME_KEY, teamName);
        ed.apply();

        token = prefs.getString(Constants.TOKEN_KEY, "");
        if (token.isEmpty()) {
            Toast.makeText(this, getString(R.string.device_not_registered), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void checkIfLoggedIn() {
        String loggedIn = prefs.getString(Constants.LOGGED_IN_KEY, "");
        if (loggedIn.equals("1")) {
            startActivity(new Intent(this, Home.class));
            finish();
        }
    }

}
