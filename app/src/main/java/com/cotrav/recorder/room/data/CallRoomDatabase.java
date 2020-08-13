package com.cotrav.recorder.room.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;

import com.cotrav.recorder.room.CallDao;
import com.cotrav.recorder.room.PhoneNumbersDao;
import com.cotrav.recorder.room.data.phoneno.PhoneNumbers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {OfflineDatabase.class, PhoneNumbers.class}, version = 7, exportSchema = false)
public abstract class CallRoomDatabase extends androidx.room.RoomDatabase {
    public abstract CallDao daoRecord();
    public abstract PhoneNumbersDao phoneNumbersDao();

    private static volatile CallRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static CallRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CallRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    CallRoomDatabase.class, "record_database")
                                    .fallbackToDestructiveMigration()
                                    .allowMainThreadQueries()
                                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
