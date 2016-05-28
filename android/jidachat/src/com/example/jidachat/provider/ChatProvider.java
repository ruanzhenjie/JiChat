package com.example.jidachat.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * @author Administrator
 * 
 * 职责：
 * 利用contentprovider特有的跨进程特性，存储消息记录。
 * 封装ContentResolver对于provider的通信。
 *
 */
public class ChatProvider extends ContentProvider {

	public final static String AUTHORY = "com.example.jidachat.ChatProvider";
	public final static Uri URI_PERSON = Uri.parse("content://" + AUTHORY
			+ "/person");
	public final static Uri URI_GROUP = Uri.parse("content://" + AUTHORY
			+ "/group");
	public final static Uri URI_GROUP_RECORD = Uri.parse("content://" + AUTHORY
			+ "/group/record");
	public final static Uri URI_PERSON_RECORD = Uri.parse("content://"
			+ AUTHORY + "/person/record");
	public final static Uri URI_GROUP_REMAIN = Uri.parse("content://" + AUTHORY
			+ "/group/remain");
	public final static Uri URI_PERSON_REMAIN = Uri.parse("content://"
			+ AUTHORY + "/person/remain");

	public final static String PERSON = "person";
	public final static String GROUP = "group";
	public final static String GROUP_RECORD = "group/record";
	public final static String PERSON_RECORD = "person/record";
	public final static String GROUP_REMAIN = "group/remain";
	public final static String PERSON_REMAIN = "person/remain";

	public final static int PERSON_URI = 1;
	public final static int GROUP_URI = 2;
	public final static int PERSON_RECORD_URI = 3;
	public final static int GROUP_RECORD_URI = 4;
	public final static int PERSON_REMAIN_URI = 5;
	public final static int GROUP_REMAIN_URI = 6;

	public final static String REMAIN_CLEAR="clear";
	public final static String REMAIN_ADD="ADD";

	UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	ChatDataHelper helper;

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub

		matcher.addURI(AUTHORY, PERSON, PERSON_URI);
		matcher.addURI(AUTHORY, GROUP, GROUP_URI);
		matcher.addURI(AUTHORY, PERSON_RECORD, PERSON_RECORD_URI);
		matcher.addURI(AUTHORY, GROUP_RECORD, GROUP_RECORD_URI);
		matcher.addURI(AUTHORY, PERSON_REMAIN, PERSON_REMAIN_URI);
		matcher.addURI(AUTHORY, GROUP_REMAIN, GROUP_REMAIN_URI);
		
		helper=new ChatDataHelper(getContext(), "mydb.db", null, 1);
		

		System.out.println("=======onCreate was call=======");
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		int match = matcher.match(uri);

		switch (match) {
		case PERSON_RECORD_URI:
			System.out.println("=======PERSON_RECORD_URI was call=======");
			return helper.GetPersonRecord(selectionArgs);
		case GROUP_RECORD_URI:
			System.out.println("=======GROUP_RECORD_URI was call=======");
			return helper.GetGroupRecord(selectionArgs);
		case PERSON_REMAIN_URI:
			System.out.println("=======PERSON_REMAIN_URI was call=======");
			return helper.GetPersonRemain();
		case GROUP_REMAIN_URI:
			System.out.println("=======GROUP_REMAIN_URI was call=======");
			return helper.GetGroupRemain();

		default:
			break;
		}

		System.out.println("=======query was call=======");
		System.out.println("where was: " + selection);
		return null;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		int match = matcher.match(uri);

		switch (match) {
		case PERSON_URI:
			System.out.println("=======PERSON_URI was call=======");
			
			helper.getWritableDatabase().insert("person_record", null, values);
			break;
		case GROUP_URI:
			System.out.println("=======GROUP_URI was call=======");
			helper.getWritableDatabase().insert("group_record", null, values);
			break;

		default:
			break;
		}

		System.out.println("=======insert was call=======");
		System.out.println("values was: " + values + "\n uri=" + uri.toString()
				+ "\n match=" + match);
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub

		System.out.println("=======delete was call=======");
		System.out.println("selection was: " + selection);
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		int match = matcher.match(uri);

		switch (match) {
		case PERSON_REMAIN_URI:
			if(selection.equals(REMAIN_CLEAR)){
				//helper.getWritableDatabase().rawQuery("update person_remain set number=0 where account_local=? and account_remote=?; ", selectionArgs);
				helper.ClearPersonRemain(selectionArgs);
			}
			else if(selection.equals(REMAIN_ADD)){
				//helper.getWritableDatabase().rawQuery("update person_remain set number=number+1 where account_local=? and account_remote=?; ", selectionArgs);
				helper.AddPersonRemain(selectionArgs);
			}
			//ContentUris.
			System.out.println("=======PERSON_URI was call=======");
			break;
		case GROUP_REMAIN_URI:

			if(selection.equals(REMAIN_CLEAR)){
				//helper.getWritableDatabase().rawQuery("update group_remain set number=0 where account_local=? and group_id=?; ", selectionArgs);
				helper.ClearGroupRemain(selectionArgs);
			}
			else if(selection.equals(REMAIN_ADD)){
				//helper.getWritableDatabase().rawQuery("update group_remain set number=number+1 where account_local=? and group_id=?; ", selectionArgs);
				helper.AddGroupRemain(selectionArgs);
			}
			System.out.println("=======GROUP_URI was call=======");
			break;

		default:
			break;
		}

		System.out.println("=======update was call=======");
		System.out.println("values was: " + values + " selection was: "
				+ selection);
		return 0;
	}

	
	
	

	

	public static Cursor ResolverQueryRemain(ContentResolver resolver,Uri goalUri){
		Cursor c=resolver.query(goalUri, null, null, null, null);
		return c;
	}	

	public static Cursor ResolverQueryRecord(ContentResolver resolver,Uri goalUri,int localID,int goalID,int limit,int offset){
		Cursor c=resolver.query(goalUri, null, null, new String[]{localID+"",goalID+"",limit+"",offset+""}, null);
		return c;
	}
	
	
	public static void ResolverInsertPerson(ContentResolver resolver,int local, int remote,String remoteName,String content, int action){
		ContentValues values = new ContentValues();
		values.put("account_local", local);
		values.put("account_remote", remote);
		values.put("account_name", remoteName);
		values.put("content", content);
		values.put("action", action);
		
		resolver.insert(ChatProvider.URI_PERSON, values);
		
	}
	
	
	public static void ResolverInsertGroup(ContentResolver resolver,int local, int group, int remote,String remoteName,String content, int action){
		ContentValues values = new ContentValues();
		values.put("account_local", local);
		values.put("group_id", group);
		values.put("account_remote", remote);
		values.put("account_name", remoteName);
		values.put("content", content);
		values.put("action", action);
		
		resolver.insert(ChatProvider.URI_GROUP, values);
		
	}
	
	public static void ResolverUpdate(ContentResolver resolver,Uri goalUri,String action,int localID,int goalID){
		resolver.update(goalUri, null, action, new String[]{localID+"",goalID+""});
	}


}
