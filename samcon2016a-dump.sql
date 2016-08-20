SET DB_CLOSE_DELAY -1;         
;              
CREATE USER IF NOT EXISTS SA SALT 'af183b8c0bcddd94' HASH '94c85d6d085f6523d61e93d0db6181f2d892fc5bcb1ef44e30e4039db1875ca7' ADMIN;            
CREATE CACHED TABLE PUBLIC."lobos_migrations"(
    "name" VARCHAR(255)
);      
-- 12 +/- SELECT COUNT(*) FROM PUBLIC."lobos_migrations";      
INSERT INTO PUBLIC."lobos_migrations"("name") VALUES
('add-conventions-table'),
('add-slots-table'),
('add-persons-table'),
('add-events-table'),
('add-schedule-table'),
('add-schedule-issues-table'),
('add-preferred-slot-to-event'),
('add-event-count-to-event'),
('add-event-day-to-schedule'),
('person-non-availability'),
('add-event-type-to-event'),
('add-preferred-day-to-event');               
CREATE CACHED TABLE PUBLIC."conventions"(
    "id" UUID NOT NULL,
    "name" VARCHAR(100),
    "from" DATE,
    "to" DATE
);   
ALTER TABLE PUBLIC."conventions" ADD CONSTRAINT PUBLIC."conventions_primary_key_id" PRIMARY KEY("id");         
-- 2 +/- SELECT COUNT(*) FROM PUBLIC."conventions";            
INSERT INTO PUBLIC."conventions"("id", "name", "from", "to") VALUES
('0bca24f0-04ce-11e6-89be-b8881352c07d', 'Samcon 2015a', DATE '2015-07-07', DATE '2015-07-12'),
('edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'SamCon 2016a', DATE '2016-08-20', DATE '2016-08-25');            
CREATE CACHED TABLE PUBLIC."slots"(
    "id" UUID NOT NULL,
    "start-minutes" INTEGER,
    "end-minutes" INTEGER,
    "convention_id" UUID
);
ALTER TABLE PUBLIC."slots" ADD CONSTRAINT PUBLIC."slots_primary_key_id" PRIMARY KEY("id");     
-- 6 +/- SELECT COUNT(*) FROM PUBLIC."slots";  
INSERT INTO PUBLIC."slots"("id", "start-minutes", "end-minutes", "convention_id") VALUES
('13c37710-04ce-11e6-89be-b8881352c07d', 600, 840, '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('19938090-04ce-11e6-89be-b8881352c07d', 840, 1080, '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('29578380-08db-11e6-ade4-87c95010ccef', 1080, 1320, '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('65fb1ca0-663e-11e6-85c2-54cf7f9c33c5', 600, 840, 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('6aa20f70-663e-11e6-85c2-54cf7f9c33c5', 840, 1080, 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('6e72c680-663e-11e6-85c2-54cf7f9c33c5', 1080, 1320, 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5');         
CREATE CACHED TABLE PUBLIC."persons"(
    "id" UUID NOT NULL,
    "name" VARCHAR(100),
    "convention_id" UUID
);             
ALTER TABLE PUBLIC."persons" ADD CONSTRAINT PUBLIC."persons_primary_key_id" PRIMARY KEY("id"); 
-- 47 +/- SELECT COUNT(*) FROM PUBLIC."persons";               
INSERT INTO PUBLIC."persons"("id", "name", "convention_id") VALUES
('330860e0-04ce-11e6-89be-b8881352c07d', 'Luke', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('33fcd300-04ce-11e6-89be-b8881352c07d', 'Sarah', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('a738f240-04ce-11e6-89be-b8881352c07d', 'Sean', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('a7ec15a0-04ce-11e6-89be-b8881352c07d', 'Nick', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('e2064b60-04d4-11e6-86bc-b8881352c07d', 'Darren', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('e303be30-04d4-11e6-86bc-b8881352c07d', 'Katy', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('e61fcb40-04d4-11e6-86bc-b8881352c07d', 'John', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('e7f54e90-04d4-11e6-86bc-b8881352c07d', 'Izzy', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('ec030ea0-04d4-11e6-86bc-b8881352c07d', 'Andy Bell', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('47e64fc0-04d5-11e6-86bc-b8881352c07d', 'Jim', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('4d64b6d0-04d5-11e6-86bc-b8881352c07d', 'Dan', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('4e4a0dc0-04d5-11e6-86bc-b8881352c07d', 'Jess', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('508b4b30-04d5-11e6-86bc-b8881352c07d', 'Clare', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('7bbce160-04d5-11e6-86bc-b8881352c07d', 'Liz', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('a82b1640-04d5-11e6-86bc-b8881352c07d', 'Micky', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('ac168140-04d5-11e6-86bc-b8881352c07d', 'Sam', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('3ac01c80-04d6-11e6-86bc-b8881352c07d', 'Andrew Brown', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('2f4e3f10-04ce-11e6-89be-b8881352c07d', 'Tom Parker', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('85bfe630-04d5-11e6-86bc-b8881352c07d', 'James Laverack', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('31215160-04ce-11e6-89be-b8881352c07d', 'David Dorward', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('f2c7aff0-663d-11e6-85c2-54cf7f9c33c5', 'Mickey', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f3a7fdd0-663d-11e6-85c2-54cf7f9c33c5', 'Tom', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f48872c0-663d-11e6-85c2-54cf7f9c33c5', 'Izzy', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f5da6e30-663d-11e6-85c2-54cf7f9c33c5', 'Mike', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f69de540-663d-11e6-85c2-54cf7f9c33c5', 'Luke', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fad74930-663d-11e6-85c2-54cf7f9c33c5', 'Sarah', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fd45d830-663d-11e6-85c2-54cf7f9c33c5', 'Sean', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fe3d7ea0-663d-11e6-85c2-54cf7f9c33c5', 'Darren', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('01646120-663e-11e6-85c2-54cf7f9c33c5', 'Elspeth', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('042c0ca0-663e-11e6-85c2-54cf7f9c33c5', 'Clare', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('147bc500-663e-11e6-85c2-54cf7f9c33c5', 'Andy', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('166ebb60-663e-11e6-85c2-54cf7f9c33c5', 'Jim', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('175b8c60-663e-11e6-85c2-54cf7f9c33c5', 'Vikki', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('1a7fafc0-663e-11e6-85c2-54cf7f9c33c5', 'David', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('1e25d460-663e-11e6-85c2-54cf7f9c33c5', 'George', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('22f536c0-663e-11e6-85c2-54cf7f9c33c5', 'Sam', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('5dea71f0-663e-11e6-85c2-54cf7f9c33c5', 'Nick', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fa9c4690-663e-11e6-85c2-54cf7f9c33c5', 'Bruno', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('061ee680-663f-11e6-85c2-54cf7f9c33c5', 'Liz', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('38df7440-663f-11e6-85c2-54cf7f9c33c5', 'Jon S', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('3cae7da0-663f-11e6-85c2-54cf7f9c33c5', 'Ezzy', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('423cc330-663f-11e6-85c2-54cf7f9c33c5', 'James L', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('464b1f80-663f-11e6-85c2-54cf7f9c33c5', 'Tash', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('4c0d1f40-663f-11e6-85c2-54cf7f9c33c5', 'Jon B', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('6a09aa90-663f-11e6-85c2-54cf7f9c33c5', 'Ross', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5');     
INSERT INTO PUBLIC."persons"("id", "name", "convention_id") VALUES
('a2b89f40-663f-11e6-85c2-54cf7f9c33c5', 'Dan', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a4379e20-663f-11e6-85c2-54cf7f9c33c5', 'Richard', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5');       
CREATE CACHED TABLE PUBLIC."schedule"(
    "id" UUID NOT NULL SELECTIVITY 100,
    "date" DATE SELECTIVITY 42,
    "convention_id" UUID SELECTIVITY 7,
    "slot_id" UUID SELECTIVITY 21,
    "event_id" UUID SELECTIVITY 64,
    "event_day" INTEGER DEFAULT 1 SELECTIVITY 17
);              
ALTER TABLE PUBLIC."schedule" ADD CONSTRAINT PUBLIC."schedule_primary_key_id" PRIMARY KEY("id");               
-- 41 +/- SELECT COUNT(*) FROM PUBLIC."schedule";              
INSERT INTO PUBLIC."schedule"("id", "date", "convention_id", "slot_id", "event_id", "event_day") VALUES
('ffbcb250-6642-11e6-90b2-844f6a1fd87f', DATE '2015-07-08', '0bca24f0-04ce-11e6-89be-b8881352c07d', '19938090-04ce-11e6-89be-b8881352c07d', '2bcf30d0-04d6-11e6-86bc-b8881352c07d', 1),
('ffbcd960-6642-11e6-90b2-844f6a1fd87f', DATE '2015-07-08', '0bca24f0-04ce-11e6-89be-b8881352c07d', '29578380-08db-11e6-ade4-87c95010ccef', '43a2b2a0-04d5-11e6-86bc-b8881352c07d', 1),
('ffbd0070-6642-11e6-90b2-844f6a1fd87f', DATE '2015-07-07', '0bca24f0-04ce-11e6-89be-b8881352c07d', '19938090-04ce-11e6-89be-b8881352c07d', 'e9c86b70-04d5-11e6-86bc-b8881352c07d', 1),
('ffbd0071-6642-11e6-90b2-844f6a1fd87f', DATE '2015-07-07', '0bca24f0-04ce-11e6-89be-b8881352c07d', '29578380-08db-11e6-ade4-87c95010ccef', 'e9c86b70-04d5-11e6-86bc-b8881352c07d', 2),
('ffbd2780-6642-11e6-90b2-844f6a1fd87f', DATE '2015-07-07', '0bca24f0-04ce-11e6-89be-b8881352c07d', '13c37710-04ce-11e6-89be-b8881352c07d', '22df4350-04ce-11e6-89be-b8881352c07d', 1),
('ffbd2781-6642-11e6-90b2-844f6a1fd87f', DATE '2015-07-08', '0bca24f0-04ce-11e6-89be-b8881352c07d', '13c37710-04ce-11e6-89be-b8881352c07d', '22df4350-04ce-11e6-89be-b8881352c07d', 2),
('ffbeae20-6642-11e6-90b2-844f6a1fd87f', DATE '2015-07-09', '0bca24f0-04ce-11e6-89be-b8881352c07d', '13c37710-04ce-11e6-89be-b8881352c07d', '22df4350-04ce-11e6-89be-b8881352c07d', 3),
('ffbed530-6642-11e6-90b2-844f6a1fd87f', DATE '2015-07-10', '0bca24f0-04ce-11e6-89be-b8881352c07d', '13c37710-04ce-11e6-89be-b8881352c07d', '22df4350-04ce-11e6-89be-b8881352c07d', 4),
('ffbed531-6642-11e6-90b2-844f6a1fd87f', DATE '2015-07-11', '0bca24f0-04ce-11e6-89be-b8881352c07d', '13c37710-04ce-11e6-89be-b8881352c07d', '22df4350-04ce-11e6-89be-b8881352c07d', 5),
('ffbefc40-6642-11e6-90b2-844f6a1fd87f', DATE '2015-07-09', '0bca24f0-04ce-11e6-89be-b8881352c07d', '19938090-04ce-11e6-89be-b8881352c07d', '6b72fff0-04d6-11e6-86bc-b8881352c07d', 1),
('ffbf2350-6642-11e6-90b2-844f6a1fd87f', DATE '2015-07-09', '0bca24f0-04ce-11e6-89be-b8881352c07d', '29578380-08db-11e6-ade4-87c95010ccef', '6b72fff0-04d6-11e6-86bc-b8881352c07d', 2),
('ffbf2351-6642-11e6-90b2-844f6a1fd87f', DATE '2015-07-10', '0bca24f0-04ce-11e6-89be-b8881352c07d', '29578380-08db-11e6-ade4-87c95010ccef', 'f25f5290-04d4-11e6-86bc-b8881352c07d', 1),
('ffbf4a60-6642-11e6-90b2-844f6a1fd87f', DATE '2015-07-12', '0bca24f0-04ce-11e6-89be-b8881352c07d', '13c37710-04ce-11e6-89be-b8881352c07d', 'a2bb7c40-04d5-11e6-86bc-b8881352c07d', 1),
('ffbf4a61-6642-11e6-90b2-844f6a1fd87f', DATE '2015-07-10', '0bca24f0-04ce-11e6-89be-b8881352c07d', '19938090-04ce-11e6-89be-b8881352c07d', '733495b0-04d5-11e6-86bc-b8881352c07d', 1),
('a28fbdb0-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-22', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '6e72c680-663e-11e6-85c2-54cf7f9c33c5', '27dc3fd0-663e-11e6-85c2-54cf7f9c33c5', 1),
('a28fe4c0-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-23', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '6e72c680-663e-11e6-85c2-54cf7f9c33c5', '2be87940-663e-11e6-85c2-54cf7f9c33c5', 1),
('a28fe4c1-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-24', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '65fb1ca0-663e-11e6-85c2-54cf7f9c33c5', '325f9830-663e-11e6-85c2-54cf7f9c33c5', 1),
('a28fe4c2-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-24', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '6aa20f70-663e-11e6-85c2-54cf7f9c33c5', '394d1fa0-663e-11e6-85c2-54cf7f9c33c5', 1),
('a2900bd0-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-24', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '6e72c680-663e-11e6-85c2-54cf7f9c33c5', '3f220b20-663e-11e6-85c2-54cf7f9c33c5', 1),
('a2900bd1-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-20', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '65fb1ca0-663e-11e6-85c2-54cf7f9c33c5', '2f6d3330-663e-11e6-85c2-54cf7f9c33c5', 1),
('a29032e0-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-21', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '65fb1ca0-663e-11e6-85c2-54cf7f9c33c5', '2f6d3330-663e-11e6-85c2-54cf7f9c33c5', 2),
('a29032e1-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-22', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '65fb1ca0-663e-11e6-85c2-54cf7f9c33c5', '2f6d3330-663e-11e6-85c2-54cf7f9c33c5', 3);        
INSERT INTO PUBLIC."schedule"("id", "date", "convention_id", "slot_id", "event_id", "event_day") VALUES
('a29032e2-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-23', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '65fb1ca0-663e-11e6-85c2-54cf7f9c33c5', '2f6d3330-663e-11e6-85c2-54cf7f9c33c5', 4),
('a29059f0-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-25', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '65fb1ca0-663e-11e6-85c2-54cf7f9c33c5', '2f6d3330-663e-11e6-85c2-54cf7f9c33c5', 5),
('a29059f1-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-25', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '6aa20f70-663e-11e6-85c2-54cf7f9c33c5', '512a5380-663f-11e6-85c2-54cf7f9c33c5', 1),
('a29059f2-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-25', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '6e72c680-663e-11e6-85c2-54cf7f9c33c5', '60e6a580-663f-11e6-85c2-54cf7f9c33c5', 1),
('a2908100-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-23', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '6e72c680-663e-11e6-85c2-54cf7f9c33c5', '2949a090-6640-11e6-85c2-54cf7f9c33c5', 1),
('a2908101-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-24', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '6aa20f70-663e-11e6-85c2-54cf7f9c33c5', '477244f0-6640-11e6-85c2-54cf7f9c33c5', 1),
('a290a810-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-20', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '6e72c680-663e-11e6-85c2-54cf7f9c33c5', '6a89ba90-6640-11e6-85c2-54cf7f9c33c5', 1),
('a290a811-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-23', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '6aa20f70-663e-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 1),
('a290cf20-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-20', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '6aa20f70-663e-11e6-85c2-54cf7f9c33c5', '3499a9b0-663e-11e6-85c2-54cf7f9c33c5', 1),
('a290cf21-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-20', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '6e72c680-663e-11e6-85c2-54cf7f9c33c5', '3499a9b0-663e-11e6-85c2-54cf7f9c33c5', 2),
('a290cf22-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-20', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '65fb1ca0-663e-11e6-85c2-54cf7f9c33c5', '9cc77930-663f-11e6-85c2-54cf7f9c33c5', 1),
('a290f630-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-20', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '6aa20f70-663e-11e6-85c2-54cf7f9c33c5', '9cc77930-663f-11e6-85c2-54cf7f9c33c5', 2),
('a290f631-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-21', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '6aa20f70-663e-11e6-85c2-54cf7f9c33c5', 'da5d4fe0-663f-11e6-85c2-54cf7f9c33c5', 1),
('a2911d40-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-21', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '6e72c680-663e-11e6-85c2-54cf7f9c33c5', 'da5d4fe0-663f-11e6-85c2-54cf7f9c33c5', 2),
('a2911d41-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-21', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '6aa20f70-663e-11e6-85c2-54cf7f9c33c5', '147e4b20-6640-11e6-85c2-54cf7f9c33c5', 1),
('a2911d42-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-21', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '6e72c680-663e-11e6-85c2-54cf7f9c33c5', '147e4b20-6640-11e6-85c2-54cf7f9c33c5', 2),
('a2914450-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-22', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '6aa20f70-663e-11e6-85c2-54cf7f9c33c5', '78a39ca0-663f-11e6-85c2-54cf7f9c33c5', 1),
('a2914451-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-22', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '6e72c680-663e-11e6-85c2-54cf7f9c33c5', '78a39ca0-663f-11e6-85c2-54cf7f9c33c5', 2),
('a2914452-6643-11e6-90b2-844f6a1fd87f', DATE '2016-08-25', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '6e72c680-663e-11e6-85c2-54cf7f9c33c5', '375ee430-663e-11e6-85c2-54cf7f9c33c5', 1);
CREATE CACHED TABLE PUBLIC."events"(
    "id" UUID NOT NULL,
    "name" VARCHAR(100),
    "convention_id" UUID,
    "preferred_slot_id" UUID,
    "event_count" INTEGER DEFAULT 1,
    "event_type" SMALLINT DEFAULT 1,
    "preferred_day" DATE
);            
ALTER TABLE PUBLIC."events" ADD CONSTRAINT PUBLIC."events_primary_key_id" PRIMARY KEY("id");   
-- 27 +/- SELECT COUNT(*) FROM PUBLIC."events";
INSERT INTO PUBLIC."events"("id", "name", "convention_id", "preferred_slot_id", "event_count", "event_type", "preferred_day") VALUES
('2bcf30d0-04d6-11e6-86bc-b8881352c07d', 'Monsterhearts', '0bca24f0-04ce-11e6-89be-b8881352c07d', NULL, 1, 1, NULL),
('43a2b2a0-04d5-11e6-86bc-b8881352c07d', 'Die Macher', '0bca24f0-04ce-11e6-89be-b8881352c07d', NULL, 1, 1, NULL),
('e9c86b70-04d5-11e6-86bc-b8881352c07d', 'Patch history', '0bca24f0-04ce-11e6-89be-b8881352c07d', NULL, 2, 2, NULL),
('22df4350-04ce-11e6-89be-b8881352c07d', 'Dramasystem', '0bca24f0-04ce-11e6-89be-b8881352c07d', '13c37710-04ce-11e6-89be-b8881352c07d', 5, 3, NULL),
('6b72fff0-04d6-11e6-86bc-b8881352c07d', 'LARP', '0bca24f0-04ce-11e6-89be-b8881352c07d', NULL, 2, 2, NULL),
('f25f5290-04d4-11e6-86bc-b8881352c07d', 'Catthulhu', '0bca24f0-04ce-11e6-89be-b8881352c07d', NULL, 1, 1, NULL),
('a2bb7c40-04d5-11e6-86bc-b8881352c07d', '18XX', '0bca24f0-04ce-11e6-89be-b8881352c07d', NULL, 1, 1, DATE '2015-07-12'),
('733495b0-04d5-11e6-86bc-b8881352c07d', 'Through The Ages for beginners', '0bca24f0-04ce-11e6-89be-b8881352c07d', NULL, 1, 1, NULL),
('27dc3fd0-663e-11e6-85c2-54cf7f9c33c5', 'Portal MAID', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', NULL, 1, 1, NULL),
('2be87940-663e-11e6-85c2-54cf7f9c33c5', 'MGPFIM', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', NULL, 1, 1, NULL),
('325f9830-663e-11e6-85c2-54cf7f9c33c5', 'Monsterhearts', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', NULL, 1, 1, NULL),
('394d1fa0-663e-11e6-85c2-54cf7f9c33c5', 'Plan 8', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', NULL, 1, 1, NULL),
('3f220b20-663e-11e6-85c2-54cf7f9c33c5', 'Exalted Alchemicals', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', NULL, 1, 1, NULL),
('2f6d3330-663e-11e6-85c2-54cf7f9c33c5', 'DramaSystem', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '65fb1ca0-663e-11e6-85c2-54cf7f9c33c5', 5, 3, NULL),
('512a5380-663f-11e6-85c2-54cf7f9c33c5', 'Diplomacy', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', NULL, 1, 1, NULL),
('60e6a580-663f-11e6-85c2-54cf7f9c33c5', 'Time Stories', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', NULL, 1, 1, NULL),
('2949a090-6640-11e6-85c2-54cf7f9c33c5', 'Arkham Horror', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', NULL, 1, 1, NULL),
('477244f0-6640-11e6-85c2-54cf7f9c33c5', 'Eldritch Horror', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', NULL, 1, 1, NULL),
('6a89ba90-6640-11e6-85c2-54cf7f9c33c5', 'Eldritch Horror 2', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', NULL, 1, 1, NULL),
('92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'LARP', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '6aa20f70-663e-11e6-85c2-54cf7f9c33c5', 2, 2, DATE '2016-08-23'),
('78ae8e90-663e-11e6-85c2-54cf7f9c33c5', 'Pub', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '65fb1ca0-663e-11e6-85c2-54cf7f9c33c5', 1, 1, DATE '2016-08-23'),
('3499a9b0-663e-11e6-85c2-54cf7f9c33c5', 'Exalted', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', NULL, 2, 2, NULL),
('9cc77930-663f-11e6-85c2-54cf7f9c33c5', '1830', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', NULL, 2, 2, NULL),
('da5d4fe0-663f-11e6-85c2-54cf7f9c33c5', 'Twilight Imperium', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', NULL, 2, 2, NULL),
('147e4b20-6640-11e6-85c2-54cf7f9c33c5', 'Twilight Imperium 2', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', NULL, 2, 2, NULL),
('78a39ca0-663f-11e6-85c2-54cf7f9c33c5', '1832', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', NULL, 2, 2, NULL),
('375ee430-663e-11e6-85c2-54cf7f9c33c5', 'The King is Dead', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', '6e72c680-663e-11e6-85c2-54cf7f9c33c5', 1, 1, NULL);      
CREATE CACHED TABLE PUBLIC."events-persons"(
    "person_id" UUID NOT NULL,
    "event_id" UUID NOT NULL,
    "convention_id" UUID
);          
ALTER TABLE PUBLIC."events-persons" ADD CONSTRAINT PUBLIC."events_persons_primary_key_event_id_person_id" PRIMARY KEY("event_id", "person_id");
-- 186 +/- SELECT COUNT(*) FROM PUBLIC."events-persons";       
INSERT INTO PUBLIC."events-persons"("person_id", "event_id", "convention_id") VALUES
('31215160-04ce-11e6-89be-b8881352c07d', '22df4350-04ce-11e6-89be-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('330860e0-04ce-11e6-89be-b8881352c07d', '22df4350-04ce-11e6-89be-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('a7ec15a0-04ce-11e6-89be-b8881352c07d', '22df4350-04ce-11e6-89be-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('33fcd300-04ce-11e6-89be-b8881352c07d', '22df4350-04ce-11e6-89be-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('a738f240-04ce-11e6-89be-b8881352c07d', '22df4350-04ce-11e6-89be-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('2f4e3f10-04ce-11e6-89be-b8881352c07d', '22df4350-04ce-11e6-89be-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('ec030ea0-04d4-11e6-86bc-b8881352c07d', 'f25f5290-04d4-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('e2064b60-04d4-11e6-86bc-b8881352c07d', 'f25f5290-04d4-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('e61fcb40-04d4-11e6-86bc-b8881352c07d', 'f25f5290-04d4-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('e303be30-04d4-11e6-86bc-b8881352c07d', 'f25f5290-04d4-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('e7f54e90-04d4-11e6-86bc-b8881352c07d', 'f25f5290-04d4-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('508b4b30-04d5-11e6-86bc-b8881352c07d', '43a2b2a0-04d5-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('47e64fc0-04d5-11e6-86bc-b8881352c07d', '43a2b2a0-04d5-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('4d64b6d0-04d5-11e6-86bc-b8881352c07d', '43a2b2a0-04d5-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('4e4a0dc0-04d5-11e6-86bc-b8881352c07d', '43a2b2a0-04d5-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('7bbce160-04d5-11e6-86bc-b8881352c07d', '733495b0-04d5-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('ec030ea0-04d4-11e6-86bc-b8881352c07d', '733495b0-04d5-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('85bfe630-04d5-11e6-86bc-b8881352c07d', '733495b0-04d5-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('47e64fc0-04d5-11e6-86bc-b8881352c07d', '733495b0-04d5-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('4d64b6d0-04d5-11e6-86bc-b8881352c07d', 'a2bb7c40-04d5-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('a82b1640-04d5-11e6-86bc-b8881352c07d', 'a2bb7c40-04d5-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('e61fcb40-04d4-11e6-86bc-b8881352c07d', 'a2bb7c40-04d5-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('ac168140-04d5-11e6-86bc-b8881352c07d', 'a2bb7c40-04d5-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('508b4b30-04d5-11e6-86bc-b8881352c07d', 'a2bb7c40-04d5-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('85bfe630-04d5-11e6-86bc-b8881352c07d', 'a2bb7c40-04d5-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('a82b1640-04d5-11e6-86bc-b8881352c07d', 'e9c86b70-04d5-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('85bfe630-04d5-11e6-86bc-b8881352c07d', 'e9c86b70-04d5-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('4d64b6d0-04d5-11e6-86bc-b8881352c07d', 'e9c86b70-04d5-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('47e64fc0-04d5-11e6-86bc-b8881352c07d', 'e9c86b70-04d5-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('2f4e3f10-04ce-11e6-89be-b8881352c07d', '2bcf30d0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('31215160-04ce-11e6-89be-b8881352c07d', '2bcf30d0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('e2064b60-04d4-11e6-86bc-b8881352c07d', '2bcf30d0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('33fcd300-04ce-11e6-89be-b8881352c07d', '2bcf30d0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'); 
INSERT INTO PUBLIC."events-persons"("person_id", "event_id", "convention_id") VALUES
('3ac01c80-04d6-11e6-86bc-b8881352c07d', '6b72fff0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('ec030ea0-04d4-11e6-86bc-b8881352c07d', '6b72fff0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('508b4b30-04d5-11e6-86bc-b8881352c07d', '6b72fff0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('4d64b6d0-04d5-11e6-86bc-b8881352c07d', '6b72fff0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('e2064b60-04d4-11e6-86bc-b8881352c07d', '6b72fff0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('31215160-04ce-11e6-89be-b8881352c07d', '6b72fff0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('e7f54e90-04d4-11e6-86bc-b8881352c07d', '6b72fff0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('85bfe630-04d5-11e6-86bc-b8881352c07d', '6b72fff0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('4e4a0dc0-04d5-11e6-86bc-b8881352c07d', '6b72fff0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('47e64fc0-04d5-11e6-86bc-b8881352c07d', '6b72fff0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('e61fcb40-04d4-11e6-86bc-b8881352c07d', '6b72fff0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('e303be30-04d4-11e6-86bc-b8881352c07d', '6b72fff0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('7bbce160-04d5-11e6-86bc-b8881352c07d', '6b72fff0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('330860e0-04ce-11e6-89be-b8881352c07d', '6b72fff0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('a82b1640-04d5-11e6-86bc-b8881352c07d', '6b72fff0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('a7ec15a0-04ce-11e6-89be-b8881352c07d', '6b72fff0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('ac168140-04d5-11e6-86bc-b8881352c07d', '6b72fff0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('2f4e3f10-04ce-11e6-89be-b8881352c07d', '6b72fff0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('33fcd300-04ce-11e6-89be-b8881352c07d', '6b72fff0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('2f4e3f10-04ce-11e6-89be-b8881352c07d', 'f25f5290-04d4-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('a738f240-04ce-11e6-89be-b8881352c07d', '6b72fff0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('330860e0-04ce-11e6-89be-b8881352c07d', '2bcf30d0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('a738f240-04ce-11e6-89be-b8881352c07d', '2bcf30d0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('a7ec15a0-04ce-11e6-89be-b8881352c07d', '2bcf30d0-04d6-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('3ac01c80-04d6-11e6-86bc-b8881352c07d', '43a2b2a0-04d5-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('e2064b60-04d4-11e6-86bc-b8881352c07d', '43a2b2a0-04d5-11e6-86bc-b8881352c07d', '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('f3a7fdd0-663d-11e6-85c2-54cf7f9c33c5', '2f6d3330-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('1a7fafc0-663e-11e6-85c2-54cf7f9c33c5', '2f6d3330-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f69de540-663d-11e6-85c2-54cf7f9c33c5', '2f6d3330-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fad74930-663d-11e6-85c2-54cf7f9c33c5', '2f6d3330-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fd45d830-663d-11e6-85c2-54cf7f9c33c5', '2f6d3330-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fe3d7ea0-663d-11e6-85c2-54cf7f9c33c5', '2f6d3330-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('5dea71f0-663e-11e6-85c2-54cf7f9c33c5', '2f6d3330-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'); 
INSERT INTO PUBLIC."events-persons"("person_id", "event_id", "convention_id") VALUES
('147bc500-663e-11e6-85c2-54cf7f9c33c5', '78ae8e90-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('042c0ca0-663e-11e6-85c2-54cf7f9c33c5', '78ae8e90-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('1a7fafc0-663e-11e6-85c2-54cf7f9c33c5', '78ae8e90-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fe3d7ea0-663d-11e6-85c2-54cf7f9c33c5', '78ae8e90-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('01646120-663e-11e6-85c2-54cf7f9c33c5', '78ae8e90-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('1e25d460-663e-11e6-85c2-54cf7f9c33c5', '78ae8e90-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f48872c0-663d-11e6-85c2-54cf7f9c33c5', '78ae8e90-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('166ebb60-663e-11e6-85c2-54cf7f9c33c5', '78ae8e90-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f69de540-663d-11e6-85c2-54cf7f9c33c5', '78ae8e90-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f2c7aff0-663d-11e6-85c2-54cf7f9c33c5', '78ae8e90-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f5da6e30-663d-11e6-85c2-54cf7f9c33c5', '78ae8e90-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('5dea71f0-663e-11e6-85c2-54cf7f9c33c5', '78ae8e90-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('22f536c0-663e-11e6-85c2-54cf7f9c33c5', '78ae8e90-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fad74930-663d-11e6-85c2-54cf7f9c33c5', '78ae8e90-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fd45d830-663d-11e6-85c2-54cf7f9c33c5', '78ae8e90-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f3a7fdd0-663d-11e6-85c2-54cf7f9c33c5', '78ae8e90-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('175b8c60-663e-11e6-85c2-54cf7f9c33c5', '78ae8e90-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f2c7aff0-663d-11e6-85c2-54cf7f9c33c5', '27dc3fd0-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f3a7fdd0-663d-11e6-85c2-54cf7f9c33c5', '27dc3fd0-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f48872c0-663d-11e6-85c2-54cf7f9c33c5', '27dc3fd0-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f5da6e30-663d-11e6-85c2-54cf7f9c33c5', '27dc3fd0-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f69de540-663d-11e6-85c2-54cf7f9c33c5', '27dc3fd0-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f3a7fdd0-663d-11e6-85c2-54cf7f9c33c5', '2be87940-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fad74930-663d-11e6-85c2-54cf7f9c33c5', '2be87940-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fad74930-663d-11e6-85c2-54cf7f9c33c5', '325f9830-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f3a7fdd0-663d-11e6-85c2-54cf7f9c33c5', '325f9830-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('042c0ca0-663e-11e6-85c2-54cf7f9c33c5', '325f9830-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('147bc500-663e-11e6-85c2-54cf7f9c33c5', '325f9830-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('166ebb60-663e-11e6-85c2-54cf7f9c33c5', '325f9830-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('175b8c60-663e-11e6-85c2-54cf7f9c33c5', '325f9830-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('1a7fafc0-663e-11e6-85c2-54cf7f9c33c5', '325f9830-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('1a7fafc0-663e-11e6-85c2-54cf7f9c33c5', '3499a9b0-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fad74930-663d-11e6-85c2-54cf7f9c33c5', '3499a9b0-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'); 
INSERT INTO PUBLIC."events-persons"("person_id", "event_id", "convention_id") VALUES
('f3a7fdd0-663d-11e6-85c2-54cf7f9c33c5', '3499a9b0-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f69de540-663d-11e6-85c2-54cf7f9c33c5', '3499a9b0-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('22f536c0-663e-11e6-85c2-54cf7f9c33c5', '3499a9b0-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fad74930-663d-11e6-85c2-54cf7f9c33c5', '375ee430-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('1a7fafc0-663e-11e6-85c2-54cf7f9c33c5', '375ee430-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('1e25d460-663e-11e6-85c2-54cf7f9c33c5', '375ee430-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('01646120-663e-11e6-85c2-54cf7f9c33c5', '375ee430-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f3a7fdd0-663d-11e6-85c2-54cf7f9c33c5', '394d1fa0-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('01646120-663e-11e6-85c2-54cf7f9c33c5', '394d1fa0-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fa9c4690-663e-11e6-85c2-54cf7f9c33c5', '394d1fa0-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('061ee680-663f-11e6-85c2-54cf7f9c33c5', '3f220b20-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fad74930-663d-11e6-85c2-54cf7f9c33c5', '3f220b20-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fe3d7ea0-663d-11e6-85c2-54cf7f9c33c5', '3f220b20-663e-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('166ebb60-663e-11e6-85c2-54cf7f9c33c5', '512a5380-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f2c7aff0-663d-11e6-85c2-54cf7f9c33c5', '512a5380-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f5da6e30-663d-11e6-85c2-54cf7f9c33c5', '512a5380-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('166ebb60-663e-11e6-85c2-54cf7f9c33c5', '60e6a580-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('6a09aa90-663f-11e6-85c2-54cf7f9c33c5', '60e6a580-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fd45d830-663d-11e6-85c2-54cf7f9c33c5', '60e6a580-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f3a7fdd0-663d-11e6-85c2-54cf7f9c33c5', '60e6a580-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('22f536c0-663e-11e6-85c2-54cf7f9c33c5', '78a39ca0-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('38df7440-663f-11e6-85c2-54cf7f9c33c5', '78a39ca0-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('166ebb60-663e-11e6-85c2-54cf7f9c33c5', '78a39ca0-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('4c0d1f40-663f-11e6-85c2-54cf7f9c33c5', '78a39ca0-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('042c0ca0-663e-11e6-85c2-54cf7f9c33c5', '78a39ca0-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a2b89f40-663f-11e6-85c2-54cf7f9c33c5', '9cc77930-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a4379e20-663f-11e6-85c2-54cf7f9c33c5', '9cc77930-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('175b8c60-663e-11e6-85c2-54cf7f9c33c5', '9cc77930-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f2c7aff0-663d-11e6-85c2-54cf7f9c33c5', '9cc77930-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('423cc330-663f-11e6-85c2-54cf7f9c33c5', '9cc77930-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('3cae7da0-663f-11e6-85c2-54cf7f9c33c5', '9cc77930-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('4c0d1f40-663f-11e6-85c2-54cf7f9c33c5', 'da5d4fe0-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('22f536c0-663e-11e6-85c2-54cf7f9c33c5', 'da5d4fe0-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'); 
INSERT INTO PUBLIC."events-persons"("person_id", "event_id", "convention_id") VALUES
('1e25d460-663e-11e6-85c2-54cf7f9c33c5', 'da5d4fe0-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('38df7440-663f-11e6-85c2-54cf7f9c33c5', 'da5d4fe0-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('6a09aa90-663f-11e6-85c2-54cf7f9c33c5', 'da5d4fe0-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a2b89f40-663f-11e6-85c2-54cf7f9c33c5', 'da5d4fe0-663f-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('3cae7da0-663f-11e6-85c2-54cf7f9c33c5', '147e4b20-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a4379e20-663f-11e6-85c2-54cf7f9c33c5', '147e4b20-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('423cc330-663f-11e6-85c2-54cf7f9c33c5', '147e4b20-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f5da6e30-663d-11e6-85c2-54cf7f9c33c5', '147e4b20-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f2c7aff0-663d-11e6-85c2-54cf7f9c33c5', '147e4b20-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('166ebb60-663e-11e6-85c2-54cf7f9c33c5', '2949a090-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f69de540-663d-11e6-85c2-54cf7f9c33c5', '2949a090-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('175b8c60-663e-11e6-85c2-54cf7f9c33c5', '2949a090-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('1e25d460-663e-11e6-85c2-54cf7f9c33c5', '2949a090-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a2b89f40-663f-11e6-85c2-54cf7f9c33c5', '2949a090-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fa9c4690-663e-11e6-85c2-54cf7f9c33c5', '2949a090-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fd45d830-663d-11e6-85c2-54cf7f9c33c5', '2949a090-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('3cae7da0-663f-11e6-85c2-54cf7f9c33c5', '2949a090-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('061ee680-663f-11e6-85c2-54cf7f9c33c5', '477244f0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('147bc500-663e-11e6-85c2-54cf7f9c33c5', '477244f0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('423cc330-663f-11e6-85c2-54cf7f9c33c5', '477244f0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f69de540-663d-11e6-85c2-54cf7f9c33c5', '477244f0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('166ebb60-663e-11e6-85c2-54cf7f9c33c5', '477244f0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a2b89f40-663f-11e6-85c2-54cf7f9c33c5', '477244f0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fd45d830-663d-11e6-85c2-54cf7f9c33c5', '6a89ba90-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('3cae7da0-663f-11e6-85c2-54cf7f9c33c5', '6a89ba90-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('175b8c60-663e-11e6-85c2-54cf7f9c33c5', '6a89ba90-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('061ee680-663f-11e6-85c2-54cf7f9c33c5', '6a89ba90-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('147bc500-663e-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fa9c4690-663e-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('042c0ca0-663e-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a2b89f40-663f-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fe3d7ea0-663d-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('1a7fafc0-663e-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'); 
INSERT INTO PUBLIC."events-persons"("person_id", "event_id", "convention_id") VALUES
('01646120-663e-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('3cae7da0-663f-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('1e25d460-663e-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f48872c0-663d-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('423cc330-663f-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('166ebb60-663e-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('4c0d1f40-663f-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('38df7440-663f-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('061ee680-663f-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f69de540-663d-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f2c7aff0-663d-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f5da6e30-663d-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('5dea71f0-663e-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a4379e20-663f-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('6a09aa90-663f-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('22f536c0-663e-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fad74930-663d-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('fd45d830-663d-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('464b1f80-663f-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('f3a7fdd0-663d-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('175b8c60-663e-11e6-85c2-54cf7f9c33c5', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5');         
CREATE CACHED TABLE PUBLIC."person-non-availability"(
    "convention_id" UUID,
    "person_id" UUID NOT NULL,
    "date" DATE NOT NULL
);     
ALTER TABLE PUBLIC."person-non-availability" ADD CONSTRAINT PUBLIC."person_non_availability_primary_key_date_person_id" PRIMARY KEY("date", "person_id");      
-- 0 +/- SELECT COUNT(*) FROM PUBLIC."person-non-availability";
CREATE CACHED TABLE PUBLIC."schedule-issues"(
    "id" UUID NOT NULL,
    "issue" VARCHAR(256),
    "score" INTEGER,
    "level" INTEGER,
    "convention_id" UUID
);          
ALTER TABLE PUBLIC."schedule-issues" ADD CONSTRAINT PUBLIC."schedule_issues_primary_key_id" PRIMARY KEY("id"); 
-- 19 +/- SELECT COUNT(*) FROM PUBLIC."schedule-issues";       
INSERT INTO PUBLIC."schedule-issues"("id", "issue", "score", "level", "convention_id") VALUES
('ffbf9880-6642-11e6-90b2-844f6a1fd87f', 'one day events are as close as possible in order as doable', 0, 1, '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('ffbfbf91-6642-11e6-90b2-844f6a1fd87f', 'one day events are as close as possible in order as doable', 0, 1, '0bca24f0-04ce-11e6-89be-b8881352c07d'),
('a291b980-6643-11e6-90b2-844f6a1fd87f', 'spread events out (penalty per extra event in slot)', -1, 1, 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a291e092-6643-11e6-90b2-844f6a1fd87f', 'spread events out (penalty per extra event in slot)', -1, 1, 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a29207a2-6643-11e6-90b2-844f6a1fd87f', 'spread events out (penalty per extra event in slot)', -1, 1, 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a29255c0-6643-11e6-90b2-844f6a1fd87f', 'spread events out (penalty per extra event in slot)', -1, 1, 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a293b550-6643-11e6-90b2-844f6a1fd87f', 'spread events out (penalty per extra event in slot)', -1, 1, 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a293dc60-6643-11e6-90b2-844f6a1fd87f', 'spread events out (penalty per extra event in slot)', -1, 1, 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a293dc63-6643-11e6-90b2-844f6a1fd87f', 'spread events out (penalty per extra event in slot)', -1, 1, 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a2940372-6643-11e6-90b2-844f6a1fd87f', 'spread events out (penalty per extra event in slot)', -1, 1, 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a2942a80-6643-11e6-90b2-844f6a1fd87f', 'spread events out (penalty per extra event in slot)', -1, 1, 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a2942a83-6643-11e6-90b2-844f6a1fd87f', 'events with people shouldn''t have null slots', -15, 0, 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a2945191-6643-11e6-90b2-844f6a1fd87f', 'events with people shouldn''t have null slots', -15, 0, 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a2945193-6643-11e6-90b2-844f6a1fd87f', 'events with preferred slots like those', -13, 1, 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a29478a1-6643-11e6-90b2-844f6a1fd87f', 'one day events are as close as possible in order as doable', 0, 1, 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a29478a3-6643-11e6-90b2-844f6a1fd87f', 'one day events are as close as possible in order as doable', 0, 1, 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a2949fb1-6643-11e6-90b2-844f6a1fd87f', 'one day events are as close as possible in order as doable', 0, 1, 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a294c6c0-6643-11e6-90b2-844f6a1fd87f', 'one day events are as close as possible in order as doable', 0, 1, 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5'),
('a294c6c2-6643-11e6-90b2-844f6a1fd87f', 'one day events are as close as possible in order as doable', 0, 1, 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5');              
CREATE CACHED TABLE PUBLIC."schedule-issues-events"(
    "id" UUID NOT NULL,
    "convention_id" UUID,
    "schedule-issue_id" UUID,
    "event_id" UUID
);    
ALTER TABLE PUBLIC."schedule-issues-events" ADD CONSTRAINT PUBLIC."schedule_issues_events_primary_key_id" PRIMARY KEY("id");   
-- 28 +/- SELECT COUNT(*) FROM PUBLIC."schedule-issues-events";
INSERT INTO PUBLIC."schedule-issues-events"("id", "convention_id", "schedule-issue_id", "event_id") VALUES
('ffbfbf90-6642-11e6-90b2-844f6a1fd87f', '0bca24f0-04ce-11e6-89be-b8881352c07d', 'ffbf9880-6642-11e6-90b2-844f6a1fd87f', '6b72fff0-04d6-11e6-86bc-b8881352c07d'),
('ffbfbf92-6642-11e6-90b2-844f6a1fd87f', '0bca24f0-04ce-11e6-89be-b8881352c07d', 'ffbfbf91-6642-11e6-90b2-844f6a1fd87f', 'e9c86b70-04d5-11e6-86bc-b8881352c07d'),
('a291e090-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a291b980-6643-11e6-90b2-844f6a1fd87f', '375ee430-663e-11e6-85c2-54cf7f9c33c5'),
('a291e091-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a291b980-6643-11e6-90b2-844f6a1fd87f', '60e6a580-663f-11e6-85c2-54cf7f9c33c5'),
('a29207a0-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a291e092-6643-11e6-90b2-844f6a1fd87f', '2be87940-663e-11e6-85c2-54cf7f9c33c5'),
('a29207a1-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a291e092-6643-11e6-90b2-844f6a1fd87f', '2949a090-6640-11e6-85c2-54cf7f9c33c5'),
('a2922eb0-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a29207a2-6643-11e6-90b2-844f6a1fd87f', '394d1fa0-663e-11e6-85c2-54cf7f9c33c5'),
('a2922eb1-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a29207a2-6643-11e6-90b2-844f6a1fd87f', '477244f0-6640-11e6-85c2-54cf7f9c33c5'),
('a29255c1-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a29255c0-6643-11e6-90b2-844f6a1fd87f', '9cc77930-663f-11e6-85c2-54cf7f9c33c5'),
('a29255c2-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a29255c0-6643-11e6-90b2-844f6a1fd87f', '3499a9b0-663e-11e6-85c2-54cf7f9c33c5'),
('a293b551-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a293b550-6643-11e6-90b2-844f6a1fd87f', '6a89ba90-6640-11e6-85c2-54cf7f9c33c5'),
('a293b552-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a293b550-6643-11e6-90b2-844f6a1fd87f', '3499a9b0-663e-11e6-85c2-54cf7f9c33c5'),
('a293dc61-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a293dc60-6643-11e6-90b2-844f6a1fd87f', '2f6d3330-663e-11e6-85c2-54cf7f9c33c5'),
('a293dc62-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a293dc60-6643-11e6-90b2-844f6a1fd87f', '9cc77930-663f-11e6-85c2-54cf7f9c33c5'),
('a2940370-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a293dc63-6643-11e6-90b2-844f6a1fd87f', '147e4b20-6640-11e6-85c2-54cf7f9c33c5'),
('a2940371-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a293dc63-6643-11e6-90b2-844f6a1fd87f', 'da5d4fe0-663f-11e6-85c2-54cf7f9c33c5'),
('a2940373-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a2940372-6643-11e6-90b2-844f6a1fd87f', 'da5d4fe0-663f-11e6-85c2-54cf7f9c33c5'),
('a2940374-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a2940372-6643-11e6-90b2-844f6a1fd87f', '147e4b20-6640-11e6-85c2-54cf7f9c33c5'),
('a2942a81-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a2942a80-6643-11e6-90b2-844f6a1fd87f', '27dc3fd0-663e-11e6-85c2-54cf7f9c33c5'),
('a2942a82-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a2942a80-6643-11e6-90b2-844f6a1fd87f', '78a39ca0-663f-11e6-85c2-54cf7f9c33c5'),
('a2945190-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a2942a83-6643-11e6-90b2-844f6a1fd87f', '78ae8e90-663e-11e6-85c2-54cf7f9c33c5'),
('a2945192-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a2945191-6643-11e6-90b2-844f6a1fd87f', '92fec8d0-6640-11e6-85c2-54cf7f9c33c5'),
('a29478a0-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a2945193-6643-11e6-90b2-844f6a1fd87f', '78ae8e90-663e-11e6-85c2-54cf7f9c33c5'),
('a29478a2-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a29478a1-6643-11e6-90b2-844f6a1fd87f', '78a39ca0-663f-11e6-85c2-54cf7f9c33c5'),
('a2949fb0-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a29478a3-6643-11e6-90b2-844f6a1fd87f', '147e4b20-6640-11e6-85c2-54cf7f9c33c5');   
INSERT INTO PUBLIC."schedule-issues-events"("id", "convention_id", "schedule-issue_id", "event_id") VALUES
('a2949fb2-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a2949fb1-6643-11e6-90b2-844f6a1fd87f', 'da5d4fe0-663f-11e6-85c2-54cf7f9c33c5'),
('a294c6c1-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a294c6c0-6643-11e6-90b2-844f6a1fd87f', '9cc77930-663f-11e6-85c2-54cf7f9c33c5'),
('a294c6c3-6643-11e6-90b2-844f6a1fd87f', 'edb4dbf0-663d-11e6-85c2-54cf7f9c33c5', 'a294c6c2-6643-11e6-90b2-844f6a1fd87f', '3499a9b0-663e-11e6-85c2-54cf7f9c33c5');               
ALTER TABLE PUBLIC."conventions" ADD CONSTRAINT PUBLIC."name" CHECK(LENGTH("name") > 1) NOCHECK;               
ALTER TABLE PUBLIC."events" ADD CONSTRAINT PUBLIC."events_fkey_preferred_slot_id" FOREIGN KEY("preferred_slot_id") REFERENCES PUBLIC."slots"("id") ON DELETE SET NULL NOCHECK; 
ALTER TABLE PUBLIC."slots" ADD CONSTRAINT PUBLIC."slots_fkey_convention_id" FOREIGN KEY("convention_id") REFERENCES PUBLIC."conventions"("id") NOCHECK;        
ALTER TABLE PUBLIC."events-persons" ADD CONSTRAINT PUBLIC."events_persons_fkey_convention_id" FOREIGN KEY("convention_id") REFERENCES PUBLIC."conventions"("id") ON DELETE CASCADE NOCHECK;    
ALTER TABLE PUBLIC."events-persons" ADD CONSTRAINT PUBLIC."events_persons_fkey_person_id" FOREIGN KEY("person_id") REFERENCES PUBLIC."persons"("id") ON DELETE CASCADE NOCHECK;
ALTER TABLE PUBLIC."schedule-issues" ADD CONSTRAINT PUBLIC."schedule_issues_fkey_convention_id" FOREIGN KEY("convention_id") REFERENCES PUBLIC."conventions"("id") ON DELETE CASCADE NOCHECK;  
ALTER TABLE PUBLIC."schedule" ADD CONSTRAINT PUBLIC."schedule_fkey_slot_id" FOREIGN KEY("slot_id") REFERENCES PUBLIC."slots"("id") ON DELETE CASCADE NOCHECK;  
ALTER TABLE PUBLIC."events-persons" ADD CONSTRAINT PUBLIC."events_persons_fkey_event_id" FOREIGN KEY("event_id") REFERENCES PUBLIC."events"("id") ON DELETE CASCADE NOCHECK;   
ALTER TABLE PUBLIC."events" ADD CONSTRAINT PUBLIC."events_fkey_convention_id" FOREIGN KEY("convention_id") REFERENCES PUBLIC."conventions"("id") NOCHECK;      
ALTER TABLE PUBLIC."persons" ADD CONSTRAINT PUBLIC."persons_fkey_convention_id" FOREIGN KEY("convention_id") REFERENCES PUBLIC."conventions"("id") NOCHECK;    
ALTER TABLE PUBLIC."person-non-availability" ADD CONSTRAINT PUBLIC."person_non_availability_fkey_convention_id" FOREIGN KEY("convention_id") REFERENCES PUBLIC."conventions"("id") NOCHECK;    
ALTER TABLE PUBLIC."schedule" ADD CONSTRAINT PUBLIC."schedule_fkey_convention_id" FOREIGN KEY("convention_id") REFERENCES PUBLIC."conventions"("id") ON DELETE CASCADE NOCHECK;
ALTER TABLE PUBLIC."schedule-issues-events" ADD CONSTRAINT PUBLIC."schedule_issues_events_fkey_schedule_issue_id" FOREIGN KEY("schedule-issue_id") REFERENCES PUBLIC."schedule-issues"("id") ON DELETE CASCADE NOCHECK;        
ALTER TABLE PUBLIC."schedule-issues-events" ADD CONSTRAINT PUBLIC."schedule_issues_events_fkey_event_id" FOREIGN KEY("event_id") REFERENCES PUBLIC."events"("id") ON DELETE CASCADE NOCHECK;   
ALTER TABLE PUBLIC."person-non-availability" ADD CONSTRAINT PUBLIC."person_non_availability_fkey_person_id" FOREIGN KEY("person_id") REFERENCES PUBLIC."persons"("id") ON DELETE CASCADE NOCHECK;              
ALTER TABLE PUBLIC."schedule" ADD CONSTRAINT PUBLIC."schedule_fkey_event_id" FOREIGN KEY("event_id") REFERENCES PUBLIC."events"("id") ON DELETE CASCADE NOCHECK;               
ALTER TABLE PUBLIC."schedule-issues-events" ADD CONSTRAINT PUBLIC."schedule_issues_events_fkey_convention_id" FOREIGN KEY("convention_id") REFERENCES PUBLIC."conventions"("id") ON DELETE CASCADE NOCHECK;    
