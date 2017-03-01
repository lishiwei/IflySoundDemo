package com.tcl.myapplication.util;

import android.content.Context;
import android.widget.Toast;

import com.tcl.myapplication.MainActivity;

/**
 * Created by lishiwei on 2017/3/1.
 */

public class UsbSerialResponseUtils {
    Context mContext;

    public UsbSerialResponseUtils(Context mContext) {
        this.mContext = mContext;
    }

    private void showTips( final String s)
    {
        ((MainActivity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, s +"MainActivity", Toast.LENGTH_SHORT).show();
            }
        });
    }
   public static void switchResponse(byte[] bytes){
       switch (bytes[0])
       {
           case 1:

               break;
           case 2:
               break;
           case 3:
               break;
           case 4:
               break;
       }
   }
}
