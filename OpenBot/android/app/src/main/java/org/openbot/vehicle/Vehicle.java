package org.openbot.vehicle;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.openbot.env.GameController;
import org.openbot.env.SensorReading;
import org.openbot.utils.Enums;

public class Vehicle implements SensorEventListener{

  private final Noise noise = new Noise(1000, 2000, 5000);
  private boolean noiseEnabled = false;

  private int indicator = 0;
//  private int speedMultiplier = 192; // 128,192,255
  private int speedMultiplier = 30; // 10,30,90 add by wangzheng 2022-08-30 适配ironbot所使用的舵机
  private Control control = new Control(0, 0);

  private final SensorReading batteryVoltage = new SensorReading();
  private final SensorReading leftWheelRpm = new SensorReading();
  private final SensorReading rightWheelRpm = new SensorReading();
  private final SensorReading sonarReading = new SensorReading();

  private float minMotorVoltage = 2.5f;
  private float lowBatteryVoltage = 9.0f;
  private float maxBatteryVoltage = 12.6f;

  private BluetoothConnection bluetoothConnection;
  protected boolean bluetoothConnected;

  private UsbConnection usbConnection;
  protected boolean usbConnected;
  private final Context context;
  private final int baudRate;

  private String vehicleType = "RTR_V1";
  private boolean hasVoltageDivider = false;
  private boolean hasIndicators = false;
  private boolean hasSonar = false;
  private boolean hasBumpSensor = false;
  private boolean hasWheelOdometryFront = false;
  private boolean hasWheelOdometryBack = false;
  private boolean hasLedsFront = false;
  private boolean hasLedsBack = false;
  private boolean hasLedsStatus = false;
  private boolean IndicatorsSelected = false;

  private Sensor sensor;
  public SensorManager sm;
  private SensorEvent event;

  public float getMinMotorVoltage() {
    return minMotorVoltage;
  }

  public void setMinMotorVoltage(float minMotorVoltage) {
    this.minMotorVoltage = minMotorVoltage;
  }

  public float getLowBatteryVoltage() {
    return lowBatteryVoltage;
  }

  public void setLowBatteryVoltage(float lowBatteryVoltage) {
    this.lowBatteryVoltage = lowBatteryVoltage;
  }

  public float getMaxBatteryVoltage() {
    return maxBatteryVoltage;
  }

  public void setMaxBatteryVoltage(float maxBatteryVoltage) {
    this.maxBatteryVoltage = maxBatteryVoltage;
  }

  public boolean isHasVoltageDivider() {
    return hasVoltageDivider;
  }

  public void setHasVoltageDivider(boolean hasVoltageDivider) {
    this.hasVoltageDivider = hasVoltageDivider;
  }

  public boolean isHasIndicators() {
    return hasIndicators;
  }

  public void setHasIndicators(boolean hasIndicators) {
    this.hasIndicators = hasIndicators;
  }

  public boolean isHasSonar() {
    return hasSonar;
  }

  public void setHasSonar(boolean hasSonar) {
    this.hasSonar = hasSonar;
  }

  public boolean isHasBumpSensor() {
    return hasBumpSensor;
  }

  public void setHasBumpSensor(boolean hasBumpSensor) {
    this.hasBumpSensor = hasBumpSensor;
  }

  public boolean isHasWheelOdometryFront() {
    return hasWheelOdometryFront;
  }

  public void setHasWheelOdometryFront(boolean hasWheelOdometryFront) {
    this.hasWheelOdometryFront = hasWheelOdometryFront;
  }

  public boolean isHasWheelOdometryBack() {
    return hasWheelOdometryBack;
  }

  public void setHasWheelOdometryBack(boolean hasWheelOdometryBack) {
    this.hasWheelOdometryBack = hasWheelOdometryBack;
  }

  public boolean isHasLedsFront() {
    return hasLedsFront;
  }

  public void setHasLedsFront(boolean hasLedsFront) {
    this.hasLedsFront = hasLedsFront;
  }

  public boolean isHasLedsBack() {
    return hasLedsBack;
  }

  public void setHasLedsBack(boolean hasLedsBack) {
    this.hasLedsBack = hasLedsBack;
  }

  public boolean isHasLedsStatus() {
    return hasLedsStatus;
  }

  public void setHasLedsStatus(boolean hasLedsStatus) {
    this.hasLedsStatus = hasLedsStatus;
  }

  public String getVehicleType() {
    return vehicleType;
  }

  public void setVehicleType(String vehicleType) {
    this.vehicleType = vehicleType;
  }

