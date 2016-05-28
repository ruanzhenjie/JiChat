package com.example.jidachat;

import com.example.jidachat.ActivityListFriend.AdapterFriend;
import com.example.jidachat.data.AccountInfo;
import com.example.jidachat.service.ServiceChat;
import com.example.jidachat.service.ServiceChatConnection;

import android.app.Activity;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


/**
 * @author Administrator
 * 职责：
 * 控制群组列表界面的显示
 * 处理接受到的群组发送的消息。
 *
 */
public class ActivityListGroup extends Activity implements 
ActivityInitable,OnItemClickListener{

	AccountInfo acInfo;

	TextView mtxtv;
	ListView mlist;
	BaseAdapter friendAdapter;

	Button btnFriendList,btnGroupList;
	
	

	ReceiverActivity receiver;
	
	Handler handler;

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
		
		btnFriendList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(ActivityListGroup.this,ActivityListFriend.class);
				startActivity(i);
				finish();
			}
		});
		
		friendAdapter=new AdapterGroup();
		mlist.setAdapter(friendAdapter);
		
		mlist.setOnItemClickListener(this);
		
		receiver=ReceiverActivity.getInstance();
		
		
		handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				Log.i("ActivityListGroup", msg.toString());
				
			}
		};
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Intent i = new Intent(ActivityListGroup.this, ServiceChat.class);
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
	
	class AdapterGroup extends BaseAdapter {

		LayoutInflater inflater;

		public AdapterGroup() {
			super();
			// TODO Auto-generated constructor stub
			inflater = LayoutInflater.from(ActivityListGroup.this);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (acInfo == null)
				return 0;
			else {
				return acInfo.getGroupList().size();
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
			String name=acInfo.getGroupList().get(position).getName();
			int id=acInfo.getGroupList().get(position).getId();

			((TextView) convertView.findViewById(R.id.item_name)).setText(name);
			//((TextView) convertView.findViewById(R.id.item_friend_name)).setTag(new String(""+id));
			//convertView.findViewById(R.id.item_friend).setTag(id+"");
			convertView.setTag(id);

			return convertView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

		Log.i("ActivityList", "position="+position+"\ttag="+view.getTag());
		Intent intent=new Intent(this, ActivityChat.class);
		intent.putExtra("index", position);
		intent.putExtra("type", ActivityChat.SAY_TO_GROUP);
		
		startActivity(intent);
	}

	@Override
	public void Init(String logstr) {
		// TODO Auto-generated method stub
		acInfo = new AccountInfo(logstr);
		mtxtv.setText("Login succeed");
		
	}

}
