package com.tcl.myapplication.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.tcl.myapplication.event.BusEvent;
import com.tcl.myapplication.listener.OnSerialPortDataListener;
import com.tcl.myapplication.util.SerialManager;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by lishiwei on 2017/3/28.
 */

public class SerialService extends Service {
    private static final String TAG = SerialService.class.getSimpleName();
    OnSerialPortDataListener mOnSerialPortDataListener = new OnSerialPortDataListener() {
        @Override
        public void OnNewData(byte[] data) {
            if (data[0] == 1 && data[1] == 1) {
                EventBus.getDefault().post(new BusEvent(BusEvent.OPEN,data));
            }
            else if (data[0] == 4)
            {
                EventBus.getDefault().post(new BusEvent(BusEvent.CLOSE,data));
            }
        }

        @Override
        public void OnError(Exception e) {

        }
    };

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SerialManager.getInstance(this).refreshDeviceList(mOnSerialPortDataListener);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SerialManager.OPENACTION);
        intentFilter.addAction(SerialManager.CLOSEACTION);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {

            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        }, new Action() {
            @Override
            public void run() throws Exception {

            }
        });
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        SerialManager.getInstance(this).onDestory();
        super.onDestroy();
    }

    @Override
    public ComponentName startService(Intent service) {
        return super.startService(service);
    }

    public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(SerialManager.CLOSEACTION)) {
                SerialManager.getInstance(context).sleep();
            }
        }
    };
}
