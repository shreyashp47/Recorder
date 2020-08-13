package com.cotrav.recorder.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.cotrav.recorder.room.data.OfflineDatabase;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface CallDao {

    @Insert
    void addRecording(OfflineDatabase list);

    @Insert(onConflict = REPLACE)
    void insert(OfflineDatabase task);



    @Query("SELECT * FROM recording")
    LiveData<List<OfflineDatabase>> getAll();

    @Query("SELECT * FROM recording WHERE date= :date")
    OfflineDatabase getRecord(String date);


    @Delete
    void delete(OfflineDatabase task);

    @Query("UPDATE recording SET uploaded='yes' WHERE date = :date")
    void update(String date);


    @Query("SELECT * from recording ")
    LiveData<List<OfflineDatabase>> getLocalTaxiBookings();

    @Query("SELECT * FROM recording WHERE callHangTime = '1' ")
    LiveData<List<OfflineDatabase>> getTodaysDBTaxiBookings();


}