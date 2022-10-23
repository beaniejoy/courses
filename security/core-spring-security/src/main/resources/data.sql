INSERT INTO account(id, username, password, email, age, role)
VALUES (1, 'user', '{bcrypt}$2a$10$8opGEe/7jAeubCZ6lvLAAuqsFCAT3/KPUTfMVCWMXa4cjN4DUlTxe', 'user@test.com', 29, 'ROLE_USER');

INSERT INTO account(id, username, password, email, age, role)
VALUES (2, 'manager', '{bcrypt}$2a$10$8opGEe/7jAeubCZ6lvLAAuqsFCAT3/KPUTfMVCWMXa4cjN4DUlTxe', 'manager@test.com', 31, 'ROLE_MANAGER');

INSERT INTO account(id, username, password, email, age, role)
VALUES (3, 'admin', '{bcrypt}$2a$10$8opGEe/7jAeubCZ6lvLAAuqsFCAT3/KPUTfMVCWMXa4cjN4DUlTxe', 'admin@test.com', 31, 'ROLE_ADMIN');