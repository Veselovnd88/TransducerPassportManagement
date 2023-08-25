drop table if exists customer;

alter table public.serial_number
    drop column customer_id,
    ADD COLUMN customer varchar NOT NULL default 'NA';

GO