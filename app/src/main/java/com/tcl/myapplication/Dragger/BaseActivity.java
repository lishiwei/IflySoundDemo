package com.tcl.myapplication.Dragger;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.tcl.myapplication.Dragger.component.AppComponent;
import com.tcl.myapplication.Dragger.modules.ActivityModule;

/**
 * Created by lishiwei on 2017/3/31.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppComponent().inject(this);
    }

    public ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

    public AppComponent getAppComponent() {
        return ((App) getApplication()).getAppComponent();
    }
}
