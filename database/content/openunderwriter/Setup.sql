CREATE DATABASE IF NOT EXISTS ##dbname.openunderwriter## character set utf8;
GRANT ALL ON ##dbname.openunderwriter##.* TO '##dbusername##'@'localhost' IDENTIFIED BY '##dbpassword##' WITH GRANT OPTION;
GRANT ALL ON ##dbname.openunderwriter##.* TO '##dbusername##'@'localhost.localdomain' IDENTIFIED BY '##dbpassword##' WITH GRANT OPTION;

USE ##dbname.openunderwriter##;

-- Baseline OpenUnderwriter 3.X Schema Definition.
--
-- DO NOT MODIFY ANYTHING BEYOND THIS POINT!! All database modifications MUST 
-- be performed by liquibase scripts. The content of this file will only change
-- as part of a major-version OpenUnderwriter upgrade.

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `accAccount`
--

DROP TABLE IF EXISTS `accAccount`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accAccount` (
  `accUID` bigint(20) NOT NULL,
  `accAttribute` longtext,
  `accCreatedBy` bigint(20) DEFAULT NULL,
  `accCreatedDate` datetime DEFAULT NULL,
  `accExternalSystemId` varchar(255) NOT NULL,
  `accForeignSystemId` varchar(255) DEFAULT NULL,
  `accLock` bit(1) DEFAULT NULL,
  `accSerialVersion` bigint(20) NOT NULL,
  `accUpdatedBy` bigint(20) DEFAULT NULL,
  `accUpdatedDate` datetime DEFAULT NULL,
  `accCurrency` varchar(255) DEFAULT NULL,
  `accDescription` longtext,
  `accName` varchar(255) DEFAULT NULL,
  `accOpeningDate` datetime DEFAULT NULL,
  `accType` varchar(255) DEFAULT NULL,
  `accAccountHolderUIDpar` bigint(20) DEFAULT NULL,
  `accStatus` varchar(255) DEFAULT 'OPEN',
  PRIMARY KEY (`accUID`),
  UNIQUE KEY `accExternalSystemId` (`accExternalSystemId`),
  KEY `FK2F6B466C6917B84` (`accAccountHolderUIDpar`),
  CONSTRAINT `FK2F6B466C6917B84` FOREIGN KEY (`accAccountHolderUIDpar`) REFERENCES `parParty` (`parUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `accAccount_`
--

