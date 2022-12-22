/*
 * This file is part of the Autonomous Android Vehicle (AAV) application.
 *
 * AAV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AAV is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AAV.  If not, see <http://www.gnu.org/licenses/>.
 */

package ioio.aav;

import org.opencv.core.Point;

import android.bluetooth.BluetoothGatt;
import android.hardware.ConsumerIrManager;
import android.util.Log;

import com.llw.bledemo.callback.BleCallback;
import com.llw.bledemo.utils.BleHelper;

public class ActuatorController {
	private static final String _TAG = "ActuatorController";

	public static final int MIN_PAN_PWM = 600;
	public static final int MAX_PAN_PWM = 2500;
	public static final int MIN_TILT_PWM = 1400;
	public static final int MAX_TILT_PWM = 2250;

	public static final int MID_PAN_PWM = (MAX_PAN_PWM + MIN_PAN_PWM) / 2;
	public static final int MID_TILT_PWM = 1800;// (MAX_TILT_PWM + MIN_TILT_PWM) / 2;

	public static final int RANGE_PAN_PWM = MAX_PAN_PWM - MID_PAN_PWM;

	public static final int RIGHT_FULL_TURN_WHEELS_PWM = 1200;
	public static final int LEFT_FULL_TURN_WHEELS_PWM = 1800;
	public static final int CENTER_FRONT_WHEELS_PWM = (LEFT_FULL_TURN_WHEELS_PWM + RIGHT_FULL_TURN_WHEELS_PWM) / 2;

	public static final int RANGE_WHEELS_PWM = LEFT_FULL_TURN_WHEELS_PWM - CENTER_FRONT_WHEELS_PWM;

	public static final int MOTOR_FORWARD_PWM = 1578;
	public static final int MOTOR_REVERSE_PWM = 1420;
	public static final int MOTOR_NEUTRAL_PWM = 1500;

	public static final int MAX_NEUTRAL_CONTOUR_AREA = 1700*5;
	public static final int MIN_NEUTRAL_CONTOUR_AREA = 800*5;

	public double _pwmPan;
	public double _pwmTilt;
	public double _pwmMotor;
	public double _pwmFrontWheels;

	IRSensors _irSensors;

	double _lastPanPWM;
	double _lastMotorPWM;
	int _pulseCounter = 0;
	boolean _wasMoving = false;

	Point _lastCenterPoint = new Point(0, 0);
	//下面的数组是一种交替的载波序列模式，通过毫秒测量
	//引导码，地址码，地址码，数据码，数据反码
	//第三行数据码反置，比如0x12=0001 0010反置为 0100 1000
//以下 方向 是以前置摄像头为准 本程序是用后置摄像头进行颜色识别追踪，调用时注意方向
	//停止 0x10
	int[] patternS = { 9000, 4500,
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
			/*0000 1000*/560, 560,	560, 560, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690,
			560, 42020, 9000, 2250, 560, 98190 };
	//前进 0x12
	int[] pattern1 = { 9000, 4500,
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
			/*0100 1000*/560, 560,	560, 1690, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560,1690, 	560, 1690, 	560, 1690,
			560, 42020, 9000, 2250, 560, 98190 };
	//后退 0x18
	int[] pattern2 = { 9000, 4500,
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
			/*0001 1000*/560, 560,	560, 560, 	560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690,
			560, 42020, 9000, 2250, 560, 98190 };
	//左转 0x14
	int[] pattern3 = { 9000, 4500,
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
			/*0010 1000*/560, 560,	560, 560, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690,
			560, 42020, 9000, 2250, 560, 98190 };
	//右转 0x16
	int[] pattern4 = { 9000, 4500,
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
			/*0110 1000*/560, 560,	560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 1690,	560, 1690, 	560, 1690,
			560, 42020, 9000, 2250, 560, 98190 };
	//左自转 0x17
	int[] pattern5 = { 9000, 4500,
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
			/*1110 1000*/560, 1690,	560, 1690, 	560, 1690, 	560, 560, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
			560, 560, 	560, 560, 	560, 560, 	560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690,
			560, 42020, 9000, 2250, 560, 98190 };
	//右自转 0x19
	int[] pattern6 = { 9000, 4500,
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
			/*1001 1000*/560, 1690,	560, 560, 	560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560, 560,
			560, 560, 	560, 1690, 	560, 1690, 	560, 560, 	560, 560, 	560,1690, 	560, 1690, 	560, 1690,
			560, 42020, 9000, 2250, 560, 98190 };
	//全速  0x01
	int[] speed = { 9000, 4500,
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
			/*1000 0000*/560,1690,	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
			560,  560, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
			560, 42020, 9000, 2250, 560, 98190 };
	//速度1 0x00
	int[] speed1 = { 9000, 4500,
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
			/*0000 0000*/560, 560,	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
			560, 42020, 9000, 2250, 560, 98190 };
	//速度2 0x02
	int[] speed2 = { 9000, 4500,
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560,560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
			/*0100 0000*/560, 560,	560, 1690, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690,
			560, 42020, 9000, 2250, 560, 98190 };
	//速度3 0x03
	int[] speed3 = { 9000, 4500,
			560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
			560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,
			/*1100 0000*/560, 1690,	560, 1690, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560, 	560, 560,
			560, 560, 	560, 560, 	560, 1690, 	560, 1690, 	560, 1690, 	560, 1690,	560, 1690, 	560, 1690,
			560, 42020, 9000, 2250, 560, 98190 };


