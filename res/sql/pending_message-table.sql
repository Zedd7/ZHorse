CREATE TABLE IF NOT EXISTS prefix_pending_message
(
	uuid VARCHAR(36) NOT NULL,
	date DATETIME(3) NOT NULL,
	message TEXT NOT NULL,
	
	PRIMARY KEY (uuid, date),
	
	CONSTRAINT fk_pending_message_player
		FOREIGN KEY (uuid)
		REFERENCES prefix_player (uuid)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
);