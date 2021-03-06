package com.shopprdriver.Model.PersonalDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PersonalDetailsModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("form_step")
    @Expose
    private int form_step;
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getForm_step() {
        return form_step;
    }

    public void setForm_step(int form_step) {
        this.form_step = form_step;
    }
}
