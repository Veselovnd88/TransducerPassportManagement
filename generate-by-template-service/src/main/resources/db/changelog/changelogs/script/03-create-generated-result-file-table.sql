CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

GO

CREATE TABLE generated_result_file
(
    id         UUID           NOT NULL DEFAULT gen_random_uuid(),
    filename   varchar UNIQUE NOT NULL,
    bucket     varchar        NOT NULL,
    synced     BOOLEAN                 DEFAULT FALSE,
    created_at TIMESTAMP without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT generated_result_file_pk PRIMARY KEY (id)
)

GO