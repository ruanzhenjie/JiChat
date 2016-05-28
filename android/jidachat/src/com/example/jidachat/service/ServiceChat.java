package com.example.jidachat.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.java_websocket.drafts.Draft_10;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.jidachat.ActivityFinishable;
import com.example.jidachat.ActivityInitable;
import com.example.jidachat.ActivityListFriend;
import com.example.jidachat.ActivityLogin;
import com.example.jidachat.R;
import com.example.jidachat.ReceiverActivity;
import com.example.jidachat.data.AccountInfo;
import com.example.jidachat.provider.ChatProvider;
import com.example.jidachat.service.WebClient.SocketType;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Administrator
 * 
 * 职责：
 * 注册ReceiverserviceController.
 * 存储用户信息。
 * 分发接受到的信息给activity和notification（notification留了接口，还没去实现）
 * 通过ondestroy发送Intent给自己实现粗糙的常驻（以后会使用守护进程）
 * 
 *
 */
public class ServiceChat extends Service {

	final static String LOG_SERVICE = "Servicehat";

	NotificationManager nm = null;
	WebClient webClient;
	Handler mhandler;

	ReceiverServiceController mReceiver;

	String loginStr = "";
	AccountInfo acInfo = null;

	ContentResolver resolver;

	private int numActivity = 0;

	ServiceChatAidl.Stub serviceBinder = new ServiceChatAidl.Stub() {

		@Override
		public void increase() throws RemoteException {
			// TODO Auto-generated method stub
			numActivity++;
		}

		@Override
		public void decrease() throws RemoteException {
			// TODO Auto-generated method stub
			numActivity--;
		}

		@Override
		public String getLoginInfo() throws RemoteException {
			// TODO Auto-generated method stub
			return loginStr;
		}
	};;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return serviceBinder;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		CreateWebClient();

		CreateBroadcast();

		resolver = getContentResolver();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		// int i;
		// Toast.makeText(this, "ServiceChat start", Toast.LENGTH_LONG).show();
		/*
		 * Notification nti=new Notification();
		 * nti.flags|=Notification.FLAG_AUTO_CANCEL;
		 * nti.icon=R.drawable.ic_launcher; nti.tickerText="hello world";
		 * nti.when=System.currentTimeMillis(); nti.setLatestEventInfo(this,
		 * "hi", "nihao", null);
		 * 
		 * nm.notify(0, nti);
		 */

