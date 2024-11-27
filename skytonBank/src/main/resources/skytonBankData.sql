CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    two_factor_token VARCHAR(6),
    two_factor_enabled BOOLEAN DEFAULT FALSE,
    two_factor_verified BOOLEAN DEFAULT FALSE
);

CREATE TABLE roles (
id SERIAL PRIMARY KEY, name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE user_roles (
    user_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

CREATE TABLE accounts (
    id SERIAL PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    balance DOUBLE PRECISION NOT NULL,
    user_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    amount DOUBLE PRECISION NOT NULL,
    type VARCHAR(10) NOT NULL, -- "credit" or "debit"
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    charge DOUBLE PRECISION NOT NULL,
    description TEXT,
    account_id INTEGER NOT NULL,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

ALTER TABLE transactions ADD COLUMN receiver VARCHAR(255);
