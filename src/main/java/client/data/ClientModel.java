package client.data;
import java.util.List;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.User;

/**
 * Modèle de données côté client.
 * 
 * Cette classe contient toutes les données de l'application côté client :
 * le kanban actuellement visualisé, la liste des kanbans disponibles,
 * l'utilisateur local et la liste des utilisateurs connectés.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Kanban
 * @see User
 */
public class ClientModel {
    /**
     * Le kanban actuellement ouvert et visualisé par l'utilisateur.
     */
    private Kanban currentKanban;
    
    /**
     * Liste des kanbans disponibles pour l'utilisateur local.
     */
    private List<LightKanban> availableLightKanbans;
    
    /**
     * L'utilisateur local connecté à l'application.
     */
    private User localUser;
    
    /**
     * Liste des utilisateurs actuellement connectés au serveur.
     */
    private List<LightUser> connectedUsers;

    /**
     * Constructeur par défaut du modèle client.
     */
    public ClientModel() {

    }

    /**
     * Récupère le kanban actuellement visualisé.
     * 
     * @return Le kanban actuel, ou null si aucun kanban n'est ouvert
     */
    public Kanban getCurrentKanban() {
        return currentKanban;
    }

    /**
     * Définit le kanban actuellement visualisé.
     * 
     * @param currentKanban Le kanban à définir comme actuel (peut être null)
     */
    public void setCurrentKanban(Kanban currentKanban) {
        this.currentKanban = currentKanban;
    }

    /**
     * Récupère la liste des kanbans disponibles.
     * 
     * @return La liste des kanbans disponibles, ou null si non initialisée
     */
    public List<LightKanban> getAvailableLightKanbans() {
        return availableLightKanbans;
    }

    /**
     * Définit la liste des kanbans disponibles.
     * 
     * @param availableLightKanbans La liste des kanbans disponibles (peut être null)
     */
    public void setAvailableLightKanbans(List<LightKanban> availableLightKanbans) {
        this.availableLightKanbans = availableLightKanbans;
    }

    /**
     * Récupère l'utilisateur local connecté.
     * 
     * @return L'utilisateur local, ou null si aucun utilisateur n'est connecté
     */
    public User getLocalUser() {
        return localUser;
    }

    /**
     * Définit l'utilisateur local connecté.
     * 
     * @param localUser L'utilisateur local à définir (peut être null)
     */
    public void setLocalUser(User localUser) {
        this.localUser = localUser;
    }

    /**
     * Récupère la liste des utilisateurs connectés.
     * 
     * @return La liste des utilisateurs connectés, ou null si non initialisée
     */
    public List<LightUser> getConnectedUsers() {
        return connectedUsers;
    }

    /**
     * Définit la liste des utilisateurs connectés.
     * 
     * @param connectedUsers La liste des utilisateurs connectés (peut être null)
     */
    public void setConnectedUsers(List<LightUser> connectedUsers) {
        this.connectedUsers = connectedUsers;
    }

    /**
     * Sauvegarde un utilisateur (non implémenté).
     * 
     * @param currentUser L'utilisateur à sauvegarder
     * @throws UnsupportedOperationException toujours, car non implémenté
     */
    public void saveUser(LightUser currentUser) {
        throw new UnsupportedOperationException("saveUser not implemented yet");
    }

    /**
     * Ajoute un kanban à la liste des kanbans disponibles s'il n'y est pas déjà.
     * 
     * @param lightKanban Le kanban à ajouter (ne doit pas être null)
     */
    public void addKanban(LightKanban lightKanban) {
        if (!availableLightKanbans.contains(lightKanban)) {
            availableLightKanbans.add(lightKanban);
        }
    }
}