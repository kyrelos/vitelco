package com.vitelco;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import okhttp3.OkHttpClient;

public class Vitelco extends BaseActivity {

    private EditText phoneEditText;
    private String phoneNumber, token;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitelco);
        this.setFinishOnTouchOutside(false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        phoneEditText = (EditText) findViewById(R.id.phoneNumberEditText);
        checkIfLoggedIn();
        findViewById(R.id.continueButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidInput()) {
                    utils.postToken(token, phoneNumber, true);
                }
            }
        });
    }

    private boolean isValidInput() {
        phoneNumber = phoneEditText.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            phoneEditText.setError("Required");
            return false;
        }
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("msisdn", phoneNumber);
        ed.apply();
        token = prefs.getString("token", "");
        if (token.isEmpty()) {
            Toast.makeText(this, getString(R.string.device_not_registered), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void checkIfLoggedIn() {
        String loggedIn = prefs.getString("loggedIn", "");
        if (loggedIn.equals("1")) {
            startActivity(new Intent(this, Home.class));
        }
    }

}
