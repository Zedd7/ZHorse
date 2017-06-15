CREATE TABLE IF NOT EXISTS prefix_horse_death
(
	uuid VARCHAR(36) NOT NULL,
	date DATETIME(3) NOT NULL,
	
	PRIMARY KEY (uuid),
	
	CONSTRAINT fk_horse_death_horse
		FOREIGN KEY (uuid)
		REFERENCES prefix_horse (uuid)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
);