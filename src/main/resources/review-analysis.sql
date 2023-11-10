/* requêtes à faire passer dans adminer en se connectant sur le bon serveur > maria-db */

/* à gauche de l'écran > Requête SQL, rentrer la commande suivante */
CREATE DATABASE review_analysis;

/*
après avoir créé la table > review_analysis, aller dessus > Requête SQL,
rentrer les deux commandes suivantes
*/
CREATE TABLE client (
    id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    email VARCHAR(127) UNIQUE,
    phone VARCHAR(31),
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lastUpdate TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE review (
    id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    rating INTEGER,
    comment VARCHAR(255),
    client_id INTEGER,
    CONSTRAINT client_fk FOREIGN KEY (client_id) REFERENCES client(id)
);