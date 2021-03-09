package com.shopprdriver.Model.TravelingDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TravelingDetailsModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private Data data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("commission_transactions")
        @Expose
        private List<CommissionTransaction> commissionTransactions = null;
        @SerializedName("total_km")
        @Expose
        private String totalKm;
        @SerializedName("commission")
        @Expose
        private String commission;

        public List<CommissionTransaction> getCommissionTransactions() {
            return commissionTransactions;
        }

        public void setCommissionTransactions(List<CommissionTransaction> commissionTransactions) {
            this.commissionTransactions = commissionTransactions;
        }

        public String getTotalKm() {
            return totalKm;
        }

        public void setTotalKm(String totalKm) {
            this.totalKm = totalKm;
        }

        public String getCommission() {
            return commission;
        }

        public void setCommission(String commission) {
            this.commission = commission;
        }

    }
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
    public class Transaction {

        @SerializedName("date")
        @Expose
        private String date;
        @SerializedName("shoppr_id")
        @Expose
        private Integer shopprId;
        @SerializedName("rider_commission")
        @Expose
        private String riderCommission;
        @SerializedName("km")
        @Expose
        private String km;
        @SerializedName("time")
        @Expose
        private String time;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Integer getShopprId() {
            return shopprId;
        }

        public void setShopprId(Integer shopprId) {
            this.shopprId = shopprId;
        }

        public String getRiderCommission() {
            return riderCommission;
        }

        public void setRiderCommission(String riderCommission) {
            this.riderCommission = riderCommission;
        }

        public String getKm() {
            return km;
        }

        public void setKm(String km) {
            this.km = km;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

    }
}