CREATE DATABASE  IF NOT EXISTS `povi_schema` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `povi_schema`;
-- MySQL dump 10.13  Distrib 5.6.23, for Win64 (x86_64)
--
-- Host: 192.168.1.2    Database: povi_schema
-- ------------------------------------------------------
-- Server version	5.6.24-0ubuntu2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

DROP TABLE IF EXISTS `parenting_tips_history`;
DROP TABLE IF EXISTS `parenting_tips`;
DROP TABLE IF EXISTS `parenting_resources`;

--
-- Table structure for table `children`
--
DROP TABLE IF EXISTS `children`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `children` (
  `user_id` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `gender` enum('male','female','unspecified') NOT NULL,
  `birthdate` date NOT NULL,
  `lastupdatetime` bigint NOT NULL,
  `localImageFile` VARCHAR(200),
  `remoteImageFile` VARCHAR(200),
  PRIMARY KEY (`user_id`,`name`),
  CONSTRAINT `fk_user_children` FOREIGN KEY (`user_id`) REFERENCES `users` (`email`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `children`
--

LOCK TABLES `children` WRITE;
/*!40000 ALTER TABLE `children` DISABLE KEYS */;
/*!40000 ALTER TABLE `children` ENABLE KEYS */;
UNLOCK TABLES;

-- -----------------------------------------------------
-- Table `comments`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `comments` ;

CREATE TABLE IF NOT EXISTS `comments` (
  `comment_id` int(8) NOT NULL AUTO_INCREMENT,
  `userId` VARCHAR(45) NOT NULL,
  `tipId` INT NOT NULL,
  `tipString` MEDIUMTEXT NOT NULL, 
  `timestamp` BIGINT NOT NULL,
  `commentText` MEDIUMTEXT NULL,
  `likeStatus` TINYINT(1) NULL,
  `childName` VARCHAR(45) NOT NULL,
  `localVoiceFile` VARCHAR(200), 
  `remoteVoiceFile` VARCHAR(200), 
  PRIMARY KEY (`comment_id`),
  CONSTRAINT `fk_userId`
    FOREIGN KEY (`userId` , `childName`)
    REFERENCES `children` (`user_id` , `name`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
AUTO_INCREMENT=10000001
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE INDEX `userId_idx` ON `comments` (`userId` ASC, `childName` ASC);
CREATE INDEX `timestamp_idx` ON `comments`(`timestamp` DESC);

/*!40101 SET character_set_client = @saved_cs_client */;
--
-- Dumping data for table `comments`
--

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
UNLOCK TABLES;


--
-- Table structure for table `sessions`
--

DROP TABLE IF EXISTS `sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sessions` (
  `user_id` varchar(45) NOT NULL,
  `token_povi` varchar(45) DEFAULT NULL,
  `token_fb` varchar(45) DEFAULT NULL,
  `login_time` date DEFAULT NULL,
  `lastupdatetime` bigint NOT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_user_session` FOREIGN KEY (`user_id`) REFERENCES `users` (`email`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sessions`
--

LOCK TABLES `sessions` WRITE;
/*!40000 ALTER TABLE `sessions` DISABLE KEYS */;
/*!40000 ALTER TABLE `sessions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tips`
--

DROP TABLE IF EXISTS `tips`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tips` (
  `idtips` int(11) NOT NULL,
  `tip` mediumtext NOT NULL,
  `category` enum('emotion','activity','friends') NOT NULL,
  `author` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idtips`),
  KEY `fk_tips_idx` (`author`),
  CONSTRAINT `fk_tips` FOREIGN KEY (`author`) REFERENCES `users` (`email`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tips`
--

LOCK TABLES `tips` WRITE;
/*!40000 ALTER TABLE `tips` DISABLE KEYS */;
/*!40000 ALTER TABLE `tips` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `email` varchar(45) NOT NULL,
  `hash` varchar(45) NOT NULL,
  `phone` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `address` varchar(45) DEFAULT NULL,
  `birthdate` date DEFAULT NULL,
  `lastupdatetime` bigint NOT NULL,
  `gender` enum('male','female','unspecified') NOT NULL,
  PRIMARY KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('fabio@email.com','2c4c1398f11ddd95c4d71ce993ad194eddeabd4d','555','Fabio',NULL,'1970-01-01'),('fh','314747b7ffea9f42e24ae3c5c512cb65e93eac5f','555','hj',NULL,'1970-01-01');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'povi_schema'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-05-12 12:26:55
-- tips related tables 
CREATE TABLE `parenting_resources` (
  `resource_id` int NOT NULL AUTO_INCREMENT,
  `resource_url` varchar(200) NOT NULL,
  `resource_type` varchar(1) NOT NULL,
  `resource_status` varchar(1) NOT NULL,
  PRIMARY KEY (`resource_id`)
)
AUTO_INCREMENT=1;
insert into parenting_resources (resource_url, resource_type, resource_status) values ('http://momastery.com/blog/2015/04/24/key-jar/', 'q', 'y');
insert into parenting_resources (resource_url, resource_type, resource_status) values ('http://www.povi.me', 'q', 'y');

CREATE TABLE `parenting_tips` (
  `resource_id` int NOT NULL,
  `tip_id` int NOT NULL,
  `content` varchar(400) NOT NULL,
  `tip_type` varchar(1) NOT NULL,
  `tip_status` varchar(1) NOT NULL,
  PRIMARY KEY (`resource_id`, `tip_id`),
  CONSTRAINT `fk_resource` FOREIGN KEY (`resource_id`) REFERENCES `parenting_resources` (`resource_id`) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `sample_answers` (
  `answer_id` int(8) NOT NULL AUTO_INCREMENT,
  `sample_answer` varchar(400) NOT NULL,
  `contributor_firstname` varchar(20) NOT NULL,
  `date` date NOT NULL,
  `resource_id` int NOT NULL,
  `tip_id` int NOT NULL,
  PRIMARY KEY(`answer_id`),
  CONSTRAINT `fk_sample_resource_id` FOREIGN KEY (`resource_id`) REFERENCES `parenting_resources` (`resource_id`) ON DELETE CASCADE ON UPDATE CASCADE
)AUTO_INCREMENT=100, ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into parenting_tips values (1, 1, 'What was your first thought when you woke up today?', 'q', 'y');
insert into parenting_tips values (1, 2, 'What are you most afraid of?', 'q', 'y');
insert into parenting_tips values (1, 3, 'What do you want to accomplish by your next birthday?', 'q', 'y');
insert into parenting_tips values (1, 4, 'If you could be famous for one thing, what would it be?', 'q', 'y');
insert into parenting_tips values (1, 5, 'What\'s your favorite word right now? Why?', 'q', 'y');
insert into parenting_tips values (1, 6, 'What do you love about yourself?', 'q', 'y');
insert into parenting_tips values (1, 7, 'What\'s something that is hard for you?', 'q', 'y');
insert into parenting_tips values (1, 8, 'Describe your perfect day.', 'q', 'y');
insert into parenting_tips values (1, 9, 'Who in your class is lonely?', 'q', 'y');
insert into parenting_tips values (1, 10, 'Who in your class is a leader?', 'q', 'y');
insert into parenting_tips values (1, 11, 'When is it hard being a friend?', 'q', 'y');
insert into parenting_tips values (1, 12, 'Who is somebody you\'d like to be friends with who isn\'t yet your friend?', 'q', 'y');
insert into parenting_tips values (1, 13, 'If you could switch places with one friend for a day, who would it be?', 'q', 'y');
insert into parenting_tips values (1, 14, 'How were you a helper today?', 'q', 'y');
insert into parenting_tips values (1, 15, 'What\'s the smartest thing you heard somebody say today?', 'q', 'y');
insert into parenting_tips values (1, 16, 'Who in your class makes you smile?', 'q', 'y');
insert into parenting_tips values (1, 17, 'What\'s the best thing about living here?', 'q', 'y');
insert into parenting_tips values (1, 18, 'How can you change the world?', 'q', 'y');
insert into parenting_tips values (1, 19, 'What\'s the biggest challenge facing our world today?', 'q', 'y');
insert into parenting_tips values (1, 20, 'If somebody from another planet came to Earth, what would he or she think of our world?', 'q', 'y');
insert into parenting_tips values (1, 21, 'What is something you use every day that you don\'t need?', 'q', 'y');
insert into parenting_tips values (1, 22, 'What would be the hardest thing about being blind?', 'q', 'y');
insert into parenting_tips values (1, 23, 'If you could give everybody in the world one piece of advice, what would you say?', 'q', 'y');
insert into parenting_tips values (1, 24, 'If you could time travel, where would you go? What would you change?', 'q', 'y');
insert into parenting_tips values (1, 25, 'What is something you know how to do that you could teach others?', 'q', 'y');
insert into parenting_tips values (1, 26, 'What will you be doing in 10 years?', 'q', 'y');
insert into parenting_tips values (1, 27, 'What\'s the most important choice you will have to make in your life?', 'q', 'y');
insert into parenting_tips values (1, 28, 'If you could only eat one food for an entire year, what would you choose?', 'q', 'y');
insert into parenting_tips values (1, 29, 'If you could have one superpower, what would it be?', 'q', 'y');
insert into parenting_tips values (1, 30, 'What is the best thing that\'s ever happened to you? What is the worst thing?', 'q', 'y');
insert into parenting_tips values (1, 31, 'If you had 3 wishes, what would they be?', 'q', 'y');
insert into parenting_tips values (1, 32, 'What are you the most proud of?', 'q', 'y');
insert into parenting_tips values (1, 33, 'Who in your class seems sad?', 'q', 'y');
insert into parenting_tips values (1, 34, 'Who do you admire? Why?', 'q', 'y');
insert into parenting_tips values (1, 35, 'What is something you\'ve always wanted to ask me?', 'q', 'y');
insert into parenting_tips values (1, 36, 'If you could switch places with one family member for a day, who would it be?', 'q', 'y');
insert into parenting_tips values (1, 37, 'What are the 3 most important qualities in a friend?', 'q', 'y');
insert into parenting_tips values (1, 38, 'What\'s the funniest thing somebody did or said today?', 'q', 'y');
insert into parenting_tips values (1, 39, 'Besides your teacher, who is somebody in your class you could learn from?', 'q', 'y');
insert into parenting_tips values (1, 40, 'Who in your class is special? Why?', 'q', 'y');
insert into parenting_tips values (1, 41, 'What is the most important job in the world?', 'q', 'y');
insert into parenting_tips values (1, 42, 'If you could create one law that everybody on Earth had to follow, what would it be?', 'q', 'y');
insert into parenting_tips values (1, 43, 'If you could go anywhere in the world to complete a good deed, where would you go and what would you do?', 'q', 'y');
insert into parenting_tips values (1, 44, 'What will the world be like in 10 years? What will be the same? What will be different?', 'q', 'y');
insert into parenting_tips values (1, 45, 'Is it possible to help somebody you\'ve never met? How?', 'q', 'y');
insert into parenting_tips values (1, 46, 'If you could live in another country for 1 year, where would you live?', 'q', 'y');
insert into parenting_tips values (1, 47, 'Is it better to have too much of something or not enough of something?', 'q', 'y');
insert into parenting_tips values (1, 48, 'Who is the most important person in the world?', 'q', 'y');

ALTER TABLE parenting_tips add column age_groups int(4);
ALTER TABLE parenting_tips add column category int(4);

insert into parenting_tips values (2, 1, 'What are you happy you did today?', 'q', 'y', 7, 1);
insert into parenting_tips values (2, 2, 'How are you silly?', 'q', 'y', 3, 1);
insert into parenting_tips values (2, 3, 'How are you different from your [friend, brother, sister, dad, mom]?', 'q', 'y', 7, 1);
insert into parenting_tips values (2, 4, 'Who is in your family? How are they related to you? ', 'q', 'y', 3, 1);
insert into parenting_tips values (2, 5, 'Who do you like being around? Why? What\'s the coolest thing you do with him/her?', 'q', 'y', 7, 1);
insert into parenting_tips values (2, 6, 'Who would you want to be most like when you grow up?', 'q', 'y', 3, 1);
insert into parenting_tips values (2, 7, 'What\'s something you have to work really hard for/at?', 'q', 'y', 2, 1);
insert into parenting_tips values (2, 8, 'What\'s something that comes easy for you but may not for other people?', 'q', 'y', 7, 1);
insert into parenting_tips values (2, 9, 'What is your favorite book right now? Why?', 'q', 'y', 2, 1);
insert into parenting_tips values (2, 10, 'What was your favorite thing to do at school today? Why?', 'q', 'y', 2, 1);
insert into parenting_tips values (2, 11, 'What is something that is easy  for other people but is hard for you?', 'q', 'y', 7, 1);
insert into parenting_tips values (2, 12, 'What are you most proud of?', 'q', 'y', 7, 1);
insert into parenting_tips values (2, 13, 'If there was anything in the world you could do or could be, what would it be?', 'q', 'y', 4, 1);
insert into parenting_tips values (2, 14, 'Why is it important to take care of yourself so that you can be good to others?', 'q', 'y', 4, 1);
insert into parenting_tips values (2, 15, 'What is really challenging for you at school? What would help it be easy for you?', 'q', 'y', 6, 1);
insert into parenting_tips values (2, 16, 'How comfortable are you telling someone about your limitations? When is it easy? When is it hard?', 'q', 'y', 4, 1);
insert into parenting_tips values (2, 17, 'Who would you like to share your toys with? Which toys? What would you do?', 'q', 'y', 1, 2);
insert into parenting_tips values (2, 18, 'When is it a good idea to share?', 'q', 'y', 1, 2);
insert into parenting_tips values (2, 19, 'What do people do when they\'re cold? How do we help people feel better when they\’re cold?’, 'q', 'y', 1, 2);
insert into parenting_tips values (2, 20, 'Who did you help today? Who did you do something nice for today?', 'q', 'y', 7, 2);
insert into parenting_tips values (2, 21, 'Why is it nice to give people compliments?', 'q', 'y', 7, 2);
insert into parenting_tips values (2, 22, 'Why do people get sad?, 'q', 'y', 2, 2);
insert into parenting_tips values (2, 23, 'What is something nice you did today?', 'q', 'y', 2, 2);
insert into parenting_tips values (2, 24, 'Why is it hard to share things sometimes? When is it easy for you to share things?', 'q', 'y', 3, 2);
insert into parenting_tips values (2, 25, 'How do you help people when they\'re sad?', 'q', 'y', 2, 2);
insert into parenting_tips values (2, 26, 'Would you do something nice for someone else even if it meant that you had to give up something you liked? What would it be?', 'q', 'y', 7, 2);
insert into parenting_tips values (2, 27, 'What\ makes people feel worse when they’re sad?', 'q', 'y', 2, 2);
insert into parenting_tips values (2, 28, 'How hard would it be for you sacrifice something you wanted for someone you loved? What would you sacrifice for the someone you loved?', 'q', 'y', 4, 2);
insert into parenting_tips values (2, 29, 'What\'s the nicest thing someone could do for someone else? Whats the nicest thing you\'ve done for someone else?', 'q', 'y', 4, 2);
insert into parenting_tips values (2, 30, 'Can you tell me about a time that you wish you could have done more for someone?', 'q', 'y', 4, 2);
insert into parenting_tips values (2, 31, 'How do you know when someone wants your advice vs when some one wants you to just listen/let them vent to you?', 'q', 'y', 4, 2);
insert into parenting_tips values (2, 32, 'Is there a time you wish someone would have reacted differently to something you did/said? What was it? Who was it?', 'q', 'y', 4, 2);
insert into parenting_tips values (2, 33, 'How does running really fast make you feel?', 'q', 'y', 1, 3);
insert into parenting_tips values (2, 34, 'How does hearing your favorite song make you feel?', 'q', 'y', 7, 3);
insert into parenting_tips values (2, 35, 'Why are sharks scary?', 'q', 'y', 3, 3);
insert into parenting_tips values (2, 36, 'If someone lost their favorite toy, what do you think they would do? How would they feel?', 'q', 'y', 3, 3);
insert into parenting_tips values (2, 37, 'How do you make yourself feel better when things don\'t go your way?', 'q', 'y', 7, 3);
insert into parenting_tips values (2, 38, 'What is something that\'s really scary?', 'q', 'y', 2, 3);
insert into parenting_tips values (2, 39, 'How can you tell when someone is sad/happy/upset/excited? What could have made them feel that way?', 'q', 'y', 2, 3);
insert into parenting_tips values (2, 40, 'How do you feel when you hear your favorite song?', 'q', 'y', 7, 3);
insert into parenting_tips values (2, 41, 'When was a time you expected something would make you happy/sad/angry/upset but it didn\'t?', 'q', 'y', 6, 3);
insert into parenting_tips values (2, 42, 'Why are dogs scary to some people but not to others?', 'q', 'y', 2, 3);
insert into parenting_tips values (2, 43, 'When/where have you seen people cry when they\'re happy? Cry when they\'re sad?', 'q', 'y', 6, 3);
insert into parenting_tips values (2, 44, 'Why do some people cry when they\'re happy AND when they\'re sad?', 'q', 'y', 6, 3);
insert into parenting_tips values (2, 45, 'Can you give an example of a situation when someone reacted in a way that you didn\'t expect or want them to?', 'q', 'y', 4, 3);
insert into parenting_tips values (2, 46, 'When was a time you expected something would make you happy but it didn\'t?', 'q', 'y', 4, 3);
insert into parenting_tips values (2, 47, 'Do you think there\'s an appropriate amount of time for someone to be sad when something bad happens to them? ', 'q', 'y', 4, 3);
insert into parenting_tips values (2, 48, 'What is the best way someone can show someone else that they care about them?', 'q','y',4, 3);
insert into parenting_tips values (2 ,49,'Who thinks you\'re silly?','q','y',1,4);
insert into parenting_tips values (2 ,50,'What is my (question asker\'s) favorite food?','q','y',1,4);
insert into parenting_tips values (2 ,51,'What do I (question asker) think is really fun for us to do?','q','y',1,4);
insert into parenting_tips values (2 ,52,'Who would be SO happy to see you if you surprised them? What would they do?','q','y',1,4);
insert into parenting_tips values (2 ,53,'What does it mean when someone yawns?','q','y',1,4);
insert into parenting_tips values (2 ,54,'What would grandpa/grandma/someone else think if they saw you right now?','q','y',2,4);
insert into parenting_tips values (2 ,55, 'Whose opinion do you care about? Why?','q','y',2,4);
insert into parenting_tips values (2 ,56,'What do you hope people say about you when describing you?','q','y',2,4);
insert into parenting_tips values (2 ,57,'Why do people like to play video games?','q','y',2,4);
insert into parenting_tips values (2 ,58,'Why are dogs scary to some people but not to others?','q','y',2,4);
insert into parenting_tips values (2 ,59,'Why do some people cry when they\'re happy AND when they\'re sad?','q','y',6,4);
insert into parenting_tips values (2 ,61,'What do you think young kids talk about?','q','y',4,4);
insert into parenting_tips values (2 ,62, 'Why do you think your parents/teachers/coaches make rules that you think are annoying?', 'q','y',4,4);
insert into parenting_tips values (2 ,63,'Can you think of a time when something happened and you and a friend reacted differently to it? Why do you think you each reacted that way?','q','y',4,4);
insert into parenting_tips values (2 ,64,'If you had to trade places with someone for a day, who would it be? Why?','q','y',4,4);
insert into parenting_tips values (2 ,65,'What is the happiest color?','q','y',1,5);
insert into parenting_tips values (2 ,66,'Where would you look if you lost your toys?','q','y',1,5);
insert into parenting_tips values (2 ,67,'Why are peaches fuzzy? Is that weird? What other fruit is really weird?','q','y',1,5);
insert into parenting_tips values (2 ,68,'What are clouds made of?','q','y',1,5);
insert into parenting_tips values (2 ,69, 'How do they make orange juice?','q','y',1,5);
insert into parenting_tips values (2 ,70, 'What\'s the difference between jam and jelly?','q','y',2,5);
insert into parenting_tips values (2 ,72,'What would happen if everyone had to smile all day every day?','q','y',2,5);
insert into parenting_tips values (2 ,73,'When is it hard to be a good listener?','q','y',2,5);
insert into parenting_tips values (2 ,74,'Why are dogs scary to some people but not to others?','q','y',2,5);
insert into parenting_tips values (2 ,75,'Why do some people cry when they\'re happy AND when they\'re sad?','q','y',6,5);
insert into parenting_tips values (2 ,77,'Why is it important to take care of yourself so that you can be good to others?','q','y',4,5);
insert into parenting_tips values (2 ,78,'If you were building a city, what would you want to make sure was built? Why?','q','y',4,5);
insert into parenting_tips values (2 ,79,'What is the first step in trying to make any kind of change (to yourself, to the world, to your routine, etc.?)','q','y',4,5);
insert into parenting_tips values (2 ,80,'What would the world be like without music? phones? cars? TV?','q','y',6,5);
insert into parenting_tips values (2 ,81,'What do you do to get ready for bed?','q','y',1,6);
insert into parenting_tips values (2 ,82,'Where do you store your toys?','q','y',1,6);
insert into parenting_tips values (2 ,83,'What is something fun we do on the weekend?','q','y',1,6);
insert into parenting_tips values (2 ,84,'What happens when you go to the doctor?','q','y',1,6);
insert into parenting_tips values (2 ,85,'When is it most important to be safe and listen?','q','y',1,6);
insert into parenting_tips values (2 ,86,'What\'s a typical school day for you like?','q','y',2,6);
insert into parenting_tips values (2 ,87,'Where do you usually play at recess?','q','y',2,6);
insert into parenting_tips values (2 ,88,'Why do we need to get to school on time and be prepared?','q','y',2,6);
insert into parenting_tips values (2 ,89,'What does your mom/dad/sibling do every day after school/work?','q','y',2,6);
insert into parenting_tips values (2 ,90,'What do you like most about field trips?','q','y',2,6);
insert into parenting_tips values (2 ,91,'Why  do people need to be quiet when someone is taking a test? ','q','y',2,6);
insert into parenting_tips values (2 ,92,'What is the point of having the same classes schedule every day?','q','y',6,6);
insert into parenting_tips values (2 ,93,'Do you like having structure at school or does it bore you? Would you rather have every day be different?','q','y',4,6);
insert into parenting_tips values (2 ,94,'Why do people seem to get excited when there is a break in a routine, like an assembly, rally, field trip, etc.?','q','y',4,6);
insert into parenting_tips values (2 ,95,'When are public displays of affection appropriate? When not?','q','y',4,6);
insert into parenting_tips values (2 ,96,'Why do parents get embarrassed when their children throw tantrums in public?','q','y',4,6);
insert into parenting_tips values (2 ,97,'Who is silly/happy/smart/nice/strong?','q','y',1,7);
insert into parenting_tips values (2 ,98,'What is your favorite thing  about your teacher?','q','y',1,7);
insert into parenting_tips values (2 ,99,'Who helps you when you need help?','q','y',1,7);
insert into parenting_tips values (2 ,100,'Who always seems grumpy? How can you tell?','q','y',1,7);
insert into parenting_tips values (2 ,101,'Which friend of yours is the most like you?','q','y',1,7);
insert into parenting_tips values (2 ,102,'Who is the smartest/funniest/happiest/strongest/nicest person you know?','q','y',2,7);
insert into parenting_tips values (2 ,103, 'Whose opinion do you care about?','q','y',2,7);
insert into parenting_tips values (2 ,104,'What is your favorite thing about your best friend?','q','y',2,7);
insert into parenting_tips values (2 ,105,'Why are some people really talkative and others aren\'t?','q','y',2,7); 
insert into parenting_tips values (2 ,106,'Why are some people sensitive/caring and others aren\'t?','q','y',2,7);
insert into parenting_tips values (2 ,107, 'Can you give me an example of someone who tries their best  even if they don\'t do well?','q','y',2,7);
insert into parenting_tips values (2 ,108,'Who do you consider popular? Why?','q','y',4,7);
insert into parenting_tips values (2 ,109,'Who do you think at your high school is nerdy or uncool? Why?','q','y',4,7);
insert into parenting_tips values (2 ,110,'Of your teachers who do you think cares the most about the students? Why?','q','y',4,7);
insert into parenting_tips values (2 ,111,'If you could think like someone else for a day, who would it be?','q','y',4,7);
insert into parenting_tips values (2 ,112,'What\'s the difference between people who have to try hard to get what they want and people who don\'t?','q','y',4,7);
insert into parenting_tips values (2 ,113,'Who did you play with today at school?','q','y',1,8);
insert into parenting_tips values (2 ,114,'What games did you play today at school?','q','y',1,8);
insert into parenting_tips values (2 ,115,'What movies do you watch with your mom and dad?','q','y',1,8);
insert into parenting_tips values (2 ,116,'What does the president do?','q','y',1,8);
insert into parenting_tips values (2 ,117,'Why do your mommy and daddy go to work? (Or; why do your mommy and daddy stay home while you go to school?)','q','y',1,8);
insert into parenting_tips values (2 ,118,'What would the world be like without music?phones? cars? TV?','q','y',6,8);
insert into parenting_tips values (2 ,119,'Where\'s the farthest place airplanes can fly to?','q','y',2,8);
insert into parenting_tips values (2 ,120,'Why is it warm here when it\'s cold in Australia? and vice versa?','q','y',2,8);
insert into parenting_tips values (2 ,121,'What is the hardest job in the world?','q','y',2,8);
insert into parenting_tips values (2 ,122,'Why is it weird to think of your teacher as having  a life outside of school?','q','y',6,8);
insert into parenting_tips values (2 ,123,'What is something everyone in the world has to do, no matter where they live?','q','y',2,8);
insert into parenting_tips values (2 ,124,'What are the benefits of learning in a class full of your peers rather than learning from the teacher in 1:1 sessions?','q','y',4,8);
insert into parenting_tips values (2 ,125,'What do you think you get out of reading books that you don\'t necessarily get from watching a movie or tv?','q','y',4,8);
insert into parenting_tips values (2 ,126,'What role does luck play in people\'s lives?','q','y',4,8);
insert into parenting_tips values (2 ,127,'Why do we learn about history in school? Why do people tell stories about the past?','q','y',4,8);
insert into parenting_tips values (2 ,128, 'Why is it awkward to talk about how much money people have, want, or don\'t have?','q','y',4,8);
insert into parenting_tips values (2 ,129,'What would you want to do if we could do anything at all today?','q','y',1,9);
insert into parenting_tips values (2 ,130,'Where would you want to go if we could go anywhere at all today?','q','y',1,9);
insert into parenting_tips values (2 ,131,'If babies could talk, what do you think they\'d say?','q','y',1,9);
insert into parenting_tips values (2 ,132,'What happens when you go to the doctor?','q','y',1,9);
insert into parenting_tips values (2 ,133,'If you had four legs what would you do?','q','y',3,9);
insert into parenting_tips values (2 ,134,'What do you think birds are singing about?','q','y',2,9);
insert into parenting_tips values (2 ,135,'If little babies could talk, what do you think they\'d say?','q','y',2,9);
insert into parenting_tips values (2 ,136,'Why are superheroes super?','q','y',3,9);
insert into parenting_tips values (2 ,137,'Would you ever want to fly a plane?','q','y',2,9);
insert into parenting_tips values (2 ,138,'What\'s at the end of a rainbow?','q','y',2,9);
insert into parenting_tips values (2 ,139,'If you had wings what would you do?','q','y',2,9);
insert into parenting_tips values (2 ,140,'If there was anything in the world you could do or could be, what would it be? Why?','q','y',6,9);
insert into parenting_tips values (2 ,141,'If you were building a city, what would you want to make sure was built? Why?','q','y',4,9);
insert into parenting_tips values (2 ,142,'Would you want to be able to predict the future ? Why or why not?','q','y',6,9);
insert into parenting_tips values (2 ,143,'If you could think like someone else for a day, who would it be?','q','y',4,9);
insert into parenting_tips values (2 ,144,'What is something you\'d invent to make your life easier?','q','y',4,9);

insert into parenting_tips values (2 ,145,'What do you think is a good way to show affection?', 'q', 'y', 4, 3);
insert into parenting_tips values (2 ,146,'What do your mommy and daddy do during the day when you are at school?', 'q', 'y', 1, 8);
insert into parenting_tips values (2 ,147,'What fruit do you think is really weird?','q','y',1,5);
insert into parenting_tips values (2 ,148,'Do you like being around talkative or quiet people?','q','y',2,7);

insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Peepee in the potty! Cleaned up toys!', 'Daphna', '2015-07-09', 2, 1);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I make funny faces', 'Daphna', '2015-07-09', 2, 2);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I am little', 'Daphna', '2015-07-09', 2, 3);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Tamir, Bilha, (grandma and grandpa)', 'Yuval', '2015-07-09', 2, 3);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I like Seth (on my baseball team)-- Cause he\'s my friend. "what\'s the coolest thing you do w/ seth? " play baseball [his mom asked him what\'s the fun thing he does w/ seth at camp, he proceeded to tell a story]', 'Yuval', '2015-07-09', 2, 5);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Firefighter; cause I want to put out fires', 'Yuval', '2015-07-09', 2, 6);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Being nice, to anyone','Yuval','2015-07-10',2,7);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('doing tricks in the water; its like you go underwater and you\'re like doing a handstand and instead you come up and you do a flip','Yuval','2015-07-10',2,8);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Pippy Longstocking; cause she\'s so crazy. (how is she crazy?) she said I\'ve always heard that egg yolks are good for your hair-- went on to talk about pippy longstocking','Yuval','2015-07-10',2,9);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Recess! I get to play with my friends','Daphna','2015-07-10',2,10);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Listening','Daphna','2015-07-10',2,11);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('That I got straight A\'s. That I was nice to my sister','Daphna','2015-07-10',2,12);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Hmm. Maybe an actor. It\'d be cool to be famous.','Daphna','2015-07-10',2,13);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('You need to have the resources to help other people. If you\'re too sad, or worried, then you can\'t think of others. Also no one else is going to look out for you. You have to look out for yourself.','Daphna','2015-07-10',2,14);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Writing papers','Daphna','2015-07-10',2,15);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I generally don\'t like to tell people about my limitations, but I\'d have to trust someone a lot','Daphna','2015-07-10',2,16);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Mommy!','Daphna','2015-07-10',2,17);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('When your mom and dad ask you to so they don\'t get mad at you','Daphna','2015-07-10',2,18);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('They go brrrrrrr and hold their hands like this [imitate hand gestures]','Daphna','2015-07-10',2,19);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('My teacher at school. She told me to clean up.','Daphna','2015-07-10',2,20);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('They are happy. It makes them feel good. [older kids-- may say it gives someone confidence, etc.]','Daphna','2015-07-10',2,21);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Cause people hurt their feelings','Yuval','2015-07-10',2,22);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Listening to my grandma and grandpa','Daphna','2015-07-10',2,23);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('cause you don\'t want to; I don\'t know','Yuval','2015-07-10',2,24);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('cheer them up; I apologize, even if I didn\'t make them sad','Yuval','2015-07-10',2,25);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I might; I might give it','Yuval','2015-07-10',2,26);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('to not apologize','Yuval','2015-07-10',2,27);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('It depends what it is but I\'d imagine pretty hard. I would sacrifice some thingsif it really meant a lot to others','Daphna','2015-07-10',2,28);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I think just listen to them, or give them a hug when they\'re sad. Something that shows that the person really understands the other personÉ maybe get them a gift that shows that they pay attention?','Daphna','2015-07-10',2,29);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('There was a girl in my class and she seemed to be struggling with her reading. I know she was embarrassed and other kids laughed at her in class. I wish I had said something to defend her.','Daphna','2015-07-10',2,30);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I don\'t know. I\'ve made that mistake a lot. I guess it depend show fast they\'re talking.','Daphna','2015-07-10',2,31);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Yes. You. Whenever I tell you that I haven\'t done my homework yet you freak out and want me to go do it right away. It\'ll get done. You need to trust me.','Daphna','2015-07-10',2,32);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('So tired! And my legs hurt','Daphna','2015-07-10',2,33);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Happy! I dance','Daphna','2015-07-10',2,34);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Cause they have sharp teeth; because they eat fish and fish are really yummy; they eat people','Yuval','2015-07-10',2,35);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('They would be sad and look for it and ask their mom.','Daphna','2015-07-10',2,36);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I take a deep breath. Then think of better things.','Daphna','2015-07-10',2,37);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('That\'s a good one. I think fires are. Yeah because they\'re dangerous- if you go in them the smoke will get all over you and you will get really realy sick and have to go to the hospital','Yuval','2015-07-10',2,38);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Somebody might be crying when they\'re sadÉ I bet someone made fun of them or was mean to them.','Daphna','2015-07-10',2,39);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I want to dance to it','Daphna','2015-07-10',2,40);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I thought that sitting by my friend in class would make me happy but it didn\'t really change anything','Daphna','2015-07-10',2,41);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I guess it depends if people know a nice dog or a mean dog, or if a dog has been mean to them before or bitten them or something','Daphna','2015-07-10',2,42);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Good one; for sad people it\'s because they hurt their feelings; and for happy people and when they\'re happy cry it\'s like they\'re so happy and they go like eh heh heh in a smiling face. In basketball I\'ve seen that once','Yuval','2015-07-10',2,43);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I don\'t know! Because they\'re feeling a lot of feelings and don\'t know what to do with them?','Daphna','2015-07-10',2,44);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I mean, I don\'t mean to but I think I judge them if I don\'t agree with their reaction. I guess ','Daphna','2015-07-10',2,45);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('When I got my favorite video game for my birthday. It was okay-- but it didn\'t make me feel as happy as I thought. I don\'t know.','Daphna','2015-07-10',2,46);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I think I get frustrated when people are sad for like,a year, over something really stupid. Sometimes I wish they could be stronger. It\'s okay to be sad about stuff for a week or a month or something.','Daphna','2015-07-10',2,47);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I think just listen to them, or give them a hug when they\'re sad. Something that shows that the person really understands them and listens to them. Getting them something special. ','Daphna','2015-07-10',2,48);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I do! ','Daphna','2015-07-10',2,49);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('umm, gummy bears','Daphna','2015-07-10',2,50);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('read bedtime stories','Daphna','2015-07-10',2,51);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('My grandma and grandpa. They\'d jump up and down and hug me','Daphna','2015-07-10',2,52);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('They\'re sleepy','Daphna','2015-07-10',2,53);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I think they\'d be happy that I did well on my test at school and that I\'m spending time talking to you','Daphna','2015-07-10',2,54);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('My best friend. I think she\'s super smart and she seems to be right about everything so I like it when she agrees with me.','Daphna','2015-07-10',2,55);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('That I\'m smart, funnyÉ caring. Nice. Helpful. Pretty!','Daphna','2015-07-10',2,56);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('They get bored and games have a lot of action. Plus they can compete with other people!','Daphna','2015-07-10',2,57);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I guess it depends if people know a nice dog or a mean dog, or if a dog has been mean to them before or bitten them or something','Daphna','2015-07-10',2,58);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Because they get confused and donÕt know how they\'re feeling.','Daphna','2015-07-10',2,59);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I don\'t know! Because they\'re feeling a lot of feelings and don\'t know what to do with them?','Daphna','2015-07-10',2,60);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Their toys. And their favorite tv shows. And Barney.','Daphna','2015-07-10',2,61);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('That I have to be home by a certain time whn I\'m out with my friends. Or that I have to call my parents all the time. Super annoying. But I think they want me to be safe. That\'s what they keep saying.','Daphna','2015-07-10',2,62);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('','Daphna','2015-07-10',2,63);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I\'d love to trade places with my favorite celebrity. They have so much money and so much attention! It must be nice.','Daphna','2015-07-10',2,64);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Red! It\'s my favorite.','Daphna','2015-07-10',2,65);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Under my bed. Or I\'d ask mom.','Daphna','2015-07-10',2,66);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('It\'s like their hair!','Daphna','2015-07-10',2,67);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('cotton candy!','Daphna','2015-07-10',2,68);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Oranges! It\'s squeezed very very hard','Daphna','2015-07-10',2,69);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I think jam and jelly are really similar. I think they taste pretty much the same. I think jam has some chunks of fruit in it. I don\'t know why they\'re called something different though.','Daphna','2015-07-10',2,70);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I helped my mom set the table for dinner. And I didn\'t yell at my brother!','Daphna','2015-07-10',2,71);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Their cheeks would get tired! Plus it would be hard to tell how people are feelingÉ they\'d never look sad or mad or anything. Maybe people would be nicer to each other?','Daphna','2015-07-10',2,72);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Sometimes when I\'m stressed out or I have something else on my mind I can\'t really pay attention to other people. Also when I\'m really really tired.','Daphna','2015-07-10',2,73);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I guess it depends if people know a nice dog or a mean dog, or if a dog has been mean to them before or bitten them or something','Daphna','2015-07-10',2,74);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I don\'t know! Because they\'re feeling a lot of feelings and don\'t know what to do with them?','Daphna','2015-07-10',2,75);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I don\'t know! Because they\'re feeling a lot of feelings and don\'t know what to do with them?','Daphna','2015-07-10',2,76);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('You have to be strong and be able to pay attention to them. It\' shard to do that when you\'ve got your own stuff going on. So it\'s okay to be selfish sometimes.','Daphna','2015-07-10',2,77);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I\'d make sure that a hospital was built so that if people got hurt there\'d be a place to take them.','Daphna','2015-07-10',2,78);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I think realizing and acknowledging that something needs to change. That\'s probably first. Knowing there\'s a problem makes it easier to try to change it.','Daphna','2015-07-10',2,79);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Boring. I donÕt know what people whould do all day. Talk? How do people fill their time?','Daphna','2015-07-10',2,80);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I brush my teeth. And mom and dad or my aunt tell me a story.','Daphna','2015-07-10',2,81);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Mom and Dad gave me these big boxes in the living room. I\'m not good at picking them up though.','Daphna','2015-07-10',2,82);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Mom makes yummy pancakes and I get to watch cartoons. Sometimes we go to the park!','Daphna','2015-07-10',2,83);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('They give me a lollipop. Sometimes I get a shot. And that hurts. I don\'t like it.','Daphna','2015-07-10',2,84);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('When crossing the strreet. Cars can hurt you. Also you shouldnÕt tlak to strangers. ','Daphna','2015-07-10',2,85);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I get there, we do circle time, we sing songs. Then we have to go to our desks, and we\'re not allowed to talk. Then after that is snack time. Then my mom comes to pick me up. ','Daphna','2015-07-10',2,86);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I play four-square on the black top with my friends.','Daphna','2015-07-10',2,87);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('So the teacher doesn\'t get mad. So we don\'t sound stupid when someone asks us questions.','Daphna','2015-07-10',2,88);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('My dad comes home and watches tv and helps clean up and make dinner. Then he takes a nap; falls asleep on the couch before bedtime.','Daphna','2015-07-10',2,89);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I like that we don\'t have to be at school for a day, and I get to hang out with my friends. It\'s also a way to learn super cool things in a different way rather than sitting in a class all day.','Daphna','2015-07-10',2,90);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('It can get really distracting. People need to focus when they\'re taking a test or when they\'re in church. It\'s also important to be respectful.','Daphna','2015-07-10',2,91);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I think we have a lot to learn before the school year is over','Yuval','2015-07-10',2,92);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I guess I like knowing what to expect at school every day- leaves nothing to the imagination, but it\'s nice to have different things happen toreak up the routine. Sometimes that makes the day go faster.','Daphna','2015-07-10',2,93);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('B ecause routines can get really super boring. It\'s tough doing the same things day in and day out. A break is nice. It\'s different.','Daphna','2015-07-10',2,94);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Um, I think affection is pretty private so I\'m not sure I ever think public displays of affection are appropriate. I guess if it comes across super genuine. But I don\'t want to see people like, making out, when I\'m trying to eat my lunch or something.','Daphna','2015-07-10',2,95);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I think they think other people are judging them. Like, thinking that they\'re bad parents or something, even if they can\'t help it.','Daphna','2015-07-10',2,96);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('My teacher is really nice. She makes us sing a lot.','Daphna','2015-07-10',2,97);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('She makes me laugh. She\'s funny.','Daphna','2015-07-10',2,98);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('My mom. She helps me tie my shoes and zip up my sweater.','Daphna','2015-07-10',2,99);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('The bus driver. She\'s always yelling at someone!','Daphna','2015-07-10',2,100);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Anna. She has brown hair like me, and laughs like me. And she likes apples!','Daphna','2015-07-10',2,101);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('When people donÕt\' listen to them, or when they want to do something and their parents won\'t let them. ','Daphna','2015-07-10',2,102);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('My best friend. I think she\'s super smart and she seems to be right about everything so I like it when she agrees with me.','Daphna','2015-07-10',2,103);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('She\'s really funny. She makes me laugh a lot. And, she listens to me when I\'m upset and gets mad at people for me.','Daphna','2015-07-10',2,104);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Some people are shy. Maybe others have made them feel stupid when they talk so they don\'t want to talk? I like being around talkative people because it\'s less awkward.','Daphna','2015-07-10',2,105);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I think some people are not patient and donÕt\' think that other people should be sad when they are, so they don\'t understand. ','Daphna','2015-07-10',2,106);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('There\'s a girl on the track team who is always running as fast as she can but never gets in first place.','Daphna','2015-07-10',2,107);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('The prom king. Everybody voted for him. And class preseident. Same thing. There are also some kids at school who act really cool, like they\'re too cool for us.','Daphna','2015-07-10',2,108);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('There\'s a kid in school who always raises his hand to ask questions. Sometimes it\'s like, can\'t you just let it go?','Daphna','2015-07-10',2,109);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Mrs. _____. She asks us our opinion and doesn\'t just make us do something like a dictator. She asks us our opinions, and laughs at our jokes. And listens.','Daphna','2015-07-10',2,110);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I think I\'d want to think like my teacher, so I culd understand why she does what she does. That way I could also know what she thinks of everyone in the class, and if she agrees with my perception as well.','Daphna','2015-07-10',2,111);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('People who don\'t have to try are just very lucky and very smart. People who have to try have a good work ethic though. And they are usually nier because they realize everyone has to work for something.','Daphna','2015-07-10',2,112);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('My friend Jamie. She\'s funny.','Daphna','2015-07-10',2,113);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I played with my cars today at school. With Jamie.','Daphna','2015-07-10',2,114);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('We see CARS and the Lion King.','Daphna','2015-07-10',2,115);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('The president tells everyone in the country what to do! He/she is the bss of everyone!','Daphna','2015-07-10',2,116);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('They need to buy me things! Also, they\'re smart!','Daphna','2015-07-10',2,117);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Weird. It\'d be super weird. People would have to talk to each other a lot. And it would take forever to get anywhere!','Daphna','2015-07-10',2,118);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Umm, the moon? Maybe Egypt or China.','Daphna','2015-07-10',2,119);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Something about the world being tilted on its axis, or something?','Daphna','2015-07-10',2,120);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Doctor. You don\'t want people to die! You have to help them stay alive.','Daphna','2015-07-10',2,121);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I only see her at school. I thought that\'s the only thing she does- she\'s my teacher!','Daphna','2015-07-10',2,122);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Eat','Daphna','2015-07-10',2,123);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('You can hear other people\'s thoughts, and sometimes they\'re different and more creative than your own. Makes you think differently.','Yuval','2015-07-10',2,124);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('You can imagine the worlds you\'re reading about however you want toÉ Nobody has made up what they think things look like for you. Also you learn how to write better.','Daphna','2015-07-10',2,125);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I don\'t really believe in luck. I think people control most things.','Daphna','2015-07-10',2,126);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('There are lots of patterns, and if we don\'t want to repeat patterns it\'s important to know what happened beforehand. Also it\'s super interesting to know where we came from or why we are the way we are.','Daphna','2015-07-10',2,127);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Money is associated with status in this country- so people/ who don\'t have enough are thought of as less than or something. Also people don\'t want to come off greedy. That\'s also not good.','Daphna','2015-07-10',2,128);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Ride the train. Go to the popcorn car wash.','Daphna','2015-07-10',2,129);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Ride the train. Go to the popcorn car wash.','Daphna','2015-07-10',2,130);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I\'m stinky. I\'m tired. I\'m hungry.','Daphna','2015-07-10',2,131);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('They give me a lollipop. Sometimes I get a shot. And that hurts. I don\'t like it.','Daphna','2015-07-10',2,132);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I\'d crawl really really fast all the time! And I\'d bark like a dog.','Daphna','2015-07-10',2,133);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('The weather. Maybe they\'re making plans to migrate or something.','Daphna','2015-07-10',2,134);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('why doesn\'t anybody understand me?','Daphna','2015-07-10',2,135);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('They can do things that other people can\'t. Like fly. That\'s pretty super. Or they\'re SUPER strong.','Daphna','2015-07-10',2,136);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Whoa, that would be cool. It would also be scary. Planes are so high up.  Plus a lot of people are trusting you not to mess up.','Daphna','2015-07-10',2,137);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Gold! Leprachauns!','Daphna','2015-07-10',2,138);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I\'d fly to go visit my family abroad, so I wouldnÕt have to wait at the airport or on an airplane. I\'d also fly to school and go on vacation to Hawaii.','Daphna','2015-07-10',2,139);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I\'d be president. That way I could help the most people and people would take me seriously. ','Daphna','2015-07-10',2,140);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I\'d make sure that a hospital was built so that if people got hurt there\'d be a place to take them.','Daphna','2015-07-10',2,141);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I think it would be good to be prepared for the future, so in that way it\'d be nice. But, I also think that it\'s nice for the future to be a surprise. That\'s a lot of where the excitement comes from.','Daphna','2015-07-10',2,142);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('I think I\'d want to think like my teacher, so I culd understand why she does what she does. That way I could also know what she thinks of everyone in the class, and if she agrees with my perception as well.','Daphna','2015-07-10',2,143);
insert into sample_answers (`sample_answer`, `contributor_firstname`, `date`, `resource_id`, `tip_id`) values ('Something that could do my homework and do chores for me! That way I could do things that I really love to do and not worry about stupid boring things.','Daphna','2015-07-10',2,144);

CREATE TABLE `parenting_tips_history` (
  `history_id` int(8) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(45) NOT NULL,
  `date` date NOT NULL,
  `resource_id` int NOT NULL,
  `tip_id` int NOT NULL,
  PRIMARY KEY(`history_id`),
  CONSTRAINT `uk_history` UNIQUE KEY (`user_id`, `date`, `resource_id`, `tip_id`),
  CONSTRAINT `fk_resource_id` FOREIGN KEY (`resource_id`) REFERENCES `parenting_resources` (`resource_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`email`) ON DELETE CASCADE ON UPDATE CASCADE
)AUTO_INCREMENT=10000001, ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table parenting_tips_history drop foreign key fk_resource_id, drop foreign key fk_user_id, drop  key uk_history;
alter table parenting_tips_history add CONSTRAINT `fk_resource_id` FOREIGN KEY (`resource_id`) REFERENCES `parenting_resources` (`resource_id`) ON DELETE CASCADE ON UPDATE CASCADE, add CONSTRAINT `fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`email`) ON DELETE CASCADE ON UPDATE CASCADE;

DROP TABLE IF EXISTS `events`;
CREATE TABLE `events` (
  `event_id` int(8) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(45) NOT NULL,
  `event_type` varchar(2) NOT NULL,
  `timestamp` bigint NOT NULL,
  `duration` int NOT NULL,
  `eventdetails` varchar(400) NOT NULL,
  PRIMARY KEY(`event_id`),
  CONSTRAINT `fk_event_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`email`) ON DELETE CASCADE ON UPDATE CASCADE
)
AUTO_INCREMENT=10000001, ENGINE=InnoDB DEFAULT CHARSET=utf8;
ALTER TABLE events ADD UNIQUE INDEX (timestamp, user_id, event_type);

DROP TABLE IF EXISTS `event_type_mappings`;
CREATE TABLE `event_type_mappings` (
  `short_type` varchar(2) NOT NULL,
  `full_type` varchar(20) NOT NULL
);
insert into event_type_mappings values ('CH', 'CHILD');
insert into event_type_mappings values ('CM', 'COMMENT');
insert into event_type_mappings values ('LN', 'LOGIN');
insert into event_type_mappings values ('LO', 'LOGOUT');
insert into event_type_mappings values ('RG', 'REGISTRATION');
insert into event_type_mappings values ('TP', 'TIPOFTHEDAY');

DROP TABLE IF EXISTS `parenting_tips_age_groups`;
CREATE TABLE `parenting_tips_age_groups` (
  `age_group_id` int(4) NOT NULL,
  `description` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into parenting_tips_age_groups values (1, 'Preschool');
insert into parenting_tips_age_groups values (2, '5 to 10 years');
insert into parenting_tips_age_groups values (4, 'Teen');

DROP TABLE IF EXISTS `parenting_tips_catogories`;
CREATE TABLE `parenting_tips_catogories` (
  `catogory_id` int(4) NOT NULL,
  `description` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into parenting_tips_catogories values (1, 'Self-esteem, self-concept');
insert into parenting_tips_catogories values (2, 'Altruism/empathy');
insert into parenting_tips_catogories values (3, 'Emotion recognition');
insert into parenting_tips_catogories values (4, 'Perspective Taking');
insert into parenting_tips_catogories values (5, 'Critical thinking');
insert into parenting_tips_catogories values (6, 'Routines/Social conventions');
insert into parenting_tips_catogories values (7, 'Perception of Others');
insert into parenting_tips_catogories values (8, 'Storytelling and making sense of the world');
insert into parenting_tips_catogories values (9, 'Imagination and Play/creativity');

create or replace view tip_comment_status as select tipId, count(*) as count, sum(likeStatus) as likeCount from comments where resourceId=2 group by tipId;
alter table comments add column resourceId int(11);

CREATE TABLE `weblinks` (
  `link_id` int(8) NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL,
  `link` varchar(400) NOT NULL,
  PRIMARY KEY(`link_id`)
)
AUTO_INCREMENT=100, ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into weblinks (date, link) values ('2015-07-13', 'https://jillkuzma.wordpress.com/perspective-taking-skills/');
insert into weblinks (date, link) values ('2015-07-13', 'https://www.psychologytoday.com/blog/the-parents-we-mean-be/201007/how-do-we-help-children-take-other-perspectives-conversation');
insert into weblinks (date, link) values ('2015-07-13', 'https://www.parentingforsocialchange.com/perspective-taking-html/');
insert into weblinks (date, link) values ('2015-07-13', 'http://www.medel.com/blog/5-strategies-develop-theory-of-mind-1/');
insert into weblinks (date, link) values ('2015-07-13', 'http://www.ahaparenting.com/BlogRetrieve.aspx?PostID=469929&A=SearchResult&SearchID=8762217&ObjectID=469929&ObjectType=55');
insert into weblinks (date, link) values ('2015-07-13', 'http://www.todaysparent.com/family/parenting/how-to-build-your-childs-self-esteem/');
insert into weblinks (date, link) values ('2015-07-13', 'https://www.psychologytoday.com/blog/our-gender-ourselves/201208/the-key-raising-confident-kids-stop-complimenting-them');
insert into weblinks (date, link) values ('2015-07-13', 'http://www.apa.org/monitor/dec06/kids.aspx');
insert into weblinks (date, link) values ('2015-07-13', 'http://drdavewalsh.com/posts/62');
insert into weblinks (date, link) values ('2015-07-13', 'http://www.parents.com/toddlers-preschoolers/development/behavioral/toddler-empathy/');
insert into weblinks (date, link) values ('2015-07-13', 'http://www.washingtonpost.com/news/parenting/wp/2014/07/18/are-you-raising-nice-kids-a-harvard-psychologist-gives-5-ways-to-raise-them-to-be-kind/');
insert into weblinks (date, link) values ('2015-07-13', 'http://www.huffingtonpost.com/dan-goleman/teach-kids-to-recognize-e_b_5724144.html');
insert into weblinks (date, link) values ('2015-07-13', 'http://well.blogs.nytimes.com/2012/12/10/understanding-how-children-develop-empathy/?_r=0');
insert into weblinks (date, link) values ('2015-07-13', 'http://www.npr.org/blogs/ed/2014/12/31/356187871/why-emotional-literacy-may-be-as-important-as-learning-the-a-b-c-s');
insert into weblinks (date, link) values ('2015-07-13', 'http://parenting.blogs.nytimes.com/2014/09/04/teaching-children-empathy/?_r=0');
insert into weblinks (date, link) values ('2015-07-13', 'http://ww2.kqed.org/mindshift/2015/04/13/the-benefits-of-helping-preschoolers-understand-and-discuss-their-emotions/');
insert into weblinks (date, link) values ('2015-07-13', 'http://www.ahaparenting.com/parenting-tools/family-life/structure-routhttp://www.hanen.org/Helpful-Info/Articles/Power-of-Using-Everyday-Routines.aspx');
insert into weblinks (date, link) values ('2015-07-13', 'http://www.pbs.org/wholechild/parents/little.html');
insert into weblinks (date, link) values ('2015-07-13', 'http://www.fathers.com/s12-championship-fathering/c53-modeling/a-fathers-example-kids-observe-everything/');
insert into weblinks (date, link) values ('2015-07-13', 'https://www.psychologytoday.com/blog/memory-medic/201110/teaching-children-think');
insert into weblinks (date, link) values ('2015-07-13', 'http://www.rootsofaction.com/critical-thinking-ways-to-improve-your-childs-mind-this-summer/');
insert into weblinks (date, link) values ('2015-07-13', 'http://neuronetlearning.com/blog/how-young-children-learn-from-examples/');
insert into weblinks (date, link) values ('2015-07-13', 'https://www.psychologytoday.com/blog/growing-friendships/201108/what-are-social-skills');
insert into weblinks (date, link) values ('2015-07-13', 'http://heartsforfamilies.org/blog/march-2014-interpersonal-skills.aspx');
insert into weblinks (date, link) values ('2015-07-13', 'http://www.scholastic.com/parents/resources/article/tell-me-story');
insert into weblinks (date, link) values ('2015-07-13', 'http://www.parenting.com/article/10-easy-ways-to-fire-your-childs-imagination-21354373');
insert into weblinks (date, link) values ('2015-07-13', 'http://drdavewalsh.com/posts/139');
insert into weblinks (date, link) values ('2015-07-13', 'http://childdevelopmentinfo.com/child-activities/storytelling-for-children/');
insert into weblinks (date, link) values ('2015-07-13', 'http://ccb.lis.illinois.edu/Projects/storytelling/lis506a_lbp/litreview.html');
insert into weblinks (date, link) values ('2015-07-13', 'http://www.pbs.org/wholechild/parents/play.html');
insert into weblinks (date, link) values ('2015-07-13', 'https://www.psychologytoday.com/blog/beautiful-minds/201203/the-need-pretend-play-in-child-development');
insert into weblinks (date, link) values ('2015-07-13', 'http://www.npr.org/templates/story/story.php?storyId=19212514');
insert into weblinks (date, link) values ('2015-07-13', 'http://www.oneperfectdayblog.net/2013/02/21/quotes-about-the-importance-of-play/');
insert into weblinks (date, link) values ('2015-07-13', 'http://www.pbs.org/wholechild/parents/play.html');
insert into weblinks (date, link) values ('2015-07-13', 'http://www.npr.org/sections/health-shots/2014/09/29/352455278/more-active-play-equals-better-thinking-skills-for-kids');
insert into weblinks (date, link) values ('2015-07-13', 'http://www.educationoasis.com/resources/Articles/good_times_play.htm');
insert into weblinks (date, link) values ('2015-07-13', 'http://www.wsj.com/news/articles/SB10001424052702303773704579270293660965768?mod=djemLifeStyle_h');
insert into weblinks (date, link) values ('2015-07-13', 'https://www.psychologytoday.com/blog/love-and-gratitude/201305/4-ways-children-learn-gratitude');
insert into weblinks (date, link) values ('2015-07-13', 'http://www.pbs.org/parents/special/article-ten-ways-raise-grateful-kid.html');

update parenting_tips set tip_status='n' where resource_id=1;

