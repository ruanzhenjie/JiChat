package com.example.jidachat.service;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

/** This example demonstrates how to create a websocket connection to a server. Only the most important callbacks are overloaded. */
/**
 * @author Administrator
 * 
 * 职责：
 * 使用websocket实现移动端和服务器端的连接，
 * 将受到的信息发送给serviceChat，
 * 被ReceiverController使用，发送消息到服务器端。
 *
 */
public class WebClient extends WebSocketClient {
	Handler MAhandler;

	public final static int DISCONNECT=-2;
	
	public WebClient(Handler h) throws URISyntaxException {
//		super( new URI( "Websocket://192.168.23.1:7272" ), new Draft_10());
		super( new URI( "Websocket://119.29.108.252:7272" ), new Draft_10());
		MAhandler=h;
		
	}

	public WebClient( URI serverURI ) {
		super( serverURI );
	}

	@Override
	public void onOpen( ServerHandshake handshakedata ) {
		System.out.println( "opened connection" );
		//send("{\"type\":\"login\",\"client_name\":\"b\",\"room_id\":\"10\",\"mtype\":\"yes\"}");
		/*
		JSONObject jo=new JSONObject();
		JSONArray ja=new JSONArray();
		try {
			//ja.put(3).put(4).put(6);
			//jo.put("type", "Connect").put("account_name", "b").put("account_id", "2").put("mtype", "yes").put("group_list", ja);
			jo.put("type", "Login").put("account_name", "aaa").put("account_password", "123");
			send(jo.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//send("{\"type\":\"Connect\",\"client_name\":\"b\",\"room_id\":\"10\",\"mtype\":\"yes\"}");
		// if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
	}

	@Override
	public void onMessage( String message ) {
		//System.out.println( "received: " + message );
		Message msg=MAhandler.obtainMessage(1, message);
		MAhandler.sendMessage(msg);
		/*
		try {
			JSONObject jso=new JSONObject(message);
			Message msg=null;
			
			switch (SocketType.toType(jso.getString("type").toUpperCase())) {
			case PING:
				send("{\"type\":\"pong\"}");
				break;
			case SAYTOFRIEND:
				msg=MAhandler.obtainMessage(1, jso.getString("content"));
				MAhandler.sendMessage(msg);
				System.out.println("resolve json content: "+jso.getString("content"));
				break;
			case SAY:
				msg=MAhandler.obtainMessage(1, jso.getString("content"));
				MAhandler.sendMessage(msg);
				System.out.println("resolve json content: "+jso.getString("content"));
				break;
			default:
				System.out.println("resolve json content: "+jso.toString());
				break;
			}
			//System.out.println("resolve json: "+jsa.getString("type"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

	@Override
	public void onFragment( Framedata fragment ) {
		System.out.println( "received fragment: " + new String( fragment.getPayloadData().array() ) );
	}

	@Override
	public void onClose( int code, String reason, boolean remote ) {
		// The codecodes are documented in class org.java_websocket.framing.CloseFrame
		System.out.println( "Connection closed by " + ( remote ? "remote peer" : "us" ) );
		
		JSONObject jso=new JSONObject();
		try {
			jso.put("type", "disconnect");
			
			Message msg=MAhandler.obtainMessage(1, jso.toString());
			MAhandler.sendMessage(msg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void onError( Exception ex ) {
		ex.printStackTrace();
		// if the error is fatal then onClose will be called additionally
	}

	/*
	public static void main( String[] args ) throws URISyntaxException {
		ExampleClient c = new ExampleClient( new URI( "Websocket://127.0.0.1:7272" ), new Draft_10() ); 
		// more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
		c.connect();
	}
	*/
	
	
	public enum SocketType

	{

		SAYTOFRIEND,SAY, PING, LOGIN, LOGOUT,NOVALUE;


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

}