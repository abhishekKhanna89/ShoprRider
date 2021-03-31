package com.shopprdriver.Model.Profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileModel {

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

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("status")
        @Expose
        private Integer status;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("mobile")
        @Expose
        private String mobile;
        @SerializedName("password")
        @Expose
        private String password;
        @SerializedName("city")
        @Expose
        private Integer city;
        @SerializedName("state")
        @Expose
        private Integer state;
        @SerializedName("email")
        @Expose
        private Object email;
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("lat")
        @Expose
        private String lat;
        @SerializedName("lang")
        @Expose
        private String lang;
        @SerializedName("location")
        @Expose
        private Object location;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("isactive")
        @Expose
        private Integer isactive;
        @SerializedName("notification_token")
        @Expose
        private String notificationToken;
        @SerializedName("sendbird_token")
        @Expose
        private String sendbirdToken;
        @SerializedName("pay_per_km")
        @Expose
        private Integer payPerKm;
        @SerializedName("pay_commission")
        @Expose
        private Integer payCommission;
        @SerializedName("pay_delivery")
        @Expose
        private Integer payDelivery;
        @SerializedName("pan_card")
        @Expose
        private String panCard;
        @SerializedName("front_aadhaar_card")
        @Expose
        private String frontAadhaarCard;
        @SerializedName("front_dl_no")
        @Expose
        private String frontDlNo;
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
        @SerializedName("form_step")
        @Expose
        private Integer formStep;
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
        @SerializedName("work_type")
        @Expose
        private String workType;
        @SerializedName("secondary_mobile")
        @Expose
        private String secondaryMobile;
        @SerializedName("emergency_mobile")
        @Expose
        private String emergencyMobile;
        @SerializedName("permanent_address")
        @Expose
        private String permanentAddress;
        @SerializedName("permanent_state")
        @Expose
        private Integer permanentState;
        @SerializedName("permanent_city")
        @Expose
        private String permanentCity;
        @SerializedName("permanent_pin")
        @Expose
        private String permanentPin;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Integer getCity() {
            return city;
        }

        public void setCity(Integer city) {
            this.city = city;
        }

        public Integer getState() {
            return state;
        }

        public void setState(Integer state) {
            this.state = state;
        }

        public Object getEmail() {
            return email;
        }

        public void setEmail(Object email) {
            this.email = email;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public Object getLocation() {
            return location;
        }

        public void setLocation(Object location) {
            this.location = location;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Integer getIsactive() {
            return isactive;
        }

        public void setIsactive(Integer isactive) {
            this.isactive = isactive;
        }

        public String getNotificationToken() {
            return notificationToken;
        }

        public void setNotificationToken(String notificationToken) {
            this.notificationToken = notificationToken;
        }

        public String getSendbirdToken() {
            return sendbirdToken;
        }

        public void setSendbirdToken(String sendbirdToken) {
            this.sendbirdToken = sendbirdToken;
        }

        public Integer getPayPerKm() {
            return payPerKm;
        }

        public void setPayPerKm(Integer payPerKm) {
            this.payPerKm = payPerKm;
        }

        public Integer getPayCommission() {
            return payCommission;
        }

        public void setPayCommission(Integer payCommission) {
            this.payCommission = payCommission;
        }

        public Integer getPayDelivery() {
            return payDelivery;
        }

        public void setPayDelivery(Integer payDelivery) {
            this.payDelivery = payDelivery;
        }

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

        public Integer getFormStep() {
            return formStep;
        }

        public void setFormStep(Integer formStep) {
            this.formStep = formStep;
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

        public String getWorkType() {
            return workType;
        }

        public void setWorkType(String workType) {
            this.workType = workType;
        }

        public String getSecondaryMobile() {
            return secondaryMobile;
        }

        public void setSecondaryMobile(String secondaryMobile) {
            this.secondaryMobile = secondaryMobile;
        }

        public String getEmergencyMobile() {
            return emergencyMobile;
        }

        public void setEmergencyMobile(String emergencyMobile) {
            this.emergencyMobile = emergencyMobile;
        }

        public String getPermanentAddress() {
            return permanentAddress;
        }

        public void setPermanentAddress(String permanentAddress) {
            this.permanentAddress = permanentAddress;
        }

        public Integer getPermanentState() {
            return permanentState;
        }

        public void setPermanentState(Integer permanentState) {
            this.permanentState = permanentState;
        }

        public String getPermanentCity() {
            return permanentCity;
        }

        public void setPermanentCity(String permanentCity) {
            this.permanentCity = permanentCity;
        }

        public String getPermanentPin() {
            return permanentPin;
        }

        public void setPermanentPin(String permanentPin) {
            this.permanentPin = permanentPin;
        }

    }
}
