CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       clerk_id VARCHAR(255) NOT NULL UNIQUE,
                       name VARCHAR(255),
                       profile_image_url VARCHAR(500),
                       status CHAR(1) NOT NULL DEFAULT 'Y'
);
