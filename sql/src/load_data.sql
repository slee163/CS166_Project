/*
 * Donovan O’Connor
 * docon001 
 * 861016751
 * 
 * Spencer Lee 
 * slee163
 * 861008681
 *
 * Group #52
 */
COPY USR 
FROM 'CS166_Project/data/USR.txt' 
WITH DELIMITER E'\t';

COPY WORK_EXPR 
FROM 'CS166_Project/data/Work_Ex.txt' 
WITH DELIMITER E'\t';

COPY EDUCATIONAL_DETAILS 
FROM 'CS166_Project/data/Edu_Det.txt' 
WITH DELIMITER E'\t';

COPY MESSAGE 
FROM 'CS166_Project/data/Message.txt' 
WITH DELIMITER E'\t';

COPY CONNECTION_USR 
FROM 'CS166_Project/data/Connection.txt' 
WITH DELIMITER E'\t';