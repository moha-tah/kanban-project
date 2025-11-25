package server.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.Modification;
import common.dataClasses.Access;
import static common.dataClasses.Role;
import server.interfaces.CommCallsDataServer;


public class ComCallsDataServImplementation implements CommCallsDataServer {
  private DataServProvider myProvider;
  public ComCallsDataServImplementation() {}
  public static ComCallsDataServImplementation newComCallsDataServImplementation() {
        return new ComCallsDataServImplementation();
  }
  @Override
  public Kanban requestKanban(LightUser user, LightKanban kanban) {
        ServerModel model = myProvider.getModel();
        List<Kanban> inUseKanbans = model.getInUseKanbans();
        Kanban myKanban = null;
        for (Kanban k : inUseKanbans) {
            LightKanban lightK = k.getLightKanban();
            if (lightK.getId().equals(kanbanID)) {
                myKanban = k;
                break;
            }
    }
        //doute sur la méthode, peut etre que les classes ont des problèmes d'implémentation (manque d'attributs ?)
        Access accessList = myKanban.getAccessList()
        Boolean hasAccess = false;
        for (Access a : accessList) {
            aUser = a.getUser();
            aRole = a.getRole();
            if (aUser.getId().equals(user.getId()) && (aRole.equals(VIEWER) || aRole.equals(MODIFIER))) {
                hasAccess = true;
                break;
            }
        }
        if (hasAccess) {
            return myKanban;
        }
        else{
            return null;
        }
    }

    @Override
    public List<Kanban> notifyLogout(UUID userId) {
        return null ; //TODO V3
    }

    public ComCallsDataServImplementation() {}

    public static ComCallsDataServImplementation newComCallsDataServImplementation() {
        return new ComCallsDataServImplementation();
    }

    @Override
    public void addNewUser(LightUser user, List<LightKanban> clientKanbans) {
        ServerModel model = myProvider.getModel();

        // 1. Enregistrer l'utilisateur
        List<LightUser> connectedUsers = model.getConnectedUsers();
        boolean userExists = connectedUsers.stream().anyMatch(u -> u.getId().equals(user.getId()));
        if (!userExists) {
            connectedUsers.add(user);
        }

        // 2. Enregistrer les Kanbans du client en mémoire serveur
        if (clientKanbans != null && !clientKanbans.isEmpty()) {
            List<Kanban> serverKanbans = model.getInUseKanbans();

            for (LightKanban lk : clientKanbans) {
                // On vérifie si on l'a déjà
                boolean kExists = serverKanbans.stream().anyMatch(k -> k.getId().equals(lk.getId()));

                if (!kExists) {
                    // On stocke une version "coquille" du Kanban côté serveur
                    // L'important est d'avoir l'ID et le Titre pour le diffuser aux autres
                    Kanban newK = new Kanban(lk.getId(), lk.getTitle());
                    serverKanbans.add(newK);
                    System.out.println("SERVEUR: Kanban importé en mémoire : " + lk.getTitle() + " (" + lk.getId() + ")");
                }
            }
        }
    }

    @Override
    public LightKanban saveKanban(Kanban kanban) {
        ServerModel model = myProvider.getModel();
        // Mise à jour ou Ajout
        model.getInUseKanbans().removeIf(k -> k.getId().equals(kanban.getId()));
        model.getInUseKanbans().add(kanban);

        System.out.println("SERVEUR: Kanban sauvegardé. Total=" + model.getInUseKanbans().size());

        // Note: le broadcast est déclenché par le MessageHandler via réflexion
        return kanban.getLightKanban();
    }

    @Override
    public List<LightKanban> getKanbansList() {
        // Génération dynamique de la liste Light à partir de la mémoire
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
    public List<LightUser> getUsersList() {
        return myProvider.getModel().getConnectedUsers();
    }

    // --- Autres méthodes (Stubs) ---
    @Override 
    public Kanban requestKanban(LightUser user, UUID kanbanID) { 
        if (myProvider == null || myProvider.getModel() == null) {
            System.err.println("SERVEUR: requestKanban - myProvider or model is null");
            return null;
        }
        
        ServerModel model = myProvider.getModel();
        List<Kanban> kanbans = model.getInUseKanbans();
        
        System.out.println("SERVEUR: Searching for Kanban ID " + kanbanID + " in " + kanbans.size() + " kanbans");
        
        // Search for the Kanban with the matching ID
        for (Kanban k : kanbans) {
            System.out.println("  - Checking Kanban: " + k.getTitle() + " (ID: " + k.getId() + ")");
            if (k.getId().equals(kanbanID)) {
                System.out.println("SERVEUR: Sending full Kanban to user " + user.getId() + ": " + k.getTitle());
                return k;
            }
        }
        
        System.err.println("SERVEUR: Kanban not found with ID: " + kanbanID);
        return null;
    }
    
    @Override public List<Kanban> notifyLogout(UUID userId) { return null; }
    @Override public void askDeleteKanban(LightUser user, LightKanban kanban) {}
    @Override public void addListModifiers(LightUser user, LightKanban kanban) {}
    @Override public boolean addAuthorizedUser(UUID kanbanId, UUID userId) { return false; }
    @Override public List<LightUser> saveModifiedKanban(LightKanban kanban, Modification modification) { return null; }
    @Override public Kanban getKanban(LightKanban lightKanban, LightUser user) { return null; }
    @Override public void closeKanban(LightKanban lightKanban, LightUser user) {}

    public void setDataServProvider(DataServProvider provider) {
        this.myProvider = provider;
    }

    public DataServProvider getDataServProvider() {
        return myProvider;
    }
}