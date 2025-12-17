package client.data;

import common.dataClasses.LightUser;
import common.dataClasses.User;
import client.comm.CommCreateProfile;

/**
 * Classe de création de profil utilisateur côté données.
 * 
 * Cette classe fait le lien entre la couche données et la couche communication
 * pour créer un nouveau profil utilisateur. Elle délègue la création effective
 * à la couche communication qui communique avec le serveur.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see CommCreateProfile
 * @see User
 * @see LightUser
 */
public class DataCreateProfile {

    /**
     * Interface de communication pour créer le profil.
     */
    private final CommCreateProfile comm;

    /**
     * Constructeur de la classe de création de profil.
     * 
     * @param comm L'interface de communication à utiliser (ne doit pas être null)
     */
    public DataCreateProfile(CommCreateProfile comm) {
        this.comm = comm;
    }

    /**
     * Envoie une demande de création de profil au serveur.
     * 
     * Cette méthode délègue la création du profil à la couche communication.
     * Aucun stockage local n'est effectué ici.
     * 
     * @param params Les paramètres du profil utilisateur à créer (ne doit pas être null)
     * @return Un utilisateur léger avec un identifiant unique généré par le serveur
     */
    public LightUser sendCreateProfile(User params) {
        System.out.println("[Data] → Comm : createProfile(params)");
        // no storage
        return comm.createProfile(params);
    }
}
