ALTER TABLE polPolicy ADD COLUMN polOwningUser bigint;
UPDATE polPolicy SET polOwningUser=polUsername;
ALTER TABLE polPolicy DROP COLUMN polUsername;
