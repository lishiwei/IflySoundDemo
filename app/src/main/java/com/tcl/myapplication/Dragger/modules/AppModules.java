package com.tcl.myapplication.Dragger.modules;

import android.content.Context;

import com.tcl.myapplication.Dragger.App;
import com.tcl.myapplication.util.ExecutorUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lishiwei on 2017/3/13.
 */


@Module
public class AppModules {
    private final App mApp;

    public AppModules(App app)
    {
        this.mApp = app;
    }
    @Provides
    @Singleton
    public ExecutorUtils providerExecutor(ExecutorUtils executorUtils) {
        return executorUtils;
    }
    @Provides
    @Singleton
    Context providerApplicationContext()
    {
        return mApp;
    }
}
