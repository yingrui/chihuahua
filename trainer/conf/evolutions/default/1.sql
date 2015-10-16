# --- !Ups

create table "dialogs" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "topic" varchar not null,
  "relationship" varchar not null,
  "username" varchar not null
);

create table "messages" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "dialog_id" bigint,
  "username" varchar not null,
  "content" varchar not null,
  "timestamp" varchar not null,
  "questionId" bigint,
  "tags" varchar
);

# --- !Downs

drop table "dialogs" if exists;
drop table "messages" if exists;
