package client.comm.messages;

import java.util.Optional;

import common.dataClasses.LightKanban;
import common.dataClasses.Modification;

/**
 * Message envoyé par le serveur pour notifier les clients d'une modification
 * effectuée sur un kanban.
 * 
 * Ce message est traité côté client qui met à jour son modèle local
 * et notifie l'interface utilisateur du kanban.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Message
 * @see Modification
 */
public class NotifyEdition extends Message {
    private static final long serialVersionUID = 1L;

    /**
     * Le kanban concerné par la modification.
     */
    private final LightKanban kanban;
    
    /**
     * La modification effectuée sur le kanban.
     */
    private final Modification modification;

    /**
     * Constructeur du message de notification d'édition.
     * 
     * @param kanban Le kanban concerné par la modification (ne doit pas être null)
     * @param modification La modification effectuée (ne doit pas être null)
     */
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