package com.shopprdriver.Model.CommissionTransactions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {

    @SerializedName("commission_transactions")
    @Expose
    private List<CommissionTransaction> commissionTransactions = null;
    @SerializedName("commission")
    @Expose
    private String commission;
    @SerializedName("delivery_charge")
    @Expose
    private String delivery_charge;

    public List<CommissionTransaction> getCommissionTransactions() {
        return commissionTransactions;
    }

    public void setCommissionTransactions(List<CommissionTransaction> commissionTransactions) {
        this.commissionTransactions = commissionTransactions;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getDelivery_charge() {
        return delivery_charge;
    }

    public void setDelivery_charge(String delivery_charge) {
        this.delivery_charge = delivery_charge;
    }
}