  public void requestVehicleConfig() {
    sendStringToUsb(String.format(Locale.US, "f\n"));
  }

  public void processVehicleConfig(String message) {

    setVehicleType(message.split(":")[0]);

    if (message.contains(":v:")) {
      setHasVoltageDivider(true);
      setVoltageFrequency(250);
    }
    if (message.contains(":i:")) {
      setHasIndicators(true);
    }
    if (message.contains(":s:")) {
      setHasSonar(true);
      setSonarFrequency(100);
    }
    if (message.contains(":b:")) {
      setHasBumpSensor(true);
    }
    if (message.contains(":wf:")) {
      setHasWheelOdometryFront(true);
      setWheelOdometryFrequency(500);
    }
    if (message.contains(":wb:")) {
      setHasWheelOdometryBack(true);
      setWheelOdometryFrequency(500);
    }
    if (message.contains(":lf:")) {
      setHasLedsFront(true);
    }
    if (message.contains(":lb:")) {
      setHasLedsBack(true);
    }
    if (message.contains(":ls:")) {
      setHasLedsBack(true);
    }
  }

  protected Enums.DriveMode driveMode = Enums.DriveMode.GAME;
  private final GameController gameController;
  private Timer heartbeatTimer;

  public Vehicle(Context context, int baudRate) {
    this.context = context;
    this.baudRate = baudRate;
    gameController = new GameController(driveMode);
//    initSensorPROXIMITY();//wz
  }

  public float getBatteryVoltage() {
    return batteryVoltage.getReading();
  }

  public int getBatteryPercentage() {
    return (int)
        ((batteryVoltage.getReading() - lowBatteryVoltage)
            * 100
            / (maxBatteryVoltage - lowBatteryVoltage));
  }

  public void setBatteryVoltage(float batteryVoltage) {
    this.batteryVoltage.setReading(batteryVoltage);
  }

  public float getLeftWheelRpm() {
    return leftWheelRpm.getReading();
  }

  public void setLeftWheelRpm(float leftWheelRpm) {
    this.leftWheelRpm.setReading(leftWheelRpm);
  }

  public float getRightWheelRpm() {
    return rightWheelRpm.getReading();
  }

  public void setRightWheelRpm(float rightWheelRpm) {
    this.rightWheelRpm.setReading(rightWheelRpm);
  }

  public float getRotation() {
    float rotation = (getLeftSpeed() - getRightSpeed()) * 180 / (getLeftSpeed() + getRightSpeed());
    if (Float.isNaN(rotation) || Float.isInfinite(rotation)) rotation = 0f;
    return rotation;
  }

  public int getSpeedPercent() {
    float throttle = (getLeftSpeed() + getRightSpeed()) / 2;
    return Math.abs((int) (throttle * 100 / 255)); // 255 is the max speed
  }

  public String getDriveGear() {
    float throttle = (getLeftSpeed() + getRightSpeed()) / 2;
    if (throttle > 0) return "D";
    if (throttle < 0) return "R";
    return "P";
  }

  public float getSonarReading() {
    return sonarReading.getReading();
  }

  public void setSonarReading(float sonarReading) {
    this.sonarReading.setReading(sonarReading);
  }

  public Control getControl() {
    return control;
  }

  public void setControl(Control control) {
    this.control = control;
    sendControl();
  }

  public void setControl(float left, float right) {
    this.control = new Control(left, right);
    sendControl();
  }

  private Timer noiseTimer;

  public void toggleNoise() {
    if (noiseEnabled) stopNoise();
    else startNoise();
  }

  public boolean isNoiseEnabled() {
    return noiseEnabled;
  }

  public void setDriveMode(Enums.DriveMode driveMode) {
    this.driveMode = driveMode;
    gameController.setDriveMode(driveMode);
  }

  public Enums.DriveMode getDriveMode() {
    return driveMode;
  }

  public GameController getGameController() {
    return gameController;
  }

  private class NoiseTask extends TimerTask {
    @Override
    public void run() {
      noise.update();
      sendControl();
    }
  }

  public void startNoise() {
    noiseTimer = new Timer();
    NoiseTask noiseTask = new NoiseTask();
    noiseTimer.schedule(noiseTask, 0, 50); // no delay 50ms intervals
    noiseEnabled = true;
  }

  public void stopNoise() {
    noiseEnabled = false;
    noiseTimer.cancel();
    sendControl();
  }

  public int getSpeedMultiplier() {
    return speedMultiplier;
  }

  public void setSpeedMultiplier(int speedMultiplier) {
    this.speedMultiplier = speedMultiplier;
  }

  public int getIndicator() {
    return indicator;
  }

