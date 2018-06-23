-- Create example actuarial databases for use by OpenActuary users
create database if not exists ##dbname.dwh.prefix##_AIL_Base_DataSource_Master_Motor character set utf8;
grant insert,select,update,delete on ##dbname.dwh.prefix##_AIL_Base_DataSource_Master_Motor.* to 'adam'@'localhost' identified by 'Dhj0TB/sA+xAg0IstYumoJrE+yo=';
grant select on ##dbname.dwh.prefix##_AIL_Base_DataSource_Master_Motor.* to 'charlie'@'localhost' identified by '2M0QuSDcvbUWPKAYXkAjV7wnwmU=';
grant select on ##dbname.dwh.prefix##_AIL_Base_DataSource_Master_Motor.* to 'lily'@'localhost' identified by '/dvgGFfi06tQW/wdrDs4osi4zWU=';
grant select on ##dbname.dwh.prefix##_AIL_Base_DataSource_Master_Motor.* to 'oliver'@'localhost' identified by 'xTkVO6H5R71Lb5ECY7lnxKCmI1c=';
grant select on ##dbname.dwh.prefix##_AIL_Base_DataSource_Master_Motor.* to 'ethan'@'localhost' identified by 'CifhLQYq1xZz1X+cJ5myB68xaIU=';
grant select on ##dbname.dwh.prefix##_AIL_Base_DataSource_Master_Motor.* to 'sophie'@'localhost' identified by 'X1BEO/529yeajg8vCpiXXNv/OOk=';
grant select on ##dbname.dwh.prefix##_AIL_Base_DataSource_Master_Motor.* to 'thomas'@'localhost' identified by 'X1CoTB+jvP8UZAUBfzauwaEKnjg=';
grant select on ##dbname.dwh.prefix##_AIL_Base_DataSource_Master_Motor.* to 'report'@'localhost' identified by 'e98d2f001da5678b39482efbdf5770dc';
use ##dbname.dwh.prefix##_AIL_Base_DataSource_Master_Motor;
source ./content/data-warehouse/OpenActuary-Table-Setup.sql
source ./content/data-warehouse/OpenActuary-Standing-Data-Setup.sql
source ./content/data-warehouse/OpenActuary-Trigger-Setup.sql

create database if not exists ##dbname.dwh.prefix##_MetropolitanInsurance_Motor_Staging character set utf8;
grant insert,select,update,delete on ##dbname.dwh.prefix##_MetropolitanInsurance_Motor_Staging.* to 'charlie'@'localhost' identified by '2M0QuSDcvbUWPKAYXkAjV7wnwmU=';
grant select on ##dbname.dwh.prefix##_MetropolitanInsurance_Motor_Staging.* to 'lily'@'localhost' identified by '/dvgGFfi06tQW/wdrDs4osi4zWU=';
grant select on ##dbname.dwh.prefix##_MetropolitanInsurance_Motor_Staging.* to 'oliver'@'localhost' identified by 'xTkVO6H5R71Lb5ECY7lnxKCmI1c=';
grant select on ##dbname.dwh.prefix##_MetropolitanInsurance_Motor_Staging.* to 'report'@'localhost' identified by 'e98d2f001da5678b39482efbdf5770dc';
use ##dbname.dwh.prefix##_MetropolitanInsurance_Motor_Staging;
source ./content/data-warehouse/OpenActuary-Table-Setup.sql
source ./content/data-warehouse/OpenActuary-Standing-Data-Setup.sql
source ./content/data-warehouse/OpenActuary-Trigger-Setup.sql

create database if not exists ##dbname.dwh.prefix##_StarInsurance_Motor_Staging  character set utf8;
grant insert,select,update,delete on ##dbname.dwh.prefix##_StarInsurance_Motor_Staging.* to 'ethan'@'localhost' identified by 'CifhLQYq1xZz1X+cJ5myB68xaIU=';
grant select on ##dbname.dwh.prefix##_StarInsurance_Motor_Staging.* to 'sophie'@'localhost' identified by 'X1BEO/529yeajg8vCpiXXNv/OOk=';
grant select on ##dbname.dwh.prefix##_StarInsurance_Motor_Staging.* to 'thomas'@'localhost' identified by 'X1CoTB+jvP8UZAUBfzauwaEKnjg=';
grant select on ##dbname.dwh.prefix##_StarInsurance_Motor_Staging.* to 'report'@'localhost' identified by 'e98d2f001da5678b39482efbdf5770dc';
use ##dbname.dwh.prefix##_StarInsurance_Motor_Staging;
source ./content/data-warehouse/OpenActuary-Table-Setup.sql
source ./content/data-warehouse/OpenActuary-Standing-Data-Setup.sql
source ./content/data-warehouse/OpenActuary-Trigger-Setup.sql

