package com.tcl.myapplication.Dragger.component;


import android.app.Application;
import android.support.v7.app.AppCompatActivity;

import com.tcl.myapplication.Dragger.BaseActivity;
import com.tcl.myapplication.Dragger.BaseActivityComponent;
import com.tcl.myapplication.Dragger.modules.AppModules;
import com.tcl.myapplication.util.ExecutorUtils;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by lishiwei on 2017/3/13.
 */
@Singleton
@Component(modules = AppModules.class)
public interface AppComponent extends BaseActivityComponent {
    void inject(BaseActivity baseActivity);
    ExecutorUtils getExecutors();
}
