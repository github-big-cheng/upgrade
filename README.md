# upgrade 升级更新程序

目前大部分部署项目为JDK1.7，故JDK版本暂使用1.7

**BeanConfig**<br/>
配置Bean的注册<br/>
如:`jdbc`、`mybatis`、`ServiceInfo`、`ConfigurationHelper` 等

**NettyServer**<br/>
Netty服务端<br/>
负责处理静态文件及资源文件下载外的所有请求

**NettyClient**<br/>
Netty客户端<br/>
服务端请求工具，负责具体的请求通讯处理

**MappingConfigHandler**<br/>
请求路由映射处理器<br/>
通过自定义的`@Controller` `@RequestMapping` `@ResponseType` 将具体`Method`与`路由`绑定<br/>

**RouteHandler**<br/>
下载路由处理器<br/>
记录本地下载次数，根据已注册分发路由情况决断是否重定向至其他分发路由

**TaskHandler**
后台任务处理器<br/>
支持单任务、定时任务及任务链的线程池管理器

**DeployHandler**<br/>
部署处理器<br/>
根据具体服务部署环境及应用类型，调用不通脚本，处理部署任务


![running-flow](https://raw.githubusercontent.com/github-big-cheng/upgrade/master/img-folder/flow.png)
