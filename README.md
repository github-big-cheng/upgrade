# upgrade 升级更新程序

目前大部分部署项目为JDK1.7，故JDK版本暂使用1.7

**BeanConfig**<br/>
配置Bean的注册<br/>
如:`jdbc`、`mybatis`、`ServiceInfo`、`ConfigurationHelper` 等

**NettyServer**<br/>
Netty服务端<br/>
接收/转发/处理所有请求

**NettyClient**<br/>
Netty客户端<br/>
服务端请求工具，负责具体的请求通讯处理
1. http请求
2. 文件下载，支持重定向下载(可设置最大重定向速度)

**MappingConfigHandler**<br/>
请求路由映射处理器<br/>
通过自定义的`@Controller` `@RequestMapping` `@ResponseType` 将具体`Method`与`路由`绑定<br/>

**RouteHandler**<br/>
下载路由处理器<br/>
1. 记录本地下载次数，根据已注册分发路由情况决断是否重定向至其他分发路由
2. 记录远程主机资源下载进度
3. 发布策略，根据资源的大小、动态下载数量以及分发路由数量实时控制下载速度的，避免过多客户端同时发起下载请求

**TaskHandler**
后台任务处理器<br/>
1. 后台任务、定时循环任务及任务链的任务管理
2. 提供中断通知
3. 提供sleep任务唤醒

**DeployHandler**<br/>
部署处理器<br/>
根据具体服务部署环境及应用类型，调用不通脚本，处理部署任务
1. 支持tomcat部署
2. 支持Runnable Jar部署
3. 支持文件内容修改，添加、追加、修改
4. 支持文件替换

**LockHandler**<br/>
锁资源管理器<br/>
将锁资源按名称管理，支持手动/自动释放(避免内存泄漏)


![running-flow](https://raw.githubusercontent.com/github-big-cheng/upgrade/master/img-folder/flow.png)
