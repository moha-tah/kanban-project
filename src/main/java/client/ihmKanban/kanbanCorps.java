package client.ihmKanban;

import client.interfaces.KanbanCallsDataClient;
import client.interfaces.KanbanCallsMain;
import client.interfaces.IhmKanbanCallsComm;
import client.interfaces.DataClientCallsKanban;
import client.interfaces.MainCallsKanban;
import client.interfaces.CommClientCallsKanban;

import client.ihmKanban.impl.CommClientCallsKanbanImpl;
import client.ihmKanban.impl.DataClientCallsKanbanImpl;
import client.ihmKanban.impl.MainCallsKanbanImpl;
import client.ihmMain.controllers.HomeViewController;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import client.ihmMain.controllers.ProfileController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import client.ihmKanban.controllers.ManageDisplay;

import java.util.logging.Logger;


/**
 * Cœur de l'interface utilisateur Kanban.
 * 
 * Cette classe orchestre les appels entre l'interface utilisateur et les différentes
 * couches de l'application (DATA, COMM, MAIN). Elle gère l'état de l'IHM Kanban,
 * expose les callbacks pour les autres couches, et fournit les ports sortants
 * pour communiquer avec les autres couches.
 * 
 * Le pattern utilisé suit une architecture en couches avec injection de dépendances
 * pour les ports sortants et exposition de callbacks pour les ports entrants.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see ManageDisplay
 * @see DataClientCallsKanbanImpl
 * @see CommClientCallsKanbanImpl
 * @see MainCallsKanbanImpl
 */
public class kanbanCorps {

    /**
     * Port sortant vers la couche Data Client.
     * 
     * Permet d'appeler les services de la couche Data depuis l'IHM Kanban.
     */
    private KanbanCallsDataClient dataPort;
    
    /**
     * Port sortant vers la couche Main.
     * 
     * Permet d'appeler les services de la couche Main depuis l'IHM Kanban.
     */
    private KanbanCallsMain    mainPort;
    
    /**
     * Port sortant vers la couche Communication.
     * 
     * Permet d'envoyer des messages de communication depuis l'IHM Kanban.
     * Ajouté pour suivre le diagramme d'architecture.
     */
    private IhmKanbanCallsComm   commPort;

    /**
     * Utilisateur actuellement connecté dans l'IHM Kanban.
     */
    private LightUser me;
    
    /**
     * Liste des utilisateurs disponibles dans l'IHM Kanban.
     */
    private final List<LightUser>   users   = new ArrayList<>();
    
    /**
     * Liste des kanbans disponibles dans l'IHM Kanban.
     */
    private final List<LightKanban> kanbans = new ArrayList<>();

    /**
     * Implémentation des callbacks de la couche Data vers Kanban.
     * 
     * Permet à la couche Data de notifier l'IHM Kanban des changements.
     */
    private final DataClientCallsKanbanImpl  datCallbacks   = new DataClientCallsKanbanImpl(this);
    
    /**
     * Implémentation des callbacks de la couche Communication vers Kanban.
     * 
     * Permet à la couche Communication de notifier l'IHM Kanban des messages reçus.
     */
    private final CommClientCallsKanbanImpl  commCallbacks  = new CommClientCallsKanbanImpl(this);
    
    /**
     * Implémentation des callbacks de la couche Main vers Kanban.
     * 
     * Permet à la couche Main de notifier l'IHM Kanban des actions utilisateur.
     */
    private final MainCallsKanbanImpl mainCallbacks = new MainCallsKanbanImpl(this);

    /**
     * Logger statique pour les messages de log de cette classe.
     */
    public static final Logger LOGGER = Logger.getLogger("Kanban Corps");

    /**
     * Gestionnaire d'affichage des kanbans.
     * 
     * Responsable de la construction et de l'affichage des vues de kanban.
     */
    private final ManageDisplay manageDisplay = new ManageDisplay( this);

    /**
     * Lance l'application en réinitialisant l'état.
     * 
     * Cette méthode vide les listes d'utilisateurs et de kanbans, et réinitialise
     * l'utilisateur actuel. Elle doit être appelée au démarrage de l'application.
     */
    public void launchApp() {
        users.clear(); kanbans.clear(); me = null;
    }

    /**
     * Récupère le service de callbacks Data pour le câblage.
     * 
     * Cette méthode expose les callbacks permettant à la couche Data
     * de notifier l'IHM Kanban.
     * 
     * @return L'implémentation des callbacks Data vers Kanban
     */
    public DataClientCallsKanban getDATService()   { return datCallbacks; }
    
    /**
     * Récupère le service de callbacks Communication pour le câblage.
     * 
     * Cette méthode expose les callbacks permettant à la couche Communication
     * de notifier l'IHM Kanban.
     * 
     * @return L'implémentation des callbacks Communication vers Kanban
     */
    public CommClientCallsKanban  getCOMMService() { return commCallbacks; }
    
    /**
     * Récupère le service de callbacks Main pour le câblage.
     * 
     * Cette méthode expose les callbacks permettant à la couche Main
     * de notifier l'IHM Kanban.
     * 
     * @return L'implémentation des callbacks Main vers Kanban
     */
    public MainCallsKanban      getMAINService(){ return mainCallbacks; }

    /**
     * Définit le port sortant vers la couche Data Client.
     * 
     * @param dataPort Le port vers la couche Data Client (peut être null)
     */
    public void setDataPort(KanbanCallsDataClient dataPort) { this.dataPort = dataPort; }
    
    /**
     * Définit le port sortant vers la couche Main.
     * 
     * @param mainPort Le port vers la couche Main (peut être null)
     */
    public void setMainPort(KanbanCallsMain mainPort) { this.mainPort = mainPort; }
    
