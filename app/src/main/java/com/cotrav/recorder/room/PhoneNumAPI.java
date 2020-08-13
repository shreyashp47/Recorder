package com.cotrav.recorder.room;

import com.cotrav.recorder.room.data.phoneno.PhoneResponce;
import com.cotrav.recorder.room.data.upload.UploadResponse;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PhoneNumAPI {


    @GET("http://novuslogic.in/AAH/PHP-Slim-Restful/api/getLeads")
    Call<PhoneResponce> getPhoneNumbers();

    @POST("http://novuslogic.in/AAH/PHP-Slim-Restful/api/insertCall")
    Call<UploadResponse> upload(@Body JsonObject locationPost);


}
