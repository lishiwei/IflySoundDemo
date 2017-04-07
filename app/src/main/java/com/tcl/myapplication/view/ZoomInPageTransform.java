package com.tcl.myapplication.view;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tcl.myapplication.util.DensityUtils;


/**
 * Created by lishiwei on 2017/3/16.
 */

public class ZoomInPageTransform implements ViewPager.PageTransformer {
    private static float mDefaultScale = (float) 14 / (float) 15;
    private static float mMinEveluation = DensityUtils.dp2px(20);
    private static String TAG = ZoomInPageTransform.class.getSimpleName();

    @Override
    public void transformPage(View view, float position) {
        view.setPivotY(view.getY() + view.getHeight());
        int diffWidth = 0;
        if (position < -1) {  //[-Infinity,-1)
            view.setScaleX(mDefaultScale);
            view.setScaleY(mDefaultScale);
            view.setTranslationX(diffWidth);
        } else if (position <= 0) {  //[-1,0]
            view.setScaleX((float) 1 + position / (float) 15);
            view.setScaleY((float) 1 + position / (float) 15);
            view.setTranslationX((0 - position) * diffWidth);
            animateEvelation(view, (1 + position) * mMinEveluation);
                log(view);
        }

        else if (position <= 1) { // (0,1]
            view.setScaleX((float) 1 - position / (float) 15);
            view.setScaleY((float) 1 - position / (float) 15);
            view.setTranslationX((0 - position) * diffWidth);
            animateEvelation(view, (1 - position) * mMinEveluation);
            log(view);
        } else { // (1,+Infinity]
            view.setScaleX(mDefaultScale);
            view.setScaleY(mDefaultScale);
            view.setTranslationX(-diffWidth);

        }

    }

    private void log(View view) {
//        if (view.getClass().equals(LinearLayout.class)) {
//            RecyclerView recyclerView = (RecyclerView) ((LinearLayout) view).getChildAt(0);
//            for (int i = 0; i < recyclerView.getLayoutManager().getItemCount(); i++) {
//                if (recyclerView.getLayoutManager().findViewByPosition(i) != null) {
//                    ImageView imageview = (ImageView) ((LinearLayout) recyclerView.getLayoutManager().findViewByPosition(i)).getChildAt(0);
//                    Log.d(TAG, "log: " + imageview.getElevation());
//
//                }
//            }
//        }
    }

    private void animateEvelationleft(View view, float value) {

        if (view.getClass().equals(LinearLayout.class)) {
            RecyclerView recyclerView = (RecyclerView) ((LinearLayout) view).getChildAt(0);
            for (int i = 0; i < recyclerView.getLayoutManager().getItemCount(); i++) {
                if (recyclerView.getLayoutManager().findViewByPosition(i) != null) {
                    if (i%2 == 0)
                    {
                        ImageView imageview = (ImageView) ((LinearLayout) recyclerView.getLayoutManager().findViewByPosition(i)).getChildAt(0);
                        imageview.setElevation(value);
                    }
                }
            }
        }
    }
    private void animateEvelation(View view, float value) {
        Log.d(TAG, "animateEvelation: "+view.getClass().getSimpleName());
        if (view.getClass().equals(RecyclerView.class)) {
            RecyclerView recyclerView = (RecyclerView) view;
                for (int i = 0; i < recyclerView.getLayoutManager().getItemCount(); i++) {
                    if (recyclerView.getLayoutManager().findViewByPosition(i) != null) {
                        ImageView imageview = (ImageView) ((LinearLayout) recyclerView.getLayoutManager().findViewByPosition(i)).getChildAt(0);
                        imageview.setElevation(value);

                    }
                }
        }
    }
}

