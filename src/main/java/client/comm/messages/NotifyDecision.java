package client.comm.messages;

import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

import java.util.Optional;
import java.util.UUID;

/**
 * Notification envoyée au demandeur pour l'informer de la décision.
 * Dans cette version, c'est un simple log côté client ; pas de routage multi-clients.
 */
public class NotifyDecision extends Message {
    private static final long serialVersionUID = 1L;

    private final LightUser requesterId;
    private final LightKanban kanbanId;
    private final boolean accepted;

    public NotifyDecision(LightUser requesterId, LightKanban kanbanId, boolean accepted) {
        this.requesterId = requesterId;
        this.kanbanId = kanbanId;
        this.accepted = accepted;
    }

    @Override
    public Optional<Message> handle() {
        // Côté CLIENT (Demandeur)
        if (client.MainApp.getCore() != null) {
            client.MainApp.getCore().getCOMMService().displayDecision(null, kanbanId, accepted);

            javafx.application.Platform.runLater(() -> {

                client.ihmMain.controllers.HomeViewController.getInstance().refreshKanbansFromModel();
            });
        }
        return Optional.empty();
    }
}
