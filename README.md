# My-Blog

该Spring Boot 项目基于[GitHub 开源博客](https://github.com/ZHENFENG13/My-Blog)开发。

### 部署 
- 详细部署过程请参照[原博客]((https://github.com/ZHENFENG13/My-Blog));
- 修改用的mysql文件放在根目录mysql内,schema.sql为数据库文件, priviliges.sql为可参考的数据库权限设定.数据库名tale;
- 本地运行，在/admin/login中登录(帐号admin,密码123456), 在/admin/register注册新用户

### 新增内容
- 升级至最新的Spring 2.0版本，升级博客相关的Mybatis,Mybatis Pagehelper,Druid 组件，并修复少量Bug
- 抛弃原项目中使用的基于Intercepter的登录模块，使用Shiro开发登录,注册模块，并基于Redis实现Shiro的会话及缓存管理。
- 基于原有的Mysql数据库，修改Service层代码，由单用户博客升级成多用户博客

### 参考链接
- [SpringBoot 博客](https://www.cnblogs.com/ityouknow/category/914493.html)
- [BootStrap 全局CSS样式](https://v3.bootcss.com/css/)
- [Mybatis Generator详解](https://blog.csdn.net/isea533/article/details/42102297)
- [Spring Boot整合Shiro 登录认证及权限控制 ](https://z77z.oschina.io/2017/02/13/SpringBoot+shiro%E6%95%B4%E5%90%88%E5%AD%A6%E4%B9%A0%E4%B9%8B%E7%99%BB%E5%BD%95%E8%AE%A4%E8%AF%81%E5%92%8C%E6%9D%83%E9%99%90%E6%8E%A7%E5%88%B6/)
- [跟我学Shiro](https://jinnianshilongnian.iteye.com/blog/2018398)
- [Shiro认证原理](https://blog.csdn.net/fcs_learner/article/details/79283936)
- [Shiro使用Redis作为缓存](https://blog.csdn.net/qq_34021712/article/details/80791219)
- [Redis指令](https://redis.io/commands#)
