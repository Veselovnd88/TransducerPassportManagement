CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

GO

create table pass_template
(
    id         UUID    NOT NULL            DEFAULT gen_random_uuid(),
    pt_art     varchar NOT NULL,
    filename   varchar NOT NULL,
    bucket     varchar NOT NULL,
    edited_at  TIMESTAMP with time zone,
    created_at TIMESTAMP without time zone DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pass_template_pk PRIMARY KEY (id)
)
    GO