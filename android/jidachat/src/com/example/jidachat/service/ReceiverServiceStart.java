package com.example.jidachat.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * @author Administrator
 * 
 * 职责：
 * 负责ServiceChat的开机自启动
 * 
 *
 */
public class ReceiverServiceStart extends BroadcastReceiver {
	

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent i=new Intent(context,ServiceChat.class);
		
		context.startService(i);

	}

}
