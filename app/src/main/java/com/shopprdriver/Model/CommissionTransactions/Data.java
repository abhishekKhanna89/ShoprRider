package com.shopprdriver.Model.CommissionTransactions;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("commission_transactions")
    @Expose
    private List<CommissionTransaction> commissionTransactions = null;
    @SerializedName("commission")
    @Expose
    private String commission;

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

}
