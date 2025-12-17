CREATE TABLE container (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       user_id VARCHAR(255) NOT NULL UNIQUE,
                       name VARCHAR(255),
                       created_at LocalDateTime NOT NULL default datetime('now')
);
