
/*
*   @version 1.2 2012-06-29
*   @author wangzheng
*/

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;

import javax.imageio.*;
import javax.swing.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.ServerSocket;

/**
*在服务器开启情况下，启动客户端，创建套接字接收图像
*/

public class ImageCtrlServer {	
    public static ServerSocket ss = null;
    
    public static void main(String args[]) throws Exception,IOException{    
    	ss = new ServerSocket(6000);
        
        final ImageFrame frame = new ImageFrame(ss);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
       
        while(true){
        	frame.panel.getimage();
            frame.repaint();
        }        
    }
       
}

/** 
    A frame with an image panel
*/
@SuppressWarnings("serial")
class ImageFrame extends JFrame{
	public ImagePanel panel;
	public JButton jb;
   
    public ImageFrame(ServerSocket ss)throws Exception{
   	    // get screen dimensions   	   
   	    Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        String IPname = null;
        
        try
        {
        	System.out.println("本机的IP = " + InetAddress.getLocalHost().getHostAddress()+" 如果电脑装了VMware虚拟机IP地址为虚拟机的地址");
        	IPname = InetAddress.getLocalHost().getHostAddress().toString();
        } catch (UnknownHostException e){ 
        	e.printStackTrace();
        }
        // center frame in screen
        setTitle("ImageTest"+"本机的IP = " + IPname);
        setLocation((screenWidth - DEFAULT_WIDTH) / 2, (screenHeight - DEFAULT_HEIGHT) / 2);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        // add panel to frame
        this.getContentPane().setLayout(null);
        panel = new ImagePanel(ss);
        panel.setSize(640,480);
		//panel.setSize(1280,720);
		//panel.setSize(1920,1080);
		//panel.setSize(1440,720);
        panel.setLocation(0, 0);
        add(panel);
        jb = new JButton("方向键控制");
        jb.setBounds(0,480,640,50);
        add(jb);
        
        jb.addKeyListener(new KeyAdapter() {
        	ServerSocket ss;
        	boolean sendFlag = false;//设置标志位,按下时只执行一次,不连续发送
        	public void keyPressed(KeyEvent e) {
        		int KeyCode = e.getKeyCode(); // 返回所按键对应的整数值
        		String s = KeyEvent.getKeyText(KeyCode); // 返回按键的字符串描述
        		System.out.print("输入的内容为：" + s + ",");
        		System.out.println("对应的KeyCode为：" + KeyCode);
        		if(!sendFlag) {
        		try{
        			ss = new ServerSocket(7788);
        			send(KeyCode);
        			ss.close();
        			sendFlag=true;
        		}catch (Exception e1) {

        			e1.printStackTrace();
        		}
        		}
        		
        	}
        	public void keyReleased(KeyEvent e) {
        		int KeyCode = e.getKeyCode(); // 返回所按键对应的整数值
        		if(KeyCode==87||KeyCode==83||KeyCode==65||KeyCode==68||KeyCode==81||KeyCode==69) {
        			try {
        				ss = new ServerSocket(7788);
						stop();
						sendFlag=false;
					} catch (Exception e1) {
						e1.printStackTrace();
					}
        		}
        	}

			public void send(int i) throws Exception{
					@SuppressWarnings("resource")
					ServerSocket serverSocket = ss;//new ServerSocket(7788); // 创建ServerSocket对象
					Socket client = serverSocket.accept(); // 调用ServerSocket的accept()方法接收数据
					OutputStream os = client.getOutputStream();// 获取客户端的输出流
					System.out.println("开始与客户端交互数据");
					switch (i) {
	        		case 87:os.write(("01").getBytes());break;//w上
	        		case 83:os.write(("02").getBytes());break;//s下
	        		case 65:os.write(("03").getBytes());break;//a左
	        		case 68:os.write(("04").getBytes());break;//d右
					case 81:os.write(("13").getBytes());break;//q左自转
					case 69:os.write(("14").getBytes());break;//e右自转
					case 49:os.write(("s1").getBytes());break;//速度1
					case 50:os.write(("s2").getBytes());break;//速度2
					case 51:os.write(("s3").getBytes());break;//速度3
					case 52:os.write(("s").getBytes());break;//全速
					case 16:os.write(("L").getBytes());break;//shift闪光灯
					case 67:os.write(("C").getBytes());break;//c切换摄像头
	        		} 			
					
					System.out.println("结束与客户端交互数据");
					os.close();
					client.close();
			}
			protected void stop() throws Exception {
				ServerSocket serverSocket = ss;// 创建ServerSocket对象
				Socket client = serverSocket.accept(); // 调用ServerSocket的accept()方法接收数据
				OutputStream os = client.getOutputStream();// 获取客户端的输出流
				os.write(("05").getBytes());//停止
				os.close();
				client.close();
				ss.close();
			}
        });
    }


	public static final int DEFAULT_WIDTH = 640;
    public static final int DEFAULT_HEIGHT = 560;  
}

/**
   A panel that displays a tiled image
*/
@SuppressWarnings("serial")
class ImagePanel extends JPanel {     
    private ServerSocket ss;
    private Image image;
    private InputStream ins;
	 
    public ImagePanel(ServerSocket ss) {  
	    this.ss = ss;
    }
    
    public void getimage() throws IOException{
    	Socket s = this.ss.accept();
        System.out.println("连接成功!");
        this.ins = s.getInputStream();
		this.image = ImageIO.read(ins);
		this.ins.close();
    }
   
    public void paintComponent(Graphics g){  
        super.paintComponent(g);    
        if (image == null) return;
        g.drawImage(image, 0, 0,640,480, null);
		//要显示的图片对象，水平位置，垂直位置，图片的新宽度，新高度，要通知的图像观察者
    }

}