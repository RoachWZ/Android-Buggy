// Created by wangzheng  2022-08-29
//todo ble蓝牙遥控
package org.openbot.vehicle;

import static java.nio.charset.StandardCharsets.UTF_8;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.openbot.env.Logger;
import org.openbot.main.MainActivity;
import org.openbot.utils.Constants;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class BluetoothConnection {
  private static final Logger LOGGER = new Logger();

  private boolean busy;

  private final Context context;

  public BluetoothSPP SPP;

  public BluetoothConnection(Context context) {

    this.context = context;

    this.SPP = new BluetoothSPP(context);

    //蓝牙接收数据监听器
    SPP.setOnDataReceivedListener(
            new BluetoothSPP.OnDataReceivedListener()
            {
              public void onDataReceived(byte[] data, String message)
              {
//                if (message.indexOf("S") > -1)
//                {
//                  Toast.makeText(context, "请说话", Toast.LENGTH_SHORT).show();
//                }
                LOGGER.d("Bluetooth data received: " + message);
              }
            }

    );

    init();
    onStart();
  }

  public void init()
  {
    if (!SPP.isBluetoothAvailable())
    {
      Toast.makeText(context
              , "蓝牙不可用"
              , Toast.LENGTH_SHORT).show();
    }

    SPP.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener()
    {
      public void onDeviceConnected(String name, String address)
      {
        Toast.makeText(context
                , "已连接 " + name + "\n" + address
                , Toast.LENGTH_SHORT).show();
        Message msg = new Message();
        msg.what = Constants.BT_CONNECTED;
        msg.obj = name + "\n" + address;
        MainActivity.mHandler.sendMessage(msg);
      }

      public void onDeviceDisconnected()
      {
        Toast.makeText(context
                , "已断开连接", Toast.LENGTH_SHORT).show();
        Message msg = new Message();
        msg.what = Constants.BT_DISCONNECTED;
        MainActivity.mHandler.sendMessage(msg);
      }

      public void onDeviceConnectionFailed()
      {
        Toast.makeText(context
                , "连接失败", Toast.LENGTH_SHORT).show();
        Message msg = new Message();
        msg.what = Constants.BT_CONNECTED_FAILED;
        MainActivity.mHandler.sendMessage(msg);
      }
    });
  }

  public void send(String data)
  {
//    SPP.send(data, false);
//    Log.d("bluetoothSend: ",data);

    if (isOpen() && !isBusy()) {
      busy = true;
      SPP.send(data, false);
      LOGGER.d("Bluetooth data send: " + data);

      long start = System.currentTimeMillis( );//LOGGER.i("start time is "+ start);
      long end ;
      while(busy){
        end = System.currentTimeMillis( );
        if(end - start >100) {
          busy = false;//Ironbot要间隔100毫秒 再小就反应不过来了
          //LOGGER.i("end time is "+ end);
        }
      }//并没有起作用，得好好研究下Java线程编程。知道咋回事了，在上一层调用加线程控制，配合此处修改可间隔100毫秒。
//      busy = false;
    }else if (isBusy()) {
      LOGGER.d("Bluetooth is busy !");
    }
  }

  public boolean isOpen() {
    return SPP.getServiceState() == BluetoothState.STATE_CONNECTED;
  }

  public boolean isBusy() {
    return busy;
  }

  public void getDeviceList(Activity activity_now)
  {
    if (SPP.getServiceState() == BluetoothState.STATE_CONNECTED)
    {
      SPP.disconnect();
    } else
    {
      Intent intent = new Intent(activity_now.getApplicationContext(), DeviceList.class);
      activity_now.startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
    }
  }

  public void onDeviceListReturn(int requestCode, int resultCode, Intent data)
  {
//    LOGGER.d("Bluetooth device is "+data.getExtras().getString(BluetoothState.EXTRA_DEVICE_ADDRESS));

    if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE)
    {
      if (resultCode == Activity.RESULT_OK)
        SPP.connect(data);
      LOGGER.d("Bluetooth is connecting !");

    } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT)
    {
      if (resultCode == Activity.RESULT_OK)
      {
        SPP.setupService();
        SPP.startService(BluetoothState.DEVICE_ANDROID);
      } else
      {
        Toast.makeText(context
                , "Bluetooth was not enabled."
                , Toast.LENGTH_SHORT).show();
      }
    }
  }

  public boolean startBluetoothConnection() {
    boolean success = false;

    return success;
  }


  public void stopBluetoothConnection() {
    onDestroy();
  }

  public void onStart()
  {
    if (!SPP.isBluetoothEnabled())
    {
      //
    } else
    {
      if (!SPP.isServiceAvailable())
      {
        SPP.setupService();
        SPP.startService(BluetoothState.DEVICE_OTHER);
      }
    }
  }

  public void onDestroy()
  {
    SPP.stopService();
  }

  public String getProductName() {
    return SPP.getConnectedDeviceName() + "\n" + SPP.getConnectedDeviceAddress();
  }
}
