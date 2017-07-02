CREATE TABLE IF NOT EXISTS prefix_horse_stable
(
	uuid VARCHAR(36) NOT NULL,
	locationWorld VARCHAR(16) NOT NULL,
	locationX INT NOT NULL,
	locationY INT NOT NULL,
	locationZ INT NOT NULL,
	
	PRIMARY KEY (uuid),
	
	CONSTRAINT fk_horse_stable_horse
		FOREIGN KEY (uuid)
		REFERENCES prefix_horse (uuid)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
);