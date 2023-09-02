alter table public.serial_number
    ADD COLUMN customer    varchar NOT NULL default 'NA',
    ADD COLUMN ext_comment varchar;

GO