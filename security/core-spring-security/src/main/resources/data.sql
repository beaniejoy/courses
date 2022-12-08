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
-- ROLE_ADMIN - /admin/**
INSERT INTO role_resources(role_id, resource_id)
VALUES (1, 1);

-- ROLE_ADMIN - /config
INSERT INTO role_resources(role_id, resource_id)
VALUES (1, 18);

-- ROLE_MANAGER - aop
INSERT INTO role_resources(role_id, resource_id)
VALUES (2, 2);

-- ROLE_MANAGER - aop
INSERT INTO role_resources(role_id, resource_id)
VALUES (2, 3);

-- ROLE_MANAGER - aop
INSERT INTO role_resources(role_id, resource_id)
VALUES (2, 4);

-- ROLE_MANAGER - /messages
INSERT INTO role_resources(role_id, resource_id)
VALUES (2, 17);

-- ROLE_USER - /user/**
INSERT INTO role_resources(role_id, resource_id)
VALUES (3, 5);

-- ROLE_USER - /mypage
INSERT INTO role_resources(role_id, resource_id)
VALUES (3, 16);

-- account_roles mapping table
-- admin - ROLE_ADMIN
INSERT INTO account_roles(account_id, role_id)
VALUES (3, 1);

-- manager - ROLE_MANAGER
INSERT INTO account_roles(account_id, role_id)
VALUES (2, 2);

-- user - ROLE_USER
INSERT INTO account_roles(account_id, role_id)
VALUES (1, 3);

INSERT INTO role_hierarchy(id, child_name, parent_name)
VALUES (20, 'ROLE_ADMIN', null);

INSERT INTO role_hierarchy(id, child_name, parent_name)
VALUES (21, 'ROLE_MANAGER', 'ROLE_ADMIN');

INSERT INTO role_hierarchy(id, child_name, parent_name)
VALUES (22, 'ROLE_USER', 'ROLE_MANAGER');

-- ip address authorization
INSERT INTO access_ip(ip_id, ip_address)
VALUES (10, '0:0:0:0:0:0:0:1');

INSERT INTO access_ip(ip_id, ip_address)
VALUES (11, '127.0.0.1');