CREATE TABLE IF NOT EXISTS dcoDocumentContent
(
   dcoUID                bigint(20) NOT NULL,
   dcoAttribute          longtext,
   dcoCreatedDate        datetime,
   dcoExternalSystemId   varchar(255) NOT NULL,
   dcoForeignSystemId    varchar(255),
   dcoLock               tinyint(1) NOT NULL,
   dcoSerialVersion      bigint(20) NOT NULL,
   dcoUpdatedDate        datetime,
   dcoContent            longblob,
   PRIMARY KEY(dcoUID),
   UNIQUE KEY dcoExternalSystemId(dcoExternalSystemId)
)
ENGINE = InnoDB;

INSERT INTO dcoDocumentContent(dcoUID,
                               dcoCreatedDate,
                               dcoExternalSystemId,
                               dcoLock,
                               dcoSerialVersion,
                               dcoUpdatedDate,
                               dcoContent)
   SELECT docUID,
          docCreatedDate,
          UUID(),
          0,
          0,
          docUpdatedDate,
          docDocument
     FROM docDocument;

ALTER TABLE docDocument
	ADD COLUMN docContentUIDdco bigint(20) NOT NULL;

UPDATE docDocument
   SET docContentUIDdco = docUID;

ALTER TABLE docDocument
   DROP COLUMN docDocument;