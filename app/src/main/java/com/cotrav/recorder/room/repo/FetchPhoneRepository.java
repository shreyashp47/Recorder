package com.cotrav.recorder.room.repo;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cotrav.recorder.other.GsonStringConvertor;
import com.cotrav.recorder.room.PhoneNumAPI;
import com.cotrav.recorder.room.data.CallRoomDatabase;
import com.cotrav.recorder.room.data.ConfigRetrofit;
import com.cotrav.recorder.room.data.phoneno.PhoneNumbers;
import com.cotrav.recorder.room.data.phoneno.PhoneResponce;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class FetchPhoneRepository {
    Application application;
    SharedPreferences phoneSp;
    PhoneNumbers phoneNumbers;
    private LiveData<List<String>> phoneNoList;
    MutableLiveData<String> string;
    PhoneNumAPI phoneNumAPI;
    CallRoomDatabase callRoomDatabase;
    private LiveData<List<PhoneNumbers>> phoneNumberLiveList;


    public FetchPhoneRepository(Application application) {
        this.application = application;
        phoneNoList = new MutableLiveData<>();
        phoneNumberLiveList = new MutableLiveData<>();
        phoneNumAPI = ConfigRetrofit.configRetrofit(PhoneNumAPI.class);
        callRoomDatabase = CallRoomDatabase.getDatabase(application);
        string = new MutableLiveData<>();


        phoneSp = application.getSharedPreferences("all_phone_no", MODE_PRIVATE);
        if (!phoneSp.getString("all_phone_no", "n").equals("n")) {
            phoneNumberLiveList = callRoomDatabase.phoneNumbersDao().getAll();
            phoneNumbers = GsonStringConvertor.stringToGson(phoneSp.getString("all_phone_no", "n"), PhoneNumbers.class);

        }

    }


    public LiveData<String> getPhoneNoList() {
        phoneNumAPI.getPhoneNumbers().enqueue(new Callback<PhoneResponce>() {
            @Override
            public void onResponse(Call<PhoneResponce> call, Response<PhoneResponce> response) {
                if (response.isSuccessful()) {
                    if (!phoneSp.getString("all_phone_no", "n").equals(GsonStringConvertor.gsonToString(response.body()))) {
                        for (int i = 0; i < response.body().getResponse().size(); i++) {
                            callRoomDatabase.phoneNumbersDao().insert(response.body().getResponse().get(i));
                        }
                        SharedPreferences.Editor editor = phoneSp.edit();
                        editor.putString("all_phone_no", GsonStringConvertor.gsonToString(response.body()));
                        editor.commit();
                    }
                    string.setValue("OK");

                }
            }

            @Override
            public void onFailure(Call<PhoneResponce> call, Throwable t) {
                Log.d("error", t.getMessage());
                string.setValue("ERROR");
            }
        });

        return string;
    }


}
