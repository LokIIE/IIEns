IIEns
=====

IIEns est l'application Android pour les élèves de l'ENSIIE. Elle permet d'avoir accès aux différents services disponibles sur le site des élèves de l'ENSIIE : http://www.iiens.net, en version mobile.
Elle est supportée par plusieurs scripts PHP côté serveur (qui ne sont pas présents sur ce repo).

[![Google Play](http://developer.android.com/images/brand/en_generic_rgb_wo_45.png)](https://play.google.com/store/apps/details?id=com.iiens.net)

#### Fonctionnalités

###### News

Affiche les news publiées sur iiens.net.
Pour éviter la consommation de data, les logos des auteurs sont stockés directement dans l'application.

###### Emploi du temps

Permet de consulter l'emploi du temps des élèves :
* selon la promotion ;
* selon la semaine voulue ;
* selon le groupe de td ;
* selon le groupe de commmunication ;
* selon les langues vivantes choisies ;
* selon les options choisies en 2è et 3è année.

Permet également d'ajouter les résultats de la recherche avec l'agenda installé sur l'appareil

###### Anniversaires

Pour ne plus jamais oublier de souhaiter un bon anniversaire ;)

###### Twitter

Basé sur l'API de Twitter, cette fonctionnalité permet de se tenir au courant de l'actualité de l'école sur Twitter ainsi que des personnes qui en parlent.

###### Notifications

Le système de notifications fonctionne sur [Google Cloud Messaging](https://developer.android.com/google/gcm/index.html).
L'application peut recevoir actuellement 4 types de notifications : 
* une nouvelle news a été publiée : entraine la mise à jour des news
* des personnes ont leur anniversaire le jour même
* un ou plusieurs partiels ont lieu la semaine d'après
* des évènements sont proposés le jour même par les clubs/assoces ou par le BdE

Les notifications sont envoyées : 
* dans l'heure qui suit pour les news ;
* le matin pour les anniversaires ou les events associatifs (s'il y en a) ;
* le lundi matin précédent le(s) partiel(s).

###### Stockage local

Comme tout appareil mobile, la batterie est limitée : il faut donc éviter au maximum l'utilisation de l'antenne 3G/4G ou du Wi-Fi car ils consomment beaucoup d'énergie.

L'application est donc dotée d'un "mode hors-ligne" : il consiste à stocker les données qui seront fréquemment utilisées et à les mettre à jour le moins souvent possible.
J'ai considéré les fichiers suivants comme les plus souvent accédés ou comme étant mises à jour rarement :
* les news (mise à jour entraînée par les notifications) ;
* les anniversaires publics, ie affichés sur http://www.iiens.net (mise à jour entraînée par les notifications) ;
* l'emploi du temps de la semaine (si l'utilisateur le consulte au moins une fois).

Les données sont stockées sous format JSON.

#### Côté serveur

Les scripts permettent de faire des requêtes dans les bases de données contenant les informations.

#### ENSIIE

L'ENSIIE (Ecole Nationale Supérieure de l'Informatique pour l'Industrie et l'Entreprise), anciennement IIE-CNAM, est une grande école d'ingénieurs généraliste en informatique créée en 1968 et comptant actuellement plus de 4000 diplômés.

Elle est présente :
* à d'Evry dans l'Essonne, où se trouvent près de 550 élèves ;
* et à Strasbourg, antenne relativement récente (créée en 2009) et acceuillant environ 80 élèves.

Le recrutement en 1ère année se fait sur le Concours Télécom INT pour les élèves de classes préparatoires et par Admission sur Titre pour les titulaires d’un DUT, d’un BTS, d’une licence ou d’un diplôme équivalent.
Le recrutement en 2e année se fait par Admission sur Titre pour les titulaires d’un Master 1 ou d’un diplôme équivalent.

Pour plus d'informations, rendez-vous sur le [site web de l'école](http://www.ensiie.fr)
