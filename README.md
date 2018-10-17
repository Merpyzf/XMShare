# 小马快传 - XMShare

[![Wercker](https://img.shields.io/badge/Android%20Client-XMShare-brightgreen.svg)]() [![Wercker](https://img.shields.io/badge/Gradle-3.0.1-brightgreen.svg)]()[![bitHound](https://img.shields.io/bithound/dependencies/github/rexxars/sse-channel.svg?maxAge=2592000)]()[![Wercker](https://img.shields.io/wercker/ci/wercker/docs.svg?maxAge=2592000?style=plastic)]() [![Hex.pm](https://img.shields.io/hexpm/l/plug.svg?maxAge=2592000?style=plastic)]()  [![David](https://img.shields.io/david/strongloop/express.svg?maxAge=2592000?style=plastic)]()

## 一款专注于文件传输的Android App  { 因为专注，所以优秀 }


<img src="http://otdmrup4y.bkt.clouddn.com/9D9BBC35-536D-483A-BBDD-DB99E1A5E8F0.png" width="40%"></img>

## 运行截图
<img src="https://raw.githubusercontent.com/Merpyzf/XMShare/master/ScreenShots/Screenshot_2018-02-01-21-35-22-480_%E5%B0%8F%E9%A9%AC%E5%BF%AB%E4%BC%A0.png" width="30%"/><img src="https://raw.githubusercontent.com/Merpyzf/XMShare/master/ScreenShots/Screenshot_2018-02-01-21-35-36-145_%E5%B0%8F%E9%A9%AC%E5%BF%AB%E4%BC%A0.png" width="30%"/><img src="https://raw.githubusercontent.com/Merpyzf/XMShare/master/ScreenShots/Screenshot_2018-02-01-21-35-58-502_%E5%B0%8F%E9%A9%AC%E5%BF%AB%E4%BC%A0.png" width="30%"/><img src="https://raw.githubusercontent.com/Merpyzf/XMShare/master/ScreenShots/Screenshot_2018-01-29-18-37-36-191_%E5%B0%8F%E9%A9%AC%E5%BF%AB%E4%BC%A0.png" width="30%"/><img src="http://otdmrup4y.bkt.clouddn.com/Screenshot_2018-01-29-18-37-54-739_%E5%B0%8F%E9%A9%AC%E5%BF%AB%E4%BC%A0.png" width="30%"/><img src="http://otdmrup4y.bkt.clouddn.com/Screenshot_2018-01-29-18-38-23-198_%E5%B0%8F%E9%A9%AC%E5%BF%AB%E4%BC%A0.png" width="30%"/>


## v1.0 功能介绍
* 在同一局域网下设备间的文件传输
* 无局域网时，通过建立热点组成局域网进行文件传输
* Android设备向PC端进行文件分享（分享整个目录）
* 支持应用(已安装)、图片、音乐、视频的传输
* 支持查看历史传输记录

## v1.3 功能前瞻(待开发)

* 加入文件浏览器，支持传输任意文件/文件夹
* 支持传输通讯录中的联系人
* 支持文件预览功能
* 支持用户自定义主题样式
* 对v1.0的代码进行部分重构和优化
* 支持向PC设备分享用户自己选定的文件


## 使用到的技术

* Socket
* 文件 I/O
* UDP协议
* TCP协议



## 核心传输功能的实现：

#### 1. 局域网内设备发现实现:
发送端建立一个 udp server 用于监听局域网内的udp消息，接收端循环向组播地址发udp消息，消息的内容包含[ 用户名、头像、ip、请求类型、消息 ]，发送端接收到消息后进行匹配，匹配成功后解析并在界面上显示。后续的传输前的验证和以上原理相同。

#### 2. 建立一次Socket连接实现多个文件发送的原理:
    
发送端在发送文件时直接循环<b>待发送的文件列表</b>完成流的写出，接收端获取到流之后需要对其进行分割保存，这就需要制定相关的协议以实现对文件的流的精确分割。因此在发送每一个文件的内容前需要加上一段头信息，其中包含文件名、文件后缀、文件长度等信息。文件的头信息固定位1024个字节。



### 3. 发现附近开启热点设备的实现原理:
这个功能的实现受 [茄子快传](http://www.ushareit.com/)的启发，通过对热点名的匹配来确定这个网络热点是否为可连接的设备所建立的。用户的头像和昵称也包含在热点名中。
    
    

## 引用库

    'com.jakewharton:butterknife:8.8.1' 
    'com.github.bumptech.glide:glide:3.7.0'
    'com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.34' 
    'com.afollestad.material-dialogs:core:0.9.1.0'
    'com.afollestad.material-dialogs:commons:0.9.1.0'
    'de.hdodenhof:circleimageview:2.2.0'
    'pub.devrel:easypermissions:0.4.2'
    'com.simplecityapps:recyclerview-fastscroll:1.0.16'
    'com.android.support:palette-v7:27.0.2'
    'httpcoreserver' 
    'p2ptransfercore' 
    'radarview'


## 致敬

感谢 mayubao 在一些技术问题上给出的帮助

感谢 Jord Riekwel 授权使用图标

 

## 关于我

    class Me {
    
        String name =  "王珂"
        String identity = "student"
        String qq = "1052060838"
    
    }
    
## 最后说两句

这个项目我将会一直开发并维护下去，尽量把代码优化到简洁明了易读。如果你有好的功能或建议，欢迎提意见给我！或者直接fork撸起袖子为其添加新功能。假如你喜欢本项目欢迎给个star ♥️。


## License
    Copyright 2018 wangke
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
