# demo-rtc-record-server
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

    编辑/etc/ld.so.conf文件，在新的一行中加入依赖库lib文件所在目录；

    运行ldconfig，以更新/etc/ld.so.cache文件；

4. 将Recorder放到jar包的同级目录  

5. 配置ServiceSettings.properties  
    #录像文件保存目录  
    recordSaveDir=
    #录像模式1：自动全录模式；2：自定义异步录像；默认为1。自定义录像模式可以自定义录像文件名
    recordType=   

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
CustomRecordController   自定义录像页面入口
```
##### 录像模式选择：

server支持两种录像模式（在ServiceSettings.properties配置recordType）：

1，自动全录模式，这种方式是会场建立后，server就自动开始录像，录像的文件名按照默认命名规则。

2，自定义异步录像，这种录像模式，服务器默认不录像，需要录像的会场依靠调用API接口启动、停止录像； 满足客户根据需要动态指定录制会场以及远程调用启动录像服务需要。接口的参数有userid和filename两个参数，其中userid为必选，filename为可选，如果不指定FileName则按照默认命名规则。

##### 异步开启录像

URL

```
POST /customrecord/start
```

body格式: json

```json
{
    "uid": "57a53c03-a1ca-d4f6-1a25-18d8c26ea488",//用户的uid，必选
    "filename": "testfilename"，//自定义的录像文件名，可选
}

```

Response

```json
{
  "code":200,
  "msg":"OK"
}
```

##### 异步关闭录像

URL

```
POST /customrecord/stop
```

body格式: json

```json

{
    "uid": "57a53c03-a1ca-d4f6-1a25-18d8c26ea488"//用户的uid，必选
}

```

Response

```json
{
  "code":200,
  "msg":"OK"
}
```

