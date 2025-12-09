package client.comm.messages;

import java.util.Optional;

import common.dataClasses.LightUser;
import server.comm.CommCoreServer;
import server.interfaces.CommCallsDataServer;

public class Logout extends Message {
    private static final long serialVersionUID = 1L;

    private final LightUser user;

    public Logout(LightUser user) {
        this.user = user;
    }

    @Override
    public Optional<Message> handle() {
        try {
            System.out.println("[SERVER] Reçu demande de logout pour : " + (user != null ? user.getUsername() : "Inconnu"));

            // 1. Appel à la couche Data Serveur
            CommCallsDataServer dataServer = this.getServerContext().getData();
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