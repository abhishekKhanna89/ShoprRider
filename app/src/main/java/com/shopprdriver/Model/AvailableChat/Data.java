package com.shopprdriver.Model.AvailableChat;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("userchats")
    @Expose
    private List<Userchat> userchats = null;
    @SerializedName("type")
    @Expose
    private String type;

    public List<Userchat> getUserchats() {
        return userchats;
    }

    public void setUserchats(List<Userchat> userchats) {
        this.userchats = userchats;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
