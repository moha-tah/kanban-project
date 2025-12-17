package client.data;

import client.interfaces.ComCallsDataClient;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.Modification;
import common.dataClasses.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation de l'interface {@link ComCallsDataClient}.
 * 
 * Cette classe gère les appels depuis la couche Communication vers la couche données.
 * Elle permet de mettre à jour les listes d'utilisateurs et de kanbans, d'ajouter
 * des utilisateurs autorisés, et de gérer les modifications de kanbans.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see ComCallsDataClient
 * @see DataClientProvider
 */
public class CommCallsDataClientImplementation implements ComCallsDataClient{
    /**
     * Fournisseur de services de la couche données.
     */
    private DataClientProvider provider;
 

    /**
     * Met à jour les listes d'utilisateurs et de kanbans dans le modèle local.
     * 
     * @param users La nouvelle liste d'utilisateurs connectés (ne doit pas être null)
     * @param kanbans La nouvelle liste de kanbans disponibles (ne doit pas être null)
     */
    @Override
    public void updateUserList(List<LightUser> users, List<LightKanban> kanbans){
        provider.getMyModel().setAvailableLightKanbans(kanbans);
        provider.getMyModel().setConnectedUsers(users);
    }

    /**
     * Récupère la liste des utilisateurs connectés depuis le modèle local.
     * 
     * @return La liste des utilisateurs connectés, ou null si non initialisée
     */
    public List<LightUser> getUsersList() {
        DataClientProvider prov = this.getProvider();
        ClientModel model = prov.getMyModel();
        return model.getConnectedUsers();
    }
    
    /**
     * Récupère l'utilisateur local connecté.
     * 
     * @return L'utilisateur local sous forme de LightUser, ou null si non connecté
     */
    @Override
    public LightUser askIdUser(){
        return this.provider.getMyModel().getLocalUser();
    }

    /**
     * Envoie un kanban au serveur (non implémenté).
     * 
     * @param kanban Le kanban à envoyer
     */
    @Override
    public void send(Kanban kanban){
        //TODO
    }

    /**
     * Met à jour et publie les listes d'utilisateurs.
     * 
     * Cette méthode publie la liste complète des utilisateurs connectés
     * à l'interface principale pour mise à jour de l'affichage.
     * 
     * @param user L'utilisateur concerné (peut être null pour publier toute la liste)
     */
    @Override
    public void updateLists(LightUser user){
        // Récupérer et publier les listes d'utilisateurs mises à jour
        if (provider != null && provider.getMyModel() != null) {
            List<LightUser> users = provider.getMyModel().getConnectedUsers();
            if (provider.getMainInterface() != null) {
                provider.getMainInterface().publishUsersList(users);
            }
        }
    }

    /**
     * Publie la liste mise à jour des kanbans disponibles.
     * 
     * Cette méthode récupère la liste des kanbans depuis le modèle local
     * et la publie à l'interface principale pour mise à jour de l'affichage.
     * 
     * @param kanban Le kanban concerné (peut être null)
     */
    @Override
    public void uploadKanbans(LightKanban kanban){
        // Publier les listes de kanbans mises à jour
        if (provider != null && provider.getMyModel() != null) {
            List<LightKanban> kanbans = provider.getMyModel().getAvailableLightKanbans();
            if (provider.getMainInterface() != null) {
                provider.getMainInterface().publishKanbansList(kanbans);
            }
        }
    }

    /**
     * Ajoute un kanban à la liste des kanbans disponibles.
     * 
     * @param user L'utilisateur concerné (peut être null)
     * @param kanban Le kanban à ajouter (ne doit pas être null)
     */
    @Override
    public void addListModifiers(LightUser user, LightKanban kanban){
        provider.getMyModel().addKanban(kanban);
    }

    /**
     * Ajoute un utilisateur autorisé à un kanban.
     * 
     * Cette méthode envoie une demande à la couche communication pour
     * ajouter un utilisateur à la liste des utilisateurs autorisés d'un kanban.
     * 
     * @param kanbanId Le kanban pour lequel ajouter l'utilisateur (ne doit pas être null)
     * @param userId L'utilisateur à autoriser (ne doit pas être null)
     * @return true si la demande a été envoyée avec succès, false sinon
     */
    @Override
    public boolean addAuthorizedUser(LightKanban kanbanId, LightUser userId){
        if (provider == null || provider.getCommInterface() == null) {
            return false;
        }
        try {
            provider.getCommInterface().addAuthorizedUser(kanbanId, userId);
            return true;
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(CommCallsDataClientImplementation.class.getName())
                .log(java.util.logging.Level.SEVERE, "Error while adding authorized user (kanbanId=" + kanbanId + ", userId=" + userId + ")", e);
            return false;
        }
    }
    
