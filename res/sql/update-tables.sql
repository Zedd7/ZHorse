CREATE TABLE IF NOT EXISTS player
(
	uuid VARCHAR(36) NOT NULL,
	name VARCHAR(16) NOT NULL,
	language VARCHAR(16) NOT NULL,
	favorite INT NOT NULL,
	PRIMARY KEY (uuid)
);

CREATE TABLE IF NOT EXISTS horse
(
	uuid VARCHAR(36) NOT NULL,
	owner VARCHAR(36) NOT NULL,
	id INT NOT NULL,
	name VARCHAR(36) NOT NULL,
	locked INT NOT NULL,
	protected INT NOT NULL,
	shared INT NOT NULL,
	locationWorld VARCHAR(16) NOT NULL,
	locationX INT NOT NULL,
	locationY INT NOT NULL,
	locationZ INT NOT NULL,
	PRIMARY KEY (uuid),
	CONSTRAINT fk_horse_player
		FOREIGN KEY (owner)
		REFERENCES player (uuid)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
);