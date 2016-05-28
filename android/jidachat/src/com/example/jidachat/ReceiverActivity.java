package com.example.jidachat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.jidachat.service.ReceiverServiceController;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

/**
 * @author Administrator
 * 
 * 
 * 单例模式
 * 职责：
 * 将service发送过来的消息分发给不同的activity
 * 由于和activity同线程，使用策略模式，通过设置不同的handler实现。
 *
 */
public class ReceiverActivity extends BroadcastReceiver {
	
	public final static String ACTION_NAME="com.example.jidachat.receiveractivity";

	public final static int DISCONNECT=-2;
	public final static int NONE=-1;
	public final static int LOGIN_SUCCEED=0;
	public final static int CONNECT=1;
	public final static int SAY_TO_FRIEND=2;
	public final static int SAY_TO_GROUP=3;
	
	private Handler handler=null;
	private boolean hadRegister=false;

	public void setHandler(Handler handler) {
		synchronized (this) {
			this.handler = handler;
		}
	}




//	public boolean isHadRegister() {
//		synchronized (this) {
//			return hadRegister;
//		}
//	}


	public void setHadRegister(boolean hadRegister) {
		synchronized (this) {
			this.hadRegister = hadRegister;
		}
	}




	private static class ReciverHolder{
		static ReceiverActivity instance=new ReceiverActivity();
	}
	
	static ReceiverActivity getInstance(){
		return ReciverHolder.instance;
	}
	

	private ReceiverActivity() {
		super();
		// TODO Auto-generated constructor stub
	}




	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		int ac=intent.getIntExtra("action", NONE);
		String jsoStr=intent.getStringExtra("json");
		switch (ac) {
		case LOGIN_SUCCEED:
			
			break;
		case CONNECT:
			
			break;
		case SAY_TO_FRIEND:
			synchronized (this) {
				if(handler!=null){
					Message msg=handler.obtainMessage(1, jsoStr);
					handler.sendMessage(msg);
				}
			}
			
			break;
		case SAY_TO_GROUP:
			synchronized (this) {
				if(handler!=null){
					Message msg=handler.obtainMessage(1, jsoStr);
					handler.sendMessage(msg);
				}
			}
			break;

		default:
			break;
		}

	}

}