    /**
     * Ajoute un kanban à la liste des kanbans disponibles et publie la mise à jour.
     * 
     * Si le kanban existe déjà (même ID), il est remplacé. La liste mise à jour
     * est ensuite publiée à l'interface principale.
     * 
     * @param kanban Le kanban à ajouter ou mettre à jour (ne doit pas être null)
     */
    @Override
    public void addToListKanban(LightKanban kanban){
        if (provider == null || provider.getMyModel() == null || kanban == null) {
            return;
        }
        
        ClientModel model = provider.getMyModel();
        List<LightKanban> kanbans = model.getAvailableLightKanbans();
        
        // Initialiser la liste si elle est null
        if (kanbans == null) {
            kanbans = new ArrayList<>();
            model.setAvailableLightKanbans(kanbans);
        }
        
        // Retirer le kanban s'il existe déjà (même ID) puis ajouter le nouveau
        kanbans.removeIf(k -> k.getId().equals(kanban.getId()));
        kanbans.add(kanban);

        // Publier la liste mise à jour à l'interface (comme dans uploadKanbans)
        if (provider.getMainInterface() != null) {
            provider.getMainInterface().publishKanbansList(kanbans);
        }
    }

    /**
     * Sauvegarde une modification de kanban (non implémenté).
     * 
     * @param modification La modification à sauvegarder
     * @param kanban Le kanban concerné par la modification
     */
    @Override
    public void saveModifiedKanban(Modification modification, LightKanban kanban){
        //TODO
    }

    /**
     * Sauvegarde temporairement un kanban dans le modèle local.
     * 
     * Cette méthode définit le kanban comme kanban actuel sans le sauvegarder
     * de manière permanente.
     * 
     * @param kanban Le kanban à sauvegarder temporairement (ne doit pas être null)
     */
    @Override
    public void saveTempKanban(Kanban kanban){
        provider.getMyModel().setCurrentKanban(kanban);
    }

    /**
     * Ajoute un utilisateur et ses kanbans à la liste (non implémenté).
     * 
     * @param user L'utilisateur à ajouter
     * @param kanbans La liste des kanbans de l'utilisateur
     */
    @Override
    public void addUserToList(LightUser user, List<LightKanban> kanbans){
        //TODO
    }
    
    /**
     * Récupère le profil complet de l'utilisateur local pour partage distant.
     * 
     * Cette méthode crée une copie du profil utilisateur local avec ses kanbans
     * pour le partager avec d'autres utilisateurs. Les données sensibles comme
     * le mot de passe ne sont pas incluses.
     * 
     * @return Une copie du profil utilisateur local, ou null si aucun utilisateur n'est connecté
     */
    @Override
    public User getDistantProfile(){
        try {
            if (this.provider == null || this.provider.getMyModel() == null) return null;
            User local = this.provider.getMyModel().getLocalUser();
            if (local == null) return null;
            User copy = new User(local.getId(), local.getUsername(), local.getFirstName(), local.getLastName(), local.getBirthDate());
            try {
                if (local.getAvatar() != null && !local.getAvatar().isBlank()) copy.setAvatar(local.getAvatar());
            } catch (Throwable ignored) {}
            // Provide the local user's kanbans to display on distant profile view
            if (local.getMyKanban() != null) {
                // Create a shallow copy list to avoid accidental mutations
                java.util.List<common.dataClasses.Kanban> list = new java.util.ArrayList<>(local.getMyKanban());
                copy.setMyKanban(list);
            } else {
                copy.setMyKanban(new java.util.ArrayList<>());
            }
            return copy;
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(CommCallsDataClientImplementation.class.getName())
                    .log(java.util.logging.Level.WARNING, "getDistantProfile: error building profile", e);
            return null;
        }
    }

    /**
     * Constructeur de l'implémentation.
     * 
     * @param provider Le fournisseur de services de la couche données (ne doit pas être null)
     */
    public CommCallsDataClientImplementation(DataClientProvider provider) {
        this.provider = provider;
    }
    
    /**
     * Récupère le fournisseur de services de la couche données.
     * 
     * @return Le fournisseur de services
     */
    public DataClientProvider getProvider() {
        return this.provider;
    }
    
    /**
     * Définit le fournisseur de services de la couche données.
     * 
     * @param provider Le fournisseur de services à définir (ne doit pas être null)
     */
    public void setProvider(DataClientProvider provider) {
        this.provider = provider;
    }
}
