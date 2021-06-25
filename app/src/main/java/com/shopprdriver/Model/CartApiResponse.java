package com.shopprdriver.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CartApiResponse {

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }




    public class Data {

        @SerializedName("items")
        @Expose
        private List<Item> items = null;
        @SerializedName("total")
        @Expose
        private String total;
        @SerializedName("service_charge")
        @Expose
        private String serviceCharge;
        @SerializedName("grand_total")
        @Expose
        private String grandTotal;
        @SerializedName("wallet_balance")
        @Expose
        private Integer walletBalance;
        @SerializedName("discount_amount")
        @Expose
        private String discountAmount;

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
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

        public String getGrandTotal() {
            return grandTotal;
        }

        public void setGrandTotal(String grandTotal) {
            this.grandTotal = grandTotal;
        }

        public Integer getWalletBalance() {
            return walletBalance;
        }

        public void setWalletBalance(Integer walletBalance) {
            this.walletBalance = walletBalance;
        }

        public String getDiscountAmount() {
            return discountAmount;
        }

        public void setDiscountAmount(String discountAmount) {
            this.discountAmount = discountAmount;
        }

    }








    public class Item {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("chat_id")
        @Expose
        private Integer chatId;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("direction")
        @Expose
        private Integer direction;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("file_path")
        @Expose
        private String filePath;
        @SerializedName("price")
        @Expose
        private String price;
        @SerializedName("quantity")
        @Expose
        private String quantity;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("deleted_at")
        @Expose
        private Object deletedAt;
        @SerializedName("order_id")
        @Expose
        private Object orderId;
        @SerializedName("lat")
        @Expose
        private Object lat;
        @SerializedName("lang")
        @Expose
        private Object lang;
        @SerializedName("seen_at")
        @Expose
        private String seenAt;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getChatId() {
            return chatId;
        }

        public void setChatId(Integer chatId) {
            this.chatId = chatId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Integer getDirection() {
            return direction;
        }

        public void setDirection(Integer direction) {
            this.direction = direction;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Object getDeletedAt() {
            return deletedAt;
        }

        public void setDeletedAt(Object deletedAt) {
            this.deletedAt = deletedAt;
        }

        public Object getOrderId() {
            return orderId;
        }

        public void setOrderId(Object orderId) {
            this.orderId = orderId;
        }

        public Object getLat() {
            return lat;
        }

        public void setLat(Object lat) {
            this.lat = lat;
        }

        public Object getLang() {
            return lang;
        }

        public void setLang(Object lang) {
            this.lang = lang;
        }

        public String getSeenAt() {
            return seenAt;
        }

        public void setSeenAt(String seenAt) {
            this.seenAt = seenAt;
        }

    }










}