	private int hz = 38000;
	private ConsumerIrManager mCIR;
	private double screenX = 175.5;
	private double screenY = 143.5;


	//1500 = 100 * t / 9 + 500  t=90 其中t为角度  速度电机 角度代表速度 90-0度 代表顺时针 90-180 代表逆时针
	int BleCarSpeed = 15;//范围 0-90
	/** 前进 */
	String commandF1 = "#A3,"+(100 * (90-BleCarSpeed) / 9 + 500)+",100,";
	String commandF2 = "4,"+(100 * (90+BleCarSpeed) / 9 + 500)+",100,*";
	/** 后退 */
	String commandB1 = "#A3,"+(100 * (90+BleCarSpeed) / 9 + 500)+",100,";
	String commandB2 = "4,"+(100 * (90-BleCarSpeed) / 9 + 500)+",100,*";
	/**左转	 */
	String commandL1 = "#A3,"+(100 * (90-BleCarSpeed) / 9 + 500)+",100," ;
	String commandL2 = "4,"+(100 * (90-BleCarSpeed) / 9 + 500)+",100,*";
	/**右转	 */
	String commandR1 = "#A3,"+(100 * (90+BleCarSpeed) / 9 + 500)+",100," ;
	String commandR2 =	"4,"+(100 * (90+BleCarSpeed) / 9 + 500)+",100,*";
	/**停	 */
	String commandS1 = "#A3,1500,100," ;
	String commandS2 = "4,1500,100,*";


	int sleeptime = 100;//单位毫秒

	public ActuatorController(ConsumerIrManager mCIR) {
		// set the pulse width to be exactly the middle
		// 设置适当的pwm
		_lastPanPWM = _pwmPan = MID_PAN_PWM;
		_pwmTilt = MID_TILT_PWM;
		_lastMotorPWM = _pwmMotor = MOTOR_NEUTRAL_PWM;
		_pwmFrontWheels = CENTER_FRONT_WHEELS_PWM;

		_irSensors = new IRSensors();

		this.mCIR = mCIR;
	}

	public synchronized double[] getPWMValues() {
		return new double[] { _pwmPan, _pwmTilt, _pwmMotor, _pwmFrontWheels };
	}

	public void updateMotorPWM(double currentContourArea ,BluetoothGatt bluetoothGatt) throws InterruptedException {
		// 根据当前轮廓面积currentContourArea确定下一步运动状态
		updateWheelsPWM();
		if (currentContourArea > MIN_NEUTRAL_CONTOUR_AREA && currentContourArea < MAX_NEUTRAL_CONTOUR_AREA) {
			Log.i(_TAG, "stop");
			_pwmMotor = (_wasMoving) ? MOTOR_REVERSE_PWM - 250 : MOTOR_NEUTRAL_PWM;

			//发送停止红外信号
			////mCIR.transmit(hz, patternS);//停

			_wasMoving = false;
			_pulseCounter = 2;
		} else if (currentContourArea < MIN_NEUTRAL_CONTOUR_AREA) {
			Log.i(_TAG, "forward");


			_pwmMotor = MOTOR_FORWARD_PWM;

			//发送前进红外信号
			////mCIR.transmit(hz, pattern2);//前

			_wasMoving = true;
			_pulseCounter = 2;
		} else if (currentContourArea > MAX_NEUTRAL_CONTOUR_AREA) {
			Log.i(_TAG, "back");

			_pwmMotor = reverseSequence(_pulseCounter);
			if (_pulseCounter > 0)
				_pulseCounter--;

			//发送后退红外信号
			////mCIR.transmit(hz, pattern1);//后退
			//发送停止红外信号
			////mCIR.transmit(hz, patternS);//停

			_wasMoving = false;
		}
		_lastMotorPWM = _pwmMotor;
	}

	private int reverseSequence(int pulseCounter) {
		return (pulseCounter == 2) ? MOTOR_REVERSE_PWM - 90 : (pulseCounter == 1) ? MOTOR_NEUTRAL_PWM + 1 : MOTOR_REVERSE_PWM;
	}

