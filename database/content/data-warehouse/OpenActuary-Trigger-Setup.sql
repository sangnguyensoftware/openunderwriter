CREATE TRIGGER pol_ins_bfr_uuid BEFORE INSERT ON polPolicy FOR EACH ROW set NEW.polID=UUID();
CREATE TRIGGER clm_ins_bfr_uuid BEFORE INSERT ON clmClaim FOR EACH ROW set NEW.clmID=UUID();
CREATE TRIGGER acc_ins_bfr_uuid BEFORE INSERT ON accAccident FOR EACH ROW set NEW.accID=UUID();
CREATE TRIGGER the_ins_bfr_uuid BEFORE INSERT ON theTheftFire FOR EACH ROW set NEW.theID=UUID();
CREATE TRIGGER veh_ins_bfr_uuid BEFORE INSERT ON vehVehicle FOR EACH ROW set NEW.vehID=UUID();
CREATE TRIGGER dri_ins_bfr_uuid BEFORE INSERT ON driDriver FOR EACH ROW set NEW.driID=UUID();
CREATE TRIGGER add_ins_bfr_uuid BEFORE INSERT ON addAddress FOR EACH ROW set NEW.addID=UUID();
