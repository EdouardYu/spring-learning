CREATE DATABASE social_network;
CREATE TABLE role (
                      id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
                      label VARCHAR(15) NOT NULL DEFAULT 'USER'
);

CREATE TABLE user (
                      id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
                      password VARCHAR(255) NOT NULL,
                      username VARCHAR(15) UNIQUE NOT NULL,
                      email VARCHAR(127) UNIQUE NOT NULL,
                      enabled BOOLEAN NOT NULL DEFAULT FALSE,
                      role_id INTEGER NOT NULL,
                      CONSTRAINT role_fk FOREIGN KEY (role_id) REFERENCES role(id)
);


CREATE TABLE validation (
    id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expired_at TIMESTAMP NOT NULL,
    activated_at TIMESTAMP,
    activation_code CHARACTER(6) NOT NULL,
    user_id INTEGER NOT NULL,
    CONSTRAINT validation_user_fk FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE post (
    id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    title VARCHAR(127) NOT NULL,
    content VARCHAR(511),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_update TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    user_id INTEGER NOT NULL,
    CONSTRAINT post_user_fk FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE refresh_token (
    id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    expired BOOLEAN NOT NULL DEFAULT FALSE,
    value VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expired_at TIMESTAMP NOT NULL
);

CREATE TABLE jwt (
    id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    value VARCHAR(255) NOT NULL,
    deactivated BOOLEAN NOT NULL DEFAULT FALSE,
    expired BOOLEAN NOT NULL DEFAULT FALSE,
    /*
    refresh_token VARCHAR(255) NOT NULL,
    refresh_token_expiration TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     */
    refresh_token_id INTEGER NOT NULL,
    CONSTRAINT refresh_token_fk FOREIGN KEY (refresh_token_id) REFERENCES refresh_token(id),
    user_id INTEGER NOT NULL,
    CONSTRAINT jwt_user_fk FOREIGN KEY (user_id) REFERENCES user(id)
);
