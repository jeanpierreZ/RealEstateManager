# RealEstateManager

Devenez un as de la gestion immobilière

OpenClassrooms Développeur d'application - Android 8ème projet

Enoncé :

Vous êtes sollicité pour rejoindre une grande agence new-yorkaise spécialisée dans la vente de biens d’exception : duplex, lofts, penthouse et manoirs. Le Board vous demande de développer une application mobile Android permettant aux agents de pouvoir accéder aux fiches des différents biens immobiliers depuis leur équipement mobile.

Amélioration des fonctionnalités existantes

Mode hors-ligne
Les agents immobiliers étant toujours en déplacement, parfois dans des zones non couvertes par un réseau cellulaire ou Wi-Fi, il est obligatoire que l’application fonctionne en mode hors-ligne. Il faut donc prévoir que l'ensemble des données sera stocké dans la base de données de l’application.

Les données devront être stockées dans une base de données SQLite. Les données devront être également accessibles en lecture en utilisant une couche d'abstraction du type ContentProvider.

Attributs d'un bien immobilier
Pour chaque bien immobilier, les informations suivantes doivent être disponibles :

Le type de bien (appartement, loft, manoir, etc) ;
Le prix du bien (en dollar) ;
La surface du bien (en m2) ;
Le nombre de pièces ;
La description complète du bien ;
Au moins une photo, avec une description associée. Vous devez gérer le cas où plusieurs photos sont présentes pour un bien ! La photo peut être récupérée depuis la galerie photos du téléphone, ou prise directement avec l'équipement ;
L’adresse du bien ;
Les points d’intérêts à proximité (école, commerces, parc, etc) ;
Le statut du bien (toujours disponible ou vendu) ;
La date d’entrée du bien sur le marché ;
La date de vente du bien, s’il a été vendu ;
L'agent immobilier en charge de ce bien.

Gestion des biens immobiliers
L’agent immobilier doit pouvoir créer un nouveau bien depuis l’application, en précisant tout ou partie des informations demandées.

Une fois l'ajout d'un bien correctement effectué, un message de notification doit apparaitre sur le téléphone de l'utilisateur afin de lui indiquer que tout s'est bien passé.

La géo-localisation d'un bien est automatiquement effectuée à partir de son adresse, afin d'afficher la vignette de carte correspondante dans le détail du bien. Pour ce faire, regardez du côté de la Static Maps API ou de la Lite Mode Maps Android API.

Les biens existants peuvent être édités pour mettre à jour leurs informations (ajout, modification, suppression).

Il n’est pas possible de supprimer un bien, en revanche il est possible de préciser qu’un bien a été vendu, en précisant obligatoirement sa date de vente.

Géo-localisation
Si l'agent immobilier est connecté et géo-localisable, il peut afficher les biens sur une carte, afin de voir d'un coup d'œil les biens les plus proches de lui. Cette carte est dynamique : l'agent peut zoomer, dézoomer, se déplacer, et afficher le détail d'un bien en cliquant sur la punaise correspondante.

Moteur de recherche
L’agent immobilier peut effectuer une recherche multi-critères sur l’ensemble des biens immobiliers de la base. Par exemple :

Afficher les appartements d’une surface comprise entre 200 et 300m2, proches d’une école et des commerces, mis sur le marché depuis moins d’une semaine ;
Afficher les maisons vendues au cours des trois derniers mois, dans le secteur de Long Island, avec au moins trois photos, pour un prix compris entre $1,500,000 et $2,000,000. 

Fonctionnalité complémentaire

Intégrer des vidéos dans les informations des biens

Icon launcher made by Freepik from www.flaticon.com.

Jean-Pierre Zingraff
