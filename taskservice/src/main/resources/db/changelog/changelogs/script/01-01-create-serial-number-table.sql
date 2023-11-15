CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

GO

CREATE TABLE serial_number
(
    serial_id UUID        NOT NULL DEFAULT gen_random_uuid(),
    serial    VARCHAR(30) NOT NULL,
    CONSTRAINT serial_number_pk PRIMARY KEY (serial_id)
)
    GO