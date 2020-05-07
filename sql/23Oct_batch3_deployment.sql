ALTER TABLE policy RENAME COLUMN total_gross_premium TO premium_gross;
ALTER TABLE policy RENAME COLUMN total_sum_insured TO sum_insured;
UPDATE policy SET premium_net = (SELECT SUM(COALESCE(p.premium_gross,0) - COALESCE(p.premium_rebate,0) + COALESCE(premium_tax,0)) FROM policy p WHERE p.id = policy.id);