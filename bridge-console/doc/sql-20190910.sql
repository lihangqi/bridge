# ************************************************************
# Sequel Pro SQL dump
# Version 5428
#
# https://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 127.0.0.1 (MySQL 5.7.18)
# Database: bridge
# Generation Time: 2019-09-10 07:53:31 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table br_app_def
# ------------------------------------------------------------

DROP TABLE IF EXISTS `br_app_def`;

CREATE TABLE `br_app_def` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creator` int(11) DEFAULT NULL COMMENT '创建人',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modifier` int(11) DEFAULT NULL COMMENT '修改人',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未删除，1：已删除',
  `enabled_state` tinyint(4) NOT NULL DEFAULT '1' COMMENT '启用状态 1:是 0:否',
  `app_code` varchar(32) NOT NULL COMMENT '系统编号',
  `app_name` varchar(200) NOT NULL COMMENT '系统名称',
  `app_owner` int(11) DEFAULT NULL COMMENT '系统负责人Id',
  `team_id` int(11) NOT NULL COMMENT '所属团队',
  `app_des` varchar(255) NOT NULL COMMENT '系统描述',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_sys_code` (`app_code`,`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='应用信息表';



# Dump of table br_config
# ------------------------------------------------------------

DROP TABLE IF EXISTS `br_config`;

CREATE TABLE `br_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creator` int(11) DEFAULT NULL COMMENT '创建人',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modifier` int(11) DEFAULT NULL COMMENT '修改人',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未删除，1：已删除',
  `app_id` int(11) NOT NULL COMMENT '应用Id',
  `config_key` varchar(1024) NOT NULL COMMENT '键',
  `config_value` varchar(2048) DEFAULT NULL COMMENT '生效值',
  `pre_config_value` varchar(2048) DEFAULT NULL COMMENT '预备值，即预备发布的值，一旦下发，它将会替换config_value',
  `key_version` varchar(64) NOT NULL COMMENT '版本号',
  `key_type` varchar(64) DEFAULT NULL COMMENT '所属文件',
  `key_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '生效状态 0:未生效 1:已生效',
  `env_id` tinyint(1) NOT NULL COMMENT '所属环境 0:开发环境 1：测试环境 2:预发布环境 3:生产环境',
  `key_des` varchar(1024) NOT NULL COMMENT '键描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='配置项信息表';



# Dump of table br_config_operate_log
# ------------------------------------------------------------

DROP TABLE IF EXISTS `br_config_operate_log`;

