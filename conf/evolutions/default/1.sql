# --- First database schema

# --- !Ups
create table user (
  id                        bigint not null auto_increment,
  cookie                    varchar(255) not null,
  browser                   varchar(255) not null,
  primary key (cookie)
);

create table web (
  id                        bigint not null auto_increment,
  url                       varchar(255) not null,
  advertiser                varchar(255) not null,
  primary key (url)
);

create table user_web (
  id                        bigint not null auto_increment,
  cookie                    varchar(255) not null,
  url                       varchar(255) not null,
  count                     bigint not null default 0,
  primary key (id)
);

# --- !Downs
drop table if exists user;
drop table if exists web;
drop table if exists user_web;