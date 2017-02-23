package com.tcl.myapplication.action;

import android.content.Intent;

import com.tcl.myapplication.MainActivity;


public class CallView {

    private MainActivity mActivity;

    public CallView(MainActivity activity) {
        mActivity = activity;
    }

    public void start() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL_BUTTON);
        mActivity.startActivity(intent);
    }
}
