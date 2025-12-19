package client.comm.messages;

import java.util.Optional;

import common.dataClasses.LightKanban;
import common.dataClasses.Modification;

/**
 * Message serveur -> client pour notifier une modification d'un kanban
 * aux utilisateurs non participants (viewers). Conformité aux diagrammes:
 * "notifyModification(lightKanban, modification)".
 */
public class NotifyModification extends Message {
    private static final long serialVersionUID = 1L;

    private final LightKanban kanban;
    private final Modification modification;

    public NotifyModification(LightKanban kanban, Modification modification) {
        this.kanban = kanban;
        this.modification = modification;
    }

    @Override
    public Optional<Message> handle() {
        try {
            client.ClientContext ctx = this.getClientContext();
            if (ctx == null) {
                System.err.println("NotifyModification: ClientContext is null!");
                return Optional.empty();
            }

            // Sauvegarde locale côté Data Client
            if (ctx.getData() != null) {
                ctx.getData().saveModifiedKanban(modification);
            }

            // Mise à jour de l'IHM Kanban
            if (ctx.getKanbanComm() != null) {
                ctx.getKanbanComm().deliverNotification(kanban, modification);
            }

        } catch (Exception e) {
            java.util.logging.Logger.getLogger(NotifyModification.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Erreur NotifyModification", e);
        }
        return Optional.empty();
    }
}
