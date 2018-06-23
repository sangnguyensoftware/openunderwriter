CREATE TABLE verVersion (
  verOverall    varchar(16) DEFAULT '0.0.0' comment 'Overall version of the published schema', 
  verSchema     int(10) DEFAULT 0 NOT NULL comment 'this is the schema version', 
  verAlteration int(10) DEFAULT 0 NOT NULL comment 'this is the most resent alteration that have been applied to the Schema', 
  verStaticData int(10) DEFAULT 0 NOT NULL comment 'This is the most recent version that applies the schema/alteration') comment='With this table we will track versions of Schema, alterations and static data' CHARACTER SET UTF8;
CREATE TABLE occENOccupation (
  name      varchar(32) NOT NULL UNIQUE, 
  validFrom date, 
  validTo   date) comment='Occupation Type' CHARACTER SET UTF8;
CREATE TABLE natENNationality (
  name      varchar(32) NOT NULL UNIQUE, 
  validFrom date, 
  validTo   date) comment='Nationality Type' CHARACTER SET UTF8;
CREATE TABLE empENEmploymentType (
  name      varchar(32) NOT NULL UNIQUE, 
  validFrom date, 
  validTo   date) comment='Employment Type' CHARACTER SET UTF8;
CREATE TABLE couENCountry (
  name      varchar(3) NOT NULL UNIQUE comment 'ISO 3 character name', 
  country   varchar(64) UNIQUE comment 'Full Country name', 
  validFrom date, 
  validTo   date) comment='Country Type' CHARACTER SET UTF8;
CREATE TABLE driDriver (
  driID                                varchar(37) UNIQUE, 
  driAverageAnnualMilage               int(10) DEFAULT 0 comment 'Average annual mileage', 
  driLicenceFirstIssued                date comment 'Date licence first issued', 
  driMainDriver                        tinyint(1) comment 'Is this the main driver', 
  driPassedDrivingTest                 date comment 'Date driving test passed', 
  driVehicleOwner                      tinyint(1) comment 'Is this the vehicle owner', 
  driYearsLicenceHeld                  int(2) comment 'How many years has the licence been held', 
  driDateOfBirth                       date comment 'Date of Birth', 
  driDefectiveHearing                  tinyint(1) comment 'Does driver have defective hearing', 
  driDefectiveVision                   tinyint(1) comment 'Does driver have defective vision', 
  driMale                              tinyint(1) comment 'Is driver male', 
  driLastCriminalOffence               date comment 'Date of last criminal offence', 
  driLastDrivingOffence                date comment 'Date of last driving offence', 
  driLastMotorRelatedAccident          date comment 'Date of last motor related accident', 
  driLastMotorRelatedClaim             date comment 'Date of last motor related claim', 
  driMotorInsuranceCancelledByInsurer  tinyint(1) comment 'Previously has motor insurance been cancelled by insurer', 
  driMotorInsuranceConditionsAdded     tinyint(1) comment 'Previously have condition been added by insurer to the motor insurance', 
  driMotorInsuranceRefusedOnRenewal    tinyint(1) comment 'Previously has motor insurance been refused on renewal by insurer', 
  driNumberOfCriminalOffences          int(2) comment 'Number of criminal offences', 
  driNumberOfDrivingOffenceConvictions int(2) comment 'Number of driving offence convictions', 
  driNumberOfMotorRelatedAccidents     int(2) comment 'Number of motor related accidents', 
  driNumberOfMotorRelatedClaims        int(2) comment 'Number of motor related claims', 
  driPreviouslyDeclineMotorInsurance   tinyint(1) comment 'Has been previously declined motor insurance', 
  driPreviousNCD                       tinyint(1) comment 'Previous no claims discount', 
  driRequiredIncreasePremium           tinyint comment 'Has there been the need to increase the premium', 
  driSufferedFits                      tinyint(1) comment 'Does the driver suffer from fits', 
  driCurrentLicenceHeld                tinyint(1) comment 'Does driver hold a current driving licence', 
  driLicencedIssuedIDcou               varchar(64) comment 'Country licence issued', 
  driNatioinalityIDnat                 varchar(32) comment 'Driver nationality', 
  driOccuptationIDocc                  varchar(32) comment 'Driver occupatioin', 
  driEmploymentStatusIDemp             varchar(32) comment 'Drivers employment status') comment='Driver Table' CHARACTER SET UTF8;