DROP TABLE IF EXISTS `accAccount_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accAccount_` (
  `accUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `accAttribute` longtext,
  `accCreatedBy` bigint(20) DEFAULT NULL,
  `accCreatedDate` datetime DEFAULT NULL,
  `accExternalSystemId` varchar(255) DEFAULT NULL,
  `accForeignSystemId` varchar(255) DEFAULT NULL,
  `accUpdatedBy` bigint(20) DEFAULT NULL,
  `accUpdatedDate` datetime DEFAULT NULL,
  `accCurrency` varchar(255) DEFAULT NULL,
  `accDescription` longtext,
  `accName` varchar(255) DEFAULT NULL,
  `accOpeningDate` datetime DEFAULT NULL,
  `accStatus` varchar(255) DEFAULT NULL,
  `accType` varchar(255) DEFAULT NULL,
  `accAccountHolderUIDpar` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`accUID`,`REV`),
  KEY `FKBDFD877352AE8777` (`REV`),
  CONSTRAINT `FKBDFD877352AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `apeAccountingPeriod`
--

DROP TABLE IF EXISTS `apeAccountingPeriod`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `apeAccountingPeriod` (
  `apeUID` bigint(20) NOT NULL,
  `apeAttribute` longtext,
  `apeCreatedBy` bigint(20) DEFAULT NULL,
  `apeCreatedDate` datetime DEFAULT NULL,
  `apeExternalSystemId` varchar(255) NOT NULL,
  `apeForeignSystemId` varchar(255) DEFAULT NULL,
  `apeLock` bit(1) DEFAULT NULL,
  `apeSerialVersion` bigint(20) NOT NULL,
  `apeUpdatedBy` bigint(20) DEFAULT NULL,
  `apeUpdatedDate` datetime DEFAULT NULL,
  `apeEndDate` timestamp(4) NULL DEFAULT NULL,
  `apeStartDate` timestamp(4) NULL DEFAULT NULL,
  `apeStatus` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`apeUID`),
  UNIQUE KEY `apeExternalSystemId` (`apeExternalSystemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `apeAccountingPeriod_`
--

DROP TABLE IF EXISTS `apeAccountingPeriod_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `apeAccountingPeriod_` (
  `apeUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `apeAttribute` longtext,
  `apeCreatedBy` bigint(20) DEFAULT NULL,
  `apeCreatedDate` datetime DEFAULT NULL,
  `apeExternalSystemId` varchar(255) DEFAULT NULL,
  `apeForeignSystemId` varchar(255) DEFAULT NULL,
  `apeUpdatedBy` bigint(20) DEFAULT NULL,
  `apeUpdatedDate` datetime DEFAULT NULL,
  `apeEndDate` datetime DEFAULT NULL,
  `apeStartDate` datetime DEFAULT NULL,
  `apeStatus` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`apeUID`,`REV`),
  KEY `FK67B5EAF352AE8777` (`REV`),
  CONSTRAINT `FK67B5EAF352AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ashAssessmentSheet`
--

DROP TABLE IF EXISTS `ashAssessmentSheet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ashAssessmentSheet` (
  `ashUID` bigint(20) NOT NULL,
  `ashAttribute` longtext,
  `ashCreatedDate` datetime DEFAULT NULL,
  `ashExternalSystemId` varchar(255) NOT NULL,
  `ashForeignSystemId` varchar(255) DEFAULT NULL,
  `ashLock` tinyint(1) NOT NULL,
  `ashSerialVersion` bigint(20) NOT NULL,
  `ashUpdatedDate` datetime DEFAULT NULL,
  `ashAssessmentLines` longtext,
  `ashAutoPriority` int(11) NOT NULL,
  `ashProcessedOrderCounter` int(11) NOT NULL,
  `ashCreatedBy` bigint(20) DEFAULT NULL,
  `ashUpdatedBy` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ashUID`),
  UNIQUE KEY `ashExternalSystemId` (`ashExternalSystemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ashAssessmentSheet_`
--

DROP TABLE IF EXISTS `ashAssessmentSheet_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ashAssessmentSheet_` (
  `ashUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `ashAttribute` longtext,
  `ashCreatedBy` bigint(20) DEFAULT NULL,
  `ashCreatedDate` datetime DEFAULT NULL,
  `ashExternalSystemId` varchar(255) DEFAULT NULL,
  `ashForeignSystemId` varchar(255) DEFAULT NULL,
  `ashUpdatedBy` bigint(20) DEFAULT NULL,
  `ashUpdatedDate` datetime DEFAULT NULL,
  `ashAssessmentLines` longtext,
  `ashAutoPriority` int(11) DEFAULT NULL,
  `ashProcessedOrderCounter` int(11) DEFAULT NULL,
  PRIMARY KEY (`ashUID`,`REV`),
  KEY `FK3C74EAD852AE8777` (`REV`),
  CONSTRAINT `FK3C74EAD852AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `assAsset`
--

DROP TABLE IF EXISTS `assAsset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assAsset` (
  `assUID` bigint(20) NOT NULL,
  `assAttribute` longtext,
  `assCreatedBy` bigint(20) DEFAULT NULL,
  `assCreatedDate` datetime DEFAULT NULL,
  `assExternalSystemId` varchar(255) NOT NULL,
  `assForeignSystemId` varchar(255) DEFAULT NULL,
  `assLock` bit(1) DEFAULT NULL,
  `assSerialVersion` bigint(20) NOT NULL,
  `assUpdatedBy` bigint(20) DEFAULT NULL,
  `assUpdatedDate` datetime DEFAULT NULL,
  `assAssetTypeId` varchar(255) DEFAULT NULL,
  `assId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`assUID`),
  UNIQUE KEY `assExternalSystemId` (`assExternalSystemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `assAsset_`
--

DROP TABLE IF EXISTS `assAsset_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assAsset_` (
  `assUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `assAttribute` longtext,
  `assCreatedBy` bigint(20) DEFAULT NULL,
  `assCreatedDate` datetime DEFAULT NULL,
  `assExternalSystemId` varchar(255) DEFAULT NULL,
  `assForeignSystemId` varchar(255) DEFAULT NULL,
  `assUpdatedBy` bigint(20) DEFAULT NULL,
  `assUpdatedDate` datetime DEFAULT NULL,
  `assAssetTypeId` varchar(255) DEFAULT NULL,
  `assId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`assUID`,`REV`),
  KEY `FK10F7355052AE8777` (`REV`),
  CONSTRAINT `FK10F7355052AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `balBalance`
--

DROP TABLE IF EXISTS `balBalance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `balBalance` (
  `balUID` bigint(20) NOT NULL,
  `balAttribute` longtext,
  `balCreatedBy` bigint(20) DEFAULT NULL,
  `balCreatedDate` datetime DEFAULT NULL,
  `balExternalSystemId` varchar(255) NOT NULL,
  `balForeignSystemId` varchar(255) DEFAULT NULL,
  `balLock` bit(1) DEFAULT NULL,
  `balSerialVersion` bigint(20) NOT NULL,
  `balUpdatedBy` bigint(20) DEFAULT NULL,
  `balUpdatedDate` datetime DEFAULT NULL,
  `balAmount` decimal(19,2) DEFAULT NULL,
  `balCurrency` varchar(255) DEFAULT NULL,
  `balDate` datetime DEFAULT NULL,
  `balJournalLineUIDjli` bigint(20) DEFAULT NULL,
  `balAccountUID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`balUID`),
  UNIQUE KEY `balExternalSystemId` (`balExternalSystemId`),
  KEY `FKD40B520F57C2D44C` (`balAccountUID`),
  KEY `FKD40B520FE61BFF77` (`balJournalLineUIDjli`),
  CONSTRAINT `FKD40B520F57C2D44C` FOREIGN KEY (`balAccountUID`) REFERENCES `accAccount` (`accUID`),
  CONSTRAINT `FKD40B520FE61BFF77` FOREIGN KEY (`balJournalLineUIDjli`) REFERENCES `jliJournalLine` (`jliUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `balBalance_`
--

DROP TABLE IF EXISTS `balBalance_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `balBalance_` (
  `balUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `balAttribute` longtext,
  `balCreatedBy` bigint(20) DEFAULT NULL,
  `balCreatedDate` datetime DEFAULT NULL,
  `balExternalSystemId` varchar(255) DEFAULT NULL,
  `balForeignSystemId` varchar(255) DEFAULT NULL,
  `balUpdatedBy` bigint(20) DEFAULT NULL,
  `balUpdatedDate` datetime DEFAULT NULL,
  `balAmount` decimal(19,2) DEFAULT NULL,
  `balCurrency` varchar(255) DEFAULT NULL,
  `balDate` datetime DEFAULT NULL,
  `balAccountUID` bigint(20) DEFAULT NULL,
  `balJournalLineUIDjli` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`balUID`,`REV`),
  KEY `FKAD5EF03052AE8777` (`REV`),
  CONSTRAINT `FKAD5EF03052AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cClaLab`
--

DROP TABLE IF EXISTS `cClaLab`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cClaLab` (
  `UIDcla` bigint(20) NOT NULL,
  `claLabel` varchar(255) DEFAULT NULL,
  KEY `FKED7CC798E0CB4A47` (`UIDcla`),
  CONSTRAINT `FKED7CC798E0CB4A47` FOREIGN KEY (`UIDcla`) REFERENCES `claClaim` (`claUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cClaLab_`
--

DROP TABLE IF EXISTS `cClaLab_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cClaLab_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDcla` bigint(20) NOT NULL DEFAULT '0',
  `claLabel` varchar(255) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDcla`,`claLabel`),
  KEY `FKC21C2BC752AE8777` (`REV`),
  CONSTRAINT `FKC21C2BC752AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cClsRto`
--

DROP TABLE IF EXISTS `cClsRto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cClsRto` (
  `UIDcls` bigint(20) NOT NULL,
  `refId` varchar(255) DEFAULT NULL,
  `refType` varchar(255) DEFAULT NULL,
  KEY `FKED850F266DB726FA` (`UIDcls`),
  CONSTRAINT `FKED850F266DB726FA` FOREIGN KEY (`UIDcls`) REFERENCES `clsClause` (`clsUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cDocLab`
--

DROP TABLE IF EXISTS `cDocLab`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cDocLab` (
  `UIDdoc` bigint(20) NOT NULL,
  `docLabel` varchar(255) DEFAULT NULL,
  KEY `FKEF5CCF78E19DC77E` (`UIDdoc`),
  CONSTRAINT `FKEF5CCF78E19DC77E` FOREIGN KEY (`UIDdoc`) REFERENCES `docDocument` (`docUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cDocLab_`
--

DROP TABLE IF EXISTS `cDocLab_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cDocLab_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDdoc` bigint(20) NOT NULL DEFAULT '0',
  `docLabel` varchar(255) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDdoc`,`docLabel`),
  KEY `FKFC3D1FE752AE8777` (`REV`),
  CONSTRAINT `FKFC3D1FE752AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cDplLab`
--

DROP TABLE IF EXISTS `cDplLab`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cDplLab` (
  `UIDdpl` bigint(20) NOT NULL,
  `dplLabel` varchar(255) DEFAULT NULL,
  KEY `FKEF6EFE5049D8A0D` (`UIDdpl`),
  CONSTRAINT `FKEF6EFE5049D8A0D` FOREIGN KEY (`UIDdpl`) REFERENCES `dplDocumentPlaceholder` (`dplUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cDplLab_`
--

DROP TABLE IF EXISTS `cDplLab_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cDplLab_` (
  `REV` int(11) NOT NULL,
  `UIDdpl` bigint(20) NOT NULL,
  `dplLabel` varchar(255) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDdpl`,`dplLabel`),
  KEY `FKFE70CC0F88B4F168` (`REV`),
  CONSTRAINT `FKFE70CC0F88B4F168` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cNotLab`
--

DROP TABLE IF EXISTS `cNotLab`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cNotLab` (
  `UIDnot` bigint(20) NOT NULL,
  `notLabel` varchar(255) DEFAULT NULL,
  KEY `FK74FFFD81AAEBAD` (`UIDnot`),
  CONSTRAINT `FK74FFFD81AAEBAD` FOREIGN KEY (`UIDnot`) REFERENCES `notNote` (`notUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cNotLab_`
--

DROP TABLE IF EXISTS `cNotLab_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cNotLab_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDnot` bigint(20) NOT NULL DEFAULT '0',
  `notLabel` varchar(255) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDnot`,`notLabel`),
  KEY `FKE2B000252AE8777` (`REV`),
  CONSTRAINT `FKE2B000252AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cParLab`
--

DROP TABLE IF EXISTS `cParLab`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cParLab` (
  `UIDpar` bigint(20) NOT NULL,
  `parLabel` varchar(255) DEFAULT NULL,
  KEY `FK3187F6F43E35FF8` (`UIDpar`),
  CONSTRAINT `FK3187F6F43E35FF8` FOREIGN KEY (`UIDpar`) REFERENCES `parParty` (`parUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cParLab_`
--

DROP TABLE IF EXISTS `cParLab_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cParLab_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDpar` bigint(20) NOT NULL DEFAULT '0',
  `parLabel` varchar(255) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpar`,`parLabel`),
  KEY `FK5FF76ED052AE8777` (`REV`),
  CONSTRAINT `FK5FF76ED052AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cPolLab`
--

DROP TABLE IF EXISTS `cPolLab`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cPolLab` (
  `UIDpol` bigint(20) NOT NULL,
  `polLabel` varchar(255) DEFAULT NULL,
  KEY `FK3DB0E4384156FE0` (`UIDpol`),
  CONSTRAINT `FK3DB0E4384156FE0` FOREIGN KEY (`UIDpol`) REFERENCES `polPolicy` (`polUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cPolLab_`
--

DROP TABLE IF EXISTS `cPolLab_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cPolLab_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDpol` bigint(20) NOT NULL DEFAULT '0',
  `polLabel` varchar(255) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpol`,`polLabel`),
  KEY `FK7786BA7C52AE8777` (`REV`),
  CONSTRAINT `FK7786BA7C52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `claClaim`
--

DROP TABLE IF EXISTS `claClaim`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `claClaim` (
  `claUID` bigint(20) NOT NULL,
  `claAttribute` longtext,
  `claCreatedBy` bigint(20) DEFAULT NULL,
  `claCreatedDate` datetime DEFAULT NULL,
  `claExternalSystemId` varchar(255) NOT NULL,
  `claForeignSystemId` varchar(255) DEFAULT NULL,
  `claLock` bit(1) DEFAULT NULL,
  `claSerialVersion` bigint(20) NOT NULL,
  `claUpdatedBy` bigint(20) DEFAULT NULL,
  `claUpdatedDate` datetime DEFAULT NULL,
  `claAdrFlag` bit(1) DEFAULT NULL,
  `claClaimNumber` varchar(255) DEFAULT NULL,
  `claContributionReserveAmount` decimal(19,2) DEFAULT NULL,
  `claContributionReserveCurrency` varchar(255) DEFAULT NULL,
  `claDeductableReserveAmount` decimal(19,2) DEFAULT NULL,
  `claDeductableReserveCurrency` varchar(255) DEFAULT NULL,
  `claEstimatedReserveAmount` decimal(19,2) DEFAULT NULL,
  `claEstimatedReserveCurrency` varchar(255) DEFAULT NULL,
  `claId` varchar(255) DEFAULT NULL,
  `claLitigationFlag` bit(1) DEFAULT NULL,
  `claOutstandingReserveAmount` decimal(19,2) DEFAULT NULL,
  `claOutstandingReserveCurrency` varchar(255) DEFAULT NULL,
  `claOutstandingTotalAmount` decimal(19,2) DEFAULT NULL,
  `claOutstandingTotalCurrency` varchar(255) DEFAULT NULL,
  `claOwningUser` bigint(20) DEFAULT NULL,
  `claPaid` bit(1) DEFAULT NULL,
  `claRecoveryStatus` varchar(255) DEFAULT NULL,
  `claSalvageReserveAmount` decimal(19,2) DEFAULT NULL,
  `claSalvageReserveCurrency` varchar(255) DEFAULT NULL,
  `claStatus` varchar(255) DEFAULT NULL,
  `claSubrogationPotential` bit(1) DEFAULT NULL,
  `claSubrogationReserveAmount` decimal(19,2) DEFAULT NULL,
  `claSubrogationReserveCurrency` varchar(255) DEFAULT NULL,
  `claSubrogationWaiver` bit(1) DEFAULT NULL,
  `claTotalEstimatedRecoveryAmount` decimal(19,2) DEFAULT NULL,
  `claTotalEstimatedRecoveryCurrency` varchar(255) DEFAULT NULL,
  `claTotalRecoveredAmount` decimal(19,2) DEFAULT NULL,
  `claTotalRecoveredCurrency` varchar(255) DEFAULT NULL,
  `claPolicyUIDpol` bigint(20) DEFAULT NULL,
  `claStartDate` datetime DEFAULT NULL,
  `claEndDate` datetime DEFAULT NULL,
  PRIMARY KEY (`claUID`),
  UNIQUE KEY `claExternalSystemId` (`claExternalSystemId`),
  KEY `owningUser` (`claOwningUser`),
  KEY `claimNumber` (`claClaimNumber`),
  KEY `status` (`claStatus`),
  KEY `FK269E2604E5FB84AA` (`claPolicyUIDpol`),
  CONSTRAINT `FK269E2604E5FB84AA` FOREIGN KEY (`claPolicyUIDpol`) REFERENCES `polPolicy` (`polUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `claClaim_`
--

DROP TABLE IF EXISTS `claClaim_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `claClaim_` (
  `claUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `claAttribute` longtext,
  `claCreatedBy` bigint(20) DEFAULT NULL,
  `claCreatedDate` datetime DEFAULT NULL,
  `claExternalSystemId` varchar(255) DEFAULT NULL,
  `claForeignSystemId` varchar(255) DEFAULT NULL,
  `claUpdatedBy` bigint(20) DEFAULT NULL,
  `claUpdatedDate` datetime DEFAULT NULL,
  `claAdrFlag` bit(1) DEFAULT NULL,
  `claClaimNumber` varchar(255) DEFAULT NULL,
  `claContributionReserveAmount` decimal(19,2) DEFAULT NULL,
  `claContributionReserveCurrency` varchar(255) DEFAULT NULL,
  `claDeductableReserveAmount` decimal(19,2) DEFAULT NULL,
  `claDeductableReserveCurrency` varchar(255) DEFAULT NULL,
  `claEstimatedReserveAmount` decimal(19,2) DEFAULT NULL,
  `claEstimatedReserveCurrency` varchar(255) DEFAULT NULL,
  `claId` varchar(255) DEFAULT NULL,
  `claLitigationFlag` bit(1) DEFAULT NULL,
  `claOutstandingReserveAmount` decimal(19,2) DEFAULT NULL,
  `claOutstandingReserveCurrency` varchar(255) DEFAULT NULL,
  `claOutstandingTotalAmount` decimal(19,2) DEFAULT NULL,
  `claOutstandingTotalCurrency` varchar(255) DEFAULT NULL,
  `claOwningUser` bigint(20) DEFAULT NULL,
  `claPaid` bit(1) DEFAULT NULL,
  `claRecoveryStatus` varchar(255) DEFAULT NULL,
  `claSalvageReserveAmount` decimal(19,2) DEFAULT NULL,
  `claSalvageReserveCurrency` varchar(255) DEFAULT NULL,
  `claStatus` varchar(255) DEFAULT NULL,
  `claSubrogationPotential` bit(1) DEFAULT NULL,
  `claSubrogationReserveAmount` decimal(19,2) DEFAULT NULL,
  `claSubrogationReserveCurrency` varchar(255) DEFAULT NULL,
  `claSubrogationWaiver` bit(1) DEFAULT NULL,
  `claTotalEstimatedRecoveryAmount` decimal(19,2) DEFAULT NULL,
  `claTotalEstimatedRecoveryCurrency` varchar(255) DEFAULT NULL,
  `claTotalRecoveredAmount` decimal(19,2) DEFAULT NULL,
  `claTotalRecoveredCurrency` varchar(255) DEFAULT NULL,
  `claPolicyUIDpol` bigint(20) DEFAULT NULL,
  `claStartDate` datetime DEFAULT NULL,
  `claEndDate` datetime DEFAULT NULL,
  PRIMARY KEY (`claUID`,`REV`),
  KEY `FKAD269ADB52AE8777` (`REV`),
  CONSTRAINT `FKAD269ADB52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clsClause`
--

DROP TABLE IF EXISTS `clsClause`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clsClause` (
  `clsUID` bigint(20) NOT NULL,
  `clsAttribute` longtext,
  `clsCreatedBy` bigint(20) DEFAULT NULL,
  `clsCreatedDate` datetime DEFAULT NULL,
  `clsExternalSystemId` varchar(255) NOT NULL,
  `clsForeignSystemId` varchar(255) DEFAULT NULL,
  `clsLock` bit(1) DEFAULT NULL,
  `clsSerialVersion` bigint(20) NOT NULL,
  `clsUpdatedBy` bigint(20) DEFAULT NULL,
  `clsUpdatedDate` datetime DEFAULT NULL,
  `clsEndDate` datetime DEFAULT NULL,
  `clsManuscript` tinyint(1) NOT NULL,
  `clsReference` varchar(255) DEFAULT NULL,
  `clsReminderDate` datetime DEFAULT NULL,
  `clsStartDate` datetime DEFAULT NULL,
  `clsSubjectId` varchar(255) DEFAULT NULL,
  `clsSubjectType` varchar(255) DEFAULT NULL,
  `clsText` longtext,
  `clsTitle` varchar(255) DEFAULT NULL,
  `clsType` varchar(255) DEFAULT NULL,
  `clsAdvisory` bit(1) DEFAULT NULL,
  PRIMARY KEY (`clsUID`),
  UNIQUE KEY `clsExternalSystemId` (`clsExternalSystemId`),
  KEY `status` (`clsType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clsClause_`
--

DROP TABLE IF EXISTS `clsClause_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clsClause_` (
  `clsUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `clsAttribute` longtext,
  `clsCreatedBy` bigint(20) DEFAULT NULL,
  `clsCreatedDate` datetime DEFAULT NULL,
  `clsExternalSystemId` varchar(255) DEFAULT NULL,
  `clsForeignSystemId` varchar(255) DEFAULT NULL,
  `clsUpdatedBy` bigint(20) DEFAULT NULL,
  `clsUpdatedDate` datetime DEFAULT NULL,
  `clsEndDate` datetime DEFAULT NULL,
  `clsManuscript` tinyint(1) DEFAULT NULL,
  `clsReference` varchar(255) DEFAULT NULL,
  `clsReminderDate` datetime DEFAULT NULL,
  `clsStartDate` datetime DEFAULT NULL,
  `clsSubjectId` varchar(255) DEFAULT NULL,
  `clsSubjectType` varchar(255) DEFAULT NULL,
  `clsText` longtext,
  `clsTitle` varchar(255) DEFAULT NULL,
  `clsType` varchar(255) DEFAULT NULL,
  `clsAdvisory` bit(1) DEFAULT NULL,
  PRIMARY KEY (`clsUID`,`REV`),
  KEY `FK4587744652AE8777` (`REV`),
  CONSTRAINT `FK4587744652AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `conConfig`
--

DROP TABLE IF EXISTS `conConfig`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `conConfig` (
  `namespace` varchar(255) NOT NULL,
  `manager` varchar(255) NOT NULL,
  `configuration` longblob NOT NULL,
  `validfrom` bigint(20) NOT NULL,
  `validto` bigint(20) DEFAULT NULL,
  `who` varchar(32) DEFAULT NULL,
  `version` varchar(32) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `covCoverage`
--

DROP TABLE IF EXISTS `covCoverage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `covCoverage` (
  `covUID` bigint(20) NOT NULL,
  `covAttribute` longtext,
  `covCreatedBy` bigint(20) DEFAULT NULL,
  `covCreatedDate` datetime DEFAULT NULL,
  `covExternalSystemId` varchar(255) NOT NULL,
  `covForeignSystemId` varchar(255) DEFAULT NULL,
  `covLock` bit(1) DEFAULT NULL,
  `covSerialVersion` bigint(20) NOT NULL,
  `covUpdatedBy` bigint(20) DEFAULT NULL,
  `covUpdatedDate` datetime DEFAULT NULL,
  `covCoverageTypeId` varchar(255) DEFAULT NULL,
  `covDeductibleAmount` decimal(19,2) DEFAULT NULL,
  `covDeductibleCurrency` varchar(255) DEFAULT NULL,
  `covDescription` varchar(255) DEFAULT NULL,
  `covEffectiveDate` datetime DEFAULT NULL,
  `covEnabled` bit(1) NOT NULL,
  `covExpiryDate` datetime DEFAULT NULL,
  `covId` varchar(255) DEFAULT NULL,
  `covLimitAmount` decimal(19,2) DEFAULT NULL,
  `covLimitCurrency` varchar(255) DEFAULT NULL,
  `covName` varchar(255) DEFAULT NULL,
  `covOptional` bit(1) NOT NULL,
  `covBrokerUIDpar` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`covUID`),
  UNIQUE KEY `covExternalSystemId` (`covExternalSystemId`),
  KEY `FKC6B73192BA6F347E` (`covBrokerUIDpar`),
  CONSTRAINT `FKC6B73192BA6F347E` FOREIGN KEY (`covBrokerUIDpar`) REFERENCES `parParty` (`parUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `covCoverage_`
--

DROP TABLE IF EXISTS `covCoverage_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `covCoverage_` (
  `covUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `covAttribute` longtext,
  `covCreatedBy` bigint(20) DEFAULT NULL,
  `covCreatedDate` datetime DEFAULT NULL,
  `covExternalSystemId` varchar(255) DEFAULT NULL,
  `covForeignSystemId` varchar(255) DEFAULT NULL,
  `covUpdatedBy` bigint(20) DEFAULT NULL,
  `covUpdatedDate` datetime DEFAULT NULL,
  `covCoverageTypeId` varchar(255) DEFAULT NULL,
  `covDeductibleAmount` decimal(19,2) DEFAULT NULL,
  `covDeductibleCurrency` varchar(255) DEFAULT NULL,
  `covDescription` varchar(255) DEFAULT NULL,
  `covEffectiveDate` datetime DEFAULT NULL,
  `covEnabled` bit(1) DEFAULT NULL,
  `covExpiryDate` datetime DEFAULT NULL,
  `covId` varchar(255) DEFAULT NULL,
  `covLimitAmount` decimal(19,2) DEFAULT NULL,
  `covLimitCurrency` varchar(255) DEFAULT NULL,
  `covName` varchar(255) DEFAULT NULL,
  `covOptional` bit(1) DEFAULT NULL,
  `covBrokerUIDpar` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`covUID`,`REV`),
  KEY `FK102F010D52AE8777` (`REV`),
  CONSTRAINT `FK102F010D52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `creClaimRecovery`
--

DROP TABLE IF EXISTS `creClaimRecovery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `creClaimRecovery` (
  `creUID` bigint(20) NOT NULL,
  `creAttribute` longtext,
  `creCreatedBy` bigint(20) DEFAULT NULL,
  `creCreatedDate` datetime DEFAULT NULL,
  `creExternalSystemId` varchar(255) NOT NULL,
  `creForeignSystemId` varchar(255) DEFAULT NULL,
  `creLock` bit(1) DEFAULT NULL,
  `creSerialVersion` bigint(20) NOT NULL,
  `creUpdatedBy` bigint(20) DEFAULT NULL,
  `creUpdatedDate` datetime DEFAULT NULL,
  `creAmount` decimal(19,2) DEFAULT NULL,
  `creCurrency` varchar(255) DEFAULT NULL,
  `crePaymentType` varchar(255) DEFAULT NULL,
  `creReason` varchar(255) DEFAULT NULL,
  `creRecoveredDate` datetime DEFAULT NULL,
  `creRecoveryType` varchar(255) DEFAULT NULL,
  `creClaimSectionUIDcse` bigint(20) DEFAULT NULL,
  `creRecoveredFromUIDpar` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`creUID`),
  UNIQUE KEY `creExternalSystemId` (`creExternalSystemId`),
  KEY `FK98FE69BB48BDEBCF` (`creRecoveredFromUIDpar`),
  KEY `FK98FE69BB4A5DF6CA` (`creClaimSectionUIDcse`),
  CONSTRAINT `FK98FE69BB48BDEBCF` FOREIGN KEY (`creRecoveredFromUIDpar`) REFERENCES `parParty` (`parUID`),
  CONSTRAINT `FK98FE69BB4A5DF6CA` FOREIGN KEY (`creClaimSectionUIDcse`) REFERENCES `cseClaimSection` (`cseUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `creClaimRecovery_`
--

DROP TABLE IF EXISTS `creClaimRecovery_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `creClaimRecovery_` (
  `creUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `creAttribute` longtext,
  `creCreatedBy` bigint(20) DEFAULT NULL,
  `creCreatedDate` datetime DEFAULT NULL,
  `creExternalSystemId` varchar(255) DEFAULT NULL,
  `creForeignSystemId` varchar(255) DEFAULT NULL,
  `creUpdatedBy` bigint(20) DEFAULT NULL,
  `creUpdatedDate` datetime DEFAULT NULL,
  `creAmount` decimal(19,2) DEFAULT NULL,
  `creCurrency` varchar(255) DEFAULT NULL,
  `crePaymentType` varchar(255) DEFAULT NULL,
  `creReason` varchar(255) DEFAULT NULL,
  `creRecoveredDate` datetime DEFAULT NULL,
  `creRecoveryType` varchar(255) DEFAULT NULL,
  `creClaimSectionUIDcse` bigint(20) DEFAULT NULL,
  `creRecoveredFromUIDpar` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`creUID`,`REV`),
  KEY `FK86CECE0452AE8777` (`REV`),
  CONSTRAINT `FK86CECE0452AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cseClaimSection`
--

DROP TABLE IF EXISTS `cseClaimSection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cseClaimSection` (
  `cseUID` bigint(20) NOT NULL,
  `cseAttribute` longtext,
  `cseCreatedBy` bigint(20) DEFAULT NULL,
  `cseCreatedDate` datetime DEFAULT NULL,
  `cseExternalSystemId` varchar(255) NOT NULL,
  `cseForeignSystemId` varchar(255) DEFAULT NULL,
  `cseLock` bit(1) DEFAULT NULL,
  `cseSerialVersion` bigint(20) NOT NULL,
  `cseUpdatedBy` bigint(20) DEFAULT NULL,
  `cseUpdatedDate` datetime DEFAULT NULL,
  `cseEstimatedReserveAmount` decimal(19,2) DEFAULT NULL,
  `cseEstimatedReserveCurrency` varchar(255) DEFAULT NULL,
  `cseOutstandingClaimAmount` decimal(19,2) DEFAULT NULL,
  `cseOutstandingClaimCurrency` varchar(255) DEFAULT NULL,
  `cseTotalRecoveredAmount` decimal(19,2) DEFAULT NULL,
  `cseTotalRecoveredCurrency` varchar(255) DEFAULT NULL,
  `cseClaimUIDcla` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`cseUID`),
  UNIQUE KEY `cseExternalSystemId` (`cseExternalSystemId`),
  KEY `FK9794819E5A3FF86E` (`cseClaimUIDcla`),
  CONSTRAINT `FK9794819E5A3FF86E` FOREIGN KEY (`cseClaimUIDcla`) REFERENCES `claClaim` (`claUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cseClaimSection_`
--

DROP TABLE IF EXISTS `cseClaimSection_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cseClaimSection_` (
  `cseUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `cseAttribute` longtext,
  `cseCreatedBy` bigint(20) DEFAULT NULL,
  `cseCreatedDate` datetime DEFAULT NULL,
  `cseExternalSystemId` varchar(255) DEFAULT NULL,
  `cseForeignSystemId` varchar(255) DEFAULT NULL,
  `cseUpdatedBy` bigint(20) DEFAULT NULL,
  `cseUpdatedDate` datetime DEFAULT NULL,
  `cseEstimatedReserveAmount` decimal(19,2) DEFAULT NULL,
  `cseEstimatedReserveCurrency` varchar(255) DEFAULT NULL,
  `cseOutstandingClaimAmount` decimal(19,2) DEFAULT NULL,
  `cseOutstandingClaimCurrency` varchar(255) DEFAULT NULL,
  `cseTotalRecoveredAmount` decimal(19,2) DEFAULT NULL,
  `cseTotalRecoveredCurrency` varchar(255) DEFAULT NULL,
  `cseClaimUIDcla` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`cseUID`,`REV`),
  KEY `FK5AFBB28152AE8777` (`REV`),
  CONSTRAINT `FK5AFBB28152AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `csyContactSystem`
--

DROP TABLE IF EXISTS `csyContactSystem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csyContactSystem` (
  `csyDSC` varchar(31) NOT NULL,
  `csyUID` bigint(20) NOT NULL,
  `csyAttribute` longtext,
  `csyCreatedBy` bigint(20) DEFAULT NULL,
  `csyCreatedDate` datetime DEFAULT NULL,
  `csyExternalSystemId` varchar(255) NOT NULL,
  `csyForeignSystemId` varchar(255) DEFAULT NULL,
  `csyLock` bit(1) DEFAULT NULL,
  `csySerialVersion` bigint(20) NOT NULL,
  `csyUpdatedBy` bigint(20) DEFAULT NULL,
  `csyUpdatedDate` datetime DEFAULT NULL,
  `csyEndDate` timestamp(4) NULL DEFAULT NULL,
  `csyFullAddress` varchar(255) DEFAULT NULL,
  `csyPrimary` bit(1) DEFAULT NULL,
  `csyStartDate` timestamp(4) NULL DEFAULT NULL,
  `csyType` varchar(255) DEFAULT NULL,
  `csyCountry` varchar(255) DEFAULT NULL,
  `csyCounty` varchar(255) DEFAULT NULL,
  `csyLine1` varchar(255) DEFAULT NULL,
  `csyLine2` varchar(255) DEFAULT NULL,
  `csyLine3` varchar(255) DEFAULT NULL,
  `csyLine4` varchar(255) DEFAULT NULL,
  `csyLine5` varchar(255) DEFAULT NULL,
  `csyPostcode` varchar(255) DEFAULT NULL,
  `csyTown` varchar(255) DEFAULT NULL,
  `csyEmailAddress` varchar(255) DEFAULT NULL,
  `csyPhoneNumber` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`csyUID`),
  UNIQUE KEY `csyExternalSystemId` (`csyExternalSystemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `csyContactSystem_`
--

DROP TABLE IF EXISTS `csyContactSystem_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csyContactSystem_` (
  `csyDSC` varchar(31) NOT NULL,
  `csyUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `csyAttribute` longtext,
  `csyCreatedBy` bigint(20) DEFAULT NULL,
  `csyCreatedDate` datetime DEFAULT NULL,
  `csyExternalSystemId` varchar(255) DEFAULT NULL,
  `csyForeignSystemId` varchar(255) DEFAULT NULL,
  `csyUpdatedBy` bigint(20) DEFAULT NULL,
  `csyUpdatedDate` datetime DEFAULT NULL,
  `csyEndDate` timestamp(4) NULL DEFAULT NULL,
  `csyFullAddress` varchar(255) DEFAULT NULL,
  `csyPrimary` bit(1) DEFAULT NULL,
  `csyStartDate` timestamp(4) NULL DEFAULT NULL,
  `csyType` varchar(255) DEFAULT NULL,
  `csyPhoneNumber` varchar(255) DEFAULT NULL,
  `csyCountry` varchar(255) DEFAULT NULL,
  `csyCounty` varchar(255) DEFAULT NULL,
  `csyLine1` varchar(255) DEFAULT NULL,
  `csyLine2` varchar(255) DEFAULT NULL,
  `csyLine3` varchar(255) DEFAULT NULL,
  `csyLine4` varchar(255) DEFAULT NULL,
  `csyLine5` varchar(255) DEFAULT NULL,
  `csyPostcode` varchar(255) DEFAULT NULL,
  `csyTown` varchar(255) DEFAULT NULL,
  `csyEmailAddress` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`csyUID`,`REV`),
  KEY `FK40BBEE3988B4F168` (`REV`),
  CONSTRAINT `FK40BBEE3988B4F168` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dcoDocumentContent`
--

DROP TABLE IF EXISTS `dcoDocumentContent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dcoDocumentContent` (
  `dcoUID` bigint(20) NOT NULL,
  `dcoAttribute` longtext,
  `dcoCreatedDate` datetime DEFAULT NULL,
  `dcoExternalSystemId` varchar(255) NOT NULL,
  `dcoForeignSystemId` varchar(255) DEFAULT NULL,
  `dcoLock` tinyint(1) NOT NULL,
  `dcoSerialVersion` bigint(20) NOT NULL,
  `dcoUpdatedDate` datetime DEFAULT NULL,
  `dcoInTableContent` longblob,
  `dcoDocumentUIDdoc` bigint(20) DEFAULT NULL,
  `dcoProductTypeId` varchar(255) DEFAULT NULL,
  `dcoCreatedBy` bigint(20) DEFAULT NULL,
  `dcoUpdatedBy` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`dcoUID`),
  UNIQUE KEY `dcoExternalSystemId` (`dcoExternalSystemId`),
  KEY `FK80F4B0EE5E88B029` (`dcoDocumentUIDdoc`),
  CONSTRAINT `FK80F4B0EE5E88B029` FOREIGN KEY (`dcoDocumentUIDdoc`) REFERENCES `docDocument` (`docUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dcoDocumentContent_`
--

DROP TABLE IF EXISTS `dcoDocumentContent_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dcoDocumentContent_` (
  `dcoUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `dcoAttribute` longtext,
  `dcoCreatedBy` bigint(20) DEFAULT NULL,
  `dcoCreatedDate` datetime DEFAULT NULL,
  `dcoExternalSystemId` varchar(255) DEFAULT NULL,
  `dcoForeignSystemId` varchar(255) DEFAULT NULL,
  `dcoUpdatedBy` bigint(20) DEFAULT NULL,
  `dcoUpdatedDate` datetime DEFAULT NULL,
  `dcoInTableContent` longblob,
  `dcoProductTypeId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`dcoUID`,`REV`),
  KEY `FK9DA16D3152AE8777` (`REV`),
  CONSTRAINT `FK9DA16D3152AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `docDocument`
--

DROP TABLE IF EXISTS `docDocument`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `docDocument` (
  `docUID` bigint(20) NOT NULL,
  `docAttribute` longtext,
  `docCreatedDate` datetime DEFAULT NULL,
  `docExternalSystemId` varchar(255) NOT NULL,
  `docForeignSystemId` varchar(255) DEFAULT NULL,
  `docLock` tinyint(1) NOT NULL,
  `docSerialVersion` bigint(20) NOT NULL,
  `docUpdatedDate` datetime DEFAULT NULL,
  `docDescription` longtext,
  `docFileName` varchar(255) DEFAULT NULL,
  `docMimeType` varchar(255) DEFAULT NULL,
  `docOtherType` varchar(255) DEFAULT NULL,
  `docTitle` varchar(255) DEFAULT NULL,
  `docType` varchar(255) DEFAULT NULL,
  `docCreatedBy` bigint(20) DEFAULT NULL,
  `docUpdatedBy` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`docUID`),
  UNIQUE KEY `docExternalSystemId` (`docExternalSystemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `docDocument_`
--

DROP TABLE IF EXISTS `docDocument_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `docDocument_` (
  `docUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `docAttribute` longtext,
  `docCreatedBy` bigint(20) DEFAULT NULL,
  `docCreatedDate` datetime DEFAULT NULL,
  `docExternalSystemId` varchar(255) DEFAULT NULL,
  `docForeignSystemId` varchar(255) DEFAULT NULL,
  `docUpdatedBy` bigint(20) DEFAULT NULL,
  `docUpdatedDate` datetime DEFAULT NULL,
  `docDescription` longtext,
  `docFileName` varchar(255) DEFAULT NULL,
  `docMimeType` varchar(255) DEFAULT NULL,
  `docOtherType` varchar(255) DEFAULT NULL,
  `docTitle` varchar(255) DEFAULT NULL,
  `docType` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`docUID`,`REV`),
  KEY `FKB8A44C4C52AE8777` (`REV`),
  CONSTRAINT `FKB8A44C4C52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dplDocumentPlaceholder`
--

DROP TABLE IF EXISTS `dplDocumentPlaceholder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dplDocumentPlaceholder` (
  `dplUID` bigint(20) NOT NULL,
  `dplAttribute` longtext,
  `dplCreatedBy` bigint(20) DEFAULT NULL,
  `dplCreatedDate` datetime DEFAULT NULL,
  `dplExternalSystemId` varchar(255) NOT NULL,
  `dplForeignSystemId` varchar(255) DEFAULT NULL,
  `dplLock` bit(1) DEFAULT NULL,
  `dplSerialVersion` bigint(20) NOT NULL,
  `dplUpdatedBy` bigint(20) DEFAULT NULL,
  `dplUpdatedDate` datetime DEFAULT NULL,
  `dplDescription` longtext,
  `dplOtherType` varchar(255) DEFAULT NULL,
  `dplTitle` varchar(255) DEFAULT NULL,
  `dplType` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`dplUID`),
  UNIQUE KEY `dplExternalSystemId` (`dplExternalSystemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dplDocumentPlaceholder_`
--

DROP TABLE IF EXISTS `dplDocumentPlaceholder_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dplDocumentPlaceholder_` (
  `dplUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `dplAttribute` longtext,
  `dplCreatedBy` bigint(20) DEFAULT NULL,
  `dplCreatedDate` datetime DEFAULT NULL,
  `dplExternalSystemId` varchar(255) DEFAULT NULL,
  `dplForeignSystemId` varchar(255) DEFAULT NULL,
  `dplUpdatedBy` bigint(20) DEFAULT NULL,
  `dplUpdatedDate` datetime DEFAULT NULL,
  `dplDescription` longtext,
  `dplOtherType` varchar(255) DEFAULT NULL,
  `dplTitle` varchar(255) DEFAULT NULL,
  `dplType` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`dplUID`,`REV`),
  KEY `FKB6DEC78788B4F168` (`REV`),
  CONSTRAINT `FKB6DEC78788B4F168` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dreDocumentRequest`
--

DROP TABLE IF EXISTS `dreDocumentRequest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dreDocumentRequest` (
  `dreUID` bigint(20) NOT NULL,
  `dreAttribute` longtext,
  `dreCreatedDate` datetime DEFAULT NULL,
  `dreExternalSystemId` varchar(255) NOT NULL,
  `dreForeignSystemId` varchar(255) DEFAULT NULL,
  `dreLock` tinyint(1) NOT NULL,
  `dreSerialVersion` bigint(20) NOT NULL,
  `dreUpdatedDate` datetime DEFAULT NULL,
  `dreDocumentType` varchar(255) DEFAULT NULL,
  `dreDocumentUID` bigint(20) DEFAULT NULL,
  `dreRequestId` varchar(255) DEFAULT NULL,
  `dreRequestType` varchar(255) DEFAULT NULL,
  `dreSourceUID` bigint(20) DEFAULT NULL,
  `dreCreatedBy` bigint(20) DEFAULT NULL,
  `dreUpdatedBy` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`dreUID`),
  UNIQUE KEY `dreExternalSystemId` (`dreExternalSystemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fsrForeignSystemReference`
--

DROP TABLE IF EXISTS `fsrForeignSystemReference`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fsrForeignSystemReference` (
  `fsrUID` bigint(20) NOT NULL,
  `fsrAttribute` longtext,
  `fsrCreatedDate` datetime DEFAULT NULL,
  `fsrExternalSystemId` varchar(255) NOT NULL,
  `fsrForeignSystemId` varchar(255) DEFAULT NULL,
  `fsrLock` bit(1) DEFAULT NULL,
  `fsrSerialVersion` bigint(20) NOT NULL,
  `fsrUpdatedDate` datetime DEFAULT NULL,
  `fsrReference` varchar(255) DEFAULT NULL,
  `fsrType` varchar(255) DEFAULT NULL,
  `fsrCreatedBy` bigint(20) DEFAULT NULL,
  `fsrUpdatedBy` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`fsrUID`),
  UNIQUE KEY `fsrExternalSystemId` (`fsrExternalSystemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fsrForeignSystemReference_`
--

DROP TABLE IF EXISTS `fsrForeignSystemReference_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fsrForeignSystemReference_` (
  `fsrUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `fsrAttribute` longtext,
  `fsrCreatedBy` bigint(20) DEFAULT NULL,
  `fsrCreatedDate` datetime DEFAULT NULL,
  `fsrExternalSystemId` varchar(255) DEFAULT NULL,
  `fsrForeignSystemId` varchar(255) DEFAULT NULL,
  `fsrUpdatedBy` bigint(20) DEFAULT NULL,
  `fsrUpdatedDate` datetime DEFAULT NULL,
  `fsrReference` varchar(255) DEFAULT NULL,
  `fsrType` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`fsrUID`,`REV`),
  KEY `FK6AE513F252AE8777` (`REV`),
  CONSTRAINT `FK6AE513F252AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jAssAssAss`
--

DROP TABLE IF EXISTS `jAssAssAss`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jAssAssAss` (
  `UIDass` bigint(20) NOT NULL,
  `assetUIDass` bigint(20) NOT NULL,
  UNIQUE KEY `assetUIDass` (`assetUIDass`),
  KEY `FKAED1A4D7C30A5D40` (`assetUIDass`),
  KEY `FKAED1A4D7C7D6650` (`UIDass`),
  CONSTRAINT `FKAED1A4D7C30A5D40` FOREIGN KEY (`assetUIDass`) REFERENCES `assAsset` (`assUID`),
  CONSTRAINT `FKAED1A4D7C7D6650` FOREIGN KEY (`UIDass`) REFERENCES `assAsset` (`assUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jAssAssAss_`
--

DROP TABLE IF EXISTS `jAssAssAss_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jAssAssAss_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDass` bigint(20) NOT NULL DEFAULT '0',
  `assetUIDass` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDass`,`assetUIDass`),
  KEY `FK2B62F66852AE8777` (`REV`),
  CONSTRAINT `FK2B62F66852AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jClaAdoDoc`
--

DROP TABLE IF EXISTS `jClaAdoDoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jClaAdoDoc` (
  `UIDcla` bigint(20) NOT NULL,
  `archivedDocumentUIDdoc` bigint(20) NOT NULL,
  UNIQUE KEY `archivedDocumentUIDdoc` (`archivedDocumentUIDdoc`),
  KEY `FK472CF6DAE0CB4A47` (`UIDcla`),
  KEY `FK472CF6DA469CDCBB` (`archivedDocumentUIDdoc`),
  CONSTRAINT `FK472CF6DA469CDCBB` FOREIGN KEY (`archivedDocumentUIDdoc`) REFERENCES `docDocument` (`docUID`),
  CONSTRAINT `FK472CF6DAE0CB4A47` FOREIGN KEY (`UIDcla`) REFERENCES `claClaim` (`claUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jClaAdoDoc_`
--

DROP TABLE IF EXISTS `jClaAdoDoc_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jClaAdoDoc_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDcla` bigint(20) NOT NULL DEFAULT '0',
  `archivedDocumentUIDdoc` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDcla`,`archivedDocumentUIDdoc`),
  KEY `FK9E71E4C552AE8777` (`REV`),
  CONSTRAINT `FK9E71E4C552AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jClaAssAss`
--

DROP TABLE IF EXISTS `jClaAssAss`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jClaAssAss` (
  `UIDcla` bigint(20) NOT NULL,
  `assetUIDass` bigint(20) NOT NULL,
  UNIQUE KEY `assetUIDass` (`assetUIDass`),
  KEY `FK48021E2EE0CB4A47` (`UIDcla`),
  KEY `FK48021E2EC30A5D40` (`assetUIDass`),
  CONSTRAINT `FK48021E2EC30A5D40` FOREIGN KEY (`assetUIDass`) REFERENCES `assAsset` (`assUID`),
  CONSTRAINT `FK48021E2EE0CB4A47` FOREIGN KEY (`UIDcla`) REFERENCES `claClaim` (`claUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jClaAssAss_`
--

DROP TABLE IF EXISTS `jClaAssAss_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jClaAssAss_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDcla` bigint(20) NOT NULL DEFAULT '0',
  `assetUIDass` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDcla`,`assetUIDass`),
  KEY `FKB841A7F152AE8777` (`REV`),
  CONSTRAINT `FKB841A7F152AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jClaClaCls`
--

DROP TABLE IF EXISTS `jClaClaCls`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jClaClaCls` (
  `UIDcla` bigint(20) NOT NULL,
  `clauseUIDcls` bigint(20) NOT NULL,
  UNIQUE KEY `clauseUIDcls` (`clauseUIDcls`),
  KEY `FK4B0102E0E0CB4A47` (`UIDcla`),
  KEY `FK4B0102E013FCA529` (`clauseUIDcls`),
  CONSTRAINT `FK4B0102E013FCA529` FOREIGN KEY (`clauseUIDcls`) REFERENCES `clsClause` (`clsUID`),
  CONSTRAINT `FK4B0102E0E0CB4A47` FOREIGN KEY (`UIDcla`) REFERENCES `claClaim` (`claUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jClaClaCls_`
--

DROP TABLE IF EXISTS `jClaClaCls_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jClaClaCls_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDcla` bigint(20) NOT NULL DEFAULT '0',
  `clauseUIDcls` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDcla`,`clauseUIDcls`),
  KEY `FK151F597F52AE8777` (`REV`),
  CONSTRAINT `FK151F597F52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jClaDocDoc`
--

DROP TABLE IF EXISTS `jClaDocDoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jClaDocDoc` (
  `UIDcla` bigint(20) NOT NULL,
  `documentUIDdoc` bigint(20) NOT NULL,
  UNIQUE KEY `documentUIDdoc` (`documentUIDdoc`),
  KEY `FK4CE10ECEAAA21859` (`documentUIDdoc`),
  KEY `FK4CE10ECEE0CB4A47` (`UIDcla`),
  CONSTRAINT `FK4CE10ECEAAA21859` FOREIGN KEY (`documentUIDdoc`) REFERENCES `docDocument` (`docUID`),
  CONSTRAINT `FK4CE10ECEE0CB4A47` FOREIGN KEY (`UIDcla`) REFERENCES `claClaim` (`claUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jClaDocDoc_`
--

DROP TABLE IF EXISTS `jClaDocDoc_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jClaDocDoc_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDcla` bigint(20) NOT NULL DEFAULT '0',
  `documentUIDdoc` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDcla`,`documentUIDdoc`),
  KEY `FK4F40CB5152AE8777` (`REV`),
  CONSTRAINT `FK4F40CB5152AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jClaDplDpl`
--

DROP TABLE IF EXISTS `jClaDplDpl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jClaDplDpl` (
  `UIDcla` bigint(20) NOT NULL,
  `documentPlaceholderUIDdpl` bigint(20) NOT NULL,
  UNIQUE KEY `documentPlaceholderUIDdpl` (`documentPlaceholderUIDdpl`),
  KEY `FK4CF33DCE6F9D1585` (`documentPlaceholderUIDdpl`),
  KEY `FK4CF33DCEE0CB4A47` (`UIDcla`),
  CONSTRAINT `FK4CF33DCE6F9D1585` FOREIGN KEY (`documentPlaceholderUIDdpl`) REFERENCES `dplDocumentPlaceholder` (`dplUID`),
  CONSTRAINT `FK4CF33DCEE0CB4A47` FOREIGN KEY (`UIDcla`) REFERENCES `claClaim` (`claUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jClaDplDpl_`
--

DROP TABLE IF EXISTS `jClaDplDpl_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jClaDplDpl_` (
  `REV` int(11) NOT NULL,
  `UIDcla` bigint(20) NOT NULL,
  `documentPlaceholderUIDdpl` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDcla`,`documentPlaceholderUIDdpl`),
  KEY `FK51747C5188B4F168` (`REV`),
  CONSTRAINT `FK51747C5188B4F168` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jClaNotNot`
--

DROP TABLE IF EXISTS `jClaNotNot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jClaNotNot` (
  `UIDcla` bigint(20) NOT NULL,
  `noteUIDnot` bigint(20) NOT NULL,
  UNIQUE KEY `noteUIDnot` (`noteUIDnot`),
  KEY `FK5DF964EEB3C35E1F` (`noteUIDnot`),
  KEY `FK5DF964EEE0CB4A47` (`UIDcla`),
  CONSTRAINT `FK5DF964EEB3C35E1F` FOREIGN KEY (`noteUIDnot`) REFERENCES `notNote` (`notUID`),
  CONSTRAINT `FK5DF964EEE0CB4A47` FOREIGN KEY (`UIDcla`) REFERENCES `claClaim` (`claUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jClaNotNot_`
--

DROP TABLE IF EXISTS `jClaNotNot_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jClaNotNot_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDcla` bigint(20) NOT NULL DEFAULT '0',
  `noteUIDnot` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDcla`,`noteUIDnot`),
  KEY `FK6133393152AE8777` (`REV`),
  CONSTRAINT `FK6133393152AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jClaProPro`
--

DROP TABLE IF EXISTS `jClaProPro`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jClaProPro` (
  `UIDcla` bigint(20) NOT NULL,
  `partyRoleUIDpro` bigint(20) NOT NULL,
  UNIQUE KEY `partyRoleUIDpro` (`partyRoleUIDpro`),
  KEY `FK618B1EAE92962E76` (`partyRoleUIDpro`),
  KEY `FK618B1EAEE0CB4A47` (`UIDcla`),
  CONSTRAINT `FK618B1EAE92962E76` FOREIGN KEY (`partyRoleUIDpro`) REFERENCES `proPartyRole` (`proUID`),
  CONSTRAINT `FK618B1EAEE0CB4A47` FOREIGN KEY (`UIDcla`) REFERENCES `claClaim` (`claUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jClaProPro_`
--

DROP TABLE IF EXISTS `jClaProPro_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jClaProPro_` (
  `REV` int(11) NOT NULL,
  `UIDcla` bigint(20) NOT NULL,
  `partyRoleUIDpro` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDcla`,`partyRoleUIDpro`),
  KEY `FKCFD8B77188B4F168` (`REV`),
  CONSTRAINT `FKCFD8B77188B4F168` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jDcoFsrFsr`
--

DROP TABLE IF EXISTS `jDcoFsrFsr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jDcoFsrFsr` (
  `UIDdco` bigint(20) NOT NULL,
  `foreignSystemReferenceUIDfsr` bigint(20) NOT NULL,
  PRIMARY KEY (`UIDdco`,`foreignSystemReferenceUIDfsr`),
  UNIQUE KEY `foreignSystemReferenceUIDfsr` (`foreignSystemReferenceUIDfsr`),
  KEY `FK227B106693BC173D` (`foreignSystemReferenceUIDfsr`),
  KEY `FK227B1066E2808C83` (`UIDdco`),
  CONSTRAINT `FK227B106693BC173D` FOREIGN KEY (`foreignSystemReferenceUIDfsr`) REFERENCES `fsrForeignSystemReference` (`fsrUID`),
  CONSTRAINT `FK227B1066E2808C83` FOREIGN KEY (`UIDdco`) REFERENCES `dcoDocumentContent` (`dcoUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jDcoFsrFsr_`
--

DROP TABLE IF EXISTS `jDcoFsrFsr_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jDcoFsrFsr_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDdco` bigint(20) NOT NULL DEFAULT '0',
  `foreignSystemReferenceUIDfsr` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDdco`,`foreignSystemReferenceUIDfsr`),
  KEY `FK2CE6FCB952AE8777` (`REV`),
  CONSTRAINT `FK2CE6FCB952AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jJouJliJli`
--

DROP TABLE IF EXISTS `jJouJliJli`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jJouJliJli` (
  `UIDjou` bigint(20) NOT NULL,
  `journalLineUIDjli` bigint(20) NOT NULL,
  UNIQUE KEY `journalLineUIDjli` (`journalLineUIDjli`),
  KEY `FKBE75DD265F0D7FE4` (`journalLineUIDjli`),
  KEY `FKBE75DD26F010F3CE` (`UIDjou`),
  CONSTRAINT `FKBE75DD265F0D7FE4` FOREIGN KEY (`journalLineUIDjli`) REFERENCES `jliJournalLine` (`jliUID`),
  CONSTRAINT `FKBE75DD26F010F3CE` FOREIGN KEY (`UIDjou`) REFERENCES `jouJournal` (`jouUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jJouJliJli_`
--

DROP TABLE IF EXISTS `jJouJliJli_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jJouJliJli_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDjou` bigint(20) NOT NULL DEFAULT '0',
  `journalLineUIDjli` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDjou`,`journalLineUIDjli`),
  KEY `FK1045C7F952AE8777` (`REV`),
  CONSTRAINT `FK1045C7F952AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jLedJliJli`
--

DROP TABLE IF EXISTS `jLedJliJli`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jLedJliJli` (
  `UIDled` bigint(20) NOT NULL,
  `journalLineUIDjli` bigint(20) NOT NULL,
  UNIQUE KEY `journalLineUIDjli` (`journalLineUIDjli`),
  KEY `FK54E90B21D18F1B2D` (`UIDled`),
  KEY `FK54E90B215F0D7FE4` (`journalLineUIDjli`),
  CONSTRAINT `FK54E90B215F0D7FE4` FOREIGN KEY (`journalLineUIDjli`) REFERENCES `jliJournalLine` (`jliUID`),
  CONSTRAINT `FK54E90B21D18F1B2D` FOREIGN KEY (`UIDled`) REFERENCES `ledLedger` (`ledUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jLedJliJli_`
--

DROP TABLE IF EXISTS `jLedJliJli_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jLedJliJli_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDled` bigint(20) NOT NULL DEFAULT '0',
  `journalLineUIDjli` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDled`,`journalLineUIDjli`),
  KEY `FK4838595E52AE8777` (`REV`),
  CONSTRAINT `FK4838595E52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jParAdoDoc`
--

DROP TABLE IF EXISTS `jParAdoDoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jParAdoDoc` (
  `UIDpar` bigint(20) NOT NULL,
  `archivedDocumentUIDdoc` bigint(20) NOT NULL,
  UNIQUE KEY `archivedDocumentUIDdoc` (`archivedDocumentUIDdoc`),
  KEY `FKDB439BA3469CDCBB` (`archivedDocumentUIDdoc`),
  KEY `FKDB439BA343E35FF8` (`UIDpar`),
  CONSTRAINT `FKDB439BA343E35FF8` FOREIGN KEY (`UIDpar`) REFERENCES `parParty` (`parUID`),
  CONSTRAINT `FKDB439BA3469CDCBB` FOREIGN KEY (`archivedDocumentUIDdoc`) REFERENCES `docDocument` (`docUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jParAdoDoc_`
--

DROP TABLE IF EXISTS `jParAdoDoc_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jParAdoDoc_` (
  `REV` int(11) NOT NULL,
  `UIDpar` bigint(20) NOT NULL,
  `ArchivedDocumentUIDdoc` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpar`,`ArchivedDocumentUIDdoc`),
  KEY `FK8D2FD91C52AE8777` (`REV`),
  CONSTRAINT `FK8D2FD91C52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jParCsyCsy`
--

DROP TABLE IF EXISTS `jParCsyCsy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jParCsyCsy` (
  `UIDpar` bigint(20) NOT NULL,
  `contactSystemUIDcsy` bigint(20) NOT NULL,
  UNIQUE KEY `contactSystemUIDcsy` (`contactSystemUIDcsy`),
  KEY `FKDF8535F7E86376F8` (`contactSystemUIDcsy`),
  KEY `FKDF8535F743E35FF8` (`UIDpar`),
  CONSTRAINT `FKDF8535F743E35FF8` FOREIGN KEY (`UIDpar`) REFERENCES `parParty` (`parUID`),
  CONSTRAINT `FKDF8535F7E86376F8` FOREIGN KEY (`contactSystemUIDcsy`) REFERENCES `csyContactSystem` (`csyUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jParCsyCsy_`
--

DROP TABLE IF EXISTS `jParCsyCsy_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jParCsyCsy_` (
  `REV` int(11) NOT NULL,
  `UIDpar` bigint(20) NOT NULL,
  `contactSystemUIDcsy` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpar`,`contactSystemUIDcsy`),
  KEY `FK1121894888B4F168` (`REV`),
  CONSTRAINT `FK1121894888B4F168` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jParDocDoc`
--

DROP TABLE IF EXISTS `jParDocDoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jParDocDoc` (
  `UIDpar` bigint(20) NOT NULL,
  `documentUIDdoc` bigint(20) NOT NULL,
  UNIQUE KEY `documentUIDdoc` (`documentUIDdoc`),
  KEY `FKE0F7B397AAA21859` (`documentUIDdoc`),
  KEY `FKE0F7B39743E35FF8` (`UIDpar`),
  CONSTRAINT `FKE0F7B39743E35FF8` FOREIGN KEY (`UIDpar`) REFERENCES `parParty` (`parUID`),
  CONSTRAINT `FKE0F7B397AAA21859` FOREIGN KEY (`documentUIDdoc`) REFERENCES `docDocument` (`docUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jParDocDoc_`
--

DROP TABLE IF EXISTS `jParDocDoc_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jParDocDoc_` (
  `REV` int(11) NOT NULL,
  `UIDpar` bigint(20) NOT NULL,
  `DocumentUIDdoc` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpar`,`DocumentUIDdoc`),
  KEY `FK3DFEBFA852AE8777` (`REV`),
  CONSTRAINT `FK3DFEBFA852AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jParDplDpl`
--

DROP TABLE IF EXISTS `jParDplDpl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jParDplDpl` (
  `UIDpar` bigint(20) NOT NULL,
  `documentPlaceholderUIDdpl` bigint(20) NOT NULL,
  UNIQUE KEY `documentPlaceholderUIDdpl` (`documentPlaceholderUIDdpl`),
  KEY `FKE109E2976F9D1585` (`documentPlaceholderUIDdpl`),
  KEY `FKE109E29743E35FF8` (`UIDpar`),
  CONSTRAINT `FKE109E29743E35FF8` FOREIGN KEY (`UIDpar`) REFERENCES `parParty` (`parUID`),
  CONSTRAINT `FKE109E2976F9D1585` FOREIGN KEY (`documentPlaceholderUIDdpl`) REFERENCES `dplDocumentPlaceholder` (`dplUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jParDplDpl_`
--

DROP TABLE IF EXISTS `jParDplDpl_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jParDplDpl_` (
  `REV` int(11) NOT NULL,
  `UIDpar` bigint(20) NOT NULL,
  `documentPlaceholderUIDdpl` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpar`,`documentPlaceholderUIDdpl`),
  KEY `FK403270A888B4F168` (`REV`),
  CONSTRAINT `FK403270A888B4F168` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jParPmePme`
--

DROP TABLE IF EXISTS `jParPmePme`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jParPmePme` (
  `UIDpar` bigint(20) NOT NULL,
  `paymentMethodUIDpme` bigint(20) NOT NULL,
  UNIQUE KEY `paymentMethodUIDpme` (`paymentMethodUIDpme`),
  KEY `FKF556C1974212110A` (`paymentMethodUIDpme`),
  KEY `FKF556C19743E35FF8` (`UIDpar`),
  CONSTRAINT `FKF556C1974212110A` FOREIGN KEY (`paymentMethodUIDpme`) REFERENCES `pmePaymentMethod` (`pmeUID`),
  CONSTRAINT `FKF556C19743E35FF8` FOREIGN KEY (`UIDpar`) REFERENCES `parParty` (`parUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jParPmePme_`
--

DROP TABLE IF EXISTS `jParPmePme_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jParPmePme_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDpar` bigint(20) NOT NULL DEFAULT '0',
  `paymentMethodUIDpme` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpar`,`paymentMethodUIDpme`),
  KEY `FKB58171A852AE8777` (`REV`),
  CONSTRAINT `FKB58171A852AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jParPrePrl`
--

DROP TABLE IF EXISTS `jParPrePrl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jParPrePrl` (
  `UIDpar` bigint(20) NOT NULL,
  `partyRelationshipUIDprl` bigint(20) NOT NULL,
  UNIQUE KEY `partyRelationshipUIDprl` (`partyRelationshipUIDprl`),
  KEY `FKF59D37BED53F4377` (`partyRelationshipUIDprl`),
  KEY `FKF59D37BE43E35FF8` (`UIDpar`),
  CONSTRAINT `FKF59D37BE43E35FF8` FOREIGN KEY (`UIDpar`) REFERENCES `parParty` (`parUID`),
  CONSTRAINT `FKF59D37BED53F4377` FOREIGN KEY (`partyRelationshipUIDprl`) REFERENCES `prlPartyRelationship` (`prlUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jParPrePrl_`
--

DROP TABLE IF EXISTS `jParPrePrl_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jParPrePrl_` (
  `REV` int(11) NOT NULL,
  `UIDpar` bigint(20) NOT NULL,
  `partyRelationshipUIDprl` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpar`,`partyRelationshipUIDprl`),
  KEY `FKBE09C06188B4F168` (`REV`),
  CONSTRAINT `FKBE09C06188B4F168` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPhoNotNot`
--

DROP TABLE IF EXISTS `jPhoNotNot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPhoNotNot` (
  `UIDpho` bigint(20) NOT NULL,
  `noteUIDnot` bigint(20) NOT NULL,
  UNIQUE KEY `noteUIDnot` (`noteUIDnot`),
  KEY `FK2A85920DB3C35E1F` (`noteUIDnot`),
  KEY `FK2A85920D4CE2A575` (`UIDpho`),
  CONSTRAINT `FK2A85920D4CE2A575` FOREIGN KEY (`UIDpho`) REFERENCES `phoPaymentHoliday` (`phoUID`),
  CONSTRAINT `FK2A85920DB3C35E1F` FOREIGN KEY (`noteUIDnot`) REFERENCES `notNote` (`notUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPhoNotNot_`
--

DROP TABLE IF EXISTS `jPhoNotNot_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPhoNotNot_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDpho` bigint(20) NOT NULL DEFAULT '0',
  `noteUIDnot` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpho`,`noteUIDnot`),
  KEY `FK262CAFF252AE8777` (`REV`),
  CONSTRAINT `FK262CAFF252AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolAdoDoc`
--

DROP TABLE IF EXISTS `jPolAdoDoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolAdoDoc` (
  `UIDpol` bigint(20) NOT NULL,
  `archivedDocumentUIDdoc` bigint(20) NOT NULL,
  UNIQUE KEY `archivedDocumentUIDdoc` (`archivedDocumentUIDdoc`),
  KEY `FK4C2EAC4F469CDCBB` (`archivedDocumentUIDdoc`),
  KEY `FK4C2EAC4F84156FE0` (`UIDpol`),
  CONSTRAINT `FK4C2EAC4F469CDCBB` FOREIGN KEY (`archivedDocumentUIDdoc`) REFERENCES `docDocument` (`docUID`),
  CONSTRAINT `FK4C2EAC4F84156FE0` FOREIGN KEY (`UIDpol`) REFERENCES `polPolicy` (`polUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolAdoDoc_`
--

DROP TABLE IF EXISTS `jPolAdoDoc_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolAdoDoc_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDpol` bigint(20) NOT NULL DEFAULT '0',
  `archivedDocumentUIDdoc` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpol`,`archivedDocumentUIDdoc`),
  KEY `FK39A6DDF052AE8777` (`REV`),
  CONSTRAINT `FK39A6DDF052AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolAslAsh`
--

DROP TABLE IF EXISTS `jPolAslAsh`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolAslAsh` (
  `UIDpol` bigint(20) NOT NULL,
  `assessmentSheetListUIDash` bigint(20) NOT NULL,
  `sheet_name` varchar(255) NOT NULL,
  PRIMARY KEY (`UIDpol`,`sheet_name`),
  UNIQUE KEY `assessmentSheetListUIDash` (`assessmentSheetListUIDash`),
  KEY `FK4D00A4FF51048E0D` (`assessmentSheetListUIDash`),
  KEY `FK4D00A4FF84156FE0` (`UIDpol`),
  CONSTRAINT `FK4D00A4FF51048E0D` FOREIGN KEY (`assessmentSheetListUIDash`) REFERENCES `ashAssessmentSheet` (`ashUID`),
  CONSTRAINT `FK4D00A4FF84156FE0` FOREIGN KEY (`UIDpol`) REFERENCES `polPolicy` (`polUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolAslAsh_`
--

DROP TABLE IF EXISTS `jPolAslAsh_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolAslAsh_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDpol` bigint(20) NOT NULL DEFAULT '0',
  `assessmentSheetListUIDash` bigint(20) NOT NULL DEFAULT '0',
  `sheet_name` varchar(255) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpol`,`assessmentSheetListUIDash`,`sheet_name`),
  KEY `FK5313FB4052AE8777` (`REV`),
  CONSTRAINT `FK5313FB4052AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolAssAss`
--

DROP TABLE IF EXISTS `jPolAssAss`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolAssAss` (
  `UIDpol` bigint(20) NOT NULL,
  `assetUIDass` bigint(20) NOT NULL,
  UNIQUE KEY `assetUIDass` (`assetUIDass`),
  KEY `FK4D03D3A384156FE0` (`UIDpol`),
  KEY `FK4D03D3A3C30A5D40` (`assetUIDass`),
  CONSTRAINT `FK4D03D3A384156FE0` FOREIGN KEY (`UIDpol`) REFERENCES `polPolicy` (`polUID`),
  CONSTRAINT `FK4D03D3A3C30A5D40` FOREIGN KEY (`assetUIDass`) REFERENCES `assAsset` (`assUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolAssAss_`
--

DROP TABLE IF EXISTS `jPolAssAss_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolAssAss_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDpol` bigint(20) NOT NULL DEFAULT '0',
  `assetUIDass` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpol`,`assetUIDass`),
  KEY `FK5376A11C52AE8777` (`REV`),
  CONSTRAINT `FK5376A11C52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolClaCls`
--

DROP TABLE IF EXISTS `jPolClaCls`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolClaCls` (
  `UIDpol` bigint(20) NOT NULL,
  `clauseUIDcls` bigint(20) NOT NULL,
  UNIQUE KEY `clauseUIDcls` (`clauseUIDcls`),
  KEY `FK5002B85584156FE0` (`UIDpol`),
  KEY `FK5002B85513FCA529` (`clauseUIDcls`),
  CONSTRAINT `FK5002B85513FCA529` FOREIGN KEY (`clauseUIDcls`) REFERENCES `clsClause` (`clsUID`),
  CONSTRAINT `FK5002B85584156FE0` FOREIGN KEY (`UIDpol`) REFERENCES `polPolicy` (`polUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolClaCls_`
--

DROP TABLE IF EXISTS `jPolClaCls_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolClaCls_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDpol` bigint(20) NOT NULL DEFAULT '0',
  `clauseUIDcls` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpol`,`clauseUIDcls`),
  KEY `FKB05452AA52AE8777` (`REV`),
  CONSTRAINT `FKB05452AA52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolCovCov`
--

DROP TABLE IF EXISTS `jPolCovCov`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolCovCov` (
  `UIDpol` bigint(20) NOT NULL,
  `coverageUIDcov` bigint(20) NOT NULL,
  UNIQUE KEY `coverageUIDcov` (`coverageUIDcov`),
  KEY `FK50368B0384156FE0` (`UIDpol`),
  KEY `FK50368B033DED8F7B` (`coverageUIDcov`),
  CONSTRAINT `FK50368B033DED8F7B` FOREIGN KEY (`coverageUIDcov`) REFERENCES `covCoverage` (`covUID`),
  CONSTRAINT `FK50368B0384156FE0` FOREIGN KEY (`UIDpol`) REFERENCES `polPolicy` (`polUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolCovCov_`
--

DROP TABLE IF EXISTS `jPolCovCov_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolCovCov_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDpol` bigint(20) NOT NULL DEFAULT '0',
  `coverageUIDcov` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpol`,`coverageUIDcov`),
  KEY `FKB69AD5BC52AE8777` (`REV`),
  CONSTRAINT `FKB69AD5BC52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolDocDoc`
--

DROP TABLE IF EXISTS `jPolDocDoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolDocDoc` (
  `UIDpol` bigint(20) NOT NULL,
  `documentUIDdoc` bigint(20) NOT NULL,
  UNIQUE KEY `documentUIDdoc` (`documentUIDdoc`),
  KEY `FK51E2C443AAA21859` (`documentUIDdoc`),
  KEY `FK51E2C44384156FE0` (`UIDpol`),
  CONSTRAINT `FK51E2C44384156FE0` FOREIGN KEY (`UIDpol`) REFERENCES `polPolicy` (`polUID`),
  CONSTRAINT `FK51E2C443AAA21859` FOREIGN KEY (`documentUIDdoc`) REFERENCES `docDocument` (`docUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolDocDoc_`
--

DROP TABLE IF EXISTS `jPolDocDoc_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolDocDoc_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDpol` bigint(20) NOT NULL DEFAULT '0',
  `documentUIDdoc` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpol`,`documentUIDdoc`),
  KEY `FKEA75C47C52AE8777` (`REV`),
  CONSTRAINT `FKEA75C47C52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolDplDpl`
--

DROP TABLE IF EXISTS `jPolDplDpl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolDplDpl` (
  `UIDpol` bigint(20) NOT NULL,
  `documentPlaceholderUIDdpl` bigint(20) NOT NULL,
  UNIQUE KEY `documentPlaceholderUIDdpl` (`documentPlaceholderUIDdpl`),
  KEY `FK51F4F34384156FE0` (`UIDpol`),
  KEY `FK51F4F3436F9D1585` (`documentPlaceholderUIDdpl`),
  CONSTRAINT `FK51F4F3436F9D1585` FOREIGN KEY (`documentPlaceholderUIDdpl`) REFERENCES `dplDocumentPlaceholder` (`dplUID`),
  CONSTRAINT `FK51F4F34384156FE0` FOREIGN KEY (`UIDpol`) REFERENCES `polPolicy` (`polUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolDplDpl_`
--

DROP TABLE IF EXISTS `jPolDplDpl_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolDplDpl_` (
  `REV` int(11) NOT NULL,
  `UIDpol` bigint(20) NOT NULL,
  `documentPlaceholderUIDdpl` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpol`,`documentPlaceholderUIDdpl`),
  KEY `FKECA9757C88B4F168` (`REV`),
  CONSTRAINT `FKECA9757C88B4F168` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolNotNot`
--

DROP TABLE IF EXISTS `jPolNotNot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolNotNot` (
  `UIDpol` bigint(20) NOT NULL,
  `noteUIDnot` bigint(20) NOT NULL,
  UNIQUE KEY `noteUIDnot` (`noteUIDnot`),
  KEY `FK62FB1A6384156FE0` (`UIDpol`),
  KEY `FK62FB1A63B3C35E1F` (`noteUIDnot`),
  CONSTRAINT `FK62FB1A6384156FE0` FOREIGN KEY (`UIDpol`) REFERENCES `polPolicy` (`polUID`),
  CONSTRAINT `FK62FB1A63B3C35E1F` FOREIGN KEY (`noteUIDnot`) REFERENCES `notNote` (`notUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolNotNot_`
--

DROP TABLE IF EXISTS `jPolNotNot_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolNotNot_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDpol` bigint(20) NOT NULL DEFAULT '0',
  `noteUIDnot` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpol`,`noteUIDnot`),
  KEY `FKFC68325C52AE8777` (`REV`),
  CONSTRAINT `FKFC68325C52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolPhiPre`
--

DROP TABLE IF EXISTS `jPolPhiPre`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolPhiPre` (
  `UIDpol` bigint(20) NOT NULL,
  `paymentHistoryUIDpre` bigint(20) NOT NULL,
  UNIQUE KEY `paymentHistoryUIDpre` (`paymentHistoryUIDpre`),
  KEY `FK65FD2ED515725D3C` (`paymentHistoryUIDpre`),
  KEY `FK65FD2ED584156FE0` (`UIDpol`),
  CONSTRAINT `FK65FD2ED515725D3C` FOREIGN KEY (`paymentHistoryUIDpre`) REFERENCES `prePaymentRecord` (`preUID`),
  CONSTRAINT `FK65FD2ED584156FE0` FOREIGN KEY (`UIDpol`) REFERENCES `polPolicy` (`polUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolPhiPre_`
--

DROP TABLE IF EXISTS `jPolPhiPre_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolPhiPre_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDpol` bigint(20) NOT NULL DEFAULT '0',
  `paymentHistoryUIDpre` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpol`,`paymentHistoryUIDpre`),
  KEY `FK59A8AC2A52AE8777` (`REV`),
  CONSTRAINT `FK59A8AC2A52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolPhoPho_`
--

DROP TABLE IF EXISTS `jPolPhoPho_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolPhoPho_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `phoPolicyUIDpol` bigint(20) NOT NULL,
  `phoUID` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`phoPolicyUIDpol`,`phoUID`),
  KEY `FK59FD14DC52AE8777` (`REV`),
  CONSTRAINT `FK59FD14DC52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolPopPsc`
--

DROP TABLE IF EXISTS `jPolPopPsc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolPopPsc` (
  `UIDpol` bigint(20) NOT NULL,
  `paymentOptionUIDpsc` bigint(20) NOT NULL,
  UNIQUE KEY `paymentOptionUIDpsc` (`paymentOptionUIDpsc`),
  KEY `FK66630212223BE18C` (`paymentOptionUIDpsc`),
  KEY `FK6663021284156FE0` (`UIDpol`),
  CONSTRAINT `FK66630212223BE18C` FOREIGN KEY (`paymentOptionUIDpsc`) REFERENCES `pscPaymentSchedule` (`pscUID`),
  CONSTRAINT `FK6663021284156FE0` FOREIGN KEY (`UIDpol`) REFERENCES `polPolicy` (`polUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolPopPsc_`
--

DROP TABLE IF EXISTS `jPolPopPsc_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolPopPsc_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDpol` bigint(20) NOT NULL DEFAULT '0',
  `paymentOptionUIDpsc` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpol`,`paymentOptionUIDpsc`),
  KEY `FK65FD408D52AE8777` (`REV`),
  CONSTRAINT `FK65FD408D52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolProPro`
--

DROP TABLE IF EXISTS `jPolProPro`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolProPro` (
  `UIDpol` bigint(20) NOT NULL,
  `partyRoleUIDpro` bigint(20) NOT NULL,
  UNIQUE KEY `partyRoleUIDpro` (`partyRoleUIDpro`),
  KEY `FK668CD42384156FE0` (`UIDpol`),
  KEY `FK668CD42392962E76` (`partyRoleUIDpro`),
  CONSTRAINT `FK668CD42384156FE0` FOREIGN KEY (`UIDpol`) REFERENCES `polPolicy` (`polUID`),
  CONSTRAINT `FK668CD42392962E76` FOREIGN KEY (`partyRoleUIDpro`) REFERENCES `proPartyRole` (`proUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolProPro_`
--

DROP TABLE IF EXISTS `jPolProPro_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolProPro_` (
  `REV` int(11) NOT NULL,
  `UIDpol` bigint(20) NOT NULL,
  `partyRoleUIDpro` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpol`,`partyRoleUIDpro`),
  KEY `FK6B0DB09C88B4F168` (`REV`),
  CONSTRAINT `FK6B0DB09C88B4F168` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolSecSec`
--

DROP TABLE IF EXISTS `jPolSecSec`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolSecSec` (
  `UIDpol` bigint(20) NOT NULL,
  `sectionUIDsec` bigint(20) NOT NULL,
  UNIQUE KEY `sectionUIDsec` (`sectionUIDsec`),
  KEY `FK6AEEC1A37C8F2B7A` (`sectionUIDsec`),
  KEY `FK6AEEC1A384156FE0` (`UIDpol`),
  CONSTRAINT `FK6AEEC1A37C8F2B7A` FOREIGN KEY (`sectionUIDsec`) REFERENCES `secSection` (`secUID`),
  CONSTRAINT `FK6AEEC1A384156FE0` FOREIGN KEY (`UIDpol`) REFERENCES `polPolicy` (`polUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPolSecSec_`
--

DROP TABLE IF EXISTS `jPolSecSec_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPolSecSec_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDpol` bigint(20) NOT NULL DEFAULT '0',
  `sectionUIDsec` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpol`,`sectionUIDsec`),
  KEY `FKF2E9731C52AE8777` (`REV`),
  CONSTRAINT `FKF2E9731C52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPscMprMpr`
--

DROP TABLE IF EXISTS `jPscMprMpr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPscMprMpr` (
  `UIDpsc` bigint(20) NOT NULL,
  `moneyProvisionUIDmpr` bigint(20) NOT NULL,
  UNIQUE KEY `moneyProvisionUIDmpr` (`moneyProvisionUIDmpr`),
  KEY `FK24C00B16ABCFDC05` (`moneyProvisionUIDmpr`),
  KEY `FK24C00B162BCAEBD1` (`UIDpsc`),
  CONSTRAINT `FK24C00B162BCAEBD1` FOREIGN KEY (`UIDpsc`) REFERENCES `pscPaymentSchedule` (`pscUID`),
  CONSTRAINT `FK24C00B16ABCFDC05` FOREIGN KEY (`moneyProvisionUIDmpr`) REFERENCES `mprMoneyProvision` (`mprUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jPscMprMpr_`
--

DROP TABLE IF EXISTS `jPscMprMpr_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jPscMprMpr_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDpsc` bigint(20) NOT NULL DEFAULT '0',
  `moneyProvisionUIDmpr` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpsc`,`moneyProvisionUIDmpr`),
  KEY `FK7341580952AE8777` (`REV`),
  CONSTRAINT `FK7341580952AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jSecAslAsh`
--

DROP TABLE IF EXISTS `jSecAslAsh`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jSecAslAsh` (
  `UIDsec` bigint(20) NOT NULL,
  `assessmentSheetListUIDash` bigint(20) NOT NULL,
  `sheet_name` varchar(255) NOT NULL,
  PRIMARY KEY (`UIDsec`,`sheet_name`),
  UNIQUE KEY `assessmentSheetListUIDash` (`assessmentSheetListUIDash`),
  KEY `FK1EEA1C0373432C55` (`UIDsec`),
  KEY `FK1EEA1C0351048E0D` (`assessmentSheetListUIDash`),
  CONSTRAINT `FK1EEA1C0351048E0D` FOREIGN KEY (`assessmentSheetListUIDash`) REFERENCES `ashAssessmentSheet` (`ashUID`),
  CONSTRAINT `FK1EEA1C0373432C55` FOREIGN KEY (`UIDsec`) REFERENCES `secSection` (`secUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jSecAslAsh_`
--

DROP TABLE IF EXISTS `jSecAslAsh_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jSecAslAsh_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDsec` bigint(20) NOT NULL DEFAULT '0',
  `assessmentSheetListUIDash` bigint(20) NOT NULL DEFAULT '0',
  `sheet_name` varchar(255) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDsec`,`assessmentSheetListUIDash`,`sheet_name`),
  KEY `FKBE5964BC52AE8777` (`REV`),
  CONSTRAINT `FKBE5964BC52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jSecClaCls`
--

DROP TABLE IF EXISTS `jSecClaCls`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jSecClaCls` (
  `UIDsec` bigint(20) NOT NULL,
  `clauseUIDcls` bigint(20) NOT NULL,
  UNIQUE KEY `clauseUIDcls` (`clauseUIDcls`),
  KEY `FK21EC2F5913FCA529` (`clauseUIDcls`),
  KEY `FK21EC2F5973432C55` (`UIDsec`),
  CONSTRAINT `FK21EC2F5913FCA529` FOREIGN KEY (`clauseUIDcls`) REFERENCES `clsClause` (`clsUID`),
  CONSTRAINT `FK21EC2F5973432C55` FOREIGN KEY (`UIDsec`) REFERENCES `secSection` (`secUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jSecClaCls_`
--

DROP TABLE IF EXISTS `jSecClaCls_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jSecClaCls_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDsec` bigint(20) NOT NULL DEFAULT '0',
  `clauseUIDcls` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDsec`,`clauseUIDcls`),
  KEY `FK1B99BC2652AE8777` (`REV`),
  CONSTRAINT `FK1B99BC2652AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jSecCovCov`
--

DROP TABLE IF EXISTS `jSecCovCov`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jSecCovCov` (
  `UIDsec` bigint(20) NOT NULL,
  `coverageUIDcov` bigint(20) NOT NULL,
  UNIQUE KEY `coverageUIDcov` (`coverageUIDcov`),
  KEY `FK222002073DED8F7B` (`coverageUIDcov`),
  KEY `FK2220020773432C55` (`UIDsec`),
  CONSTRAINT `FK222002073DED8F7B` FOREIGN KEY (`coverageUIDcov`) REFERENCES `covCoverage` (`covUID`),
  CONSTRAINT `FK2220020773432C55` FOREIGN KEY (`UIDsec`) REFERENCES `secSection` (`secUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jSecCovCov_`
--

DROP TABLE IF EXISTS `jSecCovCov_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jSecCovCov_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDsec` bigint(20) NOT NULL DEFAULT '0',
  `coverageUIDcov` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDsec`,`coverageUIDcov`),
  KEY `FK21E03F3852AE8777` (`REV`),
  CONSTRAINT `FK21E03F3852AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jdocConDco_`
--

DROP TABLE IF EXISTS `jdocConDco_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jdocConDco_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `dcoDocumentUIDdoc` bigint(20) NOT NULL,
  `dcoUID` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`dcoDocumentUIDdoc`,`dcoUID`),
  KEY `FK24FCA2C352AE8777` (`REV`),
  CONSTRAINT `FK24FCA2C352AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jliJournalLine`
--

DROP TABLE IF EXISTS `jliJournalLine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jliJournalLine` (
  `jliUID` bigint(20) NOT NULL,
  `jliAttribute` longtext,
  `jliCreatedBy` bigint(20) DEFAULT NULL,
  `jliCreatedDate` datetime DEFAULT NULL,
  `jliExternalSystemId` varchar(255) NOT NULL,
  `jliForeignSystemId` varchar(255) DEFAULT NULL,
  `jliLock` bit(1) DEFAULT NULL,
  `jliSerialVersion` bigint(20) NOT NULL,
  `jliUpdatedBy` bigint(20) DEFAULT NULL,
  `jliUpdatedDate` datetime DEFAULT NULL,
  `jliAmount` decimal(19,2) DEFAULT NULL,
  `jliCurrency` varchar(255) DEFAULT NULL,
  `jliDescription` longtext,
  `jliTransactionType` varchar(255) DEFAULT NULL,
  `jliType` varchar(255) DEFAULT NULL,
  `jliLedgerUIDled` bigint(20) DEFAULT NULL,
  `jliPaymentMethodUIDpme` bigint(20) DEFAULT NULL,
  `jliJournalUIDjou` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`jliUID`),
  UNIQUE KEY `jliExternalSystemId` (`jliExternalSystemId`),
  KEY `FKAF0315E4BF7A0EBD` (`jliLedgerUIDled`),
  KEY `FKAF0315E44759D643` (`jliPaymentMethodUIDpme`),
  KEY `FKAF0315E4335B81BE` (`jliJournalUIDjou`),
  CONSTRAINT `FKAF0315E4335B81BE` FOREIGN KEY (`jliJournalUIDjou`) REFERENCES `jouJournal` (`jouUID`),
  CONSTRAINT `FKAF0315E44759D643` FOREIGN KEY (`jliPaymentMethodUIDpme`) REFERENCES `pmePaymentMethod` (`pmeUID`),
  CONSTRAINT `FKAF0315E4BF7A0EBD` FOREIGN KEY (`jliLedgerUIDled`) REFERENCES `ledLedger` (`ledUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jliJournalLine_`
--

DROP TABLE IF EXISTS `jliJournalLine_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jliJournalLine_` (
  `jliUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `jliAttribute` longtext,
  `jliCreatedBy` bigint(20) DEFAULT NULL,
  `jliCreatedDate` datetime DEFAULT NULL,
  `jliExternalSystemId` varchar(255) DEFAULT NULL,
  `jliForeignSystemId` varchar(255) DEFAULT NULL,
  `jliUpdatedBy` bigint(20) DEFAULT NULL,
  `jliUpdatedDate` datetime DEFAULT NULL,
  `jliAmount` decimal(19,2) DEFAULT NULL,
  `jliCurrency` varchar(255) DEFAULT NULL,
  `jliDescription` longtext,
  `jliTransactionType` varchar(255) DEFAULT NULL,
  `jliType` varchar(255) DEFAULT NULL,
  `jliJournalUIDjou` bigint(20) DEFAULT NULL,
  `jliLedgerUIDled` bigint(20) DEFAULT NULL,
  `jliPaymentMethodUIDpme` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`jliUID`,`REV`),
  KEY `FK315FA6FB52AE8777` (`REV`),
  CONSTRAINT `FK315FA6FB52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jouJournal`
--

DROP TABLE IF EXISTS `jouJournal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jouJournal` (
  `jouUID` bigint(20) NOT NULL,
  `jouAttribute` longtext,
  `jouCreatedBy` bigint(20) DEFAULT NULL,
  `jouCreatedDate` datetime DEFAULT NULL,
  `jouExternalSystemId` varchar(255) NOT NULL,
  `jouForeignSystemId` varchar(255) DEFAULT NULL,
  `jouLock` bit(1) DEFAULT NULL,
  `jouSerialVersion` bigint(20) NOT NULL,
  `jouSubjectId` varchar(255) DEFAULT NULL,
  `jouSubjectType` varchar(255) DEFAULT NULL,
  `jouUpdatedBy` bigint(20) DEFAULT NULL,
  `jouUpdatedDate` datetime DEFAULT NULL,
  `jouPostedDate` datetime DEFAULT NULL,
  `jouTransactionDate` datetime DEFAULT NULL,
  `jouType` varchar(255) DEFAULT NULL,
  `jouContraOfUIDjou` bigint(20) DEFAULT NULL,
  `jouOriginUIDmpr` bigint(20) DEFAULT NULL,
  `jouAccountingPeriodUIDape` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`jouUID`),
  UNIQUE KEY `jouExternalSystemId` (`jouExternalSystemId`),
  KEY `FKF89BC147D478FC16` (`jouContraOfUIDjou`),
  KEY `FKF89BC1478B4FEA56` (`jouOriginUIDmpr`),
  KEY `FKF89BC14753F6FB2B` (`jouAccountingPeriodUIDape`),
  CONSTRAINT `FKF89BC14753F6FB2B` FOREIGN KEY (`jouAccountingPeriodUIDape`) REFERENCES `apeAccountingPeriod` (`apeUID`),
  CONSTRAINT `FKF89BC1478B4FEA56` FOREIGN KEY (`jouOriginUIDmpr`) REFERENCES `mprMoneyProvision` (`mprUID`),
  CONSTRAINT `FKF89BC147D478FC16` FOREIGN KEY (`jouContraOfUIDjou`) REFERENCES `jouJournal` (`jouUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jouJournal_`
--

DROP TABLE IF EXISTS `jouJournal_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jouJournal_` (
  `jouUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `jouAttribute` longtext,
  `jouCreatedBy` bigint(20) DEFAULT NULL,
  `jouCreatedDate` datetime DEFAULT NULL,
  `jouExternalSystemId` varchar(255) DEFAULT NULL,
  `jouForeignSystemId` varchar(255) DEFAULT NULL,
  `jouUpdatedBy` bigint(20) DEFAULT NULL,
  `jouUpdatedDate` datetime DEFAULT NULL,
  `jouPostedDate` datetime DEFAULT NULL,
  `jouSubjectId` varchar(255) DEFAULT NULL,
  `jouSubjectType` varchar(255) DEFAULT NULL,
  `jouTransactionDate` datetime DEFAULT NULL,
  `jouType` varchar(255) DEFAULT NULL,
  `jouAccountingPeriodUIDape` bigint(20) DEFAULT NULL,
  `jouContraOfUIDjou` bigint(20) DEFAULT NULL,
  `jouOriginUIDmpr` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`jouUID`,`REV`),
  KEY `FK1ADC67F852AE8777` (`REV`),
  CONSTRAINT `FK1ADC67F852AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jparnotnot`
--

DROP TABLE IF EXISTS `jparnotnot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jparnotnot` (
  `UIDpar` bigint(20) NOT NULL,
  `noteUIDnot` bigint(20) NOT NULL,
  UNIQUE KEY `noteUIDnot` (`noteUIDnot`),
  KEY `FKF21009B7B3C35E1F` (`noteUIDnot`),
  KEY `FKF21009B743E35FF8` (`UIDpar`),
  CONSTRAINT `FKF21009B743E35FF8` FOREIGN KEY (`UIDpar`) REFERENCES `parparty` (`parUID`),
  CONSTRAINT `FKF21009B7B3C35E1F` FOREIGN KEY (`noteUIDnot`) REFERENCES `notnote` (`notUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jparnotnot_`
--

DROP TABLE IF EXISTS `jparnotnot_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jparnotnot_` (
  `REV` int(11) NOT NULL DEFAULT '0',
  `UIDpar` bigint(20) NOT NULL DEFAULT '0',
  `noteUIDnot` bigint(20) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`UIDpar`,`noteUIDnot`),
  KEY `FK4FF12D8852AE8777` (`REV`),
  CONSTRAINT `FK4FF12D8852AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ledLedger`
--

DROP TABLE IF EXISTS `ledLedger`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ledLedger` (
  `ledUID` bigint(20) NOT NULL,
  `ledAttribute` longtext,
  `ledCreatedBy` bigint(20) DEFAULT NULL,
  `ledCreatedDate` datetime DEFAULT NULL,
  `ledExternalSystemId` varchar(255) NOT NULL,
  `ledForeignSystemId` varchar(255) DEFAULT NULL,
  `ledLock` bit(1) DEFAULT NULL,
  `ledSerialVersion` bigint(20) NOT NULL,
  `ledUpdatedBy` bigint(20) DEFAULT NULL,
  `ledUpdatedDate` datetime DEFAULT NULL,
  `ledDescription` longtext,
  `ledName` varchar(255) DEFAULT NULL,
  `ledAccountUIDacc` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ledUID`),
  UNIQUE KEY `ledExternalSystemId` (`ledExternalSystemId`),
  KEY `FKBA134754E3967B7` (`ledAccountUIDacc`),
  CONSTRAINT `FKBA134754E3967B7` FOREIGN KEY (`ledAccountUIDacc`) REFERENCES `accAccount` (`accUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ledLedger_`
--

DROP TABLE IF EXISTS `ledLedger_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ledLedger_` (
  `ledUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `ledAttribute` longtext,
  `ledCreatedBy` bigint(20) DEFAULT NULL,
  `ledCreatedDate` datetime DEFAULT NULL,
  `ledExternalSystemId` varchar(255) DEFAULT NULL,
  `ledForeignSystemId` varchar(255) DEFAULT NULL,
  `ledUpdatedBy` bigint(20) DEFAULT NULL,
  `ledUpdatedDate` datetime DEFAULT NULL,
  `ledDescription` longtext,
  `ledName` varchar(255) DEFAULT NULL,
  `ledAccountUIDacc` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ledUID`,`REV`),
  KEY `FK8855A38B52AE8777` (`REV`),
  CONSTRAINT `FK8855A38B52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `liqChangeLog`
--

DROP TABLE IF EXISTS `liqChangeLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `liqChangeLog` (
  `ID` varchar(255) NOT NULL,
  `AUTHOR` varchar(255) NOT NULL,
  `FILENAME` varchar(255) NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `ORDEREXECUTED` int(11) NOT NULL,
  `EXECTYPE` varchar(10) NOT NULL,
  `MD5SUM` varchar(35) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `COMMENTS` varchar(255) DEFAULT NULL,
  `TAG` varchar(255) DEFAULT NULL,
  `LIQUIBASE` varchar(20) DEFAULT NULL,
  `CONTEXTS` varchar(255) DEFAULT NULL,
  `LABELS` varchar(255) DEFAULT NULL,
  `DEPLOYMENT_ID` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `liqChangeLogLock`
--

DROP TABLE IF EXISTS `liqChangeLogLock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `liqChangeLogLock` (
  `ID` int(11) NOT NULL,
  `LOCKED` bit(1) NOT NULL,
  `LOCKGRANTED` datetime DEFAULT NULL,
  `LOCKEDBY` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mprMoneyProvision`
--

DROP TABLE IF EXISTS `mprMoneyProvision`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mprMoneyProvision` (
  `mprUID` bigint(20) NOT NULL,
  `mprAttribute` longtext,
  `mprCreatedDate` datetime DEFAULT NULL,
  `mprExternalSystemId` varchar(255) NOT NULL,
  `mprForeignSystemId` varchar(255) DEFAULT NULL,
  `mprLock` bit(1) DEFAULT NULL,
  `mprSerialVersion` bigint(20) NOT NULL,
  `mprUpdatedDate` datetime DEFAULT NULL,
  `mprAmount` decimal(19,2) DEFAULT NULL,
  `mprCurrency` varchar(255) DEFAULT NULL,
  `mprDescription` varchar(255) DEFAULT NULL,
  `mprFrequency` varchar(255) DEFAULT NULL,
  `mprNumber` int(11) NOT NULL,
  `mprPaymentId` varchar(255) DEFAULT NULL,
  `mprPaymentsEndDate` datetime DEFAULT NULL,
  `mprPaymentsStartDate` datetime DEFAULT NULL,
  `mprSaleId` varchar(255) DEFAULT NULL,
  `mprStatus` varchar(255) DEFAULT NULL,
  `mprPaymentMethodUIDpme` bigint(20) DEFAULT NULL,
  `mprCreatedBy` bigint(20) DEFAULT NULL,
  `mprUpdatedBy` bigint(20) DEFAULT NULL,
  `mprDay` int(11) DEFAULT NULL,
  `mprPurpose` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`mprUID`),
  UNIQUE KEY `mprExternalSystemId` (`mprExternalSystemId`),
  KEY `FK407B25044212110A` (`mprPaymentMethodUIDpme`),
  CONSTRAINT `FK407B25044212110A` FOREIGN KEY (`mprPaymentMethodUIDpme`) REFERENCES `pmePaymentMethod` (`pmeUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mprMoneyProvision_`
--

DROP TABLE IF EXISTS `mprMoneyProvision_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mprMoneyProvision_` (
  `mprUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `mprAttribute` longtext,
  `mprCreatedBy` bigint(20) DEFAULT NULL,
  `mprCreatedDate` datetime DEFAULT NULL,
  `mprExternalSystemId` varchar(255) DEFAULT NULL,
  `mprForeignSystemId` varchar(255) DEFAULT NULL,
  `mprUpdatedBy` bigint(20) DEFAULT NULL,
  `mprUpdatedDate` datetime DEFAULT NULL,
  `mprAmount` decimal(19,2) DEFAULT NULL,
  `mprCurrency` varchar(255) DEFAULT NULL,
  `mprDay` int(11) DEFAULT NULL,
  `mprDescription` varchar(255) DEFAULT NULL,
  `mprFrequency` varchar(255) DEFAULT NULL,
  `mprNumber` int(11) DEFAULT NULL,
  `mprPaymentId` varchar(255) DEFAULT NULL,
  `mprPaymentsEndDate` datetime DEFAULT NULL,
  `mprPaymentsStartDate` datetime DEFAULT NULL,
  `mprSaleId` varchar(255) DEFAULT NULL,
  `mprStatus` varchar(255) DEFAULT NULL,
  `mprPaymentMethodUIDpme` bigint(20) DEFAULT NULL,
  `mprPurpose` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`mprUID`,`REV`),
  KEY `FKCEE97BDB52AE8777` (`REV`),
  CONSTRAINT `FKCEE97BDB52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notNote`
--

DROP TABLE IF EXISTS `notNote`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notNote` (
  `notUID` bigint(20) NOT NULL,
  `notAttribute` longtext,
  `notCreatedDate` datetime DEFAULT NULL,
  `notExternalSystemId` varchar(255) NOT NULL,
  `notForeignSystemId` varchar(255) DEFAULT NULL,
  `notLock` bit(1) DEFAULT NULL,
  `notSerialVersion` bigint(20) NOT NULL,
  `notUpdatedDate` datetime DEFAULT NULL,
  `notBody` longtext,
  `notTitle` varchar(255) DEFAULT NULL,
  `notCreatedBy` bigint(20) DEFAULT NULL,
  `notUpdatedBy` bigint(20) DEFAULT NULL,
  `notType` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`notUID`),
  UNIQUE KEY `notExternalSystemId` (`notExternalSystemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notNote_`
--

DROP TABLE IF EXISTS `notNote_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notNote_` (
  `notUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `notAttribute` longtext,
  `notCreatedBy` bigint(20) DEFAULT NULL,
  `notCreatedDate` datetime DEFAULT NULL,
  `notExternalSystemId` varchar(255) DEFAULT NULL,
  `notForeignSystemId` varchar(255) DEFAULT NULL,
  `notUpdatedBy` bigint(20) DEFAULT NULL,
  `notUpdatedDate` datetime DEFAULT NULL,
  `notBody` longtext,
  `notTitle` varchar(255) DEFAULT NULL,
  `notType` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`notUID`,`REV`),
  KEY `FK5D067F9A52AE8777` (`REV`),
  CONSTRAINT `FK5D067F9A52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `parParty`
--

DROP TABLE IF EXISTS `parParty`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `parParty` (
  `parDSC` varchar(31) NOT NULL,
  `parUID` bigint(20) NOT NULL,
  `parAttribute` longtext,
  `parCreatedDate` datetime DEFAULT NULL,
  `parExternalSystemId` varchar(255) NOT NULL,
  `parForeignSystemId` varchar(255) DEFAULT NULL,
  `parLock` tinyint(1) NOT NULL,
  `parSerialVersion` bigint(20) NOT NULL,
  `parUpdatedDate` datetime DEFAULT NULL,
  `parLegalName` varchar(255) DEFAULT NULL,
  `parPartyId` varchar(255) DEFAULT NULL,
  `parOrganisationRegistrationNumber` varchar(255) DEFAULT NULL,
  `parTaxRegistrationNumber` varchar(255) DEFAULT NULL,
  `parContactName` varchar(255) DEFAULT NULL,
  `parDirectDebitIdentificationNumber` varchar(255) DEFAULT NULL,
  `parTradingName` varchar(255) DEFAULT NULL,
  `parDateOfBirth` datetime DEFAULT NULL,
  `parFirstName` varchar(255) DEFAULT NULL,
  `parOtherTitle` varchar(255) DEFAULT NULL,
  `parSurname` varchar(255) DEFAULT NULL,
  `parTitle` varchar(255) DEFAULT NULL,
  `parContactUIDpar` bigint(20) DEFAULT NULL,
  `parCreatedBy` bigint(20) DEFAULT NULL,
  `parUpdatedBy` bigint(20) DEFAULT NULL,
  `parContactPreference` varchar(255) DEFAULT NULL,
  `parId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`parUID`),
  UNIQUE KEY `parExternalSystemId` (`parExternalSystemId`),
  KEY `FK44DDB6C518EEC24` (`parContactUIDpar`),
  KEY `legalName` (`parLegalName`),
  KEY `partyId` (`parPartyId`),
  CONSTRAINT `FK44DDB6C518EEC24` FOREIGN KEY (`parContactUIDpar`) REFERENCES `parParty` (`parUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `parParty_`
--

DROP TABLE IF EXISTS `parParty_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `parParty_` (
  `parDSC` varchar(31) NOT NULL,
  `parUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `parAttribute` longtext,
  `parCreatedBy` bigint(20) DEFAULT NULL,
  `parCreatedDate` datetime DEFAULT NULL,
  `parExternalSystemId` varchar(255) DEFAULT NULL,
  `parForeignSystemId` varchar(255) DEFAULT NULL,
  `parUpdatedBy` bigint(20) DEFAULT NULL,
  `parUpdatedDate` datetime DEFAULT NULL,
  `parCountry` varchar(255) DEFAULT NULL,
  `parCounty` varchar(255) DEFAULT NULL,
  `parFullAddress` varchar(255) DEFAULT NULL,
  `parLine1` varchar(255) DEFAULT NULL,
  `parLine2` varchar(255) DEFAULT NULL,
  `parLine3` varchar(255) DEFAULT NULL,
  `parLine4` varchar(255) DEFAULT NULL,
  `parLine5` varchar(255) DEFAULT NULL,
  `parPostcode` varchar(255) DEFAULT NULL,
  `parTown` varchar(255) DEFAULT NULL,
  `parEmailAddress` varchar(255) DEFAULT NULL,
  `parLegalName` varchar(255) DEFAULT NULL,
  `parMobilephoneNumber` varchar(255) DEFAULT NULL,
  `parPartyId` varchar(255) DEFAULT NULL,
  `parTelephoneNumber` varchar(255) DEFAULT NULL,
  `parOrganisationRegistrationNumber` varchar(255) DEFAULT NULL,
  `parTaxRegistrationNumber` varchar(255) DEFAULT NULL,
  `parContactUIDpar` bigint(20) DEFAULT NULL,
  `parClaimTelephoneNumber` varchar(255) DEFAULT NULL,
  `parContactName` varchar(255) DEFAULT NULL,
  `parDirectDebitIdentificationNumber` varchar(255) DEFAULT NULL,
  `parPaymentTelephoneNumber` varchar(255) DEFAULT NULL,
  `parPolicyEmailAddress` varchar(255) DEFAULT NULL,
  `parQuoteEmailAddress` varchar(255) DEFAULT NULL,
  `parQuoteTelephoneNumber` varchar(255) DEFAULT NULL,
  `parTradingName` varchar(255) DEFAULT NULL,
  `parDateOfBirth` datetime DEFAULT NULL,
  `parFirstName` varchar(255) DEFAULT NULL,
  `parOtherTitle` varchar(255) DEFAULT NULL,
  `parSurname` varchar(255) DEFAULT NULL,
  `parTitle` varchar(255) DEFAULT NULL,
  `parContactPreference` varchar(255) DEFAULT NULL,
  `parId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`parUID`,`REV`),
  KEY `FK56D9223A52AE8777` (`REV`),
  CONSTRAINT `FK56D9223A52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pceProductChangeEvent`
--

DROP TABLE IF EXISTS `pceProductChangeEvent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pceProductChangeEvent` (
  `pceUID` bigint(20) NOT NULL,
  `pceAttribute` longtext,
  `pceCreatedDate` datetime DEFAULT NULL,
  `pceExternalSystemId` varchar(255) NOT NULL,
  `pceForeignSystemId` varchar(255) DEFAULT NULL,
  `pceLock` tinyint(1) NOT NULL,
  `pceSerialVersion` bigint(20) NOT NULL,
  `pceUpdatedDate` datetime DEFAULT NULL,
  `pcePath` varchar(255) DEFAULT NULL,
  `pceType` varchar(255) DEFAULT NULL,
  `pceCreatedBy` bigint(20) DEFAULT NULL,
  `pceUpdatedBy` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`pceUID`),
  UNIQUE KEY `pceExternalSystemId` (`pceExternalSystemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `phoPaymentHoliday`
--

DROP TABLE IF EXISTS `phoPaymentHoliday`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `phoPaymentHoliday` (
  `phoUID` bigint(20) NOT NULL,
  `phoAttribute` longtext,
  `phoCreatedBy` bigint(20) DEFAULT NULL,
  `phoCreatedDate` datetime DEFAULT NULL,
  `phoExternalSystemId` varchar(255) NOT NULL,
  `phoForeignSystemId` varchar(255) DEFAULT NULL,
  `phoLock` bit(1) DEFAULT NULL,
  `phoSerialVersion` bigint(20) NOT NULL,
  `phoUpdatedBy` bigint(20) DEFAULT NULL,
  `phoUpdatedDate` datetime DEFAULT NULL,
  `phoEndDate` datetime DEFAULT NULL,
  `phoStartDate` datetime DEFAULT NULL,
  `phoPolicyUIDpol` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`phoUID`),
  UNIQUE KEY `phoExternalSystemId` (`phoExternalSystemId`),
  KEY `FKADB97CC9F9D31E89` (`phoPolicyUIDpol`),
  CONSTRAINT `FKADB97CC9F9D31E89` FOREIGN KEY (`phoPolicyUIDpol`) REFERENCES `polPolicy` (`polUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `phoPaymentHoliday_`
--

DROP TABLE IF EXISTS `phoPaymentHoliday_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `phoPaymentHoliday_` (
  `phoUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `phoAttribute` longtext,
  `phoCreatedBy` bigint(20) DEFAULT NULL,
  `phoCreatedDate` datetime DEFAULT NULL,
  `phoExternalSystemId` varchar(255) DEFAULT NULL,
  `phoForeignSystemId` varchar(255) DEFAULT NULL,
  `phoUpdatedBy` bigint(20) DEFAULT NULL,
  `phoUpdatedDate` datetime DEFAULT NULL,
  `phoEndDate` datetime DEFAULT NULL,
  `phoStartDate` datetime DEFAULT NULL,
  PRIMARY KEY (`phoUID`,`REV`),
  KEY `FK9761CB652AE8777` (`REV`),
  CONSTRAINT `FK9761CB652AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pmePaymentMethod`
--

DROP TABLE IF EXISTS `pmePaymentMethod`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pmePaymentMethod` (
  `pmeDSC` varchar(31) NOT NULL,
  `pmeUID` bigint(20) NOT NULL,
  `pmeAttribute` longtext,
  `pmeCreatedDate` datetime DEFAULT NULL,
  `pmeExternalSystemId` varchar(255) NOT NULL,
  `pmeForeignSystemId` varchar(255) DEFAULT NULL,
  `pmeLock` bit(1) DEFAULT NULL,
  `pmeSerialVersion` bigint(20) NOT NULL,
  `pmeUpdatedDate` datetime DEFAULT NULL,
  `pmeAccountNumber` varchar(255) DEFAULT NULL,
  `pmeSortCode` varchar(255) DEFAULT NULL,
  `pmeCardHoldersName` varchar(255) DEFAULT NULL,
  `pmeCardNumber` varchar(255) DEFAULT NULL,
  `pmeExpiryDate` datetime DEFAULT NULL,
  `pmeIssueNumber` varchar(255) DEFAULT NULL,
  `pmeIssuer` varchar(255) DEFAULT NULL,
  `pmeSecurityCode` varchar(255) DEFAULT NULL,
  `pmeStartDate` datetime DEFAULT NULL,
  `pmeCreatedBy` bigint(20) DEFAULT NULL,
  `pmeUpdatedBy` bigint(20) DEFAULT NULL,
  `pmeStatus` varchar(255) DEFAULT NULL,
  `pmeName` varchar(255) DEFAULT NULL,
  `pmeBankName` varchar(255) DEFAULT NULL,
  `pmeBranchName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`pmeUID`),
  UNIQUE KEY `pmeExternalSystemId` (`pmeExternalSystemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pmePaymentMethod_`
--

DROP TABLE IF EXISTS `pmePaymentMethod_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pmePaymentMethod_` (
  `pmeDSC` varchar(31) NOT NULL,
  `pmeUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `pmeAttribute` longtext,
  `pmeCreatedBy` bigint(20) DEFAULT NULL,
  `pmeCreatedDate` datetime DEFAULT NULL,
  `pmeExternalSystemId` varchar(255) DEFAULT NULL,
  `pmeForeignSystemId` varchar(255) DEFAULT NULL,
  `pmeUpdatedBy` bigint(20) DEFAULT NULL,
  `pmeUpdatedDate` datetime DEFAULT NULL,
  `pmeCardHoldersName` varchar(255) DEFAULT NULL,
  `pmeCardNumber` varchar(255) DEFAULT NULL,
  `pmeExpiryDate` datetime DEFAULT NULL,
  `pmeIssueNumber` varchar(255) DEFAULT NULL,
  `pmeIssuer` varchar(255) DEFAULT NULL,
  `pmeSecurityCode` varchar(255) DEFAULT NULL,
  `pmeStartDate` datetime DEFAULT NULL,
  `pmeAccountNumber` varchar(255) DEFAULT NULL,
  `pmeSortCode` varchar(255) DEFAULT NULL,
  `pmeStatus` varchar(255) DEFAULT NULL,
  `pmeName` varchar(255) DEFAULT NULL,
  `pmeBankName` varchar(255) DEFAULT NULL,
  `pmeBranchName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`pmeUID`,`REV`),
  KEY `FK8ADFCEE052AE8777` (`REV`),
  CONSTRAINT `FK8ADFCEE052AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `polPolicy`
--

DROP TABLE IF EXISTS `polPolicy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `polPolicy` (
  `polUID` bigint(20) NOT NULL,
  `polAttribute` longtext,
  `polCreatedDate` datetime DEFAULT NULL,
  `polExternalSystemId` varchar(255) NOT NULL,
  `polForeignSystemId` varchar(255) DEFAULT NULL,
  `polLock` tinyint(1) NOT NULL,
  `polSerialVersion` bigint(20) NOT NULL,
  `polUpdatedDate` datetime DEFAULT NULL,
  `polAggregator` tinyint(1) NOT NULL,
  `polAllowable` longtext,
  `polBaseCurrency` varchar(255) DEFAULT NULL,
  `polException` longtext,
  `polExcess` longtext,
  `polExpiryDate` datetime DEFAULT NULL,
  `polId` varchar(255) DEFAULT NULL,
  `polInceptionDate` datetime DEFAULT NULL,
  `polOwningUser` bigint(20) DEFAULT NULL,
  `polPageVisit` longtext,
  `polPaymentDetails` longtext,
  `polPaymentHistory` longtext,
  `polPaymentOption` longtext,
  `polPolicyLink` longtext,
  `polPolicyNumber` varchar(255) DEFAULT NULL,
  `polProductName` varchar(255) DEFAULT NULL,
  `polProductTypeId` varchar(255) DEFAULT NULL,
  `polQuotationDate` datetime DEFAULT NULL,
  `polQuotationExpiryDate` datetime DEFAULT NULL,
  `polQuotationNumber` varchar(255) DEFAULT NULL,
  `polStatus` varchar(255) DEFAULT NULL,
  `polTestCase` tinyint(1) NOT NULL,
  `polUserSaved` tinyint(1) NOT NULL,
  `polVersionEffectiveDate` datetime DEFAULT NULL,
  `polWording` longtext,
  `polPaymentDetailsUIDpsc` bigint(20) DEFAULT NULL,
  `polCreatedBy` bigint(20) DEFAULT NULL,
  `polUpdatedBy` bigint(20) DEFAULT NULL,
  `polMtaIndex` bigint(20) DEFAULT NULL,
  `polRenewalIndex` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`polUID`),
  UNIQUE KEY `polExternalSystemId` (`polExternalSystemId`),
  KEY `status` (`polStatus`),
  KEY `owningUser` (`polOwningUser`),
  KEY `FKC88695DF9F98CA5F` (`polPaymentDetailsUIDpsc`),
  CONSTRAINT `FKC88695DF9F98CA5F` FOREIGN KEY (`polPaymentDetailsUIDpsc`) REFERENCES `pscPaymentSchedule` (`pscUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `polPolicy_`
--

DROP TABLE IF EXISTS `polPolicy_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `polPolicy_` (
  `polUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `polAttribute` longtext,
  `polCreatedBy` bigint(20) DEFAULT NULL,
  `polCreatedDate` datetime DEFAULT NULL,
  `polExternalSystemId` varchar(255) DEFAULT NULL,
  `polForeignSystemId` varchar(255) DEFAULT NULL,
  `polUpdatedBy` bigint(20) DEFAULT NULL,
  `polUpdatedDate` datetime DEFAULT NULL,
  `polAggregator` bit(1) DEFAULT NULL,
  `polAllowable` longtext,
  `polBaseCurrency` varchar(255) DEFAULT NULL,
  `polClause` longtext,
  `polException` longtext,
  `polExcess` longtext,
  `polExpiryDate` datetime DEFAULT NULL,
  `polId` varchar(255) DEFAULT NULL,
  `polInceptionDate` datetime DEFAULT NULL,
  `polMtaIndex` bigint(20) DEFAULT NULL,
  `polOwningUser` bigint(20) DEFAULT NULL,
  `polPageVisit` longtext,
  `polPolicyLink` longtext,
  `polPolicyNumber` varchar(255) DEFAULT NULL,
  `polProductName` varchar(255) DEFAULT NULL,
  `polProductTypeId` varchar(255) DEFAULT NULL,
  `polQuotationDate` datetime DEFAULT NULL,
  `polQuotationExpiryDate` datetime DEFAULT NULL,
  `polQuotationNumber` varchar(255) DEFAULT NULL,
  `polRenewalIndex` bigint(20) DEFAULT NULL,
  `polStatus` varchar(255) DEFAULT NULL,
  `polTestCase` bit(1) DEFAULT NULL,
  `polUserSaved` bit(1) DEFAULT NULL,
  `polVersionEffectiveDate` datetime DEFAULT NULL,
  `polWording` longtext,
  `polBrokerUIDpar` bigint(20) DEFAULT NULL,
  `polPaymentDetailsUIDpsc` bigint(20) DEFAULT NULL,
  `polPolicyHolderUIDpar` bigint(20) DEFAULT NULL,
  `polProposerUIDpar` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`polUID`,`REV`),
  KEY `FK484C266052AE8777` (`REV`),
  CONSTRAINT `FK484C266052AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `prePaymentRecord`
--

DROP TABLE IF EXISTS `prePaymentRecord`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `prePaymentRecord` (
  `preUID` bigint(20) NOT NULL,
  `preAttribute` longtext,
  `preCreatedDate` datetime DEFAULT NULL,
  `preExternalSystemId` varchar(255) NOT NULL,
  `preForeignSystemId` varchar(255) DEFAULT NULL,
  `preLock` bit(1) DEFAULT NULL,
  `preSerialVersion` bigint(20) NOT NULL,
  `preUpdatedDate` datetime DEFAULT NULL,
  `preAmount` decimal(19,2) DEFAULT NULL,
  `preCurrency` varchar(255) DEFAULT NULL,
  `preDate` datetime DEFAULT NULL,
  `preDescription` varchar(255) DEFAULT NULL,
  `preMethodIdentifier` varchar(255) DEFAULT NULL,
  `preTransactionReference` varchar(255) DEFAULT NULL,
  `preType` varchar(255) DEFAULT NULL,
  `preCreatedBy` bigint(20) DEFAULT NULL,
  `preUpdatedBy` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`preUID`),
  UNIQUE KEY `preExternalSystemId` (`preExternalSystemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `prePaymentRecord_`
--

DROP TABLE IF EXISTS `prePaymentRecord_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `prePaymentRecord_` (
  `preUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `preAttribute` longtext,
  `preCreatedBy` bigint(20) DEFAULT NULL,
  `preCreatedDate` datetime DEFAULT NULL,
  `preExternalSystemId` varchar(255) DEFAULT NULL,
  `preForeignSystemId` varchar(255) DEFAULT NULL,
  `preUpdatedBy` bigint(20) DEFAULT NULL,
  `preUpdatedDate` datetime DEFAULT NULL,
  `preAmount` decimal(19,2) DEFAULT NULL,
  `preCurrency` varchar(255) DEFAULT NULL,
  `preDate` datetime DEFAULT NULL,
  `preDescription` varchar(255) DEFAULT NULL,
  `preMethodIdentifier` varchar(255) DEFAULT NULL,
  `preTransactionReference` varchar(255) DEFAULT NULL,
  `preType` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`preUID`,`REV`),
  KEY `FKFA4BF6CB52AE8777` (`REV`),
  CONSTRAINT `FKFA4BF6CB52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `prlPartyRelationship`
--

DROP TABLE IF EXISTS `prlPartyRelationship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `prlPartyRelationship` (
  `prlUID` bigint(20) NOT NULL,
  `prlAttribute` longtext,
  `prlCreatedBy` bigint(20) DEFAULT NULL,
  `prlCreatedDate` datetime DEFAULT NULL,
  `prlExternalSystemId` varchar(255) NOT NULL,
  `prlForeignSystemId` varchar(255) DEFAULT NULL,
  `prlLock` bit(1) DEFAULT NULL,
  `prlSerialVersion` bigint(20) NOT NULL,
  `prlUpdatedBy` bigint(20) DEFAULT NULL,
  `prlUpdatedDate` datetime DEFAULT NULL,
  `prlEndDate` timestamp(4) NULL DEFAULT NULL,
  `prlRelationship` varchar(255) NOT NULL,
  `prlStartDate` timestamp(4) NULL DEFAULT NULL,
  `prlPartyUIDpar` bigint(20) NOT NULL,
  PRIMARY KEY (`prlUID`),
  UNIQUE KEY `prlExternalSystemId` (`prlExternalSystemId`),
  KEY `FK99E5B2B4576C1574` (`prlPartyUIDpar`),
  CONSTRAINT `FK99E5B2B4576C1574` FOREIGN KEY (`prlPartyUIDpar`) REFERENCES `parParty` (`parUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `prlPartyRelationship_`
--

DROP TABLE IF EXISTS `prlPartyRelationship_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `prlPartyRelationship_` (
  `prlUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `prlAttribute` longtext,
  `prlCreatedBy` bigint(20) DEFAULT NULL,
  `prlCreatedDate` datetime DEFAULT NULL,
  `prlExternalSystemId` varchar(255) DEFAULT NULL,
  `prlForeignSystemId` varchar(255) DEFAULT NULL,
  `prlUpdatedBy` bigint(20) DEFAULT NULL,
  `prlUpdatedDate` datetime DEFAULT NULL,
  `prlEndDate` timestamp(4) NULL DEFAULT NULL,
  `prlRelationship` varchar(255) DEFAULT NULL,
  `prlStartDate` timestamp(4) NULL DEFAULT NULL,
  `prlPartyUIDpar` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`prlUID`,`REV`),
  KEY `FKA2D0A42B88B4F168` (`REV`),
  CONSTRAINT `FKA2D0A42B88B4F168` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `proPartyRole`
--

DROP TABLE IF EXISTS `proPartyRole`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `proPartyRole` (
  `proUID` bigint(20) NOT NULL,
  `proAttribute` longtext,
  `proCreatedBy` bigint(20) DEFAULT NULL,
  `proCreatedDate` datetime DEFAULT NULL,
  `proExternalSystemId` varchar(255) NOT NULL,
  `proForeignSystemId` varchar(255) DEFAULT NULL,
  `proLock` bit(1) DEFAULT NULL,
  `proSerialVersion` bigint(20) NOT NULL,
  `proUpdatedBy` bigint(20) DEFAULT NULL,
  `proUpdatedDate` datetime DEFAULT NULL,
  `proEndDate` timestamp(4) NULL DEFAULT NULL,
  `proStartDate` timestamp(4) NULL DEFAULT NULL,
  `proRole` varchar(255) NOT NULL,
  `proPartyUIDpar` bigint(20) NOT NULL,
  PRIMARY KEY (`proUID`),
  UNIQUE KEY `proExternalSystemId` (`proExternalSystemId`),
  KEY `FKA4654C4F6E810591` (`proPartyUIDpar`),
  CONSTRAINT `FKA4654C4F6E810591` FOREIGN KEY (`proPartyUIDpar`) REFERENCES `parParty` (`parUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `proPartyRole_`
--

DROP TABLE IF EXISTS `proPartyRole_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `proPartyRole_` (
  `proUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `proAttribute` longtext,
  `proCreatedBy` bigint(20) DEFAULT NULL,
  `proCreatedDate` datetime DEFAULT NULL,
  `proExternalSystemId` varchar(255) DEFAULT NULL,
  `proForeignSystemId` varchar(255) DEFAULT NULL,
  `proUpdatedBy` bigint(20) DEFAULT NULL,
  `proUpdatedDate` datetime DEFAULT NULL,
  `proEndDate` timestamp(4) NULL DEFAULT NULL,
  `proStartDate` timestamp(4) NULL DEFAULT NULL,
  `proRole` varchar(255) DEFAULT NULL,
  `proPartyUIDpar` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`proUID`,`REV`),
  KEY `FKE8443DF04E4CA39B` (`REV`),
  CONSTRAINT `FKE8443DF04E4CA39B` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pscPaymentSchedule`
--

DROP TABLE IF EXISTS `pscPaymentSchedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pscPaymentSchedule` (
  `pscUID` bigint(20) NOT NULL,
  `pscAttribute` longtext,
  `pscCreatedDate` datetime DEFAULT NULL,
  `pscExternalSystemId` varchar(255) NOT NULL,
  `pscForeignSystemId` varchar(255) DEFAULT NULL,
  `pscLock` bit(1) DEFAULT NULL,
  `pscSerialVersion` bigint(20) NOT NULL,
  `pscUpdatedDate` datetime DEFAULT NULL,
  `pscDescription` varchar(255) DEFAULT NULL,
  `pscCreatedBy` bigint(20) DEFAULT NULL,
  `pscUpdatedBy` bigint(20) DEFAULT NULL,
  `pscType` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`pscUID`),
  UNIQUE KEY `pscExternalSystemId` (`pscExternalSystemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pscPaymentSchedule_`
--

DROP TABLE IF EXISTS `pscPaymentSchedule_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pscPaymentSchedule_` (
  `pscUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `pscAttribute` longtext,
  `pscCreatedBy` bigint(20) DEFAULT NULL,
  `pscCreatedDate` datetime DEFAULT NULL,
  `pscExternalSystemId` varchar(255) DEFAULT NULL,
  `pscForeignSystemId` varchar(255) DEFAULT NULL,
  `pscUpdatedBy` bigint(20) DEFAULT NULL,
  `pscUpdatedDate` datetime DEFAULT NULL,
  `pscDescription` varchar(255) DEFAULT NULL,
  `pscType` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`pscUID`,`REV`),
  KEY `FK759B0C2252AE8777` (`REV`),
  CONSTRAINT `FK759B0C2252AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pulProductUpgradeLog`
--

DROP TABLE IF EXISTS `pulProductUpgradeLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pulProductUpgradeLog` (
  `pulUID` bigint(20) NOT NULL,
  `pulAttribute` longtext,
  `pulCreatedBy` bigint(20) DEFAULT NULL,
  `pulCreatedDate` datetime DEFAULT NULL,
  `pulExternalSystemId` varchar(255) NOT NULL,
  `pulForeignSystemId` varchar(255) DEFAULT NULL,
  `pulLock` bit(1) DEFAULT NULL,
  `pulSerialVersion` bigint(20) NOT NULL,
  `pulUpdatedBy` bigint(20) DEFAULT NULL,
  `pulUpdatedDate` datetime DEFAULT NULL,
  `pulCheckSum` bigint(20) DEFAULT NULL,
  `pulCommandName` varchar(255) DEFAULT NULL,
  `pulProductName` varchar(255) DEFAULT NULL,
  `pulRunDate` timestamp(4) NULL DEFAULT NULL,
  `pulException` longtext,
  `pulSuccess` bit(1) NOT NULL,
  PRIMARY KEY (`pulUID`),
  UNIQUE KEY `pulExternalSystemId` (`pulExternalSystemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pviPageVisit_`
--

DROP TABLE IF EXISTS `pviPageVisit_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pviPageVisit_` (
  `pviUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `pviAttribute` longtext,
  `pviCreatedBy` bigint(20) DEFAULT NULL,
  `pviCreatedDate` datetime DEFAULT NULL,
  `pviExternalSystemId` varchar(255) DEFAULT NULL,
  `pviForeignSystemId` varchar(255) DEFAULT NULL,
  `pviUpdatedBy` bigint(20) DEFAULT NULL,
  `pviUpdatedDate` datetime DEFAULT NULL,
  `pviPageFlowName` varchar(255) DEFAULT NULL,
  `pviPageName` varchar(255) DEFAULT NULL,
  `pviVisited` datetime DEFAULT NULL,
  PRIMARY KEY (`pviUID`,`REV`),
  KEY `FKBF10836652AE8777` (`REV`),
  CONSTRAINT `FKBF10836652AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recRevisionChanges`
--

DROP TABLE IF EXISTS `recRevisionChanges`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recRevisionChanges` (
  `recRev` int(11) NOT NULL,
  `recEntityName` varchar(255) DEFAULT NULL,
  KEY `FK24FB93B850F9AED8` (`recRev`),
  CONSTRAINT `FK24FB93B850F9AED8` FOREIGN KEY (`recRev`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `revRevision`
--

DROP TABLE IF EXISTS `revRevision`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `revRevision` (
  `revId` int(11) NOT NULL AUTO_INCREMENT,
  `revTimestamp` bigint(20) NOT NULL,
  `revServiceRequestRecord` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`revId`),
  KEY `FKFAAB819E70D05A0` (`revServiceRequestRecord`),
  CONSTRAINT `FKFAAB819E70D05A0` FOREIGN KEY (`revServiceRequestRecord`) REFERENCES `srrServiceRequestRecord` (`srrUID`)
) ENGINE=InnoDB AUTO_INCREMENT=785 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `secSection`
--

DROP TABLE IF EXISTS `secSection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `secSection` (
  `secUID` bigint(20) NOT NULL,
  `secAttribute` longtext,
  `secCreatedDate` datetime DEFAULT NULL,
  `secExternalSystemId` varchar(255) NOT NULL,
  `secForeignSystemId` varchar(255) DEFAULT NULL,
  `secLock` tinyint(1) NOT NULL,
  `secSerialVersion` bigint(20) NOT NULL,
  `secUpdatedDate` datetime DEFAULT NULL,
  `secAssetId` longtext,
  `secExcessId` longtext,
  `secExcluded` varchar(255) DEFAULT NULL,
  `secId` varchar(255) DEFAULT NULL,
  `secIncluded` varchar(255) DEFAULT NULL,
  `secSectionTypeId` varchar(255) DEFAULT NULL,
  `secUninsuredAssetId` longtext,
  `secWording` longtext,
  `secCreatedBy` bigint(20) DEFAULT NULL,
  `secUpdatedBy` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`secUID`),
  UNIQUE KEY `secExternalSystemId` (`secExternalSystemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `secSection_`
--

DROP TABLE IF EXISTS `secSection_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `secSection_` (
  `secUID` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `secAttribute` longtext,
  `secCreatedBy` bigint(20) DEFAULT NULL,
  `secCreatedDate` datetime DEFAULT NULL,
  `secExternalSystemId` varchar(255) DEFAULT NULL,
  `secForeignSystemId` varchar(255) DEFAULT NULL,
  `secUpdatedBy` bigint(20) DEFAULT NULL,
  `secUpdatedDate` datetime DEFAULT NULL,
  `secAssetId` longtext,
  `secClause` longtext,
  `secExcessId` longtext,
  `secExcluded` varchar(255) DEFAULT NULL,
  `secId` varchar(255) DEFAULT NULL,
  `secIncluded` varchar(255) DEFAULT NULL,
  `secSectionTypeId` varchar(255) DEFAULT NULL,
  `secUninsuredAssetId` longtext,
  `secWording` longtext,
  PRIMARY KEY (`secUID`,`REV`),
  KEY `FKD6879D8B52AE8777` (`REV`),
  CONSTRAINT `FKD6879D8B52AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sgeSequenceGenerator`
--

DROP TABLE IF EXISTS `sgeSequenceGenerator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sgeSequenceGenerator` (
  `sequence_name` varchar(255) NOT NULL,
  `next_val` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`sequence_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `srrServiceRequestRecord`
--

DROP TABLE IF EXISTS `srrServiceRequestRecord`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `srrServiceRequestRecord` (
  `srrUID` bigint(20) NOT NULL,
  `srrAttribute` longtext,
  `srrCreatedDate` datetime DEFAULT NULL,
  `srrExternalSystemId` varchar(255) NOT NULL,
  `srrForeignSystemId` varchar(255) DEFAULT NULL,
  `srrLock` tinyint(1) NOT NULL,
  `srrSerialVersion` bigint(20) NOT NULL,
  `srrUpdatedDate` datetime DEFAULT NULL,
  `srrCommand` varchar(255) DEFAULT NULL,
  `srrEntryTimestamp` datetime DEFAULT NULL,
  `srrExitTimestamp` datetime DEFAULT NULL,
  `srrExternalPolicyId` varchar(255) DEFAULT NULL,
  `srrProduct` varchar(255) DEFAULT NULL,
  `srrRequest` longtext,
  `srrResponse` longtext,
  `srrCreatedBy` bigint(20) DEFAULT NULL,
  `srrUpdatedBy` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`srrUID`),
  UNIQUE KEY `srrExternalSystemId` (`srrExternalSystemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ukeUniqueKey`
--

DROP TABLE IF EXISTS `ukeUniqueKey`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ukeUniqueKey` (
  `ukeId` varchar(255) NOT NULL,
  `ukeValue` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ukeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ukeUniqueKey_`
--

DROP TABLE IF EXISTS `ukeUniqueKey_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ukeUniqueKey_` (
  `ukeId` varchar(255) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `ukeValue` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ukeId`,`REV`),
  KEY `FK8B7C574052AE8777` (`REV`),
  CONSTRAINT `FK8B7C574052AE8777` FOREIGN KEY (`REV`) REFERENCES `revRevision` (`revId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-05-10 10:23:16

LOCK TABLES `liqChangeLog` WRITE;
/*!40000 ALTER TABLE `liqChangeLog` DISABLE KEYS */;
INSERT INTO `liqChangeLog` VALUES ('OU-715 Add pceProductChangeEvent table','richard','db.changelog.xml','2017-10-20 22:04:50',1,'EXECUTED','7:77ddf9897c147123e194eebb7c279417','sql','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('YEL-32 Add payment schedule tables','richard','db.changelog.xml','2017-10-20 22:04:50',2,'EXECUTED','7:90a0cb415a7660e4dac31eb5ff0b255b','sql','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-756 Rename DocumentContent content column','richard','db.changelog.xml','2017-10-20 22:04:50',3,'EXECUTED','7:25fea51897f8c7b30a0f38a9c1577f79','sql','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-756 Add dcoProductTypeId column','richard','db.changelog.xml','2017-10-20 22:04:51',4,'EXECUTED','7:83754e0696eeb6dd44ced0f7e5685a30','sql','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-756 Add fsrForeignSystemReference table','richard','db.changelog.xml','2017-10-20 22:04:51',5,'EXECUTED','7:627bbeb75614c42bf00dd55a304d7c63','sql','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-756 Add jDcoFsrFsr table','richard','db.changelog.xml','2017-10-20 22:04:51',6,'EXECUTED','7:f5ec1d9cb3edea81b75ca4c4a632ea9b','sql','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-764 Add notNote table','richard','db.changelog.xml','2017-10-20 22:04:51',7,'EXECUTED','7:09de8de8e1a02f691c61e80d34507b47','sql','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-764 Add jPolNotNot table','richard','db.changelog.xml','2017-10-20 22:04:51',8,'EXECUTED','7:5e552f0db0e78edeff5973bba21e0c00','sql','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-775 Add createdBy and updatedBy audit columns','richard','db.changelog.xml','2017-10-20 22:04:52',9,'EXECUTED','7:adbcdff04006db9cba2a03941d628b3d','sql','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-796 Add covCoverage table','richard','db.changelog.xml','2017-10-20 22:04:52',10,'EXECUTED','7:609b1b61d3dd9d42c4093ba505f8c05b','sql','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-796 Add join table for Policy Coverage','richard','db.changelog.xml','2017-10-20 22:04:52',11,'EXECUTED','7:2f5c071c6409a768efcb39c2424c85ae','sql','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-796 Add join table for Section Coverage','richard','db.changelog.xml','2017-10-20 22:04:52',12,'EXECUTED','7:4c264b54584f0c5a141a11fd54fde590','sql','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-758 Add assAsset table','richard','db.changelog.xml','2017-10-20 22:04:52',13,'EXECUTED','7:5a1d8e88595d2febf0f88288ff9e9d0a','sql','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-758 Add jAssAssAss table','richard','db.changelog.xml','2017-10-20 22:04:52',14,'EXECUTED','7:ffc1af34dd64b319ad73b0d8291e389b','sql','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-758 Add jPolAssAss table','richard','db.changelog.xml','2017-10-20 22:04:52',15,'EXECUTED','7:9a32a41b355cd8b1d678945a0254fe90','sql','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-825 Change note body column type','richard','db.changelog.xml','2017-10-20 22:04:52',16,'EXECUTED','7:a42ccf7171bc105f84d39efdb80b3a65','sql','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-825 Change document descrption column type','richard','db.changelog.xml','2017-10-20 22:04:52',17,'EXECUTED','7:89ee43f12700ae81b622c598a0492cab','sql','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-823 Add renewal and MTA indexes to Policy','richard','db.changelog.xml','2017-10-20 22:04:52',18,'EXECUTED','7:f1c0476e6dcdc891bf02b49c1515b944','sql','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-854 Add phoPaymentHoliday table','richard','db.changelog.xml','2017-10-20 22:04:52',19,'EXECUTED','7:a980141212abb93e12ab84f4f9ec2ce2','sql','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-862 Add mprDay column','richard','db.changelog.xml','2017-10-20 22:04:52',20,'EXECUTED','7:47d39565053dc3b5c0f6685bf5dd2a41','sql','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('YEL-32 Migrate payment date from XML to relational model','mat','db.changelog.xml','2017-10-20 22:04:56',21,'EXECUTED','7:64ae2c6288ef2bbd1174f53b1079d4a0','executeCommand; executeCommand; executeCommand','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-796 Migrate policy coverage date from XML to relational model','richard','db.changelog.xml','2017-10-20 22:04:57',22,'EXECUTED','7:64ae2c6288ef2bbd1174f53b1079d4a0','executeCommand; executeCommand; executeCommand','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-796 Migrate section coverage date from XML to relational model','richard','db.changelog.xml','2017-10-20 22:05:01',23,'EXECUTED','7:64ae2c6288ef2bbd1174f53b1079d4a0','executeCommand; executeCommand; executeCommand','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-796 Drop redundant columns','richard','db.changelog.xml','2017-10-20 22:05:01',24,'EXECUTED','7:7b17fd815a062ed7d5e797af9b36a712','sql','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-758 Migrate asset date from XML to relational model','richard','db.changelog.xml','2017-10-20 22:05:09',25,'EXECUTED','7:64ae2c6288ef2bbd1174f53b1079d4a0','executeCommand; executeCommand; executeCommand','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-758 Drop redundant columns','richard','db.changelog.xml','2017-10-20 22:05:09',26,'EXECUTED','7:ea2a2ff2daeefb5152d54acf16bfc76f','sql','',NULL,'3.5.3',NULL,NULL,'8533490648');
INSERT INTO `liqChangeLog` VALUES ('OU-877 Correct name of paymentMethodUIDpme column to mprPaymentMethodUIDpme','richard','db.changelog.xml','2017-10-23 11:33:33',27,'EXECUTED','7:0c9cf669c662c535d6d228c9044c6cb4','sql','',NULL,'3.5.3',NULL,NULL,'8754813160');
INSERT INTO `liqChangeLog` VALUES ('OU-863 Add financials tables','richard','db.changelog.xml','2018-05-09 23:54:44',28,'EXECUTED','7:bd709c4ce65dfa568156242b30ca29d2','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-868 add account status column','richard','db.changelog.xml','2018-05-09 23:54:44',29,'EXECUTED','7:31602ee8c565e7be285a7f11f4f92704','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-868 add journal to money provision link','richard','db.changelog.xml','2018-05-09 23:54:44',30,'EXECUTED','7:d981958730e4d0e9773e0268acf8d655','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-868 correct accountHolderUIDpar to accountHolderUIDpty','richard','db.changelog.xml','2018-05-09 23:54:44',31,'EXECUTED','7:616ae6b0337f151e0c78dfa64770268e','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-868 drop accOpeningCurrency (accCurrency makes it redundant)','richard','db.changelog.xml','2018-05-09 23:54:44',32,'EXECUTED','7:dc5e800288ae3f6dd838e191b27a9678','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-887 add apeAccountingPeriod table','richard','db.changelog.xml','2018-05-09 23:54:44',33,'EXECUTED','7:fce1e3417a132dfd5ebb6caa98538eb4','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-887 add FK from jouJournal to apeAccountingPeriod','richard','db.changelog.xml','2018-05-09 23:54:44',34,'EXECUTED','7:e2d0f9bf16615c5d8a402380fd5d2564','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-887 add balBalance table','richard','db.changelog.xml','2018-05-09 23:54:44',35,'EXECUTED','7:f7749dabcab0cdb3a60c1d4c0a9e5811','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-887 add FK from jliJournalLine to jouJournal','richard','db.changelog.xml','2018-05-09 23:54:44',36,'EXECUTED','7:2d2de25c7a9dfcb6db6fc3aa127a1c3b','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-880 Create audit tables','richard','db.changelog.xml','2018-05-09 23:54:45',37,'EXECUTED','7:59c44fe11e56bc6a48dcd947179b0898','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-893 status and name to pmePaymentMethod','richard','db.changelog.xml','2018-05-09 23:54:45',38,'EXECUTED','7:fef32b8f902e39fe880335ed597a1db5','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-893 add type to pscPaymentSchedule','richard','db.changelog.xml','2018-05-09 23:54:45',39,'EXECUTED','7:8cb5c70b628b78313afd9729de6ec2e7','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-893 fix non-standard column names in pmePaymentMethod','richard','db.changelog.xml','2018-05-09 23:54:45',40,'EXECUTED','7:e21f8f4d0482dc84a558079fc728c4cf','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-893 fix non-standard column name in parParty','richard','db.changelog.xml','2018-05-09 23:54:46',41,'EXECUTED','7:eecb69e5667282e86956d7c92cb3e7ef','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-893 add parParty - pmePaymentMethod join','richard','db.changelog.xml','2018-05-09 23:54:46',42,'EXECUTED','7:49291c0f726ba197127fd2968159c95e','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-896 add parContactPreference to party','richardj','db.changelog.xml','2018-05-09 23:54:46',43,'EXECUTED','7:84ef0ab2f7b9c811416cd856868122a0','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-900 add phoPaymentHoliday - notNote join','matthewt','db.changelog.xml','2018-05-09 23:54:46',44,'EXECUTED','7:929075484258402508f8cff9d0acf47e','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-907 mprPurpose column','richard','db.changelog.xml','2018-05-09 23:54:46',45,'EXECUTED','7:655923b5dc4d272788562b99a63fc602','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-931 add parParty - notNote join','richardj','db.changelog.xml','2018-05-09 23:54:46',46,'EXECUTED','7:fabc7de4f76080c8522dc3ed2bb20ce8','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-934 Add claims related tables','richard','db.changelog.xml','2018-05-09 23:54:46',47,'EXECUTED','7:46b666b79b229402fe882e811221f92a','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-935 accountName and branchNname to pmePaymentMethod','richardj','db.changelog.xml','2018-05-09 23:54:46',48,'EXECUTED','7:20f9429eaf0996e98ee531c2dc6d8665','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-780 Add label support to claim, note, policy and party.','richard','db.changelog.xml','2018-05-09 23:54:46',49,'EXECUTED','7:6ef254c66836fcf912f477555ea80dac','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-780 Add type field to notNote.','richard','db.changelog.xml','2018-05-09 23:54:46',50,'EXECUTED','7:d6cf00f8261ce0f2eb79b45fc3a5f1d1','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-948 Add labels to docDocument','richard','db.changelog.xml','2018-05-09 23:54:46',51,'EXECUTED','7:3516852963bc11d8d6c7c791dd863172','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-949 Add Document to Claim','richard','db.changelog.xml','2018-05-09 23:54:46',52,'EXECUTED','7:8b7625db1108fe546930c23905272190','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-951 Add clause table','richard','db.changelog.xml','2018-05-09 23:54:47',53,'EXECUTED','7:0d6706eeb85eb2567737c8a31cea98c3','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-962 Add advisory to Clause','richard','db.changelog.xml','2018-05-09 23:54:47',54,'EXECUTED','7:fccccd06bee4f9ef87e0737f8f9f807b','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-967 Add start and end date to Claim','richard','db.changelog.xml','2018-05-09 23:54:47',55,'EXECUTED','7:775f5a27f97d6f727819696eb5f5a378','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-993 fix badly named secREV column to REV in all audit tables','richard','db.changelog.xml','2018-05-09 23:54:51',56,'EXECUTED','7:852a1219fb90fd61717538fa3377f961','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-993 fix badly named collection and join audit columns','richard','db.changelog.xml','2018-05-09 23:54:51',57,'EXECUTED','7:2063ee5316b9645cdb632828de5b7ff0','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-970 Add documents to party','mattc','db.changelog.xml','2018-05-09 23:54:51',58,'EXECUTED','7:9ab9f2d5756d31e8583c8c7705435fa3','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-989 Create proPartyRole tables','richard','db.changelog.xml','2018-05-09 23:54:51',59,'EXECUTED','7:09b8105770404a204ba927ad83b06d36','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-989 Create claim to party role join','richard','db.changelog.xml','2018-05-09 23:54:51',60,'EXECUTED','7:78075a0102cd94f7194c4754f3a0343f','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-989 Create policy to party role join','richard','db.changelog.xml','2018-05-09 23:54:51',61,'EXECUTED','7:aa8fa9f474a5da5a7b0f8c6482570c54','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-989 Add id column to Party','richard','db.changelog.xml','2018-05-09 23:54:51',62,'EXECUTED','7:7f71e941e0f1ebc4058b6d52c5a87667','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-989 Remove redundant column from pme','richard','db.changelog.xml','2018-05-09 23:54:52',63,'EXECUTED','7:44968bfcf1ff9a0b6d9cea10613b1af6','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-1006 Add prlPartyRelationship','richard','db.changelog.xml','2018-05-09 23:54:52',64,'EXECUTED','7:d3f81a7dc976c7c7f6f415ada93bbe31','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-1015 Add csyContactSystem','richard','db.changelog.xml','2018-05-09 23:54:52',65,'EXECUTED','7:c145401eb5cf5d8fc096156eea1a302f','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-1025 Add document placeholders','mattc','db.changelog.xml','2018-05-09 23:54:52',66,'EXECUTED','7:38f211bb1671ae6054e1afea80b24243','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-1033 add pulProductUpgradeLog','mattc','db.changelog.xml','2018-05-09 23:54:52',67,'EXECUTED','7:8b7144e9996391ed2c73c016145344c4','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-1041 add success and exception to pulProductUpgradeLog','mattc','db.changelog.xml','2018-05-09 23:54:52',68,'EXECUTED','7:8ec8ce72a750f7ee3e9be829e5820ea3','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-1039 Update apeAccountingPeriod start and end date','richarda','db.changelog.xml','2018-05-09 23:54:52',69,'EXECUTED','7:757de535e2da706e0d3f7994d8c6647e','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-863 replace strings in Reference attributes with class names','richard','db.changelog.xml','2018-05-09 23:54:52',70,'EXECUTED','7:89d7d3f55180a34749edb9c22454ccec','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-887 Migrate opening balance from accAccout to balBalance','richard','db.changelog.xml','2018-05-09 23:54:52',71,'EXECUTED','7:b5f3fddb8ab8f5e8b7db66bd1366fccb','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-887 Drop redundant columns','richard','db.changelog.xml','2018-05-09 23:54:52',72,'EXECUTED','7:eb9ab6c6df74be60cd4b677f51a508ed','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-951 Migrate clause date from XML to relational model','richard','db.changelog.xml','2018-05-09 23:54:54',73,'EXECUTED','7:64ae2c6288ef2bbd1174f53b1079d4a0','executeCommand; executeCommand; executeCommand','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-951 Drop redundant columns','richard','db.changelog.xml','2018-05-09 23:54:55',74,'EXECUTED','7:9269bbec7b99a6a60b77d84d00f8bf79','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-989 Migrate policy parties to party roles','richard','db.changelog.xml','2018-05-09 23:54:57',75,'EXECUTED','7:64ae2c6288ef2bbd1174f53b1079d4a0','executeCommand; executeCommand; executeCommand','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-989 Drop redundant columns','richard','db.changelog.xml','2018-05-09 23:54:57',76,'EXECUTED','7:f7281963a034e0423156f87ed0179e13','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-1015 Migrate party contact data','richard','db.changelog.xml','2018-05-09 23:54:59',77,'EXECUTED','7:64ae2c6288ef2bbd1174f53b1079d4a0','executeCommand; executeCommand; executeCommand','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-1015 Drop redundant columns','richard','db.changelog.xml','2018-05-09 23:54:59',78,'EXECUTED','7:ecfae9960380dedd67ff6d3fbcf4a440','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
INSERT INTO `liqChangeLog` VALUES ('OU-1053 Add claim number key into uke','richard','db.changelog.xml','2018-05-09 23:54:59',79,'EXECUTED','7:88ae5d94a817e633d894508750a6834f','sql','',NULL,'3.5.3',NULL,NULL,'5906483831');
/*!40000 ALTER TABLE `liqChangeLog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `liqChangeLogLock`
--

LOCK TABLES `liqChangeLogLock` WRITE;
/*!40000 ALTER TABLE `liqChangeLogLock` DISABLE KEYS */;
INSERT INTO `liqChangeLogLock` VALUES (1,'\0',NULL,NULL);
/*!40000 ALTER TABLE `liqChangeLogLock` ENABLE KEYS */;
UNLOCK TABLES;
