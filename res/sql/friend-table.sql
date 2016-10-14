CREATE TABLE IF NOT EXISTS friend
(
	requester VARCHAR(36) NOT NULL,
	recipient VARCHAR(36) NOT NULL,
	PRIMARY KEY (requester, recipient),
	CONSTRAINT fk_friend_player_1
		FOREIGN KEY (requester)
		REFERENCES player (uuid)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	CONSTRAINT fk_friend_player_2
		FOREIGN KEY (recipient)
		REFERENCES player (uuid)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
);