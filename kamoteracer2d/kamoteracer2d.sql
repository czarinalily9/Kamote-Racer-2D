-- DROP DATABASE kamoteracer3d;
-- CREATE DATABASE kamoteracer2d;
USE kamoteracer2d;
CREATE TABLE leaderboard (
	user_id INT NOT NULL AUTO_INCREMENT,
    created_at DATE DEFAULT (CURRENT_DATE()),
    initial CHAR(25) NOT NULL,
    score INT NOT NULL,
    PRIMARY KEY(user_id)
);

INSERT INTO leaderboard (initial, score) VALUES ('C.S.', 2000);

SELECT * FROM leaderboard;

