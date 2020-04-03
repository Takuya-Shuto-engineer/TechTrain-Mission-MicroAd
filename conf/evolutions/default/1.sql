# --- First database schema

# --- !Ups
create table user (
  id                        varchar(255) not null,
  browser                   varchar(255) not null,
  primary key (id)
);

create table web (
  id                        varchar(255) not null,
  url                       varchar(255) not null,
  primary key (id)
);

create table user_web (
  id                        bigint not null auto_increment,
  user_id                   varchar(255) not null,
  web_id                    bigint not null,
  count                     bigint not null,
  primary key (id)
);

# --- !Downs
drop table if exists user;
drop table if exists web;
drop table if exists user_web;