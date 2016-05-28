package com.example.jidachat.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Administrator
 * 
 * 职责：
 * 被chatprovider调用，协助数据库的操作。
 * 封装数据库信息。
 *
 */
public class ChatDataHelper extends SQLiteOpenHelper {
	
	//final String CREATE_TABLE_SQL="create table person_record (id integer primary key auto)"
	//final String CREATE_

	public ChatDataHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//db.rawQuery("create table person_record(id integer primary key autoincrement,account_local integer,account_remote integer,content text,action integer);", null);
		db.execSQL("create table person_record(id integer primary key autoincrement,account_local integer,account_remote integer,account_name text,content text,action integer);");
		db.execSQL("create index person_record_local on person_record(account_local);");
		db.execSQL("create index person_record_remote on person_record(account_remote);");
		
		db.execSQL("create table group_record(id integer primary key autoincrement,account_local integer,group_id integer,account_remote integer,account_name text,content text,action integer);");
		db.execSQL("create index group_record_local on group_record(account_local);");
		db.execSQL("create index group_record_group_id on group_record(group_id);");
		
		
		db.execSQL("create table group_remain(id integer primary key autoincrement,account_local integer,group_id integer,number integer);");
		db.execSQL("create index group_remain_local on group_remain(account_local);");
		db.execSQL("create index group_remain_group_id on group_remain(group_id);");
		
		db.execSQL("create table person_remain(id integer primary key autoincrement,account_local integer,account_remote integer,number);");
		db.execSQL("create index person_remain_local on person_remain(account_local);");
		db.execSQL("create index person_remain_remote on person_remain(account_remote);");


	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	

	public void ClearPersonRemain(String[] selectionArgs){
		Cursor c= getWritableDatabase().rawQuery("select * from person_remain where account_local=? and account_remote=?;",selectionArgs);
		if(c.getCount()==0){
			getWritableDatabase().execSQL("insert into person_remain (account_local,account_remote,number) values(?,?,0);",selectionArgs);
		}
		else{
			getWritableDatabase().execSQL("update person_remain set number=0 where account_local=? and account_remote=?; ", selectionArgs);
		}
		c.close();
	}
	public void ClearGroupRemain(String[] selectionArgs){
		Cursor c= getWritableDatabase().rawQuery("select * from group_remain where account_local=? and group_id=?;",selectionArgs);
		if(c.getCount()==0){
			getWritableDatabase().execSQL("insert into group_remain (account_local,group_id,number) values(?,?,0);",selectionArgs);
		}
		else{
			getWritableDatabase().execSQL("update group_remain set number=0 where account_local=? and group_id=?; ", selectionArgs);
		}
		c.close();
	}	

	public void AddPersonRemain(String[] selectionArgs){
		//getWritableDatabase().rawQuery("update person_remain set number=number+1 where account_local=? and account_remote=?; ", selectionArgs);
		//getWritableDatabase().rawQuery("insert into person_remain (account_local,account_remote,number) values( 2 , 3 ,1);",null);

		Cursor c= getWritableDatabase().rawQuery("select * from person_remain where account_local= ? and account_remote= ?;",selectionArgs);
		if(c.getCount()==0){
//			ContentValues cv=new ContentValues();
//			cv.put("account_local", selectionArgs[0]);
//			cv.put("account_remote", selectionArgs[1]);
//			cv.put("number", 1);
//			getWritableDatabase().insert("person_remain", null, cv);
			
//			getWritableDatabase().rawQuery("insert into person_remain (account_local,account_remote,number) values( ? , ? ,1);",selectionArgs);
			getWritableDatabase().execSQL("insert into person_remain (account_local,account_remote,number) values( ? , ? ,1);",selectionArgs);
			
		}
		else{
//			getWritableDatabase().rawQuery("update person_remain set number=number+1 where account_local=? and account_remote=?; ", selectionArgs);
			getWritableDatabase().execSQL("update person_remain set number=number+1 where account_local=? and account_remote=?; ", selectionArgs);
		}
		c.close();
	}
	public void AddGroupRemain(String[] selectionArgs){
		//getWritableDatabase().rawQuery("update group_remain set number=number+1 where account_local=? and group_id=?; ", selectionArgs);
		
		Cursor c= getWritableDatabase().rawQuery("select * from group_remain where account_local=? and group_id=?;",selectionArgs);
		if(c.getCount()==0){
			getWritableDatabase().execSQL("insert into group_remain (account_local,group_id,number) values(?,?,1);",selectionArgs);
		}
		else{
			getWritableDatabase().execSQL("update group_remain set number=number+1 where account_local=? and group_id=?; ", selectionArgs);
		}
		c.close();
	}
	

	public Cursor GetPersonRecord(String[] selectionArgs){
		return getWritableDatabase().rawQuery("select * from person_record where account_local=? and account_remote=? order by id desc LIMIT ? OFFSET ?;", selectionArgs);
	}
	public Cursor GetGroupRecord(String[] selectionArgs){
		return getWritableDatabase().rawQuery("select * from group_record where account_local=? and group_id=? order by id desc LIMIT ? OFFSET ?;", selectionArgs);
	}
	

	public static String[] GetPersonRecordStrings(String local,String remote,String limit,String offset){
		return new String[]{local,remote,limit,offset};
	}	

	public static String[] GetGroupRecordStrings(String local,String group,String limit,String offset){
		return new String[]{local,group,limit,offset};
	}
	

	public Cursor GetPersonRemain(){
		return getWritableDatabase().rawQuery("select * from person_remain;", null);
	}
	public Cursor GetGroupRemain(){
		return getWritableDatabase().rawQuery("select * from group_remain;", null);
	}

	public static ContentValues PersonValues(int local, int remote,
			String content, int action) {
		ContentValues values = new ContentValues();
		values.put("account_local", local);
		values.put("account_remote", remote);
		values.put("content", content);
		values.put("action", action);

		return values;
	}

	public static ContentValues GroupValues(int local, int group, int remote,
			String content, int action) {
		ContentValues values = new ContentValues();
		values.put("account_local", local);
		values.put("group_id", group);
		values.put("account_remote", remote);
		values.put("content", content);
		values.put("action", action);

		return values;
	}

}
