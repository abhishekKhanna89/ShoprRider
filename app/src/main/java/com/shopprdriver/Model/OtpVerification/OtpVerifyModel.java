package com.shopprdriver.Model.OtpVerification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OtpVerifyModel {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("form_step")
    @Expose
    private int form_step;


    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("user_id")
    @Expose
    private String user_id;
    @SerializedName("sendbird_token")
    @Expose
    private String sendbird_token;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getForm_step() {
        return form_step;
    }

    public void setForm_step(int form_step) {
        this.form_step = form_step;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSendbird_token() {
        return sendbird_token;
    }

    public void setSendbird_token(String sendbird_token) {
        this.sendbird_token = sendbird_token;
    }
}
