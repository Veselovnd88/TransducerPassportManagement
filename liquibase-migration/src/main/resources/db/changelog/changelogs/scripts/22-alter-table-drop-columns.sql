alter table serial_number
    drop COLUMN IF EXISTS ext_comment,
    DROP COLUMN IF EXISTS customer;

GO