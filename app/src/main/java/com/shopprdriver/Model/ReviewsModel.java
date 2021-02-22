package com.shopprdriver.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shopprdriver.Model.OrderDeatilsList.Data;
import com.shopprdriver.Model.OrderDeatilsList.Review;

import java.util.List;

public class ReviewsModel {


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

        @SerializedName("avgrating")
        @Expose
        private Integer avgrating;
        @SerializedName("totalreviews")
        @Expose
        private Integer totalreviews;
        @SerializedName("reviews")
        @Expose
        private List<Review> reviews = null;

        public Integer getAvgrating() {
            return avgrating;
        }

        public void setAvgrating(Integer avgrating) {
            this.avgrating = avgrating;
        }

        public Integer getTotalreviews() {
            return totalreviews;
        }

        public void setTotalreviews(Integer totalreviews) {
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
