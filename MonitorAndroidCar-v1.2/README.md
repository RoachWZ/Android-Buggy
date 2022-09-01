AndroidCar-v1.2
=========

此部分是安卓程序

本设计的安卓程序能够使用手机摄像头实时地将手机拍到的图像传送到手机端或电脑 端，同时接收手机端或电脑端发出的指令。根据指令小车上的手机再发送相应的红外遥控信 号给小车，手机安卓程序是小车端和控制端二合一的，在初始界面进行选择，主要是实现接 收和发送信令、实时音视频传输和红外信号传输等功能。

Android 开发包及其工具的安装和配置
=========

本次开发使用声网(Agora)的视频通话和信令的 SDK 进行二次开发。下面介绍在正式 使用 Agora SDK for Android 进行通话/直播和信令控制前，需要准备的开发环境，包含 前提条件及 SDK 集成方法等内容

请确保满足以下开发环境要求：

+ (1)Android Studio 3.0 或以上版本
+ (2)App 要求 Android 4.1 或以上设备
+ (3)Android SDK API Level ≥16


在编译和启动实例程序前，需要首先获取一个可用的 App ID:

+ (1)在 agora.io 创建一个开发者账号
+ (2)前往后台页面，点击左部导航栏的“项目”→“项目列表”菜单
+ (3)复制后台的 App ID 并备注，稍后启动应用时会用到它


完成以上步骤后，用 Android Studio 打开该项目，连上设备，就可以编译并运行程序 了。

手机应用介绍
=========

1）用手机替代网络摄像头，更智能，更省电；

2）支持红外遥控玩具遥控车； （移动功能，需要配套本设计红外遥控小车）

使用方法：

1、安装应用，分别在两部手机上安装；

2、将两部手机连入互联网（WiFi，或4G）；

3、把其中一部手机放到红外遥控玩具车上；

4、需要注册appID，两部手机用同一个appID

使用时，先将小车上电。将两部手机连接到互联网，打开我们事先编写好的安卓软件，小车上的手机选择小车模式，另一部手机选择控制模式便可以进行操作了，画面右侧控制小车的运动，左侧则是速度的控制。
![](https://github.com/RoachWZ/AI-in-RTC_ProgrammingChallenge/blob/master/ChallengeProject/Agora-Androidcar-v1.2/photo/xc1.2.png)
![](https://github.com/RoachWZ/AI-in-RTC_ProgrammingChallenge/blob/master/ChallengeProject/Agora-Androidcar-v1.2/photo/kz1.2.png)
