51-IR-Car
=========

此部分是蓝牙串口遥控小车单片机程序，安卓程序详见https://github.com/RoachWZ/Android-Buggy/tree/master/MonitorAndroidCar-v1.2

RCcar_bluetooth_main 是RC遥控车身的蓝牙遥控程序，小车上的安卓手机选择小车模式，进入后点蓝牙图标连接蓝牙。安卓控制端对应选择RC car 控制模式

拖动条比例调速对应的小车单片机程序https://github.com/RoachWZ/Android-Buggy/OpenBot/firmware/openbot_8051

所需软件环境

+ KEIL(编译软件)
+ STC_ISP(烧写软件)

![super_car3](https://github.com/RoachWZ/AI-in-RTC_ProgrammingChallenge/blob/master/ChallengeProject/Agora-Androidcar-v1.2/photo/super_car3.png)![rc_car](https://github.com/RoachWZ/Android-Buggy/blob/master/51-IR-Car/RCcar.png)

所需的硬件清单

+ STC89C52RC最小系统(单片机控制核心)
+ L293D模块(电机驱动)
+ BK3231蓝牙串口模块
+ 小车底盘（淘宝上面有非常多的小车底盘卖，自己任选）
+ 马达：TT马达 2个
+ 轮子：2个 （小车底盘、马达和轮子可用RC 遥控车代替）
+ 锂电池:2200mAH 两节7.4V + 充电器 （可用充电宝代替，但是当小车长时间不走时没有电流输出充电宝会断电）
+ 杜邦线若干
+ 手机固定支架

![系统框图](https://github.com/RoachWZ/Android-Buggy/blob/master/51-IR-Car/xtkt.png)
![super_car](https://github.com/RoachWZ/AI-in-RTC_ProgrammingChallenge/blob/master/ChallengeProject/Agora-Androidcar-v1.2/photo/super_car.png)
