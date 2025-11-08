package client.interfaces;

import java.util.List;
import java.util.UUID;

import common.dataClasses.LightUser;
import common.dataClasses.LightKanban;

public interface MainCallsDataClient {

    void saveUser();

    boolean authentify(String username, String password);

    LightUser getMyLightUser();

    List<LightKanban> getMyListLightKanbans();

    void exportProfile(UUID lightUserId, String path);

    void importMyProfile(String path);

    void sendCreateProfile(List<?> profileDetails);
}
package client.interfaces;

import java.util.List;
import java.util.UUID;

import common.dataClasses.LightUser;
import common.dataClasses.LightKanban;

public interface MainCallsDataClient {

    void saveUser();

    boolean authentify(String username, String password);

    LightUser getMyLightUser();

    List<LightKanban> getMyListLightKanbans();

    void exportProfile(UUID lightUserId, String path);

    void importMyProfile(String path);

    /**
     * Crée un nouveau profil utilisateur selon le diagramme de séquence "Créer un profil".
     * Envoie les informations de profil au composant DATA et retourne un LightUser (LIGHTPROFILE).
     * 
     * @param login Le nom d'utilisateur (username)
     * @param password Le mot de passe
     * @param name Le prénom
     * @param surname Le nom de famille
     * @param age L'âge (sera converti en LocalDate pour la création du User)
     * @param avatar Le chemin ou l'URL de l'avatar
     * @param role Le rôle de l'utilisateur
     * @param permissions Les permissions de l'utilisateur
     * @param contacts Les contacts de l'utilisateur
     * @param kanbanList La liste des kanbans (peut être vide)
     * @param status Le statut de l'utilisateur
     * @return LightUser Le profil léger créé (LIGHTPROFILE)
     */
    default LightUser sendCreateProfile(String login, String password, String name, String surname, 
                                       int age, String avatar, String role, String permissions, 
                                       String contacts, String kanbanList, String status) {
        // Note: Cette implémentation par défaut doit être surchargée par les classes concrètes
        // pour gérer la persistance et la communication avec le composant DATA
        
        // Calculer la date de naissance à partir de l'âge (sera utilisé dans l'implémentation concrète)
        // LocalDate birthDate = LocalDate.now().minusYears(age);
        
        // TODO: Implémenter la création du profil dans le composant DATA
        // TODO: Créer un SecureUser avec les informations fournies (login, password, name, surname, birthDate)
        // TODO: Définir l'avatar, le rôle, les permissions, les contacts, le statut
        // TODO: Sauvegarder le SecureUser dans le système de persistance
        // TODO: Retourner le LightUser correspondant (LIGHTPROFILE)
        
        // Pour l'instant, création d'un LightUser basique
        // Les classes concrètes devront implémenter la logique complète
        return new LightUser(login);
    }
}
