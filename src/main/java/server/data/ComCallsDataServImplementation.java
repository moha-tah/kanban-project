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
                    Kanban newK = new Kanban(lk.getId(), lk.getTitle(), lk.getAccessList());
                    newK.setCreatorId(user.getId());

                    // Récupération visibilité via instance check
                    if (lk instanceof Kanban) {
                        newK.setVisibility(((Kanban) lk).getVisibility());
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
    public List<Kanban> notifyLogout(UUID userId) {
        return null;
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
    public Kanban requestKanban(LightUser user, UUID kanbanID) {
        if (myProvider == null || myProvider.getModel() == null) return null;

        ServerModel model = myProvider.getModel();
        for (Kanban k : model.getInUseKanbans()) {
            if (k.getId().equals(kanbanID)) {
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
        List<Kanban> inUseKanbans = myProvider.getModel().getInUseKanbans();
        Kanban kanbanToUpdate = null;
        List<LightUser> usersToNotify = new ArrayList<LightUser>();
        for(Kanban kanbanInList : inUseKanbans){
            if( kanbanInList.getId().equals(kanban.getId())){
                kanbanToUpdate = kanbanInList;
                break;
            }
        }
        modification.execute(kanbanToUpdate);
        List <Access> accesList = kanban.getAccessList();
        for(Access acces: accesList){
            LightUser user = acces.getUser();
            usersToNotify.add(user);
        }
        return usersToNotify;
    }

    @Override
    public Kanban getKanban(LightKanban lightKanban, LightUser user) {
        return requestKanban(user, lightKanban.getId());
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