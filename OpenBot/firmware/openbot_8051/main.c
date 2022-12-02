/* C51 for OpenBot 1.22*/
//串口分别控制2个pwm 	
//有待完善 因各种软硬件问题导致两个电机速度不一致	   
//有时间看看 直流电机控制 pwm 和 pid 算法	https://blog.csdn.net/luzire/article/details/83144381
#include<AT89X52.H>		      //包含51单片机头文件，内部有各种寄存器定义
//#include <reg52.h>
#include"uart.h"
#define SPEED 128	   //速度调节变量 0-255。。。0最小，255最大
#define HIGH 20		  //threshold value 阈值
#define LOW -HIGH		  //threshold value 阈值
#define HZ 100	// 计数 约0.1ms 一次中断
    #define Left_1_led        P3_4	 //左传感器  
	
    #define Right_1_led       P3_5	 //右传感器    
   
//	#define Left_moto_pwm	  P1_6	 //PWM信号端
//
//	#define Right_moto_pwm	  P1_7	 //PWM信号端
	
	#define Left_moto_front   {P1_2=0,P1_3=1;}  //左电机向前走
	#define Left_moto_back    {P1_2=1,P1_3=0;} 	//左边电机向后转
	#define Left_moto_stop    {P1_2=0,P1_3=0;}         //左边电机停转                     
	#define Right_moto_front  {P1_4=0,P1_5=1;}	//右边电机向前走
	#define Right_moto_back   {P1_4=1,P1_5=0;}	//右边电机向后走
	#define Right_moto_stop   {P1_4=0,P1_5=0;}      	//右边电机停转
//L293D引脚定义      
//sbit in1 = P1^2;	 //如果左右电机位置不对，34 和 12 互换位置
//sbit in2 = P1^3;
//sbit in3 = P1^4;
//sbit in4 = P1^5;
//sbit ena = P0^7; 	 //	这俩我没按引脚顺序连杜邦线，我说怎么分别控制2个pwm一直调不对，长个记性，下回连接引脚也检查下
//sbit enb = P0^6;      
sbit ena = P1^6;	
sbit enb = P1^7;	
 
int ctrl_left  = 0;
int ctrl_right = 0;
uint PWMA = 20;         
uint PWMB = 20; 
unsigned char MA = 0,MB = 0;           //pwm控制用


/************************************************************************/	
//延时函数	
   void delay(unsigned int k)
{    
     unsigned int x,y;
	 for(x=0;x<k;x++) 
	   for(y=0;y<2000;y++);
}

//void ConfigPWM(long cl, long cr)
//{
//	PWMA = cl;
//	PWMB = cr;
//}

void update_right_motors()
{
  if (ctrl_right < LOW)
  {
	if(ctrl_right < -255) ctrl_right = -255;//超范围数据处理
  	Right_moto_back;
	PWMB = -ctrl_right;
	
  }
  else if (ctrl_right > HIGH)
  {
  	if(ctrl_right > 255) ctrl_right = 255;//超范围数据处理
    Right_moto_front;  
	PWMB = ctrl_right;
  }
  else
  {	
    Right_moto_stop;
	PWMB = 20;
  }
}


void update_left_motors()
{
  if (ctrl_left < LOW)
  {
	if(ctrl_left < -255) ctrl_left = -255;//超范围数据处理
    Left_moto_back;
	PWMA = -ctrl_left;
  }
  else if (ctrl_left > HIGH)
  {
  	if(ctrl_left > 255) ctrl_left = 255;//超范围数据处理
    Left_moto_front;
	PWMA = ctrl_left;
  }
  else
  {
	Left_moto_stop;
	PWMA = 20;
  }
}

/************************************************************************/
//停止
     void  stop(void)
{
     ctrl_left = 0;	 //速度调节变量 0-255。。。0最小，255最大
	 ctrl_right = 0;
	 update_left_motors();
   	 update_right_motors();
}
//前进
     void  run(void)
{
     ctrl_left = SPEED;	 //速度调节变量 0-255。。。0最小，255最大
	 ctrl_right = SPEED;
	 update_left_motors();
   	 update_right_motors();
}

//后退 
     void  backrun(void)
{
     ctrl_left = -SPEED;	 
	 ctrl_right = -SPEED;
	 update_left_motors();
   	 update_right_motors();
}

//左转
     void  leftrun(void)
{	 
     ctrl_left = -SPEED;	 
	 ctrl_right = SPEED;
	 update_left_motors();
   	 update_right_motors();
}

//右转
     void  rightrun(void)
{ 
	 ctrl_left = SPEED;	 
	 ctrl_right = -SPEED;
	 update_left_motors();
   	 update_right_motors();	
}

//主函数 
void main()
{   
	EA=1;
    ConfigUART(9600);  //配置波特率为9600
	//ConfigUART(115200);  //配置波特率为115200
	Uart_Send_String("8051 for OpenBot 1.22 \r\n");//OpenBot手机端接收未规定的命令头会报错,我解决了手机端USB接收异常数据的处理，也可以把这里的串口输出注释掉
	P1=0X00; //关电车电机

    while (1)
    {
//	  ConfigPWM(ctrl_left,ctrl_right);//pwm测试	 

	if(Left_1_led==1&&Right_1_led==1){//红外避障 有信号为0  没有信号为1
	  update_left_motors();
   	  update_right_motors();
	 }else{			  
		if(Left_1_led==1&&Right_1_led==0)	    //右边检测到红外信号
	 	 {
		 	   leftrun();	  //调用小车左转函数
			   delay(40);

	     }
	   
		if(Right_1_led==1&&Left_1_led==0)		//左边检测到红外信号
		  {	  
		      
			 
			   rightrun();	 //调用小车右转函数
			  delay(40);

		  }
		if(Right_1_led==0&&Left_1_led==0)		//两边传感器同时检测到红外
		  {	  
		    backrun();		//调用电机后退函数
			delay(40);		//后退050毫秒
			rightrun();		//调用电机右转函数
			delay(90);
		  }

		  stop(); //关电车电机
	}
//	  Uart_Send_String(" PWMA:");
//	  Uart_Send_Byte(PWMA/100+0x30);	//百位
//	  Uart_Send_Byte((PWMA-PWMA/100*100)/10+0x30);	 //十位
//	  Uart_Send_Byte(PWMA%10+0x30);					//	 个位
//	  Uart_Send_String(" PWMB:");
//	  Uart_Send_Byte(PWMB/100+0x30);	//百位
//	  Uart_Send_Byte((PWMB-PWMB/100*100)/10+0x30);	 //十位
//	  Uart_Send_Byte(PWMB%10+0x30);					//	 个位
//	  Uart_Send_String("\r\n");
	delay(1);
	}
}
 

void InterruptTimer0() interrupt 1
{
TR0 = 0;  
			    
	TH0 = (65536-HZ)/256;			   //65536 = ffff = 16位
	TL0 = (65536-HZ)%256;			   //256 ff 8位

MB++;         
if(MB < PWMB)  
{   
enb = 1;                             //使用enb来产生pwm波控制B端电机
}  
else
  enb = 0;  
if(MB == 255)
{   
  MB = 0;  
}

MA++;         
if(MA < PWMA)  
{   
ena = 1;                                 //使用ena来产生pwm波控制A端电机
}  
else
  ena = 0;  
if(MA == 255)
{   
  MA = 0;  
}  

TR0 = 1;

}
