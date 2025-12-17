package client.comm.messages;

import java.util.Optional;

import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

/**
 * Message envoyé par le serveur au demandeur pour l'informer de la décision
 * concernant sa demande d'accès à un kanban.
 * 
 * Ce message est traité côté client du demandeur qui affiche la décision
 * et met à jour l'interface utilisateur.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Message
 * @see PermissionResponse
 */
public class NotifyDecision extends Message {
    private static final long serialVersionUID = 1L;

    /**
     * L'utilisateur qui a fait la demande (peut être null si c'est pour soi-même).
     */
    private final LightUser requestedId;
    
    /**
     * Le kanban concerné par la décision.
     */
    private final LightKanban kanbanId;
    
    /**
     * Indique si la demande a été acceptée (true) ou refusée (false).
     */
    private final boolean accepted;

    /**
     * Constructeur du message de notification de décision.
     * 
     * @param requestedId L'utilisateur qui a fait la demande (peut être null)
     * @param kanbanId Le kanban concerné par la décision (ne doit pas être null)
     * @param accepted true si la demande est acceptée, false sinon
     */
    public NotifyDecision(LightUser requestedId, LightKanban kanbanId, boolean accepted) {
        this.requestedId = requestedId;
        this.kanbanId = kanbanId;
        this.accepted = accepted;
    }

    @Override
    public Optional<Message> handle() {
        // Côté CLIENT (Demandeur)
        if (client.MainApp.getCore() != null) {
            client.MainApp.getCore().getCOMMService().displayDecision(requestedId, kanbanId, accepted);

            javafx.application.Platform.runLater(() -> {

                client.ihmMain.controllers.HomeViewController.getInstance().refreshKanbansFromModel();
            });
        }
        return Optional.empty();
    }
}
