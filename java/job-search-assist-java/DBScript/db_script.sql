CREATE DATABASE `job_assist_dev`;

USE `job_assist_dev`;

/*Table structure for table `ats_platform` */

DROP TABLE IF EXISTS `ats_platform`;

CREATE TABLE `ats_platform` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `base_url_pattern` VARCHAR(255) DEFAULT NULL,
  `job_path_pattern` VARCHAR(255) DEFAULT NULL,
  `active` TINYINT(1) DEFAULT '1',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

/*Table structure for table `company` */

DROP TABLE IF EXISTS `company`;

CREATE TABLE `company` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(150) UNIQUE NOT NULL,
  `industry` VARCHAR(100) DEFAULT NULL,
  `active` TINYINT(1) DEFAULT '1',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_company_active` (`active`)
);

/*Table structure for table `company_ats` */

DROP TABLE IF EXISTS `company_ats`;

CREATE TABLE `company_ats` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `company_id` BIGINT NOT NULL,
  `ats_platform_id` BIGINT NOT NULL,
  `ats_job_url` VARCHAR(500) NOT NULL,
  `active` TINYINT(1) DEFAULT '1',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `company_id` (`company_id`),
  KEY `ats_platform_id` (`ats_platform_id`),
  KEY `idx_company_ats_active` (`active`),
  CONSTRAINT `company_ats_ibfk_1` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`),
  CONSTRAINT `company_ats_ibfk_2` FOREIGN KEY (`ats_platform_id`) REFERENCES `ats_platform` (`id`)
);

/*Table structure for table `job_posting` */

DROP TABLE IF EXISTS `job_posting`;

CREATE TABLE `job_posting` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `company_id` BIGINT NOT NULL,
  `ats_platform_id` BIGINT NOT NULL,
  `job_title` VARCHAR(300) DEFAULT NULL,
  `job_url` VARCHAR(600) DEFAULT NULL,
  `job_description` LONGTEXT,
  `location` VARCHAR(200) DEFAULT NULL,
  `posted_date` DATE DEFAULT NULL,
  `first_seen_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `last_seen_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `active` TINYINT(1) DEFAULT '1',
  `last_recommended_at` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `job_url` (`job_url`),
  KEY `ats_platform_id` (`ats_platform_id`),
  KEY `idx_job_company` (`company_id`),
  KEY `idx_job_active` (`active`),
  KEY `idx_job_last_seen` (`last_seen_at`),
  CONSTRAINT `job_posting_ibfk_1` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`),
  CONSTRAINT `job_posting_ibfk_2` FOREIGN KEY (`ats_platform_id`) REFERENCES `ats_platform` (`id`)
);

/*Table structure for table `job_analysis` */

DROP TABLE IF EXISTS `job_analysis`;

CREATE TABLE `job_analysis` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `job_id` BIGINT NOT NULL,
  `match_score` DECIMAL(5,4) DEFAULT NULL,
  `extracted_skills` TEXT,
  `experience_range` VARCHAR(50) DEFAULT NULL,
  `signals` TEXT,
  `analyzed_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_analysis_job` (`job_id`),
  CONSTRAINT `job_analysis_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `job_posting` (`id`)
);

/*Table structure for table `target_role` */

DROP TABLE IF EXISTS `target_role`;

CREATE TABLE `target_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `role_name` VARCHAR(150) NOT NULL,
  `min_experience` INT DEFAULT NULL,
  `max_experience` INT DEFAULT NULL,
  `active` TINYINT(1) DEFAULT '1',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

/*Table structure for table `target_skill` */

DROP TABLE IF EXISTS `target_skill`;

CREATE TABLE `target_skill` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `skill_name` VARCHAR(150) NOT NULL,
  `weight` DECIMAL(3,2) DEFAULT '1.00',
  `active` TINYINT(1) DEFAULT '1',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);
