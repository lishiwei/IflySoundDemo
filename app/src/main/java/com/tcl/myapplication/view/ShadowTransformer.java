package com.tcl.myapplication.view;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tcl.myapplication.adapter.MainPageAdapter;
import com.tcl.myapplication.util.DensityUtils;

public class ShadowTransformer implements ViewPager.OnPageChangeListener {
    private ViewPager viewPager;
    private MainPageAdapter cardAdapter;
    private float lastOffset;
    private boolean scalingEnabled;
    private static float mMinEveluation = DensityUtils.dp2px(20);
    private static String TAG = ShadowTransformer.class.getSimpleName();

    public ShadowTransformer(ViewPager viewPager, MainPageAdapter adapter, boolean enableScaling) {
        this.viewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
        cardAdapter = adapter;
        enableScaling(enableScaling);
    }

    private void enableScaling(boolean enable) {
        if (scalingEnabled && !enable) {
            // shrink main card
            View currentCard = cardAdapter.getViewAtPosition(viewPager.getCurrentItem());
            if (currentCard != null) {
                currentCard.animate().scaleY(1);
                currentCard.animate().scaleX(1);
            }
        } else if (!scalingEnabled && enable) {
            // grow main card
            View currentCard = cardAdapter.getViewAtPosition(viewPager.getCurrentItem());
            if (currentCard != null) {
                //enlarge the current item
                currentCard.animate().scaleY(1.1f);
                currentCard.animate().scaleX(1.1f);
            }
        }
        scalingEnabled = enable;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int realCurrentPosition;
        int nextPosition;
        float baseElevation = cardAdapter.getBaseElevation();
        float realOffset;
        boolean goingLeft = lastOffset > positionOffset;
        if (goingLeft) {
            realCurrentPosition = position + 1;
            nextPosition = position;
            realOffset = 1 - positionOffset;
        } else {
            nextPosition = position + 1;
            realCurrentPosition = position;
            realOffset = positionOffset;
        }
        if (nextPosition > cardAdapter.getCount() - 1
                || realCurrentPosition > cardAdapter.getCount() - 1) {
            return;
        }

        View currentCard = cardAdapter.getViewAtPosition(realCurrentPosition);
        if (currentCard != null) {
            if (scalingEnabled) {
                currentCard.setPivotY((currentCard.getTop() + currentCard.getBottom()) * 0.8f);
                currentCard.setScaleX((float) (1 + 0.1 * (1 - realOffset)));
                currentCard.setScaleY((float) (1 + 0.1 * (1 - realOffset)));
            }
        }
        View nextCard = cardAdapter.getViewAtPosition(nextPosition);
        if (nextCard != null) {
            if (scalingEnabled) {
                nextCard.setPivotY((nextCard.getTop() + nextCard.getBottom()) * 0.8f);
                nextCard.setScaleX((float) (1 + 0.1 * (realOffset)));
                nextCard.setScaleY((float) (1 + 0.1 * (realOffset)));
            }
        }
        lastOffset = positionOffset;
        animateEvelationFromViewPageSource();
    }
    private void animateEvelationFromViewPageSource() {
        //此处从viewpager的pagetransform接口源码拷过来改的
        final int scrollX = viewPager.getScrollX();
        final int childCount = viewPager.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = viewPager.getChildAt(i);
            final ViewPager.LayoutParams lp = (ViewPager.LayoutParams) child.getLayoutParams();

            if (lp.isDecor) continue;
            final float transformPos = (float) (child.getLeft() - scrollX) / getClientWidth();
            //此处参数为pagetransform动画的方法
            if (transformPos < -1) {

            } else if (transformPos <= 0) {
                animateEvelation1(child, (1 + transformPos) * mMinEveluation);
            } else if (transformPos <= 1) {
                animateEvelation1(child, (1 - transformPos) * mMinEveluation);
            } else {

            }
        }
    }

    private int getClientWidth() {
        return viewPager.getMeasuredWidth() - viewPager.getPaddingLeft() - viewPager.getPaddingRight();
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void animateEvelation1(View view, float value) {
        Log.d(TAG, "animateEvelation1: " + view.getClass());
        if (view.getClass() == RecyclerView.class) {
            RecyclerView recyclerView = (RecyclerView) view;
            for (int i = 0; i < recyclerView.getLayoutManager().getItemCount(); i++) {
                if (recyclerView.getLayoutManager().findViewByPosition(i) != null) {
                    ImageView imageview = (ImageView) ((LinearLayout) recyclerView.getLayoutManager().findViewByPosition(i)).getChildAt(0);
                    imageview.setElevation(value);

                }
            }
        }
    }

    private void animateEvelation(View view, float value) {

//        if (view.getClass()==RecyclerView.class)
//        {
//            RecyclerView recyclerView = (RecyclerView) view;
//            for (int i = 0; i < recyclerView.getLayoutManager().getItemCount(); i++) {
//                if (recyclerView.getLayoutManager().findViewByPosition(i) != null) {
//                    ImageView imageview = (ImageView) ((LinearLayout) recyclerView.getLayoutManager().findViewByPosition(i)).getChildAt(0);
//                    imageview.setElevation(value);
//
//                }
//            }
//        }
    }
}