	private void updateWheelsPWM() {
		if (!_irSensors.foundObstacle())
			_pwmFrontWheels = constrain(1.3 * ((MID_PAN_PWM - _pwmPan) / RANGE_PAN_PWM) * RANGE_WHEELS_PWM + CENTER_FRONT_WHEELS_PWM, RIGHT_FULL_TURN_WHEELS_PWM, LEFT_FULL_TURN_WHEELS_PWM);
	}

	public double constrain(double input, double min, double max) {
		return (input < min) ? min : (input > max) ? max : input;
	}

	public void reset() {
		_lastPanPWM = _pwmPan = MID_PAN_PWM;
		_pwmTilt = MID_TILT_PWM;
		_lastMotorPWM = _pwmMotor = MOTOR_NEUTRAL_PWM;
		_pwmFrontWheels = CENTER_FRONT_WHEELS_PWM;
	}

	class IRSensors {
		double _sideLeftIR, _sideRightIR, _frontRightIR, _frontLeftIR;

		public boolean foundObstacle() {
			boolean foundObstacle = false;

			if (_frontRightIR > 0.9) {
				_pwmFrontWheels = LEFT_FULL_TURN_WHEELS_PWM;
				// Log.e(_TAG, Double.toString(_frontRightIR));
				foundObstacle = true;
			} else if (_frontLeftIR > 0.9) {
				_pwmFrontWheels = LEFT_FULL_TURN_WHEELS_PWM;
				foundObstacle = true;
			} else if (_sideLeftIR > 1.1) {
				_pwmFrontWheels = RIGHT_FULL_TURN_WHEELS_PWM - 100;
				foundObstacle = true;
			} else if (_sideRightIR > 1.1) {
				_pwmFrontWheels = LEFT_FULL_TURN_WHEELS_PWM - 100;
				foundObstacle = true;
			}
			return foundObstacle;
		}

