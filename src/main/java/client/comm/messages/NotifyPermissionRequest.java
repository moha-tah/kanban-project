package client.comm.messages;

import java.util.Optional;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

/**
 * Message envoyé par le serveur au propriétaire d'un kanban pour notifier
 * une demande d'accès.
 * 
 * Ce message est traité côté client du propriétaire qui peut alors
 * accepter ou refuser la demande via un message {@link PermissionResponse}.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Message
 * @see PermissionResponse
 */
public class NotifyPermissionRequest extends Message {
    private static final long serialVersionUID = 1L;

    /**
     * L'utilisateur qui demande l'accès au kanban.
     */
    private final LightUser requester;
    
    /**
     * Le kanban pour lequel l'accès est demandé.
     */
    private final LightKanban kanban;

    /**
     * Constructeur du message de notification de demande de permission.
     * 
     * @param requester L'utilisateur qui demande l'accès (ne doit pas être null)
     * @param kanban Le kanban pour lequel l'accès est demandé (ne doit pas être null)
     */
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