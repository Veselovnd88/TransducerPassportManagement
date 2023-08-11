CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

GO

create table passport
(
    id            UUID    NOT NULL            DEFAULT gen_random_uuid(),
    template_id   UUID    NOT NULL,
    serial_number varchar NOT NULL,
    pt_art        varchar NOT NULL,
    print_date    DATE    NOT NULL,
    created_at    TIMESTAMP without time zone DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT template_pk PRIMARY KEY (id)
)
    GO