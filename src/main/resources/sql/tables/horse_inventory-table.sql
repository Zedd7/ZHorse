CREATE TABLE IF NOT EXISTS prefix_horse_inventory
(
	uuid VARCHAR(36) NOT NULL,
	serial TEXT NOT NULL,

	PRIMARY KEY (uuid),

	CONSTRAINT fk_horse_inventory_horse
		FOREIGN KEY (uuid)
		REFERENCES prefix_horse (uuid)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
);