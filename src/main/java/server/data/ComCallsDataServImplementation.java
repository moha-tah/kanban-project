package server.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.Modification;
import server.interfaces.CommCallsDataServer;

public class ComCallsDataServImplementation implements CommCallsDataServer {
    private DataServProvider myProvider;

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
    
    //-- Modifier KanBan - Save in Servidor --
    @Override 
    public List<LightUser> saveModifiedKanban(LightKanban kanban, Modification modification){
        // Safety check: ensure the provider and model exist
         if (myProvider == null || myProvider.getModel() == null) {
            System.err.println("SERVER: saveModifiedKanban - myProvider or model is null");
            return List.of(); // No users to notify
        }
         ServerModel model = myProvider.getModel();

        // V3 simple version — we are NOT applying the modification yet.
        // For now, we only register that the request was received on the server.
         System.out.println(
            "SERVER: saveModifiedKanban for Kanban "
            + kanban.getTitle() + " (" + kanban.getId() + ")"
        );

    // For now, return ALL connected users,
    // so that the COMM server can notify each viewer.
         return new ArrayList<>(model.getConnectedUsers());
    }
    

    @Override public Kanban getKanban(LightKanban lightKanban, LightUser user) { return null; }
    @Override public void closeKanban(LightKanban lightKanban, LightUser user) {}

    public void setDataServProvider(DataServProvider provider) {
        this.myProvider = provider;
    }

    public DataServProvider getDataServProvider() {
        return myProvider;
    }
}