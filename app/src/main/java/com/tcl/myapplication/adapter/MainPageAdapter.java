package com.tcl.myapplication.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.tcl.myapplication.R;
import com.tcl.myapplication.fragments.FragmentControl;
import com.tcl.myapplication.fragments.FragmentDaily;
import com.tcl.myapplication.fragments.FragmentEntertainment;
import com.tcl.myapplication.fragments.FragmentService;
import com.tcl.myapplication.util.DensityUtils;
import com.tcl.myapplication.view.ShadowAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lishiwei on 2017/3/14.
 */

public class MainPageAdapter extends FragmentPagerAdapter implements ShadowAdapter{
    Context mContext;
    List<Fragment> mFragmentList = new ArrayList<>();
    public MainPageAdapter(Context context,FragmentManager fm) {
        super(fm);
        mContext = context;
        mFragmentList.add(FragmentDaily.newInstance("",""));
        mFragmentList.add(FragmentControl.newInstance("",""));
        mFragmentList.add(FragmentEntertainment.newInstance("",""));
        mFragmentList.add(FragmentService.newInstance("",""));
    }

    @Override
    public float getBaseElevation() {
        return DensityUtils.dp2px(1);
    }

    @Override
    public Fragment getItem(int position) {

        return mFragmentList.get(position);
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        if (object != null) {
            return ((Fragment) object).getView() == view;
        } else {
            return false;
        }
    }
    @Override
    public View getViewAtPosition(int position){
        return mFragmentList.get(position).getView();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getStringArray(R.array.titles)[position];
    }
    @Override
    public int getCount() {
        return mContext.getResources().getStringArray(R.array.titles).length;
    }
}
