package com.tcl.myapplication.Dragger;

import android.app.Application;

import com.tcl.myapplication.Dragger.component.AppComponent;
import com.tcl.myapplication.Dragger.component.DaggerAppComponent;
import com.tcl.myapplication.Dragger.modules.AppModules;
import com.tcl.myapplication.util.ExecutorUtils;

import javax.inject.Inject;

/**
 * Created by lishiwei on 2017/2/27.
 */

public class App extends Application {
    AppComponent mAppComponent;
   static App mApp ;
    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        this.mAppComponent = DaggerAppComponent.builder().appModules(new AppModules(this)).build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    public  static App getApp() {
        return mApp;
    }
}
