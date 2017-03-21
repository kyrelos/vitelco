package com.vitelco;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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
        utils.postUserResponse(pin, Constants.ACCEPTED);
    }

    private void cancel() {
        utils.postUserResponse(pin, Constants.REJECTED);
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
