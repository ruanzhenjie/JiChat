package com.example.jidachat.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author Administrator
 * 
 * 职责：
 * 存储登陆用户信息。
 * 以及好友和群组信息。
 *
 */
public class AccountInfo {
	int id;
	String name;
	List<AccountInfo> friendList;
	List<AccountInfo> groupList;
	Map<String, String> friendMap;
	Map<String, String> groupMap;
	
	public AccountInfo(String jsoStr) {
		super();
		try {
			JSONObject jso=new JSONObject(jsoStr);
		
			this.name = jso.getString("account_name");
			this.id = jso.getInt("account_id");

			
			this.friendList=new ArrayList<AccountInfo>();
			
			JSONObject jso2=jso.getJSONObject("friend_list");
			
			Iterator<String> it=jso2.keys();
			
			
			friendMap=new HashMap<String, String>();
			while(it.hasNext()){
				String key=it.next();
				String value=jso2.getString(key);
				this.friendList.add(new AccountInfo(Integer.parseInt(key), value));
				this.friendMap.put(key, value);
			}
			
			this.groupList=new ArrayList<AccountInfo>();
			
			jso2=jso.getJSONObject("group_list");
			
			it=jso2.keys();
			
			groupMap=new HashMap<String, String>();
			while(it.hasNext()){
				String key=it.next();
				String value=jso2.getString(key);
				this.groupList.add(new AccountInfo(Integer.parseInt(key), value));
				this.groupMap.put(key, value);
			}
		

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public AccountInfo(int id,String name) {
		super();
		this.name = name;
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<AccountInfo> getFriendList() {
		return friendList;
	}
	public void setFriendList(List<AccountInfo> friendList) {
		this.friendList = friendList;
	}
	public List<AccountInfo> getGroupList() {
		return groupList;
	}
	public void setGroupList(List<AccountInfo> groupList) {
		this.groupList = groupList;
	}


	@Override
	public String toString() {
		return "AccountInfo [id=" + id + ", name=" + name + ", friendList="
				+ friendList + ", groupList=" + groupList + "]";
	}
	
	public String getFriendName(String id){
		return friendMap.get(id);
	}
	
	public String getGroupName(String id){
		return groupMap.get(id);
	}

}
