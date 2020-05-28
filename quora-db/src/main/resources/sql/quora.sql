
--USERS table is created to store the details of all the users
DROP TABLE IF EXISTS USERS CASCADE;
CREATE TABLE IF NOT EXISTS USERS(id SERIAL, uuid VARCHAR(200) NOT NULL ,firstName VARCHAR(30) NOT NULL , lastName VARCHAR(30) NOT NULL ,userName VARCHAR(30) UNIQUE NOT NULL,  email VARCHAR(50) UNIQUE NOT NULL ,password VARCHAR(255) NOT NULL, salt VARCHAR(200) NOT NULL ,country VARCHAR(30) ,aboutMe VARCHAR(50),dob VARCHAR(30), role VARCHAR(30),contactNumber VARCHAR(30), PRIMARY KEY (id));
INSERT INTO users(
	id, uuid, firstname, lastname, username, email, password, salt, country, aboutme, dob, role, contactnumber)
	VALUES (1024,'rdtrdtdyt','Abhi','Mahajan','abhi','a@gmail.com','507FF5FED1CAC746','8Xt6jxoCI3MWsVaKY/1ySAp2qzlb2Z7P89+vDrb1o6U=', 'India' ,'I am @ UpGrad' ,'22-10-1995' , 'admin' , '1222333333' );

--USER_AUTH table is created to store the login information of all the users
DROP TABLE IF EXISTS USER_AUTH CASCADE;
CREATE TABLE IF NOT EXISTS USER_AUTH(
	ID BIGSERIAL PRIMARY KEY,
	uuid VARCHAR(200) NOT NULL,
	USER_ID INTEGER NOT NULL,
	ACCESS_TOKEN VARCHAR(500) NOT NULL,
	EXPIRES_AT TIMESTAMP NOT NULL,
	LOGIN_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	LOGOUT_AT TIMESTAMP NULL
);

ALTER TABLE USER_AUTH ADD CONSTRAINT FK_USER_AUTH_USER_ID FOREIGN KEY(USER_ID) REFERENCES USERS(ID) ON DELETE CASCADE ;

--QUESTION table is created to store the questions related information posted by any user in the Application
DROP TABLE IF EXISTS QUESTION CASCADE;
CREATE TABLE IF NOT EXISTS QUESTION(id SERIAL,uuid VARCHAR(200) NOT NULL, content VARCHAR(500) NOT NULL, date TIMESTAMP NOT NULL , user_id INTEGER NOT NULL, PRIMARY KEY(id), FOREIGN KEY (user_id) REFERENCES USERS(id) ON DELETE CASCADE);


--ANSWER table is created to store the answers related information in reply to any question posted in the Application
DROP TABLE IF EXISTS ANSWER CASCADE;
CREATE TABLE IF NOT EXISTS ANSWER(id SERIAL,uuid VARCHAR(200) NOT NULL, ans VARCHAR(255) NOT NULL,date TIMESTAMP NOT NULL , user_id INTEGER NOT NULL, question_id INTEGER NOT NULL , PRIMARY KEY(id), FOREIGN KEY (user_id) REFERENCES USERS(id) ON DELETE CASCADE, FOREIGN KEY (question_id) REFERENCES QUESTION(id) ON DELETE CASCADE);
