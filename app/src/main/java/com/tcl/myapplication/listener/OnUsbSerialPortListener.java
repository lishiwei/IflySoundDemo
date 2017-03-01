package com.tcl.myapplication.listener;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.util.List;

/**
 * Created by TCL SHBC-02 on 2017/2/24.
 */

public interface OnUsbSerialPortListener {
    public void onSucceed(UsbSerialPort usbSerialPort);
    public void onError();
}