		public void updateIRSensorsVoltage(float sideLeftIR, float sideRightIR, float frontRightIR, float frontLeftIR) {
			_sideLeftIR = sideLeftIR;
			_sideRightIR = sideRightIR;
			_frontRightIR = frontRightIR;
			_frontLeftIR = frontLeftIR;
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------------------

	Point increment = new Point(0, 0);
	double target_tilt_position = 0.0;//tilt倾斜

	static final double kD_X = 0.8;// 003901;//018; // Derivative gain (Kd)

	static final int MID_SCREEN_BOUNDARY = 15;//boundary边界

	public boolean updatePanTiltPWM(Point screenCenterPoint, Point currentCenterPoint) {
		boolean reverse = false;
		Point derivativeTerm = new Point(0, 0);

		// --- Set up objects to calculate the error and derivative error
		Point error = new Point(0, 0); // The position error
		Point setpoint = new Point(0, 0);

		setpoint.x = (screenCenterPoint.x - currentCenterPoint.x) * 1.35;
		if ((setpoint.x < -MID_SCREEN_BOUNDARY || setpoint.x > MID_SCREEN_BOUNDARY) && currentCenterPoint.x > 0) {
			if (_lastCenterPoint.x != currentCenterPoint.x) {
				increment.x = setpoint.x * 0.18;
				_lastPanPWM = _pwmPan;
			}
			error.x = (_pwmPan - increment.x);

			derivativeTerm.x = (_pwmPan - _lastPanPWM);

			_lastPanPWM = _pwmPan;

			_pwmPan = error.x - constrain(kD_X * derivativeTerm.x, -9, 9);

			_pwmPan = constrain(_pwmPan, MIN_PAN_PWM, MAX_PAN_PWM);

			// if (_pwmPan >= MAX_PAN_PWM) {
			// reverse = true;
			// _pwmPan = MID_PAN_PWM;
			// }

			_lastCenterPoint.x = currentCenterPoint.x;

//			发送左自转红外信号
//			//mCIR.transmit(hz, pattern6);//左自转
			//发送左转红外信号
			////mCIR.transmit(hz, pattern3);//左转
		}

		setpoint.y = (currentCenterPoint.y - screenCenterPoint.y) * 0.8;
		if ((setpoint.y < -MID_SCREEN_BOUNDARY || setpoint.y > MID_SCREEN_BOUNDARY) && currentCenterPoint.y > 0) {
			if (_lastCenterPoint.y != currentCenterPoint.y) {
				target_tilt_position = (_pwmTilt - setpoint.y);
				increment.y = setpoint.y * 0.41;
			}
			error.y = (_pwmTilt - increment.y);

			if (target_tilt_position > MID_TILT_PWM && error.y > target_tilt_position && error.y > _pwmTilt) {
				_pwmTilt = target_tilt_position;
				increment.y = 0;
			}
			if (target_tilt_position > MID_TILT_PWM && error.y < target_tilt_position && error.y < _pwmTilt) {
				_pwmTilt = target_tilt_position;
				increment.y = 0;
			} else if (target_tilt_position < MID_TILT_PWM && error.y < target_tilt_position && error.y < _pwmTilt) {
				_pwmTilt = target_tilt_position;
				increment.y = 0;
			} else if (target_tilt_position < MID_TILT_PWM && error.y > target_tilt_position && error.y > _pwmTilt) {
				_pwmTilt = target_tilt_position;
				increment.y = 0;
			} else {
				_pwmTilt = error.y;
			}

			_pwmTilt = constrain(_pwmTilt, MIN_TILT_PWM, MAX_TILT_PWM);

			_lastCenterPoint.y = currentCenterPoint.y;

			//发送右自转红外信号
			////mCIR.transmit(hz, pattern5);//右自转
			//发送右转红外信号
			////mCIR.transmit(hz, pattern4);//右转
		}

		return reverse;
	}

	public void updatePanTiltPWM( Point currentCenterPoint,BluetoothGatt bluetoothGatt) {
		double faceX=0;
		double faceY=0;
		boolean fMoveFlag = false;//设置标志位,只执行一次,不连续发送
		boolean bMoveFlag = false;
		boolean lMoveFlag = false;
		boolean rMoveFlag = false;
		boolean stopFlag = false;
		if (true){
			faceX=currentCenterPoint.x;
			faceY=currentCenterPoint.y;
			Log.i(_TAG, "boll detected: Location X: " + (faceX-screenX) +	" Y: " + (faceY-screenY) );
			if((faceY-screenY)<-50&&!fMoveFlag){

				//蓝牙发送指令
				BleHelper.sendCommand(bluetoothGatt, commandF1, true);
				sendCommndSleep();
				BleHelper.sendCommand(bluetoothGatt, commandF2, true);
				sendCommndSleep();

				//mCIR.transmit(hz, pattern2);
				fMoveFlag=true;
				bMoveFlag=false;
				stopFlag=false;
				////mCIR.transmit(hz, pattern);
			}
			else if((faceY-screenY)>50&&!bMoveFlag){

//蓝牙发送指令
				BleHelper.sendCommand(bluetoothGatt, commandB1, true);
				sendCommndSleep();
				BleHelper.sendCommand(bluetoothGatt, commandB2, true);
				sendCommndSleep();

				//mCIR.transmit(hz, pattern1);
				bMoveFlag=true;
				fMoveFlag=false;
				stopFlag=false;
				////mCIR.transmit(hz, pattern);
			}
			else if((faceX-screenX)<-25&&!lMoveFlag){

//蓝牙发送指令
				BleHelper.sendCommand(bluetoothGatt, commandL1, true);
				sendCommndSleep();
				BleHelper.sendCommand(bluetoothGatt, commandL2, true);
				sendCommndSleep();

				// 在38KHz条件下进行模式转换
				//mCIR.transmit(hz, pattern3);
				lMoveFlag=true;
				rMoveFlag=false;
				stopFlag=false;

			}
			else if((faceX-screenX)>25&&!rMoveFlag){

				//蓝牙发送指令
				BleHelper.sendCommand(bluetoothGatt, commandR1, true);
				sendCommndSleep();
				BleHelper.sendCommand(bluetoothGatt, commandR2, true);
				sendCommndSleep();

				//mCIR.transmit(hz, pattern4);
				rMoveFlag=true;
				lMoveFlag=false;
				stopFlag=false;

			}
			else if((faceX-screenX)<25&&(faceX-screenX)>-25&&(faceY-screenY)<50&&(faceY-screenY)>-50){

				if(!stopFlag){
					//蓝牙发送指令
					BleHelper.sendCommand(bluetoothGatt, commandS1, true);
					sendCommndSleep();
					BleHelper.sendCommand(bluetoothGatt, commandS2, true);
					sendCommndSleep();

					//mCIR.transmit(hz, patternS);
					stopFlag=true;
					rMoveFlag=false;
					lMoveFlag=false;
					bMoveFlag=false;
					fMoveFlag=false;
					////mCIR.transmit(hz, patternS);
				}
			}
		}/*else{
		System.currentTimeMillis();
		if(!stopFlag){

			//mCIR.transmit(hz, patternS);
			stopFlag=true;
			rMoveFlag=false;
			lMoveFlag=false;
			bMoveFlag=false;
			fMoveFlag=false;
			////mCIR.transmit(hz, patternS);
		}
	}*/
	}
	private void sendCommndSleep() {
		try {
			Thread.sleep(sleeptime);
			Log.e("TAG", "间隔"+sleeptime);

		} catch (InterruptedException e) {
			e.printStackTrace();
			Log.e("TAG", "间隔失败");
		}
	}
	private void sendCommndAction(BluetoothGatt bluetoothGatt,int angle) {
		//蓝牙发送指令
		BleHelper.sendCommand(bluetoothGatt, commandS1, true);
	}
}