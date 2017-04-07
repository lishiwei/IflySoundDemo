package com.tcl.myapplication.view;


import android.view.View;

public interface ShadowAdapter {

    public final int MAX_ELEVATION_FACTOR = 20;

    float getBaseElevation();

    View getViewAtPosition(int position);

    int getCount();
}
