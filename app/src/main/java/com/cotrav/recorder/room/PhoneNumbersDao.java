package com.cotrav.recorder.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.cotrav.recorder.room.data.OfflineDatabase;
import com.cotrav.recorder.room.data.phoneno.PhoneNumbers;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PhoneNumbersDao {

    @Insert
    void addPhoneNumbers(PhoneNumbers list);

    @Insert(onConflict = REPLACE)
    void insert(PhoneNumbers task);



    @Query("SELECT * FROM phoneNumber")
    LiveData<List<PhoneNumbers>> getAll();

    @Query("SELECT * FROM phoneNumber")
    List<PhoneNumbers> getAllPhoneNo();

    @Query("SELECT * FROM recording WHERE date= :date")
    OfflineDatabase getRecord(String date);


    @Delete
    void delete(PhoneNumbers task);

    @Update
    void update(PhoneNumbers task);


    @Query("SELECT * from phoneNumber ")
    LiveData<List<PhoneNumbers>> getLocalTaxiBookings();



}