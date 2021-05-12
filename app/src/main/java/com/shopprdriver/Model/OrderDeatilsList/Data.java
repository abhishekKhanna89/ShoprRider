package com.shopprdriver.Model.OrderDeatilsList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("order")
    @Expose
    private Order order;

    @SerializedName("payment_text")
    @Expose
    private String payment_text;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getPayment_text() {
        return payment_text;
    }

    public void setPayment_text(String payment_text) {
        this.payment_text = payment_text;
    }
}
