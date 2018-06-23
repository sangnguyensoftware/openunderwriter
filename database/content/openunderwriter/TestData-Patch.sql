-- TestData-Redate
--
-- Update policy records created by the TestData script with dates that are valid from the date run.

UPDATE polPolicy SET 
	polCreatedDate = now(),
	polInceptionDate = now() + interval 10 day,
	polExpiryDate = now() + interval 1 year + interval 10 day,
	polQuotationDate = now(),
	polQuotationExpiryDate = now() + interval 1 month,
	polVersionEffectiveDate = now();
