# Problèmes à Résoudre dans le Système Kanban

Basé sur l'analyse du flux des actions, voici les problèmes identifiés à résoudre, classés par priorité.

---

## 🔴 PROBLÈMES CRITIQUES (À résoudre en priorité)

### 1. ✅ **"ADD TASK" ne fonctionne pas** - RÉSOLU

**Symptôme** :
- Quand on clique sur "ADD TASK" dans le menu d'une colonne, rien ne se passe
- Le popup d'ajout de tâche ne s'affiche pas

**Cause Identifiée** :
- Dans `addPopupButton()`, `closeCurrentPopup()` est appelé immédiatement après `action.run()`
- Le popup du menu se ferme avant que le popup d'ajout de tâche ne puisse s'afficher
- Le positionnement utilise `menuLabel` qui n'est plus visible après la fermeture du popup

**Solution Appliquée** :
- Capture des bounds du `anchorNode` avant la fermeture du popup du menu
- Création de `showPopupNearNodeWithBounds()` pour utiliser des bounds pré-calculés
- Modification de `showAddTaskPopup()` pour capturer les bounds avant l'appel à `showGenericTaskPopup()`
- Suppression de l'appel à `closeCurrentPopup()` dans `addPopupButton()` (laissé à `showPopupNearNode()`)

### 2. ✅ **Menu "⋮" de la dernière colonne non cliquable**

**Symptôme** :
- Les "3 petits points" (menu) sur la dernière colonne ne sont pas cliquables
- Le clic ne déclenche pas l'ouverture du menu

**Cause Identifiée** :
- Le bouton "ADD COLUMN" (le "+") est positionné à droite dans un VBox avec `AnchorPane.rightAnchor="20.0"`
- Ce bouton est superposé sur la dernière colonne, masquant le menu "⋮"
- Le ScrollPane avait `rightAnchor="0.0"` ce qui le faisait prendre toute la largeur, masquant la dernière colonne

### 3. ✅ **"Voir Kanban" ne fonctionne plus**

**Symptôme** :
- Quand on clique sur "Voir Kanban" dans une carte kanban, rien ne se passe
- Le kanban ne s'affiche pas

**Cause Identifiée** :
- Dans `HomeViewController.displayKanban()`, on charge `displayKanban.fxml` directement et on le met dans `kanbanArea`
- Puis on appelle `openCreateForm()` qui charge `kanbanView.fxml` (qui inclut `displayKanban.fxml`) et l'écrase
- Il y a un double chargement qui crée un conflit
- Le contrôleur `DisplayKanbanController` n'est pas initialisé correctement car il est chargé deux fois
- Le changement du `rightAnchor` à 85.0 peut aussi causer des problèmes de layout lors du chargement direct

### 4. **Disparition des Tâches lors du Changement de Statut**

**Symptôme** :
- Quand on change le statut d'une tâche, elle apparaît dans la nouvelle colonne puis disparaît rapidement
- Toutes les tâches de la colonne de destination peuvent aussi disparaître

**Cause Identifiée** :
- Problème de correspondance entre objets `Column` dans `taskColumn` (HashMap) et `columns` (List)
- `getTasksFromColumn()` utilise `taskColumn.getOrDefault(column, ...)` qui compare par référence d'objet
- Les objets `Column` utilisés comme clés dans `taskColumn` peuvent être différents de ceux dans `columns`
- Lors de la reconstruction de `taskCreations`, `getTasksFromColumn()` ne trouve pas les tâches car les objets ne correspondent pas

**Localisation** :
- `Kanban.getTasksFromColumn(Column column)` - ligne 159
- `ManageDisplay.buildKanbanView()` - reconstruction de `taskCreations` ligne 119
- `MoveTask.execute()` - utilise les clés existantes de `taskColumn`

**Solution Proposée** :
1. Modifier `getTasksFromColumn()` pour chercher par ID plutôt que par référence
2. S'assurer que les objets `Column` utilisés dans `taskColumn` sont les mêmes que ceux dans `columns`
3. Ou utiliser une approche différente pour stocker la relation tâche-colonne

