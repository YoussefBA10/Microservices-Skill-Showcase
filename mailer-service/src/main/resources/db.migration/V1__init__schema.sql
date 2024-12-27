-- Migration V1: Create user and user_roles tables
CREATE TABLE Mail (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      user_id INT NOT NULL,
                      to_person VARCHAR(255),
                      username VARCHAR(255),
                      email_template_name VARCHAR(255),
                      confirmation_url VARCHAR(255),
                      activation_code VARCHAR(255),
                      subject VARCHAR(255),
                      sent_date TIMESTAMP,
                      status VARCHAR(255),
                      FOREIGN KEY (user_id) REFERENCES User(id)
);
