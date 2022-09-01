package org.wangzheng.ctrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import android.hardware.ConsumerIrManager;
import android.util.Log;

public class CtrlThread extends Thread {

	private String ipname;
	private ConsumerIrManager mCIR;

	/*
	 * 有问题，此处安卓程序，和小车单片机程序的数据码不一样，但能对应执行
	 * 单片机 0x76 3 5 4
	 * 安卓     0x12 8 6 4
	 * 第三行数据码反置，比如0x12=0001 0010反置为 0100 1000
	 * 可能和接收有关系，只有反置了之后才能接收正常
	 * */
	// 一种交替的载波序列模式，通过毫秒测量
	//引导码，地址码，地址码，数据码，数据反码
	
	//0x76	0111 0110
	int[] pattern1 = { 9000, 4500, 
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
/*0100 1000*/560, 560,	560, 1690, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560, 
			560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560,1690, 	560, 1690, 	560, 1690, 
			560, 42020, 9000, 2250, 560, 98190 };
	//0x73
	int[] pattern2 = { 9000, 4500, 
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560, 
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 
/*0001 1000*/560, 560,	560, 560, 	560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560, 560, 
			560, 1690, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690, 
			560, 42020, 9000, 2250, 560, 98190 };
	//0x75
	int[] pattern3 = { 9000, 4500, 
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
/*0110 1000*/560, 560,	560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560, 	
			560, 1690, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 1690,	560, 1690, 	560, 1690, 
			560, 42020, 9000, 2250, 560, 98190 };
	//0x74
	int[] pattern4 = { 9000, 4500, 
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 
/*0010 1000*/560, 560,	560, 560, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560, 
			560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690, 
			560, 42020, 9000, 2250, 560, 98190 };
	//0x74
	int[] pattern5 = { 9000, 4500, 
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 
/*1101 1000*/560,1690,	560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560, 560, 
			560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690, 
			560, 42020, 9000, 2250, 560, 98190 };

	private int hz = 38000;

	public CtrlThread(String ipname, ConsumerIrManager mCIR) {
		this.ipname = ipname;
		this.mCIR = mCIR;
	}

	public void run() {
		// 这里写入子线程需要做的工作

		Socket socket = null;

		while (true) {
			try {
				socket = new Socket(ipname, 7788);
				// 接受服务器的信息
				BufferedReader br = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				String mstr = br.readLine();
				if (mstr.equals("01")) {

					{
						System.out.println("----------recive--ctrl---ok");
						mCIR.transmit(hz, pattern1);//前
						System.out.println("----------IR>>>send1 ok");
					}
				} else if (mstr.equals("02")) {

					{
						System.out.println("----------recive--ctrl---ok");
						mCIR.transmit(hz, pattern2);//后
						System.out.println("----------IR>>>send2 ok");
					}
				} else if (mstr.equals("03")) {

					{
						System.out.println("----------recive--ctrl---ok");
						mCIR.transmit(hz, pattern3);//左
						System.out.println("----------IR>>>send3 ok");
					}
				} else if (mstr.equals("04")) {

					{
						System.out.println("----------recive--ctrl---ok");
						mCIR.transmit(hz, pattern4);//右
						System.out.println("----------IR>>>send4 ok");
					}
				} else if (mstr.equals("05")) {
					
					{
						System.out.println("----------recive--ctrl---ok");
						mCIR.transmit(hz, pattern5);//停止
						System.out.println("----------IR>>>send5 ok");
					}
				} else {
					System.out.println("数据错误");
				}

				br.close();
				socket.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				Log.e("ctrlSocket", e.toString());
			}
		}

	}

}
