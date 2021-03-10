package com.shopprdriver.RequestService;

public class PersonalDetailsRequest {
    String permanent_address,permanent_pin,secondary_mobile,emergency_mobile;
    int permanent_city,permanent_state;

    public String getPermanent_address() {
        return permanent_address;
    }

    public void setPermanent_address(String permanent_address) {
        this.permanent_address = permanent_address;
    }

    public String getPermanent_pin() {
        return permanent_pin;
    }

    public void setPermanent_pin(String permanent_pin) {
        this.permanent_pin = permanent_pin;
    }

    public String getSecondary_mobile() {
        return secondary_mobile;
    }

    public void setSecondary_mobile(String secondary_mobile) {
        this.secondary_mobile = secondary_mobile;
    }

    public String getEmergency_mobile() {
        return emergency_mobile;
    }

    public void setEmergency_mobile(String emergency_mobile) {
        this.emergency_mobile = emergency_mobile;
    }

    public int getPermanent_city() {
        return permanent_city;
    }

    public void setPermanent_city(int permanent_city) {
        this.permanent_city = permanent_city;
    }

    public int getPermanent_state() {
        return permanent_state;
    }

    public void setPermanent_state(int permanent_state) {
        this.permanent_state = permanent_state;
    }
}
