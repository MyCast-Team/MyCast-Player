-- phpMyAdmin SQL Dump
-- version 4.1.14
-- http://www.phpmyadmin.net
--
-- Client :  127.0.0.1
-- Généré le :  Mar 19 Avril 2016 à 00:16
-- Version du serveur :  5.6.17
-- Version de PHP :  5.5.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";



--


-- --------------------------------------------------------

--
-- Structure de la table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=10 ;

--
-- Contenu de la table `user`
--

INSERT INTO `user` (`Id`) VALUES
(2),
(3),
(4),
(5),
(6),
(7),
(8);

-- --------------------------------------------------------

--
-- Structure de la table `userfilm`
--

CREATE TABLE IF NOT EXISTS `userfilm` (
  `idUser` int(11) NOT NULL,
  `idFilm` int(11) NOT NULL,
  `date` date NOT NULL,
  PRIMARY KEY (`idUser`,`idFilm`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Contenu de la table `userfilm`
--

INSERT INTO `userfilm` (`idUser`, `idFilm`, `date`) VALUES
(1, 1, '0000-00-00'),
(1, 2, '0000-00-00'),
(1, 6, '0000-00-00'),
(2, 1, '0000-00-00'),
(2, 3, '0000-00-00'),
(2, 5, '0000-00-00'),
(3, 1, '0000-00-00'),
(3, 4, '0000-00-00'),
(3, 5, '0000-00-00'),
(4, 5, '0000-00-00'),
(5, 1, '0000-00-00'),
(6, 3, '0000-00-00'),
(7, 6, '0000-00-00');

-- --------------------------------------------------------

--
-- Structure de la table `usermusique`
--

CREATE TABLE IF NOT EXISTS `usermusique` (
  `iduser` int(11) NOT NULL,
  `idmusique` int(11) NOT NULL,
  `date` date NOT NULL,
  PRIMARY KEY (`iduser`,`idmusique`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