---

### 2. **Reconstruction Complète de la Vue à Chaque Notification**

**Symptôme** :
- Chaque modification déclenche une reconstruction complète de l'interface
- Perte de l'état de l'interface (popups, scroll position, etc.)
- Performance dégradée avec beaucoup de modifications

**Cause** :
- `ManageDisplay.refreshKanban()` appelle toujours `openKanbanScreen()` qui reconstruit tout
- `taskCreations` est recréé depuis zéro à chaque fois
- Le contrôleur est réinitialisé complètement

**Localisation** :
- `ManageDisplay.refreshKanban()` - ligne 42
- `ManageDisplay.buildKanbanView()` - reconstruction complète
- `DisplayKanbanController.initBoard()` - réinitialisation

**Solution Proposée** :
1. Implémenter une mise à jour incrémentale au lieu de reconstruction complète
2. Mettre à jour uniquement `taskCreations` et appeler `renderKanban()`
3. Ne reconstruire complètement que si nécessaire (changement de kanban, première ouverture)

---

## 🟡 PROBLÈMES IMPORTANTS (À résoudre ensuite)

### 3. **Pas de Mise à Jour Optimiste (Feedback Immédiat)**

**Symptôme** :
- L'utilisateur doit attendre la notification serveur pour voir le résultat de son action
- Pas de feedback visuel immédiat (sauf Edit Column/Task qui appellent `renderKanban()`)
- Expérience utilisateur dégradée, surtout avec latence réseau

**Cause** :
- Aucune action ne met à jour l'interface localement avant la réponse serveur
- Seules `showEditColumnPopup()` et `showEditTaskPopup()` appellent `renderKanban()` localement
- `onStatusClick()` (MoveTask) n'a aucune mise à jour locale

**Localisation** :
- `DisplayKanbanController.onStatusClick()` - ligne 255
- `DisplayKanbanController.showAddTaskPopup()` - ligne 359
- `DisplayKanbanController.showAddColumnPopup()` - ligne 279

**Solution Proposée** :
1. Implémenter une mise à jour optimiste pour toutes les actions
2. Mettre à jour `taskCreations` localement avant l'envoi au serveur
3. Appliquer la modification au kanban local avant l'envoi
4. Gérer les conflits si la notification serveur contredit la mise à jour locale

---

### 4. **Synchronisation du Kanban entre Corps et Modèle**

**Symptôme** :
- Le kanban dans `kanbanCorps` peut être désynchronisé avec celui dans `ClientModel`
- `deliverNotification()` utilise `corps.getCurrentKanban()` qui peut être obsolète
- Risque d'appliquer des modifications sur un kanban non à jour

**Cause** :
- `corps.setCurrentKanban()` n'est pas toujours appelé après les modifications locales
- `saveModifiedKanban()` met à jour le modèle mais pas toujours `corps`
- `deliverNotification()` récupère le kanban depuis `corps` qui peut être obsolète

**Localisation** :
- `CommClientCallsKanbanImpl.deliverNotification()` - ligne 30
- `CommCallsDataClientImplementation.saveModifiedKanban()` - ligne 109
- `DisplayKanbanController.onStatusClick()` - pas de mise à jour de `corps`

**Solution Proposée** :
1. S'assurer que `corps.setCurrentKanban()` est appelé après chaque modification locale
2. Dans `deliverNotification()`, récupérer le kanban mis à jour depuis le modèle plutôt que `corps`
3. Synchroniser systématiquement `corps` et le modèle après chaque modification

---

## 🟢 PROBLÈMES MINEURS (Améliorations)

### 5. **Performance - Reconstruction Inutile**

**Symptôme** :
- Reconstruction complète même pour des modifications mineures
- Pas de distinction entre "modification du kanban affiché" et "changement de kanban"

