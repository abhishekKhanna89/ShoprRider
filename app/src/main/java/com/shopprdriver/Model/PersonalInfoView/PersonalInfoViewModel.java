package com.shopprdriver.Model.PersonalInfoView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PersonalInfoViewModel {

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
        @SerializedName("states")
        @Expose
        private List<State> states = null;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public List<State> getStates() {
            return states;
        }

        public void setStates(List<State> states) {
            this.states = states;
        }

    }

    public class User {

        @SerializedName("permanent_address")
        @Expose
        private String permanentAddress;
        @SerializedName("permanent_city")
        @Expose
        private String permanentCity;
        @SerializedName("permanent_pin")
        @Expose
        private String permanentPin;
        @SerializedName("secondary_mobile")
        @Expose
        private String secondaryMobile;
        @SerializedName("emergency_mobile")
        @Expose
        private String emergencyMobile;
        @SerializedName("permanent_state")
        @Expose
        private String permanentState;

        public String getPermanentAddress() {
            return permanentAddress;
        }

        public void setPermanentAddress(String permanentAddress) {
            this.permanentAddress = permanentAddress;
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

        public String getPermanentState() {
            return permanentState;
        }

        public void setPermanentState(String permanentState) {
            this.permanentState = permanentState;
        }

    }
    public class State {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("isactive")
        @Expose
        private Integer isactive;
        @SerializedName("cities")
        @Expose
        private List<City> cities = null;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getIsactive() {
            return isactive;
        }

        public void setIsactive(Integer isactive) {
            this.isactive = isactive;
        }

        public List<City> getCities() {
            return cities;
        }

        public void setCities(List<City> cities) {
            this.cities = cities;
        }

    }
    public class City {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("state_id")
        @Expose
        private Integer stateId;
        @SerializedName("isactive")
        @Expose
        private Integer isactive;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getStateId() {
            return stateId;
        }

        public void setStateId(Integer stateId) {
            this.stateId = stateId;
        }

        public Integer getIsactive() {
            return isactive;
        }

        public void setIsactive(Integer isactive) {
            this.isactive = isactive;
        }

    }

}