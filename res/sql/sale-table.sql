CREATE TABLE IF NOT EXISTS prefix_sale
(
	uuid VARCHAR(36) NOT NULL,
	price INT NOT NULL,
	
	PRIMARY KEY (uuid),
	
	CONSTRAINT fk_sale_horse
		FOREIGN KEY (uuid)
		REFERENCES prefix_horse (uuid)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
);