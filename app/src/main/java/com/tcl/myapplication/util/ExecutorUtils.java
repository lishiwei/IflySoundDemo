package com.tcl.myapplication.util;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ExecutorUtils {

    private ExecutorService mExecutor;
    @Inject
    private ExecutorUtils() {
        mExecutor = Executors.newFixedThreadPool(5);
    }


    public void submitRunnable(Runnable runnable) {
        mExecutor.submit(runnable);
    }

}