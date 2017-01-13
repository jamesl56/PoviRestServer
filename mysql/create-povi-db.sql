drop user 'povi'@'localhost';
create user 'povi'@'localhost' identified by 'some_pass';
grant all privileges on *.* to 'povi'@'localhost' with grant option;
set password for 'povi'@'localhost' = password('povi');
create database povi_schema;
