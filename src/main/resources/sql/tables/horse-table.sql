CREATE TABLE IF NOT EXISTS prefix_horse
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
		REFERENCES prefix_player (uuid)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
);