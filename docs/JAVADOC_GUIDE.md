# Guide de Documentation JavaDoc

Ce guide explique comment documenter le projet avec JavaDoc.

## 📚 Génération de la JavaDoc

### Générer la documentation

Pour générer la documentation JavaDoc de tout le projet, utilisez la commande Maven suivante :

```bash
mvn javadoc:javadoc
```

La documentation sera générée dans le dossier `target/site/apidocs/`.

### Ouvrir la documentation

Après génération, ouvrez le fichier `target/site/apidocs/index.html` dans votre navigateur.

### Générer la JavaDoc pour un package spécifique

```bash
mvn javadoc:javadoc -Dsubpackages=client
```

## ✍️ Comment documenter votre code

### Structure d'un commentaire JavaDoc

```java
/**
 * Description courte de la classe/méthode/attribut.
 * 
 * Description détaillée si nécessaire (optionnel).
 * Peut s'étendre sur plusieurs lignes.
 * 
 * @param nomParam Description du paramètre
 * @return Description de la valeur de retour
 * @throws ExceptionType Description de l'exception
 * @since Version où cette fonctionnalité a été ajoutée
 * @author Nom de l'auteur
 * @see AutreClasse#autreMethode Lien vers autre documentation
 */
```

### Exemples de documentation

#### Documenter une classe

```java
/**
 * Représente un utilisateur du système Kanban.
 * 
 * Cette classe contient les informations de base d'un utilisateur
 * et permet de gérer ses accès aux différents kanbans.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 */
public class User {
    // ...
}
```

#### Documenter une méthode

```java
/**
 * Ajoute un nouvel utilisateur au système et synchronise ses kanbans.
 * 
 * Cette méthode ajoute l'utilisateur à la liste des utilisateurs connectés
 * et charge ses kanbans dans la mémoire du serveur si nécessaire.
 * 
 * @param user L'utilisateur à ajouter (ne doit pas être null)
 * @param clientKanbans Liste des kanbans du client à synchroniser (peut être null ou vide)
 * @throws NullPointerException si user est null
 */
public void addNewUser(LightUser user, List<LightKanban> clientKanbans) {
    // ...
}
```

#### Documenter un attribut

```java
/**
 * Map pour tracker les viewers par kanban.
 * 
 * Clé : UUID du kanban
 * Valeur : Association des utilisateurs sur ce kanban
 */
private Map<UUID, AssociationUsersOnKanban> kanbanViewersMap;
```

#### Documenter une interface

```java
/**
 * Interface pour les appels de communication vers la couche données serveur.
 * 
 * Cette interface définit les contrats pour toutes les opérations
 * de communication nécessitant l'accès aux données du serveur.
 */
public interface CommCallsDataServer {
    // ...
}
```

## 📋 Checklist de documentation

Pour chaque élément de code, documentez :

### Classes
- [ ] Description de la classe
- [ ] Responsabilités principales
- [ ] Exemples d'utilisation (si pertinent)
- [ ] `@author` (optionnel mais recommandé)
- [ ] `@version` (optionnel)
- [ ] `@since` (optionnel)

### Méthodes publiques
- [ ] Description de ce que fait la méthode
- [ ] `@param` pour chaque paramètre
- [ ] `@return` pour la valeur de retour (si applicable)
- [ ] `@throws` pour chaque exception possible
- [ ] Exemples d'utilisation (si complexe)

### Méthodes privées
- [ ] Description courte (moins détaillée que les méthodes publiques)

### Attributs
- [ ] Description de l'attribut
- [ ] Contraintes ou invariants importants

### Constructeurs
- [ ] Description du constructeur
- [ ] `@param` pour chaque paramètre
- [ ] `@throws` si applicable

## 🎯 Bonnes pratiques

1. **Soyez concis mais complet** : Donnez assez d'informations sans être verbeux
2. **Utilisez la troisième personne** : "Retourne la liste..." plutôt que "Retourne la liste..."
3. **Documentez le "quoi" et le "pourquoi"**, pas le "comment" (sauf si nécessaire)
4. **Mettez à jour la documentation** quand vous modifiez le code
5. **Utilisez `@see`** pour créer des liens vers des classes/méthodes liées
6. **Utilisez `{@code}`** pour du code inline : `{@code String}`
7. **Utilisez `{@link}`** pour créer des liens : `{@link #autreMethode()}`

## 🔧 Outils utiles

### Générer la JavaDoc en continu

Pour générer la JavaDoc automatiquement à chaque compilation :

```bash
mvn clean compile javadoc:javadoc
```

### Vérifier les erreurs de documentation

Maven peut être configuré pour échouer si la documentation est incomplète (actuellement désactivé dans le pom.xml avec `failOnError=false`).

## 📖 Ressources

- [Documentation officielle Oracle JavaDoc](https://www.oracle.com/java/technologies/javase/javadoc-tool.html)
- [Guide de style JavaDoc](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html)

