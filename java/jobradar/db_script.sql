CREATE DATABASE `job_radar`;

USE `job_radar`;

/*Table structure for table `ats_platform` */

DROP TABLE IF EXISTS `ats_platform`;

CREATE TABLE `ats_platform` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `base_url_pattern` varchar(255) DEFAULT NULL,
  `job_path_pattern` varchar(255) DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

/*Table structure for table `company` */

DROP TABLE IF EXISTS `company`;

CREATE TABLE `company` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(150) NOT NULL,
  `career_page_url` varchar(500) DEFAULT NULL,
  `industry` varchar(100) DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_company_active` (`active`)
);

/*Table structure for table `company_ats` */

DROP TABLE IF EXISTS `company_ats`;

CREATE TABLE `company_ats` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `company_id` bigint NOT NULL,
  `ats_platform_id` bigint NOT NULL,
  `ats_job_url` varchar(500) NOT NULL,
  `active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `company_id` (`company_id`),
  KEY `ats_platform_id` (`ats_platform_id`),
  KEY `idx_company_ats_active` (`active`),
  CONSTRAINT `company_ats_ibfk_1` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`),
  CONSTRAINT `company_ats_ibfk_2` FOREIGN KEY (`ats_platform_id`) REFERENCES `ats_platform` (`id`)
);

/*Table structure for table `job_analysis` */

DROP TABLE IF EXISTS `job_analysis`;

CREATE TABLE `job_analysis` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `job_id` bigint NOT NULL,
  `match_score` decimal(5,4) DEFAULT NULL,
  `extracted_skills` text,
  `experience_range` varchar(50) DEFAULT NULL,
  `signals` text,
  `analyzed_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_analysis_job` (`job_id`),
  CONSTRAINT `job_analysis_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `job_posting` (`id`)
);

/*Table structure for table `job_posting` */

DROP TABLE IF EXISTS `job_posting`;

CREATE TABLE `job_posting` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `company_id` bigint NOT NULL,
  `ats_platform_id` bigint NOT NULL,
  `job_title` varchar(300) DEFAULT NULL,
  `job_url` varchar(600) DEFAULT NULL,
  `job_description` longtext,
  `location` varchar(200) DEFAULT NULL,
  `posted_date` date DEFAULT NULL,
  `first_seen_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `last_seen_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `active` tinyint(1) DEFAULT '1',
  `last_recommended_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `job_url` (`job_url`),
  KEY `ats_platform_id` (`ats_platform_id`),
  KEY `idx_job_company` (`company_id`),
  KEY `idx_job_active` (`active`),
  KEY `idx_job_last_seen` (`last_seen_at`),
  CONSTRAINT `job_posting_ibfk_1` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`),
  CONSTRAINT `job_posting_ibfk_2` FOREIGN KEY (`ats_platform_id`) REFERENCES `ats_platform` (`id`)
);

/*Table structure for table `target_role` */

DROP TABLE IF EXISTS `target_role`;

CREATE TABLE `target_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(150) NOT NULL,
  `min_experience` int DEFAULT NULL,
  `max_experience` int DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

/*Table structure for table `target_skill` */

DROP TABLE IF EXISTS `target_skill`;

CREATE TABLE `target_skill` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `skill_name` varchar(150) NOT NULL,
  `weight` decimal(3,2) DEFAULT '1.00',
  `active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);
