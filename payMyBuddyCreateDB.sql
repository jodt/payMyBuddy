/*Create database*/
CREATE DATABASE IF NOT EXISTS `paymybuddy`;

/*Use paymybuddy database*/
USE paymybuddy;

/*Create user table*/
CREATE TABLE IF NOT EXISTS `user` (
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    mail VARCHAR(255) NOT NULL UNIQUE,
    lastname VARCHAR(100) NOT NULL,
    firstname VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL
);

/*create account table*/
CREATE TABLE IF NOT EXISTS `account` (
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(50) NOT NULL,
    user_id INTEGER NOT NULL,
    balance DOUBLE,
    FOREIGN KEY (user_id) REFERENCES user (id)
);

/*create payment table*/
CREATE TABLE IF NOT EXISTS `payment` (
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    amount DOUBLE NOT NULL,
    description VARCHAR(100),
    issuer_account_id INTEGER NOT NULL,
    receiver_account_id INTEGER NOT NULL,
    FOREIGN KEY (issuer_account_id) REFERENCES account (id),
    FOREIGN KEY (receiver_account_id) REFERENCES account (id)
);

/*create bank account table*/
CREATE TABLE IF NOT EXISTS `bank_account` (
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    iban VARCHAR(100) NOT NULL,
    user_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id)
);

/*create bank transfer table*/
CREATE TABLE IF NOT EXISTS `transfer` (
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    amount DOUBLE NOT NULL,
    bank_account_id INTEGER NOT NULL,
    account_id INTEGER NOT NULL,
    operation VARCHAR(10) NOT NULL,
    FOREIGN KEY (bank_account_id) REFERENCES bank_account (id),
    FOREIGN KEY (account_id) REFERENCES account (id)
);

/*create buddy table*/
CREATE TABLE IF NOT EXISTS `buddy` (
    user_id INTEGER NOT NULL,
    buddy_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
    FOREIGN KEY (buddy_id) REFERENCES user (id),
    PRIMARY KEY (user_id,buddy_id)
)