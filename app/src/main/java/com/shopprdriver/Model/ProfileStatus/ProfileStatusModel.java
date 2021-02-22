package com.shopprdriver.Model.ProfileStatus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileStatusModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("form_step")
    @Expose
    private Integer formStep;

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

}