CREATE TABLE accAccident (
  accID                             varchar(37) UNIQUE, 
  accCarryingGoods                  tinyint(1) comment 'Were good being carried at time of accident', 
  accDamageToThirdParty             tinyint(1) comment 'Was there damage to a third party', 
  accDriverAirbagDeployed           tinyint(1) comment 'Did the driver airbags deploy', 
  accDriverLiable                   tinyint(1) comment 'Is the driver liable for the accident', 
  accInjuryToDriver                 tinyint(1) comment 'Was there injury to the driver', 
  accInjuryOtherVehicleDrivers      tinyint(1) comment 'Was there injury sustained by any other vehicles driver', 
  accInjuryToOtherVehiclePassengers tinyint(1) comment 'Was there any injury sustained by any of the other vehicles passengers', 
  accInjuryToPassengers             tinyint(1) comment 'Was there any injury sustained by the other vehicles passengers', 
  accInjuryToThirdParties           tinyint(1) comment 'Was there any injury sustained by any of the other third parties', 
  accInsuredDriving                 tinyint(1) comment 'Was a named insured from the policy driving', 
  accLightsOn                       tinyint(1) comment 'Were the lights on after light up time', 
  accNumberOfVehiclesInvolved       int(3) comment 'How many vehicles were involved in the accident', 
  accPassengerAirbagDeployed        tinyint(1) comment 'Did passenger airbag(s) deploy', 
  accPoliceProsecutingDriver        tinyint(1) comment 'Are the police prosecuting driver due to this accident', 
  accPoliceRecorded                 tinyint(1) comment 'Was this accident recorded by the police', 
  accPoliceWitness                  tinyint(1) comment 'Did the police witness the accident', 
  accSeatBeltsUsed                  tinyint(1) comment 'Were the seatbelts in use at the time of the accident.', 
  accDriverIDdri                    varchar(37), 
  accClaimIDclm                     varchar(37)) comment='Accident details' CHARACTER SET UTF8;
CREATE TABLE theTheftFire (
  theID                        varchar(37) UNIQUE, 
  theAlarmOn                   tinyint(1) comment 'Was an alarm on at time of theft', 
  theAllWindowsAndDoorsSecured tinyint(1) comment 'Were all windows and doors in the vehicle secured', 
  theAnyToolsInVehicle         tinyint(1) comment 'Were any tools or equipment in vehicle at time of theft', 
  theKeysInVehicle             tinyint(1) comment 'Were ignition keys in vehicle', 
  theKeysStolenWithVehicle     tinyint(1) comment 'Were ignition keys stolen with vehicle', 
  theLockedInGarage            tinyint(1) comment 'Was vehicle in a locked garaged prior to theft', 
  theClaimIDclm                varchar(37)) comment='Third party fire and theft information' CHARACTER SET UTF8;
CREATE TABLE addAddress (
  addID       varchar(37) UNIQUE, 
  addRoad     varchar(100), 
  addAreaCode varchar(50) comment 'Either Postal code or area') comment='Address information' CHARACTER SET UTF8;
CREATE TABLE claENClaimType (
  name      varchar(32) NOT NULL UNIQUE, 
  validFrom date, 
  validTo   date) comment='Claim Type' CHARACTER SET UTF8;
CREATE TABLE clmClaim (
  clmID                     varchar(37) UNIQUE, 
  clmClaimTypeIDcla         varchar(32), 
  clmAmountPaidAmount       decimal(12, 2) comment 'amount paid in claim to insured', 
  clmAmountPaidIDccy        varchar(3), 
  clmLegalFeesAmount        decimal(12, 2) comment 'legal fees incurred by claim', 
  clmLegalFeesIDccy         varchar(3), 
  clmRecoveriesAmount       decimal(12, 2) comment 'total recoveries made against claim', 
  clmRecoveriesIDccy        varchar(3), 
  clmTotalLossIncuredAmount decimal(12, 2) comment 'total loss of claim = paid + legal -recoveries', 
  clmTotalLossIncurredIDccy varchar(3), 
  clmIncidentMileageAtTime  int(10) comment 'main vehicle''s mileage at the time of the incident', 
  clmIncidentOccured        date comment 'when incident occurred', 
  clmPolicyIDpol            varchar(37), 
  clmIncidentAddressIDadd   varchar(37), 
  clmVehicleIDveh           varchar(37)) comment='Claim Details' CHARACTER SET UTF8;
