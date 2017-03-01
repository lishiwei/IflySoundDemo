package com.tcl.myapplication.util;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.tcl.myapplication.BuildConfig;
import com.tcl.myapplication.listener.OnSerialPortDataListener;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SerialManager {
    private static SerialManager instance;
    private ProgressDialog mProgressDialog;
    private UsbManager mUsbManager;
    private static String TAG = SerialManager.class.getSimpleName();
    private Context mContext;
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;
    private UsbSerialPort mUsbSerialPort;
    private OnSerialPortDataListener mOnSerialPortDataListener;
    private PendingIntent mPermissionIntent;
    UsbDevice mUsbDevice;
    private SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener() {
        @Override
        public void onNewData(byte[] data) {
            mOnSerialPortDataListener.OnNewData(data);
        }

        @Override
        public void onRunError(Exception e) {
            Log.d(TAG, "onRunError: " + e.toString());
            mOnSerialPortDataListener.OnError(e);
        }
    };

    private SerialManager(Context context) {
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        if (mContext == null) {
            mContext = context;
        }
        mProgressDialog = new ProgressDialog(mContext);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        mContext.registerReceiver(mUsbPermissionActionReceiver, filter);
        mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
    }


    public void sleep() {
        if (mUsbSerialPort == null) {
            Log.d(TAG, "sleep:mUsbSerialPort null ");
            return;
        }
        try {
            byte[] a = {3};
            int i = 0;
            i = mUsbSerialPort.write(a, 10000);
            Log.d(TAG, "sleep: " + i);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SerialManager getInstance(Context context) {
        if (instance == null) {
            instance = new SerialManager(context);
        }
        return instance;
    }

    public void refreshDeviceList(OnSerialPortDataListener onSerialPortDataListener) {
        if (mOnSerialPortDataListener == null) {
            mOnSerialPortDataListener = onSerialPortDataListener;
        }
        new AsyncTask<Void, Void, List<UsbSerialPort>>() {
            @Override
            protected List<UsbSerialPort> doInBackground(Void... params) {
                final List<UsbSerialDriver> drivers =
                        UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);

                final List<UsbSerialPort> result = new ArrayList<UsbSerialPort>();
                for (final UsbSerialDriver driver : drivers) {
                    final List<UsbSerialPort> ports = driver.getPorts();
                    Log.d(TAG, String.format("+ %s: %s port%s",
                            driver, Integer.valueOf(ports.size()), ports.size() == 1 ? "" : "s"));
                    result.addAll(ports);
                }

                return result;
            }

            @Override
            protected void onPostExecute(List<UsbSerialPort> result) {
                if (0 >= result.size()) {
                    return;
                }
                mUsbSerialPort = result.get(0);

                Toast.makeText(mContext, result.get(0).toString(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, result.toString());
                tryGetUsbPermission();
            }
        }.execute((Void) null);
    }


    public void onDestory() {
        stopIoManager();
        if (mUsbSerialPort != null) {
            try {
                mUsbSerialPort.close();
            } catch (IOException e) {
                // Ignore.
            }
            mUsbSerialPort = null;
        }
        if (mListener != null) {
            mListener = null;
        }
        if (mUsbDevice != null) {
            mUsbDevice = null;
        }
        if (mExecutor != null && mExecutor.isShutdown()) {
            mExecutor.shutdown();
            mExecutor = null;
        }
        if (mOnSerialPortDataListener != null) {
            mOnSerialPortDataListener = null;
        }
        if (mUsbManager != null) {
            mUsbManager = null;
        }
        if (mProgressDialog != null) {
            mProgressDialog = null;
        }
        mContext.unregisterReceiver(mUsbPermissionActionReceiver);
    }

    private void tryGetUsbPermission() {

        //here do emulation to ask all connected usb device for permission
        for (final UsbDevice usbDevice : mUsbManager.getDeviceList().values()) {
            //add some conditional check if necessary
            //if(isWeCaredUsbDevice(usbDevice)){
            mUsbDevice = usbDevice;
            if (mUsbManager.hasPermission(usbDevice)) {
                //if has already got permission, just goto connect it
                //that means: user has choose yes for your previously popup window asking for grant perssion for this usb device
                //and also choose option: not ask again
                afterGetUsbPermission(usbDevice);
            } else {
                //this line will let android popup window, ask user whether to allow this app to have permission to operate this usb device
                mUsbManager.requestPermission(usbDevice, mPermissionIntent);
            }
        }
    }


    private void afterGetUsbPermission(UsbDevice usbDevice) {
        //call method to set up device communication
        Toast.makeText(mContext, String.valueOf("Found USB device: VID=" + usbDevice.getVendorId() + " PID=" + usbDevice.getProductId()), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "afterGetUsbPermission: ");
        doYourOpenUsbDevice(usbDevice);
    }

    private void doYourOpenUsbDevice(UsbDevice usbDevice) {
        //now follow line will NOT show: User has not given permission to device UsbDevice
//        UsbDeviceConnection connection = mUsbManager.openDevice(usbDevice);
        initUsbSerialPort(usbDevice);
    }

    public UsbSerialPort getUsbSerial(OnSerialPortDataListener onSerialPortDataListener) {


        Flowable.fromIterable(UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager)).map(new Function<UsbSerialDriver, List<UsbSerialPort>>() {
            @Override
            public List<UsbSerialPort> apply(UsbSerialDriver usbSerialDriver) throws Exception {
                Toast.makeText(mContext, "usbSerialDriver.getPorts().size()" + usbSerialDriver.getPorts().size(), Toast.LENGTH_SHORT).show();
                List<UsbSerialPort> usbSerialPortList = new ArrayList<UsbSerialPort>();
                usbSerialPortList.addAll(usbSerialDriver.getPorts());
                return usbSerialPortList;
            }
        }).map(new Function<List<UsbSerialPort>, UsbSerialPort>() {
            @Override
            public UsbSerialPort apply(List<UsbSerialPort> usbSerialPortList) throws Exception {
                Toast.makeText(mContext, "usbSerialPortList.size()" + usbSerialPortList.size(), Toast.LENGTH_SHORT).show();
                if (usbSerialPortList.size() > 0) {
                    return usbSerialPortList.get(0);
                }
                return null;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<UsbSerialPort>() {
            @Override
            public void onSubscribe(Subscription s) {
                if (!((AppCompatActivity) mContext).isFinishing()) {
                }
//                {mProgressDialog.show();}
            }

            @Override
            public void onNext(UsbSerialPort usbSerialPorts) {
                Toast.makeText(mContext, "找到设备" + usbSerialPorts.toString(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
                mUsbSerialPort = usbSerialPorts;
//                initUsbSerialPort();

            }

            @Override
            public void onError(Throwable t) {
                Log.d(TAG, "onError: ");
                Toast.makeText(mContext, "未找到设备" + t.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
                mProgressDialog.dismiss();
                Toast.makeText(mContext, "未找到设备", Toast.LENGTH_SHORT).show();
            }
        });
        return null;
    }

    public void initUsbSerialPort(UsbDevice usbDevice) {
        UsbDeviceConnection connection = null;
        try {
            connection = mUsbManager.openDevice(usbDevice);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d(TAG, e.getMessage());
        }
        Log.d(TAG, "initUsbSerialPort: " + connection.toString());
        if (mUsbSerialPort == null) {
            return;
        }
        try {
            Log.d(TAG, "initUsbSerialPort: mUsbSerialPort" + mUsbSerialPort.toString() + "connection" + connection.toString());
            mUsbSerialPort.open(connection);
            mUsbSerialPort.setDTR(true);
            mUsbSerialPort.setRTS(true);
            mUsbSerialPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        } catch (IOException e) {
            Log.d(TAG, "initUsbSerialPort: Error opening device:" + e.toString());
            Toast.makeText(mContext, "Error opening device: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            try {
                mUsbSerialPort.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            mUsbSerialPort = null;
            return;
        }
        onDeviceStateChange();
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (mUsbSerialPort != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(mUsbSerialPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
        ((AppCompatActivity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "串口初始化成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";


    private final BroadcastReceiver mUsbPermissionActionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        //user choose YES for your previously popup window asking for grant perssion for this usb device
                        if (null != usbDevice) {
                            afterGetUsbPermission(usbDevice);
                        }
                    } else {
                        //user choose NO for your previously popup window asking for grant perssion for this usb device
                        Toast.makeText(context, String.valueOf("Permission denied for device" + usbDevice), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

}