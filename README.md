English | [中文](/README.cn.md)

<br>
<p align = "center">
    <img src = "https://oss-weslie.oss-cn-shanghai.aliyuncs.com/data/github_content_pic/165506_ab9d375b_718145.png" width = "300" height = "300">
    <h3 align = "center"> Bridge </h3>
    <h3 align = "center"> Configuration Center · Management System </h3>
    <p align = "center"> Full / grayscale delivery, real-time monitoring of instance configuration item subscription, release rollback, operation log, configuration item monitoring, permission control, multi-environment switching </p>
      <p align="center"><a href="https://lihangqi.gitbook.io/bridge-documentation/">-- For more information --</a></p>
</p>

### Development background

> This project is mainly to solve the problem that the configuration in the distributed system is messy, unable to be managed centrally, and the service needs to be re-released after frequent modification of configuration items. Currently provides "full-scale / grayscale publishing, real-time monitoring of client instance configuration item subscriptions, release rollback, operation history log, configuration item monitoring, permission control, multi-environment switching (development, testing, pre-issue, production)", etc. Features.

### The main function

* Configuration items are delivered in seconds, and the client system dynamically updates the configuration items without republishing
* Complete authority system
  * Accounts are divided into three roles, and the permissions are successively decreased: **System Administrator**, **Team Administrator**, **General User**
  * **System Administrator** can operate all systems and accounts under the team
  * **Team Administrator** can only operate the system under his own team and the accounts of team members
  * **Ordinary users** can only operate their own responsible system
* Record configuration file operation history, provide version rollback, reduce the impact of misoperation
* Real-time monitoring of the subscription of configuration items by client instances
* Provide log monitoring, real-time grasp the usage of configuration items of the client, and make the delivery and client release transparent
* Only need to deploy one service, support multiple environment switching, do not need to deploy one each for dev, test, stable, online
* Solve the "inconsistent read" problem that occurs when users are reading configuration items when delivering configuration items
* **Compatible with the native Spring @Value annotation, and supports **annotations** and **XML placeholders**Get configuration items**
* **Provide hosting of configuration items modified by Spring's native XML placeholders ${} and @Value annotations and springboot's @ConfigurationProperties annotations, and truly realize full hosting of project configuration files**
* Provide monitoring for specified configuration items or all configurations to facilitate business expansion
* Low code intrusion, easy integration and deployment
* Friendly console operation page

### Some techniques used

* Springboot, Mybatis, Maven
* Zookeeper
* Mysql
* Vue.js + Element + iView

### Operation interface display

* #### table <br><br>![Table page](https://oss-weslie.oss-cn-shanghai.aliyuncs.com/data/github_content_pic/121210_977443f1_718145.jpeg ".jpg table ")

* #### account management <br><br>![Account Management](https://oss-weslie.oss-cn-shanghai.aliyuncs.com/data/github_content_pic/165716_eaf8dd7e_718145.jpeg "account management.jpg" )

* #### team management <br><br>![Team management](https://oss-weslie.oss-cn-shanghai.aliyuncs.com/data/github_content_pic/121401_d2123c75_718145.jpeg "team management.jpg" )

* #### System Management <br><br>![System Management](https://oss-weslie.oss-cn-shanghai.aliyuncs.com/data/github_content_pic/121424_70fca057_718145.jpeg "System Management.jpg" )

* #### Configuration Manager <br><br>![Configuration Manager](https://oss-weslie.oss-cn-shanghai.aliyuncs.com/data/github_content_pic/170728_039d100a_718145.jpeg "20200204170607.jpg ")

* #### operating history <br><br>![Operation history](https://oss-weslie.oss-cn-shanghai.aliyuncs.com/data/github_content_pic/165737_a0729f34_718145.jpeg "operation history.jpg")

* #### Zookeeper <br><br>![Zookeeper](https://oss-weslie.oss-cn-shanghai.aliyuncs.com/data/github_content_pic/122802_40e3a02e_718145.jpeg "pic-zookeeper.jpg")

* #### system log <br><br>![System Log](https://oss-weslie.oss-cn-shanghai.aliyuncs.com/data/github_content_pic/121554_d1c2793f_718145.jpeg "system log.jpg" )

### Framework Principle

* Push-pull model

![Push-pull model](https://oss-weslie.oss-cn-shanghai.aliyuncs.com/data/github_content_pic/172017_6a19aa71_718145.png "bridge-yl-zh-cn.png")

* Configuration item loading strategy

![Configuration item loading strategy](https://oss-weslie.oss-cn-shanghai.aliyuncs.com/data/github_content_pic/181826_1e8042ee_718145.png "1112.png")
