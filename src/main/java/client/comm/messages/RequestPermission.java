package client.comm.messages;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

public class RequestPermission extends Message {
    private static final long serialVersionUID = 1L;

    private final UUID requesterId;
    private final UUID kanbanId;

    public RequestPermission(UUID requesterId, UUID kanbanId) {
        this.requesterId = requesterId;
        this.kanbanId = kanbanId;
    }

    @Override
    public Optional<Message> handle() {
        // S'exécute sur le SERVEUR
        try {
            // 1. Accès au Data Server via Réflexion
            Class<?> contextClass = Class.forName("server.ServerContext");
            java.lang.reflect.Method getDataMethod = contextClass.getMethod("getData");
            Object dataServerObj = getDataMethod.invoke(null);

            if (dataServerObj != null) {
                server.interfaces.CommCallsDataServer dataServer = (server.interfaces.CommCallsDataServer) dataServerObj;

                // 2. Retrouver l'objet LightUser du demandeur
                LightUser requesterUser = null;
                for (LightUser u : dataServer.getUsersList()) {
                    if (u.getId().equals(requesterId)) {
                        requesterUser = u;
                        break;
                    }
                }

                LightKanban targetKanban = null;
                UUID ownerId = null;

                // On scanne la liste broadcastée
                List<LightKanban> allKanbans = dataServer.getKanbansList();
                for (LightKanban k : allKanbans) {
                    if (k.getId().equals(kanbanId)) {
                        targetKanban = k;
                        break;
                    }
                }

                Class<?> implClass = dataServer.getClass();
                java.lang.reflect.Method getProviderMethod = implClass.getMethod("getDataServProvider");
                Object provider = getProviderMethod.invoke(dataServer);

                Class<?> providerClass = provider.getClass();
                java.lang.reflect.Method getModelMethod = providerClass.getMethod("getModel");
                Object model = getModelMethod.invoke(provider);

                Class<?> modelClass = model.getClass();
                java.lang.reflect.Method getKanbansMethod = modelClass.getMethod("getInUseKanbans");
                @SuppressWarnings("unchecked")
                List<Kanban> serverKanbans = (List<Kanban>) getKanbansMethod.invoke(model);

                for (Kanban k : serverKanbans) {
                    if (k.getId().equals(kanbanId)) {
                        ownerId = k.getCreatorId();
                        if (targetKanban == null) targetKanban = k.getLightKanban();
                        break;
                    }
                }

                if (requesterUser != null && targetKanban != null && ownerId != null) {
                    Class<?> commClass = Class.forName("server.comm.CommCoreServer");
                    java.lang.reflect.Method sendMethod = commClass.getMethod("sendToUser", UUID.class, Object.class);

                    NotifyPermissionRequest msg = new NotifyPermissionRequest(requesterUser, targetKanban);
                    sendMethod.invoke(null, ownerId, msg);

                    System.out.println("[SERVER] Notification envoyée au propriétaire " + ownerId);
                } else {
                    System.err.println("[SERVER] Impossible de trouver le propriétaire ou le kanban.");
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException t) {
            java.util.logging.Logger.getLogger(NotifyPermissionRequest.class.getName())
                    .log(java.util.logging.Level.SEVERE, "MsgReceiver: Exception in handler.", t);
        }
        return Optional.empty();
    }
}