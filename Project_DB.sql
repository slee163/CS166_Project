--Spencer Lee		Donovan O'Connor  
--slee163			docon001  
--861008681			861016751  
--Group 52 

DROP TABLE Users CASCADE;
DROP TABLE Work_Experience;
DROP TABLE Educational_Details;
DROP TABLE Message;
DROP TABLE Connection;

CREATE TABLE Users (
	userid			char(10)	UNIQUE NOT NULL PRIMARY KEY,
	password		char(10)	NOT NULL,
	email			char(50)	NOT NULL,
	name			char(50), 
	dateofbirth		date
	
);

CREATE TABLE Work_Experience (
	userid			char(10)	NOT NULL REFERENCES Users(userid) ON DELETE CASCADE, 
	company			char(50)	NOT NULL, 
	role			char(50)	NOT NULL, 
	location		char(50),
	startdate		date		NOT NULL, 
	enddate			date		NOT NULL, 
	
	PRIMARY KEY (userid, company, role, startdate)
);

CREATE TABLE Educational_Details (
	userid			char(10)	NOT NULL REFERENCES Users(userid) ON DELETE CASCADE, 
	institutionname	char(50)	NOT NULL,
	major			char(50)	NOT NULL, 
	degree			char(50)	NOT NULL, 
	startdate		date, 
	enddate			date, 
	
	PRIMARY KEY (userid, major, degree)
);

CREATE TABLE Message (
	messageid		int			UNIQUE NOT NULL PRIMARY KEY, 
	senderid		char(10)	DEFAULT '<DELETED>' NOT NULL REFERENCES Users(userid) ON DELETE SET DEFAULT, 
	receiverid		char(10)	DEFAULT '<DELETED>' NOT NULL REFERENCES Users(userid) ON DELETE SET DEFAULT, 
	contents		text		NOT NULL, 
	sendtime		timestamp	NOT NULL, 
	deletestatus	int, 
	status			char(30)	NOT NULL 
);

CREATE TABLE Connection (
	userid			char(10)	NOT NULL REFERENCES Users(userid) ON DELETE CASCADE, 
	connectionid	char(10)	NOT NULL REFERENCES Users(userid) ON DELETE CASCADE, 
	status			char(30)	NOT NULL,
	
	PRIMARY KEY (userid, connectionid)
);