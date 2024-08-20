INSERT INTO roles
VALUES(1, 'ROLE_ADMIN');

INSERT INTO roles
VALUES(2, 'ROLE_USER');

INSERT INTO users (user_id, fullname, email, password)
VALUES('1', 'adminBro', 'admin@gmail.com', 'admin');

INSERT INTO users (user_id, fullname, email, password)
VALUES('2', 'userBro', 'user@gmail.com', 'user');

INSERT INTO user_roles
VALUES('1', 1);

INSERT INTO user_roles
VALUES('2', 2);

INSERT INTO user_roles
VALUES('2', 2);