package client.comm.messages;

import java.util.Optional;

import common.dataClasses.LightKanban;
import common.dataClasses.Modification;


public class NotifyEdition extends Message {
    private static final long serialVersionUID = 1L;

    private final LightKanban kanban;
    private final Modification modification;

    public NotifyEdition(LightKanban kanban, Modification modification) {
        this.kanban = kanban;
        this.modification = modification;
    }

    @Override
    public Optional<Message> handle() {
        try {
            System.out.println("[CLIENT] Reçu notification d'édition pour kanban " + 
                (kanban != null ? kanban.getTitle() : "Inconnu"));

            // Utiliser le ClientContext attaché au message
            client.ClientContext ctx = this.getClientContext();
            if (ctx == null) {
                System.err.println("NotifyEdition: ClientContext is null!");
                return Optional.empty();
            }

            // 1. Sauvegarder la modification côté Data Client
            if (ctx.getData() != null) {
                ctx.getData().saveModifiedKanban(modification, kanban);
            }

            // 2. Notifier l'IHM Kanban de la modification
            if (ctx.getKanbanComm() != null) {
                ctx.getKanbanComm().deliverNotification(kanban, modification);
            }

        } catch (Exception e) {
            java.util.logging.Logger.getLogger(NotifyEdition.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Erreur lors du traitement de la notification d'édition", e);
        }
        return Optional.empty();
    }
}