CREATE DATABASE IF NOT EXISTS ##dbname.liferay## character set utf8;
GRANT ALL ON ##dbname.liferay##.* TO '##dbusername##'@'localhost' IDENTIFIED BY '##dbpassword##' WITH GRANT OPTION;
GRANT ALL ON ##dbname.liferay##.* TO '##dbusername##'@'localhost.localdomain' IDENTIFIED BY '##dbpassword##' WITH GRANT OPTION;

SET FOREIGN_KEY_CHECKS=0;

USE ##dbname.liferay##;

source ./content/liferay/Liferay.sql

