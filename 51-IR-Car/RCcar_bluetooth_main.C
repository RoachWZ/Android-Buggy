	/****************************************************************************
	 简单寻迹程序：接法
	 
     EN1 EN2 PWM输入端，本程序不输入PWM，直接使插上跳线帽，使能输出，这样就能全速运行

	 无线摇控模块与单片机连接
	 无线模块GND +5取自于单片机板的5V输出
	 P0_7 与无线模块D0相连
	 P0_6 与无线模块D1相连
	 P0_5 与无线模块D2相连
	 P0_4 与无线模块D3相连

     P1_0 P1_1 接IN1  IN2    当 P1_0=1,P1_1=0; 时左上电机正转	左上电机接驱动板子输出端（蓝色端子OUT1 OUT2）
	 P1_0 P1_1 接IN1  IN2    当 P1_0=0,P1_1=1; 时左上电机反转	

	 P1_0 P1_1 接IN1  IN2    当 P1_0=0,P1_1=0; 时左上电机停转	

	 P1_2 P1_3 接IN3  IN4    当 P1_2=1,P1_3=0; 时左下电机正转	左下电机接驱动板子输出端（蓝色端子OUT3 OUT4）
	 P1_2 P1_3 接IN3  IN4    当 P1_2=0,P1_3=1; 时左下电机反转   

	 P1_2 P1_3 接IN3  IN4    当 P1_2=0,P1_3=0; 时左下电机停转	

	 P1_4 P1_5 接IN5  IN6    当 P1_4=1,P1_5=0; 时右上电机正转	右上电机接驱动板子输出端（蓝色端子OUT5 OUT6）
	 P1_4 P1_5 接IN5  IN6    当 P1_4=0,P1_5=1; 时右上电机反转

	 P1_4 P1_5 接IN5  IN6    当 P1_4=0,P1_5=0; 时右上电机停转

	 P1_6 P1_7 接IN7  IN8    当 P1_6=1,P1_7=0; 时右下电机正转	右下电机接驱动板子输出端（蓝色端子OUT7 OUT8）
	 P1_6 P1_7 接IN7  IN8    当 P1_6=0,P1_7=1; 时右下电机反转

	 P1_6 P1_7 接IN7  IN8    当 P1_6=0,P1_7=0; 时右下电机停转
    

     P3_2接四路寻迹模块接口第一路输出信号即中控板上面标记为OUT1
     P3_3接四路寻迹模块接口第二路输出信号即中控板上面标记为OUT2	
     P3_4接四路寻迹模块接口第三路输出信号即中控板上面标记为OUT3
	 P3_5接四路寻迹模块接口第四路输出信号即中控板上面标记为OUT4
	 四路寻迹传感器有信号(白线）为0  没有信号（黑线）为1
	 四路寻迹传感器电源+5V GND 取自于单片机板靠近液晶调节对比度的电源输出接口

																							 
	 关于单片机电源：本店驱动模块内带LDO稳压芯片，当电池输入6V时时候可以输出稳定的5V
	 分别在针脚标+5 与GND 。这个输出电源可以作为单片机系统的供电电源。
	****************************************************************************/
	
	
	#include<AT89x51.H>

    #define Left_1_led        P3_4	 //P3_2接四路寻迹模块接口第一路输出信号即中控板上面标记为OUT1
	#define Left_2_led        P3_5	 //P3_3接四路寻迹模块接口第二路输出信号即中控板上面标记为OUT2	

    #define Right_1_led       P3_6	 //P3_4接四路寻迹模块接口第三路输出信号即中控板上面标记为OUT3
	#define Right_2_led       P3_7	 //P3_5接四路寻迹模块接口第四路输出信号即中控板上面标记为OUT4

	#define Left_moto_go      {P1_0=1,P1_1=0,P1_2=1,P1_3=0;}    //左边两个电机向前走
	#define Left_moto_back    {P1_0=0,P1_1=1,P1_2=0,P1_3=1;} 	//左边两个电机向后转
	#define Left_moto_Stop    {P1_0=0,P1_1=0,P1_2=0,P1_3=0;}    //左边两个电机停转                     
	#define Right_moto_go     {P1_4=1,P1_5=0,P1_6=1,P1_7=0;}	//右边两个电机向前走
	#define Right_moto_back   {P1_4=0,P1_5=1,P1_6=0,P1_7=1;}	//右边两个电机向前走
	#define Right_moto_Stop   {P1_4=0,P1_5=0,P1_6=0,P1_7=0;}	//右边两个电机停转   

	#define left     'C'
    #define right    'D'
	#define up       'A'
    #define down     'B'
	#define stop     'F'
	#define lstop    'L'
	#define rstop    'R'

	char code str[] =  "forward!\n";
	char code str1[] = "backward!\n";
	char code str2[] = "left!\n";
	char code str3[] = "right!\n";
	char code str4[] = "stop!\n";
	char code str5[] = "turn stop!\n";
	char code str6[] = "motor stop!\n";

	bit  flag_REC=0; 
	bit  flag    =0;  
	

	unsigned char  i=0;
	unsigned char  dat=0;
    unsigned char  buff[5]=0; //接收缓冲字节


   
