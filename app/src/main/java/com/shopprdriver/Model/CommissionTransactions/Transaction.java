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
    @SerializedName("rider_delivery_charge")
    @Expose
    private Integer rider_delivery_charge;
    @SerializedName("time")
    @Expose
    private String time;

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

    public Integer getRider_delivery_charge() {
        return rider_delivery_charge;
    }

    public void setRider_delivery_charge(Integer rider_delivery_charge) {
        this.rider_delivery_charge = rider_delivery_charge;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}