package client.comm.messages;

import java.util.Optional;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

public class NotifyPermissionRequest extends Message {
    private static final long serialVersionUID = 1L;

    private final LightUser requester;
    private final LightKanban kanban;

    public NotifyPermissionRequest(LightUser requester, LightKanban kanban) {
        this.requester = requester;
        this.kanban = kanban;
    }

    @Override
    public Optional<Message> handle() {
        // Cette méthode s'exécute sur le CLIENT du CRÉATEUR
        System.out.println("[CLIENT] Notification reçue : " + requester.getUsername() + " veut accéder à " + kanban.getTitle());

        try {
            // On accède au MainCore via MainApp pour déclencher l'affichage
            if (client.MainApp.getCore() != null) {
                client.MainApp.getCore().getCOMMService().displayPermissionRequest(requester, kanban);
            }
        } catch (Throwable t) {
            java.util.logging.Logger.getLogger(NotifyPermissionRequest.class.getName())
                    .log(java.util.logging.Level.SEVERE, "MsgReceiver: Exception in handler.", t);
        }
        return Optional.empty();
    }
}