package com.shopprdriver.Model.UploadDocument;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadDocumentModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("form_step")
    @Expose
    private Integer formStep;
    @SerializedName("message")
    @Expose
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getFormStep() {
        return formStep;
    }

    public void setFormStep(Integer formStep) {
        this.formStep = formStep;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
