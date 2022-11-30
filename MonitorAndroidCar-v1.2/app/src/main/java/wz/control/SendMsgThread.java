package wz.control;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SendMsgThread extends Thread {
	String ip ;
	String msg;
	public SendMsgThread(String ip, String msg) {
		this.ip=ip;
		this.msg=msg;
	}

	public void run() {
		// 这里写入子线程需要做的工作

		try {
			Socket socket = new Socket(ip, 7788);
			OutputStream os = socket.getOutputStream();// 获取客户端的输出流
			System.out.println("开始与客户端交互数据"+msg);
			os.write((msg).getBytes());
			System.out.println("结束与客户端交互数据"+msg);
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
