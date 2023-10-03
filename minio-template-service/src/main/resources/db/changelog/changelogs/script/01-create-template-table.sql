CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

GO

CREATE TABLE pass_template
(
    id            UUID           NOT NULL     DEFAULT gen_random_uuid(),
    pt_art        varchar(20)    NOT NULL,
    filename      varchar UNIQUE NOT NULL,
    template_name varchar UNIQUE NOT NULL,
    bucket        varchar        NOT NULL,
    synced        BOOLEAN                     DEFAULT FALSE,
    edited_at     TIMESTAMP with time zone,
    created_at    TIMESTAMP without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pass_template_pk PRIMARY KEY (id)
)
    GO