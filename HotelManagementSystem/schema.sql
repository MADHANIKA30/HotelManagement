create database if not exists hotel_db;
use hotel_db;

create table rooms (
    room_no int primary key,
    type varchar(20),
    price double,
    status varchar(20) default 'Available'
);

create table bookings (
    booking_id int auto_increment primary key,
    name varchar(50),
    phone varchar(15),
    room_no int,
    nights int,
    status varchar(20)
) auto_increment = 1001;

insert into rooms (room_no, type, price) values
(101, 'Single', 1500),
(102, 'Single', 1500),
(103, 'Single', 1500),
(201, 'Double', 2500),
(202, 'Double', 2500),
(301, 'Suite', 5000),
(302, 'Suite', 5000);
