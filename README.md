<h1 align="center">Scorpion</h1>
<div align="center">
    <img src="https://img.shields.io/github/repo-size/liamabyss/scorpion" alt="Size-badge"/>
    <a href="https://choosealicense.com/licenses/mit/"><img src="https://img.shields.io/github/license/liamabyss/scorpion" alt="License"/></a>
    <img alt="Downloads" src="https://img.shields.io/github/downloads/liamabyss/scorpion/total">
    <img alt="Downloads" src="https://img.shields.io/github/downloads/liamabyss/scorpion/latest/total">
</div>

# Présentation

Les étudiants de **Junia** utilisent la plateforme web **Aurion**, un progiciel développé par [**Auriga**](https://www.auriga.fr/) pour accéder à leurs plannings, notes, absences et autres informations. Dans son utilisation quotidienne, **Aurion** est peu ergonomique sur plusieurs aspects :

- Nécessité de se reconnecter régulièrement
- Chargements longs et intempestifs
- Site peu adapté au format smartphone, notamment pour le planning
- Inaccessibilité hors connexion

**Scorpion** est une application Android native qui a pour but de fournir une solution pratique à ces problèmes. Elle a été développée à l'origine par [Rémi Arbache](https://github.com/RemiArbache), [Paul Bucamp](https://github.com/LiamAbyss) et [Marc Caboche](https://github.com/ychixm).

Dans la mythologie grecque, **Orion** était un chasseur géant qui se vantait de pouvoir triompher de toutes les bêtes. Un seul animal aurait finalement vaincu **Orion** : le **Scorpion**.

# Fonctionnement

L'utilisateur doit, au lancement, renseigner son identifiant et son mot de passe qui seront stockés localement sur son smartphone. L'application se chargera ensuite d'interroger **Aurion** afin de récupérer les informations de l'utilisateur. Celui-ci sera alors redirigé vers son emploi du temps du jour. **Scorpion** se chargera de reconnecter l'utilisateur si besoin automatiquement.

Il pourra naviguer dans son emploi du temps par semaines. Une fois une semaine chargée, **Scorpion** se chargera de la stocker localement afin qu'elle soit accessible immédiatement y compris hors connexion. Cela aura pour effet de réduire drastiquement les temps de chargement, mais l'utilisateur pourra actualiser sa semaine lorsqu'il le souhaitera afin de mettre à jour son emploi du temps.

Un tiroir de navigation permet également d'accéder aux notes par ordre chronologique. Le stockage des notes en local ainsi que la possibilité de les trier autrement que par date arrivera dans une future mise à jour.

L'application se charge elle même de vérifier si elle est à jour : si elle ne l'est pas, une fenêtre apparaitra et vous proposera une description de la mise à jour et un bouton pour accéder à la dernière version de **Scorpion**.

# Téléchargement

[Releases GitHub](https://github.com/LiamAbyss/Scorpion/releases) -> **Assets** -> Télécharger **Scorpion_XXXXXX_XXXX.apk** et l'ouvrir

[Page personnelle](https://liamabyss.github.io/scorpion/)

Votre navigateur vous demandera sans doute une autorisation pour installer l'application, vous pouvez retirer cette autorisation après l'installation, mais vous devrez alors lui redonner l'autorisation à chaque mise à jour.

# Idées futures

## Général

- Ajout d'une page *Absences*
- Ajout d'une pages de paramètres : 
  - Thème graphique de l'application (Aujourd'hui aux anciennes couleurs de l'ISEN)
  - Choix de la langue
  - Fréquence d'actualisation
  - Autorisation de potentielles notifications

## Planning

- Accès au détail des cours
- Pré-chargement des semaines pour fluidifier l'application
- Amélioration de l'affichage, pour le moment très limité par **Aurion**

## Notes

- Stockage pour un accès instantané
- Tri croissant et décroissant selon les dates, notes et modules
- Barre de recherche