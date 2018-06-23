-- Make sure the database and users are there before we try to delete them!
create database if not exists ##dbname.jbpm## character set utf8;
grant all on ##dbname.jbpm##.* to '##dbusername##'@'localhost' identified by '##dbpassword##' with grant option;
grant all on ##dbname.jbpm##.* to '##dbusername##'@'localhost.localdomain' identified by '##dbpassword##' with grant option;
  
-- Now do the actual tidy up 
drop database if exists ##dbname.jbpm##;
revoke all on ##dbname.jbpm##.* from '##dbusername##'@'localhost';
revoke all on ##dbname.jbpm##.* from '##dbusername##'@'localhost.localdomain';
