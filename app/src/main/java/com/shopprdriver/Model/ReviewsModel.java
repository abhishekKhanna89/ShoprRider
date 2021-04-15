package com.shopprdriver.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewsModel {


    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private Data data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public class Data {

        @SerializedName("avgrating")
        @Expose
        private Float avgrating;
        @SerializedName("totalreviews")
        @Expose
        private String totalreviews;
        @SerializedName("reviews")
        @Expose
        private List<Review> reviews = null;

        public Float getAvgrating() {
            return avgrating;
        }

        public void setAvgrating(Float avgrating) {
            this.avgrating = avgrating;
        }

        public String getTotalreviews() {
            return totalreviews;
        }

        public void setTotalreviews(String totalreviews) {
            this.totalreviews = totalreviews;
        }

        public List<Review> getReviews() {
            return reviews;
        }

        public void setReviews(List<Review> reviews) {
            this.reviews = reviews;
        }

    }
    public class Review {

        @SerializedName("reviews")
        @Expose
        private String reviews;
        @SerializedName("rating")
        @Expose
        private String rating;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("image")
        @Expose
        private String image;

        public String getReviews() {
            return reviews;
        }

        public void setReviews(String reviews) {
            this.reviews = reviews;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

    }

}
