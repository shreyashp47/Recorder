package com.cotrav.recorder.room.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recording")
public class OfflineDatabase {

    /* @PrimaryKey(autoGenerate = true)
     @NonNull
     @ColumnInfo(name = "id")
     private int id;*/

    @ColumnInfo(name = "phoneNo")
    private String phoneNo;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "callPickUpTime")
    private String callPickUpTime;
    @ColumnInfo(name = "callState")
    private String callState;

    @ColumnInfo(name = "callHangTime")
    private String callHangTime;
    @ColumnInfo(name = "callDuration")
    private String callDuration;

    @ColumnInfo(name = "incoming")
    private String incoming;

    @ColumnInfo(name = "outgoing")
    private String outgoing;
    @ColumnInfo(name = "uploaded")
    private String uploaded;


    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getCallPickUpTime() {
        return callPickUpTime;
    }

    public void setCallPickUpTime(String callPickUpTime) {
        this.callPickUpTime = callPickUpTime;
    }

    public String getCallHangTime() {
        return callHangTime;
    }

    public void setCallHangTime(String callHangTime) {
        this.callHangTime = callHangTime;
    }


    public String getIncoming() {
        return incoming;
    }

    public void setIncoming(String incoming) {
        this.incoming = incoming;
    }

    public String getOutgoing() {
        return outgoing;
    }

    public void setOutgoing(String outgoing) {
        this.outgoing = outgoing;
    }


    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

/*    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }*/

    public String getUploaded() {
        return uploaded;
    }

    public void setUploaded(String uploaded) {
        this.uploaded = uploaded;
    }

    public String getCallState() {
        return callState;
    }

    public void setCallState(String callState) {
        this.callState = callState;
    }
}