CREATE TABLE weiENWeightType (
  name      varchar(3) NOT NULL UNIQUE, 
  validFrom date, 
  validTo   date) comment='Unit of Weight' CHARACTER SET UTF8;
CREATE TABLE risENRiskCode (
  name      varchar(32) NOT NULL UNIQUE, 
  validFrom date, 
  validTo   date) comment='Vehicle Risk Code e.g. X.1 , X.4, TAXI-Z405, HIRING CARS Z405, MINIBUS,MAXIBUS, Y3 etc.' CHARACTER SET UTF8;
CREATE TABLE mdfENModifications (
  name      varchar(32) NOT NULL UNIQUE, 
  validFrom date, 
  validTo   date) comment='Vehicle Modifications Type' CHARACTER SET UTF8;
CREATE TABLE accENAccessories (
  name      varchar(32) NOT NULL UNIQUE, 
  validFrom date, 
  validTo   date) comment='Vehicle Accessories Type' CHARACTER SET UTF8;
CREATE TABLE parENParked (
  name      varchar(32) NOT NULL UNIQUE, 
  validFrom date, 
  validTo   date) comment='Where Vehicle Parked Type' CHARACTER SET UTF8;
CREATE TABLE souENSoundSystem (
  name      varchar(32) NOT NULL UNIQUE, 
  validFrom date, 
  validTo   date) comment='Vehicle Sound System Type' CHARACTER SET UTF8;
CREATE TABLE ccyENCurrency (
  name        varchar(3) NOT NULL UNIQUE, 
  description int(11), 
  symbol      char(3), 
  validFrom   date, 
  validTo     date) comment='Currency Type' CHARACTER SET UTF8;
CREATE TABLE reaENReason (
  name      varchar(32) NOT NULL UNIQUE, 
  validFrom date, 
  validTo   date) comment='Reason for uploading Policy Data' CHARACTER SET UTF8;
CREATE TABLE insENInsuredType (
  name      varchar(32) NOT NULL UNIQUE, 
  validFrom date, 
  validTo   date) comment='Insured Type' CHARACTER SET UTF8;
CREATE TABLE modENModel (
  name  varchar(32) NOT NULL, 
  modID int(11) NOT NULL UNIQUE) comment='Vehicle Model Type' CHARACTER SET UTF8;
CREATE TABLE cascadeMakeModel (
  makID     int(11) NOT NULL, 
  modlID    int(11) NOT NULL, 
  validFrom date, 
  validTo   date) comment='Relationship between Vehicle Make and Model' CHARACTER SET UTF8;
CREATE TABLE makENMake (
  name  varchar(32) NOT NULL UNIQUE, 
  makID int(11) NOT NULL UNIQUE) comment='Vehicle Make Type' CHARACTER SET UTF8;
CREATE TABLE bodENBodyType (
  name      varchar(32) NOT NULL UNIQUE, 
  validFrom date, 
  validTo   date) comment='Vehicle Body Type' CHARACTER SET UTF8;
