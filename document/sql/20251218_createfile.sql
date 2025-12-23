CREATE TABLE files (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    container_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    parent_id BIGINT,
    is_directory BOOLEAN NOT NULL,
    path VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    extension VARCHAR(50),
    FOREIGN KEY (container_id) REFERENCES container(id),
    FOREIGN KEY (parent_id) REFERENCES files(id)
);
