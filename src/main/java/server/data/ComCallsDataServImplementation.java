package server.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import common.dataClasses.Access;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.Modification; // Import nécessaire
import server.interfaces.CommCallsDataServer;
import server.data.AssociationUsersOnKanban;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ComCallsDataServImplementation implements CommCallsDataServer {
    private DataServProvider myProvider;
    // Map pour tracker les viewers par kanban (kanbanId -> AssociationUsersOnKanban)
    private Map<UUID, AssociationUsersOnKanban> kanbanViewersMap;

    public ComCallsDataServImplementation() {
        this.kanbanViewersMap = new ConcurrentHashMap<>();
    }

    public static ComCallsDataServImplementation newComCallsDataServImplementation() {
        return new ComCallsDataServImplementation();
    }

    // -------------------------------------------------------
    // GESTION UTILISATEURS
    // -------------------------------------------------------

    @Override
    public void addNewUser(LightUser user, List<LightKanban> clientKanbans) {
        // Correction erreur "ServerModel cannot be referenced" : on utilise l'objet
        ServerModel model = myProvider.getModel();

        List<LightUser> connectedUsers = model.getConnectedUsers();
        boolean userExists = connectedUsers.stream().anyMatch(u -> u.getId().equals(user.getId()));
        if (!userExists) {
            connectedUsers.add(user);
        }

        if (clientKanbans != null && !clientKanbans.isEmpty()) {
            List<Kanban> serverKanbans = model.getInUseKanbans();

            for (LightKanban lk : clientKanbans) {
                boolean kExists = serverKanbans.stream().anyMatch(k -> k.getId().equals(lk.getId()));

                if (!kExists) {
                    Kanban newK = new Kanban(lk.getId(), lk.getTitle());
                    newK.setCreatorId(user.getId());

                    // Récupération visibilité via instance check
                    if (lk instanceof Kanban kanban) {
                        newK.setVisibility(kanban.getVisibility());
                    } else {
                        newK.setVisibility("Private");
                    }
                    serverKanbans.add(newK);
                }
            }
        }
    }

    @Override
    public List<LightUser> getUsersList() {
        return myProvider.getModel().getConnectedUsers();
    }

    @Override
    public List<Kanban> notifyLogout(LightUser user) {
        if (user == null) return null;

        ServerModel model = myProvider.getModel();
        UUID userId = user.getId();

        // 1. Supprimer l'utilisateur de la liste des connectés
        model.getConnectedUsers().removeIf(u -> u.getId().equals(userId));

        // 2. Supprimer les Kanbans créés par cet utilisateur
        int initialSize = model.getInUseKanbans().size();

        model.getInUseKanbans().removeIf(k ->
                k.getCreatorId() != null && k.getCreatorId().equals(userId)
        );

        int removedCount = initialSize - model.getInUseKanbans().size();

        System.out.println("SERVEUR: " + user.getUsername() + " déconnecté.");
        System.out.println("SERVEUR: " + removedCount + " kanban(s) de cet utilisateur retiré(s) de la mémoire.");

        // Le broadcast qui suit (dans LogoutMessage) enverra cette liste nettoyée aux autres clients
        return model.getInUseKanbans();
    }

    // -------------------------------------------------------
    // GESTION KANBANS
    // -------------------------------------------------------

    @Override
    public LightKanban saveKanban(Kanban kanban) {
        ServerModel model = myProvider.getModel();
        model.getInUseKanbans().removeIf(k -> k.getId().equals(kanban.getId()));
        model.getInUseKanbans().add(kanban);

        server.comm.CommCoreServer.triggerBroadcast();
        return kanban.getLightKanban();
    }

    @Override
    public List<LightKanban> getKanbansList() {
        List<LightKanban> lights = new ArrayList<>();
        List<Kanban> heavies = myProvider.getModel().getInUseKanbans();
        if (heavies != null) {
            for (Kanban k : heavies) {
                lights.add(k.getLightKanban());
            }
        }
        return lights;
    }

    @Override
    public Kanban requestKanban(LightUser user, LightKanban kanbanID) {
        if (myProvider == null || myProvider.getModel() == null) return null;

        ServerModel model = myProvider.getModel();
        for (Kanban k : model.getInUseKanbans()) {
            if (k.getId().equals(kanbanID.getId())) {
                // Ajouter l'utilisateur comme viewer du kanban
                if (user != null) {
                    AssociationUsersOnKanban assoc = kanbanViewersMap.computeIfAbsent(
                        kanbanID.getId(), 
                        id -> new AssociationUsersOnKanban(kanbanID)
                    );
                    synchronized (assoc) {
                        assoc.addUserOnKanban(user);
                    }
                }
                return k;
            }
        }
        return null;
    }

    @Override
    public List<LightKanban> getVisibleKanbansForUser(LightUser user) {
        List<LightKanban> result = new ArrayList<>();
        ServerModel model = myProvider.getModel();
        if (model.getInUseKanbans() != null) {
            // On renvoie tout, le client filtre l'affichage
            result.addAll(model.getInUseKanbans());
        }
        return result;
    }

    @Override
    public boolean addAuthorizedUser(LightKanban kanbanId, LightUser userId) {
        ServerModel model = myProvider.getModel();
        List<Kanban> kanbans = model.getInUseKanbans();

        for (Kanban k : kanbans) {
            if (k.getId().equals(kanbanId.getId())) {
                // Trouver l'utilisateur dans la liste des connectés
                LightUser userToAdd = model.getConnectedUsers().stream()
                        .filter(u -> u.getId().equals(userId.getId()))
                        .findFirst()
                        .orElse(null);

                if (userToAdd != null) {
                    if (k.getAccessList() == null) {
                        k.setAccessList(new ArrayList<>());
                    }
                    // Ajout de l'accès
                    k.getAccessList().add(new Access(userToAdd, null));
                    return true;
                }
            }
        }
        return false;
    }

    // -------------------------------------------------------
    // MÉTHODES MANQUANTES (Correction de "must implement abstract method")
    // -------------------------------------------------------

    @Override
    public void askDeleteKanban(LightUser user, LightKanban kanban) {
        // TODO : Implémenter la suppression
    }

    // C'était la méthode manquante qui causait l'erreur ligne 14
    @Override
    public void askAddListModifiers(LightUser user, LightKanban kanban) {
        // Ajoute l'utilisateur à la liste des modificateurs du kanban
        if (user == null || kanban == null || myProvider == null) return;
        ServerModel model = myProvider.getModel();
        List<Kanban> kanbans = model.getInUseKanbans();
        Kanban target = kanbans.stream().filter(k -> k.getId().equals(kanban.getId())).findFirst().orElse(null);
        if (target == null) return;

        // Vérifier si l'utilisateur est déjà dans la liste d'accès
        if (target.getAccessList() == null) {
            target.setAccessList(new ArrayList<>());
        }
        boolean exists = target.getAccessList().stream().anyMatch(a -> a.getUser() != null && a.getUser().getId().equals(user.getId()));
        if (!exists) {
            target.getAccessList().add(new Access(user, null)); // Ajoute avec rôle par défaut
        }
        // Optionnel : sauvegarder le kanban modifié si nécessaire
    }

    @Override
    public List<LightUser> saveModifiedKanban(LightKanban kanban, Modification modification) {
        return null;
    }

    @Override
    public Kanban getKanban(LightKanban lightKanban, LightUser user) {
        return requestKanban(user, lightKanban);
    }

    @Override
    public void closeKanban(LightKanban lightKanban, LightUser user) {
        if (lightKanban == null || user == null) {
            return;
        }

        if (myProvider == null || myProvider.getModel() == null) {
            return;
        }

        ServerModel model = myProvider.getModel();
        
        // Vérifier que le kanban existe dans le modèle
        boolean kanbanExists = model.getInUseKanbans().stream()
            .anyMatch(k -> k.getId().equals(lightKanban.getId()));
        
        if (kanbanExists) {
            // Retirer l'utilisateur de la liste des viewers du kanban de manière atomique
            kanbanViewersMap.computeIfPresent(lightKanban.getId(), (kanbanId, assoc) -> {
                synchronized (assoc) {
                    assoc.removeUserOnKanban(user);
                    // Retourner null pour supprimer l'entrée si plus aucun viewer
                    boolean isEmpty = assoc.getUsersOnKanban().isEmpty();
                    return isEmpty ? null : assoc;
                }
            });
        }

        // Le kanban reste disponible en mémoire pour les autres utilisateurs
    }

    // -------------------------------------------------------
    // GETTERS / SETTERS
    // -------------------------------------------------------

    public void setDataServProvider(DataServProvider provider) {
        this.myProvider = provider;
    }

    public DataServProvider getDataServProvider() {
        return myProvider;
    }
}