CREATE TABLE vehVehicle (
  vehID                           varchar(37) UNIQUE comment 'Unique Identifier', 
  vehMakeIDmak                    varchar(32), 
  vehModelIDmod                   int(11), 
  vehBodyIDbod                    varchar(32), 
  vehSoundSystemIDsou             varchar(32), 
  vehParkedIDpar                  varchar(32), 
  vehAccidentIDacc                varchar(32), 
  vehModifiedIDmdf                varchar(32), 
  vehRiskCodeIDrsk                varchar(32), 
  vehAlarmFitted                  tinyint(1) comment 'Does vehicle have a car alarm fitted', 
  vehBoughtFromNew                tinyint(1) comment 'Was vehicle bought from new', 
  vehYearOfManufacture            int(4) comment 'Year of vehicle''s manufacture', 
  vehCapacitiyVolume              decimal(10, 3) comment 'Vehicle''s carrying capacity by volume', 
  vehCommercialVehicle            tinyint(1) comment 'Is this a commercial vehicle', 
  vehCompanyVehicle               tinyint(1) comment 'Vehicle is owned by a company', 
  vehCoverStart                   date comment 'When did the cover start', 
  vehCoverEnd                     date comment 'When does cover end', 
  vehDriverAirBagFitted           tinyint(1) comment 'Does the vehicle have a driver''s airbag fitted', 
  vehEngineSizeCC                 int(5) comment 'Vehicle Engine capacity (cc)', 
  vehMilage                       int(7) comment 'Vehicle''s current mileage', 
  vehPassengerAirbagFitted        tinyint(1) comment 'Does the vehicle have a passenger''s airbag fitted', 
  vehRentalVehicle                tinyint(1) comment 'Is vehicle a rental', 
  vehRighthandDrive               tinyint(1) comment 'Is the vehicle right-hand drive', 
  vehRoadWorthyCertificate        tinyint(1) comment 'Does the vehicle hold a current Road worthy certificate', 
  vehRoadWorthyCertificateExpiry  date comment 'Vehicle''s current road worthy certificate expiry date', 
  vehSeatingCapacity              int(3) comment 'vehicle seating capacity', 
  vehSubjectToLoan                tinyint(1) comment 'Is the vehicle currently subject to an outstanding loan agreement', 
  vehToolsStroredDayTime          tinyint(1) comment 'are tools stored in the vehicle during the daytime', 
  vehToolsStroredNightTime        tinyint(1) comment 'are tools stored in the vehicle during the nighttime', 
  vehTrackingSystemFitted         tinyint(1) comment 'is the vehicle fitted with a tracking system', 
  vehTrailer                      tinyint(1) comment 'is the vehicle used for towing a trailer', 
  vehUsageBusiness                tinyint(1) comment 'is the vehicle used for business', 
  vehUsageCarriageOfGoods         tinyint(1) comment 'is the vehicle used for the carriage of goods', 
  vehUsageSocialHome              tinyint(1) comment 'is the vehicle used for home or socially', 
  vehWhenIsuredTookPossession     date comment 'what date did the insure take possession of the vehicle', 
  vehSumInsuredLocalAmount        decimal(15, 2) comment 'vehicle sum insured amount in the local currency', 
  vehSumInsuredLocalIDccy         varchar(3), 
  vehSumInsuredForeignAmount      decimal(15, 2) comment 'vehicle sum insured amount in the foreign currency', 
  vehSumInsuredForeignIDccy       varchar(3), 
  vehRiskCurrencyAmount           decimal(15, 2) comment 'vehicle risk amount', 
  vehRiskCurrencyIDccy            varchar(3), 
  vehPremiumForeignAmount         decimal(15, 2) comment 'vehicle foreign premium amount in foreign currency', 
  vehPremiumForeignIDccy          varchar(3), 
  vehCurrentEstimateedValueAmount decimal(15, 2) comment 'vehicle''s current estimated value', 
  vehCurrentEstimatedValueIDccy   varchar(3), 
  vehPremiumLocalAmount           decimal(15, 2) comment 'vehicle premium in local currency', 
  vehPremiumLocalIDccy            varchar(3), 
  vehValueWhenNewAmount           decimal(15, 2) comment 'value of vehicle when new', 
  vehValueWhenNewIDccy            varchar(3), 
  vehUnladedWeightAmount          decimal(10, 2) comment 'vehicle''s unladed weight', 
  vehUnladedWeightIDwei           varchar(3), 
  vehMaxLoadWeightAmount          decimal(10, 2) comment 'vehicle''s maximum loading amount', 
  vehMaxLoadWeightIDwei           varchar(3), 
  vehPolicyIDpol                  varchar(37), 
  vehDayTimeLocationIDadd         varchar(37), 
  vehNightTimeLocationIDadd       varchar(37)) comment='Vehicle Details' CHARACTER SET UTF8;
CREATE TABLE covENCover (
  name      varchar(32) NOT NULL UNIQUE, 
  validFrom date, 
  validTo   date) comment='Cover type' CHARACTER SET UTF8;
