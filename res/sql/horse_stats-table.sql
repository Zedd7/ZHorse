CREATE TABLE IF NOT EXISTS prefix_horse_stats
(
	uuid VARCHAR(36) NOT NULL,
	age INT NOT NULL,
	canBreed INT NOT NULL,
	canPickupItems INT NOT NULL,
	color VARCHAR(36) NULL,
	domestication INT NOT NULL,
	fireTicks INT NOT NULL,
	health DOUBLE NOT NULL,
	isCustomNameVisible INT NOT NULL,
	isGlowing INT NOT NULL,
	isTamed INT NOT NULL,
	jumpStrength DOUBLE NOT NULL,
	maxHealth DOUBLE NOT NULL,
	noDamageTicks INT NOT NULL,
	remainingAir INT NOT NULL,
	speed DOUBLE NOT NULL,
	strength INT NULL,
	style VARCHAR(36) NULL,
	ticksLived INT NOT NULL,
	type VARCHAR(36) NOT NULL,
	
	PRIMARY KEY (uuid)
);