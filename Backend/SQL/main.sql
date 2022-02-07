CREATE DATABASE AIR_db;
CREATE TABLE IF NOT EXISTS as_tb(asn int, country VARCHAR(255), cone_size int, org_id int);
SELECT * FROM as_tb;
CREATE TABLE IF NOT EXISTS ixp_tb(id int PRIMARY KEY, country VARCHAR(255),
city VARCHAR(255), org_id int);
SELECT * FROM ixp_tb;
CREATE TABLE IF NOT EXISTS as_rel_tb(id int PRIMARY KEY, asn_start int, asn_end int, rel_type VARCHAR(255));
SELECT * FROM as_rel_tb;
CREATE TABLE IF NOT EXISTS peering_tb(id int PRIMARY KEY, asn int, ix_id int);
SELECT * FROM peering_tb;
ALTER TABLE as_tb ADD lon float;
ALTER TABLE as_tb ADD lat float;
ALTER TABLE ixp_tb ADD lon float;
ALTER TABLE ixp_tb ADD lat float;
ALTER TABLE as_tb ADD PRIMARY KEY (asn);
ALTER TABLE as_tb ADD tier int;
ALTER TABLE as_rel_tb ADD tier int;
ALTER TABLE peering_tb ADD tier int;
ALTER TABLE as_tb ADD is_peered int DEFAULT 0;



