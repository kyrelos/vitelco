package com.vitelco;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by paulmuriithi on 21/03/2017.
 */

public class TokenResponse {

    @SerializedName("wallet_id")
    @Expose
    private String walletId;

    @SerializedName("msisdn")
    @Expose
    private String msisdn;

    public TokenResponse() {
    }

    public TokenResponse(String walletId, String msisdn) {
        this.walletId = walletId;
        this.msisdn = msisdn;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }
}
