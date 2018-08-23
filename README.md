demo-rtc-record-server
======================

## 项目描述  
* 该demo集成的功能包括会场同步和服务器录像，使用前请在开发者后台开通会场同步和服务器录像能力  
1. 会场同步。demo通过接口注册会场同步后，融云将会以http请求的方式将会场状态同步给demo  
2. 服务器录像。demo调用录像程序连接流媒体服务器进行录像  


## 快速Demo体验  
* 运行环境  
Java 8+  
能够被外网访问,用于接收融云的会场通知  

* 基于源码  Maven 打包构建  
1. 下载或克隆demo-rtc-record-server  
2. 进入项目 demo-rtc-record-server 目录  
3. 安装依赖 mvn install  
4. 打包 mvn clean package  

* 会场同步   
1. 创建Demo运行目录,并进入该目录  
2. 将maven打包好的可执行jar包demo-rtc-record-server-*.jar复制到当前目录  
3. 将项目源码根目录中的ServiceSettings.properties,log4j.properties复制到当前目录  
4. 配置ServiceSettings.properties  
  #替换为自己的appKey  
  appKey=  
  #替换为自己的secret  
  secret=  
  #接收会场同步消息的地址，格式为 http://外网访问地址:端口/recv  
  recvAddr=  
5. 启动demo  
  nohup java -jar demo-rtc-record-server-*.jar &  
6. 验证，观察日志nohup.out，无报错，当有会场状态变时能收到请求  

* 服务器录像  
1. 会场同步已经调试成功  
2. 下载[录制程序](http://downloads.rongcloud.cn/Recorder.tar.gz)  
  Recorder    录像主程序  
  lib         依赖库  
3. 将依赖库lib的路径添加到 /etc/ld.so.conf.d/  
4. 将Recorder放到jar包的同级目录  
5. 配置ServiceSettings.properties  
  #录像文件保存目录  
  recordSaveDir=  
6. 重启demo  
7. 验证，录像文件保存目录可以看到可播放的媒体文件  

## 重要类  
* 会场同步   
```
cn.rongcloud.rtc.example.channelsync

ChannelEventListener  会场事件监听接口  
ChannelSyncController 接收会场同步http请求入口  
ChannelManager        会场状态管理类，负责分发event到各个ChannelEventListener  
```
* 服务器录像  
```
cn.rongcloud.rtc.example.recorder

RecordManager         录像管理类，实现了ChannelEventListener接口  
Recorder              用于调用录像C程序  
RecordController      录像下载页面入口  
```
