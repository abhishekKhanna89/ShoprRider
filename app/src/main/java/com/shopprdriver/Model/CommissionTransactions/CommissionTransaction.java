package com.shopprdriver.Model.CommissionTransactions;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommissionTransaction {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("transactions")
    @Expose
    private List<Transaction> transactions = null;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

}