  //Indicator转向指示灯
  public void setIndicator(int indicator) {
    this.indicator = indicator;
    switch (indicator) {
      case -1:
//        sendStringToUsb(String.format(Locale.US, "i1,0\n"));
        sendStringToBluetooth(String.format(Locale.US, "#B0,255,0,*"));
        sendStringToUsb(String.format(Locale.US, "#B0,255,0,*"));
        IndicatorsSelected = false;
        break;
      case 0:
//        sendStringToUsb(String.format(Locale.US, "i0,0\n"));
        if(IndicatorsSelected){
          sendStringToBluetooth(String.format(Locale.US, "#B0,0,0,*"));
          sendStringToUsb(String.format(Locale.US, "#B0,0,0,*"));
          IndicatorsSelected = false;
        }else {
          sendStringToBluetooth(String.format(Locale.US, "#B255,0,0,*"));
          sendStringToUsb(String.format(Locale.US, "#B255,0,0,*"));
          IndicatorsSelected = true;
        }
        break;
      case 1:
//        sendStringToUsb(String.format(Locale.US, "i0,1\n"));
        sendStringToBluetooth(String.format(Locale.US, "#B0,0,255,*"));
        sendStringToUsb(String.format(Locale.US, "#B0,0,255,*"));
        IndicatorsSelected = false;
        break;
    }
  }

  /*****************************Bluetooth************************************/
  public BluetoothConnection getBluetoothConnection() {
    return bluetoothConnection;
  }
  public void connectBluetooth(Activity activity_now) {
    if (bluetoothConnection == null) bluetoothConnection = new BluetoothConnection(context);
    bluetoothConnection.getDeviceList(activity_now);

  }
  public void disconnectBluetooth() {
    if (bluetoothConnection != null) {
      stopBot();
      stopHeartbeat();
      bluetoothConnection.stopBluetoothConnection();
      bluetoothConnection = null;
      bluetoothConnected = false;
    }
  }

  public boolean isBluetoothConnected() {
    if (bluetoothConnection == null) bluetoothConnection = new BluetoothConnection(context);
    bluetoothConnected = bluetoothConnection.isOpen();
    Log.d("bluetoothConnected", bluetoothConnected?"true":"false");

    if (bluetoothConnected) {
      if (heartbeatTimer == null) {
        startHeartbeat();
      }
    }
    return bluetoothConnected;
  }

  private void sendStringToBluetooth(String message) {
    if (bluetoothConnection != null) bluetoothConnection.send(message);
  }

  /*****************************USB************************************/
  public UsbConnection getUsbConnection() {
    return usbConnection;
  }

  public void connectUsb() {
    if (usbConnection == null) usbConnection = new UsbConnection(context, baudRate);
    usbConnected = usbConnection.startUsbConnection();
    if (usbConnected) {
      if (heartbeatTimer == null) {
        startHeartbeat();
      }
    }
  }

  public void disconnectUsb() {
    if (usbConnection != null) {
      stopBot();
      stopHeartbeat();
      usbConnection.stopUsbConnection();
      usbConnection = null;
      usbConnected = false;
    }
  }

  public boolean isUsbConnected() {
    return usbConnected;
  }

  private void sendStringToUsb(String message) {
    if (usbConnection != null) usbConnection.send(message);
  }

