CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

GO

CREATE TABLE task
(
    task_id      UUID        NOT NULL        DEFAULT gen_random_uuid(),
    username     VARCHAR     NOT NULL,
    status       VARCHAR(20) NOT NULL,
    template_id  uuid        NOT NULL,
    print_date   DATE        NOT NULL,
    file_id      UUID,
    created_at   TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    performed_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT task_pk PRIMARY KEY (task_id)
)
    GO