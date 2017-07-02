CREATE TABLE IF NOT EXISTS prefix_inventory_item
(
	uuid VARCHAR(36) NOT NULL,
	slot INT NOT NULL,
	data TEXT NOT NULL,

	PRIMARY KEY (uuid, slot),
	
	CONSTRAINT fk_inventory_item_horse
		FOREIGN KEY (uuid)
		REFERENCES prefix_horse (uuid)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
);