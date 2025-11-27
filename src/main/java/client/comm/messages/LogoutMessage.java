package client.comm.messages;

import java.util.Optional;

import client.comm.MsgReceiver;
import common.dataClasses.LightUser;

public class LogoutMessage extends Message {
    private static final long serialVersionUID = 1L;

    private final LightUser user;

    public LogoutMessage(LightUser user) {
        this.user = user;
    }

    @Override
    public Optional<Message> handle() {
        try {
            System.out.println("[SERVER] Reçu demande de logout pour : " + (user != null ? user.getUsername() : "Inconnu"));

            // 1. Appel à la couche Data Serveur via Réflexion (pour éviter dépendance directe)
            Class<?> contextClass = Class.forName("server.ServerContext");
            java.lang.reflect.Method getDataMethod = contextClass.getMethod("getData");
            Object dataServerObj = getDataMethod.invoke(null);

            if (dataServerObj != null) {
                server.interfaces.CommCallsDataServer dataServer = (server.interfaces.CommCallsDataServer) dataServerObj;

                dataServer.notifyLogout(user);
            }

            // 2. Diffusion de la mise à jour aux autres (sendModified du diagramme)
            Class<?> commClass = Class.forName("server.comm.CommCoreServer");
            java.lang.reflect.Method triggerMethod = commClass.getMethod("triggerBroadcast");
            triggerMethod.invoke(null);

        } catch (Exception e) {
            java.util.logging.Logger.getLogger(LogoutMessage.class.getName())
                    .log(java.util.logging.Level.INFO, "MsgReceiver: I/O error or stream closed.", e);
        }
        return Optional.empty();
    }
}