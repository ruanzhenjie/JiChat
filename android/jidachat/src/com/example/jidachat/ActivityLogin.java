package com.example.jidachat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.jidachat.service.ReceiverServiceController;
import com.example.jidachat.service.ServiceChat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * @author Administrator
 *
 *职责：
 *登陆界面的显示
 *利用sharepreference保存登陆信息。
 */
public class ActivityLogin extends Activity {
	Button BtnLogin;
	AutoCompleteTextView edtAccount;
	EditText edtPassword;
	SharedPreferences sp;
	SharedPreferences.Editor editor;
	RelativeLayout mask;
	String newAccount;
	String newPassword;
	ReceiverLogin receiver;
	InputMethodManager imm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		BtnLogin=(Button) findViewById(R.id.btn_login);
		sp=getSharedPreferences("AccountFile", Context.MODE_PRIVATE);
		editor=sp.edit();
		edtAccount=(AutoCompleteTextView) findViewById(R.id.edt_account);
		edtPassword=(EditText) findViewById(R.id.edt_password);
		mask=(RelativeLayout) findViewById(R.id.mask_login);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		
//		String[] arr=(String[]) sp.getAll().keySet().toArray();
		//String[] arr = new String[sp.getAll().size()];// sp.getAll().size()返回的是有多少个键值对
		String[] arr = sp.getAll().keySet().toArray(new String[0]);

		
		
		/*
		List<String> list;
		list=new ArrayList<String>();
		for (String string : arr) {
			list.add(string);
		}
		arr=list.toArray(new String[0]);
		*/
		final ArrayAdapter<String> ad=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arr);
		
		edtAccount.setAdapter(ad);
		
		
		BtnLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent i=new Intent(ActivityLogin.this, ServiceChat.class);
				//startService(i);
				mask.setVisibility(View.VISIBLE);
				
				newAccount=edtAccount.getText().toString();
				newPassword=edtPassword.getText().toString();
				
				Intent i=new Intent(ReceiverServiceController.ACTION_NAME);
				i.putExtra("action", ReceiverServiceController.LOGIN);
				i.putExtra("account_name", newAccount);
				i.putExtra("account_password", newPassword);
				sendBroadcast(i);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				
				/*
				ad.add(newAccount);
				
				editor.putString(newAccount, newPassword).commit();
				
				Toast.makeText(ActivityLogin.this, "added",
						Toast.LENGTH_SHORT).show();*/
				
				/*
				String newAccount=edtAccount.getText().toString();
				String newPassword=edtPassword.getText().toString();
				String lastAccount=shareprefernce.getString("account", null);
				String lastPassword=shareprefernce.getString("password", null);

				if (null == lastAccount || null == lastPassword
						|| !lastAccount.equals(newAccount)
						|| !lastPassword.equals(newPassword)) {
					Toast.makeText(ActivityLogin.this, "don't match",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(ActivityLogin.this, "match",
							Toast.LENGTH_SHORT).show();
				}
				editor.putString("account", newAccount);
				editor.putString("password", newPassword);
				editor.commit();*/
				//editor.clear();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		receiver=new ReceiverLogin();
		IntentFilter filter=new IntentFilter(ReceiverLogin.ACTION_NAME);
		registerReceiver(receiver, filter);
		
		Intent i=new Intent(ActivityLogin.this, ServiceChat.class);
		startService(i);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(receiver);
	}



	public class ReceiverLogin extends BroadcastReceiver{
		public final static String ACTION_NAME="com.example.jidachat.receiverlogin";
		public final static int NONE=-1;
		public final static int LOGIN_SUCCEED=0;
		public final static int LOGIN_FAIL=-2;

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			int ac=intent.getIntExtra("action", NONE);
			switch (ac) {
			case LOGIN_SUCCEED:
				editor.putString(newAccount, newPassword).commit();
				
				Intent i=new Intent(ActivityLogin.this, ActivityListFriend.class);
				//i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				ActivityLogin.this.startActivity(i);
				ActivityLogin.this.finish();
				//ActivityLogin.this.mask.setVisibility(View.GONE);
				break;
			case LOGIN_FAIL:
				ActivityLogin.this.mask.setVisibility(View.GONE);
				break;

			default:
				break;
			}
		}
		
	}
	
	
}
