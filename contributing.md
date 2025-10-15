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
* feature : ajout d'une nouvelle fonctionnalité
* hotfix : résolution d'un problème critique pour le fonctionnement
* docs : documentation
* refactor : changement dans la structure ou le code interne sans ajout de fonctionnalité

Par exemple: V0 -> hotfix/voir-kanban -> voir-kanban-data

## Ecrire le nom d'un commit

Les commits consistent en des messages décrivant le contenu. Nous demandons juste d'ajouter un préfix entre crochets pour une lecture rapide des changements.

Type de préfixes:
* [+] : pour une addition
* [-] : pour une deletion
* [~] : pour un changement
* [fix] : pour une correction

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

ATTENTION A BIEN VERIFIER QUE SON CODE COMPILE BIEN SANS ERREUR AVANT DE PUSH UNE NOUVELLE VERSION D'UNE FEATURE

### Pré-commit Git (qualité locale)

Un hook **`pre-commit`** a été déployé pour tous les développeurs. Son rôle est de sécuriser et standardiser le code **avant chaque commit**.

#### Fonctionnalités du pré-commit
1. **Sécurité : détection de secrets**
   - Empêche de committer des données sensibles comme :
     - mots de passe (`password`, `passwd`)
     - tokens (`token`, `apikey`, `api_key`)
     - clés privées (`-----BEGIN PRIVATE KEY-----`)
     - clés AWS exposées (`AKIA...`)
   - Objectif : éviter toute fuite d’informations ou compromission de sécurité via Git.

2. **Formatage automatique du code Java**
   - Utilise **`google-java-format`** (style Google).
   - Garantit un code **uniforme entre développeurs**.
   - Supprime les conflits de style dans les Pull Requests.
   - Le code est **reformaté automatiquement avant commit**.

---

#### Exigences du linter (`google-java-format`)
Le formatter applique automatiquement les règles suivantes :
| Aspect | Règle appliquée |
|--------|------------------|
| Indentation | 2 espaces (pas de tabulation) |
| Longueur de ligne | gestion du retour à la ligne automatique |
| Accolades | accolade ouvrante en fin de ligne (`if (...) {`) |
| Imports | triés + pas d’imports `*` |
| Espaces | propresgardés autour des opérateurs (`a + b`) |
| Lisibilité | blocs de code uniformisés automatiquement |

Ce système ne nécessite **aucune installation locale**, car l’outil est intégré au projet (`tools/google-java-format.jar`).

---
Si nécessaire mettre à jour le pom.xml lorsque le maven sera créé (ajout de sonar)