package com.shopprdriver.Model.CommissionTransactions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transaction {

    @SerializedName("refid")
    @Expose
    private String refid;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("rider_commission")
    @Expose
    private Integer riderCommission;

    public String getRefid() {
        return refid;
    }

    public void setRefid(String refid) {
        this.refid = refid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getRiderCommission() {
        return riderCommission;
    }

    public void setRiderCommission(Integer riderCommission) {
        this.riderCommission = riderCommission;
    }

}