/************************************************************************/	
//延时函数	
   void delay(unsigned int k)
{    
     unsigned int x,y;
	 for(x=0;x<k;x++) 
	   for(y=0;y<2000;y++);
}

/************************************************************************/
//字符串发送函数
void send_str( )
                   // 传送字串
    {
	    unsigned char i = 0;
	    while(str[i] != '\0')
	   {
		SBUF = str[i];
		while(!TI);				// 等特数据传送
		TI = 0;					// 清除数据传送标志
		i++;					// 下一个字符
	   }	
    }
	
	void send_str1( )
                   // 传送字串
    {
	    unsigned char i = 0;
	    while(str1[i] != '\0')
	   {
		SBUF = str1[i];
		while(!TI);				// 等特数据传送
		TI = 0;					// 清除数据传送标志
		i++;					// 下一个字符
	   }	
    }	

void send_str2( )
                   // 传送字串
    {
	    unsigned char i = 0;
	    while(str2[i] != '\0')
	   {
		SBUF = str2[i];
		while(!TI);				// 等特数据传送
		TI = 0;					// 清除数据传送标志
		i++;					// 下一个字符
	   }	
    }	
	    	
void send_str3()
                   // 传送字串
    {
	    unsigned char i = 0;
	    while(str3[i] != '\0')
	   {
		SBUF = str3[i];
		while(!TI);				// 等特数据传送
		TI = 0;					// 清除数据传送标志
		i++;					// 下一个字符
	   }	
    }	

void send_str4()
                   // 传送字串
    {
	    unsigned char i = 0;
	    while(str4[i] != '\0')
	   {
		SBUF = str4[i];
		while(!TI);				// 等特数据传送
		TI = 0;					// 清除数据传送标志
		i++;					// 下一个字符
	   }	
    }	
	    	
void send_str5()
                   // 传送字串
    {
	    unsigned char i = 0;
	    while(str5[i] != '\0')
	   {
		SBUF = str5[i];
		while(!TI);				// 等特数据传送
		TI = 0;					// 清除数据传送标志
		i++;					// 下一个字符
	   }	
    }
		
void send_str6()
                   // 传送字串
    {
	    unsigned char i = 0;
	    while(str6[i] != '\0')
	   {
		SBUF = str6[i];
		while(!TI);				// 等特数据传送
		TI = 0;					// 清除数据传送标志
		i++;					// 下一个字符
	   }	
    }	   
	 	
/************************************************************************/
//前速前进
     void  run(void)
{
    
	 //Left_moto_go ;   //左电机往前走
	 //Right_moto_go ;  //右电机往前走
	 Right_moto_back ;
}

//前速后退
     void  backrun(void)
{
    
	 //Left_moto_back ;   //左电机往前走
	 //Right_moto_back ;  //右电机往前走
	 Right_moto_go ;
}

//左转
     void  leftrun(void)
{
    
	 Left_moto_back ;   //左电机往前走
	 //Right_moto_go ;  //右电机往前走
}

//右转
     void  rightrun(void)
{
    
	 Left_moto_go ;   //左电机往前走
	 //Right_moto_back ;  //右电机往前走
}
//STOP
     void  stoprun(void)
{
    
	 Left_moto_Stop ;   //左电机往前走
	 Right_moto_Stop ;  //右电机往前走
}
//L-STOP
     void  stop_l(void)
{
    
	 Left_moto_Stop ;   //左电机停止
}
//R-STOP
     void  stop_r(void)
{
    
	 Right_moto_Stop ;  //右电机停止
}
/************************************************************************/
void sint() interrupt 4	  //中断接收3个字节
{ 
 
    if(RI)	                 //是否接收中断
    {
       RI=0;
       dat=SBUF;
       if(dat=='O'&&(i==0)) //接收数据第一帧
         {
            buff[i]=dat;
            flag=1;        //开始接收数据
         }
       else
      if(flag==1)
     {
      i++;
      buff[i]=dat;
      if(i>=2)
      {i=0;flag=0;flag_REC=1 ;}  // 停止接收
     }
	 }
}
/*********************************************************************/		 
/*--主函数--*/
	void main(void)
{
	TMOD=0x20;  
    TH1=0xFd;  		   //11.0592M晶振，9600波特率
    TL1=0xFd;
    SCON=0x50;  
    PCON=0x00; 
    TR1=1;
	ES=1;   
    EA=1;   
  	
	while(1)							/*无限循环*/
	{ 
	  if(flag_REC==1)				    //
	   {
		flag_REC=0;
		if(buff[0]=='O'&&buff[1]=='N')	//第一个字节为O，第二个字节为N，第三个字节为控制码
		switch(buff[2])
	     {
		      case up :						    // 前进
			  send_str( );
			  run();
			  break;
		      case down:						// 后退
			   send_str1( );
			  backrun();
			  break;
		      case left:						// 左转
			   send_str2( );
			  leftrun();
			  break;
		      case right:						// 右转
			  send_str3( );
			  rightrun();
			  break;
		      case stop:						// 停止
			   send_str4( );
			  stoprun();
			  break;
			  case lstop:						// L停止
			   send_str5( );
			  stop_l();
			  break;
			  case rstop:						// R停止
			   send_str6( );
			  stop_r();
			  break;
	     }
      
					 
	 }
	}
}				   
	