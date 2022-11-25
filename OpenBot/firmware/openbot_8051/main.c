/* C51 for OpenBot 1.22*/
//串口分别控制2个pwm 	
//有待完善 因各种软硬件问题导致两个电机速度不一致	   
//有时间看看 直流电机控制 pwm 和 pid 算法	https://blog.csdn.net/luzire/article/details/83144381
#include <reg52.h>
#include"uart.h"
#define HIGH 1		  //threshold value 阈值
#define LOW -HIGH		  //threshold value 阈值
#define HZ 100	// 计数 约0.1ms 一次中断

//L293D引脚定义      
sbit in3 = P1^2;	 //如果左右电机位置不对，34 和 12 互换位置
sbit in4 = P1^3;
sbit in1 = P1^4;
sbit in2 = P1^5;
//sbit ena = P0^7; 	 //	这俩我没按引脚顺序连，我说怎么分别控制2个pwm一直调不对，长个记性，下回连接引脚也检查下
//sbit enb = P0^6;     // 就这顺序了，杜邦线已经生锈在上边了，不好换位置了   
sbit ena = P1^6;	//引脚换了个位置
sbit enb = P1^7;
 
int ctrl_left  = 0;
int ctrl_right = 0;
uint PWMA = 20;         
uint PWMB = 20; 
unsigned char MA = 0,MB = 0;           //pwm控制用

void delay(unsigned int n){ while (n--);}


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
  	in3=1;  
	in4=0;
	PWMB = -ctrl_right;
	
  }
  else if (ctrl_right > HIGH)
  {
  	if(ctrl_right > 255) ctrl_right = 255;//超范围数据处理
    in3=0;  
	in4=1;
	PWMB = ctrl_right;
  }
  else
  {	//stop_left_motors
    in3=0;  
    in4=0;
  }
}


void update_left_motors()
{
  if (ctrl_left < LOW)
  {
	if(ctrl_left < -255) ctrl_left = -255;//超范围数据处理
    in1=1;  
	in2=0;
	PWMA = -ctrl_left;
  }
  else if (ctrl_left > HIGH)
  {
  	if(ctrl_left > 255) ctrl_left = 255;//超范围数据处理
    in1=0;  
	in2=1;
	PWMA = ctrl_left;
  }
  else
  {
    //stop_right_motors
	in1=0;  
    in2=0;
  }
}

//主函数 
void main()
{   
	EA=1;
    ConfigUART(9600);  //配置波特率为9600
	Uart_Send_String("8051 for OpenBot 1.22 \r\n");//OpenBot手机端接收未规定的命令头会报错,我解决了手机端USB接收异常数据的处理，也可以把这里的串口输出注释掉
	in1=1;  
	in2=1;
	in3=1;  
	in4=1;
    while (1)
    {
//	  ConfigPWM(ctrl_left,ctrl_right);//pwm测试	 

	  update_left_motors();
   	  update_right_motors();

//	  Uart_Send_String("PWMA:");
//	  Uart_Send_Byte(PWMA/100+0x30);	//百位
//	  Uart_Send_Byte((PWMA-PWMA/100*100)/10+0x30);	 //十位
//	  Uart_Send_Byte(PWMA%10+0x30);					//	 个位
//	  Uart_Send_String("\r\n");
	delay(2000);
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
