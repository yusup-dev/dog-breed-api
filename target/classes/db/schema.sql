CREATE TABLE IF NOT EXISTS dogs (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100)  NOT NULL,
    breed       VARCHAR(100)  NOT NULL,
    sub_breed   VARCHAR(100),
    age         INT,
    description VARCHAR(500),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
