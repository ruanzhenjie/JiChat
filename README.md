# JiChat
这是我五月开始写的Android端通讯软件
包括后台和前端
独立开发后台和前端，后台使用的是workman的websocket框架，前端使用Android JDK。

后台实现的代码位于workerman/Application/chat/Events.php内。

前端使用常驻service实时接收信息，

sharepreference存储账户信息，contentprovider负责使用数据库存储通信消息。

activity与service之间的通信复合使用AIDL与ReceiverBrocast。

使用的各种设计模式就不在这里详述了，请直接看代码。

目前该程序我已布置到自己的服务器上，下载链接是：http://119.29.108.252/jidachat/jidachat.apk 
可以使用aaa（123）bbb（234）ccc（345）等三个账户，括号内是密码。
目前还没有制作注册功能，

由于有其他计划，短期这里的代码不会有大的改动。
但是这个东西已经是一个即时通信系统的核心骨架，只要往上面添加东西即可实现与QQ微信一摸一样。
