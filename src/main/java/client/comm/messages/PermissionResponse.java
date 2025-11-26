package client.comm.messages;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

import client.comm.CommCoreClient;
import common.dataClasses.LightKanban;
import common.dataClasses.Kanban;

public class PermissionResponse extends Message {
    private static final long serialVersionUID = 1L;

    private final UUID requesterId;
    private final UUID kanbanId;
    private final boolean accepted;

    public PermissionResponse(UUID requesterId, UUID kanbanId, boolean accepted) {
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
                LightKanban k = new LightKanban(kanbanId, "Updated");

                // 3. Notifier le demandeur (Requester)
                Class<?> commClass = Class.forName("server.comm.CommCoreServer");
                java.lang.reflect.Method sendMethod = commClass.getMethod("sendToUser", UUID.class, Object.class);

                // On envoie NotifyDecision au demandeur
                NotifyDecision msg = new NotifyDecision(null, k, accepted); // user null car c'est pour soi-même
                sendMethod.invoke(null, requesterId, msg);
            }
        } catch (Throwable t) {
            java.util.logging.Logger.getLogger(CommCoreClient.class.getName())
                    .log(java.util.logging.Level.SEVERE, "MsgReceiver: Exception in handler.", t);
        }
        return Optional.empty();
    }
}