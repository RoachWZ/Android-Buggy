// Created by wangzheng  2022-08-29
//红外遥控
package org.openbot.vehicle;

import android.hardware.ConsumerIrManager;

import org.openbot.env.Logger;
import org.openbot.utils.Constants;

public class IrConnection {
  private static final Logger LOGGER = new Logger();
  private static ConsumerIrManager mCIR;
  private static int hz = 38000;

  String TAG = "IrConnection";
  public IrConnection(ConsumerIrManager mCIR) {
    this.mCIR = mCIR;
    LOGGER.d(TAG,"ConsumerIrManager is "+mCIR);
  }

  public static void send(int left, int right) {
    if(left>0&&right>0){
      if((left-right)>20){
        left();
      }else if ((left-right)<20){
        right();
      }else{
        forward();
      }
    }else if (left<0&&right<0){
      if((left-right)>20){
        left();
      }else if ((left-right)<20){
        right();
      }else{
        backward();
      }
    }else if (left<0&&right>0){
      left_0();
    }else if (left>0&&right<0){
      right_0();
    }else{
      stop();
    }


  }

  //发送红外信号
  public static void stop(){
    mCIR.transmit(hz, Constants.patternS);
  }
  public static void forward(){
    mCIR.transmit(hz, Constants.pattern1);
  }
  public static void backward(){
    mCIR.transmit(hz, Constants.pattern2);
  }
  public static void left(){
    mCIR.transmit(hz, Constants.pattern3);
  }
  public static void right(){
    mCIR.transmit(hz, Constants.pattern4);
  }
  public static void left_0(){
    mCIR.transmit(hz, Constants.pattern5);
  }
  public static void right_0(){
    mCIR.transmit(hz, Constants.pattern6);
  }
  public static void speed_0(){
    mCIR.transmit(hz, Constants.speed0);
  }
  public static void speed_1(){
    mCIR.transmit(hz, Constants.speed1);
  }
}
