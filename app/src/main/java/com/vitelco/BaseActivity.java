package com.vitelco;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

public class BaseActivity extends AppCompatActivity {

    protected static final String TAG = "BaseActivity";
    protected BaseActivity activity;
    protected MaterialDialog progressDialog;
    protected Utils utils;
    protected VitelcoApplication application;
    protected Toolbar toolbar;
    protected SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        application = (VitelcoApplication) getApplication();
        prefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE);
        utils = new Utils(activity);
    }

    public void showProgress(int title, int message) {
        showProgress(getString(title), getString(message));
    }

    public void showProgress(int title) {
        showProgress(getString(title), getString(R.string.please_wait));
    }

    public void showProgress(String title) {
        showProgress(title, getString(R.string.please_wait));
    }

    public void showProgress(String title, String message) {
        try {
            if (progressDialog == null || !progressDialog.isShowing()) {
                progressDialog = new MaterialDialog.Builder(this)
                        .title(title)
                        .content(message)
                        .cancelable(false)
                        .progress(true, 0)
                        .show();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    public void hideProgress() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    void quitApp() {
        activity.finish();
        activity.moveTaskToBack(true);
    }

    public Utils getUtils() {
        return utils;
    }

    public void setUtils(Utils utils) {
        this.utils = utils;
    }

}
