-- Script de création de base de données pour 'paymybuddy'

-- Désactivation des contraintes pour une création de table en douceur
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- Création du schéma de base de données
CREATE SCHEMA IF NOT EXISTS `paymybuddy` DEFAULT CHARACTER SET utf8mb4 ;
USE `paymybuddy`;

-- Création de la table 'user'
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `nom` VARCHAR(255) NOT NULL,
  `prenom` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `enabled` BIT(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB;

-- Création de la table 'bank_account'
CREATE TABLE IF NOT EXISTS `bank_account` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `balance` DECIMAL(19,4) NOT NULL,
  `rib` VARCHAR(255) NOT NULL UNIQUE,
  `user_id` BIGINT NOT NULL,
  `nom` VARCHAR(255) DEFAULT NULL,
  `prenom` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB;

-- Création de la table 'friend'
CREATE TABLE IF NOT EXISTS `friend` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `friend_email` VARCHAR(255) DEFAULT NULL,
  `user_id` BIGINT DEFAULT NULL,
  `friend_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  FOREIGN KEY (`friend_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB;

-- Création de la table 'pay_my_buddy_account'
CREATE TABLE IF NOT EXISTS `pay_my_buddy_account` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `balance` DECIMAL(19,4) NOT NULL,
  `user_id` BIGINT NOT NULL UNIQUE,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB;

-- Création de la table 'transaction'
CREATE TABLE IF NOT EXISTS `transaction` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `amount` DECIMAL(19,4) NOT NULL,
  `date` DATETIME NOT NULL,
  `description` VARCHAR(255),
  `recipient_id` BIGINT NOT NULL,
  `sender_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`recipient_id`) REFERENCES `user` (`id`),
  FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB;

-- Création de la table 'transaction_history'
CREATE TABLE IF NOT EXISTS `transaction_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `amount` DECIMAL(19,4) NOT NULL,
  `date` DATETIME NOT NULL,
  `description` VARCHAR(255),
  `recipient_id` BIGINT NOT NULL,
  `sender_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`recipient_id`) REFERENCES `user` (`id`),
  FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB;

-- Création de la table 'transfer_history'
CREATE TABLE IF NOT EXISTS `transfer_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `amount` DECIMAL(19,4),
  `from_account` VARCHAR(255),
  `to_account` VARCHAR(255),
  `transfer_date` DATETIME,
  `user_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB;

-- Rétablissement des paramètres initiaux
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;