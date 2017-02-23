package com.tcl.myapplication.action;

import android.content.Intent;

import com.tcl.myapplication.MainActivity;


public class MessageView {
	private MainActivity mActivity;

	public MessageView(MainActivity activity){
		mActivity=activity;
	}

	public void start(){
		Intent intent=new Intent();
		intent.setClassName("com.android.mms","com.android.mms.ui.ConversationList");
		mActivity.startActivity(intent);
	}
}
