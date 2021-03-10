package com.shopprdriver.Model.BankDetailsGet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BankDetailsGetModel {

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

        @SerializedName("user")
        @Expose
        private User user;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

    }
    public class User {

        @SerializedName("account_no")
        @Expose
        private String accountNo;
        @SerializedName("ifsc_code")
        @Expose
        private String ifscCode;
        @SerializedName("account_holder")
        @Expose
        private String accountHolder;
        @SerializedName("bank_name")
        @Expose
        private String bankName;

        public String getAccountNo() {
            return accountNo;
        }

        public void setAccountNo(String accountNo) {
            this.accountNo = accountNo;
        }

        public String getIfscCode() {
            return ifscCode;
        }

        public void setIfscCode(String ifscCode) {
            this.ifscCode = ifscCode;
        }

        public String getAccountHolder() {
            return accountHolder;
        }

        public void setAccountHolder(String accountHolder) {
            this.accountHolder = accountHolder;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

    }
}
