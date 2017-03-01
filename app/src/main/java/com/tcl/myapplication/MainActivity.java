package com.tcl.myapplication;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.inputmethod.asr.vad.VadEngine;
import com.iflytek.util.IflyRecorder;
import com.iflytek.util.IflyRecorderListener;
import com.tcl.myapplication.action.CallAction;
import com.tcl.myapplication.action.CallView;
import com.tcl.myapplication.action.MessageView;
import com.tcl.myapplication.action.OpenAppAction;
import com.tcl.myapplication.action.OpenQA;
import com.tcl.myapplication.action.ScheduleCreate;
import com.tcl.myapplication.action.ScheduleView;
import com.tcl.myapplication.action.SearchAction;
import com.tcl.myapplication.action.SearchApp;
import com.tcl.myapplication.action.SearchWeather;
import com.tcl.myapplication.action.SendMessage;
import com.tcl.myapplication.bean.AnswerBean;
import com.tcl.myapplication.bean.DataBean;
import com.tcl.myapplication.bean.DatetimeBean;
import com.tcl.myapplication.bean.MainBean;
import com.tcl.myapplication.bean.ResultBean;
import com.tcl.myapplication.bean.SlotsBean;
import com.tcl.myapplication.listener.OnSerialPortDataListener;
import com.tcl.myapplication.util.JsonParserNew;
import com.tcl.myapplication.util.SerialManager;
import com.tcl.myapplication.view.WaveformView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ConcurrentLinkedQueue;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements IflyRecorderListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    VoiceWakeuper mVoiceWakeuper;
    Thread mMainThread;
    InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int i) {

        }
    };
    @Bind(R.id.WaveFormView)
    WaveformView mWaveFormView;
    @Bind(R.id.status_wake)
    TextView statusWake;
    @Bind(R.id.content_read)
    TextView contentRead;
    @Bind(R.id.sentiment_history)
    RecyclerView sentimentHistory;
    @Bind(R.id.btn_start)
    Button btnStart;
    @Bind(R.id.btn_pause)
    Button btnPause;
    @Bind(R.id.activity_main)
    LinearLayout activityMain;
    private boolean mIsWakeUp = false;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(final Message msg) {
            if (msg.what == MSG_GET_VOLUME) {
                mWaveFormView.updateAmplitude((Float.parseFloat(msg.obj + "")));
            }
            return true;
        }
    });
    WakeuperListener mWakeuperListener;
    SpeechSynthesizer mSpeechSynthesizer;
    SynthesizerListener mSynthesizerListener;
    SpeechRecognizer mSpeechRecognizer;
    TextUnderstander mTextUnderstander;
    TextUnderstanderListener mTextUnderstanderListener;
    private Toast mToast;

    /* 录音临时保存队列 */
    private ConcurrentLinkedQueue<byte[]> mRecordQueue = null;

    /* 端点检测引擎 */
    private VadEngine mVadengine = null;

    /* 是否写入数据 */
    private boolean mIsRunning = true;
    private PowerManager.WakeLock mWakeLock;
    /* 是否用户主动结束 */
    private boolean mIsUserEnd = false;
    public static final int MSG_GET_VOLUME = 0x1001;

    private final int SAMPLE_RATE = 16000;
    SentimentRecyclerAdapter mSentimentRecyclerAdapter;
    ArrayList<String> mHistory = new ArrayList<>();


    private MainBean mMainBean;//Json值
    public static String SRResult = "";    //识别结果
    public static boolean service_flag = false;//表示是否在一项服务中
    private final static String ACTIVESOUND = "AlfredActiveSound.wav";
    private final static String INACTIVESOUND = "AlfredInactiveSound.wav";
    OnSerialPortDataListener mOnSerialPortDataListener = new OnSerialPortDataListener() {
        @Override
        public void OnNewData(byte[] data) {
            for (int i = 0; i < data.length; i++) {
                if (BuildConfig.DEBUG) Log.d(TAG, "data[i]:" + data[i]);
            }
            if (data[0] == 1) {
                showTips("唤醒成功!");
                playActiveSound();
                statusWake.setText("您的阿福助手已经唤醒成功");
                statusWake.setBackgroundColor(Color.parseColor("#00ff00"));
                mWaveFormView.setmWaveColor(Color.parseColor("#00ff00"));
                mVoiceWakeuper.stopListening();
                mIsWakeUp = true;
            }
            if (data[0] == 4) {

                showTips("休眠成功!");
                statusWake.setBackgroundColor(Color.parseColor("#ffffff"));
                statusWake.setText("已睡眠");
            }
        }

        @Override
        public void OnError(Exception e) {

        }
    };

    private void showTips(final String s) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, s + "MainActivity", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID + "=5896c658");
        mVoiceWakeuper = VoiceWakeuper.createWakeuper(MainActivity.this, null);
        mSpeechRecognizer = SpeechRecognizer.createRecognizer(MainActivity.this, mInitListener);
        findViewById(R.id.sleep).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SerialManager.getInstance(MainActivity.this).sleep();
            }
        });
        initSpeechSynthesizerParam();
        setSpeechRecognizerParam();
        setTextUnderstandParam();
        initContinuousSpeechRecognized();
        initUI();
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(this);  //传入参数必须为Activity，否则AlertDialog将不显示。
        SerialManager.getInstance(this).refreshDeviceList(mOnSerialPortDataListener);

    }


    private void initUI() {
        mSentimentRecyclerAdapter = new SentimentRecyclerAdapter(this, mHistory);
        sentimentHistory.setLayoutManager(new LinearLayoutManager(this));
        sentimentHistory.setAdapter(mSentimentRecyclerAdapter);
    }

    RecognizerListener mRecognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            updateVolume(i);
        }

        @Override
        public void onBeginOfSpeech() {
            if (BuildConfig.DEBUG) Log.d(TAG, "听写开始");
        }

        @Override
        public void onEndOfSpeech() {
            updateVolume(0);
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            String result = JsonParser.parseIatResult(recognizerResult.getResultString());
            if (BuildConfig.DEBUG)
                Log.d(TAG, result);
            contentRead.append(result);
            mTextUnderstander.understandText(result, mTextUnderstanderListener);
        }

        @Override
        public void onError(SpeechError speechError) {
            if (speechError.getErrorCode() == 10118) {
                statusWake.setBackgroundColor(Color.parseColor("#ffffff"));
                statusWake.setText("已睡眠");
                //报错10118 检测到未说话，从新开始一次

                Toast.makeText(MainActivity.this, "未听到您说话。", Toast.LENGTH_SHORT).show();
                playInactiveSound();
//                initWakeUpParam();


            }

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    private void initContinuousSpeechRecognized() {
        initRecorder();
        mVadengine = VadEngine.getInstance();
        mVadengine.reset();
        mRecordQueue = new ConcurrentLinkedQueue<byte[]>();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                "wakeLock");
    }

    // 初始化录音机
    private void initRecorder() {
        // 对三星手机的优化
        if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            IflyRecorder.getInstance().initRecoder(SAMPLE_RATE,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    MediaRecorder.AudioSource.VOICE_RECOGNITION);
        } else {
            IflyRecorder.getInstance().initRecoder(SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_FRONT,
                    AudioFormat.ENCODING_PCM_16BIT,
                    MediaRecorder.AudioSource.MIC);
        }
    }


    private void showTip(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
    }

    private void initSpeechSynthesizerParam() {
        mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(this, mInitListener);
        mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, "50");
        mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, "80");
        mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        mSynthesizerListener = new SynthesizerListener() {
            @Override
            public void onSpeakBegin() {
                stopRecognized();
            }

            @Override
            public void onBufferProgress(int i, int i1, int i2, String s) {

            }

            @Override
            public void onSpeakPaused() {
                startRecognized();
            }

            @Override
            public void onSpeakResumed() {
                stopRecognized();
            }

            @Override
            public void onSpeakProgress(int i, int i1, int i2) {

            }

            @Override
            public void onCompleted(SpeechError speechError) {
                startRecognized();

            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        };
    }

    public void updateVolume(int volume) {
        Message message = Message.obtain();
        message.what = MSG_GET_VOLUME;
        message.obj = volume / 5;
        handler.sendMessage(message);
    }

    private void setSpeechRecognizerParam() {
        mSpeechRecognizer.setParameter(SpeechConstant.DOMAIN, "iat");
        mSpeechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mSpeechRecognizer.setParameter(SpeechConstant.ACCENT, "mandarin");
        mSpeechRecognizer.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mSpeechRecognizer.setParameter(SpeechConstant.VAD_BOS, "5000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mSpeechRecognizer.setParameter(SpeechConstant.VAD_EOS, "5000");

    }

    private void setTextUnderstandParam() {
        mTextUnderstanderListener = new TextUnderstanderListener() {
            @Override
            public void onResult(UnderstanderResult result) {
                if (null != result) {
                    Log.d(TAG, "语义分析结果为" + result.getResultString());
                    // 显示
                    String text = result.getResultString();

                    mMainBean = JsonParserNew.parseIatResult(text);


                    if (!TextUtils.isEmpty(text)) {

                        if (mMainBean.getRc() == 0) {
                            SRResult = mMainBean.getText();
                            judgeService();
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(result.getResultString());
                                String content = jsonObject.getString("text");
                                mHistory.add(content);
                                mSentimentRecyclerAdapter.notifyDataSetChanged();
                                speakAnswer(content);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    speakAnswer("语义识别失败！");
                    mHistory.add("语义识别失败");
                    mSentimentRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(SpeechError speechError) {
                speakAnswer("语义识别失败！");
            }
        };
        mTextUnderstander = TextUnderstander.createTextUnderstander(this, mInitListener);
    }

    private void playActiveSound() {
        MediaPlayer mActiveMediaPlayer = new MediaPlayer();
        if (mActiveMediaPlayer.isPlaying()) {
            mActiveMediaPlayer.reset();
        }
        try {
            AssetFileDescriptor mActiveSoundFileDescriptor = getAssets().openFd(ACTIVESOUND);
            mActiveMediaPlayer.setDataSource(mActiveSoundFileDescriptor.getFileDescriptor(), mActiveSoundFileDescriptor.getStartOffset(), mActiveSoundFileDescriptor.getLength());
            mActiveMediaPlayer.prepare();
            mActiveMediaPlayer.start();
            mActiveMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    startRecognized();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playInactiveSound() {
        MediaPlayer mInactiveMediaPlayer = new MediaPlayer();
        if (mInactiveMediaPlayer.isPlaying()) {
            mInactiveMediaPlayer.reset();

        }
        try {
            AssetFileDescriptor mInActiveSoundFileDescriptor = getAssets().openFd(INACTIVESOUND);
            mInactiveMediaPlayer.setDataSource(mInActiveSoundFileDescriptor.getFileDescriptor(), mInActiveSoundFileDescriptor.getStartOffset(), mInActiveSoundFileDescriptor.getLength());
            mInactiveMediaPlayer.prepare();
            mInactiveMediaPlayer.start();
            mInactiveMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopRecognized();
                    SerialManager.getInstance(MainActivity.this).sleep();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void initWakeUpParam() {
//        if (mVoiceWakeuper != null) {
//            if (mVoiceWakeuper.isListening()) {
//                mVoiceWakeuper.stopListening();
//                mVoiceWakeuper.cancel();
//
//                mVoiceWakeuper = null;
//            }
//        }
//        if (mWakeuperListener != null) {
//            mWakeuperListener = null;
//        }
//        mVoiceWakeuper = VoiceWakeuper.createWakeuper(MainActivity.this, null);
//        //1.加载唤醒词资源，resPath为唤醒资源路径
//        StringBuffer param = new StringBuffer();
//        String resPath = ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "ivw/5896c658.jet");
//        param.append(ResourceUtil.IVW_RES_PATH + "=" + resPath);
//        param.append("," + ResourceUtil.ENGINE_START + "=" + SpeechConstant.ENG_IVW);
//        SpeechUtility.getUtility().setParameter(ResourceUtil.ENGINE_START, param.toString());
////2.创建VoiceWakeuper对象
//
////3.设置唤醒参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
////唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
//        mVoiceWakeuper.setParameter(SpeechConstant.IVW_THRESHOLD, "0:" + 50);
////设置当前业务类型为唤醒
//        mVoiceWakeuper.setParameter(SpeechConstant.IVW_SST, "wakeup");
////设置唤醒一直保持，直到调用stopListening，传入0则完成一次唤醒后，会话立即结束（默认0）
//        mVoiceWakeuper.setParameter(SpeechConstant.KEEP_ALIVE, "1");
//
//        mVoiceWakeuper.setParameter(SpeechConstant.IVW_SST, "oneshot");
//        mVoiceWakeuper.setParameter(SpeechConstant.ENGINE_TYPE, "cloud");
//
//        mWakeuperListener = new WakeuperListener() {
//            @Override
//            public void onBeginOfSpeech() {
//                if (BuildConfig.DEBUG) Log.d(TAG, "唤醒开始");
//            }
//
//            @Override
//            public void onResult(WakeuperResult wakeuperResult) {
//                playActiveSound();
//                statusWake.setText("您的阿福助手已经唤醒成功");
//                statusWake.setBackgroundColor(Color.parseColor("#00ff00"));
//                mWaveFormView.setmWaveColor(Color.parseColor("#00ff00"));
//
//                mVoiceWakeuper.stopListening();
//                mIsWakeUp = true;
//            }
//
//            @Override
//            public void onError(SpeechError speechError) {
//                if (BuildConfig.DEBUG) Log.d(TAG, "唤醒失败" + speechError.getErrorDescription());
//
//                mIsWakeUp = false;
//            }
//
//            @Override
//            public void onEvent(int i, int i1, int i2, Bundle bundle) {
//
//            }
//
//            @Override
//            public void onVolumeChanged(int i) {
//
//            }
//        };
//        mVoiceWakeuper.startListening(mWakeuperListener);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        mWakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWakeLock.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSpeechRecognizer.cancel();
        mSpeechRecognizer.destroy();
        SerialManager.getInstance(this).onDestory();
    }

    private void startRecognized() {
        mRecordQueue.clear();
        // 重置启动标志位
        setIsRunning(true);
        mIsUserEnd = false;

        // 开始录音
        IflyRecorder.getInstance().initRecoder(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_FRONT,
                AudioFormat.ENCODING_PCM_16BIT,
                MediaRecorder.AudioSource.MIC);
        IflyRecorder.getInstance().startRecoder(this);
        if (mSpeechRecognizer.isListening()) {
            mSpeechRecognizer.stopListening();
        }
        mSpeechRecognizer.startListening(mRecognizerListener);
        if (mMainThread == null) {
            mMainThread = new Thread(myRunner);
        }

        new Thread(myRunner).start();

    }

    private void stopRecognized() {
        mIsUserEnd = true;
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.destroy();
            mSpeechRecognizer.cancel();
        }
        IflyRecorder.getInstance().stopRecorder();


    }


    private void judgeService() {

        SRResult = null;
        String service = mMainBean.getService();
        String operation = mMainBean.getOperation();

        AnswerBean answerBean = new AnswerBean();
        SlotsBean slotsBean = new SlotsBean();
        DatetimeBean datetimeBean = new DatetimeBean();
        ResultBean resultBean = new ResultBean();
        DataBean dataBean = new DataBean();

        String date = "该天";

        if (mMainBean.getAnswer() != null) {
            answerBean = mMainBean.getAnswer();
        }


        if (mMainBean.getSemantic() != null) {
            if (mMainBean.getSemantic().getSlots() != null) {
                slotsBean = mMainBean.getSemantic().getSlots();
                if (mMainBean.getSemantic().getSlots().getDatetime() != null) {
                    datetimeBean = mMainBean.getSemantic().getSlots().getDatetime();
                }
            }
        }


        if (mMainBean.getData() != null) {
            if (mMainBean.getData().getResult() != null) {
                if (mMainBean.getSemantic().getSlots().getDatetime() != null) {
                    Calendar calendar = Calendar.getInstance();
                    int today = calendar.get(Calendar.DAY_OF_MONTH);
                    dataBean = mMainBean.getData();
                    String day = datetimeBean.getDate().substring(datetimeBean.getDate().length() - 2, datetimeBean.getDate().length());
                    if (day.equals("AY")) {
                        day = today + "";
                    }
                    int getday = Integer.parseInt(day);
                    int sub = getday - today;
                    resultBean = dataBean.getResult().get(sub);

                    if (sub == 0) {
                        date = "今天";
                    } else if (sub == 1) {
                        date = "明天";
                    } else if (sub == 2) {
                        date = "后天";
                    } else if (sub == 3) {
                        date = "大后天";
                    } else if (sub == 4) {
                        date = "四天后";
                    } else if (sub == 5) {
                        date = "五天后";
                    } else if (sub == 6) {
                        date = "六天后";
                    }
                }
            }

        }

        if (!service_flag) {//如果不在一项服务中才进行服务的判断
            switch (service) {
                case "tvControl":
                case "airControl":

                    if (operation.equals("CONTROL") || operation.equals("OPEN")) {
                        String text = "设置" + mMainBean.getMoreResults().get(0).getSemantic().getSlots().getAttr() + "成功!";
                        speakAnswer(text);
                        mHistory.add(text);
                        mSentimentRecyclerAdapter.notifyDataSetChanged();
                    }
                    break;
                case "airControl_smartHome":
                case "fan_smartHome":
                case "light_smartHome":
                case "freezer_smartHome":
                case "switch_smartHome":
                case "tv_smartHome":
                    String text = "操作" + mMainBean.getSemantic().getSlots().getAttr() + "成功!";
                    speakAnswer(text);
                    mHistory.add(text);
                    mSentimentRecyclerAdapter.notifyDataSetChanged();
                    break;
                case "TCL_TV":
                    if (operation.equals("turnChannel")) {
                        String message = "调台" + mMainBean.getSemantic().getSlots().getChannel() + "成功!";
                        speakAnswer(message);
                        mHistory.add(message);
                        mSentimentRecyclerAdapter.notifyDataSetChanged();
                    }
                    break;
                case "telephone":
                    switch (operation) {
                        case "CALL": {    //1打电话
//                        必要条件【电话号码code】
//                        可选条件【人名name】【类型category】【号码归属地location】【运营商operator】【号段head_num】【尾号tail_num】
//                        可由多个可选条件确定必要条件
                            mHistory.add("打电话给" + slotsBean.getName());
                            mSentimentRecyclerAdapter.notifyDataSetChanged();
                            CallAction callAction = new CallAction(slotsBean.getName(), slotsBean.getCode(), MainActivity.this);//目前可根据名字或电话号码拨打电话
                            callAction.start();
                            break;
                        }
                        case "VIEW": {    //2查看电话拨打记录
//                        必要条件无
//                        可选条件【未接电话】【已拨电话】【已接电话】
                            mHistory.add("查看电话拨打记录");
                            mSentimentRecyclerAdapter.notifyDataSetChanged();
                            CallView callview = new CallView(this);
                            callview.start();
                            break;
                        }
                        default:
                            break;
                    }

                    break;
                case "message": {//2 短信相关服务

                    switch (operation) {

                        case "SEND": {//1发送短信
                            mHistory.add("发短信给" + slotsBean.getName());
                            mSentimentRecyclerAdapter.notifyDataSetChanged();
                            SendMessage sendMessage = new SendMessage(slotsBean.getName(), slotsBean.getCode(), slotsBean.getContent(), MainActivity.this);
                            sendMessage.start();
                            break;
                        }

                        case "VIEW": {//2查看发送短信页面
                            mHistory.add("查看短信页面");
                            mSentimentRecyclerAdapter.notifyDataSetChanged();
                            MessageView messageView = new MessageView(this);
                            messageView.start();
                            break;
                        }

                        default:
                            break;
                    }

                    break;
                }
                case "app": {//3 应用相关服务

                    switch (operation) {

                        case "LAUNCH": {//1打开应用
                            mHistory.add("打开应用" + slotsBean.getName());
                            mSentimentRecyclerAdapter.notifyDataSetChanged();
                            OpenAppAction openApp = new OpenAppAction(slotsBean.getName(), MainActivity.this);
                            openApp.start();
                            break;
                        }

                        case "QUERY": {//2应用中心搜索应用
                            mHistory.add("搜索应用" + slotsBean.getName());
                            mSentimentRecyclerAdapter.notifyDataSetChanged();

                            SearchApp searchApp = new SearchApp(slotsBean.getName(), this);
                            searchApp.start();
                            break;
                        }

                        default:
                            break;

                    }
                    break;
                }

                case "websearch": {//5 搜索相关服务

                    switch (operation) {

                        case "QUERY": {//1搜索
                            mHistory.add("搜索" + slotsBean.getName());
                            mSentimentRecyclerAdapter.notifyDataSetChanged();
                            SearchAction searchAction = new SearchAction(slotsBean.getKeywords(), MainActivity.this);
                            searchAction.Search();
                            break;
                        }

                        default:
                            break;

                    }

                    break;
                }

                case "faq": {//6 社区问答相关服务

                    switch (operation) {
                        case "ANSWER": {//1社区问答
                            OpenQA openQA = new OpenQA(answerBean.getText(), this);

                            openQA.start();
                            mHistory.add(answerBean.getText());
                            mSentimentRecyclerAdapter.notifyDataSetChanged();
                            break;
                        }
                        default:
                            break;
                    }

                    break;

                }

                case "chat": {//7 聊天相关服务

                    switch (operation) {

                        case "ANSWER": {//1聊天模式

                            OpenQA openQA = new OpenQA(answerBean.getText(), this);

                            mHistory.add(answerBean.getText());
                            mSentimentRecyclerAdapter.notifyDataSetChanged();
                            openQA.start();
                            break;
                        }

                        default:
                            break;
                    }

                    break;
                }

                case "openQA": {//8 智能问答相关服务

                    switch (operation) {

                        case "ANSWER": {//1智能问答

                            OpenQA openQA = new OpenQA(answerBean.getText(), this);
                            mHistory.add(answerBean.getText());
                            mSentimentRecyclerAdapter.notifyDataSetChanged();
                            openQA.start();

                            break;
                        }

                        default:
                            break;
                    }

                    break;
                }

                case "baike": {//9 百科知识相关服务

                    switch (operation) {

                        case "ANSWER": {//1百科

                            OpenQA openQA = new OpenQA(answerBean.getText(), this);
                            mHistory.add(answerBean.getText());
                            mSentimentRecyclerAdapter.notifyDataSetChanged();
                            openQA.start();

                            break;
                        }

                        default:
                            break;
                    }

                    break;
                }

                case "schedule": {//10 日程相关服务

                    switch (operation) {

                        case "CREATE": {//1创建日程/闹钟(直接跳转相应设置界面)

                            ScheduleCreate scheduleCreate = new ScheduleCreate(slotsBean.getName(), datetimeBean.getTime(), datetimeBean.getDate(), slotsBean.getContent(), this);
                            scheduleCreate.start();

                            break;
                        }

                        case "VIEW": {//1查看闹钟/日历(未实现)

                            ScheduleView scheduleView = new ScheduleView(slotsBean.getName(), datetimeBean.getTime(), datetimeBean.getDate(), slotsBean.getContent(), this);
                            scheduleView.start();
                            break;
                        }


                        default:
                            break;
                    }

                    break;
                }

                case "weather": {//11 天气相关服务

                    switch (operation) {

                        case "QUERY": {//1查询天气

                            SearchWeather searchWeather = new SearchWeather(date, resultBean.getCity(), resultBean.getSourceName(), resultBean.getDate(), resultBean.getWeather(), resultBean.getTempRange(), resultBean.getAirQuality(), resultBean.getWind(), resultBean.getHumidity(), resultBean.getWindLevel() + "", this);
                            mHistory.add(resultBean.getCity() + date + "的天气情况为" + resultBean.getWeather() + ",气温范围" + resultBean.getTempRange() + ",风向以及风力情况为" + resultBean.getWind() + "。");
                            mSentimentRecyclerAdapter.notifyDataSetChanged();
                            searchWeather.start();

                            break;
                        }

                        default:
                            break;

                    }

                    break;
                }

                default:
//                    mUnderstanderText.setText("我听不懂您说什么，亲爱的，下次可能我就明白了");
//                    speakAnswer("我听不懂您说什么，亲爱的，下次可能我就明白了");
                    break;
            }
        }

    }

    public void speakAnswer(String text) {

        int code = mSpeechSynthesizer.startSpeaking(text, mSynthesizerListener);
        if (code != ErrorCode.SUCCESS) {

            Toast.makeText(this, "语音合成失败,错误码: ", Toast.LENGTH_SHORT).show();
        }
    }

    protected synchronized void setIsRunning(boolean trueOrfalse) {
        mIsRunning = trueOrfalse;
    }

    protected synchronized boolean getIsRunning() {

        return mIsRunning;
    }

    @Override
    public void OnReceiveBytes(byte[] data, int length) {
        if (length > 0) {
            byte[] temp = new byte[length];
            System.arraycopy(data, 0, temp, 0, length);
            if (data == null || data.length == 0)
                return;
            // 不断的填充数据
//			Log.d(TAG, "get----data");
            mRecordQueue.add(temp);
        }
    }

    int volume;
    private Runnable myRunner = new Runnable() {
        public void run() {
            while (!mIsUserEnd) { // 条件是否用户主动结束
                if (getIsRunning()) {
                    byte[] data = mRecordQueue.poll();
                    if (data == null) {

                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    volume = mVadengine.vadCheck(data, data.length);
                    if (volume >= 0) {
                        Log.d(TAG, "no----volume");
                        mSpeechRecognizer.writeAudio(data, 0, data.length);
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // 检测到端点
                    else {
                        Log.d(TAG, "no----checked");
                        // 重置引擎、停止监听等待返回结果
                        mVadengine.reset();
                        mSpeechRecognizer.stopListening();
                        setIsRunning(false);
                    }
                } else {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    @OnClick({R.id.btn_start, R.id.btn_pause, R.id.btn_stop})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                mSpeechSynthesizer.resumeSpeaking();
                break;
            case R.id.btn_pause:
                if (mSpeechSynthesizer.isSpeaking()) {
                    mSpeechSynthesizer.pauseSpeaking();
                }
                break;
            case R.id.btn_stop:
                if (mSpeechSynthesizer.isSpeaking()) {
                    mSpeechSynthesizer.stopSpeaking();
                }
                break;
        }
    }


}
