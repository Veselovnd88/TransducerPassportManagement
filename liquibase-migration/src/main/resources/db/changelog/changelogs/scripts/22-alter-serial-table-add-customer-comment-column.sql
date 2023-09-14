alter table public.serial_number
    ADD COLUMN customer    varchar default 'NA',
    ADD COLUMN ext_comment varchar;

GO