CREATE TABLE polPolicy (
  polID                 varchar(37) UNIQUE, 
  polCoverTypeIDcov     varchar(32) comment 'Policy cover type e.g Comprehensive', 
  polInsuredTypeIDins   varchar(32) comment 'Policy Insured Type e.g. Company, Private', 
  polReasonIDrea        varchar(32) comment 'Business Source e.g.�Direct, Marketing, Broker, Agent, Reinsurance, Banc assurance) ', 
  polReplaced           date comment 'date policy record was replace on system, if null then this is the first instance', 
  polExternalSystemID   varchar(100) comment 'To be populated by the company uploading the data with a value that helps them relate this record back to their internal systems. This should not hold a value that would allow others (outside of the company supplying the record) to identify the policy. Specifically, this should not be populated with a policy number. This column is not expected to be unique in the staging or master databases. It is expected that records relating to the same policy will have the same polClientID.', 
  polSystemID           varchar(100) comment 'Generated by ARDR when records are added to the master database. This is generated using the database''s UUID function.', 
  polInception          date comment 'policy inception date', 
  polExpiry             date comment 'policy expiration date', 
  polLeadInsurer        tinyint(1) comment 'is this the lead insurer', 
  polCoInsuranceCover   decimal(9, 4) DEFAULT 0 comment 'percentage co - insured', 
  polFacultativeCover   decimal(9, 4) DEFAULT 0 comment 'percentage of facultative cover', 
  polQuotaShare         decimal(9, 4) DEFAULT 0 comment 'percentage of quote share', 
  polTax                decimal(9, 4) DEFAULT 0 comment 'percentage of Tax', 
  polUWYear             int(4) comment 'underwriting year or year of account', 
  polGrossPremiumAmount decimal(9, 2) DEFAULT 0 comment 'gross premium amount', 
  polGrossPremiumIDccy  varchar(3), 
  driPolicyIDpol        varchar(37)) comment='Main Policy Document' CHARACTER SET UTF8;
