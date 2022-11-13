-- account(password: 1111)
INSERT INTO account(id, username, password, email, age)
VALUES (1, 'user', '{bcrypt}$2a$10$8opGEe/7jAeubCZ6lvLAAuqsFCAT3/KPUTfMVCWMXa4cjN4DUlTxe', 'user@test.com', 29);

INSERT INTO account(id, username, password, email, age)
VALUES (2, 'manager', '{bcrypt}$2a$10$8opGEe/7jAeubCZ6lvLAAuqsFCAT3/KPUTfMVCWMXa4cjN4DUlTxe', 'manager@test.com', 31);

INSERT INTO account(id, username, password, email, age)
VALUES (3, 'admin', '{bcrypt}$2a$10$8opGEe/7jAeubCZ6lvLAAuqsFCAT3/KPUTfMVCWMXa4cjN4DUlTxe', 'admin@test.com', 31);

-- role
INSERT INTO role(role_id, role_name, role_desc)
VALUES (1, 'ROLE_ADMIN', '관리자');

INSERT INTO role(role_id, role_name, role_desc)
VALUES (2, 'ROLE_MANAGER', '매니저');

INSERT INTO role(role_id, role_name, role_desc)
VALUES (3, 'ROLE_USER', '회원');

-- resources
INSERT INTO resources(resource_id, resource_name, http_method, order_num, resource_type)
VALUES (1, '/admin/**', '', 1, 'url');

INSERT INTO resources(resource_id, resource_name, http_method, order_num, resource_type)
VALUES (2, 'io.security.corespringsecurity.aopsecurity.method.AopMethodService.methodTest', '', 2, 'method');

INSERT INTO resources(resource_id, resource_name, http_method, order_num, resource_type)
VALUES (3, 'io.security.corespringsecurity.aopsecurity.method.AopMethodService.innerCallMethodTest', '', 3, 'method');

INSERT INTO resources(resource_id, resource_name, http_method, order_num, resource_type)
VALUES (4, 'execution(* io.security.corespringsecurity.aopsecurity.pointcut.*Service.*(..))', '', 4, 'method');

INSERT INTO resources(resource_id, resource_name, http_method, order_num, resource_type)
VALUES (5, '/users/**', '', 5, 'url');

INSERT INTO resources(resource_id, resource_name, http_method, order_num, resource_type)
VALUES (16, '/mypage', '', 16, 'url');

INSERT INTO resources(resource_id, resource_name, http_method, order_num, resource_type)
VALUES (17, '/messages', '', 17, 'url');

INSERT INTO resources(resource_id, resource_name, http_method, order_num, resource_type)
VALUES (18, '/config', '', 18, 'url');

-- role_resources mapping table
INSERT INTO role_resources(role_id, resource_id)
VALUES (1, 1);

INSERT INTO role_resources(role_id, resource_id)
VALUES (1, 18);

INSERT INTO role_resources(role_id, resource_id)
VALUES (2, 2);

INSERT INTO role_resources(role_id, resource_id)
VALUES (2, 3);

INSERT INTO role_resources(role_id, resource_id)
VALUES (2, 4);

INSERT INTO role_resources(role_id, resource_id)
VALUES (2, 17);

INSERT INTO role_resources(role_id, resource_id)
VALUES (3, 5);

INSERT INTO role_resources(role_id, resource_id)
VALUES (3, 16);

-- account_roles mapping table
INSERT INTO account_roles(account_id, role_id)
VALUES (3, 1);

INSERT INTO account_roles(account_id, role_id)
VALUES (2, 2);

INSERT INTO account_roles(account_id, role_id)
VALUES (1, 3);
