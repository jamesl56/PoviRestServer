drop user 'socialplay'@'localhost';
create user 'socialplay'@'localhost' identified by 'some_pass';
grant all privileges on *.* to 'socialplay'@'localhost' with grant option;
set password for 'socialplay'@'localhost' = password('socialplay');
create database socialplay;
