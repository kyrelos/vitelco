package com.vitelco;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class Home extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.setFinishOnTouchOutside(false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView teamNameTextView = (TextView) findViewById(R.id.teamNameTextView);
        TextView phoneTextView = (TextView) findViewById(R.id.phoneNumberTextView);

        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        teamNameTextView.setText(String.format(getString(R.string.team), prefs.getString(Constants.TEAM_NAME_KEY, "")));
        phoneTextView.setText(String.format(getString(R.string.phone), prefs.getString(Constants.MSISDN_KEY, "")));
    }

}
