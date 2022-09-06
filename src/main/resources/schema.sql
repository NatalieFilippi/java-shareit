CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                     name VARCHAR(255) NOT NULL,
                                     email VARCHAR(512) NOT NULL,
                                     CONSTRAINT pk_user PRIMARY KEY (id),
                                     CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
                                    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                    name VARCHAR(255) NOT NULL,
                                    description VARCHAR,
                                    is_available BOOLEAN NOT NULL,
                                    owner_id BIGINT REFERENCES users(id),
                                    request_id BIGINT
);

CREATE TABLE IF NOT EXISTS bookings (
                                     id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     start_date TIMESTAMP WITHOUT TIME ZONE,
                                     end_date TIMESTAMP WITHOUT TIME ZONE,
                                     item_id BIGINT REFERENCES items(id),
                                     booker_id BIGINT REFERENCES users(id),
                                     status VARCHAR
);

CREATE TABLE IF NOT EXISTS requests (
                                        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                        description VARCHAR,
                                        requestor_id BIGINT REFERENCES users(id),
                                        created TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS comments (
                                        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                        text VARCHAR,
                                        item_id BIGINT REFERENCES items(id),
                                        author_id BIGINT REFERENCES users(id)
);