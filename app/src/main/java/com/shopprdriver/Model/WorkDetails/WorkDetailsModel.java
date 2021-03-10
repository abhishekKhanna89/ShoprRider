package com.shopprdriver.Model.WorkDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WorkDetailsModel {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("form_step")
    @Expose
    private int formStep;

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

    public int getFormStep() {
        return formStep;
    }

    public void setFormStep(int formStep) {
        this.formStep = formStep;
    }
}
