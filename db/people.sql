DROP TABLE IF EXISTS `ppl_people`;

CREATE TABLE `ppl_people` (
  `id` varchar(32) NOT NULL,
  `version` bigint(20) NOT NULL DEFAULT 1,
  `creation_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_updated` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_active` bit(1) NOT NULL DEFAULT b'0',
  `gender` varchar(8) NOT NULL,
  `nickname` varchar(256) NOT NULL,
  `date_of_birth` date NULL,
  `first_name` varchar(128) NOT NULL,
  `last_name` varchar(128) NOT NULL,
  `height_in_cm` decimal(8,2) NULL,
  `weight_in_kg` decimal(8,4) NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `ppl_people`
ADD INDEX `ix_ppl_people_nickname` (`nickname` ASC);

ALTER TABLE `ppl_people`
ADD INDEX `ix_ppl_people_date_of_birth` (`date_of_birth` ASC);

ALTER TABLE `ppl_people`
ADD INDEX `ix_ppl_people_is_active` (`is_active` ASC);
