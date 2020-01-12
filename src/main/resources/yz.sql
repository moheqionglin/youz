create database yz;

use yz;


CREATE TABLE users (
    id int auto_increment primary key,
    sex varchar(5) NULL default '男',
    password varchar(100) default '',
    nick_name varchar(50) NULL,
    birthday datetime DEFAULT CURRENT_TIMESTAMP,
    reg_time timestamp DEFAULT CURRENT_TIMESTAMP,
    head_picture varchar(255) NULL,
    disable bit(1) default false,
    open_code varchar(100) NULL,
    amount decimal(10,2)   default 0,
    yongjin decimal(10,2)  default 0,
    created_time timestamp DEFAULT CURRENT_TIMESTAMP ,
	modified_time timestamp  DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE roles (
    id int auto_increment primary key,
    role_name varchar(20) NULL
);
insert into roles values(1, 'BUYER');
insert into roles values(2, 'JIANHUO');
insert into roles values(3, 'ADMIN');


CREATE TABLE user_roles (
    user_id int,
    roles_id int
);

create table user_tokes(
    id int auto_increment primary key,
    user_id int,
    token varchar(400)
);


select * from users
select role_name from roles left join user_roles on roles.id = user_roles.roles_id where user_roles.user_id = 1