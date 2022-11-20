package com.csst.ir.control;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SendCtrlThread extends Thread {
	String ip ;
	int motorCode;
	public SendCtrlThread( String ip,int motorCode) {
		this.ip=ip;
		this.motorCode=motorCode;
	}

	public void run() {
		// 这里写入子线程需要做的工作

		try {
			Socket socket = new Socket(ip, 7788);
			OutputStream os = socket.getOutputStream();// 获取客户端的输出流
			System.out.println("开始与客户端交互数据");
			switch (motorCode) {
				case 1:os.write(("01").getBytes());break;//w上
				case 2:os.write(("02").getBytes());break;//s下
				case 3:os.write(("03").getBytes());break;//a左
				case 4:os.write(("04").getBytes());break;//d右
				case 5:os.write(("05").getBytes());break;//停
				case 6:os.write(("s").getBytes());break;//全速
				case 7:os.write(("s1").getBytes());break;//速度1
				case 8:os.write(("s2").getBytes());break;//速度2
				case 9:os.write(("s3").getBytes());break;//速度3
				case 10:os.write(("L").getBytes());break;//开关闪光灯
				case 11:os.write(("C").getBytes());break;//切换摄像头
				case 13:os.write(("13").getBytes());break;//rc_car前后停止
				case 14:os.write(("14").getBytes());break;//rc_car左右停止
			}
			System.out.println("结束与客户端交互数据");
			os.close();
			socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
