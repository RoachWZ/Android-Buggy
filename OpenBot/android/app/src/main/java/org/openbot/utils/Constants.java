package org.openbot.utils;

import android.Manifest;

public class Constants {

  public static final String USB_ACTION_DATA_RECEIVED = "usb.data_received";

  public static final int REQUEST_CAMERA_PERMISSION = 1;
  public static final int REQUEST_AUDIO_PERMISSION = 2;
  public static final int REQUEST_STORAGE_PERMISSION = 3;
  public static final int REQUEST_LOCATION_PERMISSION = 4;
  public static final int REQUEST_BLUETOOTH_PERMISSION = 5;
  public static final int REQUEST_LOGGING_PERMISSIONS = 6;
  public static final int REQUEST_CONTROLLER_PERMISSIONS = 7;
  public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
  public static final String PERMISSION_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
  public static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
  public static final String PERMISSION_BLUETOOTH = Manifest.permission.BLUETOOTH;
  public static final String PERMISSION_AUDIO = Manifest.permission.RECORD_AUDIO;

  public static final String[] PERMISSIONS_LOGGING =
      new String[] {PERMISSION_CAMERA, PERMISSION_STORAGE, PERMISSION_LOCATION};
  public static final String[] PERMISSIONS_CONTROLLER =
      new String[] {PERMISSION_CAMERA, PERMISSION_AUDIO, PERMISSION_LOCATION};

  public static final String GENERIC_MOTION_EVENT = "dispatchGenericMotionEvent";
  public static final String KEY_EVENT = "dispatchKeyEvent";
  public static final String DATA = "data";

  public static final String KEY_EVENT_CONTINUOUS = "dispatchKeyEvent_continuous";
  public static final String DATA_CONTINUOUS = "data_continuous";

  // Controller Commands
  public static final String CMD_DRIVE = "DRIVE_CMD";
  public static final String CMD_LOGS = "LOGS";
  public static final String CMD_NOISE = "NOISE";
  public static final String CMD_INDICATOR_LEFT = "INDICATOR_LEFT";
  public static final String CMD_INDICATOR_RIGHT = "INDICATOR_RIGHT";
  public static final String CMD_INDICATOR_STOP = "INDICATOR_STOP";
  public static final String CMD_NETWORK = "NETWORK";
  public static final String CMD_DRIVE_MODE = "DRIVE_MODE";
  public static final String CMD_CONNECTED = "CONNECTED";
  public static final String CMD_DISCONNECTED = "DISCONNECTED";
  public static final String CMD_SPEED_UP = "SPEED_UP";
  public static final String CMD_SPEED_DOWN = "SPEED_DOWN";
  // endregion

  //add by wangzheng 2022-12-22
  public static final int OBJ_DETECTED = 0;
  public static final int BT_CONNECTED_FAILED = 1;
  public static final int BT_CONNECTED = 2;
  public static final int BT_DISCONNECTED = 3;
  public static final int SCENE_IDLE = 4;
  public static final int SCENE_COLOR = 5;
  public static final int SCENE_FACE = 6;
  public static final int SOCKET_MSG = 7;

  //add by wangzheng 2022-08-29
  //下面的数组是一种交替的载波序列模式，通过毫秒测量
  //引导码，地址码，地址码，数据码，数据反码
  //第三行数据码反置，比如0x12=0001 0010反置为 0100 1000
//以下 方向 是以前置摄像头为准 本程序是用后置摄像头，调用时注意方向
  //停止 0x10
  public static final int[] patternS = { 9000, 4500,
          560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
          560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
          /*0000 1000*/560, 560,	560, 560, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
          560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690,
          560, 42020, 9000, 2250, 560, 98190 };
  //前进 0x12
  public static final int[] pattern1 = { 9000, 4500,
          560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
          560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
          /*0100 1000*/560, 560,	560, 1690, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
          560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560,1690, 	560, 1690, 	560, 1690,
          560, 42020, 9000, 2250, 560, 98190 };
  //后退 0x18
  public static final int[] pattern2 = { 9000, 4500,
          560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
          560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
          /*0001 1000*/560, 560,	560, 560, 	560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
          560, 1690, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690,
          560, 42020, 9000, 2250, 560, 98190 };
  //左转 0x14
  public static final int[] pattern3 = { 9000, 4500,
          560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
          560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
          /*0010 1000*/560, 560,	560, 560, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
          560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690,
          560, 42020, 9000, 2250, 560, 98190 };
  //右转 0x16
  public static final int[] pattern4 = { 9000, 4500,
          560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
          560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
          /*0110 1000*/560, 560,	560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
          560, 1690, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 1690,	560, 1690, 	560, 1690,
          560, 42020, 9000, 2250, 560, 98190 };
  //左自转 0x17
  public static final int[] pattern5 = { 9000, 4500,
          560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
          560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
          /*1110 1000*/560, 1690,	560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
          560, 560, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690,
          560, 42020, 9000, 2250, 560, 98190 };
  //右自转 0x19
  public static final int[] pattern6 = { 9000, 4500,
          560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
          560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
          /*1001 1000*/560, 1690,	560, 560, 	560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
          560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560,1690, 	560, 1690, 	560, 1690,
          560, 42020, 9000, 2250, 560, 98190 };
  //全速  0x01
  public static final int[] speed0 = { 9000, 4500,
          560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
          560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
          /*1000 0000*/560,1690,	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
          560,  560, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
          560, 42020, 9000, 2250, 560, 98190 };
  //速度1 0x00
  public static final int[] speed1 = { 9000, 4500,
          560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
          560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
          /*0000 0000*/560, 560,	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
          560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
          560, 42020, 9000, 2250, 560, 98190 };
  //速度2 0x02
  public static final int[] speed2 = { 9000, 4500,
          560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
          560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
          /*0100 0000*/560, 560,	560, 1690, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
          560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690,
          560, 42020, 9000, 2250, 560, 98190 };
  //速度3 0x03
  public static final int[] speed3 = { 9000, 4500,
          560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
          560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
          /*1100 0000*/560, 1690,	560, 1690, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
          560, 560, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690,
          560, 42020, 9000, 2250, 560, 98190 };
}
