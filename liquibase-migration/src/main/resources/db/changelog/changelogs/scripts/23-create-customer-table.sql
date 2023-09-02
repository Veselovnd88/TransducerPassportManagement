CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

GO

create table customer
(
    id   UUID    NOT NULL DEFAULT gen_random_uuid(),
    customer_name varchar NOT NULL,
    inn  varchar NOT NULL UNIQUE,
    created_at TIMESTAMP without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT customer_id PRIMARY KEY (id)
)
    GO

alter table public.serial_number
    drop column customer,
    add column customer_id uuid REFERENCES public.customer (id) ON DELETE SET NULL;

GO