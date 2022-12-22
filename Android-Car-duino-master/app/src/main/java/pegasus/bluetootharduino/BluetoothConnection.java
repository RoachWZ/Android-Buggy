/**
 * The bluetooth-arduino connection part uses code from the following site
 * https://bellcode.wordpress.com/2012/01/02/android-and-arduino-bluetooth-communication/
 */

package pegasus.bluetootharduino;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.UUID;
import android.bluetooth.*;
import android.os.Handler;
import android.util.Log;

public class BluetoothConnection {

    static BluetoothSocket socket;
    BluetoothAdapter adapt;
    InputStream in;
    static OutputStream out;
    String returnResult;
//    String carduino = "98:D3:31:70:22:71";
    String carduino = "14:41:13:07:18:0A";

    Thread BlueToothThread;
    boolean stop = false;
    int position;
    byte read[];
    static Netstrings nt = new Netstrings();

    static private boolean busy;

    public void runBT() throws IOException, NullPointerException {

        //opens connection //SPP_UUID
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID

        if(BluetoothPairing.MiDevice == null) {
            adapt = BluetoothAdapter.getDefaultAdapter();
            socket = adapt.getRemoteDevice(carduino).createRfcommSocketToServiceRecord(uuid);
        } else {
            socket = BluetoothPairing.MiDevice.createRfcommSocketToServiceRecord(uuid);
        }

        socket.connect();
        out = socket.getOutputStream();
        in = socket.getInputStream();
//      data.setText("connection established");

        //gets data
        final Handler handler = new Handler();
        final byte delimiter = 10;

        stop = false;
        position = 0;
        read = new byte[1024];
        BlueToothThread = new Thread(new Runnable() {

            public void run() {

                while(!Thread.currentThread().isInterrupted() && !stop) {

                    try {

                        int bytesAvailable = in.available();
                        if(bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            in.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++) {
                                byte b = packetBytes[i];
                                if(b == delimiter) {
                                    byte[] encodedBytes = new byte[position];
                                    System.arraycopy(read, 0, encodedBytes, 0, encodedBytes.length);
                                    final String result = new String(encodedBytes, "US-ASCII");
                                    position = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                            returnResult = nt.decodedNetstring(result);
                                            Log.i("result", returnResult);
                                            SensorData.handleInput(returnResult);
                                        }
                                    });

                                } else {
                                    read[position++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex) {
                        stop = true;
                    }
                }
            }
        });

        BlueToothThread.start();

    }


    public static void send() {
        try {
                String text = "";
                if(Autodrive.speedChanged())
                    text +=nt.encodedNetstring("m" + String.valueOf(Autodrive.getConvertedSpeed()));
                if(Autodrive.angleChanged())
                    text += nt.encodedNetstring("t" + String.valueOf(Autodrive.getConvertedAngle()));

                if(!text.isEmpty()) {
                    if (socket.isConnected()) {
                        out.write(text.getBytes());
                    }
                }
            Log.d("Bluetooth data", text);

            } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToManualMode(String command) {
        try {
            String text ="";
            if(command.equals("front")) {
                text = nt.encodedNetstring("m80");
                text += nt.encodedNetstring("t0");
            } else if(command.equals("back")) {
                text = nt.encodedNetstring("m-250");
                text += nt.encodedNetstring("t0");
            } else if(command.equals("right")) {
                text = nt.encodedNetstring("t20");
            } else if(command.equals("left")) {
                text = nt.encodedNetstring("t-20");
            } else if(command.equals("stop")) {
                text = nt.encodedNetstring("m0");
                text += nt.encodedNetstring("t0");
            }

            if(!text.isEmpty()) {
                if (socket.isConnected()) {
                    out.write(text.getBytes());
                }
            }
            Log.d("Bluetooth data", text);

        } catch (IOException e) {
            e.printStackTrace();

        }

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
    public static void sendToIronbot() {
        try {
        String text ="";
        int speed = Autodrive.getConvertedSpeed()/6;
        int angle = Autodrive.getConvertedAngle()/2;
        Log.d("Bluetooth data", "m" + speed +","+"t"+ angle +"\n");

        int leftPwm = (100 * (90+speed+angle) / 9 + 500);
        int rightPwm = (100 * (90-speed+angle) / 9 + 500);

        text = String.format(Locale.US, "#A1,%d,100,2,%d,100,*", leftPwm, rightPwm);
        if (!isBusy()) {
            busy = true;
            if(!text.isEmpty()) {
                if (socket.isConnected()) {
                    out.write(text.getBytes());
                }
            }
            Log.d("Bluetooth data", "send: " + text);

            long start = System.currentTimeMillis( );//LOGGER.i("start time is "+ start);
            long end ;
            while(busy){
                end = System.currentTimeMillis( );
                if(end - start >100) {
                    busy = false;//Ironbot要间隔100毫秒 再小就反应不过来了
                    //LOGGER.i("end time is "+ end);
                }
            }
//      busy = false;
        }else if (isBusy()) {
            Log.d("Bluetooth data", "Serial is busy !");
        }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static boolean isBusy() {
        return busy;
    }

    public void disconnect() {

        try {
            stop = true;
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
