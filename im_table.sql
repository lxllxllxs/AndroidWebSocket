/*
SQLyog v10.2 
MySQL - 5.5.28 : Database - im
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`im` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `im`;

/*Table structure for table `im_linkman` */

DROP TABLE IF EXISTS `im_linkman`;

CREATE TABLE `im_linkman` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user1id` char(255) NOT NULL,
  `user2id` char(255) NOT NULL,
  `user1name` char(255) NOT NULL,
  `user2name` char(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

/*Data for the table `im_linkman` */

insert  into `im_linkman`(`id`,`user1id`,`user2id`,`user1name`,`user2name`) values (1,'20191112lxl123','20125588885556','lxl','lxl1'),(2,'20125588885556','20191112lxl123','lxl1','lxl'),(3,'20191112lxl123','201911asdasd','lxl','lxl2'),(4,'201911asdasd','20191112lxl123','lxl2','lxl'),(5,'20191112lxl123','201sww22123sd','lxl','lxl3'),(6,'201sww22123sd','20191112lxl123','lxl3','lxl');

/*Table structure for table `im_unsend` */

DROP TABLE IF EXISTS `im_unsend`;

CREATE TABLE `im_unsend` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `receiverId` varchar(100) CHARACTER SET latin1 NOT NULL,
  `content` text,
  `senderId` varchar(100) DEFAULT NULL,
  `sendDate` varchar(25) DEFAULT NULL,
  `msgId` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

/*Data for the table `im_unsend` */

insert  into `im_unsend`(`id`,`receiverId`,`content`,`senderId`,`sendDate`,`msgId`) values (11,'20125588885556','to lxl1','20191112lxl123','2016-11-04 10:41:07','8e8e25bd-97ca-4fab-bd1d-877aebc2a436'),(12,'20125588885556','to lxl1 from lxl 23.01','20191112lxl123','2016-11-04 11:02:15','4591012e-580e-4fe9-b4cb-0780215ef011'),(13,'20125588885556','from lxl to lxl1','20191112lxl123','2016-11-08 09:13:26','bae2537d-f92d-47d8-8103-2be36680f956');

/*Table structure for table `im_user` */

DROP TABLE IF EXISTS `im_user`;

CREATE TABLE `im_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` char(255) NOT NULL,
  `pwd` char(255) NOT NULL,
  `userid` char(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;

/*Data for the table `im_user` */

insert  into `im_user`(`id`,`username`,`pwd`,`userid`) values (5,'lxl','123','20191112lxl123'),(8,'lxl1','123','20125588885556'),(9,'lxl2','123','201911asdasd'),(10,'lxl3','123','201sww22123sd');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
