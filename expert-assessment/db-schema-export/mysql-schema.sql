-- MySQL dump 10.13  Distrib 8.0.23, for Linux (x86_64)
--
-- Host: localhost    Database: ai
-- ------------------------------------------------------
-- Server version	8.0.23-0ubuntu0.20.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categorization`
--

DROP TABLE IF EXISTS `categorization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categorization` (
  `uid` bigint NOT NULL AUTO_INCREMENT,
  `date_created` timestamp NOT NULL,
  `last_updated` timestamp NOT NULL,
  `document_uid` bigint DEFAULT NULL,
  `response` json DEFAULT NULL,
  PRIMARY KEY (`uid`),
  KEY `fk_categorization_document_idx` (`document_uid`,`uid`),
  CONSTRAINT `fk_categorization_document` FOREIGN KEY (`document_uid`) REFERENCES `document` (`uid`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `uid` bigint NOT NULL AUTO_INCREMENT,
  `date_created` timestamp NOT NULL,
  `last_updated` timestamp NOT NULL,
  `frequency` float DEFAULT NULL,
  `id` varchar(255) DEFAULT NULL,
  `label` varchar(255) DEFAULT NULL,
  `namespace` varchar(255) DEFAULT NULL,
  `score` int NOT NULL,
  `winner` tinyint(1) DEFAULT NULL,
  `categorization_uid` bigint DEFAULT NULL,
  PRIMARY KEY (`uid`),
  KEY `fk_category_categorization_idx` (`categorization_uid`),
  CONSTRAINT `fk_category_categorization` FOREIGN KEY (`categorization_uid`) REFERENCES `categorization` (`uid`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `category_hierarchy`
--

DROP TABLE IF EXISTS `category_hierarchy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category_hierarchy` (
  `hierarchy` varchar(255) DEFAULT NULL,
  `uid` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `category_position`
--

DROP TABLE IF EXISTS `category_position`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category_position` (
  `uid` int DEFAULT NULL,
  `start` bigint DEFAULT NULL,
  `end` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `document`
--

DROP TABLE IF EXISTS `document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `document` (
  `uid` bigint NOT NULL AUTO_INCREMENT,
  `date_created` timestamp NOT NULL,
  `last_updated` timestamp NOT NULL,
  `content_type` varchar(50) DEFAULT NULL,
  `filepath` varchar(4096) NOT NULL,
  `name` varchar(255) NOT NULL,
  `size` bigint DEFAULT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `full_analisys`
--

DROP TABLE IF EXISTS `full_analisys`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `full_analisys` (
  `uid` bigint NOT NULL AUTO_INCREMENT,
  `date_created` timestamp NOT NULL,
  `last_updated` timestamp NOT NULL,
  `response` json DEFAULT NULL,
  `type` varchar(45) DEFAULT NULL,
  `document_uid` bigint DEFAULT NULL,
  PRIMARY KEY (`uid`),
  KEY `fk_full_analisys_document_idx` (`document_uid`),
  CONSTRAINT `fk_full_analisys_document` FOREIGN KEY (`document_uid`) REFERENCES `document` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-03-14 17:27:01
