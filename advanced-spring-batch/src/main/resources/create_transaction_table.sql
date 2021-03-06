drop table if exists bank_transaction;

create table bank_transaction(
    transaction_id varchar(255),
    account_id varchar(255),
    transaction_date date,
    transaction_type varchar(255),
    transaction_amount decimal(32,6)
);
