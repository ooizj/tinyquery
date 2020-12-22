
-- mysql
drop table if exists t1 ; 
CREATE TABLE t1 (
	id int(11) NOT NULL AUTO_INCREMENT,
	name varchar(255),
	user_sex int(4),
	user_birthday date,
	create_time timestamp(6),
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- oracle
CREATE TABLE t1 (
	id NUMBER(11) PRIMARY KEY ,
	name varchar2(255),
	user_sex NUMBER(4),
	user_birthday date,
	create_time timestamp(6)
);
CREATE SEQUENCE t1_seq
    INCREMENT BY 1
    START WITH 1 
    NOCYCLE
    NOCACHE;