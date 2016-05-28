package com.example.jidachat.service;

import com.example.jidachat.ActivityFinishable;
import com.example.jidachat.ActivityInitable;
import com.example.jidachat.ActivityLogin;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author Administrator
 * 
 * 职责：
 * Activity与Service的连接类。由于使用了AIDL这里将其封装起来。
 *
 */
public class ServiceChatConnection implements ServiceConnection {

	ServiceChatAidl mBinder;

	ActivityInitable acInit;

	Activity ac;
	
	boolean shouldAutoDecrease=false;

	public ServiceChatConnection(Activity ac, ActivityInitable acInit) {
		super();
		this.acInit = acInit;
		this.ac = ac;
	}
	
	public void disconnect(){
		synchronized (this) {

			if(mBinder==null){
				shouldAutoDecrease=true;
			}
			else{
				try {
					mBinder.decrease();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		synchronized (this) {
		
		mBinder = ServiceChatAidl.Stub.asInterface(service);
		try {
			mBinder.increase();

			if(shouldAutoDecrease){
				mBinder.decrease();
			}

			String loginStr = mBinder.getLoginInfo();

			if (loginStr.length() == 0) {
//				mBinder.decrease();
				// ActivityList.this.unbindService(conn);

				Intent i = new Intent(ac, ActivityLogin.class);
				ac.startActivity(i);
				// ActivityList.this.finish();
				ac.finish();
				return;
			}
			
			// acInfo=new AccountInfo(loginStr);
			acInit.Init(loginStr);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub

//		try {
//			mBinder.decrease();
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		mBinder = null;
		Log.i("Connection", "onServiceDisconnected");
	}

}
