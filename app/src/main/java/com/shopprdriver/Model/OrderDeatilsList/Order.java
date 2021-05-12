package com.shopprdriver.Model.OrderDeatilsList;


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Order {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("refid")
    @Expose
    private String refid;
    @SerializedName("total")
    @Expose
    private String total;
    @SerializedName("service_charge")
    @Expose
    private String serviceCharge;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("payment_status")
    @Expose
    private String paymentStatus;
    @SerializedName("balance_used")
    @Expose
    private String balanceUsed;
    @SerializedName("show_deliver_button")
    @Expose
    private Integer showDeliverButton;
    @SerializedName("details")
    @Expose
    private List<Detail> details = null;
    @SerializedName("reviews")
    @Expose
    private List<Review> reviews = null;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRefid() {
        return refid;
    }

    public void setRefid(String refid) {
        this.refid = refid;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(String serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getBalanceUsed() {
        return balanceUsed;
    }

    public void setBalanceUsed(String balanceUsed) {
        this.balanceUsed = balanceUsed;
    }

    public Integer getShowDeliverButton() {
        return showDeliverButton;
    }

    public void setShowDeliverButton(Integer showDeliverButton) {
        this.showDeliverButton = showDeliverButton;
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

}
