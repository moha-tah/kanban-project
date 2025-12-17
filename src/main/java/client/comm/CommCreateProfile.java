package client.comm;

import java.util.UUID;
import common.dataClasses.User;
import common.dataClasses.LightUser;

/**
 * Classe de création de profil utilisateur (stub serveur).
 * 
 * Cette classe simule la création d'un profil utilisateur côté serveur.
 * Elle génère un identifiant unique et crée un utilisateur léger à partir
 * des paramètres fournis. Cette implémentation est un stub pour les tests
 * et le développement local.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see User
 * @see LightUser
 */
public class CommCreateProfile {
    /**
     * Crée un nouveau profil utilisateur avec un identifiant unique.
     * 
     * Cette méthode simule l'appel au serveur pour créer un profil.
     * Un identifiant UUID aléatoire est généré pour le nouvel utilisateur.
     * 
     * @param params Les paramètres du profil utilisateur à créer (ne doit pas être null)
     * @return Un utilisateur léger avec un identifiant unique généré
     */
    public LightUser createProfile(User params) {
        System.out.println("[Comm] → Server(stub) : createProfile(params)");
        return new LightUser(UUID.randomUUID(), params.getUsername());
    }
}
