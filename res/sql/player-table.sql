CREATE TABLE IF NOT EXISTS <prefix>player
(
	uuid VARCHAR(36) NOT NULL,
	name VARCHAR(16) NOT NULL,
	language VARCHAR(16) NOT NULL,
	favorite INT NOT NULL,
	PRIMARY KEY (uuid)
);