-- password: 1111
INSERT INTO account(id, username, password, email, age)
VALUES (1, 'user', '{bcrypt}$2a$10$8opGEe/7jAeubCZ6lvLAAuqsFCAT3/KPUTfMVCWMXa4cjN4DUlTxe', 'user@test.com', 29);

INSERT INTO account(id, username, password, email, age)
VALUES (2, 'manager', '{bcrypt}$2a$10$8opGEe/7jAeubCZ6lvLAAuqsFCAT3/KPUTfMVCWMXa4cjN4DUlTxe', 'manager@test.com', 31);

INSERT INTO account(id, username, password, email, age)
VALUES (3, 'admin', '{bcrypt}$2a$10$8opGEe/7jAeubCZ6lvLAAuqsFCAT3/KPUTfMVCWMXa4cjN4DUlTxe', 'admin@test.com', 31);

INSERT INTO role(role_id, role_name, role_desc)
VALUES (1, 'ROLE_ADMIN', '관리자');

INSERT INTO resources(resource_id, resource_name, http_method, order_num, resource_type)
VALUES (1, '/admin/**', '', 1, 'url');

INSERT INTO role_resources(role_id, resource_id)
VALUES (1, 1);

INSERT INTO account_roles(account_id, role_id)
VALUES (3, 1);
