//可以完整接收处理字符串
#include <reg52.h>
#include "uart.h"
#include "stdlib.h"
#include "string.h"


typedef unsigned int u16;	  //对数据类型进行声明定义
typedef unsigned char u8;

char msg_buf[MAX_MSG_SZ];     //定义数组，储存返回语句


//串口初始化函数  9600
void ConfigUART(unsigned int baud)
{
//    SCON  = 0x50;  //配置串口为模式1 单片机串口通讯 基本都选方式1
//    TH1 = 256 - (11059200/12/32)/baud;  //计算定时器T1重载值	设置产生波特率的定时器T1
//    TL1 = TH1;     //初值等于重载值
//	ES = 1;			//串口 中断 开放
//	PS=1;       	//串口中断有最高优先级
//
//    TR1 = 1;       //启动定时器T1
//	TMOD &= 0x0F;  //清零T1的控制位
//    TMOD |= 0x20;  //配置T1为模式2
//	ET1 = 0;		// 定时器T1 中断  关闭

/*******************************************************************************************/

	TMOD = 0x21;                   //T0定时器为工作方式一，T1定时器为工作方式二
	PCON = 0x00;                   //串口初始化相关，波特率
	SCON = 0x50;                   //串口初始化相关，串口工作方式一，允许接收
	TH1  = 0xFD;                   //设置初值
	TL1  = 0xFD;                   //设置初值
    TH1 = 256 - (11059200/12/32)/baud;  //计算定时器T1重载值	设置产生波特率的定时器T1
    TL1 = TH1;     					//初值等于重载值
	TR1  = 1;                      //开启定时器T1
	
	
	TH0 = 0xF4;                    //设置初值
	TL0 = 0x48;                    //设置初值
	TR0 = 1;                       //开启定时器T0
	ES  = 1;                       //开放串口中断
	PT0 = 1;                       //定时器0中断优先
	ET0 = 1;                       //开放定时器T0中断
	ET1 = 0;						//关闭 定时器T1 中断  		因为工作方式二 无需打开定时器1 中断
	EA  = 1;                       //开放总中断

}
 
//发送一个字符
void Uart_Send_Byte(unsigned char dat)
{
 	SBUF = dat;
	while(!TI);
	TI=0;
}

//发送一串字符串
void Uart_Send_String(unsigned char *p)
{
 	while(*p != '\0')
	{
	 	Uart_Send_Byte(*p++);	
	} 
}

void process_ctrl_msg()
{
  char *tmp;                   // this is used by strtok() as an index
  tmp = strtok(msg_buf, ","); // replace delimiter with ","
  Uart_Send_String("P_l:");
  Uart_Send_String(tmp);
  ctrl_left = atoi(tmp);       // convert to int

  tmp = strtok(NULL, ",");    // continues where the previous call left off
  Uart_Send_String("P_r:");
  Uart_Send_String(tmp);
  ctrl_right = atoi(tmp);      // convert to int
     
  Uart_Send_String("\n");

}

bit Deal_UART_RecData()   //处理串口接收数据包函数（成功处理数据包则返回1，否则返回0）
{
	char head =  RX_DAT[0];
  	Uart_Send_String("m1:");	
	Uart_Send_String(RX_DAT);
	Uart_Send_String("\n");
	strncpy(msg_buf, RX_DAT+1, strlen(RX_DAT));
	Uart_Send_String("m2:");
	Uart_Send_String(msg_buf);
	Uart_Send_String("\n");
    switch (head)
  {

    case 'c':
      process_ctrl_msg();
      break;
//    case 'f':
//      process_feature_msg();
//      break;
//    case 'h':
//      process_heartbeat_msg();
//      break;
    default :
	  break;
	}
//  	Uart_Send_String("\r\nover\r\n");
//	memset(RX_DAT,0,sizeof(char)*MAX_MSG_SZ);//这时RX_DAT中的数据全都是0了
	RX_DAT[0] = '\0';
	RX_OVER=0;
    return 0;
}

u8 ch,i;
u8 RX_BUF[20],RX_DAT[20],RX_CNT=0,RX_OVER=0;
//串口接收中断
void Uart_IRQ() interrupt 4
{

 if(RI)
 {  
   ch=SBUF;
   if(ch!='\n')
   {
	  RX_BUF[RX_CNT++]=ch;
   }
   else	//结束
   {
	 for(i=0;i<RX_CNT;i++)
	 {
	  	RX_DAT[i]=RX_BUF[i];
	 }
	 RX_DAT[RX_CNT+1] = '\0';
	 RX_CNT=0;
	 RX_OVER=1;

	 Deal_UART_RecData();
   }
   RI=0;//清标志位 
 } 	 
}

