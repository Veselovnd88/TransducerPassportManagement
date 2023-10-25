CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

GO

CREATE TABLE result_file
(
    id          UUID           NOT NULL     DEFAULT gen_random_uuid(),
    filename    VARCHAR UNIQUE NOT NULL,
    bucket      VARCHAR        NOT NULL,
    synced      BOOLEAN                     DEFAULT FALSE,
    template_id UUID           REFERENCES public.pass_template (id) ON DELETE SET NULL,
    username    VARCHAR        NOT NULL,
    task_id      UUID           NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT generated_result_file_pk PRIMARY KEY (id)
)
    GO