# Guide de Contribution
Bonjour,
Merci de bien vouloir contribuer au projet LO23 Kanban. Avant de pouvoir coder, voici quelques règles à respecter.

## Avant de Commencer

1. Assurez-vous d’avoir un compte GitHub.
2. Lisez le [README.md](./README.md) pour comprendre le contexte, les objectifs, la structure et les conventions du projet.
3. Vérifiez bien les **issues** ouvertes pour éviter les doublons.

## Ecrire le nom d'une branche

Il y a 3 types de branches sur le projet, elles respectent la casse kebab-case.

Les branches V# sont des branches pour les différentes version du projet.

Les branches de features suivent les branches de version et s'écrivent featuretype/featurename.

Les branches de modules suivent les branches de features et s'écrivent featurename-modulename.

A l'intérieur de sa branche, le module s'organise lui même en respectant les conventions de nommage.

Ce nommage semble créer une redondance mais permet un affichage pratique que ce soit dans l'IDE ou dans les PR.

Types de features:
-feature : ajout d'une nouvelle fonctionnalité
-hotfix : résolution d'un problème critique pour le fonctionnement
-docs : documentation
-refactor : changement dans la structure ou le code interne sans ajout de fonctionnalité

Par exemple: V0 -> hotfix/voir-kanban -> voir-kanban-data

## Ecrire le nom d'un commit

Les commits consistent en des messages décrivant le contenu. Nous demandons juste d'ajouter un préfix entre crochets pour une lecture rapide des changements.

Type de préfixes:
- [+] : pour une addition
- [-] : pour une deletion
- [~] : pour un changement
- [fix] : pour une correction

  Par exemple: [+] Ajout d'un bouton pour supprimer le kanban

## Ecrire une PR

Les PR utilisent la forme

Titre (contenant qui, quoi et ou)

Description (optionnel)

Utiliser pull_request_templace.md et compléter afin d'avoir des PR propres.

### Merge module -> feature

Nous demandons à ce qu'un reviewer approuve le merge

### Merge feature -> version

Nous demandons à ce que 2 reviewers approuvent le merge.

### Merge version -> main (Intégrations)

Les merges de versions sont sujets à des **Intégrations**. Ces intégrations consistent à choisir une team de développeur avec une personne par module. Cette équipe tournera à chaque version.

Ces intégrations on lieu après la deadline de version et permmettront de faire des retouches et des corrections sur cette dernière.

Ainsi nous considèrons que le travail de ces 4 personnes constitue en soit une review suffisante pour approuver le merge.

## Qualite du code

