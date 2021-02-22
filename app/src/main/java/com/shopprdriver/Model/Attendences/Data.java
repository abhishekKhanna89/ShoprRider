package com.shopprdriver.Model.Attendences;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("attendences")
    @Expose
    private List<Attendence> attendences = null;

    public List<Attendence> getAttendences() {
        return attendences;
    }

    public void setAttendences(List<Attendence> attendences) {
        this.attendences = attendences;
    }

}
