#include <reg52.h>                                  //包含51单片机相关的头文件
#define uint    unsigned int                          //重定义无符号整数类型
#define uchar   unsigned char                         //重定义无符号字符类型

uchar code  LedShowData[]={0x03,0x9F,0x25,0x0D,0x99,  //定义数码管显示数据
                            0x49,0x41,0x1F,0x01,0x19};//0,1,2,3,4,5,6,7,8,9

uchar code  RecvData[]={0x19,0x12,0x18,0x14,0x16,0x10,0x17,0x19,0x00,0x0F};

uchar IRCOM[7];

                                         
/*************完成电机控制端口定义***********/
sbit M1A=P1^2;                                        //定义电机1正向端口
sbit M1B=P1^3;                                        //定义电机1反向端口
sbit M2A=P1^4;                                        //定义电机2正向端口
sbit M2B=P1^5;                                        //定义电机2反向端口

sbit SB1=P2^3;                                        //定义蜂鸣端口
/*********完成话筒,光敏电阻,蜂鸣器.端口定义**/
sbit IRIN=P3^3;   //外部中断1                       //定义红外接收端口
/*********完成红外接收端口的定义*************/
#define ShowPort P0                                   //定义数码管显示端口

extern void ControlCar(uchar CarType);                //声明小车控制子程序

void delayms(unsigned char x)                         //0.14mS延时程序
{
  unsigned char i;                                    //定义临时变量
  while(x--)                                          //延时时间循环
  {
    for (i = 0; i<13; i++) {}                         //14mS延时
  }
}

void Delay1ms(unsigned int i) 
{ 
unsigned char j,k; 
do{ 
  j = 10; 
  do{ 
   k = 50; 
   do{ 
    _nop_(); 
   }while(--k);     
  }while(--j); 
}while(--i); 

} 
void Delay()                                          //定义延时子程序
{ uint DelayTime=30000;                               //定义延时时间变量
  while(DelayTime--);                                 //开始进行延时循环
  return;                                             //子程序返回
}

void stopM1()
{
   M1A=0;                                   //将M1电机A端初始化为0
   M1B=0;                                   //将M1电机B端初始化为0
  
}

void stopM2()
{
   M2A=0;                                   //将M2电机A端初始化为0
   M2B=0;                                   //将M2电机B端初始化为0
  
}

void tingzhi()
{
   M1A=0;                                   //将M1电机A端初始化为0
   M1B=0;                                   //将M1电机B端初始化为0
   M2A=0;                                   //将M2电机A端初始化为0
   M2B=0;

}
void qianjin()
{
                                     
   M2A=0;                                   
   M2B=1;
}

void houtui()
{
                                     
   M2A=1;                                   
   M2B=0;
}

void youzhuan()
{
   M1A=1;                                   
   M1B=0;                                   
  

}

void zuozhuan()
{
   M1A=0;                                   
   M1B=1;                                   
   

}

void ControlCar(unsigned char ConType)    //定义电机控制子程序
{
 
  
 switch(ConType)                          //判断用户设定电机形式
 {
  case 1:  //前进                         //判断用户是否选择形式1
  { 
    
	stopM2();						      //进入前进之前 先停止一段时间  防止电机反向电压冲击主板 导致系统复位
	 Delay1ms(240);
	qianjin();
	ShowPort=LedShowData[1];
    break;
  }
  case 2: //后退                              //判断用户是否选择形式2
  { 
    stopM2();							      //进入后退之前 先停止一段时间  防止电机反向电压冲击主板 导致系统复位
	  Delay1ms(240);
	houtui();
	ShowPort=LedShowData[2];                                //M2电机反转
    break;
  }
  case 3: //左转                              //判断用户是否选择形式3
  { 
    stopM1();								  //进入左转之前 先停止一段时间  防止电机反向电压冲击主板 导致系统复位
	 Delay1ms(240); 
	zuozhuan(); 
	ShowPort=LedShowData[3];                              //M2电机正转
	break;
  }
  case 4: //右转                              //判断用户是否选择形式4
  { 
    stopM1();								  //进入右转之前 先停止一段时间  防止电机反向电压冲击主板 导致系统复位
	  Delay1ms(240);
	youzhuan();                                //M1电机正转
    ShowPort=LedShowData[4];                                      //M2电机反转
	break;
  }
  case 5: //停止                          //判断用户是否选择形式8
  {
    tingzhi();
	ShowPort=LedShowData[0]; 
	break; 
	                              //退出当前选择
  }
   case 6: //前后停止                          //判断用户是否选择形式8
  {
    stopM2();
	ShowPort=LedShowData[6]; 
	break; 
	                              //退出当前选择
  }
   case 7: //左右停止                          //判断用户是否选择形式8
  {
    stopM1();
	ShowPort=LedShowData[7]; 
	break; 
	                              //退出当前选择
  }
 }
}

void IR_IN() interrupt 2 using 0                      //定义INT2外部中断函数
{
  unsigned char j,k,N=0;                              //定义临时接收变量
   
  EX1 = 0;                                            //关闭外部中断,防止再有信号到达   
  delayms(15);                                        //延时时间，进行红外消抖
  if (IRIN==1)                                        //判断红外信号是否消失
  {  
     EX1 =1;                                          //外部中断开
	 return;                                          //返回
  } 
                           
  while (!IRIN)                                       //等IR变为高电平，跳过9ms的前导低电平信号。
  {
      delayms(1);                                     //延时等待
  }

  for (j=0;j<4;j++)                                   //采集红外遥控器数据
  { 
    for (k=0;k<8;k++)                                 //分次采集8位数据
    {
       while (IRIN)                                   //等 IR 变为低电平，跳过4.5ms的前导高电平信号。
       {
         delayms(1);                                  //延时等待
       }
       
       while (!IRIN)                                  //等 IR 变为高电平
       {
         delayms(1);                                  //延时等待
       }
   
       while (IRIN)                                   //计算IR高电平时长
       {
         delayms(1);                                  //延时等待
         N++;                                         //计数器加加
         if (N>=30)                                   //判断计数器累加值
	     { 
           EX1=1;                                     //打开外部中断功能
	       return;                                    //返回
         }                   
       }
                                       
      IRCOM[j]=IRCOM[j] >> 1;                         //进行数据位移操作并自动补零
     
      if (N>=8)                                       //判断数据长度 
      {
         IRCOM[j] = IRCOM[j] | 0x80;                  //数据最高位补1
      } 
      N=0;                                            //清零位数计录器
    }
  }
   
  if (IRCOM[2]!=~IRCOM[3])                            //判断地址码是否相同
  { 
     EX1=1;                                           //打开外部中断
     return;                                          //返回
  }

   ShowPort=IRCOM[2];//显示收到的数值
  for(j=0;j<10;j++)                                   //循环进行键码解析
   {
      if(IRCOM[2]==RecvData[j])                       //进行键位对应
      {
        ControlCar(j);                                          //数码管显示相应数码
      }
   }
   
   
   EX1 = 1;                                           //外部中断开 
} 



void main(void)                                       //主程序入口
{
 bit ExeFlag=0;                                       //定义可执行位变量
 
 LedFlash=3000;                                       //对闪灯数据进行初始化
 EX1=1;                                               //同意开启外部中断1
 IT1=1;                                               //设定外部中断1为低边缘触发类型
 EA=1;                                                //总中断开启
 ShowPort=LedShowData[0];                             //数码管显示数字0
while(1)                                              //程序主循环
 {
   
   Delay();                                           //延时

  
 }
}
