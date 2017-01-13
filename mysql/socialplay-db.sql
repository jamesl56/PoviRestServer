DROP TABLE IF EXISTS `parenting_settings`;
DROP TABLE IF EXISTS `parenting_reminder_schedule`;
DROP TABLE IF EXISTS `parenting_notification_schedule`;
DROP TABLE IF EXISTS `parenting_tips_history`;
DROP TABLE IF EXISTS `parenting_tips_comments`;
DROP TABLE IF EXISTS `parenting_tips`;
DROP TABLE IF EXISTS `parenting_resources`;
DROP TABLE IF EXISTS `gcm_registration`;
DROP TABLE IF EXISTS `events`;
DROP TABLE IF EXISTS `friends`;
DROP TABLE IF EXISTS `chat_invitation`;
DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `user_id` int(8) NOT NULL AUTO_INCREMENT,
  `user_hash` varchar(100) NOT NULL,
  `user_type` varchar(1) NOT NULL,
  `user_status` varchar(1) NOT NULL,
  `user_name` varchar(50) NOT NULL,
  `identity` varchar(50) NOT NULL,
  `lastaccesstime` bigint NOT NULL,
  PRIMARY KEY (`user_id`)
) 
AUTO_INCREMENT=1234567;
ALTER TABLE users ADD UNIQUE INDEX (user_hash);
insert into users (user_hash, user_type, user_status, user_name, identity, lastaccesstime) values ('zllo5/Rd6gzaZhKAUC3VSa4fDH/WD0EgvI0e99UuV1A=', 'p', 'y', 'povi test', '4089876543', NOW());

CREATE TABLE `friends` (
  `invitor_id` int(8) NOT NULL,
  `invitee_id` int(8) NOT NULL,
  PRIMARY KEY (`invitor_id`, `invitee_id`),
  CONSTRAINT `fk_invitor_id` FOREIGN KEY (`invitor_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_invitee_id` FOREIGN KEY (`invitee_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `chat_invitation` (
  `invitor_id` int(8) NOT NULL,
  `invitee_id` int(8) NOT NULL,
  `room_id` varchar(15) NOT NULL,
  `started` varchar(1) NOT NULL,
  `invite_time` bigint NOT NULL,
  PRIMARY KEY (`invitor_id`, `invitee_id`),
  CONSTRAINT `fk_chat_invitor_id` FOREIGN KEY (`invitor_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_chat_invitee_id` FOREIGN KEY (`invitee_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `events` (
  `event_id` int(8) NOT NULL AUTO_INCREMENT,
  `user_id` int(8) NOT NULL,
  `event_type` varchar(2) NOT NULL,
  `timestamp` bigint NOT NULL,
  `duration` int NOT NULL,
  `eventdetails` varchar(400) NOT NULL,
  PRIMARY KEY(`event_id`),
  CONSTRAINT `fk_events_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
)
AUTO_INCREMENT=10000001;
ALTER TABLE events ADD UNIQUE INDEX (timestamp, user_id);

CREATE TABLE `gcm_registration` (
  `user_id` int(8) NOT NULL,
  `registration_id` varchar(400) NOT NULL,
  `timestamp` bigint NOT NULL,
  PRIMARY KEY (`user_id`, `registration_id`),
  CONSTRAINT `fk_gcm_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `parenting_resources` (
  `resource_id` int NOT NULL AUTO_INCREMENT,
  `resource_url` varchar(200) NOT NULL,
  `resource_type` varchar(1) NOT NULL,
  `resource_status` varchar(1) NOT NULL,
  PRIMARY KEY (`resource_id`)
) 
AUTO_INCREMENT=1;
insert into parenting_resources (resource_url, resource_type, resource_status) values ('http://momastery.com/blog/2015/04/24/key-jar/', 'q', 'y');

CREATE TABLE `parenting_tips` (
  `resource_id` int NOT NULL,
  `tip_id` int NOT NULL,
  `content` varchar(400) NOT NULL,
  `tip_type` varchar(1) NOT NULL,
  `tip_status` varchar(1) NOT NULL,
  PRIMARY KEY (`resource_id`, `tip_id`),
  CONSTRAINT `fk_resource_id` FOREIGN KEY (`resource_id`) REFERENCES `parenting_resources` (`resource_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

insert into parenting_tips values (1, 1, 'What was your first thought when you work up today?', 'q', 'y');
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
insert into parenting_tips values (1, 21, 'What is something you sue every day that you don\'t need?', 'q', 'y');
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

CREATE TABLE `parenting_tips_comments` (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int(8) NOT NULL,
  `timestamp` bigint NOT NULL,
  `resource_id` int NOT NULL,
  `tip_id` int NOT NULL,
  `comments` varchar(400) NOT NULL,
  PRIMARY KEY (`comment_id`),
  CONSTRAINT `fk_comments_resource_id` FOREIGN KEY (`resource_id`) REFERENCES `parenting_resources` (`resource_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_comments_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
)
AUTO_INCREMENT=10001;

CREATE TABLE `parenting_tips_history` (
  `user_id` int(8) NOT NULL,
  `timestamp` bigint NOT NULL,
  `resource_id` int NOT NULL,
  `tip_id` int NOT NULL,
  PRIMARY KEY (`user_id`, `timestamp`),
  CONSTRAINT `fk_history_resource_id` FOREIGN KEY (`resource_id`) REFERENCES `parenting_resources` (`resource_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_history_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `parenting_notification_schedule` (
  `user_id` int(8) NOT NULL,
  `day` smallint NOT NULL,
  `schedule` time NOT NULL,
  PRIMARY KEY (`user_id`, `day`),
  CONSTRAINT `fk_schedule_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `parenting_reminder_schedule` (
  `user_id` int(8) NOT NULL,
  `schedule` time NOT NULL,
  PRIMARY KEY (`user_id`, `schedule`),
  CONSTRAINT `fk_reminder_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `parenting_settings` (
  `user_id` int(8) NOT NULL,
  `timeZone` varchar(10) NOT NULL,
  PRIMARY KEY (`user_id`, `timeZone`),
  CONSTRAINT `fk_settings_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
);
