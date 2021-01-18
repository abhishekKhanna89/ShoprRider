package com.shopprdriver.Model.UserChatList;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("userchats")
    @Expose
    private List<Userchat> userchats = null;

    public List<Userchat> getUserchats() {
        return userchats;
    }

    public void setUserchats(List<Userchat> userchats) {
        this.userchats = userchats;
    }

}
