-- Below script works with PGSql.

-- A Twitter User's information.

create table if not exists tweeter (
id varchar(10) primary key,
location varchar(20),
followers_count bigint default 0,
friends_count bigint default 0,
statuses_count bigint default 0,
lang varchar(5)
);

-- A Tweet status updated by a Tweeter.

create table if not exists tweet (
id varchar(20) primary key,
message varchar(140) not null,
lang varchar(5),
epoch_ms bigint not null,
tweeter_id varchar(10) references tweeter(id) not null
);
