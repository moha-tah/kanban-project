package server.interfaces;

import java.util.List;
import java.util.UUID;

import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.Modification;


// CommCallsData
public interface CommCallsDataServer {
    Kanban requestKanban(UUID lightUserId, UUID lightKanbId);
    List<Kanban> notifyLogout(UUID userId);
    void askDeleteKanban(LightUser user, LightKanban kanban);
    void askAddListModifiers(LightUser user, LightKanban kanban);
    boolean addAuthorizedUser(UUID kanbanId, UUID userId);
    
    // Implémentation par défaut de addNewUser selon le diagramme de séquence
    default void addNewUser(LightUser user, List<LightKanban> kanbans) {
        // Ajouter l'utilisateur et les kanbans (doit être implémenté par les classes concrètes)
        // Cette méthode par défaut orchestre les appels nécessaires
        
        // Récupérer les listes mises à jour
        List<LightUser> updatedUsersList = getUsersList();
        List<LightKanban> updatedKanbansList = getKanbansList();
        
        // Notifier tous les clients connectés via COMM-Server
        // TODO: Appeler addUserToList sur tous les ConnectedAppCliente via DataCallsCommServer
        // addUserToList(user, kanbans);
        
        // Envoyer les listes mises à jour au COMM-Server
        // TODO: Appeler sendUsersAndKanbansList sur COMM-Server via DataCallsCommServer
        // sendUsersAndKanbansList(updatedUsersList, updatedKanbansList);
    }
    
    List<LightUser> getUsersList();
    List<LightKanban> getKanbansList();
    LightKanban saveKanban(Kanban kanban);
    List<LightUser> saveModifiedKanban(LightKanban kanban, Modification modif);
    Kanban getKanban(LightKanban kanban, LightUser user);
    void closeKanban(LightKanban kaban, LightUser user);
}