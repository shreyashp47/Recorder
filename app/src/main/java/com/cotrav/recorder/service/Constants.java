package com.cotrav.recorder.service;

public  class Constants {
    public static final String TAG = "Call recorder";

    public static final String FILE_NAME_PATTERN = "^[\\d]{14}_[_\\d]*\\..+$";

    public static final int STATE_INCOMING_NUMBER = 1;
    public static final int STATE_CALL_START = 2;
    public static final int STATE_CALL_END = 3;
    public static final int RECORDING_ENABLED = 4;
    public static final int RECORDING_DISABLED = 5;
}