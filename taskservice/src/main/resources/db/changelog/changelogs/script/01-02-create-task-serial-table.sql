CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

GO

CREATE TABLE task_serial
(
    task_id   UUID REFERENCES public.task (id)                 NOT NULL,
    serial_id UUID REFERENCES public.serial_number (serial_id) NOT NULL,
    CONSTRAINT task_serial_pk PRIMARY KEY (task_id, serial_id)

)
    GO