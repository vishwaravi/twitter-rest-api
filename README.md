# Twitter backend
This project is a Twitter-like backend application built using Spring Boot and Maven. It provides RESTful APIs to perform various Twitter-like functionalities such as posting tweets, following/unfollowing users, and fetching timelines.

#### This project has Two Branches :
* the main branch uses local db and stores images in project folder
* the another branch built used cloud db to store images and data 

## Technologies Used
### Backend :
* Java (version : JDK 21 LTS)
* Spring Boot (version 3.4.2)
* Dependencies
    * Spring Web
    * Spring Security
    * Spring Data Jpa
    * MySQL Driver
    * Lombok
    * JJWT (JWT Authentication)
### Database :
- Maria DB

- ℹ️ Note : 
    if you are using MySQL or PostGres. Change the Driver Dependency in pom.xml and Change Connection URL in .env Config.

### TESTING 
- [POSTMAN](https://www.postman.com/downloads/)
### Installation :

Step 1 : Clone the Repo. <br>
Step 2 : rename the ".env.example" file to ".env" <br>
Step 3 : create a Database.
<br>
Step 4 : Setup the Environment variables using .env file by Your db url and password<br> Example :
```javascript
DB_URL= "jdbc:mariadb://localhost:3306/DB_name"
DB_USERNAME= "username here"
DB_PASSWORD= "password here"
JWT_SECRET="enter the secret"
```
step 5 : Run the application


---

### IDE : [VSCode extension and Spring Tool Suite](https://spring.io/tools) , [IntelliJ](https://www.jetbrains.com/idea/download/?section=windows)

## Key Features :
### User :
- Creating Accounts
- Deleting Accounts
- Following Users
- Followers
### Content :
- Posts
- Tweets
- Likes
- comments
### other Featues
- Authentication and Authorization ( username and password )
- JWT Authentication and Authorization
### ER - Diagram
![Schema image](/schema-img/schema.png)
---
## DDL commands
### USER TABLE
```javascript
    CREATE TABLE users_table (
    id bigint NOT NULL AUTO_INCREMENT,
    user_id varchar(50) NOT NULL UNIQUE,
    user_name varchar(50) NOT NULL,
    user_dob date DEFAULT NULL,
    user_email varchar(50) NOT NULL,
    user_passwd varchar(100) NOT NULL,
    time_stamp varchar(255) DEFAULT NULL,
    user_pic varchar(255) DEFAULT NULL,
    banner_pic varchar(255) DEFAULT NULL,
    followers bigint DEFAULT NULL,
    following bigint DEFAULT NULL,
    PRIMARY KEY (id)
);
```
---
### TWEET TABLE
```javascript
    CREATE TABLE tweets_table (
    tweet_id bigint NOT NULL AUTO_INCREMENT,
    user_id varchar(255) DEFAULT NULL,
    tweet_filepath varchar(255) DEFAULT NULL,
    hashtags varchar(255) DEFAULT NULL,
    time_stamp varchar(255) DEFAULT NULL,
    tweet_content varchar(255) DEFAULT NULL,
    likes_count bigint DEFAULT NULL,
    PRIMARY KEY (tweet_id)
);
```
---
### COMMENTS TABLE
```javascript
    CREATE TABLE comments_table (
    id bigint NOT NULL AUTO_INCREMENT,
    comment_content varchar(255) DEFAULT NULL,
    time_stamp varchar(255) DEFAULT NULL,
    tweet_id bigint DEFAULT NULL,
    user_id varchar(255) DEFAULT NULL,
    PRIMARY KEY (id)
);
```
---
### LIKES TABLE
```javascript
    CREATE TABLE likes_table (
    id bigint NOT NULL AUTO_INCREMENT,
    liked_by varchar(255) DEFAULT NULL,
    tweet_id bigint DEFAULT NULL,
    time_stamp varchar(255) DEFAULT NULL,
    PRIMARY KEY (id)
);
```
---
### FOLLOWERS TABLE
```javascript
    CREATE TABLE followers_table (
    id bigint NOT NULL AUTO_INCREMENT,
    followed_by varchar(255) DEFAULT NULL,
    time_stamp varchar(255) DEFAULT NULL,
    user_id varchar(255) DEFAULT NULL,
    PRIMARY KEY (id)
);
```
---
### FOLLOWING TABLE
```javascript
    CREATE TABLE following_table (
    id bigint NOT NULL AUTO_INCREMENT,
    following varchar(255) DEFAULT NULL,
    time_stamp varchar(255) DEFAULT NULL,
    user_id varchar(255) DEFAULT NULL,
    PRIMARY KEY (id)
);
```

## API ENDPOINTS
### USER
**POST** - `localhost:8080/register` - *REGISTER USER* <br>
**GET** - `localhost:8080/{userId}` - *GET USER DETAILS*<br>
**DELETE** - `localhost:8080/{userId}` - *DELETE USER* <br>
**PUT** - `localhost:8080/{userId}/follow` - *FOLLOW USER*<br>
**PUT** - `localhost:8080/{userId}/unfollow` - *UNFOLLOW USER*<br>

### TWEET 
**GET** - `localhost:8080/home` - *FEED POSTS* <br>
**POST** - `localhost:8080/home` - *POST TWEET* <br>
**DELETE** - `localhost:8080/home/{tweetId}` - *DELETE TWEET* <br>
**PUT** - `localhost:8080/home/{tweetId}/comment` - *POST COMMENT* <br>
**DELETE** - `llocalhost:8080/home/{tweetId}/comment` - *DELETE COMMENT* <br>
**PUT** - `localhost:8080/home/{tweetId}/like` - *LIKE POST* <br>
**PUT** - `localhost:8080/home/{tweetId}/dislike` - *DISLIKE POST* <br>
