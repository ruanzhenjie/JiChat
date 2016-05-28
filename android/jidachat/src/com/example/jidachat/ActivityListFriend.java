package com.example.jidachat;

import com.example.jidachat.data.AccountInfo;
import com.example.jidachat.service.ServiceChat;
import com.example.jidachat.service.ServiceChatAidl;
import com.example.jidachat.service.ServiceChatConnection;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


/**
 * @author Administrator
 * 
 *职责：
 *控制好友列表的视图层，
 *通过AIDL与ServiceChat通信，检测用户登陆与否，
 *开启ReceiverActivity
 *处理接收到的好友的信息。
 *
 */
public class ActivityListFriend extends Activity implements 
		ActivityInitable,OnItemClickListener {
	ServiceChatAidl mbinder;

	AccountInfo acInfo;

	TextView mtxtv;
	ListView mlist;
	BaseAdapter friendAdapter;
	Button btnFriendList,btnGroupList;
	
	ReceiverActivity receiver;
//	boolean shouldCloseReciver=false;
	
	Handler handler;
	
	// boolean

	private ServiceChatConnection conn = new ServiceChatConnection(this, this);


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		mtxtv = (TextView) findViewById(R.id.mtxtv);
		mlist=(ListView) findViewById(R.id.list_friend);
		btnFriendList=(Button) findViewById(R.id.btn_friend_list);
		btnGroupList=(Button) findViewById(R.id.btn_group_list);
		receiver=ReceiverActivity.getInstance();
		
		btnGroupList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(ActivityListFriend.this,ActivityListGroup.class);
				startActivity(i);
			}
		});
		
		friendAdapter=new AdapterFriend();
		mlist.setAdapter(friendAdapter);
		
		mlist.setOnItemClickListener(this);

		
		
		handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				Log.i("ActivityFriend", msg.toString());
				
			}
		};
		

		IntentFilter filter=new IntentFilter(ReceiverActivity.ACTION_NAME);
		registerReceiver(receiver, filter);
	}
	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Log.i("ActivityList", "position="+position+"\ttag="+view.getTag());
		
		Intent intent=new Intent(this, ActivityChat.class);
		intent.putExtra("index", position);
		intent.putExtra("type", ActivityChat.SAY_TO_FRIEND);
		
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		super.onDestroy();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		Intent i = new Intent(ActivityListFriend.this, ServiceChat.class);
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
		
		Log.i("ActivityList", "onPause");
	}

	void InitWithAccount() {
		mtxtv.setText("Login succeed");
	}

	@Override
	public void Init(String logstr) {
		// TODO Auto-generated method stub
		acInfo = new AccountInfo(logstr);
		mtxtv.setText("Login succeed");
		Log.i("ActivityList", acInfo.toString());
		
		
		
		
		
//		friendAdapter.notifyDataSetChanged();
//		mlist.
	}


	class ViewHolder {
		public TextView friendName;
	}

	class AdapterFriend extends BaseAdapter {

		LayoutInflater inflater;

		public AdapterFriend() {
			super();
			// TODO Auto-generated constructor stub
			inflater = LayoutInflater.from(ActivityListFriend.this);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (acInfo == null)
				return 0;
			else {
				return acInfo.getFriendList().size();
			}
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
//			ViewHolder holder = null;
//
//			if (convertView == null) {
//				holder = new ViewHolder();
//				convertView = inflater.inflate(R.layout.list_item_friend, null);
//				holder.friendName = (TextView) convertView.findViewById(R.id.item_friend_name);
//
//			} else {
//				holder = (ViewHolder) convertView.getTag();
//			}
//			
//			String name=acInfo.getFriendList().get(position).getName();
//			
//			holder.friendName.setText(name);
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_item, null);
			}
			String name=acInfo.getFriendList().get(position).getName();
			int id=acInfo.getFriendList().get(position).getId();

			((TextView) convertView.findViewById(R.id.item_name)).setText(name);
			//((TextView) convertView.findViewById(R.id.item_friend_name)).setTag(new String(""+id));
			//convertView.findViewById(R.id.item_friend).setTag(id+"");
			convertView.setTag(id);

			return convertView;
		}

	}


}
