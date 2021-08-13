package com.shopprdriver.Model;

public class MultipleProduct {

    private String productName;
    private String productImage;
    private String productQty;
    private String productPrice;
    private boolean isSaved = false;

    public MultipleProduct(String productName, String productImage, String productQty, String productPrice, boolean isSaved) {
        this.productName = productName;
        this.productImage = productImage;
        this.productQty = productQty;
        this.productPrice = productPrice;
        this.isSaved = isSaved;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductQty() {
        return productQty;
    }

    public void setProductQty(String productQty) {
        this.productQty = productQty;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }
}