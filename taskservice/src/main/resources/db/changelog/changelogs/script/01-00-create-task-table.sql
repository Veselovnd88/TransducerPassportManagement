CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

GO

CREATE TABLE task
(
    id           UUID    NOT NULL            DEFAULT gen_random_uuid(),
    username     VARCHAR NOT NULL,
    is_performed BOOLEAN                     DEFAULT FALSE,
    created_at   TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    performed_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT task_pk PRIMARY KEY (id)
)
    GO