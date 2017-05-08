SELECT q.id, qq.stem, qk.pointids FROM question_kpoints qk
  JOIN questions q on qk.questionid = q.id
  JOIN question_qmls qq on qk.questionid = qq.questionid
WHERE applicationid = 'zujuan' AND courseid = 27
LIMIT 0, 10000;

SELECT * FROM base_info;

select label from ml_feature group by label having count(*)>1;

select * from ml_feature f where not exists(select 1 from ml_feature where name=f.name and label="_total");
select * from ml_feature f where not exists(select 1 from ml_feature where name=f.name and label !="_total");

SELECT name, label FROM ml_feature where label="_total" group by name having count(name)>1;
SELECT count(*) FROM ml_feature where label="_total";
select count(*) from (SELECT name FROM ml_feature group by name)tmp;

SELECT count(*) FROM ml_feature where name="则";
SELECT  * from ml_feature where label="_total";
SELECT  * from ml_feature where name="则" and label="_total";
SELECT sum(count) FROM ml_feature where name="则" and label != "_total";

select count(*) from ml_label;
select count(*) from (select name from ml_label group by name)temp;

SHOW GLOBAL VARIABLES LIKE '%packet%';
set global SQL_MODE = "";
SET GLOBAL max_allowed_packet = 1677721600;

delete from base_info;
delete from ml_label;
drop table ml_feature;
CREATE TABLE ml_feature(     NAME VARCHAR(512) BINARY,     label VARCHAR(20),     COUNT INT,     courseid INT );


show create TABLE ml_feature;
ALTER TABLE ml_feature ADD INDEX name(name), ADD INDEX label(label), ADD INDEX `count`(`count`);


select * from ml_feature where BINARY name="A." and label="_total";