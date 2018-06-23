ALTER TABLE dcoDocumentContent
	ADD COLUMN dcoDocumentUIDdoc bigint(20) NOT NULL;

UPDATE dcoDocumentContent dco
       JOIN docDocument doc ON doc.docContentUIDdco = dco.dcoUID
   SET dco.dcoDocumentUIDdoc = doc.docUID;
   
ALTER TABLE docDocument
	DROP FOREIGN KEY FKA2DC0273A6B57804;
	
ALTER TABLE docDocument
   DROP COLUMN docContentUIDdco;