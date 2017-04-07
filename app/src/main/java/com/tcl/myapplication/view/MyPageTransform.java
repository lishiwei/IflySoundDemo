package com.tcl.myapplication.view;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tcl.myapplication.util.DensityUtils;

/**
 * Created by lishiwei on 2017/3/24.
 */
//通过OnPageChangeListener自定义一个PageTransform 参照ZoomInPageTransform
//  由于PageTransfrom借口的Evelation会闪烁
public class MyPageTransform implements ViewPager.OnPageChangeListener {
    private static final String TAG = MyPageTransform.class.getSimpleName();
    ViewPager mViewPager;
    private static float mDefaultScale = (float) 14 / (float) 15;
    private static float mMinEveluation = DensityUtils.dp2px(10);

    public MyPageTransform(ViewPager viewPager) {
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //此处从viewpager的pagetransform接口源码拷过来改的
        final int scrollX = mViewPager.getScrollX();
        final int childCount = mViewPager.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = mViewPager.getChildAt(i);
            final ViewPager.LayoutParams lp = (ViewPager.LayoutParams) child.getLayoutParams();
            if (lp.isDecor) continue;
            final float transformPos = (float) (child.getLeft() - scrollX) / getClientWidth();
            //此处参数为ZoomInPageTransform动画的方法
            child.setPivotY(child.getY() + child.getHeight());
            int diffWidth = 0;
            if (transformPos < -1) {  //[-Infinity,-1)
                child.setScaleX(mDefaultScale);
                child.setScaleY(mDefaultScale);
                child.setTranslationX(diffWidth);
            } else if (transformPos <= 0) {  //[-1,0]
                Log.d(TAG, "onPageScrolled: transformPos " + transformPos);
                child.setScaleX((float) 1 + transformPos / (float) 15);
                child.setScaleY((float) 1 + transformPos / (float) 15);
                child.setTranslationX((0 - transformPos) * diffWidth);
                animateEvelation(child, (1 + transformPos) * mMinEveluation);
            } else if (transformPos <= 1) { // (0,1]
                child.setScaleX((float) 1 - transformPos / (float) 15);
                child.setScaleY((float) 1 - transformPos / (float) 15);
                child.setTranslationX((0 - transformPos) * diffWidth);
                animateEvelation(child, (1 - transformPos) * mMinEveluation);
            } else { // (1,+Infinity]
                child.setScaleX(mDefaultScale);
                child.setScaleY(mDefaultScale);
                child.setTranslationX(-diffWidth);
            }


        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private int getClientWidth() {
        return mViewPager.getMeasuredWidth() - mViewPager.getPaddingLeft() - mViewPager.getPaddingRight();
    }

    private void animateEvelation(View view, float value) {

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
}
