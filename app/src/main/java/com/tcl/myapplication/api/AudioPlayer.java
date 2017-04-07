/*
 *  COPYRIGHT NOTICE  
 *  Copyright (C) 2016, Jhuster <lujun.hust@gmail.com>
 *  https://github.com/Jhuster/AudioDemo
 *   
 *  @license under the Apache License, Version 2.0 
 *
 *  @file    AudioPlayer.java
 *  
 *  @version 1.0     
 *  @author  Jhuster
 *  @date    2016/03/19
 */
package com.tcl.myapplication.api;

import android.content.res.AssetManager;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.tcl.myapplication.util.ExecutorUtils;

import java.io.IOException;

public class AudioPlayer {

    private static final String TAG = "AudioPlayer";
    private static final int DEFAULT_STREAM_TYPE = AudioManager.STREAM_MUSIC;
    private static final int DEFAULT_SAMPLE_RATE = 8000;
    private static final int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_STEREO;
    private static final int DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int DEFAULT_PLAY_MODE = AudioTrack.MODE_STREAM;

    private boolean mIsTrackInited = false;
    private int mMinBufferSize = 0;
    private AudioTrack mAudioTrack;
    private AudioDeviceInfo mAudioDeviceInfo;
    private WavFileReader mWavFileReader;

    public boolean initAudioTrack() {
        return initAudioTrack(DEFAULT_STREAM_TYPE, DEFAULT_SAMPLE_RATE, DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT);
    }

    public boolean initAudioTrack(int streamType, int sampleRateInHz, int channelConfig, int audioFormat) {

        if (mIsTrackInited) {
            Log.e(TAG, "Player already started !");
            return false;
        }

        mMinBufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        if (mMinBufferSize == AudioTrack.ERROR_BAD_VALUE) {
            Log.e(TAG, "Invalid parameter !");
            return false;
        }
        Log.d(TAG, "getMinBufferSize = " + mMinBufferSize + " bytes !");

        mAudioTrack = new AudioTrack(streamType, sampleRateInHz, channelConfig, audioFormat, mMinBufferSize, DEFAULT_PLAY_MODE);
        if (mAudioTrack.getState() == AudioTrack.STATE_UNINITIALIZED) {
            Log.e(TAG, "AudioTrack initialize fail !");
            return false;
        }

        mIsTrackInited = true;

        Log.d(TAG, "Start audio player success !");

        return mIsTrackInited;
    }

    public int getMinBufferSize() {
        return mMinBufferSize;
    }

    public void stopPlayer() {

        if (!mIsTrackInited) {
            return;
        }

        if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            mAudioTrack.stop();
        }

        mAudioTrack.release();
        mIsTrackInited = false;

        Log.d(TAG, "Stop audio player success !");
    }

    public boolean play(byte[] audioData, int offsetInBytes, int sizeInBytes) {

        if (!mIsTrackInited) {
            Log.e(TAG, "Player not started !");
            return false;
        }

        if (sizeInBytes < mMinBufferSize) {
            Log.e(TAG, "audio data is not enough !");
            return false;
        }

        if (mAudioTrack.write(audioData, offsetInBytes, sizeInBytes) != sizeInBytes) {
            Log.e(TAG, "Could not write all the samples to the audio device !");
        }
        if (mAudioDeviceInfo!=null&&mAudioTrack.getPreferredDevice() != mAudioDeviceInfo) {

            mAudioTrack.setPreferredDevice(mAudioDeviceInfo);
        }
        try {
            mAudioTrack.play();
        } catch (IllegalStateException exception) {
            exception.printStackTrace();
        }

        Log.d(TAG, "OK, Played " + sizeInBytes + " bytes !");

        return true;
    }

    public void startPlayAsyn(AssetManager assetManager, String FileName, AudioDeviceInfo audioDeviceInfo) {
        mAudioDeviceInfo = audioDeviceInfo;
        mWavFileReader = new WavFileReader();
        try {
            mWavFileReader.openFile(assetManager.open(FileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        initAudioTrack();
//        new Thread(mAudioPlayRunnable).start();
        ExecutorUtils.getInstance().submitRunnable(mAudioPlayRunnable);
    }
    public void startPlayAsyn( String filePath, AudioDeviceInfo audioDeviceInfo) {
        if (audioDeviceInfo != null)
        {
            mAudioDeviceInfo = audioDeviceInfo;

        }
        mWavFileReader = new WavFileReader();
        try {
            mWavFileReader.openFile(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initAudioTrack();
//        new Thread(mAudioPlayRunnable).start();
        ExecutorUtils.getInstance().submitRunnable(mAudioPlayRunnable);
    }

    private Runnable mAudioPlayRunnable = new Runnable() {

        @Override
        public void run() {
            byte[] buffer = new byte[getMinBufferSize()];
            while (mWavFileReader.readData(buffer, 0, buffer.length) > 0) {
                play(buffer, 0, buffer.length);
            }
            stopPlayer();
            try {
                mWavFileReader.closeFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
