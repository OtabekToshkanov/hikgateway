CREATE DATABASE IF NOT EXISTS `hikgateway`;
USE `hikgateway`;

CREATE TABLE `middleware` (
  `id`            int          NOT NULL AUTO_INCREMENT,
  `host`          varchar(200) NOT NULL,
  `token`         varchar(100) NOT NULL,
  `credentials`   varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE (`token`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `device` (
  `id`            int         NOT NULL AUTO_INCREMENT,
  `device_index`  varchar(36) NOT NULL,
  `vhr_id`        int         NOT NULL,
  `middleware_id` int         NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE  KEY (`device_index`),
  UNIQUE  KEY (`vhr_id`, `middleware_id`),
  FOREIGN KEY (`middleware_id`) REFERENCES `middleware` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;