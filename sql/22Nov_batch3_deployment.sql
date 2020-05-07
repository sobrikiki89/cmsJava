INSERT INTO claim_status (code, name, sort_order, active_flag) VALUES ('OPN', 'Open', 0, true);
INSERT INTO claim_status (code, name, sort_order, active_flag) VALUES ('PDOC', 'PendingDocument', 1, true);
INSERT INTO claim_status (code, name, sort_order, active_flag) VALUES ('PADJ', 'PendingAdjuster', 2, true);
INSERT INTO claim_status (code, name, sort_order, active_flag) VALUES ('PACC', 'PendingAcceptance', 3, true);
INSERT INTO claim_status (code, name, sort_order, active_flag) VALUES ('POFR', 'PendingOffer', 4, true);
INSERT INTO claim_status (code, name, sort_order, active_flag) VALUES ('PPYMT', 'PendingPayment', 5, true);
INSERT INTO claim_status (code, name, sort_order, active_flag) VALUES ('CPAID', 'Closed-Paid', 6, true);
INSERT INTO claim_status (code, name, sort_order, active_flag) VALUES ('CUEX', 'Closed-WithinPolicyExcess', 7, true);
INSERT INTO claim_status (code, name, sort_order, active_flag) VALUES ('CDCL', 'Closed-Declined', 8, true);
INSERT INTO claim_status (code, name, sort_order, active_flag) VALUES ('CWDTH', 'Closed-Withdrawn', 9, true);

UPDATE INSURANCE_CLASS_CATEGORY SET SORT_ORDER = '1' WHERE CODE = 'PRT' AND NAME = 'Property';
UPDATE INSURANCE_CLASS_CATEGORY SET SORT_ORDER = '2' WHERE CODE = 'MISC' AND NAME = 'Miscellaneous';
UPDATE INSURANCE_CLASS_CATEGORY SET SORT_ORDER = '3' WHERE CODE = 'LBT' AND NAME = 'Liability';
UPDATE INSURANCE_CLASS_CATEGORY SET SORT_ORDER = '4' WHERE CODE = 'AVM' AND NAME = 'Aviation / Marine';
UPDATE INSURANCE_CLASS_CATEGORY SET SORT_ORDER = '5' WHERE CODE = 'MTR' AND NAME = 'Motor';