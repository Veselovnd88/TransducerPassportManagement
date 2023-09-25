CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

GO

CREATE TABLE IF NOT EXISTS pt_user
(
    id            UUID         NOT NULL DEFAULT gen_random_uuid(),

    firstname     varchar(255) NOT NULL,

    lastname      varchar(255) NOT NULL,

    company_name  varchar(255),

    email         varchar(255) not null,

    user_password varchar(3000),

    role_id       UUID         not null,

    primary key (id)

);

GO

ALTER TABLE IF EXISTS pt_user
    ADD CONSTRAINT user_email_uk UNIQUE (email);

GO

CREATE TABLE user_role
(
    id        UUID    NOT NULL DEFAULT gen_random_uuid(),
    role_name varchar NOT NULL,
    primary key (id)
);

GO

ALTER TABLE IF EXISTS user_role
    ADD CONSTRAINT role_uk UNIQUE (role_name)
    GO

ALTER TABLE IF EXISTS pt_user
    ADD CONSTRAINT user_role_fk
        FOREIGN KEY (role_id) REFERENCES user_role (id);

GO

