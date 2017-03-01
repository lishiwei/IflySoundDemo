package com.tcl.myapplication.listener;

/**
 * Created by TCL SHBC-02 on 2017/2/27.
 */

public interface OnSerialPortDataListener {
    public void OnNewData(byte[] data);
    public void OnError(Exception e);
}