CREATE TABLE `br_config_operate_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creator` int(11) DEFAULT NULL COMMENT '创建人',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modifier` int(11) DEFAULT NULL COMMENT '修改人',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未删除，1：已删除',
  `app_id` int(11) NOT NULL COMMENT '应用Id',
  `env_id` tinyint(1) NOT NULL COMMENT '所属环境 0:开发环境 1：测试环境 2:预发布环境 3:生产环境',
  `operate_id` int(11) NOT NULL COMMENT '操作人Id',
  `operate_name` varchar(32) DEFAULT NULL COMMENT '操作人姓名',
  `config_key` varchar(64) NOT NULL COMMENT '键',
  `value_before` varchar(2048) DEFAULT NULL COMMENT '操作前的值',
  `version_before` varchar(64) DEFAULT NULL COMMENT '操作前的版本号',
  `value_after` varchar(2048) DEFAULT NULL COMMENT '操作后的值',
  `version_after` varchar(64) DEFAULT NULL COMMENT '操作后的版本号',
  `key_des` varchar(1024) DEFAULT NULL COMMENT '键描述',
  `operate_type` tinyint(4) NOT NULL COMMENT '操作类型 0:新增 1:删除 2:回滚',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='k/v操作日志表';



# Dump of table br_sys_log
# ------------------------------------------------------------

DROP TABLE IF EXISTS `br_sys_log`;

CREATE TABLE `br_sys_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未删除，1：已删除',
  `env_id` tinyint(1) NOT NULL COMMENT '所属环境 0:开发环境 1：测试环境 2:预发布环境 3:生产环境 4:所有环境',
  `log_record_time` datetime(3) NOT NULL COMMENT '日志时间',
  `log_level` tinyint(1) NOT NULL COMMENT '日志级别 0:DEBUG 1:INFO 2:WARN 3:ERROR',
  `log_content` text NOT NULL COMMENT '日志内容',
  `client_ip` varchar(32) DEFAULT NULL COMMENT '系统ip',
  `app_code` varchar(255) NOT NULL COMMENT '系统编码',
  PRIMARY KEY (`id`),
  KEY `idx_log_record_time` (`log_record_time`),
  KEY `idx_log_level` (`log_level`),
  KEY `idx_env_id` (`env_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统日志表';



# Dump of table br_team_def
# ------------------------------------------------------------

DROP TABLE IF EXISTS `br_team_def`;

CREATE TABLE `br_team_def` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creator` int(11) DEFAULT NULL COMMENT '创建人',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modifier` int(11) DEFAULT NULL COMMENT '修改人',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未删除，1：已删除',
  `team_name` varchar(32) NOT NULL COMMENT '团队名称',
  `team_des` varchar(255) NOT NULL COMMENT '团队描述',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_team_name` (`team_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='团队信息表';

LOCK TABLES `br_team_def` WRITE;
/*!40000 ALTER TABLE `br_team_def` DISABLE KEYS */;

INSERT INTO `br_team_def` (`id`, `creator`, `gmt_create`, `modifier`, `gmt_modified`, `is_deleted`, `team_name`, `team_des`)
VALUES
	(1,0,'2019-03-15 15:09:28',0,'2019-03-15 15:10:24',0,'系统管理员团队','拥有系统操作的最高权限的团队，负责系统日常运维。');

/*!40000 ALTER TABLE `br_team_def` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table br_user_account
# ------------------------------------------------------------

DROP TABLE IF EXISTS `br_user_account`;

CREATE TABLE `br_user_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creator` int(11) DEFAULT NULL COMMENT '创建人',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modifier` int(11) DEFAULT NULL COMMENT '修改人',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未删除，1：已删除',
  `account_name` varchar(32) NOT NULL COMMENT '账号',
  `account_role` tinyint(4) NOT NULL COMMENT '角色 0: 系统管理员 1: 普通用户 2:团队管理员 ',
  `team_id` int(11) NOT NULL COMMENT '团队信息表Id',
  `password` varchar(64) NOT NULL COMMENT '密码',
  `token` varchar(256) DEFAULT NULL COMMENT '用户token',
  `real_name` varchar(32) DEFAULT NULL COMMENT '姓名',
  `account_mobile` varchar(32) DEFAULT NULL COMMENT '手机号',
  `email` varchar(32) DEFAULT NULL COMMENT '邮箱',
  `enabled_state` tinyint(4) NOT NULL DEFAULT '1' COMMENT '启用状态 1:是 0:否',
  `push_count` int(11) NOT NULL DEFAULT '0' COMMENT '成功下发的次数',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_account_name` (`account_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息表';

LOCK TABLES `br_user_account` WRITE;
/*!40000 ALTER TABLE `br_user_account` DISABLE KEYS */;

INSERT INTO `br_user_account` (`id`, `creator`, `gmt_create`, `modifier`, `gmt_modified`, `is_deleted`, `account_name`, `account_role`, `team_id`, `password`, `token`, `real_name`, `account_mobile`, `email`, `enabled_state`, `push_count`)
VALUES
	(1,0,'2019-03-15 15:05:55',0,'2019-03-21 15:26:31',0,'admin',0,1,'21232f297a57a5a743894a0e4a801fc3','NULL','凌霄','13000000001','13000000001@126.com',1,0);

/*!40000 ALTER TABLE `br_user_account` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
