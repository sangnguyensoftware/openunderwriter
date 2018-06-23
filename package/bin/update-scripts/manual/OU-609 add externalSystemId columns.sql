# Add external system ID columns to the following table: 
# - ashAssessmentSheet 
# - docDocument 
# - dreDocumentRequest 
# - parParty 
# - polPolicy 
# - secSection 

ALTER TABLE ashAssessmentSheet 
  ADD COLUMN ashExternalSystemId VARCHAR(255); 

UPDATE ashAssessmentSheet 
SET    ashExternalSystemId = Concat(Uuid(), ashUID) 
WHERE  ashExternalSystemId IS NULL; 

ALTER TABLE ashAssessmentSheet 
  MODIFY ashExternalSystemId VARCHAR(255) NOT NULL UNIQUE; 

ALTER TABLE docDocument 
  ADD COLUMN docExternalSystemId VARCHAR(255); 

UPDATE docDocument 
SET    docExternalSystemId = Concat(Uuid(), docUID) 
WHERE  docExternalSystemId IS NULL; 

ALTER TABLE docDocument 
  MODIFY docExternalSystemId VARCHAR(255) NOT NULL UNIQUE; 

ALTER TABLE dreDocumentRequest 
  ADD COLUMN dreExternalSystemId VARCHAR(255); 

UPDATE dreDocumentRequest 
SET    dreExternalSystemId = Concat(Uuid(), dreUID) 
WHERE  dreExternalSystemId IS NULL; 

ALTER TABLE dreDocumentRequest 
  MODIFY dreExternalSystemId VARCHAR(255) NOT NULL UNIQUE; 

ALTER TABLE parParty 
  ADD COLUMN parExternalSystemId VARCHAR(255); 

UPDATE parParty 
SET    parExternalSystemId = Concat(Uuid(), parUID) 
WHERE  parExternalSystemId IS NULL; 

ALTER TABLE parParty 
  MODIFY parExternalSystemId VARCHAR(255) NOT NULL UNIQUE; 

ALTER TABLE polPolicy 
  ADD COLUMN polExternalSystemId VARCHAR(255); 

UPDATE polPolicy 
SET    polExternalSystemId = Concat(Uuid(), polUID) 
WHERE  polExternalSystemId IS NULL; 

ALTER TABLE polPolicy 
  MODIFY polExternalSystemId VARCHAR(255) NOT NULL UNIQUE; 

ALTER TABLE secSection 
  ADD COLUMN secExternalSystemId VARCHAR(255); 

UPDATE secSection 
SET    secExternalSystemId = Concat(Uuid(), secUID) 
WHERE  secExternalSystemId IS NULL; 

ALTER TABLE secSection 
  MODIFY secExternalSystemId VARCHAR(255) NOT NULL UNIQUE; 