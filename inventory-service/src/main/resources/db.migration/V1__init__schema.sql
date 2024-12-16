-- Migration V1: Create user and user_roles tables

CREATE TABLE user (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      firstname VARCHAR(255),
                      lastname VARCHAR(255),
                      username VARCHAR(255) UNIQUE NOT NULL,
                      email VARCHAR(255) UNIQUE NOT NULL,
                      password VARCHAR(255) NOT NULL,
                      accoutlocked BOOLEAN DEFAULT FALSE,
                      accountenable BOOLEAN DEFAULT TRUE,
                      enabled BOOLEAN DEFAULT TRUE,
                      createdDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      LastModified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                      lastTimeLoged TIMESTAMP
);

CREATE TABLE role (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE user_roles (
                            users_id INT,
                            roles_id INT,
                            PRIMARY KEY (users_id, roles_id),
                            FOREIGN KEY (users_id) REFERENCES user(id) ON DELETE CASCADE,
                            FOREIGN KEY (roles_id) REFERENCES role(id) ON DELETE CASCADE
);


CREATE INDEX idx_user_username ON user(username);
CREATE INDEX idx_user_email ON user(email);
