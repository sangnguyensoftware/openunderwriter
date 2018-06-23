# Before running this script, you must confirm that the values being inserted in the ukeValue
# column match up with the next unused policy/quote/invoice numbers on the system being upgraded.
# This can be achieved by inspecting the values returned by (and similar for QuoteNumber):
#
#  SELECT polPolicyNumber FROM polPolicy WHERE polPolicyNumber IS NOT NULL ORDER BY polUID DESC LIMIT 1;
#
# Also, check the release notes on: https://openunderwriter.atlassian.net/browse/OU-594
#
# Remove the following line once the values are configured.
SIGNAL SQLSTATE '99999' Set MESSAGE_TEXT='This script must be edited before it can be run.';

CREATE TABLE IF NOT EXISTS ukeUniqueKey
(
   ukeId      varchar(255) NOT NULL PRIMARY KEY,
   ukeValue   bigint(20)
) ENGINE=InnoDB;

INSERT IGNORE INTO ukeUniqueKey (ukeId,ukeValue) VALUES 
	('Base.PolicyNumber', 93174663), 
	('Base.QuoteNumber', 2531);
