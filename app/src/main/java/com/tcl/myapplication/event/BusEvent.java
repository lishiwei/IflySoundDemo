package com.tcl.myapplication.event;

/**
 * Created by lishiwei on 2017/3/29.
 */

public class BusEvent {
    public static int OPEN = 0;
    public static int CLOSE = 1;
    public static int SLEEP = 2;

    public BusEvent(int action, byte[] data) {
        this.action = action;
        this.data = data;
    }

    public int action = -1; ;
   public byte[] data;

}
