drop table if exists `log_parser`.`logline`;
drop table if exists `log_parser`.`transition`;
drop table if exists `log_parser`.`view`;

CREATE TABLE `log_parser`.`logline` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `logText` longtext,
  `startDate` varchar(64) DEFAULT NULL,
  `clientConnectionId` varchar(64) DEFAULT NULL,
  `transitionId` int(11) DEFAULT NULL,
  `viewId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `log_parser`.`transition` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(45) DEFAULT NULL,
  `startDate` varchar(64) DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `transitionId` int(11) DEFAULT NULL,
  `viewId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `log_parser`.`view` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(45) DEFAULT NULL,
  `name` varchar(128) DEFAULT NULL,
  `startDate` varchar(64) DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `transitionId` int(11) DEFAULT NULL,
  `viewId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

