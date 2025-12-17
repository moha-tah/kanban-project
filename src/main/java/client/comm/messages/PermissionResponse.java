package client.comm.messages;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.UUID;

import client.comm.CommCoreClient;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

/**
 * Message envoyé par le propriétaire d'un kanban pour répondre à une demande d'accès.
 * 
 * Ce message est traité côté serveur qui met à jour les permissions si accepté
 * et notifie le demandeur via un message {@link NotifyDecision}.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Message
 * @see NotifyDecision
 */
public class PermissionResponse extends Message {
    private static final long serialVersionUID = 1L;

    /**
     * L'utilisateur qui a demandé l'accès.
     */
    private final LightUser requesterId;
    
    /**
     * Le kanban concerné par la demande.
     */
    private final LightKanban kanbanId;
    
    /**
     * Indique si la permission est accordée (true) ou refusée (false).
     */
    private final boolean accepted;

    /**
     * Constructeur du message de réponse à une demande de permission.
     * 
     * @param requesterId L'utilisateur qui a demandé l'accès (ne doit pas être null)
     * @param kanbanId Le kanban concerné par la demande (ne doit pas être null)
     * @param accepted true si la permission est accordée, false sinon
     */
    public PermissionResponse(LightUser requesterId, LightKanban kanbanId, boolean accepted) {
        this.requesterId = requesterId;
        this.kanbanId = kanbanId;
        this.accepted = accepted;
    }

    @Override
    public Optional<Message> handle() {
        // Côté SERVEUR
        try {
            Class<?> contextClass = Class.forName("server.ServerContext");
            java.lang.reflect.Method getDataMethod = contextClass.getMethod("getData");
            Object dataServerObj = getDataMethod.invoke(null);

            if (dataServerObj != null) {
                server.interfaces.CommCallsDataServer dataServer = (server.interfaces.CommCallsDataServer) dataServerObj;

                // 1. Si accepté, on met à jour les données serveur
                if (accepted) {
                    dataServer.addAuthorizedUser(kanbanId, requesterId);
                    System.out.println("SERVER: Accès accordé pour " + requesterId + " sur " + kanbanId);
                } else {
                    System.out.println("SERVER: Accès refusé.");
                }

                // 2. On récupère le LightKanban (potentiellement mis à jour)
                // Pour simplifier, on renvoie une coquille, le client fera la mise à jour locale
                LightKanban k = new LightKanban(kanbanId.getId(), "Updated");

                // 3. Notifier le demandeur (Requester)
                Class<?> commClass = Class.forName("server.comm.CommCoreServer");
                java.lang.reflect.Method sendMethod = commClass.getMethod("sendToUser", UUID.class, Object.class);

                // On envoie NotifyDecision au demandeur
                NotifyDecision msg = new NotifyDecision(null, k, accepted); // user null car c'est pour soi-même
                sendMethod.invoke(null, requesterId.getId(), msg);
            }
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException t) {
            java.util.logging.Logger.getLogger(CommCoreClient.class.getName())
                    .log(java.util.logging.Level.SEVERE, "MsgReceiver: Exception in handler.", t);
        }
        return Optional.empty();
    }
}