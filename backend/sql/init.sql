-- 元数据
create schema ct;
create table ct.os_meta(queryName varchar primary key,
chineseName VARCHAR, description VARCHAR,
tableNames VARCHAR, connection VARCHAR);
upsert into ct.os_meta values ('t1', 't1', 't1_desc', 'us.userA,us.userB', 'id,classId');
create table ct.os_table(schemaName VARCHAR, tableName VARCHAR, comment VARCHAR,
CONSTRAINT pk PRIMARY KEY(schemaName, tableName));
create table ct.os_column(schemaName VARCHAR, tableName VARCHAR, columnName VARCHAR, comment VARCHAR,
CONSTRAINT pk PRIMARY KEY(schemaName, tableName, columnName));
-- 示例数据
create schema "us";
create table "us"."userA" ("id" bigint not null, "classId" bigint not null, "pkId" bigint not
 null, "nameA" varchar, "adderA" varchar, constraint userA_pk PRIMARY KEY("id", "classId", "pkId"));

create index userA_name on "us"."userA"("nameA");

create table "us"."userB" ("id" bigint not null, "classId" bigint not null, "nameB" varchar,
 constraint userB_pk PRIMARY KEY("id", "classId"));

upsert into "us"."userA" values (1, 1, 1, 'a', 'A_adder');
upsert into "us"."userA" values (1, 2, 3, 'a', 'A_adder3');
upsert into "us"."userA" values (1, 1, 2, 'a2', 'a2_adder');
upsert into "us"."userA" values (2, 2, 2, 'a2', 'a2_adder');
upsert into "us"."userA" values (2, 2, 1, 'a1', 'a1_adder');

upsert into "us"."userB" values (1, 1, 'b-a');
upsert into "us"."userB" values (1, 2, 'b-b');
upsert into "us"."userB" values (2, 2, 'b-b');