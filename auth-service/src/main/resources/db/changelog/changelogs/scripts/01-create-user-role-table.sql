CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

GO

CREATE TABLE IF NOT EXISTS pt_user
(
    id            UUID         NOT NULL       DEFAULT gen_random_uuid(),

    firstname     VARCHAR(255) NOT NULL,

    lastname      VARCHAR(255) NOT NULL,

    company_name  VARCHAR(255),

    email         VARCHAR(255) NOT NULL UNIQUE,

    user_password VARCHAR(3000),

    created_at    TIMESTAMP without time zone DEFAULT CURRENT_TIMESTAMP,

    deleted       BOOLEAN                     default FALSE,

    PRIMARY KEY (id)

);

GO

CREATE TABLE user_role
(
    id         UUID    NOT NULL            DEFAULT gen_random_uuid(),
    role_name  VARCHAR NOT NULL,
    created_at TIMESTAMP without time zone DEFAULT CURRENT_TIMESTAMP,
    primary key (id)
);

GO

CREATE TABLE users_roles
(
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES pt_user (id),
    FOREIGN KEY (role_id) REFERENCES user_role (id)
);


GO

INSERT INTO user_role(role_name)
VALUES ('ADMIN'),
       ('MANAGER'),
       ('CUSTOMER');

GO

