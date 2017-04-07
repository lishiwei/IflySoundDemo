package com.tcl.myapplication.view;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.androidkun.xtablayout.XTabLayout;
import com.tcl.myapplication.R;
import com.tcl.myapplication.adapter.MainPageAdapter;
import com.tcl.myapplication.util.DensityUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AudioPlayerActivity extends FragmentActivity {

    private static final String TAG = AudioPlayerActivity.class.getSimpleName();

    @Bind(R.id.xTablayout)
    XTabLayout mIndicator;
    @Bind(R.id.main_pager)
    ViewPager mViewPager;

    FragmentPagerAdapter mFragmentPagerAdapter;
    @Bind(R.id.activity_audio_player)
    LinearLayout mActivityAudioPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        View decorView = getWindow().getDecorView();
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
        ButterKnife.bind(this);
        initViews();
        loadData();
    }

    private void loadData() {

    }

    private void initViews() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((DensityUtils.getScreenWidth() - DensityUtils.dp2px(60)), ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.topMargin = (int) getResources().getDimension(R.dimen.viewPage_marginTop);
        mViewPager.setLayoutParams(layoutParams);
        mFragmentPagerAdapter = new MainPageAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mIndicator.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(4);

        findViewById(R.id.activity_audio_player).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mViewPager.dispatchTouchEvent(event);
            }
        });

//切记去掉硬件加速
        mViewPager.setPageTransformer(false, new ZoomInPageTransform(), View.LAYER_TYPE_NONE);


//        MyPageTransform myPageTransform = new MyPageTransform(mViewPager);

//        ShadowTransformer shadowTransformer = new ShadowTransformer(mViewPager, (MainPageAdapter) mFragmentPagerAdapter,true);
//        mViewPager.addOnPageChangeListener(shadowTransformer);

    }
}
