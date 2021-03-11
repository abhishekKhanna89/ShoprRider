package com.shopprdriver.Model.WorkDetailsView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkDetailsViewModel {

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
        @SerializedName("locations")
        @Expose
        private List<Location> locations = null;
        @SerializedName("work_locations")
        @Expose
        private List<WorkLocation> workLocations = null;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public List<Location> getLocations() {
            return locations;
        }

        public void setLocations(List<Location> locations) {
            this.locations = locations;
        }

        public List<WorkLocation> getWorkLocations() {
            return workLocations;
        }

        public void setWorkLocations(List<WorkLocation> workLocations) {
            this.workLocations = workLocations;
        }

    }
    public class User {

        @SerializedName("work_type")
        @Expose
        private String workType;

        public String getWorkType() {
            return workType;
        }

        public void setWorkType(String workType) {
            this.workType = workType;
        }

    }
    public class Location {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("isactive")
        @Expose
        private Integer isactive;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("city_id")
        @Expose
        private Object cityId;
        @SerializedName("created_at")
        @Expose
        private Object createdAt;
        @SerializedName("updated_at")
        @Expose
        private Object updatedAt;
        @SerializedName("deleted_at")
        @Expose
        private Object deletedAt;
        @SerializedName("pivot")
        @Expose
        private Pivot pivot;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getIsactive() {
            return isactive;
        }

        public void setIsactive(Integer isactive) {
            this.isactive = isactive;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getCityId() {
            return cityId;
        }

        public void setCityId(Object cityId) {
            this.cityId = cityId;
        }

        public Object getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Object createdAt) {
            this.createdAt = createdAt;
        }

        public Object getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(Object updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Object getDeletedAt() {
            return deletedAt;
        }

        public void setDeletedAt(Object deletedAt) {
            this.deletedAt = deletedAt;
        }

        public Pivot getPivot() {
            return pivot;
        }

        public void setPivot(Pivot pivot) {
            this.pivot = pivot;
        }

    }
    public class Pivot {

        @SerializedName("shoppr_id")
        @Expose
        private Integer shopprId;
        @SerializedName("location_id")
        @Expose
        private Integer locationId;

        public Integer getShopprId() {
            return shopprId;
        }

        public void setShopprId(Integer shopprId) {
            this.shopprId = shopprId;
        }

        public Integer getLocationId() {
            return locationId;
        }

        public void setLocationId(Integer locationId) {
            this.locationId = locationId;
        }

    }
    public class WorkLocation {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;

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

    }


}
