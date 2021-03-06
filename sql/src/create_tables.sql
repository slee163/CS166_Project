/*
 * Donovan O�Connor
 * docon001 
 * 861016751
 * 
 * Spencer Lee 
 * slee163
 * 861008681
 *
 * Group #52
 */
DROP TABLE WORK_EXPR;
DROP TABLE EDUCATIONAL_DETAILS;
DROP TABLE MESSAGE;
DROP TABLE CONNECTION_USR;
DROP TABLE USR;


CREATE TABLE USR(
	userId 					varchar 		UNIQUE NOT NULL, 
	password 				varchar 		NOT NULL,
	email 					text 			NOT NULL,
	name 					char(50),
	dateOfBirth 			date,
	PRIMARY KEY(userId));

CREATE TABLE WORK_EXPR(
	userId 					varchar 		NOT NULL, 
	company 				char(50) 		NOT NULL, 
	role 					char(50) 		NOT NULL,
	location 				char(50),
	startDate 				date,
	endDate 				date,
	PRIMARY KEY(userId,company,role,startDate),
	FOREIGN KEY (userId) REFERENCES USR (userId) ON DELETE CASCADE);

CREATE TABLE EDUCATIONAL_DETAILS(
	userId 					varchar 		NOT NULL, 
	instituitionName 		char(50) 		NOT NULL, 
	major 					char(50) 		NOT NULL,
	degree 					char(50) 		NOT NULL,
	startdate 				date,
	enddate 				date,
	PRIMARY KEY(userId,major,degree),
	FOREIGN KEY (userId) REFERENCES USR (userId) ON DELETE CASCADE);

CREATE TABLE MESSAGE(
	msgId 					integer 		UNIQUE NOT NULL, 
	senderId 				varchar 		DEFAULT '<DELETED>' NOT NULL,
	receiverId 				varchar 		DEFAULT '<DELETED>' NOT NULL,
	contents 				char(500) 		NOT NULL,
	sendTime 				timestamp		DEFAULT CURRENT_TIMESTAMP,
	deleteStatus 			integer			DEFAULT 0,
	status 					char(30) 		NOT NULL,
	PRIMARY KEY(msgId),
	FOREIGN KEY (senderId) REFERENCES USR (userId) ON DELETE SET DEFAULT,
	FOREIGN KEY (receiverId) REFERENCES USR (userId) ON DELETE SET DEFAULT);

CREATE TABLE CONNECTION_USR(
	userId 					varchar 		NOT NULL, 
	connectionId 			varchar 		NOT NULL, 
	status 					char(30) 		DEFAULT 'Request'	NOT NULL ,
	PRIMARY KEY(userId,connectionId),
	FOREIGN KEY (userId) REFERENCES USR (userId) ON DELETE CASCADE,
	FOREIGN KEY (connectionId) REFERENCES USR (userId) ON DELETE CASCADE);