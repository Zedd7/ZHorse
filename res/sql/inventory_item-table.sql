CREATE TABLE IF NOT EXISTS prefix_inventory_item
(
	uuid VARCHAR(36) NOT NULL,
	slot INT NOT NULL,
	data TEXT NOT NULL,

	PRIMARY KEY (uuid, slot)
);