package client.ihmMain.impl;

import client.ihmMain.MainCore;
import client.interfaces.KanbanCallsMain;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

/**
 * Implémentation des callbacks de la couche Kanban vers la couche Main.
 * 
 * Cette classe sert d'adaptateur entre l'interface utilisateur Kanban et la couche
 * Main. Elle permet à l'IHM Kanban de notifier la couche Main des actions de
 * navigation (fermeture de kanban, retour à l'accueil, affichage du profil).
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see KanbanCallsMain
 * @see MainCore
 */
public class kanbanCallsMainImpl implements KanbanCallsMain {

    /**
     * Cœur de l'application principale.
     */
    private final MainCore core;

    /**
     * Constructeur de l'implémentation.
     * 
     * @param core Le cœur de l'application principale (ne doit pas être null)
     */
    public kanbanCallsMainImpl(MainCore core) {
        this.core = core;
    }

    /**
     * Notifie la fermeture d'un kanban avec envoi au serveur.
     * 
     * Cette méthode est appelée lorsque l'utilisateur ferme un kanban.
     * L'implémentation actuelle se contente de logger l'action.
     * 
     * @param kanban Le kanban fermé (ne doit pas être null)
     * @param user L'utilisateur qui ferme le kanban (ne doit pas être null)
     * @implNote Cette méthode devrait notifier le serveur de la fermeture.
     */
    @Override
    public void closingKanbanToServer(LightKanban kanban, LightUser user) {
        System.out.println("[Kanban->Main] closingKanbanToServer kanbanId=" + kanban.getId() + " userId=" + user.getId());
        // core.onCloseKanban(kanbanId, userId);
    }

    /**
     * Notifie la fermeture d'un kanban sans envoi au serveur.
     * 
     * Cette méthode est appelée lorsque l'utilisateur ferme un kanban localement.
     * L'implémentation actuelle n'est pas complète.
     * 
     * @implNote Cette méthode devrait gérer la fermeture locale du kanban.
     */
    @Override
    public void closingKanban(){
        //core.onCloseKanban();
    }

    /**
     * Navigue vers la page d'accueil.
     * 
     * Cette méthode est appelée lorsque l'utilisateur souhaite retourner
     * à la page d'accueil depuis l'interface Kanban.
     */
    @Override
    public void goHomeView() {
        core.showHomeView();
    }

    /**
     * Navigue vers la page de profil.
     * 
     * Cette méthode est appelée lorsque l'utilisateur souhaite afficher
     * son profil depuis l'interface Kanban.
     */
    @Override
    public void goProfileView() {
        core.showProfileView();
    }
    
}