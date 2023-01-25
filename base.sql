CREATE SEQUENCE s_Admin INCREMENT BY 1 MINVALUE 1 NO MAXVALUE START WITH 1;

CREATE SEQUENCE s_Categorie INCREMENT BY 1 MINVALUE 1 NO MAXVALUE START WITH 1;

CREATE SEQUENCE s_CategorieDelete INCREMENT BY 1 MINVALUE 1 NO MAXVALUE START WITH 1;

CREATE SEQUENCE s_Client INCREMENT BY 1 MINVALUE 1 NO MAXVALUE START WITH 1;

CREATE SEQUENCE s_configuration INCREMENT BY 1 MINVALUE 1 NO MAXVALUE START WITH 1;

CREATE SEQUENCE s_DemandeRechargement INCREMENT BY 1 MINVALUE 1 NO MAXVALUE START WITH 1;

CREATE SEQUENCE s_Enchere INCREMENT BY 1 MINVALUE 1 NO MAXVALUE START WITH 1;

CREATE SEQUENCE s_Encherir INCREMENT BY 1 MINVALUE 1 NO MAXVALUE START WITH 1;

CREATE TABLE Admin (
  idAdmin varchar(10) NOT NULL,
  Login varchar(50) NOT NULL UNIQUE,
  Mdp text NOT NULL,
  PRIMARY KEY (idAdmin)
);

CREATE TABLE Categorie (
  idCategorie varchar(20) NOT NULL,
  Designation varchar(100) NOT NULL,
  PRIMARY KEY (idCategorie)
);

CREATE TABLE CategorieDelete (
  idCategorieDelete varchar(30) NOT NULL,
  idCategorie varchar(20) NOT NULL,
  PRIMARY KEY (idCategorieDelete)
);

CREATE TABLE Client (
  idClient varchar(50) NOT NULL,
  Nom varchar(50) NOT NULL,
  Email varchar(50) NOT NULL UNIQUE,
  Mdp text NOT NULL,
  Solde DOUBLE PRECISION NOT NULL DEFAULT 0,
  PRIMARY KEY (idClient)
);

CREATE TABLE Configuration (
  DureMin DOUBLE PRECISION NOT NULL,
  DureeMax DOUBLE PRECISION NOT NULL,
  idConfiguration varchar(30) NOT NULL,
  Commission DOUBLE PRECISION NOT NULL,
  PRIMARY KEY (idConfiguration)
);

CREATE TABLE DemandeRechargement (
  idDemandeRechargement varchar(30) NOT NULL,
  idClient varchar(50) NOT NULL,
  Montant DOUBLE PRECISION NOT NULL,
  Statut int4 NOT NULL DEFAULT 1,
  PRIMARY KEY (idDemandeRechargement)
);

CREATE TABLE Enchere (
  idEnchere varchar(10) NOT NULL,
  Nom varchar(50) NOT NULL,
  idCategorie varchar(20) NOT NULL,
  Duree DOUBLE PRECISION NOT NULL,
  PrixDepart DOUBLE PRECISION NOT NULL,
  Description text NOT NULL,
  idClient varchar(50) NOT NULL,
  Date timestamp DEFAULT current_timestamp NOT NULL,
  Commission DOUBLE PRECISION NOT NULL,
  PRIMARY KEY (idEnchere)
);

CREATE TABLE Encherir (
  idEncherir varchar(10) NOT NULL,
  idEnchere varchar(10) NOT NULL,
  idClient varchar(50) NOT NULL,
  Montant DOUBLE PRECISION NOT NULL,
  Date timestamp NOT NULL DEFAULT current_timestamp NOT NULL,
  PRIMARY KEY (idEncherir)
);

ALTER TABLE
  Enchere
ADD
  CONSTRAINT FKEnchere922758 FOREIGN KEY (idCategorie) REFERENCES Categorie (idCategorie);

ALTER TABLE
  Enchere
ADD
  CONSTRAINT FKEnchere462703 FOREIGN KEY (idClient) REFERENCES Client (idClient);

ALTER TABLE
  DemandeRechargement
ADD
  CONSTRAINT FKDemandeRec561574 FOREIGN KEY (idClient) REFERENCES Client (idClient);

ALTER TABLE
  Encherir
ADD
  CONSTRAINT FKEncherir77814 FOREIGN KEY (idEnchere) REFERENCES Enchere (idEnchere);

ALTER TABLE
  Encherir
ADD
  CONSTRAINT FKEncherir900186 FOREIGN KEY (idClient) REFERENCES Client (idClient);

ALTER TABLE
  CategorieDelete
ADD
  CONSTRAINT FKCategorieD952076 FOREIGN KEY (idCategorie) REFERENCES Categorie (idCategorie);

INSERT INTO Configuration VALUES (1,2,'Configuration_'||nextval('s_Configuration'),20);

CREATE OR REPLACE VIEW v_Categorie AS
  SELECT * FROM Categorie WHERE idCategorie NOT IN (SELECT idCategorie FROM CategorieDelete);


CREATE VIEW v_Rechargement AS 
  SELECT D.Montant,D.idDemandeRechargement,C.Nom,D.statut FROM DemandeRechargement D 
  JOIN Client C ON D.idClient=C.idClient;

/* top 3 categorie be mpanao enchere / chiffre d'affaire journalier / top 3 client mividy entana */
  CREATE OR REPLACE VIEW  v_CategorieTop3 AS 
    SELECT CAST(COUNT(E.idCategorie) as int) as nombre,C.Designation FROM Enchere E JOIN Categorie C ON E.idCategorie=C.idCategorie GROUP BY E.idCategorie,C.Designation ORDER BY nombre DESC LIMIT 3;

CREATE OR REPLACE VIEW v_EnchereFini AS
   SELECT * FROM Enchere E WHERE date+(duree * interval '1 day')::interval<current_timestamp;

CREATE OR REPLACE VIEW v_EnchereEnCours AS
  SELECT * FROM Enchere WHERE idEnchere NOT IN (SELECT idEnchere FROM v_EnchereFini);
  
CREATE OR REPLACE VIEW v_DernierEncherirEnchere AS  
  SELECT * FROM Encherir ORDER BY Date DESC LIMIT 1;
  
CREATE OR REPLACE VIEW v_EnchereFiniDetail AS 
  SELECT E.*,Er.idClient as idClientNividy,C.Nom as NomClient,Er.Montant FROM Enchere E
   JOIN Encherir Er ON E.idEnchere=Er.idEnchere
   JOIN Client C ON C.idClient=Er.idClient;

  CREATE OR REPLACE VIEW v_ClientTop3 AS 
  SELECT NomClient,CAST(COUNT(idClientNividy) as int) as nombre FROM v_EnchereFiniDetail GROUP BY  idClientNividy,NomClient ORDER BY nombre DESC LIMIT 3;    
