INSERT INTO permissions (name) values ('all');
INSERT INTO permissions (name) values ('create_project');
INSERT INTO permissions (name) values ('read_all_projects');
INSERT INTO permissions (name) values ('create_project_employee');
INSERT INTO permissions (name) values ('read_all_project_engineers');
INSERT INTO permissions (name) values ('read_all_engineer_projects');
INSERT INTO permissions (name) values ('update_job_description');
INSERT INTO permissions (name) values ('read_workers_not_employed');
INSERT INTO permissions (name) values ('read_all_manager_projects');
INSERT INTO permissions (name) values ('delete_project_employee');
INSERT INTO permissions (name) values ('read_all_users');
INSERT INTO permissions (name) values ('read_pending_users');
INSERT INTO permissions (name) values ('update_users_approval');
INSERT INTO permissions (name) values ('update_password');
INSERT INTO permissions (name) values ('create_skill');
INSERT INTO permissions (name) values ('create_cv');

INSERT INTO roles (name) values ('ADMIN');
INSERT INTO roles (name) values ('PROJECT_MANAGER');
INSERT INTO roles (name) values ('HR_MANAGER');
INSERT INTO roles (name) values ('ENGINEER');

-- INSERT INTO addresses (city, country, street, street_number, zip_code) values ('Admin','Admin','Admin','Admin','Admin');
-- INSERT INTO users (email, first_logged, name, password, phone_number, status, surname, address_id) values ('admin@gmail.com', false, 'Admin', '$2a$10$mnN40QSm0Tna6ldGdpv6uu8RoyXk0EEo29.qipXeixxKlnSdI700i', 'Admin', 3, 'Admin', 1);
--
-- INSERT INTO users_roles (users_id, roles_name) values (1, 'ADMIN');

-- Permission all for all roles
INSERT INTO permissions_roles (roles_name, permissions_id) values ('ADMIN', 1);
INSERT INTO permissions_roles (roles_name, permissions_id) values ('PROJECT_MANAGER', 1);
INSERT INTO permissions_roles (roles_name, permissions_id) values ('HR_MANAGER', 1);
INSERT INTO permissions_roles (roles_name, permissions_id) values ('ENGINEER', 1);


INSERT INTO permissions_roles (roles_name, permissions_id) values ('ADMIN', 2);
INSERT INTO permissions_roles (roles_name, permissions_id) values ('ADMIN', 3);
INSERT INTO permissions_roles (roles_name, permissions_id) values ('ADMIN', 4);
INSERT INTO permissions_roles (roles_name, permissions_id) values ('PROJECT_MANAGER', 4);
INSERT INTO permissions_roles (roles_name, permissions_id) values ('ADMIN', 5);
INSERT INTO permissions_roles (roles_name, permissions_id) values ('PROJECT_MANAGER', 5);
INSERT INTO permissions_roles (roles_name, permissions_id) values ('ENGINEER', 6);
INSERT INTO permissions_roles (roles_name, permissions_id) values ('ENGINEER', 7);
INSERT INTO permissions_roles (roles_name, permissions_id) values ('ADMIN', 8);
INSERT INTO permissions_roles (roles_name, permissions_id) values ('PROJECT_MANAGER', 8);
INSERT INTO permissions_roles (roles_name, permissions_id) values ('PROJECT_MANAGER', 9);
INSERT INTO permissions_roles (roles_name, permissions_id) values ('PROJECT_MANAGER', 10);
INSERT INTO permissions_roles (roles_name, permissions_id) values ('ADMIN', 11);
INSERT INTO permissions_roles (roles_name, permissions_id) values ('ADMIN', 12);
INSERT INTO permissions_roles (roles_name, permissions_id) values ('ADMIN', 13);
INSERT INTO permissions_roles (roles_name, permissions_id) values ('ADMIN', 14);
INSERT INTO permissions_roles (roles_name, permissions_id) values ('ENGINEER', 15);
INSERT INTO permissions_roles (roles_name, permissions_id) values ('ENGINEER', 16);





