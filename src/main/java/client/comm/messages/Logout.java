package client.comm.messages;

import java.util.Optional;

import common.dataClasses.LightUser;
import server.comm.CommCoreServer;
import server.interfaces.CommCallsDataServer;

/**
 * Message envoyé par un client pour se déconnecter du serveur.
 * 
 * Ce message est traité côté serveur et déclenche :
 * - La suppression de l'utilisateur de la liste des connectés
 * - La suppression des kanbans créés par cet utilisateur
 * - Un broadcast pour notifier tous les autres clients
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Message
 */
public class Logout extends Message {
    private static final long serialVersionUID = 1L;

    /**
     * L'utilisateur qui se déconnecte.
     */
    private final LightUser user;

    /**
     * Constructeur du message de déconnexion.
     * 
     * @param user L'utilisateur qui se déconnecte (ne doit pas être null)
     */
    public Logout(LightUser user) {
        this.user = user;
    }

    @Override
    public Optional<Message> handle() {
        try {
            System.out.println("[SERVER] Reçu demande de logout pour : " + (user != null ? user.getUsername() : "Inconnu"));

            // 1. Appel à la couche Data Serveur
            CommCallsDataServer dataServer = server.ServerContext.getData();
            if (dataServer != null) {
                dataServer.notifyLogout(user);
            }

            // 2. Diffusion de la mise à jour aux autres clients
            CommCoreServer.triggerBroadcast();

        } catch (Exception e) {
            java.util.logging.Logger.getLogger(Logout.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Erreur lors du traitement du logout", e);
        }
        return Optional.empty();
    }
}