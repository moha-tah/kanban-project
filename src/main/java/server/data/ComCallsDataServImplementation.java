package server.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.Modification;
import common.dataClasses.Access; // Import nécessaire
import server.interfaces.CommCallsDataServer;

public class ComCallsDataServImplementation implements CommCallsDataServer {
    private static final String SERVEUR_PREFIX = "SERVEUR: ";
    private DataServProvider myProvider;

    public ComCallsDataServImplementation() {}

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
    public List<Kanban> notifyLogout(LightUser lightUser) {
        // Retourner une liste vide si l'utilisateur est null
        if (lightUser == null) {
            return new ArrayList<>();
        }

        ServerModel model = myProvider.getModel();
        UUID userId = lightUser.getId();

        // 1. Supprimer l'utilisateur de la liste des utilisateurs connectés
        boolean userRemoved = model.getConnectedUsers().removeIf(u -> u.getId().equals(userId));
        
        if (!userRemoved) {
            java.util.logging.Logger.getLogger(ComCallsDataServImplementation.class.getName())
                .info(SERVEUR_PREFIX + "Utilisateur " + lightUser.getUsername() + " n'était pas dans la liste des connectés.");
        }

        // 2. Retirer l'utilisateur des listes d'accès des kanbans où il n'est pas le créateur
        List<Kanban> affectedKanbans = new ArrayList<>();
        for (Kanban kanban : model.getInUseKanbans()) {
            if (kanban.getAccessList() != null) {
                boolean removed = kanban.getAccessList().removeIf(access -> 
                    access.getUser() != null && access.getUser().getId().equals(userId)
                );
                if (removed) {
                    affectedKanbans.add(kanban);
                }
            }
        }

        // 3. Supprimer les Kanbans créés par cet utilisateur de la mémoire serveur
        // (Les kanbans sont sauvegardés sur disque et seront rechargés à la reconnexion)
        int initialSize = model.getInUseKanbans().size();
        model.getInUseKanbans().removeIf(k ->
                k.getCreatorId() != null && k.getCreatorId().equals(userId)
        );
        int removedCount = initialSize - model.getInUseKanbans().size();

        // Logging
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ComCallsDataServImplementation.class.getName());
        logger.info(SERVEUR_PREFIX + lightUser.getUsername() + " déconnecté.");
        logger.info(SERVEUR_PREFIX + removedCount + " kanban(s) créé(s) par cet utilisateur retiré(s) de la mémoire.");
        if (!affectedKanbans.isEmpty()) {
            logger.info(SERVEUR_PREFIX + affectedKanbans.size() + " kanban(s) affecté(s) (accès retiré).");
        }

        // Retourner la liste des kanbans restants en mémoire
        // Le broadcast qui suit (dans LogoutMessage) enverra cette liste nettoyée aux autres clients
        return new ArrayList<>(model.getInUseKanbans());
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
        // TODO : Implémenter la logique
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