package com.shopprdriver.Model.UploadDocument;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetDocumentModel {

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

        @SerializedName("pan_card")
        @Expose
        private String panCard;
        @SerializedName("front_aadhaar_card")
        @Expose
        private String frontAadhaarCard;
        @SerializedName("front_dl_no")
        @Expose
        private String frontDlNo;
        @SerializedName("back_aadhaar_card")
        @Expose
        private String backAadhaarCard;
        @SerializedName("back_dl_no")
        @Expose
        private String backDlNo;
        @SerializedName("bike_front")
        @Expose
        private String bikeFront;
        @SerializedName("bike_back")
        @Expose
        private String bikeBack;

        public String getPanCard() {
            return panCard;
        }

        public void setPanCard(String panCard) {
            this.panCard = panCard;
        }

        public String getFrontAadhaarCard() {
            return frontAadhaarCard;
        }

        public void setFrontAadhaarCard(String frontAadhaarCard) {
            this.frontAadhaarCard = frontAadhaarCard;
        }

        public String getFrontDlNo() {
            return frontDlNo;
        }

        public void setFrontDlNo(String frontDlNo) {
            this.frontDlNo = frontDlNo;
        }

        public String getBackAadhaarCard() {
            return backAadhaarCard;
        }

        public void setBackAadhaarCard(String backAadhaarCard) {
            this.backAadhaarCard = backAadhaarCard;
        }

        public String getBackDlNo() {
            return backDlNo;
        }

        public void setBackDlNo(String backDlNo) {
            this.backDlNo = backDlNo;
        }

        public String getBikeFront() {
            return bikeFront;
        }

        public void setBikeFront(String bikeFront) {
            this.bikeFront = bikeFront;
        }

        public String getBikeBack() {
            return bikeBack;
        }

        public void setBikeBack(String bikeBack) {
            this.bikeBack = bikeBack;
        }

    }

}
