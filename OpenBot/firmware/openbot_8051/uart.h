#ifndef  __UART_H__
#define  __UART_H__
#define MAX_MSG_SZ 20
#define uint unsigned int
#define uchar unsigned char

extern unsigned char RX_DAT[20],RX_OVER;
extern unsigned char i;
//Vehicle Control
extern int ctrl_left ;
extern int ctrl_right ; 
//º¯Êý
void ConfigUART(unsigned int baud);
void Uart_Send_Byte(unsigned char dat);
void Uart_Send_String(unsigned char *p);
void Uart_IRQ();

#endif
