create table client (
    id bigint not null,
    gender char(10),
    birth_date date,
    district varchar(100)
);

create table account (
    id bigint not null,
    statement_frequency char(10),
    date_opened date,
    district varchar(100)
);

create table client_account(
    id bigint not null,
    client_id bigint not null,
    account_id bigint not null
);