		/*
		 * Intent mintent=new Intent(this,ActivityLogin.class); PendingIntent
		 * pd=PendingIntent.getActivity(this, 0, mintent,
		 * PendingIntent.FLAG_UPDATE_CURRENT); Notification.Builder mbuilder=new
		 * Notification
		 * .Builder(this).setSmallIcon(R.drawable.ic_launcher).setContentText
		 * ("contenttext").setContentTitle("contentitle").setContentIntent(pd);
		 * mbuilder.setAutoCancel(true);
		 * 
		 * 
		 * 
		 * mbuilder.setTicker("ticker"); //Notification nti=mbuilder.
		 * 
		 * nm.notify(0, mbuilder.getNotification());
		 * 
		 * 
		 * for(i=0;i<10;i++){ Log.i(ServiceChat.LOG_SERVICE, i+""); try {
		 * Thread.sleep(1000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } }
		 */
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Intent i = new Intent(this, ReceiverServiceStart.class);
		// sendBroadcast(i);
	}

	private void CreateWebClient() {
		mhandler = new InfoHandler();
		try {
			webClient = new WebClient(mhandler);
			// c = new ExampleClient( new URI( "Websocket://119.29.108.252:7272"
			// ), new Draft_10() ,mhandler);
			webClient.connect();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void CreateBroadcast() {

		IntentFilter filter = new IntentFilter(
				ReceiverServiceController.ACTION_NAME);
		mReceiver = new ReceiverServiceController(webClient);
		registerReceiver(mReceiver, filter);

	}

	public enum SocketType

	{

		SAYTOFRIEND, SAYTOGROUP, SAY, PING, LOGIN, LOGOUT, NOVALUE, DISCONNECT, CONNECTSUCCEED, CONNECTFAIL, LOGINSUCCEED, LOGINFAIL;

		public static SocketType toType(String str)

		{

			try {

				return valueOf(str);

			}

			catch (Exception ex) {

				return NOVALUE;

			}

		}

	}

	private void StoreInfo(JSONObject jso, SocketType type) {
		Log.i("ServiceChat", "StoreInfo");
		String content;
		int remoteAccount;
		String remoteName;
		try {
			remoteAccount = jso.getInt("from_account_id");
			content = jso.getString("content");
			remoteName=jso.getString("from_account_name");

			if (type == SocketType.SAYTOFRIEND) {
				if (acInfo != null) {
					ChatProvider.ResolverInsertPerson(resolver, acInfo.getId(),
							remoteAccount,remoteName, content, 0);
					if(numActivity==0){
						ChatProvider.ResolverUpdate(resolver, ChatProvider.URI_PERSON_REMAIN, ChatProvider.REMAIN_ADD, acInfo.getId(), remoteAccount);
					}
				}
			} else if (type == SocketType.SAYTOGROUP) {
				int group_id=jso.getInt("group_id");
				if (acInfo != null) {
					ChatProvider.ResolverInsertGroup(resolver, acInfo.getId(), group_id, remoteAccount,remoteName, content, 0);
					if(numActivity==0){
						ChatProvider.ResolverUpdate(resolver, ChatProvider.URI_GROUP_REMAIN, ChatProvider.REMAIN_ADD, acInfo.getId(), group_id);
					}
				}

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void DistributeInfo(JSONObject jso, SocketType type) {
		Log.i("ServiceChat", "DistributeInfo" + "\tnumActivity=" + numActivity);

		if (numActivity == 0) {
			SendToNoticefycation(jso, type);
		} else {
			SendToActivity(jso, type);
		}
	}

	private void SendToActivity(JSONObject jso, SocketType type) {

		Intent i = new Intent(ReceiverActivity.ACTION_NAME);
		if (type == SocketType.SAYTOFRIEND) {
			i.putExtra("action", ReceiverActivity.SAY_TO_FRIEND);
		} else if (type == SocketType.SAYTOGROUP) {
			i.putExtra("action", ReceiverActivity.SAY_TO_GROUP);
		}

		i.putExtra("json", jso.toString());

		sendBroadcast(i);
		Log.i("ServiceChat", "SendToActivity");
	}

	private void SendToNoticefycation(JSONObject jso, SocketType type) {

		Log.i("ServiceChat", "SendToNoticefycation");
	}

	class InfoHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			String message = (String) msg.obj;
			Intent i;

			try {
				JSONObject jso = new JSONObject(message);

				switch (SocketType.toType(jso.getString("type").toUpperCase())) {
				case PING:
					webClient.send("{\"type\":\"pong\"}");
					break;
				case SAYTOFRIEND:
					StoreInfo(jso, SocketType.SAYTOFRIEND);

					DistributeInfo(jso, SocketType.SAYTOFRIEND);

					// System.out.println("resolve json content: "+jso.getString("content"));
					break;
				case SAYTOGROUP:
					StoreInfo(jso, SocketType.SAYTOGROUP);

					DistributeInfo(jso, SocketType.SAYTOGROUP);

					// System.out.println("resolve json content: "+jso.getString("content"));
					break;
				case SAY:
					System.out.println("resolve json content: "
							+ jso.getString("content"));
					break;
				case CONNECTSUCCEED:
					System.out.println("resolve json content: "
							+ jso.getString("content"));
					break;
				case LOGINSUCCEED:

					acInfo = new AccountInfo(jso.toString());

					loginStr = jso.toString();

					i = new Intent(ActivityLogin.ReceiverLogin.ACTION_NAME);
					i.putExtra("action",
							ActivityLogin.ReceiverLogin.LOGIN_SUCCEED);
					sendBroadcast(i);
					System.out.println("LOGINSUCCEED: " + jso.toString());

					JSONObject jsoConn = new JSONObject();

					JSONArray groupList = new JSONArray();
					Iterator<String> it = jso.getJSONObject("group_list")
							.keys();
					while (it.hasNext()) {
						String key = it.next();
						groupList.put(Integer.parseInt(key));
					}
					jsoConn.put("type", "Connect")
							.put("account_id", jso.get("account_id"))
							.put("account_name", jso.get("account_name"))
							.put("group_list", groupList);

					webClient.send(jsoConn.toString());

					// String keystr="";
					// JSONObject jso2=jso.getJSONObject("friend_list");
					// JSONArray ja=jso2.names();

					// Iterator<String> it=jso2.keys();
					//
					// while(it.hasNext()){
					// String key=it.next();
					// String value=jso2.getString(key);
					// keystr+=key+" : "+value+" | ";
					// }

					Log.i("ServiceChat", acInfo.toString());

					break;
				case LOGINFAIL:
					i = new Intent(ActivityLogin.ReceiverLogin.ACTION_NAME);
					i.putExtra("action", ActivityLogin.ReceiverLogin.LOGIN_FAIL);
					sendBroadcast(i);
					System.out.println("LOGINFAIL: " + jso.toString());
					break;
				default:
					System.out.println("resolve json content: "
							+ jso.toString());
					break;
				}
				// System.out.println("resolve json: "+jsa.getString("type"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// super.handleMessage(msg);

			/*
			 * switch (msg.what) { case WebClient.DISCONNECT:
			 * Log.i("ServiceChat handleMessage", "disconnect"); break;
			 * 
			 * default: Log.i("ServiceChat handleMessage", (String)msg.obj);
			 * break; }
			 */
		}

	}

}