  /**
   * 厦门匠客信息科技有限公司 https://www.robospace.cc
   * RoboSpace的ironbot的控制命令格式
   * #A0,1500,100,*
   * #表示开命令开头 *表示命令结尾 A表示设备是角度电机（180度舵机） 0表示0号位置 1500是pwm范围500-2500对应角度根据公式计算出 100表示动作时间
   * 角度范围0-180
   * 角度计算公式 1500 = 100 * t / 9 + 500
   * t=90 其中t为角度  如果是速度电机 角度代表速度 90-0度 代表顺时针 90-180 代表逆时针
   *
   * #B0,0,0,*
   * #B255,255,255,*
   * B表示LED灯 从左到右为红绿蓝 取值0到255
   */
  private void sendStringToUsbForIronbot(int left, int right) {
    Log.d("ironbot", left +","+ right +"\n");

    int leftPwm = (100 * (90+left) / 9 + 500);
    int rightPwm = (100 * (90-right) / 9 + 500);
//    if (usbConnection != null) usbConnection.send(String.format(Locale.US, "#A1,%d,100,2,%d,100,*", leftPwm, rightPwm));
//    try {
//      Thread.sleep(100);//间隔100毫秒 再小就反应不过来了
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }//并没有起作用，得好好研究下Java线程编程。下边是成功的改法，间隔100毫秒的控制在调用的方法里。
    new Thread()
    {
      public void run()
      {
        if (usbConnection != null) usbConnection.send(String.format(Locale.US, "#A1,%d,100,2,%d,100,*", leftPwm, rightPwm));

      }
    }.start();

  }

  private void sendStringToBluetoothForIronbot(int left, int right) {
    Log.d("ironbot", left +","+ right +"\n");

    int leftPwm = (100 * (90+left) / 9 + 500);
    int rightPwm = (100 * (90-right) / 9 + 500);

    new Thread()
    {
      public void run()
      {
        if (bluetoothConnection != null) bluetoothConnection.send(String.format(Locale.US, "#A1,%d,100,2,%d,100,*", leftPwm, rightPwm));

      }
    }.start();

  }

  public float getLeftSpeed() {
    return control.getLeft() * speedMultiplier;
  }

  public float getRightSpeed() {
    return control.getRight() * speedMultiplier;
  }

  public void sendControl() {
    int left = (int) (getLeftSpeed());
    int right = (int) (getRightSpeed());
    if (noiseEnabled && noise.getDirection() < 0)
      left =
          (int)
              ((control.getLeft() - noise.getValue())
                  * speedMultiplier); // since noise value does not have speedMultiplier component,
    // raw control value is used
    if (noiseEnabled && noise.getDirection() > 0)
      right = (int) ((control.getRight() - noise.getValue()) * speedMultiplier);
/*
    //获得距离传感器中的数值，这里只有一个距离
    float juli[]=event.values;
//                for (float d :juli) {
//                    bluemessage.append("当前距离："+d+"\n");
//                }
    int distance=(int) juli[0];//距离传感器在第一位存数据，存值 0和5

    //与默认距离比较 如果小于默认距离 就后退转向
    if(distance<sensor.getMaximumRange()){
      Log.d("sensor", "默认距离："+sensor.getMaximumRange()+" 当前距离："+distance+"\n");
      left = (int) (-2 * speedMultiplier);
      right = (int) (-1 * speedMultiplier);
    }*/
    //我改了一版红外遥控的，不行太卡了，红外发送的慢，赶不上指令发送。注释掉不用了。
    //IrConnection.send( left, right);//add by wangzheng 2022-08-29
    sendStringToUsbForIronbot( left, right);//modify by wz 解决了赶不上指令发送的问题，间隔100毫秒，期间的指令都舍弃。
    sendStringToBluetoothForIronbot( left, right);
//    sendStringToUsb(String.format(Locale.US, "c%d,%d\n", left, right));

  }

  /**
   * 初始化距离传感器 add by wangzheng 2022-08-31
   */
  private void initSensorPROXIMITY() {
    //获得传感器服务
    sm=(SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    //获得距离传感器
    sensor=sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);

  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    this.event=event;
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }

  protected void sendHeartbeat(int timeout_ms) {
    if (usbConnection != null && usbConnection.isOpen() && !usbConnection.isBusy()) {
      usbConnection.send(String.format(Locale.getDefault(), "h%d\n", timeout_ms));
    }
  }

  protected void setSonarFrequency(int interval_ms) {
    if (usbConnection != null && usbConnection.isOpen() && !usbConnection.isBusy()) {
      usbConnection.send(String.format(Locale.getDefault(), "s%d\n", interval_ms));
    }
  }

  protected void setVoltageFrequency(int interval_ms) {
    if (usbConnection != null && usbConnection.isOpen() && !usbConnection.isBusy()) {
      usbConnection.send(String.format(Locale.getDefault(), "v%d\n", interval_ms));
    }
  }

  protected void setWheelOdometryFrequency(int interval_ms) {
    if (usbConnection != null && usbConnection.isOpen() && !usbConnection.isBusy()) {
      usbConnection.send(String.format(Locale.getDefault(), "w%d\n", interval_ms));
    }
  }

  private class HeartBeatTask extends TimerTask {

    @Override
    public void run() {
      //sendHeartbeat(750);//modify by wz
    }
  }

  public void startHeartbeat() {
    heartbeatTimer = new Timer();
    HeartBeatTask heartBeatTask = new HeartBeatTask();
    heartbeatTimer.schedule(heartBeatTask, 250, 250); // 250ms delay and 250ms intervals
  }

  public void stopHeartbeat() {
    if (heartbeatTimer != null) {
      heartbeatTimer.cancel();
      heartbeatTimer.purge();
      heartbeatTimer = null;
    }
  }

  public void stopBot() {
    Control control = new Control(0, 0);
    setControl(control);
  }
}
