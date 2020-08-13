package com.cotrav.recorder.other;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonStringConvertor {

    public static Gson gson= new GsonBuilder().create();

    public static String gsonToString(Object obj){
        return gson.toJson(obj).toString();
    }

    public static <S> S stringToGson(String json,Class<S> sClass){
        return gson.fromJson(json,sClass);
    }
}
