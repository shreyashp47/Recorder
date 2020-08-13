package com.cotrav.recorder.room.repo;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.cotrav.recorder.room.repo.FetchPhoneRepository;

import java.util.List;

/**
 * Created by Baliram on 11/09/19.
 */

public class AllPhoneNoViewModel extends AndroidViewModel {

    private LiveData<List<String>> phoneNoList;
    private LiveData<String> string;
    private FetchPhoneRepository fetchPhoneRepository;

    public AllPhoneNoViewModel(Application application) {
        super(application);

        fetchPhoneRepository=new FetchPhoneRepository(this.getApplication());
    }



    public LiveData<String> getPhoneNoList() {
        string = fetchPhoneRepository.getPhoneNoList();
        return string;
    }
}
