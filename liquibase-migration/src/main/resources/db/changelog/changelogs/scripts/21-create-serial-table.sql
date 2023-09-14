CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

GO

create table serial_number
(
    id         UUID    NOT NULL            DEFAULT gen_random_uuid(),
    number     varchar NOT NULL,
    pt_art     varchar REFERENCES public.transducer (art) ON DELETE SET NULL,
    pt_id      uuid    REFERENCES public.transducer (id) ON DELETE SET NULL,
    saved_at   DATE                        DEFAULT CURRENT_DATE,
    created_at TIMESTAMP without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT serial_pk PRIMARY KEY (id)
)
    GO