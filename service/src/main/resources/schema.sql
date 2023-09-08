CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT PK_USER PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description  VARCHAR(512)                         NOT NULL,
    requester_id BIGINT                               NOT NULL,
    created   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT PK_REQUEST PRIMARY KEY (id),
    CONSTRAINT FK_USER FOREIGN KEY (requester_id) references users
        on delete cascade on update cascade
);

CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name         VARCHAR(255)                            NOT NULL,
    description  VARCHAR(512)                            NOT NULL,
    is_available BOOLEAN                                 NOT NULL,
    owner_id     BIGINT                                  NOT NULL,
    request_id BIGINT,
    CONSTRAINT PK_ITEM PRIMARY KEY (id),
    CONSTRAINT FK_ITEM_OWNER FOREIGN KEY (owner_id) references users
        on delete cascade on update cascade,
    CONSTRAINT FK_REQUEST_ID FOREIGN KEY (request_id) references requests
        on delete cascade on update cascade
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    end_date   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    item_id    BIGINT                                  NOT NULL,
    booker_id  BIGINT                                  NOT NULL,
    status     VARCHAR(10)                             NOT NULL,
    CONSTRAINT PK_BOOKING PRIMARY KEY (id),
    CONSTRAINT FK_BOOKING_ITEM FOREIGN KEY (item_id) references items
        on delete cascade on update cascade,
    CONSTRAINT FK_BOOKING_BOOKER FOREIGN KEY (booker_id) references users
        on delete cascade on update cascade
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text      VARCHAR(1000)                           NOT NULL,
    created   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    item_id   BIGINT                                  NOT NULL,
    author_id BIGINT                                  NOT NULL,
    CONSTRAINT PK_COMMENT PRIMARY KEY (id),
    CONSTRAINT FK_COMMENT_ITEM FOREIGN KEY (item_id) references items
        on delete cascade on update cascade,
    CONSTRAINT FK_COMMENT_AUTHOR FOREIGN KEY (author_id) references users
        on delete cascade on update cascade
);