**Solution Proposée** :
- Implémenter une mise à jour incrémentale (voir problème #2)
- Ne reconstruire que si le kanban a changé ou si c'est la première ouverture

---

### 6. **Fonctionnalité "Voir les Users" Incomplète**

**Symptôme** :
- `taskUsers` est une Map locale non synchronisée avec le serveur
- Les utilisateurs affichés peuvent être obsolètes ou incomplets

**Localisation** :
- `DisplayKanbanController.showTaskUsersPopup()` - ligne 416
- `DisplayKanbanController.taskUsers` - Map locale ligne 67

**Solution Proposée** :
1. Implémenter une synchronisation serveur pour les utilisateurs des tâches
2. Créer une modification `GetTaskUsers` ou inclure les users dans les notifications
3. Mettre à jour `taskUsers` à la réception des notifications

---

### 7. **Fonctionnalité "Ajouter des Users" Non Implémentée**

**Symptôme** :
- Le bouton "ADD" dans `showAddUserToTaskPopup()` est commenté
- Pas de classe `AddUserToTask` dans le code

**Localisation** :
- `DisplayKanbanController.showAddUserToTaskPopup()` - ligne 412
- Ligne 449 : code commenté

**Solution Proposée** :
1. Créer la classe `AddUserToTask extends Modification`
2. Implémenter `execute()` et `undo()`
3. Décommenter et compléter le code dans `showAddUserToTaskPopup()`
4. Ajouter la gestion côté serveur

---

## 📋 RÉSUMÉ DES ACTIONS À PRENDRE

### Priorité 1 (Critique) :
1. ✅ **Résoudre la disparition des tâches** - Modifier `getTasksFromColumn()` pour chercher par ID
2. ✅ **Implémenter mise à jour incrémentale** - Éviter la reconstruction complète systématique

### Priorité 2 (Important) :
3. ✅ **Mise à jour optimiste** - Feedback immédiat pour toutes les actions
4. ✅ **Synchronisation kanban** - S'assurer que `corps` et modèle sont toujours synchronisés

### Priorité 3 (Amélioration) :
5. ⚠️ **Optimiser les performances** - Reconstruction seulement si nécessaire
6. ⚠️ **Compléter "Voir les Users"** - Synchronisation serveur
7. ⚠️ **Implémenter "Ajouter des Users"** - Créer `AddUserToTask`

---

## 🔍 POINTS D'ATTENTION TECHNIQUES

### Correspondance des Objets Column
- `Column` a `equals()` et `hashCode()` basés sur l'ID (ligne 42-52 de `Column.java`)
- Mais `HashMap.getOrDefault()` peut avoir des problèmes si les objets ne sont pas exactement les mêmes
- Vérifier que les objets `Column` dans `taskColumn.keySet()` sont les mêmes que ceux dans `columns`

### Gestion de l'État
- `taskCreations` est la source de vérité pour l'affichage
- Mais il est recréé depuis `taskColumn` à chaque reconstruction
- S'assurer que `taskColumn` est toujours cohérent avec `columns` et `tasks`

### Thread Safety
- Les notifications arrivent sur un thread différent (MsgReceiver)
- `Platform.runLater()` est utilisé pour les mises à jour UI
- Vérifier qu'il n'y a pas de race conditions entre modifications locales et notifications

---

## 🎯 PLAN D'ACTION RECOMMANDÉ

1. **Phase 1 - Correction Critique** :
   - Corriger `getTasksFromColumn()` pour résoudre la disparition des tâches
   - Tester le changement de statut

2. **Phase 2 - Amélioration UX** :
   - Implémenter mise à jour optimiste pour MoveTask
   - Implémenter mise à jour incrémentale au lieu de reconstruction complète

3. **Phase 3 - Synchronisation** :
   - S'assurer de la synchronisation entre `corps` et modèle
   - Améliorer `deliverNotification()` pour utiliser le kanban à jour

4. **Phase 4 - Fonctionnalités** :
   - Compléter "Voir les Users"
   - Implémenter "Ajouter des Users"

