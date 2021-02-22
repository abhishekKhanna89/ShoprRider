package com.shopprdriver.Model.Attendences;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attendence {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("checkin")
    @Expose
    private String checkin;
    @SerializedName("checkin-address")
    @Expose
    private String checkinAddress;
    @SerializedName("checkout")
    @Expose
    private String checkout;
    @SerializedName("checkout-address")
    @Expose
    private String checkoutAddress;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public String getCheckinAddress() {
        return checkinAddress;
    }

    public void setCheckinAddress(String checkinAddress) {
        this.checkinAddress = checkinAddress;
    }

    public String getCheckout() {
        return checkout;
    }

    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }

    public String getCheckoutAddress() {
        return checkoutAddress;
    }

    public void setCheckoutAddress(String checkoutAddress) {
        this.checkoutAddress = checkoutAddress;
    }

}
