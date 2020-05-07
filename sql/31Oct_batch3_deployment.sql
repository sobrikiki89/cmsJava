INSERT INTO insurance_class (code, name, sort_order, active_flag, category) VALUES ('HOHH', 'HOUSEOWNER & HOUSEHOLDER', 21, true, null);
update policy set insurance_class_code = 'HOHH' where insurance_class_code = 'HO/HH';
delete from insurance_class where code = 'HO/HH';

INSERT INTO insurance_class (code, name, sort_order, active_flag, category) VALUES ('AVHU', 'AVIATION HULL, LIABILITY & PA', 14, true, 'AVM');
update policy set insurance_class_code = 'AVHU' where insurance_class_code = 'AV HU';
delete from insurance_class where code = 'AV HU';

INSERT INTO insurance_class (code, name, sort_order, active_flag, category) VALUES ('AVLO', 'AVIATION LOSS OF LICENSE', 15, true, 'AVM');
update policy set insurance_class_code = 'AVLO' where insurance_class_code = 'AV LO';
delete from insurance_class where code = 'AV LO';

INSERT INTO insurance_class (code, name, sort_order, active_flag, category) VALUES ('AVPA', 'AVIATION PERSONAL ACCIDENT', 13, true, 'AVM');
update policy set insurance_class_code = 'AVPA' where insurance_class_code = 'AV PA';
delete from insurance_class where code = 'AV PA';

INSERT INTO claim_status ( code, name, sort_order, active_flag ) VALUES ( 'OPN','Open',0, true);
INSERT INTO claim_status ( code, name, sort_order, active_flag ) VALUES ( 'PDOC','PendingDocument',1, true);
INSERT INTO claim_status ( code, name, sort_order, active_flag ) VALUES ( 'PADJ','PendingAdjuster',2, true);
INSERT INTO claim_status ( code, name, sort_order, active_flag ) VALUES ( 'PACC','PendingAcceptance',3, true);
INSERT INTO claim_status ( code, name, sort_order, active_flag ) VALUES ( 'POFR','PendingOffer',4, true);
INSERT INTO claim_status ( code, name, sort_order, active_flag ) VALUES ( 'PPYMT','PendingPayment',5, true);
INSERT INTO claim_status ( code, name, sort_order, active_flag ) VALUES ( 'CPAID','Closed-Paid',6, true);
INSERT INTO claim_status ( code, name, sort_order, active_flag ) VALUES ( 'CUEX','Closed-WithinPolicyExcess',7, true);
INSERT INTO claim_status ( code, name, sort_order, active_flag ) VALUES ( 'CDCL','Closed-Declined',8, true);
INSERT INTO claim_status ( code, name, sort_order, active_flag ) VALUES ( 'CWDTH','Closed-Withdrawn',9, true);
