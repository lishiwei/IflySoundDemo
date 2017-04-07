package com.tcl.myapplication.Dragger.modules;

import android.support.v7.app.AppCompatActivity;

import com.tcl.myapplication.Dragger.PerActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lishiwei on 2017/3/31.
 */
@Module
public class ActivityModule {
    AppCompatActivity mAppCompatActivity;
    public ActivityModule(AppCompatActivity appCompatActivity) {
    mAppCompatActivity = appCompatActivity;
    }
    @PerActivity
    @Provides
    public AppCompatActivity providerActivity()
    {
        return mAppCompatActivity;
    }

}
