package com.csst.ffmpeg.control;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.csst.videotalk.VideoTalkActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;


public class ConnectListeningServer extends Thread {
	String TAG="ConnectListeningServer";
	ServerSocket serverSocket = null;//new ServerSocket(4038);
	Socket s = null;//so.accept();    
	private final int serverPort=5432;
	private Context mContext;
	private Handler mHandler = null;

	public ConnectListeningServer(Handler mHandler){
		this.mHandler=mHandler;
	}

	public ConnectListeningServer(Context mContex){
		this.mContext=mContex;
	}

	public void run(){
		try {
			System.out.println("监听中客户端连接...... " );

			serverSocket = new ServerSocket(serverPort);
			while (true){
				s = serverSocket.accept();    //会一直的暂停在这里的，s应该保留来维持会话
				Log.e(TAG,"请求连接的IP is :"+s.getInetAddress().getHostAddress());

				Intent intent = new Intent();
				intent.setClass(mContext, VideoTalkActivity.class);
				intent.putExtra("CONNECTIP",s.getInetAddress().getHostAddress());
				intent.putExtra("CONNECTTYPE", "BECALLER");
				mContext.startActivity(intent);

			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
