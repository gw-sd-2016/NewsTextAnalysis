Create database HerNews;

use HerNews;

CREATE TABLE Nonprofit (
	nid INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(50) NOT NULL,
	webpage VARCHAR(50) NOT NULL,
	location VARCHAR(50) NOT NULL,
	PRIMARY KEY(nid)
);

CREATE TABLE Keyword (
	kid INT NOT NULL AUTO_INCREMENT,
	word VARCHAR(30) NOT NULL,
	PRIMARY KEY(kid)
);

CREATE TABLE Keyword_map (
	mid INT NOT NULL AUTO_INCREMENT,
	nonprofit_id INT NOT NULL,
	keyword_id INT NOT NULL,
	PRIMARY KEY(mid),
	FOREIGN KEY (nonprofit_id) REFERENCES Nonprofit (nid),
	FOREIGN KEY (keyword_id) REFERENCES Keyword (kid)
);