    /**
     * Définit le port sortant vers la couche Communication.
     * 
     * @param commPort Le port vers la couche Communication (peut être null)
     */
    public void setCommPort(IhmKanbanCallsComm commPort)    { this.commPort = commPort; }

    /**
     * Récupère le port sortant vers la couche Data Client.
     * 
     * @return Le port vers la couche Data Client, ou null si non défini
     */
    public KanbanCallsDataClient getDataPort() { return dataPort; }
    
    /**
     * Récupère le port sortant vers la couche Main.
     * 
     * @return Le port vers la couche Main, ou null si non défini
     */
    public KanbanCallsMain getMainPort()   { return mainPort; }
    
    /**
     * Récupère le port sortant vers la couche Communication.
     * 
     * @return Le port vers la couche Communication, ou null si non défini
     */
    public IhmKanbanCallsComm getCommPort()    { return commPort; }

    /**
     * Définit l'utilisateur actuellement connecté.
     * 
     * @param me L'utilisateur actuel (peut être null)
     */
    public void setMe(LightUser me) { this.me = me; }
    
    /**
     * Récupère l'utilisateur actuellement connecté.
     * 
     * @return L'utilisateur actuel, ou null si aucun utilisateur connecté
     */
    public LightUser getMe()        { return me; }

    /**
     * Récupère un snapshot de la liste des utilisateurs.
     * 
     * Retourne une copie de la liste pour éviter les modifications externes.
     * 
     * @return Une nouvelle liste contenant tous les utilisateurs
     */
    public List<LightUser> getUsersSnapshot()     { return new ArrayList<>(users); }
    
    /**
     * Récupère un snapshot de la liste des kanbans.
     * 
     * Retourne une copie de la liste pour éviter les modifications externes.
     * 
     * @return Une nouvelle liste contenant tous les kanbans
     */
    public List<LightKanban> getKanbansSnapshot() { return new ArrayList<>(kanbans); }

    /**
     * Ajoute ou remplace un kanban dans la liste.
     * 
     * Si un kanban avec le même ID existe déjà, il est remplacé.
     * Sinon, le kanban est ajouté à la liste.
     * 
     * @param k Le kanban à ajouter ou remplacer (ne doit pas être null)
     */
    public void addOrReplaceKanban(LightKanban k) {
        kanbans.removeIf(x -> x.getId().equals(k.getId()));
        kanbans.add(k);
    }

    /**
     * Ajoute ou remplace plusieurs kanbans dans la liste.
     * 
     * @param list La liste des kanbans à ajouter (ne doit pas être null)
     */
    public void addKanbans(List<LightKanban> list) { for (var k : list) addOrReplaceKanban(k); }

    /**
     * Ajoute ou remplace un utilisateur dans la liste.
     * 
     * Si un utilisateur avec le même ID existe déjà, il est remplacé.
     * Sinon, l'utilisateur est ajouté à la liste.
     * 
     * @param u L'utilisateur à ajouter ou remplacer (ne doit pas être null)
     */
    public void addUser(LightUser u) {
        users.removeIf(x -> x.getId().equals(u.getId())); users.add(u);
    }

    /**
     * Ajoute ou remplace plusieurs utilisateurs dans la liste.
     * 
     * @param list La liste des utilisateurs à ajouter (ne doit pas être null)
     */
    public void addUsers(List<LightUser> list) { for (var u : list) addUser(u); }

    /**
     * Met à jour tous les kanbans pour un utilisateur donné.
     * 
     * Cette méthode est appelée lorsqu'un utilisateur doit voir ses kanbans
     * mis à jour. L'implémentation actuelle se contente de logger l'action.
     * 
     * @param userId L'identifiant de l'utilisateur (ne doit pas être null)
     * @implNote Cette méthode devrait déclencher une actualisation de l'affichage.
     */
    public void updateAllKanbansForUser(UUID userId) {
        LOGGER.log(Level.INFO, "[MainCore] updateAllKanbansForUser: {0}", userId);
    }

    /**
     * Affiche un kanban dans l'interface utilisateur principale.
     * 
     * Cette méthode délègue l'affichage au gestionnaire d'affichage qui
     * construit la vue et l'affiche dans le contrôleur de la vue principale.
     * 
     * @param kanban Le kanban à afficher (ne doit pas être null)
     * @param homeController Le contrôleur de la vue principale (ne doit pas être null)
     */
    public void displayKanban(Kanban kanban, HomeViewController homeController) { 
        manageDisplay.openKanbanScreen(kanban, homeController); 
    }

    /**
     * Affiche un kanban depuis la vue de profil utilisateur.
     * 
     * Cette méthode délègue l'affichage au gestionnaire d'affichage pour
     * afficher le kanban depuis la vue de profil.
     * 
     * @param kanban Le kanban à afficher (ne doit pas être null)
     * @param profileController Le contrôleur de la vue de profil (ne doit pas être null)
     */
    public void displayKanbanFromProfile(Kanban kanban, ProfileController profileController) {
        manageDisplay.openKanbanScreenFromProfile(kanban, profileController);

    }

    /**
     * Met à jour l'affichage d'un kanban existant.
     * 
     * Cette méthode rafraîchit l'affichage d'un kanban qui a été modifié.
     * Elle délègue au gestionnaire d'affichage qui reconstruit la vue.
     * 
     * @param kanban Le kanban mis à jour (ne doit pas être null)
     */
    public void updateKanban(Kanban kanban) {
        manageDisplay.refreshKanban(kanban);
    }
}
