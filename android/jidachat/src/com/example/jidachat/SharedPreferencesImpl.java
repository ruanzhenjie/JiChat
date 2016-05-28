package com.example.jidachat;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesImpl{
	private SharedPreferences sp;
	Context context;
	
	private static class SharedpreferencedHolder{
		static SharedPreferencesImpl instance=new SharedPreferencesImpl();
	}
	
	private SharedPreferencesImpl() {
		// TODO Auto-generated constructor stub
	}
	
	public SharedPreferencesImpl getIntance(Context mcontext){
		context=mcontext;
		return SharedpreferencedHolder.instance;
	}
	
	public void putString(String filename,String key,String value){
		synchronized (this) {
			sp=context.getSharedPreferences(filename, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor= sp.edit();
			editor.putString(key, value);
			editor.commit();
		}
	}
	
	public void putInt(String filename,String key,int value){
		synchronized (this) {
			sp=context.getSharedPreferences(filename, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor= sp.edit();
			editor.putInt(key, value);
			editor.commit();
		}
	}
	

	public Map<String, ?> getAll(String filename){
		Map<String, ?> res;
		synchronized (this) {
			sp=context.getSharedPreferences(filename, Context.MODE_PRIVATE);
			res=sp.getAll();
		}
		return res;
	}

	
	public String getString(String filename,String key,String defValue){
		String res;
		synchronized (this) {
			sp=context.getSharedPreferences(filename, Context.MODE_PRIVATE);
			res=sp.getString(key, defValue);
		}
		return res;
	}
	
	public int getInt(String filename,String key,int defValue){
		int res;
		synchronized (this) {
			sp=context.getSharedPreferences(filename, Context.MODE_PRIVATE);
			res=sp.getInt(key, defValue);
		}
		return res;
	}

}
