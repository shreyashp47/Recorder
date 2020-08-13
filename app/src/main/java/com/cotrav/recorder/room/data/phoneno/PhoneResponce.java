package com.cotrav.recorder.room.data.phoneno;


import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhoneResponce {

    @SerializedName("Response")
    @Expose
    private List<PhoneNumbers> response = null;

    public List<PhoneNumbers> getResponse() {
        return response;
    }

    public void setResponse(List<PhoneNumbers> response) {
        this.response = response;
    }


}
