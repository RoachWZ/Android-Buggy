自己尝试的第一个视频遥控小车,电脑端控制
基于安卓的视频遥控小车的安卓程序能够使用手机后置摄像头实时地将手机拍到的图像传送到电脑端，同时接收电脑端发出的指令。根据电脑的指令手机再发送相应的红外遥控信号给小车，手机安卓程序主要是实现自定义相机、实时视频传输和红外信号传输等功能。

开发环境用的是ADT Bundle，集成了Eclipse、ADT插件和SDK Tools，安装好JDK即可开始开发，方便使用者进行相关的应用开发。
CSDN链接：https://blog.csdn.net/sinat_37637615/article/details/94854142

ImageCtrlServer.java 为电脑端程序,执行以下程序编译并运行，前提是电脑上装了jdk （哈哈，感觉这句多余）
javac ImageCtrlServer.java
java ImageCtrlServer

在我的OPPO A51 上测试可用，安卓手机因为厂家不同安卓版本不同，就会有适配问题。所以换了手机不一定能用这个程序。