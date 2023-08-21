CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

GO

create table transducer
(
    id          UUID    NOT NULL            DEFAULT gen_random_uuid(),
    art         varchar NOT NULL UNIQUE,
    tr_name     varchar NOT NULL,
    pressure_type  varchar NOT NULL,
    model       varchar NOT NULL,
    output_code varchar NOT NULL,
    pressure_range     varchar NOT NULL,
    accuracy    varchar NOT NULL,
    electrical_output   varchar NOT NULL,
    thread      varchar NOT NULL,
    connector   varchar NOT NULL,
    pin_out      varchar NOT NULL,
    options     varchar,
    created_at  TIMESTAMP without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT transducer_pk PRIMARY KEY (id)
)
    GO