create database if not exists ##dbname.dwh.prefix##_AIL_Base_DataSource_Test character set utf8;
grant insert,select,update,delete on ##dbname.dwh.prefix##_AIL_Base_DataSource_Test.* to 'adam'@'localhost' identified by 'Dhj0TB/sA+xAg0IstYumoJrE+yo=';
grant select on ##dbname.dwh.prefix##_AIL_Base_DataSource_Test.* to 'charlie'@'localhost' identified by '2M0QuSDcvbUWPKAYXkAjV7wnwmU=';
grant select on ##dbname.dwh.prefix##_AIL_Base_DataSource_Test.* to 'lily'@'localhost' identified by '/dvgGFfi06tQW/wdrDs4osi4zWU=';
grant select on ##dbname.dwh.prefix##_AIL_Base_DataSource_Test.* to 'oliver'@'localhost' identified by 'xTkVO6H5R71Lb5ECY7lnxKCmI1c=';
grant select on ##dbname.dwh.prefix##_AIL_Base_DataSource_Test.* to 'ethan'@'localhost' identified by 'CifhLQYq1xZz1X+cJ5myB68xaIU=';
grant select on ##dbname.dwh.prefix##_AIL_Base_DataSource_Test.* to 'sophie'@'localhost' identified by 'X1BEO/529yeajg8vCpiXXNv/OOk=';
grant select on ##dbname.dwh.prefix##_AIL_Base_DataSource_Test.* to 'thomas'@'localhost' identified by 'X1CoTB+jvP8UZAUBfzauwaEKnjg=';
grant select on ##dbname.dwh.prefix##_AIL_Base_DataSource_Test.* to 'report'@'localhost' identified by 'e98d2f001da5678b39482efbdf5770dc';


-- Create example reporting database for use by OpenQuote users
create database if not exists ##dbname.dwh.prefix##_AIL_Base_Report character set utf8;
grant insert,select,update,delete on ##dbname.dwh.prefix##_AIL_Base_Report.* to 'adam'@'localhost' identified by 'Dhj0TB/sA+xAg0IstYumoJrE+yo=';
grant select on ##dbname.dwh.prefix##_AIL_Base_Report.* to 'charlie'@'localhost' identified by '2M0QuSDcvbUWPKAYXkAjV7wnwmU=';
grant select on ##dbname.dwh.prefix##_AIL_Base_Report.* to 'lily'@'localhost' identified by '/dvgGFfi06tQW/wdrDs4osi4zWU=';
grant select on ##dbname.dwh.prefix##_AIL_Base_Report.* to 'oliver'@'localhost' identified by 'xTkVO6H5R71Lb5ECY7lnxKCmI1c=';
grant select on ##dbname.dwh.prefix##_AIL_Base_Report.* to 'ethan'@'localhost' identified by 'CifhLQYq1xZz1X+cJ5myB68xaIU=';
grant select on ##dbname.dwh.prefix##_AIL_Base_Report.* to 'sophie'@'localhost' identified by 'X1BEO/529yeajg8vCpiXXNv/OOk=';
grant select on ##dbname.dwh.prefix##_AIL_Base_Report.* to 'thomas'@'localhost' identified by 'X1CoTB+jvP8UZAUBfzauwaEKnjg=';
grant select on ##dbname.dwh.prefix##_AIL_Base_Report.* to 'report'@'localhost' identified by 'e98d2f001da5678b39482efbdf5770dc';
use ##dbname.dwh.prefix##_AIL_Base_Report;
 
create table if not exists bordereau (
      id bigint(20) auto_increment primary key,
      broker varchar(255),
      quote_number varchar(255),
      policy_number varchar(255),
      quote_date date,
      accepted_date date,
      inception_date date,
      status varchar(255),
	  policy_holder_name varchar(255),
      premium_amount decimal(19,2),
      premium_currency varchar(255),
      product varchar(255),
      commission decimal(19,2),
      tax decimal(19,2),
      test varchar(5),
      agent varchar(255),
      expiry_date date,
      address_state varchar(255),
      stamp_duty decimal(19,2),
      commission_tax decimal(19,2),
      total_premium_amount decimal(19,2)
  ) ENGINE=InnoDB;


  create table if not exists report_summary (
      id bigint(20) auto_increment primary key,
      broker varchar(255),
      quote_date date,
      accepted_date date,
      status varchar(255),
      premium_amount decimal(19,2),
      premium_currency varchar(255),
      product varchar(255)
  ) ENGINE=InnoDB;
  
  create table if not exists pre_policy (
      id bigint(20) auto_increment primary key,
      policy_system_id bigint(20),
      broker varchar(255),
	  quote_number varchar(255),
	  quote_date date,
	  status varchar(255),
	  page varchar(255),
	  policy_holder_name varchar(255),
	  policy_holder_number varchar(255),
	  policy_holder_email varchar(255),
	  premium_amount decimal(19,2),
	  premium_currency varchar(255),
	  product varchar(255),
	  quote_exception varchar(1000),
	  test varchar(5)
  ) ENGINE=InnoDB;
  
  create table if not exists referral_reason (
     id BIGINT(20) auto_increment primary key,
     policy_system_id BIGINT(20) NOT NULL,
     referral_reason  VARCHAR(1000) NULL
    ) ENGINE=InnoDB;
  
  
  create table if not exists export_summary (
      id bigint(20) auto_increment primary key,
      report_name varchar(255),
      last_success_date datetime
  ) ENGINE=InnoDB;
  
  delete from export_summary;
 
  insert into export_summary (report_name, last_success_date) values('bordereau', NOW());
  insert into export_summary (report_name, last_success_date) values('pre_policy', NOW());



  
  