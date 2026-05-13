CREATE DATABASE IF NOT EXISTS reservas_eventos_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE reservas_eventos_db;

CREATE TABLE IF NOT EXISTS `user` (
                                      id_user INT AUTO_INCREMENT PRIMARY KEY,
                                      username VARCHAR(255) NOT NULL UNIQUE,
    firstname VARCHAR(255),
    lastname VARCHAR(255),
    age INT,
    password VARCHAR(255) NOT NULL,
    role ENUM('ROLE_USER', 'ROLE_ADMIN') DEFAULT 'ROLE_USER'
    );

CREATE TABLE IF NOT EXISTS events (
                                      id_event INT AUTO_INCREMENT PRIMARY KEY,
                                      title VARCHAR(255) NOT NULL,
    description TEXT NULL,
    event_date DATETIME NOT NULL,
    venue VARCHAR(255) NOT NULL,
    capacity INT NOT NULL,
    price_per_ticket DECIMAL(10,2) NOT NULL,
    CONSTRAINT chk_events_capacity CHECK (capacity > 0),
    CONSTRAINT chk_events_price CHECK (price_per_ticket >= 0)
    );

CREATE TABLE IF NOT EXISTS bookings (
                                        id_booking INT AUTO_INCREMENT PRIMARY KEY,
                                        event_id INT NOT NULL,
                                        user_id INT NOT NULL,
                                        quantity INT NOT NULL,
                                        total_amount DECIMAL(10,2),
    booking_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('CONFIRMED', 'CANCELLED') NOT NULL DEFAULT 'CONFIRMED',
    CONSTRAINT chk_bookings_quantity CHECK (quantity >= 1),
    CONSTRAINT fk_bookings_event FOREIGN KEY (event_id) REFERENCES events(id_event),
    CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES `user`(id_user)
    );
