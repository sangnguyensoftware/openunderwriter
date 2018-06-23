CREATE TABLE  IF NOT EXISTS srrServiceRequestRecord
(
   srrUID                bigint(20) NOT NULL,
   srrAttribute          longtext,
   srrCreatedDate        datetime,
   srrExternalSystemId   varchar(255) NOT NULL,
   srrForeignSystemId    varchar(255),
   srrLock               tinyint(1) NOT NULL,
   srrSerialVersion      bigint(20) NOT NULL,
   srrUpdatedDate        datetime,
   srrEntryTimestamp     datetime,
   srrExitTimestamp      datetime,
   srrProduct            varchar(255),
   srrCommand            varchar(255),
   srrExternalPolicyId   varchar(255),
   srrRequest            longtext,
   srrResponse           longtext
)  ENGINE=InnoDB;