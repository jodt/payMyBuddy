/*Create users*/
INSERT INTO user (mail, lastname, firstname, password)
    VALUES
    ('johndoo@gmail.com', 'doo', 'john', '$2a$10$xqnPokM.tOqQCiEga/HGAev0Fvx2JYUvCDWxU./VF5YvKzq6xuYFu'),
    ('bettyholberton@gmail.com', 'holberton', 'betty', '$2a$10$xqnPokM.tOqQCiEga/HGAev0Fvx2JYUvCDWxU./VF5YvKzq6xuYFu'),
    ('dennisritchie@gmail.com', 'richie', 'denis', '$2a$10$xqnPokM.tOqQCiEga/HGAev0Fvx2JYUvCDWxU./VF5YvKzq6xuYFu');

/*Create accounts*/
INSERT INTO account (number, user_id, balance)
    VALUES
        ('HGHGGHGH1233456770', 1, 0),
        ('HGHGGHGH1233456771', 2, 50),
        ('HGHGGHGH1233456772', 3, 100);

/*Create payment*/
INSERT INTO payment (amount, description, issuer_account_id, receiver_account_id)
    VALUES
        (10, "remboursement", 2, 3),
        (5, "remboursement", 3, 2);

/*Create bank account*/
INSERT INTO bank_account (iban, user_id)
    VALUES
        ('FR123456789',1),
        ('FR987654321',2),
        ('FR555666777',3);

/*Create transfer*/
INSERT INTO transfer (amount, bank_account_id, account_id, operation)
    VALUES
        ('50',3,3, 'CREDIT');

/*Create buddy*/
INSERT INTO buddy (user_id, buddy_id)

    VALUES
        (2,3),
        (3,2);