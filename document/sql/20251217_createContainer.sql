CREATE TABLE container (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       user_id BIGINT NOT NULL UNIQUE,
                       name VARCHAR(255),
                       created_at TIMESTAMP NOT NULL default datetime('now')
);
