package com.example.jidachat;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.jidachat.data.AccountInfo;
import com.example.jidachat.provider.ChatProvider;
import com.example.jidachat.service.ReceiverServiceController;
import com.example.jidachat.service.ServiceChat;
import com.example.jidachat.service.ServiceChatConnection;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class ActivityChat extends Activity implements ActivityInitable,OnClickListener{
	public final static int SAY_TO_FRIEND=0;
	public final static int SAY_TO_GROUP=1;
	public final static int NONE=-1;
	public final static int HANDLER_FRIEND=2;
	public final static int HANDLER_GROUP=3;
	private int index;
	private int remoteID;
	private int chatType;
	private AccountInfo acInfo;
	private AccountInfo remoteInfo;
	
	ReceiverActivity receiver;
	ServiceChatConnection conn=new ServiceChatConnection(this, this);
	
	Handler handler;
	
	TextView txtChat;
	EditText edtChat;
	Button btnSub;
	ScrollView scroll;
	InputMethodManager imm;
	
	ContentResolver resolver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		resolver=getContentResolver();
		
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 

		Intent intent=getIntent();
		index=intent.getIntExtra("index", NONE);
		chatType=intent.getIntExtra("type", NONE);
		if(index==NONE){
			this.finish();
			return;
		}
		
		receiver=ReceiverActivity.getInstance();

		scroll=(ScrollView) findViewById(R.id.scroll_chat);
		txtChat=(TextView) findViewById(R.id.txt_chat);
		edtChat=(EditText) findViewById(R.id.edt_chat);
		btnSub=(Button) findViewById(R.id.submit_chat);
		btnSub.setEnabled(false);
		
		edtChat.clearFocus();
//		imm.hideSoftInputFromWindow(edtChat.getWindowToken(),0);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		
		btnSub.setOnClickListener(this);
		
		
		handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				Cursor c;
				String name;
				String content;
				String res;
				String temp="";
				
				switch (msg.what) {
				case 1:

					Log.i("ActivityChat", msg.toString());
					try {
						JSONObject jso=new JSONObject((String)msg.obj);
						
						String type=new String(jso.getString("type"));
						int id=jso.getInt("from_account_id");
						if(type.equals(new String("saytofriend")) && chatType==SAY_TO_FRIEND && id==remoteInfo.getId()){
							content=jso.getString("content");
							name=jso.getString("from_account_name");
							txtChat.append(name+":\n\t"+content+"\n");
						}
						else if(type.equals(new String("saytogroup")) && chatType==SAY_TO_GROUP && id==remoteInfo.getId()){
							content=jso.getString("content");
							name=jso.getString("from_account_name");
							txtChat.append(name+":\n\t"+content+"\n");
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					break;
				case HANDLER_FRIEND:
					
					c=(Cursor)msg.obj;
					res="";
					while(c.moveToNext()){
						temp=c.getString(3)+":\n\t";
						temp+=c.getString(4)+"\n";
						res=temp+res;
					}
					txtChat.setText(res);
					
					break;
				case HANDLER_GROUP:
					
					c=(Cursor)msg.obj;
					res="";
					while(c.moveToNext()){
						temp=c.getString(4)+":\n\t";
						temp+=c.getString(5)+"\n";
						res=temp+res;
					}
					txtChat.setText(res);
					
					break;

				default:
					break;
				}
				scroll.fullScroll(ScrollView.FOCUS_DOWN);
			}
		};
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		Intent i=new Intent(this, ServiceChat.class);
		startService(i);
		bindService(i, conn, BIND_AUTO_CREATE);
		receiver.setHandler(handler);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		conn.disconnect();
		unbindService(conn);
		receiver.setHandler(null);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void Init(String logstr) {
		// TODO Auto-generated method stub
		
		acInfo=new AccountInfo(logstr);
		Cursor c=null;
		Message msg;
		switch (chatType) {
		case SAY_TO_FRIEND:
			remoteInfo=acInfo.getFriendList().get(index);
			c=ChatProvider.ResolverQueryRecord(resolver, ChatProvider.URI_PERSON_RECORD, acInfo.getId(), remoteInfo.getId(), -1, 0);
			msg=handler.obtainMessage(HANDLER_FRIEND,c);
			handler.sendMessage(msg);
			break;
		case SAY_TO_GROUP:
			remoteInfo=acInfo.getGroupList().get(index);
			c=ChatProvider.ResolverQueryRecord(resolver, ChatProvider.URI_GROUP_RECORD, acInfo.getId(), remoteInfo.getId(), -1, 0);
			msg=handler.obtainMessage(HANDLER_GROUP,c);
			handler.sendMessage(msg);
			break;

		default:
			break;
		}
		
		
		
		
		btnSub.setEnabled(true);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		String content=edtChat.getText().toString();
		if(content.length()==0)
			return;
		
		Intent i=new Intent(ReceiverServiceController.ACTION_NAME);
		
		String id=remoteInfo.getId()+"";
		
		switch (chatType) {
		case SAY_TO_FRIEND:
			i.putExtra("action", ReceiverServiceController.SAY_TO_FRIEND);
			i.putExtra("to_account_id", id);
			txtChat.append(acInfo.getName()+":\n\t"+content+"\n");
			ChatProvider.ResolverInsertPerson(resolver, acInfo.getId(), remoteInfo.getId(), acInfo.getName(), content, 1);
			break;
		case SAY_TO_GROUP:
			i.putExtra("action", ReceiverServiceController.SAY_TO_GROUP);
			i.putExtra("group_id", id);
			break;

		default:
			break;
		}
		i.putExtra("content", content);
		
		sendBroadcast(i);
		edtChat.setText("");
//		imm.hideSoftInputFromWindow(edtChat.getWindowToken(),0);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

}
