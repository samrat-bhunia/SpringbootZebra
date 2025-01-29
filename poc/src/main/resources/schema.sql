CREATE TABLE transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255),
    amount DECIMAL(10, 2),
    transaction_date TIMESTAMP
);