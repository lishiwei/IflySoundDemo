package com.tcl.myapplication.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import com.tcl.myapplication.Dragger.App;

/**
 * Created by lishiwei on 2017/3/15.
 */

public class DensityUtils {


    /**
     * dp转px
     *
     * @param
     * @param
     * @return
     */
    public static int dp2px( float dpVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, App.getApp().getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param
     * @param
     * @return
     */
    public static int sp2px( float spVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, App.getApp().getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     *
     * @param
     * @param pxVal
     * @return
     */
    public static float px2dp( float pxVal)
    {
        final float scale = App.getApp().getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     *
     * @param
     * @param pxVal
     * @return
     */
    public static float px2sp( float pxVal)
    {
        return (pxVal / App.getApp().getResources().getDisplayMetrics().scaledDensity);
    }
    public static int getScreenWidth()
    {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) App.getApp().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }
}
