package com.example.jidachat.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


/**
 * @author Administrator
 * 
 * 职责：
 * 接受activity发送过来的消息。
 * 并通过webClien发送到服务器。
 *
 */
public class ReceiverServiceController extends BroadcastReceiver {
	
	public final static String ACTION_NAME="com.example.jidachat.service.receivercontroller";

	public final static int DISCONNECT=-2;
	public final static int NONE=-1;
	public final static int LOGIN=0;
	public final static int CONNECT=1;
	public final static int SAY_TO_FRIEND=2;
	public final static int SAY_TO_GROUP=3;
	
	private WebClient webclient;
	

	public ReceiverServiceController(WebClient web) {
		super();
		// TODO Auto-generated constructor stub
		webclient=web;
	}




	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//switch
		//String ac=intent.getStringExtra("action");
		JSONObject jo;
		JSONArray ja;
		String content;
		int ac=intent.getIntExtra("action", ReceiverServiceController.NONE);
		switch (ac) {
		case LOGIN:
			jo=new JSONObject();
			ja=new JSONArray();
			String account=intent.getStringExtra("account_name");
			String password=intent.getStringExtra("account_password");
			try {
				//ja.put(3).put(4).put(6);
				//jo.put("type", "Connect").put("account_name", "b").put("account_id", "2").put("mtype", "yes").put("group_list", ja);
				jo.put("type", "Login").put("account_name", account).put("account_password", password);
				webclient.send(jo.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case CONNECT:
			
			break;
		case SAY_TO_FRIEND:
			jo=new JSONObject();
			String toAccountId=intent.getStringExtra("to_account_id");
			content=intent.getStringExtra("content");
			
			
			try {
				jo.put("type", "SayToFriend").put("to_account_id", toAccountId).put("content", content);
				webclient.send(jo.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
		case SAY_TO_GROUP:
			jo=new JSONObject();
			String groupId=intent.getStringExtra("group_id");
			content=intent.getStringExtra("content");
			
			try {
				jo.put("type", "SayToGroup").put("group_id", groupId).put("content", content);
				webclient.send(jo.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;

		default:
			break;
		}

	}

}