ALTER TABLE theTheftFire ADD INDEX FKtheTheftFi708736 (theClaimIDclm), ADD CONSTRAINT FKtheTheftFi708736 FOREIGN KEY (theClaimIDclm) REFERENCES clmClaim (clmID);
ALTER TABLE accAccident ADD INDEX FKaccAcciden675312 (accClaimIDclm), ADD CONSTRAINT FKaccAcciden675312 FOREIGN KEY (accClaimIDclm) REFERENCES clmClaim (clmID);
ALTER TABLE vehVehicle ADD INDEX FKvehVehicle805580 (vehUnladedWeightIDwei), ADD CONSTRAINT FKvehVehicle805580 FOREIGN KEY (vehUnladedWeightIDwei) REFERENCES weiENWeightType (name);
ALTER TABLE vehVehicle ADD INDEX FKvehVehicle434686 (vehMaxLoadWeightIDwei), ADD CONSTRAINT FKvehVehicle434686 FOREIGN KEY (vehMaxLoadWeightIDwei) REFERENCES weiENWeightType (name);
ALTER TABLE vehVehicle ADD INDEX FKvehVehicle513882 (vehNightTimeLocationIDadd), ADD CONSTRAINT FKvehVehicle513882 FOREIGN KEY (vehNightTimeLocationIDadd) REFERENCES addAddress (addID);
ALTER TABLE vehVehicle ADD INDEX FKvehVehicle483024 (vehDayTimeLocationIDadd), ADD CONSTRAINT FKvehVehicle483024 FOREIGN KEY (vehDayTimeLocationIDadd) REFERENCES addAddress (addID);
ALTER TABLE clmClaim ADD INDEX FKclmClaim44027 (clmPolicyIDpol), ADD CONSTRAINT FKclmClaim44027 FOREIGN KEY (clmPolicyIDpol) REFERENCES polPolicy (polID);
ALTER TABLE clmClaim ADD INDEX FKclmClaim974077 (clmClaimTypeIDcla), ADD CONSTRAINT FKclmClaim974077 FOREIGN KEY (clmClaimTypeIDcla) REFERENCES claENClaimType (name);
ALTER TABLE clmClaim ADD INDEX FKclmClaim47506 (clmAmountPaidIDccy), ADD CONSTRAINT FKclmClaim47506 FOREIGN KEY (clmAmountPaidIDccy) REFERENCES ccyENCurrency (name);
ALTER TABLE clmClaim ADD INDEX FKclmClaim200310 (clmLegalFeesIDccy), ADD CONSTRAINT FKclmClaim200310 FOREIGN KEY (clmLegalFeesIDccy) REFERENCES ccyENCurrency (name);
ALTER TABLE clmClaim ADD INDEX FKclmClaim541304 (clmRecoveriesIDccy), ADD CONSTRAINT FKclmClaim541304 FOREIGN KEY (clmRecoveriesIDccy) REFERENCES ccyENCurrency (name);
ALTER TABLE clmClaim ADD INDEX FKclmClaim659378 (clmTotalLossIncurredIDccy), ADD CONSTRAINT FKclmClaim659378 FOREIGN KEY (clmTotalLossIncurredIDccy) REFERENCES ccyENCurrency (name);
ALTER TABLE clmClaim ADD INDEX FKclmClaim289057 (clmIncidentAddressIDadd), ADD CONSTRAINT FKclmClaim289057 FOREIGN KEY (clmIncidentAddressIDadd) REFERENCES addAddress (addID);
ALTER TABLE polPolicy ADD INDEX FKpolPolicy732934 (driPolicyIDpol), ADD CONSTRAINT FKpolPolicy732934 FOREIGN KEY (driPolicyIDpol) REFERENCES driDriver (driID);
ALTER TABLE driDriver ADD INDEX FKdriDriver500451 (driLicencedIssuedIDcou), ADD CONSTRAINT FKdriDriver500451 FOREIGN KEY (driLicencedIssuedIDcou) REFERENCES couENCountry (country);
ALTER TABLE accAccident ADD INDEX FKaccAcciden528634 (accDriverIDdri), ADD CONSTRAINT FKaccAcciden528634 FOREIGN KEY (accDriverIDdri) REFERENCES driDriver (driID);
ALTER TABLE driDriver ADD INDEX FKdriDriver381963 (driEmploymentStatusIDemp), ADD CONSTRAINT FKdriDriver381963 FOREIGN KEY (driEmploymentStatusIDemp) REFERENCES empENEmploymentType (name);
ALTER TABLE driDriver ADD INDEX FKdriDriver213188 (driNatioinalityIDnat), ADD CONSTRAINT FKdriDriver213188 FOREIGN KEY (driNatioinalityIDnat) REFERENCES natENNationality (name);
ALTER TABLE driDriver ADD INDEX FKdriDriver657757 (driOccuptationIDocc), ADD CONSTRAINT FKdriDriver657757 FOREIGN KEY (driOccuptationIDocc) REFERENCES occENOccupation (name);
ALTER TABLE vehVehicle ADD INDEX FKvehVehicle154161 (vehSoundSystemIDsou), ADD CONSTRAINT FKvehVehicle154161 FOREIGN KEY (vehSoundSystemIDsou) REFERENCES souENSoundSystem (name);
ALTER TABLE vehVehicle ADD INDEX FKvehVehicle585004 (vehParkedIDpar), ADD CONSTRAINT FKvehVehicle585004 FOREIGN KEY (vehParkedIDpar) REFERENCES parENParked (name);
ALTER TABLE vehVehicle ADD INDEX FKvehVehicle657851 (vehAccidentIDacc), ADD CONSTRAINT FKvehVehicle657851 FOREIGN KEY (vehAccidentIDacc) REFERENCES accENAccessories (name);
ALTER TABLE vehVehicle ADD INDEX FKvehVehicle546082 (vehModifiedIDmdf), ADD CONSTRAINT FKvehVehicle546082 FOREIGN KEY (vehModifiedIDmdf) REFERENCES mdfENModifications (name);
ALTER TABLE vehVehicle ADD INDEX FKvehVehicle706658 (vehRiskCodeIDrsk), ADD CONSTRAINT FKvehVehicle706658 FOREIGN KEY (vehRiskCodeIDrsk) REFERENCES risENRiskCode (name);
ALTER TABLE vehVehicle ADD INDEX FKvehVehicle822388 (vehSumInsuredLocalIDccy), ADD CONSTRAINT FKvehVehicle822388 FOREIGN KEY (vehSumInsuredLocalIDccy) REFERENCES ccyENCurrency (name);
ALTER TABLE vehVehicle ADD INDEX FKvehVehicle964444 (vehSumInsuredForeignIDccy), ADD CONSTRAINT FKvehVehicle964444 FOREIGN KEY (vehSumInsuredForeignIDccy) REFERENCES ccyENCurrency (name);
ALTER TABLE vehVehicle ADD INDEX FKvehVehicle358880 (vehRiskCurrencyIDccy), ADD CONSTRAINT FKvehVehicle358880 FOREIGN KEY (vehRiskCurrencyIDccy) REFERENCES ccyENCurrency (name);
ALTER TABLE vehVehicle ADD INDEX FKvehVehicle193849 (vehPremiumForeignIDccy), ADD CONSTRAINT FKvehVehicle193849 FOREIGN KEY (vehPremiumForeignIDccy) REFERENCES ccyENCurrency (name);
ALTER TABLE vehVehicle ADD INDEX FKvehVehicle977480 (vehCurrentEstimatedValueIDccy), ADD CONSTRAINT FKvehVehicle977480 FOREIGN KEY (vehCurrentEstimatedValueIDccy) REFERENCES ccyENCurrency (name);
ALTER TABLE vehVehicle ADD INDEX FKvehVehicle192992 (vehPremiumLocalIDccy), ADD CONSTRAINT FKvehVehicle192992 FOREIGN KEY (vehPremiumLocalIDccy) REFERENCES ccyENCurrency (name);
ALTER TABLE vehVehicle ADD INDEX FKvehVehicle86368 (vehValueWhenNewIDccy), ADD CONSTRAINT FKvehVehicle86368 FOREIGN KEY (vehValueWhenNewIDccy) REFERENCES ccyENCurrency (name);
ALTER TABLE polPolicy ADD INDEX FKpolPolicy482946 (polInsuredTypeIDins), ADD CONSTRAINT FKpolPolicy482946 FOREIGN KEY (polInsuredTypeIDins) REFERENCES insENInsuredType (name);
ALTER TABLE polPolicy ADD INDEX FKpolPolicy389109 (polReasonIDrea), ADD CONSTRAINT FKpolPolicy389109 FOREIGN KEY (polReasonIDrea) REFERENCES reaENReason (name);
ALTER TABLE polPolicy ADD INDEX FKpolPolicy254915 (polGrossPremiumIDccy), ADD CONSTRAINT FKpolPolicy254915 FOREIGN KEY (polGrossPremiumIDccy) REFERENCES ccyENCurrency (name);
ALTER TABLE vehVehicle ADD INDEX FKvehVehicle338565 (vehPolicyIDpol), ADD CONSTRAINT FKvehVehicle338565 FOREIGN KEY (vehPolicyIDpol) REFERENCES polPolicy (polID);
ALTER TABLE polPolicy ADD INDEX FKpolPolicy72147 (polCoverTypeIDcov), ADD CONSTRAINT FKpolPolicy72147 FOREIGN KEY (polCoverTypeIDcov) REFERENCES covENCover (name);
ALTER TABLE vehVehicle ADD INDEX FKvehVehicle139866 (vehBodyIDbod), ADD CONSTRAINT FKvehVehicle139866 FOREIGN KEY (vehBodyIDbod) REFERENCES bodENBodyType (name);
ALTER TABLE vehVehicle ADD INDEX FKvehVehicle432499 (vehModelIDmod), ADD CONSTRAINT FKvehVehicle432499 FOREIGN KEY (vehModelIDmod) REFERENCES modENModel (modID);
ALTER TABLE vehVehicle ADD INDEX FKvehVehicle442164 (vehMakeIDmak), ADD CONSTRAINT FKvehVehicle442164 FOREIGN KEY (vehMakeIDmak) REFERENCES makENMake (name);
ALTER TABLE cascadeMakeModel ADD INDEX FKcascadeMak336466 (makID), ADD CONSTRAINT FKcascadeMak336466 FOREIGN KEY (makID) REFERENCES makENMake (makID);
ALTER TABLE cascadeMakeModel ADD INDEX FKcascadeMak936409 (modlID), ADD CONSTRAINT FKcascadeMak936409 FOREIGN KEY (modlID) REFERENCES modENModel (modID);
ALTER TABLE clmClaim ADD INDEX FKclmClaim513054 (clmVehicleIDveh), ADD CONSTRAINT FKclmClaim513054 FOREIGN KEY (clmVehicleIDveh) REFERENCES vehVehicle (vehID);

