[English](/README.md) |  中文

<br>
<p align="center">
    <img src="https://oss-weslie.oss-cn-shanghai.aliyuncs.com/data/github_content_pic/165506_ab9d375b_718145.png" width="300" height="300">
    <h3 align="center">Bridge</h3>
    <h3 align="center">配置中心 · 管理系统</h3>
    <p align="center">全量/灰度下发、实例配置项订阅的实时监控、发布回滚、操作日志、配置项监听、权限控制、多环境切换</p>
</p>

### 开发背景
>  本项目主要是为了解决分布式系统中配置杂乱，无法集中管理，和频繁修改配置项后需要重新发布服务的问题。目前提供了“全量/灰度发布、客户端实例配置项订阅情况实时监控、发布回滚、操作历史日志、配置项监听、权限控制、多环境切换（开发、测试、预发、生产）"等功能。

### 主要功能

* 秒级下发配置项，客户端系统动态更新配置项无需重新发布
* 完整的权限体系
  * 账号分为三种角色，权限依次递减：**系统管理员**、**团队管理员**、**普通用户**
  * **系统管理员**可以操作所有团队下的系统和账号
  * **团队管理员**只可以操作自己团队下的系统和团队成员的账号
  * **普通用户**只可以操作自己负责的系统
* 记录配置文件操作历史，提供版本回退，减少误操作带来的影响
* 实时监控客户端实例对配置项订阅的情况
* 提供日志监控，实时掌握客户端的配置项使用情况，让下发、客户端发布变的透明
* 只需部署一台服务即可，支持多环境切换，不需要dev、test、stable、online各部署一台
* 解决在下发配置项，用户正在读取配置项时发生的“不一致性读”的问题
* **兼容原生的Spring的@Value注解，同时支持 **注解** 和 **XML占位符** 获取配置项**
* **对Spring原生的XML占位符${}和@Value注解以及springboot的@ConfigurationProperties注解修饰的配置项提供托管，真正实现项目配置文件全托管**
* 对指定的配置项或全部的配置提供监听，方便业务扩展
* 代码侵入性低，集成、部署简单
* 友好的控制台操作页面

### 使用到的一些技术

* Springboot、Mybatis、Maven
* Zookeeper
* Mysql
* Vue.js + Element + iView

### 操作界面展示

* #### 工作台<br><br>![工作台页面](https://oss-weslie.oss-cn-shanghai.aliyuncs.com/data/github_content_pic/121210_977443f1_718145.jpeg "工作台.jpg")

* #### 账号管理<br><br>![账号管理](https://oss-weslie.oss-cn-shanghai.aliyuncs.com/data/github_content_pic/165716_eaf8dd7e_718145.jpeg "账号管理.jpg")

* #### 团队管理<br><br>![团队管理](https://oss-weslie.oss-cn-shanghai.aliyuncs.com/data/github_content_pic/121401_d2123c75_718145.jpeg "团队管理.jpg")

* #### 系统管理<br><br>![系统管理](https://oss-weslie.oss-cn-shanghai.aliyuncs.com/data/github_content_pic/121424_70fca057_718145.jpeg "系统管理.jpg")

* #### 配置项管理<br><br>![配置项管理](https://oss-weslie.oss-cn-shanghai.aliyuncs.com/data/github_content_pic/170728_039d100a_718145.jpeg "20200204170607.jpg")

* #### 操作历史<br><br>![操作历史](https://oss-weslie.oss-cn-shanghai.aliyuncs.com/data/github_content_pic/165737_a0729f34_718145.jpeg "操作历史.jpg")

* #### Zookeeper<br><br>![Zookeeper](https://oss-weslie.oss-cn-shanghai.aliyuncs.com/data/github_content_pic/122802_40e3a02e_718145.jpeg "pic-zookeeper.jpg")

* #### 系统日志<br><br>![系统日志](https://oss-weslie.oss-cn-shanghai.aliyuncs.com/data/github_content_pic/121554_d1c2793f_718145.jpeg "系统日志.jpg")

### 框架原理

* 推拉模型
![推拉模型](https://oss-weslie.oss-cn-shanghai.aliyuncs.com/data/github_content_pic/172017_6a19aa71_718145.png "bridge-yl-zh-cn.png")

* 配置项加载策略
![配置项加载策略](https://oss-weslie.oss-cn-shanghai.aliyuncs.com/data/github_content_pic/181826_1e8042ee_718145.png "1112.png")
