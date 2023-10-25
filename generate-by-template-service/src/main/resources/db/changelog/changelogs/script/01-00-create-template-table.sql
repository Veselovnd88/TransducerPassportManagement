CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

GO

CREATE TABLE pass_template
(
    id            UUID           NOT NULL     DEFAULT gen_random_uuid(),
    pt_art        VARCHAR(20)    NOT NULL,
    filename      VARCHAR UNIQUE NOT NULL,
    template_name VARCHAR UNIQUE NOT NULL,
    bucket        VARCHAR(20)    NOT NULL,
    synced        BOOLEAN                     DEFAULT FALSE,
    edited_at     TIMESTAMP WITHOUT TIME ZONE,
    created_at    TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pass_template_pk PRIMARY KEY (id)
)
    GO