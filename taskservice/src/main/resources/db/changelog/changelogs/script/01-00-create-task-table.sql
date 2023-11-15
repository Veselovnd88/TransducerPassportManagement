CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

GO

CREATE TABLE task
(
    task_id      UUID    NOT NULL            DEFAULT gen_random_uuid(),
    username     VARCHAR NOT NULL,
    performed    BOOLEAN                     DEFAULT FALSE,
    started      BOOLEAN                     DEFAULT FALSE,
    template_id  uuid    NOT NULL,
    print_date   DATE    NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    performed_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT task_pk PRIMARY KEY (task_id)
)
    GO