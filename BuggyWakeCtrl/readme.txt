1.使用demo测试时，需将res中除layout外资源拷贝到demo中assets相应的路径下;
2.使用带UI接口时，请将assets下文件拷贝到项目中;
3.文档说明请参考:http://doc.xfyun.cn/msc_android/;
4.在调用sdk时,请将res/layout下xml文件拷贝至工程的layout目录下，此文件为sdk内置ui所需，资源缺失会导致sdk部分功能无法使用;

注： 1. 由于更新优化更新，本次(1138)的libmsc.so库需与本次Msc.jar相匹配，使用之前的Msc.jar包可能会导致出错。


感谢您使用科大讯飞服务。
官方网站：http://www.xfyun.cn/
我在此基础上加了红外控制，当语音唤醒后听到 前进、后退、左转、右转 就通过红外发送对应的控制命令来控制红外遥控小车的运动。