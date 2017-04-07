package com.tcl.myapplication.Dragger.component;

import android.support.v7.app.AppCompatActivity;

import com.tcl.myapplication.Dragger.PerActivity;
import com.tcl.myapplication.Dragger.modules.ActivityModule;

import dagger.Component;

/**
 * Created by lishiwei on 2017/3/31.
 */
@PerActivity
@Component(dependencies = AppComponent.class,modules = ActivityModule.class)
public interface ActivityComponent {
    AppCompatActivity getActivity();
}
