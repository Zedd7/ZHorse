CREATE TABLE IF NOT EXISTS prefix_inventory_item
(
	uuid VARCHAR(36) NOT NULL,
	position INT NOT NULL,
	amount INT NOT NULL,
	displayName VARCHAR(36) NULL,
	durability INT NOT NULL,
	enchantmentsFormatted VARCHAR(256) NULL,
	localizedName VARCHAR(36) NULL,
	loreFormatted VARCHAR(256) NULL,
	type VARCHAR(36) NOT NULL,
	unbreakable INT NULL,

	PRIMARY KEY (uuid, position)
);