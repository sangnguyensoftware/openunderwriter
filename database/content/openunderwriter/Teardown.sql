-- Make sure the database and users are there before we try to delete them!
create database if not exists ##dbname.openunderwriter## character set utf8;
grant all on ##dbname.openunderwriter##.* to '##dbusername##'@'localhost' identified by '##dbpassword##' with grant option;
grant all on ##dbname.openunderwriter##.* to '##dbusername##'@'localhost.localdomain' identified by '##dbpassword##' with grant option;
  
-- Now do the actual tidy up 
drop database if exists ##dbname.openunderwriter##;
revoke all on ##dbname.openunderwriter##.* from '##dbusername##'@'localhost';
revoke all on ##dbname.openunderwriter##.* from '##dbusername##'@'localhost.localdomain';
